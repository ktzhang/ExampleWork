package edu.ucsd.placeit.db.util;

import edu.ucsd.placeit.util.Cfg;

public class PlaceItsUrlBuilder implements IUrlBuilder {

	@Override
	public String buildUrl() {
		String url = Cfg.PRODUCT_API;
		url += "?action=getPlaceIts";
		return url;
	}

}
