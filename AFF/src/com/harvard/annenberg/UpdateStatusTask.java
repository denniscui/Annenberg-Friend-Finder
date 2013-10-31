package com.harvard.annenberg;

import java.util.Hashtable;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

/*
 * Updates user status in the background.
 */
public class UpdateStatusTask extends AsyncTask<String, Integer, String> {
	private Hashtable<String, String> parameters;

	public UpdateStatusTask(Hashtable<String, String> params) {
		parameters = params;
	}

	protected String doInBackground(String... searchKey) {

		String url = searchKey[0];

		try {
			// Make connection with the server
			return ServerDbAdapter.connectToServer(this, url, parameters);
			// return "SUCCESS";
			// return downloadImage(url);
		} catch (Exception e) {
			// Log.v("Exception google search","Exception:"+e.getMessage());
			return null;

		}
	}

	protected void onPostExecute(String result) {
		try {
			showUploadSuccess(result);
		} catch (Exception e) {

		}

	}

	public void showUploadSuccess(String json) {
		if (json == null) {
			return;
		}
		try {
			JSONObject object = new JSONObject(json);
			String status = object.getString("status");
			status = status.trim();
			if (status.equals("OK")) {

			} else {
			}
		} catch (Exception e) {

		}

	}

};