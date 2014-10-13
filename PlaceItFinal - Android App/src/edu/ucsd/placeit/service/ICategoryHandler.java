package edu.ucsd.placeit.service;

import java.util.List;

import android.location.Location;
import edu.ucsd.placeit.model.PlaceIt;

public interface ICategoryHandler {
	public List<PlaceIt> onLocationChanged(Location location, String userName);

}
