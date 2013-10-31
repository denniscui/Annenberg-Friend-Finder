package com.harvard.annenberg;

import java.util.ArrayList;
import java.util.Hashtable;

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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

/*
 * Updated AnnenbergActivity. Currently hardcoding rows/columns - can be scaled to other dining halls 
 * by making a server call to find rows/columns.
 */
public class NewAnnenbergActivity extends Activity {

	private static final String CHECKIN_URL = "http://mgm.funformobile.com/aff/checkIn.php";
	public static final String FETCH_URL = "http://mgm.funformobile.com/aff/fetchAll.php";
	public static final String FETCHFRIENDS_URL = "http://mgm.funformobile.com/aff/fetchFriends.php";
	private ProgressDialog mProgressDialog;
	private Hashtable<String, String> parameters;
	private SharedPreferences prefs;

	private ArrayList<Person> friends;
	private ArrayList<Person> everyone;

	int numRows;
	int numCols;
	int numTables;

	private boolean shouldFinish;

	ArrayList<FrameLayout> tables;
	ArrayList<Table> tableData;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.newannenberg);
		shouldFinish = this.getIntent().getBooleanExtra("STARTCODE", false);
		prefs = getSharedPreferences("AFF", MODE_PRIVATE);
		friends = new ArrayList<Person>();
		everyone = new ArrayList<Person>();
		tableData = new ArrayList<Table>();
		fetchFriends(prefs.getString("huid", ""));

		numRows = 17;
		numCols = 3;
		numTables = numRows * numCols;
		tables = new ArrayList<FrameLayout>(numTables);
		for (int i = 0; i < numTables; i++)
			tables.add(i, null);
		// ScrollView scroller = new ScrollView(this);

		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int width = metrics.widthPixels;

		TableLayout layout = (TableLayout) this.findViewById(R.id.table_grid);

		layout.setStretchAllColumns(true);
		layout.setShrinkAllColumns(true);

		// TableRow exit = new TableRow(this);
		// TextView exitSign = new TextView(this);
		// exitSign.setText("Exit");
		// exit.addView(exitSign);
		// layout.addView(exit);

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i = 0; i < numRows; i++) {
			TableRow row = new TableRow(this);

			for (int j = 0; j < numCols; j++) {
				FrameLayout tableLayout = (FrameLayout) inflater.inflate(
						R.layout.table, null);
				Button tableButton = (Button) tableLayout.getChildAt(0);
				tableButton.setWidth(width / (numCols + 1));
				tableButton.setHeight(width / 3 / (numCols + 1));
				char letter = (char) ('A' + j);
				tableButton.setText("" + (numRows - i) + letter);
				tableButton.setOnClickListener(tableListener);

				TableRow.LayoutParams p = new android.widget.TableRow.LayoutParams();
				// right-margin = 5dp
				p.rightMargin = (int) (getResources().getDisplayMetrics().density * 5);
				p.leftMargin = (int) (getResources().getDisplayMetrics().density * 5);
				tableLayout.setLayoutParams(p);

				row.addView(tableLayout);
				tables.set(17 * j + numRows - i - 1, tableLayout);
			}

			layout.addView(row);
		}

		// TableRow entrance = new TableRow(this);
		// TextView entranceSign = new TextView(this);
		// entranceSign.setText("Entrance");
		// entrance.addView(entranceSign);
		// layout.addView(entrance);

		// scroller.addView(layout);
	}

	View.OnClickListener tableListener = new View.OnClickListener() {

		public void onClick(View v) {
			String tableInfo = ((TextView) v).getText().toString();
			String number;
			String letter;
			if (tableInfo.length() == 2) {
				number = tableInfo.substring(0, 1);
				letter = tableInfo.substring(1);
			} else {
				number = tableInfo.substring(0, 2);
				letter = tableInfo.substring(2);
			}

			final int tableClicked = Integer.valueOf(number)
					+ (letter.charAt(0) - 'A') * numRows;

			AlertDialog.Builder builder = new AlertDialog.Builder(
					NewAnnenbergActivity.this);
			if (tableData.get(tableClicked - 1).getNumPeople() == 0) {
				builder.setMessage("Nobody is currently sitting at this table!")
						.setPositiveButton("Check In",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										checkIn(prefs.getString("huid", ""),
												tableClicked + "");
									}
								}).setNegativeButton("Close", null)
						.setTitle("Table " + tableInfo).show();
			} else {
				ListView tableList = new ListView(NewAnnenbergActivity.this);
				TableAdapter tAdapter = new TableAdapter(
						NewAnnenbergActivity.this, tableData.get(
								tableClicked - 1).getAll());

				tableList.setAdapter(tAdapter);
				tableList.setCacheColorHint(0);
				tableList.setBackgroundColor(getResources().getColor(
						R.color.black));
				tableList.setDivider(getResources().getDrawable(
						R.drawable.list_divider));
				tableList.setDividerHeight(1);

				builder.setView(tableList)
						.setPositiveButton("Check In",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										checkIn(prefs.getString("huid", ""),
												tableClicked + "");
									}
								}).setNegativeButton("Close", null)
						.setTitle("Table " + tableInfo).show();
			}
		}
	};

	// private void showFinalAlert(CharSequence message) {
	// new AlertDialog.Builder(NewAnnenbergActivity.this)
	// .setTitle("Notice")
	// .setMessage(message)
	// .setPositiveButton("Okay",
	// new DialogInterface.OnClickListener() {
	// public void onClick(DialogInterface dialog,
	// int whichButton) {
	// // finish();
	// }
	// }).setCancelable(true).show();
	// }

	// Fetch list of users
	public void getPeople() {
		parameters = new Hashtable<String, String>();

		parameters.put("token", "trololol");

		FetchAllTask upl = new FetchAllTask();
		upl.execute(FETCH_URL);
	}

	public void fetchFriends(String huid) {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("Fetching People");
		mProgressDialog.setMessage("Fetching People, Please Wait");
		mProgressDialog.setIndeterminate(true);

		mProgressDialog.setCancelable(false);

		mProgressDialog.show();
		parameters = new Hashtable<String, String>();
		parameters.put("huid", huid);
		FetchFriendsTask upl = new FetchFriendsTask();
		upl.execute(FETCHFRIENDS_URL);
	}

	private class FetchFriendsTask extends AsyncTask<String, Integer, String> {

		protected String doInBackground(String... searchKey) {

			String url = searchKey[0];

			try {
				// Log.v("gsearch","gsearch result with AsyncTask");
				return ServerDbAdapter.connectToServer(this, url, parameters);
				// return "SUCCESS";
				// return downloadImage(url);
			} catch (Exception e) {
				// Log.v("Exception google search","Exception:"+e.getMessage());
				if (mProgressDialog.isShowing())
					mProgressDialog.dismiss();
				String message = "An network error has occured. Please try again later";
				Toast.makeText(NewAnnenbergActivity.this, message,
						Toast.LENGTH_SHORT);
				return null;

			}
		}

		protected void onPostExecute(String result) {
			// Toast.makeText(this.get, "Your Ringtone has been downloaded",
			// Toast.LENGTH_LONG).show();
			try {
				// displayMsg();
				// displayImage(resultbm);
				processFriends(result);
				getPeople();
				// Log.v("Ringtone","Ringtone Path:"+resultbm);

			} catch (Exception e) {
				// Log.v("Exception google search","Exception:"+e.getMessage());

			}

		}

		public void processFriends(String json) {
			Log.v("JSON", json);
			if (json == null) {
				String message = "An network error has occured. Please try again later";
				Toast.makeText(NewAnnenbergActivity.this, message,
						Toast.LENGTH_SHORT);
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
						if (image.equals("null"))
							image = "";
						String name = user.getString("name");

						Person person = new Person(HUID, name, image, state,
								table, time);

						friends.add(person);
					}
				} else {
				}
			} catch (Exception e) {

			}

		}
	};

	private class FetchAllTask extends AsyncTask<String, Integer, String> {

		protected String doInBackground(String... searchKey) {

			String url = searchKey[0];

			try {
				// Log.v("gsearch","gsearch result with AsyncTask");
				return ServerDbAdapter.connectToServer(this, url, parameters);
				// return "SUCCESS";
				// return downloadImage(url);
			} catch (Exception e) {
				// Log.v("Exception google search","Exception:"+e.getMessage());
				if (mProgressDialog.isShowing())
					mProgressDialog.dismiss();
				String message = "An network error has occured. Please try again later";
				Toast.makeText(NewAnnenbergActivity.this, message,
						Toast.LENGTH_SHORT);
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
				processAll();
				// Log.v("Ringtone","Ringtone Path:"+resultbm);

			} catch (Exception e) {
				// Log.v("Exception google search","Exception:"+e.getMessage());

			}

		}
	};

	public void showUploadSuccess(String json) {
		Log.v("JSON", json);
		if (json == null) {
			String message = "An network error has occured. Please try again later";
			Toast.makeText(NewAnnenbergActivity.this, message,
					Toast.LENGTH_SHORT);
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

					if (!friends.contains(person))
						everyone.add(person);
				}
			} else {
			}
		} catch (Exception e) {

		}

	}

	public void processAll() {
		for (int i = 1; i <= numTables; i++) {
			Table table = new Table(i);

			Log.v("Number of friends", friends.size() + "");
			for (Person p : friends) {
				if (Integer.valueOf(p.getTable()) == i)
					table.addFriend(p);
			}

			Log.v("Number of others", everyone.size() + "");
			for (Person p : everyone) {
				if (Integer.valueOf(p.getTable()) == i)
					table.addOther(p);
			}

			TextView friendsButton = (TextView) tables.get(i - 1).getChildAt(1);
			if (table.getNumFriends() > 0) {
				friendsButton.setVisibility(View.VISIBLE);
				friendsButton.setText(table.getNumFriends() + "");
			}

			tableData.add(table);
		}
	}

	public void checkIn(String huid, String tableNum) {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("Checking In");
		mProgressDialog.setMessage("Checking in, Please Wait");
		mProgressDialog.setIndeterminate(true);

		mProgressDialog.setCancelable(false);

		mProgressDialog.show();
		parameters = new Hashtable<String, String>();
		parameters.put("huid", huid);
		parameters.put("table", tableNum);
		CheckInTask upl = new CheckInTask();
		upl.execute(CHECKIN_URL);
	}

	private class CheckInTask extends AsyncTask<String, Integer, String> {

		protected String doInBackground(String... searchKey) {

			String url = searchKey[0];

			try {
				return ServerDbAdapter.connectToServer(this, url, parameters);
			} catch (Exception e) {
				if (mProgressDialog.isShowing())
					mProgressDialog.dismiss();
				String message = "An network error has occured. Please try again later";
				Toast.makeText(NewAnnenbergActivity.this, message,
						Toast.LENGTH_SHORT);
				return null;

			}
		}

		protected void onPostExecute(String result) {
			try {
				mProgressDialog.dismiss();
				showUploadSuccess(result);
				if (shouldFinish) {
					Intent intent = NewAnnenbergActivity.this.getIntent();
					NewAnnenbergActivity.this.setResult(RESULT_OK, intent);
					NewAnnenbergActivity.this.finish();
				}

			} catch (Exception e) {

			}

		}

		public void showUploadSuccess(String json) {
			Log.v("JSON", json);
			if (json == null) {
				String message = "An network error has occured. Please try again later";
				Toast.makeText(NewAnnenbergActivity.this, message,
						Toast.LENGTH_SHORT).show();
				return;
			}
			try {
				JSONObject object = new JSONObject(json);
				String status = object.getString("status");
				status = status.trim();
				Log.v("STATUS", "Status is: " + status);
				if (status.equals("OK")) {
					setAlarm(prefs.getString("huid", ""));
					Toast.makeText(NewAnnenbergActivity.this,
							"You have checked in!", Toast.LENGTH_SHORT).show();
				} else {
					Log.v("STATUS", status);
					String message = status;
					Toast.makeText(NewAnnenbergActivity.this, message,
							Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {

			}

		}
	};

	public void setAlarm(final String HUID) {
		Log.v("Alarm", "Alarm Set");

		Intent intent = new Intent("CheckOutAlarm");
		intent.putExtra("huid", HUID);
		PendingIntent pintent = PendingIntent.getBroadcast(this, 0, intent, 0);
		AlarmManager manager = (AlarmManager) (this
				.getSystemService(Context.ALARM_SERVICE));

		manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,
				SystemClock.elapsedRealtime() + 1000 * 60 * 45, pintent);
	}
}
