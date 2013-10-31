package com.harvard.annenberg;

import android.content.ContentValues;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/*
 * Database adapter for the Menu.
 */
public class MenuDbAdapter {

	public static final String FOOD_DB_TABLE = "food";

	public static final String KEY_FOOD_ID = "local_id";
	public static final int KEY_FOOD_ID_NUM = 0;
	public static final String KEY_FOOD_DATE = "date";
	public static final int KEY_FOOD_DATE_NUM = 1;
	public static final String KEY_FOOD_MEAL = "meal";
	public static final int KEY_FOOD_MEAL_NUM = 2;
	public static final String KEY_FOOD_CATEGORY = "category";
	public static final int KEY_FOOD_CATEGORY_NUM = 3;
	public static final String KEY_FOOD_RECIPE = "recipe";
	public static final int KEY_FOOD_RECIPE_NUM = 4;
	public static final String KEY_FOOD_NAME = "name";
	public static final int KEY_FOOD_NAME_NUM = 5;

	private static final String DATABASE_NAME = "foods";
	private static final int DATABASE_VERSION = 1;

	public DatabaseHelper mDbHelper;
	public SQLiteDatabase mDb;

	private final Context context;

	public MenuDbAdapter(Context _context) {
		this.context = _context;
		mDbHelper = null;
		mDb = null;
	}

	/**
	 * Table creation sql statement
	 */
	private static final String FOOD_DB_CREATE = "create table "
			+ FOOD_DB_TABLE + " (" + KEY_FOOD_ID
			+ " integer primary key autoincrement, " + KEY_FOOD_DATE
			+ " text default 'Unknown', " + KEY_FOOD_MEAL
			+ " text default 'Unknown', " + KEY_FOOD_CATEGORY
			+ " text default 'Unknown', " + KEY_FOOD_RECIPE
			+ " text default 'Unknown', " + KEY_FOOD_NAME
			+ " text default 'Unknown'" + ");";

	public static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(FOOD_DB_CREATE);
			Log.v("Database", FOOD_DB_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		}
	}

	/**
	 * Open the contacts database. If it cannot be opened, try to create a new
	 * instance of the database. If it cannot be created, throw an exception to
	 * signal the failure
	 * 
	 * @param readOnly
	 *            if the database should be opened read only
	 * @return this (self reference, allowing this to be chained in an
	 *         initialization call)
	 * @throws SQLException
	 *             if the database could be neither opened or created
	 */
	public void open(boolean readOnly) throws SQLException {
		if (mDbHelper == null) {
			mDbHelper = new DatabaseHelper(context);
			if (readOnly) {
				mDb = mDbHelper.getReadableDatabase();
			} else {
				mDb = mDbHelper.getWritableDatabase();
			}
		}

	}

	/**
	 * Close the database
	 */
	public void close() {
		if (mDbHelper != null) {
			mDbHelper.close();
			mDbHelper = null;
		}
	}

	public int createFood(String date, String meal, String category,
			String recipe, String name) {

		ContentValues vals = new ContentValues();
		// vals.put(KEY_CONTACT_ID, contactId);
		vals.put(KEY_FOOD_DATE, date.trim());
		vals.put(KEY_FOOD_MEAL, meal.trim());
		vals.put(KEY_FOOD_CATEGORY, category.trim());
		vals.put(KEY_FOOD_RECIPE, recipe.trim());
		vals.put(KEY_FOOD_NAME, name.trim());
		return (int) mDb.insert(FOOD_DB_TABLE, null, vals);
	}

	public Cursor fetchFood(int id) throws SQLException {
		boolean found = false;
		Cursor mCursor = mDb.query(true, FOOD_DB_TABLE, null, KEY_FOOD_ID + "="
				+ id, null, null, null, null, null);
		if (mCursor != null) {
			found = mCursor.moveToFirst();
		}
		if (!found) {
			if (mCursor != null) {
				mCursor.close();
			}
			return null;
		}
		return mCursor;
	}

	public Cursor fetchFoodByDate(String date) throws SQLException {
		boolean found = false;
		Cursor mCursor = mDb.query(true, FOOD_DB_TABLE, null, KEY_FOOD_DATE
				+ "='" + date + "'", null, null, null, null, null);
		if (mCursor != null) {
			found = mCursor.moveToFirst();
		}
		if (!found) {
			if (mCursor != null) {
				mCursor.close();
			}
			return null;
		}
		return mCursor;
	}

	/**
	 * Fetch a list of all contacts in the database
	 * 
	 * @return Db cursor
	 */
	public Cursor fetchAllFoods() {
		boolean found = false;
		Cursor mCursor = mDb.query(FOOD_DB_TABLE, null, null, null, null, null,
				null);

		if (mCursor != null) {
			found = mCursor.moveToFirst();
		}
		if (!found) {
			if (mCursor != null) {
				mCursor.close();
			}
			return null;
		}
		return mCursor;
	}
}
