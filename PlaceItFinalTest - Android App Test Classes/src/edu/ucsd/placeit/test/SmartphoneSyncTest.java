package edu.ucsd.placeit.test;

import android.os.RemoteException;

import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

public class SmartphoneSyncTest extends UiAutomatorTestCase {
	
	public void testSmarthpnSyncTest() throws UiObjectNotFoundException, RemoteException {
	
		
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
		 *		Scenario: User creates a reminder on their tablet and sees it on their phone
		 *		Given: The user is logged in to the same Place-Its account on their tablet and smartphone
		 *		When: The user creates a reminder on their tablet
		 *		Then: The user can see the newly created reminder on their smartphone
		 *
		 *		NOTE: Only smartphone portion of the test will be conducted here, the tablet portion
		 *			  is conducted under TabletSyncTest.java
		 */
		
		UiObject listButton = new UiObject(new UiSelector()
		.className("android.widget.Button")
		.text("PlaceIt List"));

		listButton.clickAndWaitForNewWindow();
		
		UiObject catListButton = new UiObject(new UiSelector()
			.className("android.widget.TextView")
			.text("Category Place-It"));
	
		//30 seconds to wait for new reminder from tablet
		catListButton.waitForExists(30000);
		catListButton.click();
		
		UiObject tabletReminder = new UiObject(new UiSelector()
			.className("android.widget.TextView")
			.text("This reminder is from the tablet"));
	
		//Verification newly created reminder using tablet
		assertTrue("No category reminder created in database", tabletReminder.exists());
		
		UiObject backButton = new UiObject(new UiSelector()
			.className("android.widget.Button")
			.text("Go Back"));
		
		
		/*	
		 *		Scenario: User marks as completed a reminded from tablet
		 *		Given: The user is logged in to the same Place-Its account on their tablet and smartphone
		 *		When: The user marks a reminder as completed on their tablet
		 *		Then: The user will see the reminder as completed on their smartphone
		 *
		 *		NOTE: Only smartphone portion of the test will be conducted here, the tablet portion
		 *			  is conducted under TabletSyncTest.java
		 */

		UiObject tabletCompReminder = new UiObject(new UiSelector()
			.className("android.widget.TextView")
			.text("Completed"));
			
		//Wait for 30 seconds for database to update reminder recently completed
		tabletCompReminder.waitForExists(30000);
		
		//Verification that reminder completed on tablet appears as completed on smartphone
		assertTrue("Reminder completed on tablet was not completed in database", tabletCompReminder.exists());
		
		
		/*
		 *		Scenario: User repost a reminder from smartphone
		 *		Given: The user is logged in to the same Place-Its account on their tablet and smartphone
		 *		When: The user reposts a reminder on their smartphone
		 *		Then: The user will continue to see the reminder on their tablet
		 *
		 *		NOTE: Only smartphone portion of the test will be conducted here, the tablet portion
		 *			  is conducted under TabletSyncTest.java
		 */
		
		tabletCompReminder.clickAndWaitForNewWindow();

		UiObject hitPlaceIt = new UiObject(new UiSelector()
			.className("android.widget.TextView")
			.text("You've hit a Place-it"));

		//Verification of correct next screen
		assertTrue("PlaceIt was not tapped", hitPlaceIt.exists());
		
		UiObject repostPlaceIt = new UiObject(new UiSelector()
			.className("android.widget.Button")
			.text("Re-post"));
		
		repostPlaceIt.clickAndWaitForNewWindow();
		
		//Verification of correct next screen
		assertTrue("PlaceIt was not reposted", catListButton.exists());
		
		//Wait for 30 seconds to allow tablet to verify reminder was reposted
		catListButton.waitForExists(30000);
		
		/*	
		 *		Scenario: User deletes a reminder from smartphone
		 *		Given: The user is logged in to the same Place-Its account on their tablet and smartphone
		 *		When: The user deletes a reminder on their smartphone
		 *		Then: The user will no longer see the reminder on their tablet
		 *
		 *		NOTE: Only smartphone portion of the test will be conducted here, the tablet portion
		 *			  is conducted under TabletSyncTest.java
		 */
		
		catListButton.click();
		
		tabletReminder.clickAndWaitForNewWindow();
		
		//Verification of correct next screen
		assertTrue("PlaceIt was not tapped", hitPlaceIt.exists());
		
		UiObject deletePlaceIt = new UiObject(new UiSelector()
			.className("android.widget.Button")
			.text("Delete"));
		
		deletePlaceIt.clickAndWaitForNewWindow();
		
		//Verification of correct next screen
		assertTrue("PlaceIt was not marked completed", catListButton.exists());
		
		//Wait for 30 seconds to allow tablet to verify reminder was deleted
		catListButton.waitForExists(30000);
		
		
			
		/*
		 *		Scenario: User creates a future date reminder on tablet
		 *		Given: The user is logged in to the same Place-Its account on their tablet and smartphone
		 *		When: The user creates a future date reminder on their tablet
		 *		Then: The user will not see the reminder on their smartphone on that day, but reminder will be saved on the smartphone for future posting
		 *
		 *		NOTE: Only smartphone portion of the test will be conducted here, the tablet portion
		 *			  is conducted under TabletSyncTest.java
		 */
		
		UiObject futureReminder = new UiObject(new UiSelector()
			.className("android.widget.TextView")
			.text("Future reminder"));
				
		//Wait for 30 seconds to allow tablet to create reminder future reminder
		catListButton.waitForExists(30000);

		//Verification of correct next screen
		assertFalse("Future PlaceIt is visible earlier than when posted", futureReminder.exists());
		
		backButton.clickAndWaitForNewWindow();

	
	}
}