package edu.ucsd.placeit.service;

import java.util.List;

import edu.ucsd.placeit.model.PlaceIt;

public interface IPlaceItHandler {

	/**
	 * Updates the placeIt state
	 * @param intent
	 * @return
	 */
	public PlaceIt getPlaceIt(int placeItId);

	public List<PlaceIt> getAllPlaceIts();
	
	public List<PlaceIt> getAllPlaceIts(int state, String category);

}
