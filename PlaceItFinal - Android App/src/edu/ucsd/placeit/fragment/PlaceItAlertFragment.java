package edu.ucsd.placeit.fragment;

import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import edu.ucsd.placeit.R;
import edu.ucsd.placeit.db.DatabaseHelper;
import edu.ucsd.placeit.model.GoogleMapData;
import edu.ucsd.placeit.model.PlaceIt;
import edu.ucsd.placeit.service.OnFragmentEventListener;
import edu.ucsd.placeit.util.Consts;

public class PlaceItAlertFragment extends Fragment {

	private OnFragmentEventListener mListener;
	private String id;
	private String title;
	private String description;
	private int state;
	
// Fragment Life Cycle Management	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.empty_layout, container, false);
		Bundle extras = getArguments();
		id = extras.getString(Consts.DATA_PLACEIT_ID);
		title = extras.getString(Consts.DATA_PLACEIT_TITLE);
		DatabaseHelper db = new DatabaseHelper(getActivity());
		PlaceIt pi = db.getPlaceIt(Integer.valueOf(id));
		db.closeDB();
		// Get information about place it
		description = pi.getDesc();
		state = pi.getState();
		if(description.length() == 0) description = "There's no description...";
		Log.d("Frankie","lol, description is: "+description);
		displayAlert();
		return view;
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnArticleSelectedListener");
        }
    }

// Displaying the alert
	private void displayAlert(){
		String alertTitle = "You've hit a reminder: " + title;
		String message = description;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(alertTitle);
		builder.setMessage(message);
		
		builder.setPositiveButton(R.string.mark_complete, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int vid){
				HashMap<String,String> info = new HashMap<String,String>();
				info.put(Consts.DATA_PLACEIT_ID, id);
				GoogleMapData result = new GoogleMapData(null, info);
				mListener.onFragmentEvent(Consts.MAIN_ALERT_MARK_COMPLETE, result);
			}
		});
		builder.setNegativeButton(R.string.do_delete, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int vid){
				HashMap<String,String> info = new HashMap<String,String>();
				info.put(Consts.DATA_PLACEIT_ID, id);
				GoogleMapData result = new GoogleMapData(null, info);
				mListener.onFragmentEvent(Consts.MAIN_ALERT_MARK_DELETE, result);
			}
		});
		builder.setNeutralButton(R.string.do_repost, new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int Vid){
				HashMap<String,String> info = new HashMap<String,String>();
				info.put(Consts.DATA_PLACEIT_ID, id);
				info.put(Consts.DATA_PLACEIT_STATE, String.valueOf(state));
				GoogleMapData result = new GoogleMapData(null, info);
				mListener.onFragmentEvent(Consts.MAIN_ALERT_MARK_REPOST, result);
			}
		});
		builder.create().show();
	}

}
