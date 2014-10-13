package edu.ucsd.placeit.util;

/**
 * Class to hold all of the configurable information
 * 
 * @author Kevin
 * 
 */
public class Cfg {

	// ----- Google Places API Information -----\\
//	public static String GOOGLE_PlACES_KEY = "AIzaSyD6Cl1PHHGpqd1B2MEBHtL-vb5DVq9fyUU";
//	public static String GOOGLE_PlACES_KEY = "AIzaSyDFrT57O1tdIKCfkxQWOJ-D8mCckou2qKE";
	public static String GOOGLE_PlACES_KEY = "AIzaSyB4lIenZWPo8Y0MOmAIdYNwG5YYZfQrbFY";
	public static boolean GOOGLE_PLACES_SENSOR = true;
	// -----------------------------------------\\

	// ----- PlaceIt Config -----\\
	public static final float PLACEIT_RADIUS = 50;
	public static final float CATEGORY_RADIUS = 50;
	// --------------------------\\

	// ----- Notification Config -----\\
	public static final boolean NOTIFY_ONLY_BACKGROUND = true;
	// -------------------------------\\

	// ----- Google APP Engine -----\\
	public static final String PRODUCT_API = "http://ktzluke10.appspot.com/product";
	// ------------------------------\\

	// ----- Version stuff ----- \\
	// Time difference of version allowed in milliseconds
	public static final long VERSION_RANGE = 0;
	public static final int SYNC_DB_INTERVAL = 10;
	public static final boolean SHOW_CAT_LOG = false;

	// -------------------------- \\
	private Cfg() {
		throw new AssertionError();
	}
}
