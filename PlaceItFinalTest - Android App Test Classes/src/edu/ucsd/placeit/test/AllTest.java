package edu.ucsd.placeit.test;

import android.os.RemoteException;

import com.android.uiautomator.testrunner.UiAutomatorTestCase;
import com.android.uiautomator.core.*;

public class AllTest extends UiAutomatorTestCase {
	
	/*
	/**
     * Launches an app by it's name. 
     * 
     * Code source: http://blog.bettersoftwaretesting.com/2013/03/android-test-automation-getting-to-grips-with-ui-automator/
     * 
     * @param nameOfAppToLaunch the localized name, an exact match is required to launch it.
     */
	/*   protected static void launchAppCalled(String PlaceIt) throws UiObjectNotFoundException {
        UiScrollable appViews = new UiScrollable(new UiSelector().scrollable(true));
          // Set the swiping mode to horizontal (the default is vertical)
          appViews.setAsHorizontalList();
          appViews.scrollToBeginning(10);  // Otherwise the Apps may be on a later page of apps.
          int maxSearchSwipes = appViews.getMaxSearchSwipes();

          UiSelector selector;
          selector = new UiSelector().className(android.widget.TextView.class.getName());
          
          UiObject appToLaunch;
          
          // The following loop is to workaround a bug in Android 4.2.2 which
          // fails to scroll more than once into view.
          for (int i = 0; i < maxSearchSwipes; i++) {

              try {
                  appToLaunch = appViews.getChildByText(selector, PlaceIt);
                  if (appToLaunch != null) {
                      // Create a UiSelector to find the Settings app and simulate      
                      // a user click to launch the app.
                      appToLaunch.clickAndWaitForNewWindow();
                      break;
                  }
              } catch (UiObjectNotFoundException e) {
                  System.out.println("Did not find match for " + e.getLocalizedMessage());
              }

              for (int j = 0; j < i; j++) {
                  appViews.scrollForward();
                  System.out.println("scrolling forward 1 page of apps.");
              }
          }
    }
*/
	
	public void CreateLocRemTest() throws UiObjectNotFoundException, RemoteException {
		
		//creates the UI device object to use for User Inputs on screen
		UiDevice device = getUiDevice();
		device.pressHome();
		//device.pressRecentApps(); in case not already in PlaceIt app will find in recents
	
		//object for Place-it app made to find in recents menu
		UiObject placeIt = new UiObject(new UiSelector() 
			.className("android.widget.TextView")
			.text("Place it"));
	
		//open Place-it
		placeIt.clickAndWaitForNewWindow();
		
		
		
	}
}
