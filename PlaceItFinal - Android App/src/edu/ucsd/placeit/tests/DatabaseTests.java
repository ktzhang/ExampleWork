package edu.ucsd.placeit.tests;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.test.AndroidTestCase;
import android.util.Log;
import edu.ucsd.placeit.db.DatabaseHelper;
import edu.ucsd.placeit.fragment.NewCategoryPlaceIt;
import edu.ucsd.placeit.model.PlaceIt;
import edu.ucsd.placeit.model.impl.CategoricalPlaceIt;
import edu.ucsd.placeit.util.Cfg;
import edu.ucsd.placeit.util.Consts;
import edu.ucsd.placeit.util.GooglePlacesTypes;

public class DatabaseTests extends AndroidTestCase {
	private DatabaseHelper db;

	/**
	 * Setup and create context
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Context context = getContext();
		db = new DatabaseHelper(context);
		db.deleteAllPlaceIts();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		db.closeDB();
		db = null;
	}

	/**
	 * Retrieving multiple placeIts from the database
	 */
	public void testRetrievePlaceIt() {
		int size = 10;
		CategoricalPlaceIt[] placeIt = new CategoricalPlaceIt[size];

		for (int i = 0; i < size; i++) {
			placeIt[i] = (CategoricalPlaceIt) Util.makeCategoricalPlaceIt();
			placeIt[i].setState(Consts.ACTIVE);
			String[] cats = new String[] { GooglePlacesTypes.CATEGORIES.get(i),
					"", "" };
			placeIt[i].setCategories(cats);
			placeIt[i].setId(db.createPlaceIt(placeIt[i]));
		}

		List<PlaceIt> allPlaceIts = db.getAllPlaceIts(Consts.ACTIVE,
				GooglePlacesTypes.CATEGORIES.get(1), 0, "");
		assertEquals(1, allPlaceIts.size());

		placeIt[2].setState(Consts.COMPLETE);
		db.updatePlaceIt(placeIt[2]);
		List<PlaceIt> allPlaceIts2 = db
				.getAllPlaceIts(Consts.COMPLETE, null, 0, "");
		assertEquals(1, allPlaceIts2.size());

		String[] cats2 = new String[] { "", "",
				GooglePlacesTypes.CATEGORIES.get(1) };
		placeIt[3].setCategories(cats2);
		List<PlaceIt> allPlaceIts3 = db.getAllPlaceIts(Consts.ACTIVE,
				GooglePlacesTypes.CATEGORIES.get(1), 0, "");
		assertEquals(1, allPlaceIts3.size());

	}
	
	
	/**
	 * Test update placeIt
	 */
	public void testUpdatePlaceItState() {
		PlaceIt placeIt = Util.makeNormalPlaceIt();
		int id = db.createPlaceIt(placeIt);
		
		int stateConst = 421;
		placeIt.setState(stateConst);
		placeIt.setId(id);
		
		db.updatePlaceIt(placeIt);
		PlaceIt newPlaceIt = db.getPlaceIt(id);
		int newState = newPlaceIt.getState();
		
		assertEquals(newState, stateConst);
	}

	/**
	 * Testing the general adding placeit method
	 */
	public void testAddSinglePlaceIt() {
		PlaceIt placeIt = Util.makeNormalPlaceIt();
		// Adds in one single placeIt
		long newId = db.createPlaceIt(placeIt);
		Log.d(Consts.TAG_TEST, Long.toString(newId));

		// Ensures that it is the last placeit added
		assertEquals(db.getPlaceItCount(), newId);
	}

	/**
	 * Test update placeit
	 */
	public void testUpdatePlaceIt() {
		// Adds in one single placeIt
		PlaceIt placeIt = Util.makeNormalPlaceIt();
		int id = db.createPlaceIt(placeIt);

		// Updating
		PlaceIt newPlaceIt = db.getPlaceIt(id);
		assertNotNull(newPlaceIt);

		newPlaceIt.setState(Consts.COMPLETE);

		db.updatePlaceIt(newPlaceIt);

		// Final getting
		PlaceIt finalPlaceIt = db.getPlaceIt(id);
		assertEquals(newPlaceIt.getDesc(), finalPlaceIt.getDesc());
		assertEquals(newPlaceIt.getCoord(), finalPlaceIt.getCoord());
		assertEquals(newPlaceIt.getStartTime(), finalPlaceIt.getStartTime());
		assertNotNull(finalPlaceIt);
		assertEquals(finalPlaceIt.getState(), Consts.COMPLETE);
	}

	/**
	 * Inserting a cat placeIt
	 */
	public void testInsertCatPlaceIt() {
		PlaceIt placeIt = Util.makeCategoricalPlaceIt();
		int id = db.createPlaceIt(placeIt);
		assertEquals(db.getPlaceItCount(), id);
	}

	/**
	 * Testing retrieve a cat PlaceIt
	 */
	public void testRetrieveCatPlaceIt() {
		PlaceIt placeIt = Util.makeCategoricalPlaceIt();
		int id = db.createPlaceIt(placeIt);
		assertEquals(db.getPlaceItCount(), id);

		PlaceIt newPlaceIt = db.getPlaceIt(id);
		assertTrue(newPlaceIt instanceof CategoricalPlaceIt);
	}

	/**
	 * Test update category stuff
	 */
	public void testUpdateCatPlaceIt() {
		PlaceIt placeIt = Util.makeCategoricalPlaceIt();
		int id = db.createPlaceIt(placeIt);

		try {
			CategoricalPlaceIt newPlaceIt = (CategoricalPlaceIt) db
					.getPlaceIt(id);
			newPlaceIt.setState(Consts.COMPLETE);
			String[] cats = new String[] { GooglePlacesTypes.CATEGORIES.get(1),
					GooglePlacesTypes.CATEGORIES.get(2),
					GooglePlacesTypes.CATEGORIES.get(3) };
			newPlaceIt.setCategories(cats);
			db.updatePlaceIt(newPlaceIt);

			// Final getting
			PlaceIt finalPlaceIt = db.getPlaceIt(id);
			assertEquals(newPlaceIt.getDesc(), finalPlaceIt.getDesc());
			assertEquals(newPlaceIt.getCoord(), finalPlaceIt.getCoord());
			assertEquals(newPlaceIt.getStartTime(), finalPlaceIt.getStartTime());

			for (int i = 0; i < 3; i++)
				assertEquals(newPlaceIt.getCategories()[i],
						((CategoricalPlaceIt) finalPlaceIt).getCategories()[i]);
			assertNotNull(finalPlaceIt);
			assertEquals(finalPlaceIt.getState(), Consts.COMPLETE);
		} catch (Exception e) {
			assertTrue(false);
		}
	}

	/**
	 * Test remove placeIt
	 */
	public void testRemovePlaceIt() {
		PlaceIt placeIt = Util.makeNormalPlaceIt();
		PlaceIt placeIt2 = Util.makeCategoricalPlaceIt();

		int id = db.createPlaceIt(placeIt);
		int id2 = db.createPlaceIt(placeIt2);

		int remove = db.deletePlaceIt(id);
		int remove1 = db.deletePlaceIt(id2);

		// Seeing that 1 row is removed
		assertEquals(remove, 1);
		assertEquals(remove1, 1);

		// See that you cannot get anymore PlaceIts
		assertNull(db.getPlaceIt(id));
		assertNull(db.getPlaceIt(id2));

		assertEquals(0, db.getPlaceItCount());
	}

	/**
	 * Test get version
	 */
	public void testGetVersion() {
		Log.d(Consts.TAG_TEST,
				"" + Consts.DATE_FORMAT_SIMPLE.format(new Date(0)));
		Date date = db.getDbVersion();
		assertNotNull(date);
		Log.d(Consts.TAG_TEST, "" + date);
	}
	
	/**
	 * Testing updating the database version
	 */
	public void testUpdateVersion() {
		//Manually setting date
		Date date = new Date(2521200);
		db.setVersion(date);
		Date date2 = db.getDbVersion();
		assertEquals(date2, date);
		
//		auto date
//		db.setVersion(null);
//		date2 = db.getVersion();
//		assertEquals(date2, new Date());
	}
	
	/**
	 * Testing getting user information
	 */
	public void testUserInfo(){
		PlaceIt placeIt = Util.makeNormalPlaceIt();
		PlaceIt placeIt2 = Util.makeCategoricalPlaceIt();
		String user = placeIt.getUser();
		int id = db.createPlaceIt(placeIt);
		int id2 = db.createPlaceIt(placeIt2);
		PlaceIt placeIt3 = db.getPlaceIt(id);
		PlaceIt placeIt4 = db.getPlaceIt(id2);
		assertEquals(placeIt4.getUser(), user);
		placeIt3.setUser("AMIGO");
		db.updatePlaceIt(placeIt3);
		PlaceIt finalPlaceIt = db.getPlaceIt(id);
		assertEquals(finalPlaceIt.getUser(), "AMIGO");
		
	}
	
	
	public void testGetCategory() {
		PlaceIt placeIt = Util.makeNormalPlaceIt();
		PlaceIt placeIt2 = Util.makeCategoricalPlaceIt();

		int id = db.createPlaceIt(placeIt);
		int id2 = db.createPlaceIt(placeIt2);
		
		List<PlaceIt> list = db.getAllPlaceIts(0, null, 0, "");
		int i = 0;
		for(PlaceIt pl : list){
			i++;
			assertEquals(pl.getId(), i);
		}
	}

	public void testGetUsername() {
		PlaceIt placeIt = Util.makeNormalPlaceIt();
		PlaceIt placeIt2 = Util.makeCategoricalPlaceIt();
		placeIt.setUser("Hi");
		placeIt2.setUser("Bye");
		
		int id = db.createPlaceIt(placeIt);
		int id2 = db.createPlaceIt(placeIt2);
		
		List<PlaceIt> allList = db.getAllPlaceIts(0, null, 0, null);
		assertEquals(allList.size(), 2);
		List<PlaceIt> list = db.getAllPlaceIts(0, null, 0, "Hi");
		assertEquals(list.size(), 1);
		List<PlaceIt> list2 = db.getAllPlaceIts(0, null, 0, "Bye");
		assertEquals(list2.size(), 1);
		
		assertEquals(list.get(0).getId(), id);
		assertEquals(list2.get(0).getId(), id2);
	}

	/**
	 * Testing the preconditions of the database
	 */
	public void testPreConditions() {
		assertNotNull(db);
	}

}
