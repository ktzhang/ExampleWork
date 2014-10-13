package edu.ucsd.placeit.tests;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.nfc.Tag;
import android.test.AndroidTestCase;
import android.util.Log;
import edu.ucsd.placeit.db.OnlineDatabaseHelper;
import edu.ucsd.placeit.db.util.PlaceItsUrlBuilder;
import edu.ucsd.placeit.model.PlaceIt;
import edu.ucsd.placeit.model.PlaceItBank;
import edu.ucsd.placeit.model.PlaceItBank.PlaceItIterator;
import edu.ucsd.placeit.util.Consts;

public class OnlineDatabaseTest extends AndroidTestCase {
	private OnlineDatabaseHelper db;

	/**
	 * Setup and create context
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Context context = getContext();
		db = new OnlineDatabaseHelper(context);
		db.deleteAllPlaceIts();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
//		db.deleteAllPlaceIts();
		db = null;
	}

	/**
	 * Testing add normal placeIt
	 */
	public void testAddPlaceIt() {
		PlaceIt placeIt = Util.makeNormalPlaceIt();
		placeIt.setId(1);
		db.createUpdatePlaceIt(placeIt);
		List<PlaceIt> placeIts = db.getAllPlaceIts();
		assertEquals(placeIts.size(), 1);
	}

	/**
	 * Testing add normal placeIt
	 */
	public void testGetNormalPlaceIt() {
		PlaceIt placeIt = Util.makeNormalPlaceIt();
		placeIt.setId(1);
		db.createUpdatePlaceIt(placeIt);
		List<PlaceIt> placeIts = db.getAllPlaceIts();
		assertEquals(placeIts.size(), 1);
		assertEquals(placeIts.get(0).getId(), 1);
	}
	
	/**
	 * Testing add normal placeIt
	 */
	public void testGetUser() {
		PlaceIt placeIt = Util.makeNormalPlaceIt();
		placeIt.setId(1);
		db.createUpdatePlaceIt(placeIt);
		List<PlaceIt> placeIts = db.getAllPlaceIts();
		assertEquals(placeIts.size(), 1);
		assertEquals(placeIts.get(0).getUser(), "Kevin");
	}

	/**
	 * Testing add normal placeIt
	 */
	public void testAddCategoricalPlaceIt() {
		PlaceIt placeIt = Util.makeCategoricalPlaceIt();
		placeIt.setId(2);
		db.createUpdatePlaceIt(placeIt);
		List<PlaceIt> placeIts = db.getAllPlaceIts();
		assertEquals(placeIts.size(), 1);
	}

	/**
	 * Testing add normal placeIt
	 */
	public void testAddReccurringPlaceIt() {
		PlaceIt placeIt = Util.makeReccuringPlaceIt();
		placeIt.setId(3);
		db.createUpdatePlaceIt(placeIt);
	}

	/**
	 * Testing add normal placeIt
	 */
	public void testUpdateVersion() {
//		Date date = new Date();
		Date date = db.addUpdateVersion(null);
		Date dbVersionDate = db.getDbVersion();
		
		
		long differenceInMillis = date.getTime() - dbVersionDate.getTime();

		Log.d(Consts.TAG_TEST,
				"Is date >? " + differenceInMillis);
		Log.d(Consts.TAG_TEST, "date is " + dbVersionDate);
		//Make sure the date is within the range
		assertTrue(Math.abs(differenceInMillis) < 2000);
		assertEquals(dbVersionDate, date);
	}

	/**
	 * Test retrieve version information
	 */
	public void testRetreiveVersion() {
		assertNotNull(db.getDbVersion());
	}

	/**
	 * Testing retrieving of placeIts
	 */
	public void testGetPlaceIt() {
		PlaceIt placeIt = Util.makeNormalPlaceIt();
		placeIt.setId(1);
		db.createUpdatePlaceIt(placeIt);
		PlaceIt placeIt2 = Util.makeCategoricalPlaceIt();
		placeIt2.setId(2);
		db.createUpdatePlaceIt(placeIt2);
		PlaceIt placeIt3 = Util.makeReccuringPlaceIt();
		placeIt3.setId(3);
		db.createUpdatePlaceIt(placeIt3);

		List<PlaceIt> list = db.getAllPlaceIts();

		PlaceItBank placeItBank = new PlaceItBank(list);
		PlaceItIterator iterator = placeItBank.iterator();

		while (iterator.hasNext()) {
			PlaceIt bank = iterator.next();
		}
		assertNotNull(list);
	}
	
	public void testSetUsername() {
		PlaceIt placeIt = Util.makeNormalPlaceIt();
		PlaceIt placeIt2 = Util.makeCategoricalPlaceIt();
		placeIt.setUser("Hi");
		placeIt.setId(1);
		placeIt2.setUser("Bye");
		placeIt2.setId(2);
		
		db.createUpdatePlaceIt(placeIt);
		db.createUpdatePlaceIt(placeIt2);
		
		List<PlaceIt> allList = db.getAllPlaceIts();
		
		assertEquals(allList.get(0).getUser(), "Hi");
		assertEquals(allList.get(1).getUser(), "Bye");
	}
	
	/**
	 * Testing the preconditions of the database
	 */
	public void testPreConditions() {
		assertNotNull(db);
	}

}
