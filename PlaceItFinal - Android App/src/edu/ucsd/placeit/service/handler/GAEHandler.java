package edu.ucsd.placeit.service.handler;

import edu.ucsd.placeit.db.OnlineDatabaseHelper;
import android.content.Context;

public class GAEHandler {
	private Context mContext;
	private OnlineDatabaseHelper mOnlineDatabaseHelper;
	public GAEHandler(Context context) {
		mContext = context;
	}

	/**
	 * Push notification to update the local database
	 * 
	 * @return true: if the database is updated, false if no current updates
	 *         needed
	 */
	public boolean checkNeedUpdate() {
		
		mOnlineDatabaseHelper = new OnlineDatabaseHelper(mContext);
		
		return false;
	}
}
