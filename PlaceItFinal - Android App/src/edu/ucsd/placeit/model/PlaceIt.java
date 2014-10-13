package edu.ucsd.placeit.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.w3c.dom.UserDataHandler;

import com.google.android.gms.maps.model.LatLng;

import edu.ucsd.placeit.util.Consts;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

abstract public class PlaceIt implements Parcelable {
	protected int mId;
	protected String mTitle;
	protected String mDesc;
	protected Date mCreationDate;
	protected Date mPostDate;
	protected int mState;
	protected int mStartTime;
	protected LatLng mCoord;
	protected String mUser;

	public String getUser() {
		return mUser;
	}

	public void setUser(String user) {
		mUser = user;
	}

	@SuppressLint("SimpleDateFormat")
	private SimpleDateFormat dFormat = new SimpleDateFormat(Consts.DATE_FORMAT);

	/**
	 * @return the id
	 */
	public int getId() {
		return mId;
	}

	/**
	 * Sets the ID
	 * 
	 * @param id
	 *            the ID to set
	 */
	public void setId(int id) {
		mId = id;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return mTitle;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		mTitle = title;
	}

	/**
	 * @return the desc
	 */
	public String getDesc() {
		return mDesc;
	}

	/**
	 * @param desc
	 *            the desc to set
	 */
	public void setDesc(String desc) {
		mDesc = desc;
	}

	/**
	 * @return the startTime
	 */
	public int getStartTime() {
		return mStartTime;
	}

	/**
	 * @param startTime
	 *            the startTime to set
	 */
	public void setStartTime(int startTime) {
		this.mStartTime = startTime;
	}

	/**
	 * @return the date
	 */
	public Date getCreationDate() {
		return mCreationDate;
	}

	public Date getPostDate() {
		return mPostDate;
	}

	public void setCreationDate(Date date) {
		mCreationDate = date;
	}

	public void setPostDate(Date date) {
		mPostDate = date;
	}

	/**
	 * @return the state
	 */
	public int getState() {
		return mState;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(int state) {
		mState = state;
	}

	/**
	 * Check if two placeIts are equal based on their ID's
	 * 
	 * @param other
	 * @return
	 */
	public boolean equals(PlaceIt other) {
		return mId == other.getId();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * @return the coord
	 */
	public LatLng getCoord() {
		return mCoord;
	}

	/**
	 * @param coord
	 *            the coord to set
	 */
	public void setCoord(LatLng coord) {
		mCoord = coord;
	}

	/**
	 * Write partial parcel for parcelable.
	 * 
	 * @param dest
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	protected Parcel writeToParcelInit(Parcel dest) {
		dest.writeInt(mId);
		dest.writeString(mTitle);
		dest.writeString(mDesc);

		dest.writeString(dFormat.format(mCreationDate));
		dest.writeString(dFormat.format(mPostDate));
		dest.writeInt(mState);
		dest.writeInt(mStartTime);
		dest.writeDouble(mCoord.latitude);
		dest.writeDouble(mCoord.longitude);
		dest.writeString(mUser);
		return dest;
	}

	/**
	 * Read partially from source parcel
	 * 
	 * @param source
	 * @return
	 */
	protected Parcel readFromParcel(Parcel source) {
		mId = source.readInt();
		mTitle = source.readString();
		mDesc = source.readString();

		try {
			mCreationDate = dFormat.parse(source.readString());
			mPostDate = dFormat.parse(source.readString());
		} catch (ParseException e) {
			Log.d(Consts.TAG, "error in logging date");
			e.printStackTrace();
		}

		mState = source.readInt();
		mStartTime = source.readInt();
		mCoord = new LatLng(source.readDouble(), source.readDouble());
		mUser = source.readString();
		return source;
	}
}
