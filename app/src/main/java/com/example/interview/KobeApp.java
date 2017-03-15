package com.example.interview;

import android.app.Application;

import com.example.interview.task.TaskAsyncGenresSync;

import io.realm.Realm;


public class KobeApp extends Application {
    private static final String TAG = "TaskAsyncUpcomingMovies";
    private static KobeApp instance;

    public static KobeApp getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);

        // Sync genres from TheMovieDB into local database
        TaskAsyncGenresSync asyncGenresSync = new TaskAsyncGenresSync(this);
        asyncGenresSync.execute();
    }
}
