package com.example.interview.task;

import android.content.Context;
import android.util.Log;

import com.example.interview.R;
import com.example.interview.model.ModelMovie;
import com.example.interview.network.NetworkApiTheMovieDb;
import com.example.interview.network.NetworkApiTheMovieDb.UpcomingMoviesResult;
import com.example.interview.storage.StorageSharedPrefs;

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
				NetworkApiTheMovieDb.UpcomingMoviesResult body = response.body();
				List<ModelMovie> movies = body.getResults();
				if (movies == null || movies.size() == 0) throw new Exception("No movies found, empty result.");

				failure = databasePersistence(failure, body, movies);
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

    /**
     * Persisting movies into local database
	 * If the movie position already exists, another movie will be replaced in this position
     */
	private String databasePersistence(String failure, UpcomingMoviesResult body, List<ModelMovie> movies) {
		try{
			Realm realm = Realm.getDefaultInstance();
			realm.beginTransaction();

			int position = ModelMovie.getFirstPositionForSpecificPage(pageToLoad);
			for(ModelMovie m : movies){
				m.setPosition(position);
                ModelMovie.updateMovieModelWithGenresString(ctx, m, TAG, realm);
                position++;
			}
			realm.copyToRealmOrUpdate(movies);
			realm.commitTransaction();

			StorageSharedPrefs.getInstance(ctx).setMoviesTotalPages(body.getTotalPages());
			Log.i(TAG, "Database synced successfully, movies table updated");
		}
		catch (Exception ex){
			failure = ctx.getString(R.string.movies_sync_error_database) + " Exc: " + ex.getMessage();
		}
		return failure;
	}
}
