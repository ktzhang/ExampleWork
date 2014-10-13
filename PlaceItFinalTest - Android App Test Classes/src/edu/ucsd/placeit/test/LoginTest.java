package edu.ucsd.placeit.test;

import android.os.RemoteException;

import com.android.uiautomator.testrunner.UiAutomatorTestCase;
import com.android.uiautomator.core.*;

public class LoginTest extends UiAutomatorTestCase {
	
	public void testLoginTest() throws UiObjectNotFoundException, RemoteException {
		
		//creates the UI device object to use for User Inputs on screen
		UiDevice device = getUiDevice();
		device.pressHome();
		//device.pressRecentApps(); in case not already in PlaceIt app will find in recents
	
		//object for Place-it app made to find in recents menu
		UiObject placeIt = new UiObject(new UiSelector().text("PlaceIt").className("android.widget.TextView"));
	
		//open Place-it
		placeIt.clickAndWaitForNewWindow();
		
		
		/*		Scenario: User creates reminder using a single category of a location
		 *		Given: That user is using Place-its, is logged in, and is creating a reminder
		 *		When: The user chooses a single category (e.g. Grocery Store)
		 *		Then: A reminder is created for all locations of that category
		 */
		
		UiObject signInScreen = new UiObject(new UiSelector()
		.className("android.widget.TextView")
		.text("Please Sign in Here"));
	
		//Verify that there is a sign in screen
		assertTrue("No Sign In Screen", signInScreen.exists());
		
//		UiObject username = new UiObject(new UiSelector()
//			.className("android.widget.LinearLayout")
//			.index(0)
//				.childSelector(new UiSelector()
//				.className("android.widget.LinearLayout")
//				.index(1)
//						.childSelector(new UiSelector()
//						.className("android.widget.LinearLayout")
//						.index(0)
//								.childSelector(new UiSelector())
//								.className("android.widget.EditTex")
//								.index(1))));
//		
//		UiObject password = new UiObject(new UiSelector()
//			.className("android.widget.LinearLayout")
//			.index(0)
//				.childSelector(new UiSelector()
//				.className("android.widget.LinearLayout")
//				.index(1)
//						.childSelector(new UiSelector()
//						.className("android.widget.LinearLayout")
//						.index(1)
//								.childSelector(new UiSelector())
//								.className("android.widget.EditTex")
//								.index(1))));


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
		
		
		// Inside MainActivity Now
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
		.className("android.widget.TextView").text("Grocery"));
		
		//Verification previously created reminder
		assertTrue("No previously created reminder in database", oldReminder.exists());
		
		device.pressHome();
		
		/*
		 *	Scenario: User opens Place-its after previously login
		 *	Given: That a user previously entered his/her login and password on Place-its
		 *	When: The user opens Place-its again
		 *	Then: Place-its will remember his login credentials and will see previously created reminders
		 */
		
		placeIt.clickAndWaitForNewWindow();
		catListButton.click();

		//Verification previously created reminder
		assertTrue("No previously saved user login credentials", oldReminder.exists());
			
		UiObject backButton = new UiObject(new UiSelector()
			.className("android.widget.Button")
			.text("Go Back"));
		
		UiObject logoutButton = new UiObject(new UiSelector()
			.className("android.widget.Button")
			.text("Log out"));

		backButton.clickAndWaitForNewWindow();
		
		/*
		 *	Scenario: New user logs in to Place-its
		 *	Given: That user A has logged in to Place-its and saved some reminders
		 *	When: User A logs out
		 *	And a new user B logs in to Place-its on the same Android device
		 *	Then: All the user A's reminders will no longer be available on Place-its
		 *	And Place-its will be empty for the new user who logged in
		 */
	
		logoutButton.clickAndWaitForNewWindow();		
		username.setText("Jin");
		password.setText("12345");
		loginButton.clickAndWaitForNewWindow();
		listButton.clickAndWaitForNewWindow();
		
		UiObject oldLocReminder = new UiObject(new UiSelector()
			.className("android.widget.TextView")
			.text("stuff"));
		
		//Verification that location reminders list is empty
		assertTrue("Location reminders list not empty part 1", oldLocReminder.exists());
		
		catListButton.click();

		backButton.clickAndWaitForNewWindow();
		logoutButton.clickAndWaitForNewWindow();
		
	}
	
}
