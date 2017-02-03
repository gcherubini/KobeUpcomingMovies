package com.example.interview.task;

import android.content.Context;
import android.util.Log;

import com.example.interview.model.ModelMovie;
import com.example.interview.network.NetworkApiTheMovieDb;
import com.example.interview.network.NetworkApiTheMovieDb.UpcomingMoviesResult;
import com.example.interview.storage.StorageSharedPrefs;
import com.example.interview.task.TaskAsyncSearchMovies.TaskAsyncSearchMoviesResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Async network operation responsible to search for movies with title,
 * This class is not responsible to update the local Database
 * This class is also presenting some feedback through Android Toasts
 */
public class TaskAsyncSearchMovies extends TaskListenableAsync<Void, Void, TaskAsyncSearchMoviesResponse> {
	private static final String TAG = "TaskAsyncSearchMovies";
	private final int pageToLoad;
	private final String query;
	private Context ctx;

	public TaskAsyncSearchMovies(Context ctx, int pageToLoad, String query) {
		this.ctx = ctx;
		this.pageToLoad = pageToLoad;
		this.query = query;
	}

	@Override
	protected TaskAsyncSearchMoviesResponse doInBackground(Void... params) {
		Log.i(TAG, "Starting task, params = page: " + pageToLoad + " query: " + query);

		TaskAsyncSearchMoviesResponse asyncTaskResponse = new TaskAsyncSearchMoviesResponse();
		String failure = null;
		List<ModelMovie> movies = new ArrayList<ModelMovie>();

		Call<UpcomingMoviesResult> call = NetworkApiTheMovieDb.getClient()
				.searchMovies(NetworkApiTheMovieDb.API_KEY, query, pageToLoad);
		try {
			Response<UpcomingMoviesResult> response = call.execute();
			if(response.isSuccessful() && response.body() != null) {
				NetworkApiTheMovieDb.UpcomingMoviesResult body = response.body();
				movies  = body.getResults();
				StorageSharedPrefs.getInstance(ctx).setMoviesTotalPages(Integer.parseInt(body.getTotalPages()));
				Log.i(TAG, "Success to search movies from Network");
			}
			else{
				failure = response.errorBody().toString();
			}
		}
		catch(Exception ex){
			failure = ex.getMessage();
		}

		asyncTaskResponse.setFailure(failure);
		asyncTaskResponse.setMovies(movies);

		return asyncTaskResponse;
	}

	/**
	 * AsyncTask response provided for caller (activity for instance)
	 */
	public class TaskAsyncSearchMoviesResponse {
		List<ModelMovie> movies;
		String failure;

		public List<ModelMovie> getMovies() {
			return movies;
		}

		public void setMovies(List<ModelMovie> movies) {
			this.movies = movies;
		}

		public String getFailure() {
			return failure;
		}

		public void setFailure(String failure) {
			this.failure = failure;
		}
	}
}
