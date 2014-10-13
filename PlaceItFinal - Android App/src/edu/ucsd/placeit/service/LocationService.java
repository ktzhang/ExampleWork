package edu.ucsd.placeit.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import edu.ucsd.placeit.model.PlaceIt;
import edu.ucsd.placeit.service.handler.CategoryHandler;
import edu.ucsd.placeit.service.handler.LocationHandler;
import edu.ucsd.placeit.service.handler.NotificationHandler;
import edu.ucsd.placeit.service.handler.PlaceItHandler;
import edu.ucsd.placeit.service.handler.VersionHandler;
import edu.ucsd.placeit.util.Cfg;
import edu.ucsd.placeit.util.Consts;

//import android.location.LocationListener;

public class LocationService extends Service implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {
	// Binder to give to main activity
	// private final IBinder mBinder = new LocalBinder();

	// flag for GPS status
	boolean isGPSEnabled = false;

	// flag for network status
	boolean isNetworkEnabled = false;
	boolean canGetLocation = false;

	Location location; // location
	double latitude; // latitude
	double longitude; // longitude

	// Declaring a Location Manager
	protected LocationManager mLocationManager;
	private LocationClient mLocationClient;
	private LocationRequest mLocationRequest;
	Intent intent;
	int counter = 0;

	// ------- Private variables to be used in the logic ------\\
	/**
	 * Place it bank to store all the current placeIts
	 */

	// ----- Handler List -----\\
	private ILocationHandler mLocationHandler;
	private IPlaceItHandler mPlaceItHandler;
	private ICategoryHandler mCategoryHandler;
	private NotificationHandler mNotificationHandler;
	private VersionHandler mVersionHandler;

	public String userName;

	@SuppressLint("NewApi")
	@Override
	public void onCreate() {
		super.onCreate();
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		// Initialize handlers
		Context applicationContext = getApplicationContext();
		mLocationHandler = new LocationHandler(applicationContext);
		mPlaceItHandler = new PlaceItHandler(applicationContext);
		mNotificationHandler = new NotificationHandler(applicationContext);
		mCategoryHandler = new CategoryHandler(applicationContext);
		mVersionHandler = new VersionHandler(applicationContext);
		// Initializes new placeIt bank
		// mPlaceItBank = new PlaceItBank(getApplicationContext());
		Log.d(Consts.TAG_TEST, "creating");
		int resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (resp == ConnectionResult.SUCCESS) {
			Log.d(Consts.TAG_TEST, "location client created");
			mLocationClient = new LocationClient(this, this, this);
			mLocationClient.connect();
		} else {
			Toast.makeText(this, "Google Play Service Error " + resp,
					Toast.LENGTH_LONG).show();
		}
		// Start the version checking
		startDbChecker();
		// Start requesting location updates
		intent = new Intent(Consts.BROADCAST_ACTION);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.d(Consts.TAG_TEST, "Location service start");
//		if(intent.getExtras() == null) return;
		// Setting the activity active state
		if (intent.hasExtra(Consts.EXTRA_ACTIVITY_ONLINE)) {
			userName = intent.getStringExtra(Consts.USER_LOGGED_IN);
			if (mNotificationHandler == null)
				mNotificationHandler = new NotificationHandler(
						getApplicationContext());
			mNotificationHandler.toggleEnableActivity(intent);
		}

		// Checking the version
		if (intent.hasExtra(Consts.EXTRA_CHECK_VERSION)) {
			checkVersion();
		}

		if (mLocationClient != null && mLocationClient.isConnected()) {
			Log.d(Consts.TAG_TEST, "Enable location update from create");

			mLocationRequest = LocationRequest.create();
			mLocationRequest
					.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
			// Set the update interval to 5 seconds
			mLocationRequest.setInterval(Consts.UPDATE_INTERVAL);
			// Set the fastest update interval to 1 second
			mLocationRequest.setFastestInterval(Consts.FASTEST_INTERVAL);
			mLocationClient.requestLocationUpdates(mLocationRequest, this);
		}

	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.d(Consts.TAG, "Location service connected");
		if (mLocationClient != null && mLocationClient.isConnected()) {
			mLocationRequest = LocationRequest.create();
			mLocationRequest
					.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

			// Set the update interval to 5 seconds
			mLocationRequest.setInterval(Consts.UPDATE_INTERVAL);
			// Set the fastest update interval to 1 second
			mLocationRequest.setFastestInterval(Consts.FASTEST_INTERVAL);
			mLocationClient.requestLocationUpdates(mLocationRequest, this);
		}
	}

	/**
	 * Define the callback method that receives location updates Will call
	 * categoryHandler test
	 */
	@Override
	public void onLocationChanged(Location location) {
		Log.d(Consts.TAG_TEST, "Location changed from service!");
		List<PlaceIt> newList = new ArrayList<PlaceIt>();
		// // Retrieve categories
		List<PlaceIt> categoryPlaceIts = mCategoryHandler.onLocationChanged(
				location, userName);
		newList.addAll(categoryPlaceIts);

		//
		// // Create notifications
		List<PlaceIt> normalPlaceIts = mLocationHandler.onLocationChanged(
				location, userName);
		newList.addAll(normalPlaceIts);
		//
		// // Create the notifications
		mNotificationHandler.createNotifications(newList);

	}

	// @Override
	// public void onConnected(Bundle connectionHint) {
	// Log.d(Consts.TAG, "Location service connected");
	// // if (mLocationClient != null && mLocationClient.isConnected()) {
	// //
	// // Log.d(Consts.TAG_TEST, "Enable location update from connected");
	// //
	// // mLocationRequest = LocationRequest.create();
	// // mLocationRequest
	// // .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	// //
	// // // Set the update interval to 5 seconds
	// // mLocationRequest.setInterval(Consts.UPDATE_INTERVAL);
	// // // Set the fastest update interval to 1 second
	// // mLocationRequest.setFastestInterval(Consts.FASTEST_INTERVAL);
	// // mLocationClient.requestLocationUpdates(mLocationRequest, this);
	// // }
	// }

	/**
	 * Handle version checking
	 */
	private void checkVersion() {
		Log.d(Consts.TAG_VERSION, "Check Version from SERVICE!");
		VersionHandler vh = new VersionHandler(getApplicationContext());
		vh.handleConflicts();
	}

	private AlarmManager alarmMgr;
	private PendingIntent alarmIntent;

	private void startDbChecker() {
		Intent intent = new Intent(this, LocationService.class);
		intent.putExtra(Consts.EXTRA_CHECK_VERSION, true);
		alarmIntent = PendingIntent.getService(getApplicationContext(), 12345,
				intent, PendingIntent.FLAG_CANCEL_CURRENT);

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		calendar.add(Calendar.SECOND, 10);
		alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarmMgr.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
				1000 * Cfg.SYNC_DB_INTERVAL, alarmIntent);
	}

	@Override
	public void onDestroy() {
		Log.d(Consts.TAG, "Location service destroyed");
		Log.d(Consts.TAG_TEST, "Location service destroyed");

		alarmMgr.cancel(alarmIntent);

		if (mLocationClient != null && mLocationClient.isConnected()) {
			Log.d(Consts.TAG_TEST, "Removing location updates");
			mLocationClient.removeLocationUpdates(this);
			mLocationClient.disconnect();
		}
	}

	/**
	 * Function to show settings alert dialog if GPS is enabled or not
	 **/
	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				getApplicationContext());

		// Setting Dialog Title
		alertDialog.setTitle("GPS is settings");

		// Setting Dialog Message
		alertDialog
				.setMessage("GPS is not enabled. Do you want to go to settings menu?");

		// On pressing Settings button
		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						getApplicationContext().startActivity(intent);
					}
				});

		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		// Showing Alert Message
		alertDialog.show();
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	// /**
	// * Class used for the client Binder. Because we know this service always
	// * runs in the same process as its clients, we don't need to deal with
	// IPC.
	// */
	// public class LocalBinder extends Binder {
	// public LocationService getService() {
	// // Return this instance of LocationService so clients can call
	// // public
	// // methods
	// return LocationService.this;
	// }
	// }
	//
	// @Override
	// public IBinder onBind(Intent intent) {
	// return mBinder;
	// }
	//
	// public String returnTrue() {
	// return Consts.TAG_TEST;
	// }

}