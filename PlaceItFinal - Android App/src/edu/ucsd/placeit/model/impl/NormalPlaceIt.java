package edu.ucsd.placeit.model.impl;

import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import edu.ucsd.placeit.model.PlaceIt;

public class NormalPlaceIt extends PlaceIt {
	/** Stores the coordinates */

	/**
	 * Generate placeIt from parcel
	 * 
	 * @param parcel
	 */
	public NormalPlaceIt(Parcel parcel) {
		parcel = super.readFromParcel(parcel);
		mCoord = new LatLng(parcel.readDouble(), parcel.readDouble());
	}
	

	public NormalPlaceIt(String user, String title, String desc, int state,
			LatLng coord, Date dateCreated, Date datePosted) {
		mId = 0;
		mUser = user;
		mTitle = title;
		mCoord = coord;
		mDesc = desc;
		mState = state;
		mCreationDate = dateCreated;
		mPostDate = datePosted;
	}
	public NormalPlaceIt(int id, String user, String title, String desc, int state,
			LatLng coord, Date dateCreated, Date datePosted) {
		mId = id;
		mUser = user;
		mTitle = title;
		mCoord = coord;
		mDesc = desc;
		mState = state;
		mCreationDate = dateCreated;
		mPostDate = datePosted;
	}

	/**
	 * Writes to the parcel
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest = super.writeToParcelInit(dest);

	}

	/**
	 * It will be required during un-marshaling data stored in a Parcel
	 * 
	 * @author prasanta
	 */
	public class MyCreator implements Parcelable.Creator<NormalPlaceIt> {
		public NormalPlaceIt createFromParcel(Parcel source) {
			return new NormalPlaceIt(source);
		}

		public NormalPlaceIt[] newArray(int size) {
			return new NormalPlaceIt[size];
		}
	}

}
