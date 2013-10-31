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
 * TabHost for the menu.
 */
public class MenuTabHost extends TabActivity {

	private ArrayList<ArrayList<FoodItem>> menu;
	private SharedPreferences prefs;
	private MenuDbAdapter db;
	private FetchData fetcher;
	private String date;
	private Calendar c;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		menu = new ArrayList<ArrayList<FoodItem>>(3);
		for (int i = 0; i < 4; i++)
			menu.add(new ArrayList<FoodItem>());

		db = new MenuDbAdapter(this);
		db.open(false);
		prefs = this.getSharedPreferences("Menu", 0);

		date = getDate();

		String savedDate = prefs.getString("date", null);
		if (savedDate == null || !savedDate.equals(date)) {
			fetcher = new FetchData(this);
			fetcher.doFetch();
		} else
			menu = populateMenu();

		TabHost tabHost = getTabHost();

		// Tab for breakfast
		TabSpec breakfastSpec = tabHost.newTabSpec("Breakfast");
		// setting Title and Icon for the Tab
		breakfastSpec.setIndicator("Breakfast",
				getResources().getDrawable(R.drawable.breakfast));
		Intent breakfastIntent = new Intent(this, BreakfastActivity.class);
		breakfastIntent.putExtra("menu", menu.get(0));
		breakfastIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		breakfastSpec.setContent(breakfastIntent);

		// Tab for lunch
		TabSpec lunchSpec = tabHost.newTabSpec("Lunch");
		lunchSpec.setIndicator("Lunch",
				getResources().getDrawable(R.drawable.lunch));
		Intent lunchIntent = new Intent(this, LunchActivity.class);
		lunchIntent.putExtra("menu", menu.get(1));
		lunchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		lunchSpec.setContent(lunchIntent);

		// Tab for dinner
		TabSpec dinnerSpec = tabHost.newTabSpec("Dinner");
		dinnerSpec.setIndicator("Dinner",
				getResources().getDrawable(R.drawable.dinner));
		Intent dinnerIntent = new Intent(this, DinnerActivity.class);
		dinnerIntent.putExtra("menu", menu.get(2));
		dinnerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		dinnerSpec.setContent(dinnerIntent);

		// Tab for brunch
		TabSpec brunchSpec = tabHost.newTabSpec("Brunch");
		brunchSpec.setIndicator("Brunch",
				getResources().getDrawable(R.drawable.brunch));
		Intent brunchIntent = new Intent(this, BrunchActivity.class);
		brunchIntent.putExtra("menu", menu.get(3));
		brunchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		brunchSpec.setContent(brunchIntent);

		// Adding all TabSpecs to TabHost
		if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
			tabHost.addTab(brunchSpec);
			tabHost.addTab(dinnerSpec);
		} else {
			tabHost.addTab(breakfastSpec);
			tabHost.addTab(lunchSpec);
			tabHost.addTab(dinnerSpec);
		}
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

	private ArrayList<ArrayList<FoodItem>> populateMenu() {

		Cursor mCursor = db.fetchFoodByDate(date);
		mCursor.moveToFirst();
		
		//TODO Check if mCursor is null

		while (!mCursor.isAfterLast()) {
			String meal = mCursor.getString(mCursor
					.getColumnIndexOrThrow(MenuDbAdapter.KEY_FOOD_MEAL));

			Log.v("MEAL", meal);
			
			String category = mCursor.getString(mCursor
					.getColumnIndexOrThrow(MenuDbAdapter.KEY_FOOD_CATEGORY));
			String recipe = mCursor.getString(mCursor
					.getColumnIndexOrThrow(MenuDbAdapter.KEY_FOOD_RECIPE));
			String name = mCursor.getString(mCursor
					.getColumnIndexOrThrow(MenuDbAdapter.KEY_FOOD_NAME));

			FoodItem food = new FoodItem(meal, category, recipe, name);
			if (meal.equals("BREAKFAST"))
				menu.get(0).add(food);
			else if (meal.equals("LUNCH"))
				menu.get(1).add(food);
			else if (meal.equals("DINNER"))
				menu.get(2).add(food);
			else if (meal.equals("BRUNCH"))
				menu.get(3).add(food);

			mCursor.moveToNext();
		}
		mCursor.close();
		db.close();

		return menu;
	}

	public class FetchData extends AsyncTask<String, Integer, String> {

		private ProgressDialog mProgressDialog;
		Context context;
		private MenuDbAdapter db;

		public FetchData(Context context1) {
			context = context1;
			db = new MenuDbAdapter(context);
		}

		public void doFetch() {
			mProgressDialog = new ProgressDialog(context);
			mProgressDialog.setTitle("Retrieving Data");
			mProgressDialog.setMessage("Looking for Food Data, Please Wait");
			mProgressDialog.setIndeterminate(true);

			mProgressDialog.setCancelable(false);

			mProgressDialog.show();
			this.execute();
		}

		@Override
		protected String doInBackground(String... searchKey) {

			try {
				// Log.v("gsearch","gsearch result with AsyncTask");
				// Log.v("Create", "Create successfully executed.");
				return fetch();
				// return downloadImage(url);
			} catch (Exception e) {
				// Log.v("Exception google search","Exception:"+e.getMessage());
				return null;

			}
		}

		protected void onPostExecute(String result) {
			try {
				SharedPreferences.Editor edit = prefs.edit();

				edit.putString("date", date);
				edit.commit();
				menu = populateMenu();
				mProgressDialog.dismiss();
				getTabHost().setCurrentTab(1);
				getTabHost().setCurrentTab(0);
			} catch (Exception e) {
				// Log.v("Exception google search","Exception:"+e.getMessage());

			}

		}

		public void showSuccess(String json) {
			if (json == null) {
				Toast.makeText(
						context,
						"Unable to retrieve data. Please check that you have a working internet connection.",
						Toast.LENGTH_LONG).show();
				return;
			}
			try {
				JSONArray foodArray = new JSONArray(json);
				db.open(false);

				for (int i = 0; i < foodArray.length(); i++) {

					String date = foodArray.getJSONObject(i).getString("date");
					// Log.v("Insert Date", date);
					String meal = foodArray.getJSONObject(i).getString("meal");
					String category = foodArray.getJSONObject(i).getString(
							"category");
					String recipe = foodArray.getJSONObject(i).getString(
							"recipe");
					String name = foodArray.getJSONObject(i).getString("name");

					db.createFood(date, meal, category, recipe, name);

				}

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				db.close();
				try {
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		public String fetch() {
			try {
				URL requestUrl = null;
				HttpURLConnection conn = null;
				requestUrl = new URL(
						"http://food.cs50.net/api/1.3/menus?output=json");
				conn = (HttpURLConnection) requestUrl.openConnection();
				conn.setRequestMethod("GET");

				conn.setDoOutput(true);
				conn.setDoInput(true);

				// get json from raw resources
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(conn.getInputStream()));
				String line;
				StringBuffer result = new StringBuffer();
				while ((line = reader.readLine()) != null) {
					result.append(line);
					result.append('\r');
				}

				reader.close();

				// concert json to string for parsing
				String jString = result.toString();

				showSuccess(jString);
				return jString;
			} catch (MalformedURLException me) {
				Log.v("MalformedURLException", me.toString());
				return null;
			} catch (IOException ie) {

				Log.v("IOException", ie.toString());
				return null;

			} catch (StringIndexOutOfBoundsException e) {
				Log.v("Exception", e.toString());
				return null;
			} catch (Exception e) {
				Log.v("Exception", e.toString());
				return null;
				// Log.e("HREQ", "Exception: "+e.toString());
			}

		}

	}

}
