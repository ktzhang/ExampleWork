package edu.ucsd.placeit.model;


public interface PlaceItBankSubject {
	
	/**
	 * Registers listeners to the interface.
	 * 
	 * @param pListener the listener that needs to be added
	 */
	public void registerListener(PlaceItBankListener pListener);
	
	/**
	 * Will notify all the listeners by calling the updatePlaceit function.
	 */
	public void notifyListeners(int placeItId, int updateState, int options);
	
	/**
	 * Removes a listener from the subject.
	 * 
	 * @param pListener	the listener that needs to be removed
	 */
	public void removeListener(PlaceItBankListener pListener);

}
