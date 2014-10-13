package edu.ucsd.placeit.service;

import java.util.List;

import android.location.Location;
import edu.ucsd.placeit.model.PlaceIt;
import edu.ucsd.placeit.model.PlaceItBank;

public interface ILocationHandler {
	//Called to handle location changed
	public List<PlaceIt> onLocationChanged(Location location, String userName);
}
