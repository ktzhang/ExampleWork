package edu.ucsd.placeit.db;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.nfc.Tag;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import edu.ucsd.placeit.model.PlaceIt;
import edu.ucsd.placeit.model.impl.CategoricalPlaceIt;
import edu.ucsd.placeit.model.impl.NormalPlaceIt;
import edu.ucsd.placeit.model.impl.RecurringPlaceIt;
import edu.ucsd.placeit.service.handler.VersionHandler;
import edu.ucsd.placeit.util.Consts;

@SuppressLint("DefaultLocale")
public class DatabaseHelper extends SQLiteOpenHelper {

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "placeItsManager";

	// Table Names
	private static final String TABLE_PLACEIT = "placeIts";
	// private static final String TABLE_TAG = "states";
	// private static final String TABLE_PLACEIT_TAG = "placeIt_states";

	// Common column names
	private static final String KEY_ID = "id";

	// PlaceIts Table - column names
	private static final String KEY_TYPE = "type";
	private static final String KEY_USER = "user";
	private static final String KEY_TITLE = "title";
	private static final String KEY_DESC = "desc";
	private static final String KEY_STATE = "state";
	private static final String KEY_LONGITUDE = "longitude";
	private static final String KEY_LATITUDE = "latitude";
	private static final String KEY_DATE_CREATED = "date_created";
	private static final String KEY_DATE_TO_POST = "date_to_post";
	private static final String KEY_DATE_FREQUENCY_START = "date_freq_start";
	private static final String KEY_FREQUENCY = "frequency";
	private static final String KEY_CAT_1 = "cat_1";
	private static final String KEY_CAT_2 = "cat_2";
	private static final String KEY_CAT_3 = "cat_3";

	// ------------Version stuff-------------\\
	private static final String TABLE_VERSION = "version";
	private static final String KEY_VERSION = "date";

	private Context mContext;
	private VersionHandler mVh;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		mContext = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// create required tables
		String query = String.format(Queries.CREATE_TABLE_PLACEIT,
				TABLE_PLACEIT, KEY_ID, KEY_TYPE, KEY_USER, KEY_TITLE, KEY_DESC,
				KEY_STATE, KEY_LONGITUDE, KEY_LATITUDE, KEY_DATE_CREATED,
				KEY_DATE_TO_POST, KEY_DATE_FREQUENCY_START, KEY_FREQUENCY,
				KEY_CAT_1, KEY_CAT_2, KEY_CAT_3);
		Log.d(Consts.TAG_TEST, "creating table: " + query);
		db.execSQL(query);

		// creating the version table
		String versionQuery = String
				.format(Queries.CREATE_TABLE_VERSION_STRING, TABLE_VERSION,
						KEY_VERSION);
		Log.d(Consts.TAG_TEST, "creating table: " + query);
		db.execSQL(versionQuery);

		// Adding the current version to the database
		ContentValues values = new ContentValues();
		values.put(KEY_VERSION, Consts.DATE_FORMAT_SIMPLE.format(new Date(0)));
		@SuppressWarnings("unused")
		long placeIt_id = db.insert(TABLE_VERSION, null, values);
		Log.d(Consts.TAG_DATABASE, "inserted version into " + placeIt_id);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// On upgrade drop older tables
		db.execSQL(String.format(Queries.DROP_TABLE, TABLE_PLACEIT));
		// Create new tables
		onCreate(db);
	}

	// ------METHODS------\\
	/**
	 * Get the current version of the database
	 * 
	 * @return
	 */
	public Date getDbVersion() {
		SQLiteDatabase db = this.getReadableDatabase();
		String selectQuery = String.format(Queries.SELECT_VERSION,
				TABLE_VERSION);
		Log.e(Consts.TAG_DATABASE, selectQuery);
		Cursor c = db.rawQuery(selectQuery, null);
		// Only selecting the first
		if (c != null) {
			c.moveToFirst();
			return getVersionDate(c);
		} else {
			return null;
		}
	}

	/**
	 * Set the version of the database
	 * 
	 * @param date
	 * @return
	 */
	public Date setVersion(Date date) {
		Log.d(Consts.TAG_VERSION, "version is set with date = " + date);
		if (date == null) {
			date = new Date();
		}
		SQLiteDatabase db = this.getWritableDatabase();
		String dateString = Consts.DATE_FORMAT_SIMPLE.format(date);
		// String updateQuery = String.format(Queries.CHANGE_VERSION,
		// TABLE_VERSION, KEY_VERSION, dateString);
		// Log.e(Consts.TAG_DATABASE, "set version " + updateQuery);
		// Cursor cursor = db.rawQuery(updateQuery, null);
		// cursor.close();
		ContentValues values = new ContentValues();
		values.put(KEY_VERSION, dateString);
		db.update(TABLE_VERSION, values, null, null);
		return date;
	}

	/*
	 * Creating a placeIt
	 * 
	 * @return PlaceIt id
	 */
	public int createPlaceIt(PlaceIt placeIt) {
		Log.d(Consts.TAG_DATABASE, "databasehelper create a new placeit");
		SQLiteDatabase db = this.getWritableDatabase();

		this.setVersion(new Date());

		Log.d(Consts.TAG_DATABASE, "Received a writable Database");
		ContentValues values = new ContentValues();
		values.put(KEY_USER, placeIt.getUser());
		values.put(KEY_TITLE, placeIt.getTitle());
		values.put(KEY_DESC, placeIt.getDesc());
		values.put(KEY_STATE, placeIt.getState());
		values.put(KEY_DATE_CREATED,
				Consts.DATE_FORMAT_SIMPLE.format(placeIt.getCreationDate()));
		values.put(KEY_DATE_TO_POST,
				Consts.DATE_FORMAT_SIMPLE.format(placeIt.getPostDate()));
		values.put(KEY_DATE_FREQUENCY_START,
				Consts.DATE_FORMAT_SIMPLE.format(placeIt.getPostDate()));

		if (placeIt instanceof NormalPlaceIt) {
			LatLng coord = ((PlaceIt) placeIt).getCoord();
			values.put(KEY_TYPE, Consts.TYPE_NORMAL);
			values.put(KEY_LONGITUDE, coord.longitude);
			values.put(KEY_LATITUDE, coord.latitude);
			values.put(KEY_CAT_1, "");
			values.put(KEY_CAT_2, "");
			values.put(KEY_CAT_3, "");
			values.put(KEY_FREQUENCY, "");

		} else if (placeIt instanceof CategoricalPlaceIt) {
			values.put(KEY_TYPE, Consts.TYPE_CATEGORICAL);

			values.put(KEY_LONGITUDE, "0");
			values.put(KEY_LATITUDE, "0");
			try {
				values.put(KEY_CAT_1,
						((CategoricalPlaceIt) placeIt).getCategories()[0]);
				values.put(KEY_CAT_2,
						((CategoricalPlaceIt) placeIt).getCategories()[1]);
				values.put(KEY_CAT_3,
						((CategoricalPlaceIt) placeIt).getCategories()[2]);
			} catch (ArrayIndexOutOfBoundsException e) {
			}

		} else if (placeIt instanceof RecurringPlaceIt) {
			values.put(KEY_TYPE, Consts.TYPE_RECURRING);

			LatLng coord = ((RecurringPlaceIt) placeIt).getCoord();
			values.put(KEY_LONGITUDE, coord.longitude);
			values.put(KEY_LATITUDE, coord.latitude);
			values.put(KEY_CAT_1, "");
			values.put(KEY_CAT_2, "");
			values.put(KEY_CAT_3, "");
			values.put(KEY_FREQUENCY,
					((RecurringPlaceIt) placeIt).getFrequency());
		}

		// insert row
		Log.d(Consts.TAG_DATABASE, "Before inserting into database");
		long placeIt_id = db.insert(TABLE_PLACEIT, null, values);
		Log.d(Consts.TAG_DATABASE, "Inserted" + placeIt_id);

		return (int) placeIt_id;
	}

	/*
	 * Get single placeIt based on the ID
	 * 
	 * @return PlaceIT
	 */
	@SuppressLint("DefaultLocale")
	public PlaceIt getPlaceIt(int placeItId) {
		SQLiteDatabase db = this.getReadableDatabase();

		String selectQuery = String.format(Queries.SELECT_PLACEIT,
				TABLE_PLACEIT, KEY_ID, placeItId);

		Log.e(Consts.TAG_DATABASE, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		// Only selecting the first
		if (c != null) {
			c.moveToFirst();
			return addPlaceItFromDb(c);
		} else {
			return null;
		}
	}

	/**
	 * getting all placeIts
	 * */
	public List<PlaceIt> getAllPlaceIts() {
		SQLiteDatabase db = this.getReadableDatabase();

		List<PlaceIt> placeIts = new ArrayList<PlaceIt>();
		String selectQuery = String.format(Queries.SELECT_ALL_PLACEIT,
				TABLE_PLACEIT);

		Log.e(Consts.TAG_DATABASE, selectQuery);

		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			Log.e(Consts.TAG_DATABASE, "TABLE has stuff");
			do {
				placeIts.add(addPlaceItFromDb(c));
			} while (c.moveToNext());
		} else {
			Log.e(Consts.TAG_DATABASE, "TABLE IS EMPTY");
		}
		return placeIts;
	}

	/**
	 * Getting all placeIts based on the state and category
	 * 
	 * @param state
	 *            the state to pass in. 0 for any.
	 * @param category
	 *            the category to check for. null for any.
	 * */
	@SuppressLint("NewApi")
	public List<PlaceIt> getAllPlaceIts(int state, String category, int type,
			String userName) {
		SQLiteDatabase db = this.getReadableDatabase();
		// Category check to make sure it is not wildcard
		if (category == null || category.isEmpty() || category == "") {
			category = "%";
		}
		String stateString;
		// Do a state check
		if (state == 0) {
			stateString = "%";
		} else {
			stateString = Integer.toString(state);
		}

		String sType;
		if (type == 0) {
			sType = "%";
		} else {
			sType = String.valueOf(type);
		}

		//Username Validation
		if (userName == null || userName.isEmpty() || userName == "") {
			userName = "%";
		}

		List<PlaceIt> placeIts = new ArrayList<PlaceIt>();
		String selectQuery = String.format(Queries.SELECT_STATE_CAT_PLACEIT,
				TABLE_PLACEIT, KEY_STATE, stateString, KEY_CAT_1, category,
				KEY_CAT_2, category, KEY_CAT_3, category, KEY_TYPE, sType,
				KEY_USER, userName);

		Cursor c = db.rawQuery(selectQuery, null);
		Log.e(Consts.TAG_DATABASE, selectQuery);
		Log.d(Consts.TAG_TEST, "getAllPlaceIts() " + c.getCount());
		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			Log.e(Consts.TAG_DATABASE, "TABLE has stuff");
			do {
				placeIts.add(addPlaceItFromDb(c));
			} while (c.moveToNext());
		} else {
			Log.e(Consts.TAG_DATABASE, "TABLE IS EMPTY");
		}
		return placeIts;
	}

	/**
	 * getting all placeIts under single tag
	 * */
	// public List<PlaceIt> getAllPlaceItsByTag(String tag_name) {
	// List<PlaceIt> placeIts = new ArrayList<PlaceIt>();
	//
	// String selectQuery = "SELECT  * FROM " + TABLE_PLACEIT + " td, "
	// + TABLE_TAG + " tg, " + TABLE_PLACEIT_TAG + " tt WHERE tg."
	// + KEY_TAG_NAME + " = '" + tag_name + "'" + " AND tg." + KEY_ID
	// + " = " + "tt." + KEY_TAG_ID + " AND td." + KEY_ID + " = "
	// + "tt." + KEY_TODO_ID;
	//
	// Log.e(LOG, selectQuery);
	//
	// SQLiteDatabase db = this.getReadableDatabase();
	// Cursor c = db.rawQuery(selectQuery, null);
	//
	// // looping through all rows and adding to list
	// if (c.moveToFirst()) {
	// do {
	// PlaceIt td = new PlaceIt();
	// td.setId(c.getInt((c.getColumnIndex(KEY_ID))));
	// td.setNote((c.getString(c.getColumnIndex(KEY_TODO))));
	// td.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));
	//
	// // adding to placeIt list
	// placeIts.add(td);
	// } while (c.moveToNext());
	// }
	//
	// return placeIts;
	// }

	/*
	 * Getting placeIt count
	 */
	public int getPlaceItCount() {
		String countQuery = String.format(Queries.SELECT_ALL_PLACEIT,
				TABLE_PLACEIT);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int count = cursor.getCount();
		cursor.close();

		// return count
		return count;
	}

	/*
	 * Updating a placeIt
	 * 
	 * @param the placeIt
	 * 
	 * @return the count of rows affected
	 */
	@SuppressLint("DefaultLocale")
	public int updatePlaceIt(PlaceIt placeIt) {
		SQLiteDatabase db = this.getWritableDatabase();
		this.setVersion(new Date());
		// ContentValues values = new ContentValues();

		int placeItId = placeIt.getId();
		String user = placeIt.getUser();
		String title = placeIt.getTitle();
		String desc = placeIt.getDesc();
		int state = placeIt.getState();

		String created = Consts.DATE_FORMAT_SIMPLE.format(placeIt
				.getCreationDate());
		String posted = Consts.DATE_FORMAT_SIMPLE.format(placeIt.getPostDate());
		String freqStart = Consts.DATE_FORMAT_SIMPLE.format(placeIt
				.getPostDate());
		String[] category = new String[Consts.NUM_CAT];
		// Initializing the category string array
		for (int i = 0; i < category.length; i++) {
			category[i] = "";
		}
		// Default variables
		double longitude = 0;
		double latitude = 0;
		int frequency = 0;
		int type = 0;
		if (placeIt instanceof NormalPlaceIt) {
			type = Consts.TYPE_NORMAL;
			LatLng coord = ((PlaceIt) placeIt).getCoord();
			longitude = coord.longitude;
			latitude = coord.latitude;

		} else if (placeIt instanceof CategoricalPlaceIt) {
			type = Consts.TYPE_CATEGORICAL;

			try {
				String[] pCategory = ((CategoricalPlaceIt) placeIt)
						.getCategories();
				for (int i = 0; i < 3; i++) {
					category[i] = pCategory[i];
				}
			} catch (ArrayIndexOutOfBoundsException e) {
			}

		} else if (placeIt instanceof RecurringPlaceIt) {
			type = Consts.TYPE_RECURRING;

			LatLng coord = ((RecurringPlaceIt) placeIt).getCoord();
			longitude = coord.longitude;
			latitude = coord.latitude;
			frequency = ((RecurringPlaceIt) placeIt).getFrequency();

		}

		String updateQuery = String.format(Queries.UPDATE_PLACEIT,
				TABLE_PLACEIT, KEY_TYPE, type, KEY_USER, user, KEY_TITLE,
				title, KEY_DESC, desc, KEY_STATE, state, KEY_LONGITUDE,
				longitude, KEY_LATITUDE, latitude, KEY_DATE_CREATED, created,
				KEY_DATE_TO_POST, posted, KEY_DATE_FREQUENCY_START, freqStart,
				KEY_FREQUENCY, frequency, KEY_CAT_1, category[0], KEY_CAT_2,
				category[1], KEY_CAT_3, category[2], KEY_ID, placeItId);

		Log.e(Consts.TAG_DATABASE, updateQuery);
		Cursor cursor = db.rawQuery(updateQuery, null);
		return cursor.getCount();
	}

	/**
	 * Deleting a placeIt
	 */
	public int deletePlaceIt(int placeItId) {
		SQLiteDatabase db = this.getWritableDatabase();
		this.setVersion(new Date());

		return db.delete(TABLE_PLACEIT, KEY_ID + " = ?",
				new String[] { String.valueOf(placeItId) });
	}

	public void changePlaceItState(int placeItId, int state) {
		SQLiteDatabase db = this.getWritableDatabase();

		this.setVersion(new Date());

		// Just change the state of the query
		String updateQuery = String.format(Queries.CHANGE_STATE_PLACEIT,
				TABLE_PLACEIT, KEY_STATE, state, KEY_ID, placeItId);

		Log.e(Consts.TAG_DATABASE, updateQuery);
		db.execSQL(updateQuery);
	}

	/**
	 * Delete all placeIts
	 */
	public void deleteAllPlaceIts() {
		SQLiteDatabase db = this.getReadableDatabase();
		this.setVersion(new Date());

		Log.d(Consts.TAG_DATABASE, "deleting all placeits!!!");
		// Clearing the table
		db.delete(TABLE_PLACEIT, null, null);
		// Resetting the unique key
		db.delete("sqlite_sequence", "name='" + TABLE_PLACEIT + "'", null);

	}

	/**
	 * Private helper method retrieve single place it from a cursor
	 * 
	 * @param c
	 *            the current cursor
	 * @return
	 */
	private PlaceIt addPlaceItFromDb(Cursor c) {
		PlaceIt placeIt = null;
		try {
			if (c.getCount() > 0) {
				Log.d(Consts.TAG_DATABASE, "retreiving single placeit");
				int id = c.getInt(c.getColumnIndex(KEY_ID));
				String user = c.getString(c.getColumnIndex(KEY_USER));
				String title = c.getString(c.getColumnIndex(KEY_TITLE));
				String desc = c.getString(c.getColumnIndex(KEY_DESC));
				int state = c.getInt(c.getColumnIndex(KEY_STATE));
				double longitude = c.getDouble(c.getColumnIndex(KEY_LONGITUDE));
				double latitude = c.getDouble(c.getColumnIndex(KEY_LATITUDE));
				LatLng coord = new LatLng(latitude, longitude);
				Date dateCreated = Consts.DATE_FORMAT_SIMPLE.parse(c
						.getString(c.getColumnIndex(KEY_DATE_CREATED)));
				Date datePosted = Consts.DATE_FORMAT_SIMPLE.parse(c.getString(c
						.getColumnIndex(KEY_DATE_TO_POST)));
				// Date dateFreqStart =
				// Consts.DATE_FORMAT_SIMPLE.parse(c.getString(c
				// .getColumnIndex(KEY_DATE_FREQUENCY_START)));

				int frequency = c.getInt(c.getColumnIndex(KEY_FREQUENCY));
				int type = c.getInt(c.getColumnIndex(KEY_TYPE));
				String[] categories = new String[3];
				categories[0] = c.getString(c.getColumnIndex(KEY_CAT_1));
				categories[1] = c.getString(c.getColumnIndex(KEY_CAT_2));
				categories[2] = c.getString(c.getColumnIndex(KEY_CAT_3));

				switch (type) {
				case Consts.TYPE_NORMAL:
					placeIt = new NormalPlaceIt(id, user, title, desc, state,
							coord, dateCreated, datePosted);
					break;
				case Consts.TYPE_CATEGORICAL:
					placeIt = new CategoricalPlaceIt(id, user, title, desc,
							state, dateCreated, datePosted, categories);
					break;
				case Consts.TYPE_RECURRING:
					placeIt = new RecurringPlaceIt(id, user, title, desc,
							state, coord, dateCreated, datePosted, frequency);
					break;
				default:
					Log.d(Consts.TAG_DATABASE, "cant find a placeit");
					break;
				}
			} else {
				return null;
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return placeIt;
	}

	/**
	 * Get version date
	 * 
	 * @param c
	 * @return
	 */
	private Date getVersionDate(Cursor c) {
		Date date = null;
		try {
			if (c.getCount() > 0) {
				Log.d(Consts.TAG_DATABASE, "retreiving version");
				String dateString = c.getString(c.getColumnIndex(KEY_VERSION));
				date = Consts.DATE_FORMAT_SIMPLE.parse(dateString);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * Closing the database
	 */
	public void closeDB() {
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null && db.isOpen())
			db.close();
	}

	/**
	 * Calls the update version
	 * 
	 * @return
	 */
//	public int checkVersionUpdate() {
//		if (mVh == null) {
//			mVh = new VersionHandler(mContext);
//		}
//		return mVh.handleConflicts();
//	}

}
