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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/*
 * Displays the title page.
 */
public class TitlePageActivity extends Activity {

	private DbAdapter db = new DbAdapter(this);
	private ImageView logStatus;
	private boolean mIsLogin = false;

	private LinearLayout masterRow;

	private SharedPreferences prefs;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = this.getSharedPreferences("AFF", MODE_PRIVATE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.title_page);

		checkLogin();

		logStatus = (ImageView) findViewById(R.id.title_login_image);
		if (mIsLogin)
			logStatus.setImageResource(R.drawable.login);
		else
			logStatus.setImageResource(R.drawable.logout);

		logStatus.setOnClickListener(logStatusListener);

		TextView mTitleText = (TextView) findViewById(R.id.title_title);
		mTitleText.setText("Annenberg Friend Finder");

		TextView mBottomText = (TextView) findViewById(R.id.welcomeTitle);
		if (mIsLogin) {
			mBottomText.setText("Hi, " + prefs.getString("n", "") + ".");
		} else
			mBottomText.setText("Not logged in.");

		ImageView friendfinder = (ImageView) findViewById(R.id.title_friendfinder);
		friendfinder.setOnClickListener(friendFinderListener);
		ImageView menu = (ImageView) findViewById(R.id.title_menu);
		menu.setOnClickListener(menuListener);
		ImageView schedule = (ImageView) findViewById(R.id.title_schedule);
		schedule.setOnClickListener(scheduleListener);

		masterRow = (LinearLayout) findViewById(R.id.master_row);
		// Button recoverCards = (Button) findViewById(R.id.title_recovercards);
		// recoverCards.setOnClickListener(recoverCardsListener);
	}

	private View.OnClickListener friendFinderListener = new View.OnClickListener() {

		public void onClick(View v) {
			if (mIsLogin) {
				Intent currentIntent = new Intent(TitlePageActivity.this,
						FriendFinderTabHost.class);
				startActivity(currentIntent);
			} else {
				Intent currentIntent = new Intent(TitlePageActivity.this,
						LogInActivity.class);
				startActivityForResult(currentIntent, 0);
			}
		}
	};

	private View.OnClickListener menuListener = new View.OnClickListener() {

		public void onClick(View v) {
			Intent currentIntent = new Intent(TitlePageActivity.this,
					MenuTabHost.class);

			startActivity(currentIntent);
		}

	};

	private View.OnClickListener scheduleListener = new View.OnClickListener() {

		public void onClick(View v) {
			Intent currentIntent = new Intent(TitlePageActivity.this,
					CalendarActivity.class);
			startActivity(currentIntent);
		}

	};

	private View.OnClickListener logStatusListener = new View.OnClickListener() {

		public void onClick(View v) {
			if (mIsLogin) {
				new AlertDialog.Builder(TitlePageActivity.this)
						.setTitle("Log Out")
						.setMessage("Would you like to log out?")
						.setPositiveButton("Okay",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										final SharedPreferences prefs = getSharedPreferences(
												"AFF", MODE_PRIVATE);
										SharedPreferences.Editor prefsEditor = prefs
												.edit();
										prefsEditor.clear();
										prefsEditor.commit();

										mIsLogin = false;
										logStatus
												.setImageResource(R.drawable.logout);

										TextView mBottomText = (TextView) findViewById(R.id.welcomeTitle);
										mBottomText.setText("Not logged in.");
									}
								})
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
									}
								}).setCancelable(true).show();
			} else {
				Intent currentIntent = new Intent(TitlePageActivity.this,
						LogInActivity.class);
				startActivity(currentIntent);
			}
		}

	};

	private void checkLogin() {
		final SharedPreferences prefs = getSharedPreferences("AFF",
				MODE_PRIVATE);
		mIsLogin = prefs.getBoolean("login", false);
	}

	@Override
	public void onResume() {
		checkLogin();
		if (mIsLogin)
			logStatus.setImageResource(R.drawable.login);
		else
			logStatus.setImageResource(R.drawable.logout);

		TextView mBottomText = (TextView) findViewById(R.id.welcomeTitle);
		if (mIsLogin) {
			mBottomText.setText("Hi, " + prefs.getString("n", "") + ".");
		} else
			mBottomText.setText("Not logged in.");
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			checkLogin();
			if (mIsLogin)
				logStatus.setImageResource(R.drawable.login);
			else
				logStatus.setImageResource(R.drawable.logout);

			TextView mBottomText = (TextView) findViewById(R.id.welcomeTitle);
			if (mIsLogin) {
				mBottomText.setText("Hi, " + prefs.getString("n", "") + ".");
			} else
				mBottomText.setText("Not logged in.");
		}
		// TODO handle here.
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// Checks the orientation of the screen
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			masterRow.setOrientation(LinearLayout.HORIZONTAL);
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			masterRow.setOrientation(LinearLayout.VERTICAL);
		}
	}

	public void onDestroy() {
		try {
			db.close();
		} catch (Exception e) {

		}
		super.onDestroy();
	}

}
