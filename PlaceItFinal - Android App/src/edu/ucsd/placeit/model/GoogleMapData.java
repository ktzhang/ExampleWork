package edu.ucsd.placeit.model;

import java.util.HashMap;
import java.util.Map;

import com.google.android.gms.maps.model.LatLng;

public class GoogleMapData {
	private LatLng location;
	private HashMap<String, String> data;
	
	public GoogleMapData(LatLng location, HashMap<String, String> data){
		this.location = location;
		this.data = data;
	}

	public LatLng getLocation() {
		return location;
	}

	public HashMap<String, String> getData() {
		return data;
	}
}
