package edu.ucsd.placeit.service.handler;

import java.util.HashMap;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;
import edu.ucsd.placeit.R;
import edu.ucsd.placeit.db.DatabaseHelper;
import edu.ucsd.placeit.fragment.GoogleMapFragment;
import edu.ucsd.placeit.fragment.NewLocationPlaceIt;
import edu.ucsd.placeit.fragment.PlaceItAlertFragment;
import edu.ucsd.placeit.fragment.PlaceItListFragment;
import edu.ucsd.placeit.model.GoogleMapData;
import edu.ucsd.placeit.util.Consts;

/* Performs Various trivial calculation tasks
 * A Helper class, serving exclusively MainActivity
 * 
 */
public class FrontEndManager {
	private FragmentActivity activity;
	private AlarmManager am;
	private PendingIntent pendingIntent;
	private FutureEventHandler scheduler;
	private String user;


	public FrontEndManager(FragmentActivity activity, String user){
		this.activity = activity;
		this.user = user;
	}
	
	
	public void onMapClick(GoogleMapData data, String user){
		Bundle bundle = new Bundle();
		bundle.putParcelable(Consts.MAIN_BUNDLE_LOCATION, data.getLocation());
		bundle.putString(Consts.USER_LOGGED_IN, user);
		displayFragment(new NewLocationPlaceIt(), bundle);
	}
	
	
	public void onMarkerClick(GoogleMapData data){
		Bundle bundle = packPopupAlertBundle(data);
		displayFragment(new PlaceItAlertFragment(), bundle);
	}
	
	public void onNewLocationPlaceIt(GoogleMapData data, String user){
		
		Log.d("Frankie", "Packing from FrontEnd id=" + data.getData().get(Consts.DATA_PLACEIT_ID));
		
		// User Interface Part
		String sState = data.getData().get(Consts.DATA_PLACEIT_STATE);
		int state = Integer.valueOf(sState);
		String recurring = data.getData().get(Consts.DATA_PLACEIT_FREQUENCY);
		data.getData().put(Consts.USER_LOGGED_IN, user);
		
		// Normal Place It && display now
		if(state == Consts.ACTIVE && recurring.equals("false")){
			Toast.makeText(activity, Consts.ACTION_SUCCESS_STRING, Toast.LENGTH_SHORT).show();
			// Tell GoogleMapFragment to display a new marker
			GoogleMapFragment mapFrag = (GoogleMapFragment) activity.getSupportFragmentManager().findFragmentById(R.id.map);
			mapFrag.performDuty(Consts.GOOGLEMAP_ADD_MARKER, data);
		}
		// Recurring placeit && display now
		else if(state == Consts.ACTIVE && !recurring.equals("false")){
			scheduler = new FutureEventHandler(activity);
			scheduler.assignRepeatingTask(data,recurring);
			
			// Create the placeit for this instance
			GoogleMapFragment mapFrag = (GoogleMapFragment) activity.getSupportFragmentManager().findFragmentById(R.id.map);
			mapFrag.performDuty(Consts.GOOGLEMAP_ADD_MARKER, data);
		}
		// Normal Place It && display in the future
		else if (state == Consts.SLEEP && recurring.equals("false")){
			scheduler = new FutureEventHandler(activity);
			scheduler.assignFutureTask(data);
			
		}
		// Recurring placeit && display in the future
		else if (state == Consts.SLEEP && !recurring.equals("false")){
			scheduler = new FutureEventHandler(activity);
			scheduler.assignRepeatingTask(data, recurring);
		}
		// Error branch
		else{
			Log.d(Consts.TAG_NOTIFY, "Entered Error Branch at FrontEndManager in method: OnNewLocationPlaceIt()");
		}
		removeCurrentFragment();
	}
	
	public void onFragmentResult(String message){
		removeCurrentFragment();
		Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
	}
	

	public void onAlertMarkerComplete(GoogleMapData data){
		// Display message on screen
		int placeItId = Integer.valueOf((data.getData().get(Consts.DATA_PLACEIT_ID)));
		Toast.makeText(activity, Consts.ACTION_REMINDER_COMPLETE, Toast.LENGTH_SHORT).show();
		// Call Database helper to change the state of reminder
		DatabaseHelper database = new DatabaseHelper(activity);
		database.changePlaceItState(placeItId, Consts.COMPLETE);
		database.closeDB();
		// Remove Marker from UI
		removeMarker(data);
		// Remove alert fragment
		removeCurrentFragment();
	}
	
	public void onAlertMarkerDelete(GoogleMapData data){
		// Display message on screen
		int placeItId= Integer.valueOf((data.getData().get(Consts.DATA_PLACEIT_ID)));
		Toast.makeText(activity, Consts.ACTION_REMINDER_DELETE, Toast.LENGTH_SHORT).show();
		// Call Database helper to delete the reminder
		DatabaseHelper database = new DatabaseHelper(activity);
		database.deletePlaceIt(placeItId);
		database.closeDB();
		// Remove Marker from UI
		removeMarker(data);
		// Remove alert fragment
		removeCurrentFragment();
	}
	
	public void onAlertMarkerRepost(GoogleMapData data){
		// Display message on screen
		int placeItId = Integer.valueOf((data.getData().get(Consts.DATA_PLACEIT_ID)));
		Toast.makeText(activity, "Reminder has been re-activated.", Toast.LENGTH_SHORT).show();
		// Call Database helper to change the state of reminder
		DatabaseHelper database = new DatabaseHelper(activity);
		database.changePlaceItState(placeItId, Consts.ACTIVE);
		database.closeDB();
		// Remove Marker from UI
		removeMarker(data);
		// Remove alert fragment
		removeCurrentFragment();		
	}
	
	public void updateGoogleMap(){
		// Let GoogleMapFragment refresh itself
		GoogleMapFragment mapFrag = (GoogleMapFragment) activity.getSupportFragmentManager().findFragmentById(R.id.map);
		HashMap<String,String> info = new HashMap<String,String>();
		info.put(Consts.USER_LOGGED_IN, user);
		GoogleMapData data = new GoogleMapData(null,info);
		mapFrag.performDuty(Consts.GOOGLEMAP_REFRESH, data);
		Log.d(Consts.TAG, "GoogleMap is refreshed");
	}
	
	public void updateListView(){
		PlaceItListFragment listFrag = (PlaceItListFragment) activity.getSupportFragmentManager().findFragmentById(R.id.fragment_container);
		listFrag.refreshAllLists();
		Log.d(Consts.TAG, "List View is refreshed");
	}
	
	
// Private Helper function (reuses many times) --------------------------------
	public Bundle packPopupAlertBundle(GoogleMapData data){
		Bundle bundle = new Bundle();
		HashMap<String,String> info = data.getData();
		bundle.putString(Consts.DATA_PLACEIT_TITLE, info.get(Consts.DATA_PLACEIT_TITLE));
		bundle.putString(Consts.DATA_PLACEIT_ID, info.get(Consts.DATA_PLACEIT_ID));
	//	Log.d("Frankie", "Packing from sub function, id="+info.get(Consts.DATA_PLACEIT_ID));
		return bundle;
	}
	
    public void displayFragment(Fragment fragment, Bundle bundle){
    	// Attach bundle to fragment
		if(bundle != null) fragment.setArguments(bundle);
		// Replace fragment in the container with new fragment
    	FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.fragment_container, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
    }
    
    // Always return true
    public void removeCurrentFragment(){
		activity.getSupportFragmentManager().popBackStack();
    }
    
    private void removeMarker(GoogleMapData data){
		GoogleMapFragment mapFrag = (GoogleMapFragment) activity.getSupportFragmentManager().findFragmentById(R.id.map);
		mapFrag.performDuty(Consts.GOOGLEMAP_REMOVE_MARKER, data);
	}
}
