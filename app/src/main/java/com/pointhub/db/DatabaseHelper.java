package com.pointhub.db;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Venu on 02/05/2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	// Logcat tag
	private static final String LOG = "DatabaseHelper";

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "pointshub";

	// Table Names
	private static final String TABLE_POINTS = "points";


	// Common column names
	private static final String KEY_ID = "id";
	private static final String STORE_NAME = "store_name";
	private static final String POINTS = "points";
	private static final String LAST_VISITED = "last_visited";




	// Table Create Statements
	// Todo table create statement
	private static final String CREATE_TABLE_POINTS = "CREATE TABLE "
			+ TABLE_POINTS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + STORE_NAME
			+ " TEXT," + POINTS + " TEXT," + LAST_VISITED
			+ " TEXT" + ")";



	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	private static Context context;

	private static DatabaseHelper helper;

	// private User currentUser;

	public static DatabaseHelper getInstance(Context context) {

		if(helper == null) {

			helper = new DatabaseHelper(context);
		}
		return helper;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		// creating required tables
		db.execSQL(CREATE_TABLE_POINTS);
		// db.execSQL(CREATE_TABLE_USER);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_POINTS);

		// create new tables
		onCreate(db);
	}

	// ------------------------ "todos" table methods ----------------//

	/*
	 * Creating a todo
	 */
	public long createPoints(Points points) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(STORE_NAME, points.getStoreName());
		values.put(POINTS, points.getPoints());
		values.put(LAST_VISITED, points.getLastVisited());

		// insert row
		long todo_id = db.insert(TABLE_POINTS, null, values);

		return todo_id;
	}

	/*
	 * get single todo
	 */
	public Points getPoints(String storeName) {

		Points point = null;

		try {

			SQLiteDatabase db = this.getReadableDatabase();

			String selectQuery = "SELECT  * FROM " + TABLE_POINTS + " WHERE " + STORE_NAME + " like '" + storeName + "'";

			Log.e(LOG, selectQuery);

			Cursor c = db.rawQuery(selectQuery, null);

			if (c != null && c.getCount()>0) {


					c.moveToFirst();
			} else {
				return point;
			}

			point = new Points();
			// point.setLastVisited(c.getString(c.getColumnIndex(LAST_VISITED)));
			point.setStoreName(c.getString(c.getColumnIndex(STORE_NAME)));
			point.setPoints(c.getString(c.getColumnIndex(POINTS)));
			point.setId(c.getInt(c.getColumnIndex(KEY_ID)));

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return point;
	}


	/**
	 * getting all todos
	 * */
	public List<Points> getAllPoints() {

		List<Points> todos = new ArrayList<Points>();
		String selectQuery = "SELECT  * FROM " + TABLE_POINTS;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {

				Points point = new Points();
				point.setLastVisited(c.getString(c.getColumnIndex(LAST_VISITED)));
				point.setStoreName(c.getString(c.getColumnIndex(STORE_NAME)));
				point.setPoints(c.getString(c.getColumnIndex(POINTS)));
				point.setId(c.getInt(c.getColumnIndex(KEY_ID)));
				// adding to todo list
				todos.add(point);
			} while (c.moveToNext());
		}

		return todos;
	}


	/*
	 * Updating a todo
	 */
	public int updatePoints(Points points) {

		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(POINTS, points.getPoints());
		values.put(STORE_NAME, points.getStoreName());
		values.put(LAST_VISITED, points.getLastVisited());

		// updating row
		return db.update(TABLE_POINTS, values, STORE_NAME + " = ?", new String[] { points.getStoreName() });
	}

	/*
	 * Deleting a todo
	 */
	public void deletePoint(String storeName) {

		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_POINTS, STORE_NAME + " = ?", new String[] { storeName });
	}

	public void deletePoint(int id) {

		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_POINTS, KEY_ID + " = ?", new String[] { id+"" });
	}


	// closing database
	public void closeDB() {
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null && db.isOpen())
			db.close();
	}

	/**
	 * get datetime
	 * */
	public String getDateTime() {

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}
}
