package com.harvard.annenberg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;

import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

/*
 * Tab host for the friend finder
 */
public class FriendFinderTabHost extends TabActivity {

	private SharedPreferences prefs;
	private DbAdapter db;
	private String date;
	private Calendar c;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tab_host);

		TabHost tabHost = getTabHost();

		// Tab for profile
		TabSpec profileSpec = tabHost.newTabSpec("Profile");
		// setting Title and Icon for the Tab
		profileSpec.setIndicator("Profile",
				getResources().getDrawable(R.drawable.profile));
		Intent profileIntent = new Intent(this, ProfileActivity.class);
		profileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		profileSpec.setContent(profileIntent);

		// Tab for friend list
		TabSpec friendSpec = tabHost.newTabSpec("Friends");
		friendSpec.setIndicator("Friends",
				getResources().getDrawable(R.drawable.friends));
		Intent friendIntent = new Intent(this, FriendListActivity.class);
		friendIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		friendSpec.setContent(friendIntent);

		// Tab for everyone list
		// TabSpec everyoneSpec = tabHost.newTabSpec("Everyone");
		// everyoneSpec.setIndicator("Everyone",
		// getResources().getDrawable(R.drawable.everyone));
		// Intent everyoneIntent = new Intent(this, EveryoneListActivity.class);
		// everyoneIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// everyoneSpec.setContent(everyoneIntent);

		// Tab for annenberg
		TabSpec annenbergSpec = tabHost.newTabSpec("Annenberg");
		annenbergSpec.setIndicator("Annenberg",
				getResources().getDrawable(R.drawable.dining));
		Intent annenbergIntent = new Intent(this, NewAnnenbergActivity.class);
		annenbergIntent.putExtra("STARTCODE", false);
		annenbergIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		annenbergSpec.setContent(annenbergIntent);

		tabHost.addTab(profileSpec);
		tabHost.addTab(friendSpec);
		// tabHost.addTab(everyoneSpec);
		tabHost.addTab(annenbergSpec);
	}

	private String getDate() {
		String theDay;

		c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH) + 1;
		int day = c.get(Calendar.DAY_OF_MONTH);

		theDay = year + "-";
		theDay += month < 10 ? "0" + month + "-" : month + "-";
		theDay += day < 10 ? "0" + day : day;

		return theDay;
	}
}
