package edu.ucsd.placeit.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import edu.ucsd.placeit.db.DatabaseHelper;
import edu.ucsd.placeit.main.MainActivity;
import edu.ucsd.placeit.model.GoogleMapData;
import edu.ucsd.placeit.model.PlaceIt;
import edu.ucsd.placeit.service.OnFragmentEventListener;
import edu.ucsd.placeit.util.Consts;



/* Improvements needed:
 * Need to find a way to hide placeit id and put placeit
 *   description in the snippet.
 */

public class GoogleMapFragment extends SupportMapFragment implements
					OnMapClickListener, OnInfoWindowClickListener { 
	
	// Class Variable Declaration
	private View mapView;
	private GoogleMap mMap;
	private ArrayList<Marker> allMarkers;
	private OnFragmentEventListener mListener;   
	private DatabaseHelper database;
	private String username;
	
// Fragment Interactivity with MainActivity Section -------
	// Called by Main Activity directly
	public void performDuty(int action, GoogleMapData data){
		username = data.getData().get(Consts.USER_LOGGED_IN);
		switch (action){
		case Consts.GOOGLEMAP_ADD_MARKER:
			// Add a new Marker to map with given information
			HashMap<String,String> info = data.getData();
			String title = info.get(Consts.DATA_PLACEIT_TITLE);
			String id = info.get(Consts.DATA_PLACEIT_ID);
			MarkerOptions markerOpt = new MarkerOptions()
				.position(data.getLocation())
				.title(title)
				.snippet(id);
			Marker newMarker = mMap.addMarker(markerOpt);
			allMarkers.add(newMarker);
			break;
		case Consts.GOOGLEMAP_REMOVE_MARKER:
			// Remove a PlaceIt's marker from Map
			String sID = data.getData().get(Consts.DATA_PLACEIT_ID);
			for(int i = 0; i < allMarkers.size(); i++){
				Marker mMarker = allMarkers.get(i);
				if(sID.equals(mMarker.getSnippet())){
					mMarker.remove();
					break;
				}
			}
			break;
		case Consts.GOOGLEMAP_REFRESH:
			for(int i = 0; i < allMarkers.size(); i++){
				Marker mMarker = allMarkers.get(i);
				mMarker.remove();
			}
			allMarkers.clear();
			loadAllMarkers();
			break;
		}
	}
	
	
	@Override
	public void onMapClick(LatLng location) {
		// Tell main where the click happened and nothing else
		GoogleMapData data = new GoogleMapData(location, null);
		mListener.onFragmentEvent(Consts.MAIN_MAP_CLICK,  data);
	}
	
	@Override
	public void onInfoWindowClick(Marker marker) {
		// Gather place it title description and location, send to main
		HashMap<String,String> info = new HashMap<String,String>();
		info.put(Consts.DATA_PLACEIT_TITLE, marker.getTitle());
		info.put(Consts.DATA_PLACEIT_ID, marker.getSnippet());
		GoogleMapData data = new GoogleMapData(marker.getPosition(), info);
		mListener.onFragmentEvent(Consts.MAIN_MARKER_CLICK, data);
	}
	
	
// Fragment Life Cycle Management Section -----------------
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, 
			Bundle savedInstanceState){
		// Inflate the layout, setup google map, and load all markers
		mapView = super.onCreateView(inflater,  container,  savedInstanceState);
		initializeGoogleMap();
		username = ((MainActivity)getActivity()).getUser();
		loadAllMarkers();
		return mapView;
	}
	
	@Override 
	public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
    }
	
	
// Private Helper Method Section --------------------------
	private void initializeGoogleMap(){
		if(mMap == null){
			mMap = this.getMap();
			mMap.setMyLocationEnabled(true);
			mMap.setOnMapClickListener(this);
			mMap.setOnInfoWindowClickListener(this);
			allMarkers = new ArrayList<Marker>();
		}
	}
	
	private void loadAllMarkers(){
		allMarkers.clear();
		Log.d("Frankie", "lol, googlemapfragment, about to load markers for "+username);
		DatabaseHelper database = new DatabaseHelper(getActivity());
		ArrayList<PlaceIt> piPool = (ArrayList<PlaceIt>) database.getAllPlaceIts(Consts.ACTIVE, null, Consts.TYPE_NORMAL, username);		
		ArrayList<PlaceIt> piPool2 = (ArrayList<PlaceIt>) database.getAllPlaceIts(Consts.ACTIVE, null, Consts.TYPE_RECURRING, username);		
		database.closeDB();
		setMarkers(piPool);
		setMarkers(piPool2);
	}


	private void setMarkers(ArrayList<PlaceIt> piPool) {
		for(int i = 0; i < piPool.size(); i++){
			PlaceIt pi = piPool.get(i);
			String title = pi.getTitle();
			String id = String.valueOf(pi.getId());
			LatLng coord = pi.getCoord();
			MarkerOptions markerOpt = new MarkerOptions()
				.position(coord)
				.title(title)
				.snippet(id);
			Marker newMarker = mMap.addMarker(markerOpt);
			allMarkers.add(newMarker);
		}
	}

}
