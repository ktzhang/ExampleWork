package edu.ucsd.placeit.tests;

import java.util.List;

import android.content.Context;
import android.location.Location;
import android.test.AndroidTestCase;
import edu.ucsd.placeit.db.DatabaseHelper;
import edu.ucsd.placeit.model.PlaceIt;
import edu.ucsd.placeit.model.impl.CategoricalPlaceIt;
import edu.ucsd.placeit.service.handler.CategoryHandler;

public class CategoryHandlerTest extends AndroidTestCase {
	CategoryHandler categoryHandler;
	DatabaseHelper db;

	@Override
	protected void setUp() {
		Context context = getContext();
		categoryHandler = new CategoryHandler(context);
		db = new DatabaseHelper(context);
		db.deleteAllPlaceIts();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		db.closeDB();
		db = null;
	}

	public void testSimpleAdd() {
		Location location = new Location("");
		location.setLatitude(65.9667);
		location.setLongitude(-18.5333);
		CategoricalPlaceIt placeIt = (CategoricalPlaceIt) Util
				.makeCategoricalPlaceIt();
		String[] cats = new String[] { null, null, new String("political") };
		placeIt.setCategories(cats);
		db.createPlaceIt(placeIt);
		db.createPlaceIt(placeIt);

		// Retrieve a list of categories
		List<PlaceIt> categories = categoryHandler.onLocationChanged(location, "");
		assertEquals(categories.size(), 1);

		// db.closeDB();
		// for( String cat: categories) {
		// Log.d(Consts.TAG_TEST, cat);
		// }

		// assertEquals(categories.size(), 3);
		// assertTrue(categories.contains("route"));
		// assertTrue(categories.contains("locality"));
		// assertTrue(categories.contains("political"));
	}

}
