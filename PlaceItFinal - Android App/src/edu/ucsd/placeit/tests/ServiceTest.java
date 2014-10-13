package edu.ucsd.placeit.tests;

import android.content.Intent;
import android.os.IBinder;
import android.test.ServiceTestCase;
import edu.ucsd.placeit.service.LocationService;

public class ServiceTest extends ServiceTestCase<LocationService> {

	IBinder binder;
	Intent intent;
	LocationService service;

	public ServiceTest() {
		super(LocationService.class);
	}

	public ServiceTest(Class<LocationService> serviceClass) {
		super(serviceClass);
	}

	protected void setUp() throws Exception {

		super.setUp();
		intent = new Intent(getSystemContext(), LocationService.class);
		// startActivity(intent, null, null);
		// activity = getActivity();

		// setApplication(null);
		// getApplication().onCreate();
		// setContext(getApplication());
		// startIntent = new Intent();
		// startIntent.setClass(getContext(), LocationService.class);

		// Tells that the main activity is in the foreground
		// startIntent = new Intent(getActivity(), LocationService.class);
		// startIntent.putExtra(Consts.EXTRA_ACTIVITY_ONLINE, true);

		// Intent intent = new Intent(getSystemContext(),
		// LocationService.class);
		setApplication(getApplication());
		setContext(getSystemContext());
		// startService(intent);
		binder = bindService(intent);
//		service = ((LocationService.LocalBinder) binder).getService();
	}

	//
	// @SuppressLint("NewApi")
	// public void testInitialSetup() {
	// assertTrue(service.returnTrue().equals(Consts.TAG_TEST));
	// }
	// @SuppressLint("NewApi")
	// public void testDBInsert() {
	// service = ((LocationService.LocalBinder) binder).getService();
	//
	// assertTrue(service.returnTrue().equals(Consts.TAG_TEST));
	// }

	// @MediumTest
	// public void testBind() {
	// binder = bindService(intent);
	// service = ((LocationService.LocalBinder) binder).getService();
	// assertNotNull(binder);
	// assertNotNull(service);
	// }
	//
	// public void testServiceSetup() {
	// service = ((LocationService.LocalBinder) binder).getService();
	// assertEquals(Consts.TAG_TEST, service.returnTrue());
	// }
}