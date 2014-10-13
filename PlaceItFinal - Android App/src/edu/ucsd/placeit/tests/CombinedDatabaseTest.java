package edu.ucsd.placeit.tests;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.MessageQueue.IdleHandler;
import android.test.AndroidTestCase;
import android.util.Log;
import edu.ucsd.placeit.db.DatabaseHelper;
import edu.ucsd.placeit.db.OnlineDatabaseHelper;
import edu.ucsd.placeit.model.PlaceIt;
import edu.ucsd.placeit.model.PlaceItBank;
import edu.ucsd.placeit.model.PlaceItBank.PlaceItIterator;
import edu.ucsd.placeit.service.handler.VersionHandler;
import edu.ucsd.placeit.util.Consts;

public class CombinedDatabaseTest extends AndroidTestCase {
	private OnlineDatabaseHelper onlineDb;
	private DatabaseHelper offlineDb;
	private VersionHandler versionHandler;
	/**
	 * Setup and create context
	 */
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Context context = getContext();
		onlineDb = new OnlineDatabaseHelper(context);
		offlineDb = new DatabaseHelper(context);
		versionHandler = new VersionHandler(context);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
//		db.deleteAllPlaceIts();
		onlineDb = null;
		offlineDb = null;
		versionHandler = null;
	}

	/**
	 * Testing add normal placeIt
	 */
	public void testLocalAddPlaceIt() {
		PlaceIt placeIt = Util.makeNormalPlaceIt();
		int id = offlineDb.createPlaceIt(placeIt);
		versionHandler.handleConflicts();
		
		List<PlaceIt> placeIts = onlineDb.getAllPlaceIts();
		assertEquals(placeIts.get(0).getStartTime(), placeIt.getStartTime());
	}
	
	/**
	 * Testing add online placeIt
	 */
	public void testOneAddPlaceIt() {
		PlaceIt placeIt = Util.makeNormalPlaceIt();
		placeIt.setId(1);
		onlineDb.createUpdatePlaceIt(placeIt);
		versionHandler.handleConflicts();
		
		List<PlaceIt> placeIts = offlineDb.getAllPlaceIts();
		assertEquals(placeIts.get(0).getStartTime(), placeIt.getStartTime());
	}

	/**
	 * Random other tests
	 */
	public void testRandomAddPlaceIt() {
		PlaceIt placeIt = Util.makeNormalPlaceIt();
		placeIt.setId(1);
		onlineDb.createUpdatePlaceIt(placeIt);
		versionHandler.handleConflicts();
		
		List<PlaceIt> placeIts = offlineDb.getAllPlaceIts();
		assertEquals(placeIts.get(0).getStartTime(), placeIt.getStartTime());
		assertEquals(placeIts.get(0).getUser(), placeIt.getUser());
		
		PlaceIt placeIt2 = Util.makeCategoricalPlaceIt();
		PlaceIt placeIt3 = Util.makeReccuringPlaceIt();
		placeIt2.setId(1);
		placeIt3.setId(2);
		onlineDb.createUpdatePlaceIt(placeIt2);
		onlineDb.createUpdatePlaceIt(placeIt3);
		onlineDb.addUpdateVersion(new Date());
		versionHandler.handleConflicts();
		
		List<PlaceIt> placeIts2 = offlineDb.getAllPlaceIts();
		assertEquals(placeIts2.get(0).getStartTime(), placeIt2.getStartTime());
		assertEquals(placeIts2.get(0).getUser(), placeIt2.getUser());
		assertEquals(placeIts2.get(1).getStartTime(), placeIt3.getStartTime());
		assertEquals(placeIts2.get(1).getUser(), placeIt3.getUser());		
	}

	/**
	 * Testing the preconditions of the database
	 */
	public void testPreConditions() {
		assertNotNull(onlineDb);
		assertNotNull(offlineDb);
		assertNotNull(versionHandler);
	}

}
