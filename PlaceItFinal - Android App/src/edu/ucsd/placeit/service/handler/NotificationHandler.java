package edu.ucsd.placeit.service.handler;

import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import edu.ucsd.placeit.main.MainActivity;
import edu.ucsd.placeit.model.PlaceIt;
import edu.ucsd.placeit.util.Cfg;
import edu.ucsd.placeit.util.Consts;

public class NotificationHandler {
	private boolean mActivityEnabled;
	private Context mContext;

	public NotificationHandler(Context context) {
		mActivityEnabled = false;
		mContext = context;
	}

	/**
	 * Chooses whether to enable or disable activity.
	 * 
	 * @param intent
	 */
	public void toggleEnableActivity(Intent intent) {
		mActivityEnabled = intent.getExtras().getBoolean(
				Consts.EXTRA_ACTIVITY_ONLINE);
		Log.d(Consts.TAG_NOTIFY, "Activity is on: " + mActivityEnabled);
	}

	/**
	 * Create the notifications based on the newLists
	 */
	public void createNotifications(List<PlaceIt> newList) {
		//Check if allow
		if (!Cfg.NOTIFY_ONLY_BACKGROUND) {
			if (mActivityEnabled) {
				return;
			}
		}
		
		// Check that newList has something to return
		if (newList.size() > 0) {
			// Convert to a list of IDs
			int size = newList.size();
			int[] idArray = new int[size];
			for (int i = 0; i < size; i++) {
				idArray[i] = newList.get(i).getId();
				createNotification(newList.get(i));
				Log.d(Consts.TAG_OTHER, "USER MOVED INTO RADIUS OF "
						+ newList.get(i).getTitle());
			}

			// Passing the intent
			Intent intent = new Intent(Consts.BROADCAST_ACTION);
//			intent.addFlags(Intent.FLAG_FROM_BACKGROUND);
			intent.putExtra(Consts.EXTRA_IN_PROX_ID, idArray);
			mContext.sendBroadcast(intent);

		}

	}

	/**
	 * Creates notification based on proximity. Will make sure the activity is
	 * not running, so only will display when it is in the background.
	 * 
	 * @param placeIt
	 */
	@SuppressWarnings("deprecation")
	private void createNotification(PlaceIt placeIt) {
		Log.d(Consts.TAG_NOTIFY, "create new notification");
		NotificationManager notificationManager;
		String title = placeIt.getTitle();
		String subject = placeIt.getDesc();
		String body = String.format(Consts.MESSAGE_NOTIFICATION,
				placeIt.getTitle());

		notificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notify = new Notification(
				android.R.drawable.stat_notify_more, title,
				System.currentTimeMillis());
		notify.flags |= Notification.FLAG_AUTO_CANCEL;

		// PendingIntent pending = PendingIntent.getActivity(
		// getApplicationContext(), 0, new Intent(), 0);
		/* Creates an explicit intent for an Activity in your app */

		Intent resultIntent = new Intent(mContext, MainActivity.class);
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);

		TaskStackBuilder stackBuilder = TaskStackBuilder.create(mContext);

		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		notify.setLatestEventInfo(mContext, subject, body, resultPendingIntent);
		notificationManager.notify(placeIt.getId(), notify);
	}
}
