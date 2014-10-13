package edu.ucsd.placeit.model;

import java.util.Iterator;


public interface PlaceItIterable {
	public Iterator<PlaceIt> iterator();
	public Iterator<PlaceIt> iterator(int state);
}
