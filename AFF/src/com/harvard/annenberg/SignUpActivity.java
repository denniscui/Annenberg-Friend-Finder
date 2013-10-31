package com.harvard.annenberg;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;

import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.widget.*;

/*
 * Handles signing up.
 */
public class SignUpActivity extends Activity {

	public static final String SIGNUP_URL = "http://mgm.funformobile.com/aff/SignUp.php";

	private Hashtable<String, String> parameters;
	private ProgressDialog mProgressDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.sign_up);
		TextView signUpTitle = (TextView) findViewById(R.id.signupTitle);
		signUpTitle.setText("Sign Up");
		
		Button signUpButton = (Button) findViewById(R.id.signup_submit);
		signUpButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String name = ((EditText) findViewById(R.id.signup_name))
						.getText().toString();
				if (name.length() < 2) {
					showAlert("Please enter a valid name");
					return;
				}
				String password = ((EditText) findViewById(R.id.signup_password))
						.getText().toString();
				String passwordConfirm = ((EditText) findViewById(R.id.signup_password_confirm))
						.getText().toString();
				if (!password.equals(passwordConfirm)) {
					showAlert("Error: Your passwords did not match");
					return;
				}
				if (password.length() < 6) {
					showAlert("Please enter a password of at least 6 characters");
					return;
				}
				int HUID = Integer
						.parseInt(((EditText) findViewById(R.id.signup_HUID))
								.getText().toString());
				// DbAdapter database = new DbAdapter(SignUpActivity.this);
				// database.open(false);
				// if (database.fetchUserByHUID(HUID) != null) {
				// showAlert("Error: That HUID is already registered");
				// return;
				// }
				// database.createUser(name, "", HUID, passwordConfirm);
				// database.close();

				doSignUp(String.valueOf(HUID), password, name);
				// =======
				// if (database.fetchUserByHUID(HUID) != null) {
				// database.close();
				// showAlert("Error: That HUID is already registered");
				// return;
				// }
				// if (database.createUser(name, "", HUID, passwordConfirm) < 0)
				// {
				// database.close();
				// showAlertAndTransfer("Failed\n" + name + "\n" + HUID + "\n"
				// + passwordConfirm + "\n");
				// } else {
				// database.close();
				// showAlertAndTransfer("Profile Created");
				// }
				//
				// >>>>>>> refs/remotes/origin/master
			}
		});
	}

	private void showAlertAndTransfer(String text) {
		new AlertDialog.Builder(this)
				.setTitle("Alert")
				.setMessage(text)
				.setPositiveButton("Okay",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								Intent i = new Intent(SignUpActivity.this,
										LogInActivity.class);
								startActivity(i);
								finish();
							}
						}).setCancelable(false).show();
	}

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_log_in, menu);
		return true;
	}

	// Sign in server stuff
	public void doSignUp(String huid, String password, String username) {
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle("Sign Up");
		mProgressDialog.setMessage("Signing Up, Please Wait");
		mProgressDialog.setIndeterminate(true);

		mProgressDialog.setCancelable(false);

		mProgressDialog.show();
		parameters = new Hashtable<String, String>();

		parameters.put("passwd", password);
		String usrnm = username.replace("\n", " ");
		parameters.put("name", usrnm.trim());
		parameters.put("huid", huid);
		SignupTask upl = new SignupTask();
		upl.execute(SIGNUP_URL);
	}

	private class SignupTask extends AsyncTask<String, Integer, String> {

		protected String doInBackground(String... searchKey) {

			String url = searchKey[0];

			try {
				// Log.v("gsearch","gsearch result with AsyncTask");
				return signup(url, parameters);
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

		public String signup(String serverUrl, Hashtable<String, String> params) {
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
		new AlertDialog.Builder(SignUpActivity.this)
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

				SharedPreferences prefs = getSharedPreferences("AFF",
						MODE_PRIVATE);
				SharedPreferences.Editor prefsEditor = prefs.edit();
				prefsEditor.putString("h", object.getString("h"));
				prefsEditor.putString("huid", object.getString("huid"));
				prefsEditor.putString("n", object.getString("n"));
				prefsEditor.putBoolean("login", true);
				prefsEditor.commit();

				Toast.makeText(this, "You have successfully Signed Up",
						Toast.LENGTH_LONG).show();
				finish();
			} else {
				Log.v("STATUS", status);
				String message = status;
				showFinalAlert(message);
			}
		} catch (Exception e) {

		}

	}
}
