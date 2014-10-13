package edu.ucsd.placeit.db.util;
/**
 * Class to generate url builders for the get http request
 * @author Kevin
 *
 */
public class GetRequestFactory {
	/**
	 * Creates a builder based on the string input passed in
	 * @param type
	 * @return
	 */
	public IUrlBuilder createBuilder(String type) {
		IUrlBuilder urlBuilder;
		if (type.equalsIgnoreCase("placeIts")) {
			urlBuilder = new PlaceItsUrlBuilder();
		} else if (type.equalsIgnoreCase("version")) {
			urlBuilder = new VersionUrlBuilder();
		} else {
			urlBuilder = null;
		}

		return urlBuilder;
	}
}
