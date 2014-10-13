package edu.ucsd.placeit.test;

import android.os.RemoteException;

import com.android.uiautomator.testrunner.UiAutomatorTestCase;
import com.android.uiautomator.core.*;

public class NotifyLocationReminderTest extends UiAutomatorTestCase {

	public void testNotifyLocRemTest() throws UiObjectNotFoundException, RemoteException {
		
		//creates the UI device object to use for User Inputs on screen
		UiDevice device = getUiDevice();
		device.pressHome();
		//device.pressRecentApps(); in case not already in PlaceIt app will find in recents
	
		//Setting up mock location tester
		UiObject mockLocation = new UiObject(new UiSelector() 
			.className("android.widget.TextView")
			.text("Mock Location Tester"));
			
		mockLocation.clickAndWaitForNewWindow();
		
		UiObject pauseSec = new UiObject(new UiSelector()
			.className("android.widget.RelativeLayout")
			.index(2)
				.childSelector(new UiSelector()
				.className("android.widget.EditText")));
			
				
		UiObject intervalSec = new UiObject(new UiSelector()
			.className("android.widget.RelativeLayout")
			.index(3)
				.childSelector(new UiSelector()
				.className("android.widget.EditText")));
		

		UiObject runContinuously = new UiObject(new UiSelector()
			.className("android.widget.Button")
			.text("Run continuously"));
		
		pauseSec.setText("5");
		intervalSec.setText("1");
		runContinuously.clickAndWaitForNewWindow();

		device.pressHome();
		
		UiObject placeIt = new UiObject(new UiSelector() 
			.className("android.widget.TextView")
			.text("PlaceIt"));
		
		placeIt.clickAndWaitForNewWindow();

		
		/*		Scenario: Notify user when near a category of previously saved place-it
		 *		Given: That a previously saved reminder of a specific category is active
		 *		When: The user is near a location that is of the same category as the place-it
		 *		Then: The user is notified of the reminder
		 */
		UiObject username = new UiObject(new UiSelector()
			.className("android.widget.LinearLayout")
			.index(1)
			.childSelector(new UiSelector()
			.className("android.widget.EditText")
			.index(1)
			.focused(true)));

		UiObject password = new UiObject(new UiSelector()
			.className("android.widget.LinearLayout")
			.index(1)
			.childSelector(new UiSelector()
			.className("android.widget.EditText")
			.index(1)
			.focused(false)));
		
		username.setText("Alex");
		password.setText("12345");
		
		UiObject loginButton = new UiObject(new UiSelector()
			.className("android.widget.Button"));
	
		// Log user in
		loginButton.clickAndWaitForNewWindow();
		
		UiObject listButton = new UiObject(new UiSelector()
			.className("android.widget.Button")
			.text("PlaceIt List"));

		//wait for mock location to drive by reminder's location
		listButton.waitForExists(30000);
		
//		
//		UiObject nearAlert = new UiObject(new UiSelector()
//			.className("android.widget.TextView")
//			.text(""));
//
//		//Verification notification screen
//		assertTrue("Notification did not appear", nearAlert.exists());
//		
		UiObject completePlaceIt = new UiObject(new UiSelector()
			.className("android.widget.Button")
			.text("Mark Complete"));
		completePlaceIt.waitForExists(100000);

		completePlaceIt.clickAndWaitForNewWindow();
		
						
		/*
		 *		Scenario: No notification when near a location of a different category than the active place-its
		 *		Given: That there are active reminders for specific categories selected
		 *		When: The user approaches a location that is not the same category as the place-its saved
		 *		Then: The user is not notified of any reminder
		 */

		//wait for mock location to drive by reminder's location
		listButton.waitForExists(30000);
				
		UiObject farAlert = new UiObject(new UiSelector()
			.className("android.widget.TextView")
			.text("Duplicate house keys"));

		//Verification of no notification screen
		assertFalse("Notification did not appear", farAlert.exists());
	
	}
}
