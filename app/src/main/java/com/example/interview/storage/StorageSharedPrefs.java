package com.example.interview.storage;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Singleton used to persist and restore data from Android Shared Preferences
 * */
public class StorageSharedPrefs {

	public static final String SHARED_PREFS = "SHARED_PREFS";
	public static final String SHARED_PREFS_KEY_MOVIES_CURR_PAGE = "PREFS_KEY_UPCOMING_MOVIES_CURR_PAGE";
    public static final String SHARED_PREFS_KEY_UPCOMING_MOVIES_TOTAL_PAGES = "PREFS_KEY_UPCOMING_MOVIES_TOTAL_PAGES";
    public static final String SHARED_PREFS_KEY_MOVIES_SEARCH_MODE = "PREFS_KEY_MOVIES_SEARCH_MODE";
    public static final String SHARED_PREFS_KEY_MOVIES_SEARCH_QUERY = "PREFS_KEY_MOVIES_SEARCH_QUERY";

	static private StorageSharedPrefs instance;
	private SharedPreferences sharedPrefs;
	private SharedPreferences.Editor editor;

	private StorageSharedPrefs(Context ctx) {
		sharedPrefs = ctx.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
		editor = sharedPrefs.edit();
	}

	public static StorageSharedPrefs getInstance(Context ctx) {
		if (instance == null) {
			instance = new StorageSharedPrefs(ctx);
		}
		return instance;
	}

    /**
     * Preferences needed in order to restore the correct movies list page
     * that user was navigating when he left this view
     * */
	public int getMoviesCurrentPage() {
		return sharedPrefs.getInt(SHARED_PREFS_KEY_MOVIES_CURR_PAGE, 1);
	}

	public void setMoviesCurrentPage(int val) {
		editor.putInt(SHARED_PREFS_KEY_MOVIES_CURR_PAGE, val);
		editor.commit();
	}

    /**
     * Preferences used to disable next page button when user is navigating in last page
     * */
    public int getUpcomingMoviesTotalPages() {
        return sharedPrefs.getInt(SHARED_PREFS_KEY_UPCOMING_MOVIES_TOTAL_PAGES, 1);
    }

    public void setMoviesTotalPages(int val) {
        editor.putInt(SHARED_PREFS_KEY_UPCOMING_MOVIES_TOTAL_PAGES, val);
        editor.commit();
    }

    /**
     * Preferences used to define if search mode is active or not
     * */
    public Boolean isMoviesSearchModeActive() {
        return sharedPrefs.getBoolean(SHARED_PREFS_KEY_MOVIES_SEARCH_MODE, false);
    }

    public void setMoviesSearchMode(boolean val) {
        editor.putBoolean(SHARED_PREFS_KEY_MOVIES_SEARCH_MODE, val);
        editor.commit();
    }

    /**
     * Preferences used to restore the last search query when the movies activity is restored
     * */
    public String getMoviesSearchQuery() {
        return sharedPrefs.getString(SHARED_PREFS_KEY_MOVIES_SEARCH_QUERY,"");
    }

    public void setMoviesSearchQuery(String val) {
        editor.putString(SHARED_PREFS_KEY_MOVIES_SEARCH_QUERY, val);
        editor.commit();
    }
}
