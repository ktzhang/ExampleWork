package edu.ucsd.placeit.service.handler;

import java.util.List;

import android.content.Context;
import edu.ucsd.placeit.db.DatabaseHelper;
import edu.ucsd.placeit.model.PlaceIt;
import edu.ucsd.placeit.service.IPlaceItHandler;

/**
 * Class to retrieve placeIt and their information
 * @author Kevin
 *
 */
public class PlaceItHandler implements IPlaceItHandler {
	private Context mContext;
	private DatabaseHelper mDatabaseHelper;

	public PlaceItHandler(Context context) {
		mContext = context;
		mDatabaseHelper = new DatabaseHelper(context);
	}

	/**
	 * Get a single place it.
	 * 
	 * @param placeItId
	 *            the id of the place it
	 * @return place it object
	 */
	public PlaceIt getPlaceIt(int placeItId) {
		PlaceIt placeIt;
		mDatabaseHelper = new DatabaseHelper(mContext);
		placeIt = mDatabaseHelper.getPlaceIt(placeItId);
		mDatabaseHelper.closeDB();
		return placeIt;
	}

	/**
	 * Get all the placeIts from the database
	 * 
	 * @return list of placeits
	 */
	public List<PlaceIt> getAllPlaceIts() {
		List<PlaceIt> list;
		mDatabaseHelper = new DatabaseHelper(mContext);
		list = mDatabaseHelper.getAllPlaceIts();
		mDatabaseHelper.closeDB();
		return list;
	}

	/**
	 * Retrive a list of the placeIts in the service
	 * 
	 * @param state
	 * @param category
	 * @return
	 */
	public List<PlaceIt> getAllPlaceIts(int state, String category) {
		mDatabaseHelper = new DatabaseHelper(mContext);
		List<PlaceIt> list = mDatabaseHelper.getAllPlaceIts(state, category, 0, "");
		mDatabaseHelper.closeDB();
		return list;
	}

}
