package edu.ucsd.placeit.test;

import android.os.RemoteException;

import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

public class SecondDevLoginTest extends UiAutomatorTestCase {
	
	public void testSecDevLogTest() throws UiObjectNotFoundException, RemoteException {
	
		
		//creates the UI device object to use for User Inputs on screen
		UiDevice device = getUiDevice();
		device.pressHome();
		//device.pressRecentApps(); in case not already in PlaceIt app will find in recents
	
		//object for Place-it app made to find in recents menu
		UiObject placeIt = new UiObject(new UiSelector() 
			.className("android.widget.TextView")
			.text("PlaceIt"));
		
		placeIt.clickAndWaitForNewWindow();

		
		/*
		 *	Scenario: User logs in to a different Android device
		 *	Given: That a user has a logged in to Place-its on an Android device and saved some reminders
		 *	When: The user logs in to a different Android device with the same login
		 *	Then: All the user's reminders will be available on the new Android device where the user has logged in
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
		
		
		UiObject newCatButton = new UiObject(new UiSelector()
			.className("android.widget.Button")
			.text("New Category PlaceIt"));
	
		//This verifies that the app did not crash, logged in, and changed to
		//the screen that corresponds.
		assertTrue("Failed to enter with previous login credentials", newCatButton.exists());
		
		UiObject listButton = new UiObject(new UiSelector()
			.className("android.widget.Button")
			.text("PlaceIt List"));
	
		listButton.clickAndWaitForNewWindow();
		
		UiObject catListButton = new UiObject(new UiSelector()
			.className("android.widget.TextView")
			.text("Category Place-It"));
	
		catListButton.click();
		
		UiObject oldReminder = new UiObject(new UiSelector()
			.className("android.widget.TextView").text("time"));
		
		//Verification previously created reminder
		assertTrue("No previously created reminder in database", oldReminder.exists());
		
		UiObject logoutButton = new UiObject(new UiSelector()
		.className("android.widget.Button")
		.text("Log out"));
		
		logoutButton.clickAndWaitForNewWindow();
		
		device.pressHome();
	}
}
