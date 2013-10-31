package com.harvard.annenberg;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;

/*
 * Displays list of friends
 */
public class FriendListActivity extends Activity {
	private FriendListAdapter fla;
	private ExpandableListView expListView;
	private ArrayList<Request> requests;
	private ArrayList<Person> friends;

	private ProgressDialog mProgressDialog;
	private Hashtable<String, String> parameters;
	private SharedPreferences prefs;

	private ArrayList<HashMap<String, String>> groups;
	private ArrayList<ArrayList<HashMap<String, String>>> children;

	public static final String FETCHFRIENDS_URL = "http://mgm.funformobile.com/aff/fetchFriends.php";
	public static final String FETCHREQ_URL = "http://mgm.funformobile.com/aff/fetchFriendRequests.php";
	public static final String ADDFRIEND_URL = "http://mgm.funformobile.com/aff/addFriend.php";
	public static final String ACCEPTFRIEND_URL = "http://mgm.funformobile.com/aff/acceptFriend.php";

	public void onCreate(Bundle bunny) {
		super.onCreate(bunny);
		setContentView(R.layout.friends_list_layout);

		friends = new ArrayList<Person>();
		requests = new ArrayList<Request>();
		prefs = this.getSharedPreferences("AFF", MODE_PRIVATE);

		// TODO: Server call, update requests + friends
		fetchFriends(prefs.getString("huid", ""));
		expListView = (ExpandableListView) findViewById(android.R.id.list);
		expListView.setOnChildClickListener(reqListener);
		expListView.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (ExpandableListView.getPackedPositionType(id) == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
					int groupPosition = ExpandableListView
							.getPackedPositionGroup(id);
					int childPosition = ExpandableListView
							.getPackedPositionChild(id);

					// You now have everything that you would as if this was an
					// OnChildClickListener()
					// Add your logic here.

					// Return true as we are handling the event.
					return true;
				}

				return false;
			}
		});
		expListView.setGroupIndicator(null);

		ImageView b = (ImageView) findViewById(R.id.add_new_friend);
		b.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				EditText e = (EditText) findViewById(R.id.add_friend_huid);

				addFriend(prefs.getString("huid", ""), e.getText().toString());
				// TODO: addFriend based on friendHUID
			}
		});
	}

	private OnChildClickListener reqListener = new OnChildClickListener() {

		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			if (groupPosition == 0) {

				final String friendHUID = children.get(0).get(childPosition)
						.get("HUID");
				final int position = childPosition;

				AlertDialog.Builder builder = new AlertDialog.Builder(
						FriendListActivity.this);
				builder.setCancelable(true);
				builder.setTitle("Options");
				builder.setItems(R.array.req_array,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								if (which == 0) {
									acceptFriend(prefs.getString("huid", ""),
											friendHUID, "Y");

									children.get(0).remove(position);
									fla.notifyDataSetChanged();
								} else if (which == 1) {
									acceptFriend(prefs.getString("huid", ""),
											friendHUID, "N");

									children.get(0).remove(position);
									fla.notifyDataSetChanged();
								}
							}
						});
				builder.create();
				builder.show();
			} else {

			}
			return true;
		}

	};

	private void showAlert(String text) {
		new AlertDialog.Builder(this)
				.setTitle("Alert")
				.setMessage(text)
				.setPositiveButton("Okay",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								// close
							}
						}).setCancelable(false).show();
	}

	private ArrayList<HashMap<String, String>> getGroups() {
		HashMap<String, String> h = new HashMap<String, String>();
		h.put("category", "Friend Requests");
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		result.add(h);
		h = new HashMap<String, String>();
		h.put("category", "Friends");
		result.add(h);
		return result;
	}

	private ArrayList<ArrayList<HashMap<String, String>>> getChilds() {
		ArrayList<ArrayList<HashMap<String, String>>> result = new ArrayList<ArrayList<HashMap<String, String>>>();
		// Do requests.
		ArrayList<HashMap<String, String>> requestChildren = new ArrayList<HashMap<String, String>>();
		for (Request r : requests) {
			HashMap<String, String> h = new HashMap<String, String>();
			h.put("HUID", r.HUID);
			h.put("name", r.name);
			h.put("img", r.img);
			requestChildren.add(h);
		}
		result.add(requestChildren);

		// Do friends.
		ArrayList<HashMap<String, String>> friendChildren = new ArrayList<HashMap<String, String>>();
		for (Person f : friends) {
			HashMap<String, String> h = new HashMap<String, String>();
			h.put("HUID", f.getHUID());
			h.put("name", f.getName());
			h.put("img", f.getImg());
			h.put("status", f.getStatus());
			h.put("table", f.getTable());
			h.put("time", f.getTime());
			friendChildren.add(h);
		}
		result.add(friendChildren);
		return result;
	}

	public class Request {
		String HUID;
		String name;
		String img;
	}

	// Sign in server stuff
	public void fetchFriends(String huid) {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("Fetching Friends");
		mProgressDialog.setMessage("Fetching Friends, Please Wait");
		mProgressDialog.setIndeterminate(true);

		mProgressDialog.setCancelable(false);

		mProgressDialog.show();
		parameters = new Hashtable<String, String>();
		parameters.put("huid", huid);
		FetchFriendsTask upl = new FetchFriendsTask();
		upl.execute(FETCHFRIENDS_URL);
	}

	public void addFriend(String huid, String fhuid) {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("Adding Friend");
		mProgressDialog.setMessage("Adding Friend, Please Wait");
		mProgressDialog.setIndeterminate(true);

		mProgressDialog.setCancelable(false);

		mProgressDialog.show();
		parameters = new Hashtable<String, String>();
		parameters.put("huid", huid);
		parameters.put("f_huid", fhuid);
		AddFriendTask upl = new AddFriendTask();
		upl.execute(ADDFRIEND_URL);
	}

	public void acceptFriend(String huid, String fhuid, String add) {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("Adding Friend");
		mProgressDialog.setMessage("Adding Friend, Please Wait");
		mProgressDialog.setIndeterminate(true);

		mProgressDialog.setCancelable(false);

		mProgressDialog.show();
		parameters = new Hashtable<String, String>();
		parameters.put("huid", huid);
		parameters.put("f_huid", fhuid);
		parameters.put("add", add);
		AcceptFriendTask upl = new AcceptFriendTask();
		upl.execute(ACCEPTFRIEND_URL);
	}

	public void fetchReq(String huid) {
		parameters = new Hashtable<String, String>();
		parameters.put("huid", huid);
		FetchReqTask upl = new FetchReqTask();
		upl.execute(FETCHREQ_URL);
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
				if (mProgressDialog.isShowing())
					mProgressDialog.dismiss();
				String message = "An network error has occured. Please try again later";
				Toast.makeText(FriendListActivity.this, message,
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
				showUploadSuccess(result);
				fetchReq(prefs.getString("huid", ""));
				// Log.v("Ringtone","Ringtone Path:"+resultbm);

			} catch (Exception e) {
				// Log.v("Exception google search","Exception:"+e.getMessage());

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

	private class FetchReqTask extends AsyncTask<String, Integer, String> {

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
				Toast.makeText(FriendListActivity.this, message,
						Toast.LENGTH_SHORT);
				return null;

			}
		}

		protected void onPostExecute(String result) {
			// Toast.makeText(this.get, "Your Ringtone has been downloaded",
			// Toast.LENGTH_LONG).show();
			try {
				// displayMsg();
				// displayImage(resultbm)
				mProgressDialog.dismiss();
				showUploadSuccess(result);
				groups = getGroups();
				children = getChilds();
				fla = new FriendListAdapter(FriendListActivity.this, groups,
						children, expListView);
				expListView.setAdapter(fla);
				// Log.v("Ringtone","Ringtone Path:"+resultbm);

			} catch (Exception e) {
				Log.v("Exception google search", "Exception:" + e.getMessage());

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
					int length = list.length();
					for (int i = 0; i < length; i++) {
						JSONObject user = list.getJSONObject(i);

						String HUID = user.getString("huid");
						String image = user.getString("imageUri");
						if (image == null)
							image = "";
						String name = user.getString("name");

						Request req = new Request();
						req.HUID = HUID;
						req.img = image;
						req.name = name;

						requests.add(req);
					}

				} else {
				}
			} catch (Exception e) {

			}

		}
	};

	private class AddFriendTask extends AsyncTask<String, Integer, String> {

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
				Toast.makeText(FriendListActivity.this, message,
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
				// Log.v("Ringtone","Ringtone Path:"+resultbm);

			} catch (Exception e) {
				// Log.v("Exception google search","Exception:"+e.getMessage());

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

					String message = "Friend successfully added";
					showFinalAlert(message);

				} else {
					Log.v("STATUS", status);
					String message = status;
					showFinalAlert(message);
				}
			} catch (Exception e) {

			}

		}
	};

	private class AcceptFriendTask extends AsyncTask<String, Integer, String> {

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
				Toast.makeText(FriendListActivity.this, message,
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
				// Log.v("Ringtone","Ringtone Path:"+resultbm);

			} catch (Exception e) {
				// Log.v("Exception google search","Exception:"+e.getMessage());

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
				if (status.equals("ADD")) {

					String message = "Friend successfully added.";
					showFinalAlert(message);

				} else if (status.equals("REMOVE")) {
					String message = "Friend successfully rejected.";
					showFinalAlert(message);
				} else {
					Log.v("STATUS", status);
					String message = status;
					showFinalAlert(message);
				}
			} catch (Exception e) {

			}
		}
	};

	private void showFinalAlert(CharSequence message) {
		new AlertDialog.Builder(FriendListActivity.this)
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
}