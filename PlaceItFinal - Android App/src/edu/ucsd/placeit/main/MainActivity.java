package edu.ucsd.placeit.main;

import java.util.Date;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.model.LatLng;

import edu.ucsd.placeit.R;
import edu.ucsd.placeit.db.DatabaseHelper;
import edu.ucsd.placeit.fragment.NewCategoryPlaceIt;
import edu.ucsd.placeit.fragment.PlaceItListFragment;
import edu.ucsd.placeit.model.GoogleMapData;
import edu.ucsd.placeit.model.PlaceIt;
import edu.ucsd.placeit.model.impl.NormalPlaceIt;
import edu.ucsd.placeit.service.LocationService;
import edu.ucsd.placeit.service.OnFragmentEventListener;
import edu.ucsd.placeit.service.handler.FrontEndManager;
import edu.ucsd.placeit.util.Consts;

/* Logic Flow: 
 * 1. Always transferred from Login Activity
 * 2. Automatically embed Google Map on Start up
 * 3. Start LocationService
 * 4. a) Listen for LocationService BroadCast
 *    b) Listen to fragment activity message
 * MainActivity is responsible for fragment interaction
 * 
 */


/* Improvements needed:
 * Fix bug concerning remove placeit markers
 * Fix bug on handling locationservice broadcast (perhaps same as above)
 * Add Future task and recurring task manager
 * 
 * Other improvments in:
 * GoogleMapFragment.java
 * PlaceItListFragment.java
 * 
 */
public class MainActivity extends FragmentActivity implements
		OnFragmentEventListener {
	// Local variable
	boolean listenToMapClick;
	private Button listViewBtn;
	private Button newCatBtn;
	private Button logoutBtn;
	private FrontEndManager mainHelper;
	private BroadcastReceiver alarmReceiver;
	private BroadcastReceiver locationReceiver;
	private BroadcastReceiver updateReceiver;
	private Intent intent;
	public static String user;
	private boolean listViewOnFocus;

	
	
// Main Logic: Controllers for Fragment Interactivity ---------------
	@SuppressLint("NewApi")
	@Override
	public void onFragmentEvent(int action, GoogleMapData data) {
		switch (action) {
		case Consts.MAIN_MAP_CLICK:
			// Bring up the NewLocationPlaceIt fragment
			if (listenToMapClick) {
				mainHelper.onMapClick(data, user);
				listenToMapClick = false;
			}
			break;
		case Consts.MAIN_MARKER_CLICK:
			// Bring up the PlaceItAlert fragment
			if (listenToMapClick || listViewOnFocus) {
				Log.d("Frankie", "received marker click in main");
				mainHelper.onMarkerClick(data);
				//listenToMapClick = false;
			}
			break;
		case Consts.MAIN_NEW_LOC_SUBMIT:
			mainHelper.onNewLocationPlaceIt(data,user);
			break;
		case Consts.MAIN_NEW_LOC_CANCEL:
		case Consts.MAIN_NEW_CAT_RETURN:
			mainHelper.onFragmentResult(Consts.ACTION_CANCELED_STRING);
			break;
		case Consts.MAIN_NEW_CAT_SUCCESS:
			mainHelper.onFragmentResult(Consts.ACTION_SUCCESS_STRING);
			break;
		case Consts.MAIN_ALERT_MARK_COMPLETE:
			mainHelper.onAlertMarkerComplete(data);
			break;
		case Consts.MAIN_ALERT_MARK_DELETE:
			mainHelper.onAlertMarkerDelete(data);
			break;
		case Consts.MAIN_ALERT_MARK_REPOST:
			HashMap<String,String> info = data.getData();
			int state = Integer.valueOf(info.get(Consts.DATA_PLACEIT_STATE));
			if(state == Consts.ACTIVE){
				mainHelper.onFragmentResult(Consts.ACTION_REMINDER_REPOST);
			}
			else{
				mainHelper.onAlertMarkerRepost(data);
				mainHelper.onFragmentResult(Consts.ACTION_REMINDER_REACTIVATE);
				mainHelper.updateGoogleMap();
			}
			break;
		case Consts.MAIN_RETURN_FROM_VIEW:
			mainHelper.removeCurrentFragment();
			listViewOnFocus = false;
		default:
			break;
		}
		if (action != Consts.MAIN_MAP_CLICK
				&& action != Consts.MAIN_MARKER_CLICK) {
			listenToMapClick = true;
		}
	}

	// Monitoring Back key press
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mainHelper.removeCurrentFragment();
			listenToMapClick = true;
			if(listViewOnFocus) listViewOnFocus = false;
			return true;
		}
		return false;
	}

	
	
// Main Activity Life Cycle Management ------------------------------
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		user = intent.getStringExtra(Consts.MAIN_ENTRANCE);

		Log.d("Frankie","lol, main, user is: "+user);
		setContentView(R.layout.activity_main);
		
		initialization();
	}

	
	
// Private Helper Methods That Does Trivial Tasks -------------------

	@SuppressLint("NewApi")
	private void initialization() {
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
					.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}

		// We'd like to listen to map click events
		listenToMapClick = true;
		listViewOnFocus = false;
		
		// Initialize main helper object
		mainHelper = new FrontEndManager(this, user);

		// Initialize ListView Button
		listViewBtn = (Button) findViewById(R.id.listViewButton);
		listViewBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listenToMapClick) {
					Bundle bundle = new Bundle();
					bundle.putString(Consts.USER_LOGGED_IN, user);
					mainHelper.displayFragment(new PlaceItListFragment(), bundle);
					listViewOnFocus = true;
					listenToMapClick = false;
				}
			}
		});
		newCatBtn = (Button) findViewById(R.id.newCatPiButton);
		newCatBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listenToMapClick) {
					Bundle bundle = new Bundle();
					bundle.putString(Consts.USER_LOGGED_IN, user);
					mainHelper.displayFragment(new NewCategoryPlaceIt(), bundle);
					listenToMapClick = false;
				}
			}
		});
		logoutBtn = (Button) findViewById(R.id.logoutButton);
		logoutBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listenToMapClick){
					logoutProcess();
					Intent intent = new Intent(getBaseContext(), Login.class);
					intent.putExtra(Consts.LOGIN_LOG_USER_OUT, user);
					startActivity(intent);
				}
				
			}
		});

		alarmReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
            	Log.d("Frankie", "Frankie,Receied pending intent");
                // Retrieve necessary data from intent  
            	Bundle bundle = intent.getExtras();
            	String action = bundle.getString(Consts.ALARM_ACTION);
            	String id = bundle.getString(Consts.DATA_PLACEIT_ID);
            	String title = bundle.getString(Consts.DATA_PLACEIT_TITLE);

            	if(action.equals(Consts.ALARM_DISPLAY_MARKER)){
            		Log.d("Frankie", "Frankie, displaying marker");
	            	HashMap<String,String> info = new HashMap<String,String>();
	            	info.put(Consts.DATA_PLACEIT_ID, id);
	            	info.put(Consts.DATA_PLACEIT_TITLE, title);
	            	GoogleMapData data = new GoogleMapData(null, info);
	            	mainHelper.onMarkerClick(data);
					listenToMapClick = false;
            	}
            	// Create a new marker then send to google map
            	else if (action.equals(Consts.ALARM_CREATE_PLACEIT)){
            		Log.d("Frankie", "Frankie, creating palceit");
            		String description = bundle.getString(Consts.DATA_PLACEIT_DESCRIPTION);
            		LatLng location = bundle.getParcelable(Consts.DATA_PLACEIT_LOCATION);
            		PlaceIt pi = new NormalPlaceIt(user, title, description, Consts.ACTIVE, location, new Date(), new Date());
        			HashMap<String,String> info = new HashMap<String,String>();
        			info.put(Consts.DATA_PLACEIT_ID, String.valueOf(pi.getId()));
                	info.put(Consts.DATA_PLACEIT_TITLE, pi.getTitle());
                	GoogleMapData data = new GoogleMapData(location, info);
            		mainHelper.onNewLocationPlaceIt(data, user);
            	}
            }
        };  
        registerReceiver(alarmReceiver, new IntentFilter(Consts.INTENT_DELAYED_NAME));
        
        
		// Setting up receiver for location service
		locationReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				DatabaseHelper database = new DatabaseHelper(getBaseContext());
				Bundle extraBundle = intent.getExtras();
				int[] ids = extraBundle.getIntArray(Consts.EXTRA_IN_PROX_ID);
				for (int i = 0; i < ids.length; i++) {
					// Get the placeit object and display its alert
					PlaceIt pi = database.getPlaceIt(ids[i]);
					HashMap<String, String> info = new HashMap<String, String>();
					info.put(Consts.DATA_PLACEIT_ID, String.valueOf(pi.getId()));
					info.put(Consts.DATA_PLACEIT_TITLE, pi.getTitle());
					GoogleMapData data = new GoogleMapData(null, info);
					mainHelper.onMarkerClick(data);
					listenToMapClick = false;
				}
				database.closeDB();
			}
		};
		
		updateReceiver = new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {
				// Refresh GoogleMap
				mainHelper.updateGoogleMap();
				// Refresh ListView if applicable
				if(listViewOnFocus){
					mainHelper.updateListView();
				}
			}
		};
		
		Log.d(Consts.TAG_TEST, "Reciever is updated");
        registerReceiver(updateReceiver, new IntentFilter(Consts.BROADCAST_DB_UPDATE));
	}

//	private boolean isLocationServiceRunning() {
//		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//		for (RunningServiceInfo service : manager
//				.getRunningServices(Integer.MAX_VALUE)) {
//			if (LocationService.class.getName().equals(
//					service.service.getClassName())) {
//				return true;
//			}
//		}
//		return false;
//	}

	// ----- Stuff to start the service -----\\
	// /**
	// * BroadcastReciever variable which registers a BroadcastReciever every
	// time
	// * a new LocationService broadcast is sent.
	// */
	// private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
	// /**
	// * Receiving the broadcasts from LocationServices
	// */
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// Log.d(Consts.TAG, "RECIVED TE BROADCAST REQUEST!");
	// Bundle extrasBundle = intent.getExtras();
	//
	// // Retrieves the broadcast intArray from the reciever
	// int[] idArray = extrasBundle.getIntArray(Consts.EXTRA_IN_PROX_ID);
	// if (idArray.length > 0) {
	// // Displays the popups
	// display_popups(idArray, 0);
	// }
	// }
	// };
	//

	public String getUser(){
		return user;
	}
	
	public void logoutProcess(){
		 // unregisterReceiver(broadcastReceiver);
		 unregisterReceiver(locationReceiver);
		 unregisterReceiver(updateReceiver);
	}
	
	//
	@Override
	public void onPause() {
		super.onPause();
		 Log.d("f","lol, onPause called");
		// Tells that the activity is not in the foreground
		intent = new Intent(MainActivity.this, LocationService.class);
		intent.putExtra(Consts.EXTRA_ACTIVITY_ONLINE, false);
		startService(intent);
	}

	@Override
	public void onResume() {
		super.onResume();

		Log.d(Consts.TAG_TEST, "MainActivity: should start the intent");
		// Tells that the main activity is in the foreground
		intent = new Intent(MainActivity.this, LocationService.class);
		intent.putExtra(Consts.USER_LOGGED_IN, user);
		intent.putExtra(Consts.EXTRA_ACTIVITY_ONLINE, true);
		startService(intent);
		// Call to register receiver
		registerReceiver(locationReceiver, new IntentFilter(
				Consts.BROADCAST_ACTION));
	}
	//
	 @Override
	 public void onDestroy() {
		 Log.d("f","lol, onStop called");
		 super.onDestroy();
		 logoutProcess();
		 stopService(new Intent(MainActivity.this, LocationService.class));
	 }
}
