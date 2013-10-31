package com.harvard.annenberg;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

/*
 * Shows a list of everyone in Annenberg... not used anymore.
 */
public class EveryoneListActivity extends Activity {
	private ProgressDialog mProgressDialog;
	private Hashtable<String, String> parameters;
	private ArrayList<Person> people;
	private ListView l;
	private PersonAdapter p;
	public static final String FETCH_URL = "http://mgm.funformobile.com/aff/fetchAll.php";

	public void onCreate(Bundle bunny) {
		super.onCreate(bunny);
		setContentView(R.layout.all_list_layout);
		l = (ListView) findViewById(R.id.allList);
		people = new ArrayList<Person>();
		// TODO: server call fetch all Active
		p = new PersonAdapter(this, people);
		l.setAdapter(p);
		getPeople();
	}

	// Fetch list of users
	public void getPeople() {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("Finding People");
		mProgressDialog.setMessage("Finding People, Please Wait");
		mProgressDialog.setIndeterminate(true);

		mProgressDialog.setCancelable(false);

		mProgressDialog.show();
		parameters = new Hashtable<String, String>();

		parameters.put("token", "trololol");

		FetchAllTask upl = new FetchAllTask();
		upl.execute(FETCH_URL);
	}

	private class FetchAllTask extends AsyncTask<String, Integer, String> {

		protected String doInBackground(String... searchKey) {

			String url = searchKey[0];

			try {
				// Log.v("gsearch","gsearch result with AsyncTask");
				return fetchAll(url, parameters);
				// return "SUCCESS";
				// return downloadImage(url);
			} catch (Exception e) {
				// Log.v("Exception google search","Exception:"+e.getMessage());
				return null;

			}
		}

		protected void onPostExecute(String result) {
			// Toast.makeText(this.get, "Your Ringtone has been downloaded",
			// Toast.LENGTH_LONG).show();
			try {
				// displayMsg();
				// displayImage(resultbm);
				mProgressDialog.dismiss();
				showUploadSuccess(result);

				// Log.v("Ringtone","Ringtone Path:"+resultbm);

			} catch (Exception e) {
				// Log.v("Exception google search","Exception:"+e.getMessage());

			}

		}

		public String fetchAll(String serverUrl,
				Hashtable<String, String> params) {
			try {

				// Log.v("serverUrl", serverUrl);
				URL url = new URL(serverUrl);
				HttpURLConnection con = (HttpURLConnection) url
						.openConnection();
				con.setDoInput(true);
				con.setDoOutput(true);
				con.setUseCaches(false);
				con.setRequestMethod("POST");
				con.setRequestProperty("Connection", "Keep-Alive");
				String postString = "";

				Enumeration<String> keys = params.keys();
				String key, val;
				while (keys.hasMoreElements()) {
					key = keys.nextElement().toString();
					// Log.v("KEY", key);
					val = params.get(key);
					// Log.v("VAL", val);
					postString += key + "=" + URLEncoder.encode(val, "UTF-8")
							+ "&";
				}
				postString = postString.substring(0, postString.length() - 1);
				Log.v("postString", postString);
				con.setRequestProperty("Content-Length",
						"" + Integer.toString(postString.getBytes().length));
				con.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				DataOutputStream dos = new DataOutputStream(
						con.getOutputStream());
				dos.writeBytes(postString);
				dos.flush();
				dos.close();
				// Responses from the server (code and message)
				int serverResponseCode = con.getResponseCode();
				Log.v("CODE", String.valueOf(serverResponseCode));
				String serverResponseMessage = con.getResponseMessage();
				Log.v("PAGE", serverResponseMessage);

				BufferedReader rd = new BufferedReader(new InputStreamReader(
						con.getInputStream()));
				String line;
				StringBuffer response = new StringBuffer();
				while ((line = rd.readLine()) != null) {
					response.append(line);
					response.append('\r');
				}
				rd.close();
				String json = response.toString();
				return json;

			} catch (MalformedURLException me) {
				Log.v("MalformedURLException", me.toString());
				return null;
			} catch (IOException ie) {
				Log.v("IOException", ie.toString());
				return null;

			} catch (Exception e) {
				Log.v("Exception", e.toString());
				return null;
				// Log.e("HREQ", "Exception: "+e.toString());
			}
		}

	};
	

	private void showFinalAlert(CharSequence message) {
		new AlertDialog.Builder(EveryoneListActivity.this)
				.setTitle("Error")
				.setMessage(message)
				.setPositiveButton("Okay",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// finish();
							}
						}).setCancelable(false).show();
	}

	public void showUploadSuccess(String json) {
		Log.v("JSON", json);
		if (json == null) {
			String message = "An network error has occured. Please try again later";
			showFinalAlert(message);
			return;
		}
		try {
			JSONObject object = new JSONObject(json);
			String status = object.getString("status");
			status = status.trim();
			Log.v("STATUS", "Status is: " + status);
			if (status.equals("OK")) {
				JSONArray list = new JSONArray(object.getString("list"));
				int length = list.length();
				for (int i = 0; i < length; i++) {
					JSONObject user = list.getJSONObject(i);

					String time = user.getString("time");
					String HUID = user.getString("huid");
					String state = user.getString("state");
					String table = user.getString("tableNum");
					String image = user.getString("imageUri");
					if (image == null)
						image = "";
					String name = user.getString("name");

					Person person = new Person(HUID, name, image, state, table,
							time);

					people.add(person);
				}

				p.notifyDataSetChanged();

				// Bundle bundle = new Bundle();
				// bundle.putString("json", json);
				//
				// Intent mIntent = new Intent();
				// mIntent.putExtras(bundle);
				// setResult(RESULT_OK, mIntent);
			} else {
				Log.v("STATUS", status);
				String message = status;
				showFinalAlert(message);
			}
		} catch (Exception e) {

		}

	}
}
