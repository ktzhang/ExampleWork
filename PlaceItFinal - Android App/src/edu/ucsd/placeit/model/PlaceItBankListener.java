package edu.ucsd.placeit.model;

public interface PlaceItBankListener {
	/**
	 * Called every time the bank has been changed and will pass in a iterator
	 * 
	 * @param placeItId
	 *            placeIt id that is changed
	 * 
	 * @param updateState
	 * 			  refer to the Consts.java for more info
	 * 
	 * @param options
	 * 			  additional parameters to pass in, eg state
	 */
	public void onPlaceItBankChange(int placeItId, int updateState, int options);
}
