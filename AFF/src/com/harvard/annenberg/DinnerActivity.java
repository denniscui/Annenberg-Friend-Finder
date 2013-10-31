package com.harvard.annenberg;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.ExpandableListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

/*
 * Displays dinner menu.
 */
public class DinnerActivity extends ExpandableListActivity {

	private ExpandableListView dinner;

	private ArrayList<FoodItem> menu;
	private ArrayList<ArrayList<FoodItem>> orderedMenu;

	private ArrayList<String> categories;

	MenuAdapter expListAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dinner_activity);
		categories = new ArrayList<String>();

		// get menus
		menu = extractMenu();

		for (FoodItem item : menu) {
			if (categories.indexOf(item.getCategory()) < 0)
				categories.add(item.getCategory());
		}

		orderedMenu = reorganizeMenu();

		// set up list view
		dinner = (ExpandableListView) findViewById(android.R.id.list);
		expListAdapter = new MenuAdapter(this, createGroupList(),
				new String[] { "categoryName" }, createChildList(),
				new String[] { "foodName" }, dinner);

		dinner.setAdapter(expListAdapter);
		dinner.setOnChildClickListener(dinnerListener);
		dinner.setGroupIndicator(null);
	}

	@SuppressWarnings("unchecked")
	private ArrayList<FoodItem> extractMenu() {
		return (ArrayList<FoodItem>) this.getIntent().getExtras().get("menu");
	}

	private OnChildClickListener dinnerListener = new OnChildClickListener() {
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			String recipeId = orderedMenu.get(groupPosition).get(childPosition)
					.getRecipe();

			FetchRecipe fetcher = new FetchRecipe(DinnerActivity.this, recipeId);
			fetcher.doFetch();
			return true;
		}

	};

	private ArrayList<HashMap<String, String>> createGroupList() {
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < categories.size(); i++) {
			HashMap<String, String> m = new HashMap<String, String>();
			m.put("categoryName", categories.get(i));
			result.add(m);
		}
		return result;
	}

	private ArrayList<ArrayList<HashMap<String, String>>> createChildList() {
		ArrayList<ArrayList<HashMap<String, String>>> result = new ArrayList<ArrayList<HashMap<String, String>>>();
		for (int i = 0; i < orderedMenu.size(); i++) {
			// Second-level lists
			ArrayList<HashMap<String, String>> secList = new ArrayList<HashMap<String, String>>();
			for (int n = 0; n < orderedMenu.get(i).size(); n++) {
				HashMap<String, String> child = new HashMap<String, String>();
				child.put("foodName", orderedMenu.get(i).get(n).getName());
				secList.add(child);
			}
			result.add(secList);
		}

		return result;
	}

	private ArrayList<ArrayList<FoodItem>> reorganizeMenu() {
		ArrayList<ArrayList<FoodItem>> newMenu = new ArrayList<ArrayList<FoodItem>>();

		for (int i = 0; i < categories.size(); i++) {
			newMenu.add(new ArrayList<FoodItem>());
			for (int j = 0; j < menu.size(); j++) {
				FoodItem food = menu.get(j);
				if (food.getCategory().equals(categories.get(i)))
					newMenu.get(i).add(food);
			}
		}
		return newMenu;
	}

}
