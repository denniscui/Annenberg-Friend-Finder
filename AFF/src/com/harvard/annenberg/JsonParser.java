package com.harvard.annenberg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/*
 * Parses JSON data.
 */
public class JsonParser {

	private static MenuDbAdapter db;

	public static ArrayList<ArrayList<FoodItem>> parseMenu(Context c) {

		db = new MenuDbAdapter(c);
		// return value
		ArrayList<FoodItem> breakfast = new ArrayList<FoodItem>();
		ArrayList<FoodItem> lunch = new ArrayList<FoodItem>();
		ArrayList<FoodItem> dinner = new ArrayList<FoodItem>();

		// HttpClient httpclient = new DefaultHttpClient();
		// HttpPost httpUrl = new HttpPost(
		// "http://courses.cs50.net/api/1.0/courses?output=json");
		// HttpResponse response = null;

		URL requestUrl = null;
		HttpURLConnection conn = null;
		try {
			requestUrl = new URL(
					"http://food.cs50.net/api/1.3/menus?output=json");
			conn = (HttpURLConnection) requestUrl.openConnection();
			conn.setRequestMethod("GET");
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		conn.setDoOutput(true);
		conn.setDoInput(true);
		// access json file in resources

		try {
			// get json from raw resources
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line;
			StringBuffer result = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				result.append(line);
				result.append('\r');
			}

			reader.close();

			// concert json to string for parsing
			String jString = result.toString();

			JSONArray foodArray = new JSONArray(jString);
			db.open(false);

			for (int i = 0; i < foodArray.length(); i++) {

				String date = foodArray.getJSONObject(i).getString("date");
				Log.v("Insert Date", date);
				String meal = foodArray.getJSONObject(i).getString("meal");
				String category = foodArray.getJSONObject(i).getString(
						"category");
				String recipe = foodArray.getJSONObject(i).getString("recipe");
				String name = foodArray.getJSONObject(i).getString("name");

				FoodItem food = new FoodItem(meal, category, recipe, name);

				db.createFood(date, meal, category, recipe, name);
				if (meal.equals("BREAKFAST"))
					breakfast.add(food);
				else if (meal.equals("LUNCH"))
					lunch.add(food);
				else
					dinner.add(food);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		db.close();

		ArrayList<ArrayList<FoodItem>> menu = new ArrayList<ArrayList<FoodItem>>();
		menu.add(breakfast);
		menu.add(lunch);
		menu.add(dinner);

		return menu;
	}
}
