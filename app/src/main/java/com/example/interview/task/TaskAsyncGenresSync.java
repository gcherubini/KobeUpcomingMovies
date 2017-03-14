package com.example.interview.task;

import android.content.Context;
import android.util.Log;

import com.example.interview.R;
import com.example.interview.model.ModelGenre;
import com.example.interview.network.NetworkApiTheMovieDb;
import com.example.interview.network.NetworkApiTheMovieDb.GenresListResult;

import java.util.List;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Async network operation responsible to synchronize database with genres list from The Movie http API
 */
public class TaskAsyncGenresSync extends TaskListenableAsync<Void, Void, String> {
	private static final String TAG = "TaskAsyncGenresSync";
	private Context ctx;

	public TaskAsyncGenresSync(Context ctx) {
		this.ctx = ctx;
		Realm.init(ctx);
	}

	@Override
	protected String doInBackground(Void... params) {
		Log.i(TAG, "Starting task");
		String failure = null;

		Call<GenresListResult> call = NetworkApiTheMovieDb.getClient()
				.getGenresList(NetworkApiTheMovieDb.API_KEY);

		try {
			Response<GenresListResult> response = call.execute();
			if(response.isSuccessful() && response.body() != null) {
				Log.i(TAG, "Success to get genres from Network");
				GenresListResult body =  response.body();
				List<ModelGenre> genres = body.getGenres();

				failure = databasePersistence(failure, genres);
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

	private String databasePersistence(String failure, List<ModelGenre> genres) {
		try{
			Realm realm = Realm.getDefaultInstance();
			realm.beginTransaction();
			realm.copyToRealmOrUpdate(genres);
			realm.commitTransaction();

			Log.i(TAG, "Database synced successfully, genres list updated");
		}
		catch (Exception ex){
			failure = ctx.getString(R.string.genres_sync_error_database);
		}
		return failure;
	}
}
