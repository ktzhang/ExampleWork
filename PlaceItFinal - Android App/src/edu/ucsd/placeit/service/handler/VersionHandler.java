package edu.ucsd.placeit.service.handler;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import edu.ucsd.placeit.db.DatabaseHelper;
import edu.ucsd.placeit.db.OnlineDatabaseHelper;
import edu.ucsd.placeit.model.PlaceIt;
import edu.ucsd.placeit.service.IVersionHandler;
import edu.ucsd.placeit.util.Cfg;
import edu.ucsd.placeit.util.Consts;

public class VersionHandler implements IVersionHandler {
	private OnlineDatabaseHelper onlineDb;
	private DatabaseHelper localDb;
	private Context mContext;

	/**
	 * Constructor to start and initialize the new online database helper
	 * 
	 * @param context
	 * @return 0 if equal,
	 */
	public VersionHandler(Context context) {
		mContext = context;
		onlineDb = new OnlineDatabaseHelper(context);
	}

	public int handleConflicts() {
		Date onlineVersion = getOnlineVersion();
		Date localVersion = getLocalVersion();
		long differenceInMillis = onlineVersion.getTime()
				- localVersion.getTime();
		// Make sure the date is within the range
		long diff = Math.abs(differenceInMillis);

		Log.d(Consts.TAG_VERSION, " THE DIFFERENCE IS " + diff);
		if (diff < Cfg.VERSION_RANGE) {
			Log.d(Consts.TAG_VERSION, "Database is current");
			// Create a toast
			// Toast.makeText(mContext, "Database is up to date",
			// Toast.LENGTH_SHORT).show();
			return 0;
		} else if (onlineVersion.after(localVersion)) {
			Log.d(Consts.TAG_VERSION, "Local database is behind");
			updateLocalDb();
			// Passing the intent to the main activity
			Intent intent = new Intent(Consts.BROADCAST_DB_UPDATE);
			mContext.sendBroadcast(intent);
			Toast.makeText(mContext, "Recieving sync message",
					Toast.LENGTH_SHORT).show();

			return 1;
		} else if (onlineVersion.before(localVersion)) {
			Toast.makeText(mContext, "Sending sync message", Toast.LENGTH_SHORT)
					.show();

			Log.d(Consts.TAG_VERSION, "Local database is ahead");
			updateOnlineDb();

			return -1;
		}
		// Shouldn't come out here
		return 200;
	}

	/**
	 * Update the onlineDb based on localDb
	 * 
	 * @return number of placeIts affected
	 */
	private int updateOnlineDb() {
		localDb = new DatabaseHelper(mContext);
		List<PlaceIt> list = localDb.getAllPlaceIts();
		Date lastUpdate = localDb.getDbVersion();
		localDb.closeDB();

		onlineDb.deleteAllPlaceIts();
		int counter = 0;
		for (PlaceIt placeIt : list) {
			onlineDb.createUpdatePlaceIt(placeIt);
			counter++;
		}
		onlineDb.addUpdateVersion(lastUpdate);
		return counter;
	}

	/**
	 * Updates the localDb based on onlineDb
	 * 
	 * @return number of placeIts affected
	 */
	private int updateLocalDb() {
		List<PlaceIt> list = onlineDb.getAllPlaceIts();
		Date lastUpdate = onlineDb.getDbVersion();

		localDb = new DatabaseHelper(mContext);
		localDb.deleteAllPlaceIts();
		int counter = 0;
		for (PlaceIt placeIt : list) {
			localDb.createPlaceIt(placeIt);
			counter++;
		}
		localDb.setVersion(lastUpdate);
		localDb.closeDB();
		return counter;
	}

	/**
	 * Get the online database version from the database
	 * 
	 * @return
	 */
	public Date getOnlineVersion() {
		return onlineDb.getDbVersion();
	}

	/**
	 * Retrieve local database version
	 * 
	 * @return
	 */
	public Date getLocalVersion() {
		localDb = new DatabaseHelper(mContext);
		Date date = localDb.getDbVersion();
		localDb.closeDB();
		return date;
	}
}
