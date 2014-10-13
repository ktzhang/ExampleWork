package edu.ucsd.placeit.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;

import edu.ucsd.placeit.util.Consts;

/**
 * Storage of PlaceIts. Will have methods such as determining if any place it is
 * within its radius, etc.
 * 
 * Also contains the PlaceItBankIterator class which will iterate through each
 * of the currently active placeits.
 * 
 * @author Kevin
 * 
 */
public class PlaceItBank implements PlaceItIterable {

	/** Stores all the placeIts */
	private List<PlaceIt> mPlaceItBank;

	public PlaceItBank() {
		//Initialize new placeItBank from DB
		mPlaceItBank = new ArrayList<PlaceIt>();

	}

	public PlaceItBank(List<PlaceIt> placeItBank) {
		mPlaceItBank = placeItBank;
	}

	/**
	 * Adds a placeIt to the bank. Will cause listeners to receive new updates.
	 * 
	 * @param placeIt
	 *            the PlaceIt that needs to be added
	 */
	public void addPlaceIt(PlaceIt placeIt) {
		mPlaceItBank.add(placeIt);
	}

	@Override
	public PlaceItIterator iterator() {
		return new PlaceItIterator(Consts.ACTIVE);
	}

	@Override
	public PlaceItIterator iterator(int state) {
		return new PlaceItIterator(state);
	}

	/**
	 * Iterator class for PlaceIts that will only return active placeIts if it
	 * is initialized.
	 * 
	 * @author Kevin
	 * 
	 */
	public class PlaceItIterator implements Iterator<PlaceIt> {
		private int size;
		private int curr;
		private int state;

		public PlaceItIterator() {
			size = mPlaceItBank.size();
			curr = -1;
		}

		/**
		 * The state of the iterator
		 * 
		 * @param state
		 */
		public PlaceItIterator(int state) {
			this.state = state;
			size = mPlaceItBank.size();
			curr = -1;
		}

		@Override
		public boolean hasNext() {
			while (curr + 1 < size) {
				if (mPlaceItBank.get(curr + 1).getState() == state) {
					return true;
				}
			}
			return false;
		}

		/**
		 * Will throw an Index
		 * 
		 * @return the next active placeIt
		 * @throws IndexOutOfBoundsException
		 *             if the index is out of range (index < 0 || index >=
		 *             size())
		 */
		@Override
		public PlaceIt next() {
			curr++;
			return mPlaceItBank.get(curr);
		}

		@Override
		public void remove() {
			mPlaceItBank.remove(curr);
		}
	}

	/**
	 * Method that returns an array of placeIt objects in the bank that are
	 * within the specified radius
	 * 
	 * @param location
	 *            the location of the thing that needs to be compared.
	 * @param radius
	 *            the radius (in meters) of the PlaceIt
	 * @return an array of PlaceIts within the location.
	 * 
	 */
	public PlaceIt[] insideRadius(LatLng location, double radius) {
		// TODO
		return null;
	}

	/**
	 * Retrieve the placeIt based on a specified UUID that is passed in. Cycles
	 * through the PlaceIt bank until it finds the correct one.
	 * 
	 * @param id
	 *            the UUID of the Place IT
	 * @return the PlaceIt with the corresponding ID. NULL if PlaceIt is not
	 *         found.
	 */
	public PlaceIt getPlaceIt(int id) {
		for (PlaceIt placeIt : mPlaceItBank) {
			if (placeIt.getId() == id) {
				return placeIt;
			}
		}
		return null;
	}

	/**
	 * Changes the state of a PlaceIt based on the ID
	 * 
	 * @param id
	 * @param state
	 * @return
	 */
	public boolean changePlaceItState(int placeItId, int updateState) {
		try {
			mPlaceItBank.get(placeItId).setState(updateState);
			return true;
		} catch (IndexOutOfBoundsException e) {
			return false;
		}
	}

	/**
	 * Updating the place it bank
	 * 
	 * @param placeItId
	 * @param updateState
	 * @param option
	 */
//	public void updatePlaceItBank(int placeItId, int updateState, int option) {
//		switch (updateState) {
//		case Consts.UPDATE_STATE_UPDATE:
//			mPlaceItBank.get(placeItId).setState(option);
//			break;
//		case Consts.UPDATE_ADD:
////			IPlaceIt placeIt = mCic.getPlaceIt(placeItId);
//			mPlaceItBank.add(placeIt);
//			break;
//		case Consts.UPDATE_DELETE:
//			try {
//				mPlaceItBank.remove(placeItId);
//			} catch (IndexOutOfBoundsException e) {
//				Log.e(Consts.TAG, "CANNOT DELETE PlaceIt #" + placeItId
//						+ " . Index out of bounds ");
//			}
//		}
//	}

	/**
	 * Returns size of the PlaceItBank
	 * 
	 * @return
	 */
	public int size() {
		return mPlaceItBank.size();
	}
}
