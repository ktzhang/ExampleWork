package edu.ucsd.placeit.model;

public class PlaceItItem {
	String id;
	String title;
	int state;
	
	public PlaceItItem(String id, String title, int state){
		this.id = id;
		this.title = title;
		this.state = state;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public int getState() {
		return state;
	}

}