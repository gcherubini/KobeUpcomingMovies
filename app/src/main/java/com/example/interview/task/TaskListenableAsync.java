package com.example.interview.task;

import android.os.AsyncTask;

/**
 * Customized async task that allows the onPostExecute method could be exported to UI activity
 */
public abstract class TaskListenableAsync<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    private AsyncTaskListener<Result> mListener;

    @Override
    protected final void onPostExecute(Result result) {
        notifyListenerOnPostExecute(result);
    }

    public void setOnPostExecuteListener(AsyncTaskListener<Result> l) {
        mListener = l;
    }

    private void notifyListenerOnPostExecute(Result result) {
        if (mListener != null)
            mListener.onPostExecute(result);
    }

    public interface AsyncTaskListener<Result> {
        public void onPostExecute(Result result);
    }

}

