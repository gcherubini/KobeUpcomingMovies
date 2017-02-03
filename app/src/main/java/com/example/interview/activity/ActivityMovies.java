package com.example.interview.activity;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.interview.R;
import com.example.interview.adapter.AdapterMovieItem;
import com.example.interview.model.ModelMovie;
import com.example.interview.storage.StorageSharedPrefs;
import com.example.interview.task.TaskAsyncSearchMovies;
import com.example.interview.task.TaskAsyncSearchMovies.TaskAsyncSearchMoviesResponse;
import com.example.interview.task.TaskAsyncUpcomingMovies;
import com.example.interview.task.TaskListenableAsync;
import com.example.interview.utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

import static com.example.interview.R.layout.movies;


public class ActivityMovies extends AppCompatActivity implements SearchView.OnQueryTextListener, SearchView.OnCloseListener {
    private static final String TAG = "ActivityMovies";

    private Realm realm;
    private RealmResults<ModelMovie> moviesDBResults;
    private StorageSharedPrefs prefs;

    // UI Elements
    private RecyclerView recyclerView;
    private Button previousBtn;
    private Button nextBtn;
    private AdapterMovieItem adapter;
    private SearchView searchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "ON CREATE");
        super.onCreate(savedInstanceState);
        setContentView(movies);

        previousBtn = (Button) findViewById(R.id.movies_previous_btn);
        nextBtn = (Button) findViewById(R.id.movies_next_btn);
        recyclerView = (RecyclerView) findViewById(R.id.movies_recycler_view);
        adapter = new AdapterMovieItem(new ArrayList<ModelMovie>(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        setTitle(R.string.activity_movie_name);

        Realm.init(getApplicationContext());
        realm = Realm.getDefaultInstance();
        prefs = StorageSharedPrefs.getInstance(this);

        setUpcomingMoviesPrefsKeys();
        loadMoviesFromNetwork(prefs.getMoviesCurrentPage());
        loadDataFromDatabase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.movies_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.movies_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    /**
     * Load movies from database and
     * Prepare Realm change listener to update UI when local database is updated
     * */
    private void loadDataFromDatabase() {
        moviesDBResults = realm.where(ModelMovie.class).equalTo("page", prefs.getMoviesCurrentPage()).findAll();
        adapter.setDataSet(moviesDBResults);
        adapter.notifyDataSetChanged();
        updatePaginationElements();
    }

    /** Pagination buttons */

    public void nextPageBtnClick(View view) {
        loadMoviesFromNetwork(prefs.getMoviesCurrentPage() + 1);
    }

    public void previousPageBtnClick(View view) {
        loadMoviesFromNetwork(prefs.getMoviesCurrentPage()- 1);
    }

    /**
     * Load movies from network according filtering type,
     * If user is in searching mode, populate data with searchMoviesTask
     * Otherwise, sync database and load data with syncUpcomingMoviesTask
     * */
    private void loadMoviesFromNetwork(final int pageToLoad) {
        if(!prefs.isMoviesSearchModeActive()) {
            TaskAsyncUpcomingMovies taskAsyncUpcomingMovies = new TaskAsyncUpcomingMovies(this, pageToLoad);
            taskAsyncUpcomingMovies.setOnPostExecuteListener(new TaskListenableAsync.AsyncTaskListener<String>() {
                @Override
                public void onPostExecute(String failure) {
                    if(failure == null) {
                        prefs.setMoviesCurrentPage(pageToLoad);
                        loadDataFromDatabase();
                    }
                    else processFailure(failure);
                }
            });
            taskAsyncUpcomingMovies.execute();
        }
        else {
            TaskAsyncSearchMovies taskAsyncSearchMovies = new TaskAsyncSearchMovies(this, pageToLoad, prefs.getMoviesSearchQuery());
            taskAsyncSearchMovies.setOnPostExecuteListener(new TaskListenableAsync.AsyncTaskListener<TaskAsyncSearchMoviesResponse>() {
                @Override
                public void onPostExecute(TaskAsyncSearchMoviesResponse response) {
                    if(response.getFailure() == null){
                        adapter.setDataSet(response.getMovies());
                        adapter.notifyDataSetChanged();
                        prefs.setMoviesCurrentPage(pageToLoad);
                        updatePaginationElements();
                    }
                    else processFailure(response.getFailure());
                }
            });
            taskAsyncSearchMovies.execute();
        }
    }

    /**
     * Process failures from AsyncTasks and show user feedback
     * */
    private void processFailure(String errorMessage) {
		Log.e(TAG, "Failed to sync database with movies from Network. err:" + errorMessage);
		if(NetworkUtils.hasInternetConnection(this)){
			Toast.makeText(this, R.string.movies_sync_error_generic, Toast.LENGTH_SHORT).show();
		}
		else{
			Toast.makeText(this, R.string.movies_sync_error_no_internet_connection, Toast.LENGTH_SHORT).show();
		}
	}

    /**
     * Update UI of pagination elements according current page counters
     * */
    private void updatePaginationElements() {
        if(prefs.getMoviesCurrentPage() == 1)
            previousBtn.setEnabled(false);
        else
            previousBtn.setEnabled(true);

        if(prefs.getUpcomingMoviesTotalPages() == prefs.getMoviesCurrentPage())
            nextBtn.setEnabled(false);
        else
            nextBtn.setEnabled(true);
    }

    /** Action Search Callback Methods */
    @Override
    public boolean onQueryTextChange(String query) { return false; }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.i(TAG, "On search submit: " + query);
        setSearchMoviesPrefsKeys(query);
        loadMoviesFromNetwork(prefs.getMoviesCurrentPage());
        return false;
    }

    @Override
    public boolean onClose() {
        setUpcomingMoviesPrefsKeys();
        loadMoviesFromNetwork(prefs.getMoviesCurrentPage());
        return false;
    }

    /**
     * Set upcoming movies vision default preferences
     * */
    private void setUpcomingMoviesPrefsKeys() {
        prefs.setMoviesCurrentPage(1);
        prefs.setMoviesSearchQuery("");
        prefs.setMoviesSearchMode(false);
    }

    /**
     * Set search movies vision default preferences
     * */
    private void setSearchMoviesPrefsKeys(String query) {
        prefs.setMoviesCurrentPage(1);
        prefs.setMoviesSearchQuery(query);
        prefs.setMoviesSearchMode(true);
    }
}
