package com.harvard.annenberg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/*
 * Fetches recipe data.
 */
public class FetchRecipe extends AsyncTask<String, Integer, String> {

	private ProgressDialog mProgressDialog;
	private Context context;
	private MenuDbAdapter db;
	private String recipeId;

	private String name;
	private String ingredients;
	private boolean isVegetarian;
	private boolean isVegan;
	private boolean isMK;
	private boolean isLocal;
	private boolean isOrganic;

	public FetchRecipe(Context context1, String recipeId) {
		context = context1;
		db = new MenuDbAdapter(context);
		this.recipeId = recipeId;
	}

	public void doFetch() {
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setTitle("Retrieving Data");
		mProgressDialog.setMessage("Looking for Recipe Data, Please Wait");
		mProgressDialog.setIndeterminate(true);

		mProgressDialog.setCancelable(false);

		mProgressDialog.show();
		this.execute(recipeId);
	}

	@Override
	protected String doInBackground(String... searchKey) {
		String recipe = searchKey[0];

		try {
			return getRecipeInfo(recipe);
		} catch (Exception e) {
			return null;

		}
	}

	protected void onPostExecute(String result) {
		try {
			mProgressDialog.dismiss();
			if (showSuccess(result)) {
				Intent intent = new Intent(context, RecipeActivity.class);
				intent.putExtra("name", this.name);
				intent.putExtra("ingredients", ingredients);
				intent.putExtra("isVegetarian", isVegetarian);
				intent.putExtra("isVegan", isVegan);
				intent.putExtra("isMK", isMK);
				intent.putExtra("isLocal", isLocal);
				intent.putExtra("isOrganic", isOrganic);
				context.startActivity(intent);
			}
		} catch (Exception e) {
			// Log.v("Exception google search","Exception:"+e.getMessage());

		}

	}

	public boolean showSuccess(String json) {
		if (json == null) {
			Toast.makeText(
					context,
					"Unable to retrieve data. Please check that you have a working internet connection.",
					Toast.LENGTH_LONG).show();
			Log.v("GG!", "JSON IS NULL");
			return false;
		}
		try {
			JSONArray recipeArray = new JSONArray(json);

			for (int i = 0; i < recipeArray.length(); i++) {

				String vegetarian = recipeArray.getJSONObject(i).getString(
						"VEGETARIAN");
				String vegan = recipeArray.getJSONObject(i).getString("VEGAN");
				String mollieKatzen = recipeArray.getJSONObject(i).getString(
						"MOLLIE KATZEN");
				String local = recipeArray.getJSONObject(i).getString("LOCAL");
				String organic = recipeArray.getJSONObject(i).getString(
						"ORGANIC");

				ingredients = recipeArray.getJSONObject(i).getString(
						"ingredients");

				name = recipeArray.getJSONObject(i).getString("name");

				isVegetarian = vegetarian.equals("TRUE");
				isVegan = vegan.equals("TRUE");
				isMK = mollieKatzen.equals("TRUE");
				isLocal = local.equals("TRUE");
				isOrganic = organic.equals("TRUE");

			}

			Log.v("YAY", "IT WORKS!");
			return true;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}

		Log.v("GG!", "JSON IS NULL");
		return false;
	}

	private String getRecipeInfo(String recipeId) {
		URL requestUrl = null;
		HttpURLConnection conn = null;
		try {
			requestUrl = new URL("http://food.cs50.net/api/1.3/recipes?id="
					+ recipeId + "&output=json");
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

			conn.disconnect();
			reader.close();

			// concert json to string for parsing
			String jString = result.toString();

			return jString;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}

		return null;
	}
}
