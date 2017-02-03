package com.example.interview.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.interview.R;
import com.example.interview.model.ModelMovie;
import com.example.interview.network.NetworkApiTheMovieDb;
import com.example.interview.network.NetworkApiTheMovieDb.UpcomingMoviesResult;
import com.example.interview.storage.StorageSharedPrefs;
import com.example.interview.utils.NetworkUtils;

import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Async network operation responsible to synchronize database with upcoming movies from The Movie http API
 */
public class TaskAsyncUpcomingMovies extends TaskListenableAsync<Void, Void, String> {
	private static final String TAG = "TaskAsyncUpcomingMovies";
	private final int pageToLoad;
	private Context ctx;

	public TaskAsyncUpcomingMovies(Context ctx, int pageToLoad) {
		this.ctx = ctx;
		this.pageToLoad = pageToLoad;
		Realm.init(ctx);
	}

	@Override
	protected String doInBackground(Void... params) {
		Log.i(TAG, "Starting task, params = page: " + pageToLoad);
		String failure = null;

		Call<UpcomingMoviesResult> call = NetworkApiTheMovieDb.getClient()
				.getUpcomingMovies(NetworkApiTheMovieDb.API_KEY, pageToLoad);

		try {
			Response<UpcomingMoviesResult> response = call.execute();
			if(response.isSuccessful() && response.body() != null) {
				Log.i(TAG, "Success to get movies from Network");
				NetworkApiTheMovieDb.UpcomingMoviesResult body =  response.body();
				List<ModelMovie> movies = body.getResults();

				failure = databasePersistence(pageToLoad, failure, body, movies);
			}
			else{
				failure =  response.errorBody().toString();
			}
		}
		catch(Exception ex){
			failure = ex.getMessage();
		}

		return failure;
	}

	private String databasePersistence(int page, String failure, UpcomingMoviesResult body, List<ModelMovie> movies) {
		try{
			Realm realm = Realm.getDefaultInstance();
			realm.beginTransaction();
			for(ModelMovie m : movies){
				m.setPage(page);
			}
			realm.copyToRealmOrUpdate(movies);
			realm.commitTransaction();

			Log.i(TAG, "Database synced successfully, movies table updated");
			StorageSharedPrefs.getInstance(ctx).setMoviesTotalPages(Integer.parseInt(body.getTotalPages()));
		}
		catch (Exception ex){
			failure = ctx.getString(R.string.movies_sync_error_database);
		}
		return failure;
	}
}
