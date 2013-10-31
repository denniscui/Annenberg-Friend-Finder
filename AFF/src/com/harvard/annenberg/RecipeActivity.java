package com.harvard.annenberg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

/*
 * Displays data for a food item.
 */
public class RecipeActivity extends Activity {

	private boolean isVegetarian = false;
	private boolean isVegan = false;
	private boolean isMK = false;
	private boolean isLocal = false;
	private boolean isOrganic = false;

	private String ingredients;
	private String name;

	private TextView title;
	private TextView mIngredients;

	private CheckBox mVegetarian;
	private CheckBox mVegan;
	private CheckBox mMK;
	private CheckBox mLocal;
	private CheckBox mOrganic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.recipe_activity);

		title = (TextView) findViewById(R.id.recipeTitle);
		mIngredients = (TextView) findViewById(R.id.ingredients);
		mVegetarian = (CheckBox) findViewById(R.id.vegetarian);
		mVegan = (CheckBox) findViewById(R.id.vegan);
		mMK = (CheckBox) findViewById(R.id.mollieKatzen);
		mLocal = (CheckBox) findViewById(R.id.local);
		mOrganic = (CheckBox) findViewById(R.id.organic);

		ingredients = this.getIntent().getExtras().getString("ingredients");
		name = this.getIntent().getExtras().getString("name");

		isVegetarian = this.getIntent().getExtras().getBoolean("isVegetarian");
		isVegan = this.getIntent().getExtras().getBoolean("isVegan");
		isMK = this.getIntent().getExtras().getBoolean("isMK");
		isLocal = this.getIntent().getExtras().getBoolean("isLocal");
		isOrganic = this.getIntent().getExtras().getBoolean("isOrganic");

		title.setText(name);
		mIngredients.setText("Ingredients:\n" + ingredients);

		if (isVegetarian)
			mVegetarian.setChecked(true);
		if (isVegan)
			mVegan.setChecked(true);
		if (isMK)
			mMK.setChecked(true);
		if (isLocal)
			mLocal.setChecked(true);
		if (isOrganic)
			mOrganic.setChecked(true);
	}

}
