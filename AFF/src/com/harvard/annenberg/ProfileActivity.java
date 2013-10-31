package com.harvard.annenberg;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.Toast;

/*
 * User's profile.
 */
public class ProfileActivity extends Activity {
	DbAdapter database;
	private SharedPreferences prefs;
	private int currentSelection;
	private ProgressDialog mProgressDialog;
	private Hashtable<String, String> parameters;
	private boolean notSet = false;
	private Spinner s;

	private String mImageUri;
	private Uri image;
	private String timeOfUpdate;

	private TextView table;
	private ImageView mImage;
	private static byte[] myImage;
	private static String sessionId;

	private int tableID;

	private Button checkIn;

	public static final String UPDATE_URL = "http://mgm.funformobile.com/aff/updateIsEating.php";
	public static final String GET_URL = "http://mgm.funformobile.com/aff/getStatus.php";

	public static final int PICKPIC_FROM_ALBUM = 1;

	public void onCreate(Bundle bun) {
		super.onCreate(bun);
		setContentView(R.layout.profile_layout);
		prefs = getSharedPreferences("AFF", 0);
		// Update Name and HUID
		TextView name = (TextView) findViewById(R.id.profile_name);
		name.setText(prefs.getString("n", ""));
		table = (TextView) findViewById(R.id.profile_table);
		// TextView huid = (TextView) findViewById(R.id.profile_HUID);
		// huid.setText(prefs.getString("huid", ""));

		mImage = (ImageView) findViewById(R.id.profile_image);

		String huid = prefs.getString("huid", "").trim();
		int urlData = huid.lastIndexOf("/");
		String aURL = "http://mgm.funformobile.com/aff/img/"
				+ huid.substring(urlData + 1) + ".jpg";

		ImageDownloader imgDownloader = new ImageDownloader();
		imgDownloader.execute(aURL);

		// Image
		// TODO: Gallery set image stuff.
		// String uriString = c.getString(c
		// .getColumnIndexOrThrow(DbAdapter.KEY_USER_IMAGE));
		// if (uriString.equals("") == false) {
		// Uri uri = Uri.parse(uriString);
		// ImageView image = (ImageView) findViewById(R.id.profile_image);
		// image.setImageURI(uri);
		// }
		// c.close();
		// database.close();
		// Spinner
		s = (Spinner) findViewById(R.id.profile_status);
		ArrayAdapter a = ArrayAdapter.createFromResource(this,
				R.array.status_array, android.R.layout.simple_spinner_item);
		a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(a);
		s.setOnItemSelectedListener(statusListener);

		getStatus();

		// Table
		// TextView tableText = (TextView) findViewById(R.id.profile_table);
		// String table = prefs.getString("table", "");
		// tableText.setText((table.equals("") ? "N/A" : "" + table));

		// Check in button
		checkIn = (Button) findViewById(R.id.check_in);
		checkIn.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				if (prefs.getBoolean("checkedin", false) == false) {
					Intent i = new Intent(ProfileActivity.this,
							NewAnnenbergActivity.class);
					i.putExtra("STARTCODE", true);
					startActivityForResult(i, 0);
				} else {
					currentSelection = 1;
					tableID = 0;
					prefs.edit().putBoolean("checkedin", false).commit();
					checkIn.setText("Check In");
					s.setSelection(0);
					table.setText("N/A");
				}

			}
		}

		);

		/**
		 * Creates listener for the icon.
		 */
		mImage.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent innerIntent = new Intent(Intent.ACTION_GET_CONTENT);
				innerIntent.setType("image/*");
				startActivityForResult(innerIntent, PICKPIC_FROM_ALBUM);
			}
		});

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {
			if (requestCode == PICKPIC_FROM_ALBUM && data != null) {
				image = data.getData();
				ImgUtil imageUtilities = new ImgUtil(this);
				byte[] bm = imageUtilities.getResizedImageData(image, 320, 320);
				Bitmap bmp = BitmapFactory.decodeByteArray(bm, 0, bm.length);
				mImage.setImageBitmap(bmp);
				myImage = bm;

				doUpload((prefs.getString("huid", "")), image);
			} else if (requestCode == 0) {
				getStatus();
				// int tableNum = data.getIntExtra("tableNum", tableID);
				//
				// String tableString = "" + ((tableNum - 1) % 17 + 1);
				// if (tableNum > 17 && tableNum <= 34)
				// tableString += "B";
				// else if (tableNum > 34)
				// tableString += "C";
				// else if (tableNum == 0)
				// tableString = "N/A";
				// else
				// tableString += "A";
				//
				// table.setText(tableString);
			}
		}
	}

	public void doUpload(String HUID, Uri imgUri) {
		if (imgUri == null)
			return;

		// this.serverId = serverId;
		int urlData = HUID.lastIndexOf("/");
		UploadTask upl = new UploadTask(this);
		upl.execute(HUID.substring(urlData + 1), imgUri.toString());
	}

	OnItemSelectedListener statusListener = new OnItemSelectedListener() {

		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			currentSelection = position + 1;
			if (notSet) {
				updateStatus();
				prefs.edit().putInt("status", position).commit();
			} else
				notSet = true;
			// TODO Auto-generated method stub

		}

		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub

		}

	};

	protected void onResume() {
		notSet = false;
		if (prefs.getInt("status", -1) != -1)
			s.setSelection(prefs.getInt("status", -1));
		super.onResume();
	}

	// Fetch list of users
	public void updateStatus() {
		parameters = new Hashtable<String, String>();

		parameters.put("huid", prefs.getString("huid", ""));
		if (currentSelection == 1)
			parameters.put("eatStatus", "N");
		else
			parameters.put("eatStatus", "Y");
		parameters.put("state", String.valueOf(currentSelection));

		UpdateStatusTask upl = new UpdateStatusTask(parameters);
		upl.execute(UPDATE_URL);

	}

	public void getStatus() {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("Getting Status");
		mProgressDialog.setMessage("Getting Status, Please Wait");
		mProgressDialog.setIndeterminate(true);

		mProgressDialog.setCancelable(false);

		mProgressDialog.show();
		parameters = new Hashtable<String, String>();

		parameters.put("huid", prefs.getString("huid", ""));

		GetStatusTask upl = new GetStatusTask();
		upl.execute(GET_URL);
	}

	private void showFinalAlert(CharSequence message) {
		new AlertDialog.Builder(ProfileActivity.this)
				.setTitle("Notice")
				.setMessage(message)
				.setPositiveButton("Okay",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// finish();
							}
						}).setCancelable(false).show();
	}

	private class GetStatusTask extends AsyncTask<String, Integer, String> {

		protected String doInBackground(String... searchKey) {

			String url = searchKey[0];

			try {
				// Log.v("gsearch","gsearch result with AsyncTask");
				return ServerDbAdapter.connectToServer(this, url, parameters);
				// return "SUCCESS";
				// return downloadImage(url);
			} catch (Exception e) {
				if (mProgressDialog.isShowing())
					mProgressDialog.dismiss();
				String message = "An network error has occured. Please try again later";
				Toast.makeText(ProfileActivity.this, message,
						Toast.LENGTH_SHORT);
				return null;

			}
		}

		protected void onPostExecute(String result) {
			try {
				mProgressDialog.dismiss();
				showUploadSuccess(result);
			} catch (Exception e) {
			}

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
					JSONObject info = list.getJSONObject(0);
					currentSelection = info.getInt("statusNum");
					timeOfUpdate = info.getString("time").split(" ")[1];
					prefs.edit().putInt("status", currentSelection - 1)
							.commit();
					tableID = info.getInt("tableNum");

					if (prefs.getInt("status", -1) != -1)
						s.setSelection(prefs.getInt("status", -1));

					if (tableID != 0) {
						prefs.edit().putBoolean("checkedin", true).commit();
						checkIn.setText("Check Out");
						// setAlarm();
					} else {
						prefs.edit().putBoolean("checkedin", false).commit();
						checkIn.setText("Check In");
					}

					String tableString = "" + ((tableID - 1) % 17 + 1);
					if (tableID > 17 && tableID <= 34)
						tableString += "B";
					else if (tableID > 34)
						tableString += "C";
					else if (tableID == 0)
						tableString = "N/A";
					else
						tableString += "A";
					table.setText(tableString);

					ImageDownloader imageGetter = new ImageDownloader();
					String aURL = "http://mgm.funformobile.com/aff/img/"
							+ prefs.getString("huid", "").trim() + ".jpg";
					imageGetter.execute(aURL);
				} else {
					Log.v("STATUS", status);
					String message = status;
					showFinalAlert(message);
				}
			} catch (Exception e) {

			}

		}
	};

	private class ImageDownloader extends AsyncTask<String, Integer, byte[]> {

		@Override
		protected byte[] doInBackground(String... searchKey) {

			String url = searchKey[0];

			if (url.equals(""))
				return null;

			URL aURL;
			try {
				aURL = new URL(url);

				if (sessionId != null)
					Log.v("Session", sessionId);
				Log.v("HUID", prefs.getString("huid", ""));
				if (myImage != null
						&& prefs.getString("huid", "").equals(sessionId)) {
					return myImage;
				}

				URLConnection conn = aURL.openConnection();
				conn.connect();
				InputStream is = conn.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				ByteArrayBuffer baf = new ByteArrayBuffer(50);

				int current = 0;

				while ((current = bis.read()) != -1) {

					baf.append((byte) current);

				}
				is.close();
				bis.close();

				byte[] bytes = baf.toByteArray();
				baf.clear();
				myImage = bytes;

				return bytes;
				// adp.notifyDataSetChanged();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}

		}

		protected void onPostExecute(byte[] result) {
			sessionId = prefs.getString("huid", "");
			if (result != null) {
				Bitmap bmp = BitmapFactory.decodeByteArray(result, 0,
						result.length);
				myImage = result;
				mImage.setImageBitmap(bmp);
			}
		}
	};
}
