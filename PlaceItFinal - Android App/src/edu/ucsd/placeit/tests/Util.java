package edu.ucsd.placeit.tests;

import java.util.Date;

import com.google.android.gms.maps.model.LatLng;

import edu.ucsd.placeit.model.PlaceIt;
import edu.ucsd.placeit.model.impl.CategoricalPlaceIt;
import edu.ucsd.placeit.model.impl.NormalPlaceIt;
import edu.ucsd.placeit.model.impl.RecurringPlaceIt;
import edu.ucsd.placeit.util.Consts;
import edu.ucsd.placeit.util.GooglePlacesTypes;

public class Util {
	/**
	 * Create a random categorical placeit
	 * 
	 * @return
	 */
	public static PlaceIt makeCategoricalPlaceIt() {
		int id = 0;
		String user = "Kevin";
		String title = "CatTitle";
		String desc = "CatItDesc";
		int state = Consts.ACTIVE;
		Date dateCreated = new Date();
		Date datePosted = new Date();
		String[] categories = new String[3];

		int listLength = GooglePlacesTypes.CATEGORIES.size();
		// Getting random categories
		for (int i = 0; i < 3; i++) {
			int randomCat = (int) (Math.random() * listLength);
			categories[i] = GooglePlacesTypes.CATEGORIES.get(randomCat);
		}
		PlaceIt placeIt = new CategoricalPlaceIt(id, user, title, desc, state,
				dateCreated, datePosted, categories);
		return placeIt;
	}

	/**
	 * Create a random placeit
	 * 
	 * @return
	 */
	public static PlaceIt makeNormalPlaceIt() {
		int id = 0;
		String user = "Kevin";
		String title = "NewPlaceItTitle";
		String desc = "PlaceItDesc";
		int state = Consts.ACTIVE;
		LatLng coord = new LatLng(1.52, 2.15);
		Date dateCreated = new Date();
		Date datePosted = new Date();
		PlaceIt placeIt = new NormalPlaceIt(id, user, title, desc, state,
				coord, dateCreated, datePosted);
		return placeIt;
	}

	/**
	 * Create a recurring placeIts
	 */
	public static PlaceIt makeReccuringPlaceIt() {
		int id = 0;
		String user = "Kevin";
		String title = "NewRecurringPlaceIt";
		String desc = "ReccuringDesc";
		int state = Consts.ACTIVE;
		LatLng coord = new LatLng(1.52, 2.15);
		Date dateCreated = new Date();
		Date datePosted = new Date();
		int frequency = 123;
		// int expiration = 1000;
		PlaceIt placeIt = new RecurringPlaceIt(id, user, title, desc, state,
				coord, dateCreated, datePosted, frequency);
		return placeIt;
	}

}
