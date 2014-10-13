package edu.ucsd.placeit.test;

import android.os.RemoteException;

import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

public class TabletSyncTest extends UiAutomatorTestCase {

	public void testTabltSyncTest() throws UiObjectNotFoundException,
			RemoteException {

		// creates the UI device object to use for User Inputs on screen
		UiDevice device = getUiDevice();
		device.pressHome();
		// device.pressRecentApps(); in case not already in PlaceIt app will
		// find in recents

		// object for Place-it app made to find in recents menu
		UiObject placeIt = new UiObject(new UiSelector().className(
				"android.widget.TextView").text("PlaceIt"));

		placeIt.clickAndWaitForNewWindow();

		/*
		 * Scenario: User creates a reminder on their tablet and sees it on
		 * their phone Given: The user is logged in to the same Place-Its
		 * account on their tablet and smartphone When: The user creates a
		 * reminder on their tablet Then: The user can see the newly created
		 * reminder on their smartphone
		 * 
		 * NOTE: Only tablet portion of the test will be conducted here, the
		 * smartphone portion is conducted under SmartphoneSyncTest.java
		 */

		UiObject newCatButton = new UiObject(new UiSelector().className(
				"android.widget.Button").text("New Category PlaceIt"));

		newCatButton.clickAndWaitForNewWindow();

		UiObject enterText = new UiObject(new UiSelector().text("task name..")
				.className("android.widget.EditText"));

		UiObject text = new UiObject(new UiSelector().className(
				"android.widget.EditText").index(0));

		UiObject firstCategory = new UiObject(
				new UiSelector()
						.className("android.widget.LinearLayout")
						.index(6)
						.childSelector(
								new UiSelector()
										.className(
												"android.widget.LinearLayout")
										.index(0)
										.childSelector(
												new UiSelector()
														.className("android.widget.Spinner"))));

		enterText.setText("This reminder is from the tablet");
		text.click();
		text.setText("This provides a test for device synchronization");
		firstCategory.click();

		UiObject chooseFifthCategory = new UiObject(new UiSelector().className(
				"android.widget.CheckedTextView").index(5));

		chooseFifthCategory.click();

		UiObject subButton = new UiObject(new UiSelector().className(
				"android.widget.Button").text("Submit"));

		subButton.clickAndWaitForNewWindow();

		// Verification of correct next screen
		assertFalse("No category reminder created", subButton.exists());

		UiObject listButton = new UiObject(new UiSelector().className(
				"android.widget.Button").text("PlaceIt List"));

		// Wait for 30 seconds to allow smartphone to verify new reminder
		listButton.waitForExists(30000);

		/*
		 * Scenario: User marks as completed a reminded from tablet Given: The
		 * user is logged in to the same Place-Its account on their tablet and
		 * smartphone When: The user marks a reminder as completed on their
		 * tablet Then: The user will see the reminder as completed on their
		 * smartphone
		 * 
		 * NOTE: Only tablet portion of the test will be conducted here, the
		 * smartphone portion is conducted under SmartphoneSyncTest.java
		 */

		listButton.clickAndWaitForNewWindow();

		UiObject catListButton = new UiObject(new UiSelector().className(
				"android.widget.TextView").text("Category Place-It"));
		catListButton.waitForExists(30000);
		catListButton.click();

		UiObject reminderButton = new UiObject(new UiSelector().className(
				"android.widget.TextView").text(
				"This reminder is from the tablet"));

		reminderButton.clickAndWaitForNewWindow();

		UiObject hitPlaceIt = new UiObject(new UiSelector().className(
				"android.widget.TextView").text("You've hit a reminder: This reminder is from the tablet"));

		// Verification of correct next screen
		assertTrue("PlaceIt was not tapped", hitPlaceIt.exists());

		UiObject completePlaceIt = new UiObject(new UiSelector().className(
				"android.widget.Button").text("Mark Complete"));

		completePlaceIt.clickAndWaitForNewWindow();

		// Verification of correct next screen
		assertTrue("PlaceIt was not marked completed", catListButton.exists());

		// Wait for 30 seconds to allow smartphone to verify reminder was
		// completed
		catListButton.waitForExists(30000);

		/*
		 * Scenario: User repost a reminder from smartphone Given: The user is
		 * logged in to the same Place-Its account on their tablet and
		 * smartphone When: The user reposts a reminder on their smartphone
		 * Then: The user will continue to see the reminder on their tablet
		 * 
		 * NOTE: Only tablet portion of the test will be conducted here, the
		 * smartphone portion is conducted under SmartphoneSyncTest.java
		 */

		catListButton.click();

		UiObject compReminder = new UiObject(new UiSelector().className(
				"android.widget.TextView").text("Completed"));

		// Wait for 30 seconds for database to update reminder recently deleted
		compReminder.waitForExists(30000);

		UiObject activeReminder = new UiObject(new UiSelector().className(
				"android.widget.TextView").text("Completed"));

		// Verification that reminder completed is still present
		assertTrue("Reminder was not reposted", activeReminder.exists());

		/*
		 * Scenario: User deletes a reminder from smartphone Given: The user is
		 * logged in to the same Place-Its account on their tablet and
		 * smartphone When: The user deletes a reminder on their smartphone
		 * Then: The user will no longer see the reminder on their tablet
		 * 
		 * NOTE: Only tablet portion of the test will be conducted here, the
		 * smartphone portion is conducted under SmartphoneSyncTest.java
		 */

		// Wait for 30 seconds for database to update reminder recently deleted
		activeReminder.waitForExists(30000);

		// Verification that reminder deleted on smartphone does not appear on
		// tablet anymore
		assertFalse("Reminder on smartphone was not deleted in database",
				!activeReminder.exists());

		/*
		 * Scenario: User creates a future date reminder on tablet Given: The
		 * user is logged in to the same Place-Its account on their tablet and
		 * smartphone When: The user creates a future date reminder on their
		 * tablet Then: The user will not see the reminder on their smartphone
		 * on that day, but reminder will be saved on the smartphone for future
		 * posting
		 * 
		 * NOTE: Only tablet portion of the test will be conducted here, the
		 * smartphone portion is conducted under SmartphoneSyncTest.java
		 */

		UiObject backButton = new UiObject(new UiSelector().className(
				"android.widget.Button").text("Go Back"));

		backButton.clickAndWaitForNewWindow();

		newCatButton.clickAndWaitForNewWindow();

		enterText.setText("Future reminder");
		text.click();
		text.setText("This reminder will not show today");
		firstCategory.click();

		UiObject chooseCategory = new UiObject(new UiSelector().className(
				"android.widget.CheckedTextView").index(7));

		chooseCategory.click();
		subButton.clickAndWaitForNewWindow();

		// Verification of correct next screen
		assertFalse("No future reminder created", subButton.exists());

	}
}
