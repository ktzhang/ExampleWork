package edu.ucsd.placeit.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Intent;

public final class Consts {
	/** TAG to use for debugging */
	public static final String TAG = "PlaceIt";
	public static final String TAG_DATABASE = "DatabaseHelper";
	public static final String TAG_OTHER = "Location";
	public static final String TAG_NOTIFY = "NotificationTag";
	public static final String TAG_TEST = "PlaceItTest";
	public static final String TAG_VERSION = "TagVersion";

	// Place it State parameter values
	public static final int ACTIVE = 1;
	public static final int COMPLETE = 2;
	public static final int SLEEP = 3;

	public static final int TYPE_NORMAL = 1;
	public static final int TYPE_CATEGORICAL = 2;
	public static final int TYPE_RECURRING = 3;

	// Login Activity: constants for preferences
	public static final String USER_LOGGED_IN = "login";
	public static final String LOGIN_LOG_USER_OUT = "logout";
	
	// MainActivity: Signals sent by Login Activity and all fragments
	public static final String MAIN_ENTRANCE = "EnterMain";
	public static final int MAIN_MAP_CLICK = 1;
	public static final int MAIN_MARKER_CLICK = 2;
	public static final int MAIN_NEW_LOC_SUBMIT = 3;
	public static final int MAIN_NEW_LOC_CANCEL = 4;
	public static final int MAIN_NEW_CAT_SUCCESS = 5;
	public static final int MAIN_NEW_CAT_RETURN = 6;
	public static final int MAIN_ALERT_MARK_COMPLETE = 7;
	public static final int MAIN_ALERT_MARK_DELETE = 8;
	public static final int MAIN_ALERT_MARK_REPOST = 9;
	public static final int MAIN_RETURN_FROM_VIEW = 10;
	public static final int MAIN_REFRESH_MAP = 11;
	
	
	// Output / display string values
	public static final String ACTION_SUCCESS_STRING = "The new reminder is successfully created!";
	public static final String ACTION_CANCELED_STRING = "The new reminder is not created..";
	public static final String ACTION_PEDING_REMINDER = "The reminder will be added in the future";
	public static final String ACTION_REMINDER_COMPLETE = "The reminder has been marked as COMPLETED.";
	public static final String ACTION_REMINDER_DELETE = "The reminder has been DELETED.";
	public static final String ACTION_REMINDER_REPOST = "The reminder alert is snoozed..";
	public static final String ACTION_REMINDER_REACTIVATE = "The reminder has been Re-activated.";

	// MainActivity: Local Bundle Parameters
	public static final String MAIN_BUNDLE_LOCATION = "LocationValue";

	// GoogleMap Fragment Duty Index
	public static final int GOOGLEMAP_ADD_MARKER = 10;
	public static final int GOOGLEMAP_REMOVE_MARKER = 11;
	public static final int GOOGLEMAP_REFRESH = 12;
	
	// NewLocationPlaceIt fragment data
	public static final String DATA_PLACEIT_ID = "PlaceIt_ID";
	public static final String DATA_PLACEIT_TITLE = "PlaceIt_Title";
	public static final String DATA_PLACEIT_DESCRIPTION = "PlaceIt_Description";
	public static final String DATA_PLACEIT_CREATED_DATE = "PlaceIt_Created";
	public static final String DATA_PLACEIT_DATE = "PlaceIt_PostDate";
	public static final String DATA_PLACEIT_STATE = "PlaceIt_State";
	public static final String DATA_PLACEIT_FREQUENCY = "PlaceIt_Repost";
	public static final String DATA_PLACEIT_LOCATION = "PlaceIt_Location";
	public static final String USER_INFO = "User_LoggedIn";

	// Milliseconds per second
	private static final int MILLISECONDS_PER_SECOND = 1000;
	// Update frequency in seconds
	private static final int UPDATE_INTERVAL_IN_SECONDS = 5;
	// Update frequency in milliseconds
	public static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND
			* UPDATE_INTERVAL_IN_SECONDS;
	// The fastest update frequency, in seconds
	private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
	// A fast frequency ceiling in milliseconds
	public static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND
			* FASTEST_INTERVAL_IN_SECONDS;

	// ----- Intent stuff -----\\
	public static final String KEY_USER = "edu.ucsd.placeit.main.bundle_user";

	public static final String EXTRA_LONGITUDE = "edu.ucsd.placeit.main.extra_longitude";
	public static final String EXTRA_LATITUDE = "edu.ucsd.placeit.main.extra_latitude";
	public static final String EXTRA_PROVIDER = "edu.ucsd.placeit.main.extra_provider";
	public static final String EXTRA_IN_PROX_ID = "edu.ucsd.placeit.main.extra_in_prox_id";
	public static final String EXTRA_ACTIVITY_ONLINE = "edu.ucsd.placeit.main.extra_activity_online";

	// Intent to call service
	public static final String EXTRA_UPDATE_ID = "edu.ucsd.placeit.main.extra_update_id";
	public static final String EXTRA_UPDATE_STATE = "edu.ucsd.placeit.main.extra_update_state";
	public static final String EXTRA_UPDATE_OPTIONS = "edu.ucsd.placeit.main.extra_update_options";
	public static final String EXTRA_CHECK_VERSION = "edu.ucsd.placeit.main.extra_version";

	// Alarm manager action indicator
	public static final String ALARM_ACTION = "AlarmAction";
	public static final String ALARM_DISPLAY_MARKER = "AlarmNormalFuture";
	public static final String ALARM_CREATE_PLACEIT = "AlarmCreatePlaceIt";

	
	// ---- UpdateStates -----\\

	public static final int UPDATE_STATE_UPDATE = 1;
	public static final int UPDATE_ADD = 2;
	public static final int UPDATE_DELETE = 3;

	// ----- Create new place it Intent Code -----\\
	public static final int CREATE_NEW_MARKER = 0;
	public static final int RESULT_SUCCESS = 0;
	public static final int RESULT_ABORT = 1;
	public static final int DELAYED_INTENT_CODE = 100;
	public static final String RESULT_SET_NAME = "TaskName";
	public static final String RESULT_SET_DISPLAY = "DisplayOption";
	public static final String RESULT_SET_ID = "TaskID";
	public static final String RESULT_SET_LAT = "ReminderLatitude";
	public static final String RESULT_SET_LNG = "ReminderLongitude";
	public static final String RESULT_SET_DATE = "ReminderDate";
	public static final String INTENT_DELAYED_NAME = "edu.ucsd.placeit";

	// ----- MESSAGES -----\\
	public static final String MESSAGE_NOTIFICATION = "You are near %s. Please choose to accept or not.";
	public static final String LOGIN_SERVER_URL = "http://www.frankieliu.com/placeit/login.php";
	
	
	// ----- DATE STUFF -----\\
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:s";

	public static final int NUM_CAT = 3;
	// ----- ACTIONS -----\\
	public static final String BROADCAST_ACTION = "com.ucsd.placeit.main";
	public static final String BROADCAST_DB_UPDATE = "com.ucsd.placeit.service.versionhandler.updatedb";
	// Logcat tag
	public static final SimpleDateFormat DATE_FORMAT_SIMPLE = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());

	/**
	 * The caller references the constants using <tt>Consts.EMPTY_STRING</tt>,
	 * and so on. Thus, the caller should be prevented from constructing objects
	 * of this class, by declaring this private constructor.
	 */
	private Consts() {
		throw new AssertionError();
	}
}
