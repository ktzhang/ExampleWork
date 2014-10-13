package edu.ucsd.placeit.test;

import android.os.RemoteException;

import com.android.uiautomator.testrunner.UiAutomatorTestCase;
import com.android.uiautomator.core.*;

public class CategoryPlTest extends UiAutomatorTestCase {

	public void testCreateReminder() throws UiObjectNotFoundException,
			RemoteException {

		// creates the UI device object to use for User Inputs on screen
		UiDevice device = getUiDevice();
		device.pressHome();
		// device.pressRecentApps(); in case not already in PlaceIt app will
		// find in recents

		// object for Place-it app made to find in recents menu
		UiObject placeIt = new UiObject(new UiSelector().className(
				"android.widget.TextView").text("PlaceIt"));

		// open Place-it
		placeIt.clickAndWaitForNewWindow();

		/*
		 * Scenario: User creates reminder using a single category of a location
		 * Given: That user is using Place-its, is logged in, and is creating a
		 * reminder When: The user chooses a single category (e.g. Grocery
		 * Store) Then: A reminder is created for all locations of that category
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
		// .resourceId("edu.ucsd.placeit:id/new_task_category_1"));

		enterText.setText("Get groceries");
		text.click();
		text.setText("Almond milk, pinto beans, brown rice, oranges, apples");

		firstCategory.click();

		UiObject chooseFirstCategory = new UiObject(new UiSelector().className(
				"android.widget.CheckedTextView").index(1));

		chooseFirstCategory.click();

		UiObject subButton = new UiObject(new UiSelector().className(
				"android.widget.Button").text("Submit"));

		subButton.click();
		subButton.waitUntilGone(30000);
		

		// Verification of correct next screen
		assertFalse("No category reminder created", subButton.exists());

		UiObject listButton = new UiObject(new UiSelector().className(
				"android.widget.Button").text("PlaceIt List"));

		listButton.clickAndWaitForNewWindow();

		UiObject catListButton = new UiObject(new UiSelector().className(
				"android.widget.TextView").text("Category Place-It"));

		catListButton.clickAndWaitForNewWindow();

		UiObject groceriesReminder = new UiObject(new UiSelector().className(
				"android.widget.TextView").text("Get groceries"));

		// Verification newly created reminder
		assertTrue("No category reminder created in database",
				groceriesReminder.exists());

		UiObject backButton = new UiObject(new UiSelector().className(
				"android.widget.Button").text("Go Back"));

		backButton.clickAndWaitForNewWindow();

		/*
		 * Scenario: User creates reminder not using a category of a location
		 * Given: That user is using Place-its, is logged in, and is creating a
		 * reminder When: The user does not choose a category Then: The user
		 * will be asked to select at least one location
		 */
//
		newCatButton.clickAndWaitForNewWindow();
		enterText.setText("Get cash");
		text.click();
		text.setText("Soon will be cash only! get enough cash");
		subButton.clickAndWaitForNewWindow();

		UiObject noCategory = new UiObject(new UiSelector().className(
				"android.widget.TextView").text(
				"Please select at leaset one category."));

		// Verification of request for at least one category from user
		assertTrue("No error for blank category", noCategory.exists());

		UiObject okButton = new UiObject(new UiSelector().className(
				"android.widget.Button").text("OK"));

		okButton.clickAndWaitForNewWindow();

		/*
		 * Scenario: User creates reminder using more than one category of a
		 * location Given: That user is using Place-its and is creating a
		 * reminder When: The user selects more than one category for the
		 * place-it (up to three) Then: A reminder will be created with the
		 * categories selected
		 */

		firstCategory.click();
		chooseFirstCategory.click();

		UiObject secondCategory = new UiObject(
				new UiSelector()
						.className("android.widget.LinearLayout")
						.index(6)
						.childSelector(
								new UiSelector()
										.className(
												"android.widget.LinearLayout")
										.index(1)
										.childSelector(
												new UiSelector()
														.className("android.widget.Spinner"))));

		secondCategory.click();

		UiObject chooseSecondCategory = new UiObject(new UiSelector()
				.className("android.widget.CheckedTextView").index(2));

		chooseSecondCategory.click();
		
		UiObject thirdCategory = new UiObject(
				new UiSelector()
						.className("android.widget.LinearLayout")
						.index(6)
						.childSelector(
								new UiSelector()
										.className(
												"android.widget.LinearLayout")
										.index(2)
										.childSelector(
												new UiSelector()
														.className("android.widget.Spinner"))));


		UiObject chooseThirdCategory = new UiObject(new UiSelector().className(
				"android.widget.CheckedTextView").index(3));

		thirdCategory.click();
		chooseThirdCategory.click();
		subButton.clickAndWaitForNewWindow();

		// Verification of correct next screen
		assertFalse("No 3 category reminder created", subButton.exists());

		listButton.clickAndWaitForNewWindow();
		catListButton.click();

		UiObject cashReminder = new UiObject(new UiSelector().className(
				"android.widget.TextView").text("Get cash"));

		// Verification 3 category newly created reminder
		assertTrue("No (3) category reminder created in database",
				cashReminder.exists());

		backButton.clickAndWaitForNewWindow();

	}

}
