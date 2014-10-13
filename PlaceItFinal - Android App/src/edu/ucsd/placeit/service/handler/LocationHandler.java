package edu.ucsd.placeit.service.handler;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import edu.ucsd.placeit.db.DatabaseHelper;
import edu.ucsd.placeit.model.PlaceIt;
import edu.ucsd.placeit.model.PlaceItBank;
import edu.ucsd.placeit.model.PlaceItBank.PlaceItIterator;
import edu.ucsd.placeit.service.ILocationHandler;
import edu.ucsd.placeit.util.Cfg;
import edu.ucsd.placeit.util.Consts;

public class LocationHandler implements ILocationHandler {
	private Context mContext;
	private String mUsername;
	/**
	 * The list of placeIt's that entered the proximity previously.
	 */
	private List<PlaceIt> mProximityList;

	/**
	 * Constructor which creates new placeIt proximity list
	 */
	public LocationHandler(Context context) {
		mProximityList = new ArrayList<PlaceIt>();
		mContext = context;
	}

	/**
	 * Called from the location service facade.
	 * 
	 * @param location
	 * @return
	 */
	public List<PlaceIt> onLocationChanged(Location location, String userName) {
		Log.d(Consts.TAG, "Location changed from service!");
		mUsername = userName;
		// Calling helper method to return a list of proximity
		List<PlaceIt> newList = checkInProximity(location);

		// Create notifications based on the newList
		return newList;
	}

	/**
	 * Check the proximity of proximity of the location passed in
	 * 
	 * @param location
	 * @param state
	 * @return
	 */
	private List<PlaceIt> checkInProximity(Location location) {
		PlaceIt placeIt;

		// Where the distance is stored in the Location.distanceBetween method
		float[] results = new float[1];

		// Create a newList of PlaceIts
		List<PlaceIt> newList = new ArrayList<PlaceIt>();
		int count = 0;
		boolean contains;

		// Retrieving the db stuff
		DatabaseHelper db = new DatabaseHelper(mContext);
		List<PlaceIt> allPlaceIts = db.getAllPlaceIts(Consts.ACTIVE, null, 0,
				mUsername);
		db.closeDB();

		// Creating iterator
		PlaceItBank placeItBank = new PlaceItBank(allPlaceIts);
		PlaceItIterator iterator = placeItBank.iterator();
		// Check to see whether there are more placeIts to compare
		while (iterator.hasNext()) {
			placeIt = iterator.next();
			count++;

			// Getting the distance between the location and each placeit of the
			Location.distanceBetween(location.getLatitude(),
					location.getLongitude(), placeIt.getCoord().latitude,
					placeIt.getCoord().longitude, results);

			contains = false;
			// Cycle through each of the placeIts in that are already near
			if (results[0] < Cfg.PLACEIT_RADIUS) {
				// Check through the proximity list to see if it is inside
				// already
				for (int i = 0; i < mProximityList.size(); i++) {
					// Expand proximity list
					if (placeIt.equals(mProximityList.get(i))) {
						Log.d(Consts.TAG_OTHER, "Already contained " + count
								+ "id = " + placeIt.getId());
						contains = true;
						break; // to the outer loop
					}
				}
				if (!contains) {
					Log.d(Consts.TAG_OTHER,
							"Added " + count + "id = " + placeIt.getId()
									+ " , placeIt Name: " + placeIt.getTitle());
					mProximityList.add(placeIt);
					newList.add(placeIt);
				}
			} else {
				// Shrink proximity list
				for (int i = 0; i < mProximityList.size(); i++) {
					if (placeIt.equals(mProximityList.get(i))) {
						Log.d(Consts.TAG_OTHER, "Removed " + count + "id = "
								+ placeIt.getId());
						mProximityList.remove(i);
					}
				}
			}
		}
		return newList;
	}

}