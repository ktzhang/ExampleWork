package edu.ucsd.placeit.model.impl;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import edu.ucsd.placeit.model.PlaceIt;

public class RecurringPlaceIt extends PlaceIt {
	private int mFrequency;

	public RecurringPlaceIt(String user, String title, String desc, int state, LatLng coord,
			Date dateCreated, Date datePosted, int frequency) {
		mId = 0;
		mUser = user;
		mTitle = title;
		mCoord = coord;
		mDesc = desc;
		mState = state;
		mCreationDate = dateCreated;
		mPostDate = datePosted;
		mFrequency = frequency;

		// Adds a expiration date
		// Not useful for now, so assign a dummie value
//		mExpiration = 0;
	}

//	public ReccuringPlaceIt(int id, String title, String desc, int state,
//			LatLng coord, Date dateCreated, Date datePosted, int frequency,
//			int type) {
//		// TODO Auto-generated constructor stub
//	}

	public RecurringPlaceIt(int id, String user, String title, String desc, int state,
			LatLng coord, Date dateCreated, Date datePosted, int frequency) {
		mId = id;
		mUser = user;
		mTitle = title;
		mCoord = coord;
		mDesc = desc;
		mState = state;
		mCreationDate = dateCreated;
		mPostDate = datePosted;
		mFrequency = frequency;

		// Adds a expiration date
//		mExpiration = expiration;

	}

	/**
	 * New Constructor
	 * 
	 * @param source
	 */
	public RecurringPlaceIt(Parcel source) {
		source = super.readFromParcel(source);
//		mExpiration = source.readInt();
		mFrequency = source.readInt();
		mCoord = new LatLng(source.readDouble(), source.readDouble());
	}

	/**
	 * @return the expiration
	 */
//	public int getExpiration() {
//		return mExpiration;
//	}

	/**
	 * @param expiration
	 *            the expiration to set
	 */
//	public void setExpiration(int expiration) {
//		this.mExpiration = expiration;
//	}


	/**
	 * @return the frequency
	 */
	public int getFrequency() {
		return mFrequency;
	}

	/**
	 * @param frequency
	 *            the frequency to set
	 */
	public void setFrequency(int frequency) {
		mFrequency = frequency;
	}

	/**
	 * It will be required during un-marshaling data stored in a Parcel
	 * 
	 * @author prasanta
	 */
	public class MyCreator implements Parcelable.Creator<PlaceIt> {
		public PlaceIt createFromParcel(Parcel source) {
			return new RecurringPlaceIt(source);
		}

		public PlaceIt[] newArray(int size) {
			return new PlaceIt[size];
		}
	}

	/**
	 * Writes to the parcel
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest = super.writeToParcelInit(dest);
//		dest.writeInt(mExpiration);
		dest.writeInt(mFrequency);
	}

}
