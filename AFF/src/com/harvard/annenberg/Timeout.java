package com.harvard.annenberg;

import java.util.TimerTask;

import android.os.AsyncTask;
import android.util.Log;

public class Timeout extends TimerTask {
	private AsyncTask _task;

	public Timeout(AsyncTask task) {
		_task = task;
	}

	@Override
	public void run() {
		Log.w("Timeout", "Timed out while downloading.");
		_task.cancel(false);
	}
};