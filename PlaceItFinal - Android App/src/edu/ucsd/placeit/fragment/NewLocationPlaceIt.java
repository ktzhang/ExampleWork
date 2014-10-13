package edu.ucsd.placeit.fragment;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.maps.model.LatLng;

import edu.ucsd.placeit.R;
import edu.ucsd.placeit.db.DatabaseHelper;
import edu.ucsd.placeit.model.GoogleMapData;
import edu.ucsd.placeit.model.PlaceIt;
import edu.ucsd.placeit.model.impl.NormalPlaceIt;
import edu.ucsd.placeit.model.impl.RecurringPlaceIt;
import edu.ucsd.placeit.service.OnFragmentEventListener;
import edu.ucsd.placeit.service.handler.VersionHandler;
import edu.ucsd.placeit.util.Consts;

public class NewLocationPlaceIt extends Fragment {
// Local class variables
	private OnFragmentEventListener mListener;
	private DatabaseHelper database;
	private LatLng location;
	private Button submit_btn;
	private Button cancel_btn;
	private EditText input_title;
	private EditText input_description;
	private DatePicker date_picker;
	private Spinner repost_option;
	private String user;
// Fragment Life Cycle Managements ------------------------
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Get view and retrieve bundles
		View view = inflater.inflate(R.layout.new_location_placeit, container, false);
		Bundle extras = getArguments();
		location = extras.getParcelable(Consts.MAIN_BUNDLE_LOCATION);
		user = extras.getString(Consts.USER_LOGGED_IN);
		// Create handles to UI components
		initializeComponents(view);
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
	
	
// On click listeners setup -------------------------------
	public OnClickListener onSubmitClick = new OnClickListener(){
		public void onClick(View v){
		// If invalid, validateInput() will alert user
		if(validateInput()){
			PlaceIt pi = createNewPlaceIt();
			int newId = database.createPlaceIt(pi);
			database.closeDB();
			//Handle the version
			VersionHandler versionHandler = new VersionHandler(getActivity());
			versionHandler.handleConflicts();
			GoogleMapData result = gatherInputData(pi,newId);
			mListener.onFragmentEvent(Consts.MAIN_NEW_LOC_SUBMIT, result);
		}
	}};
	public OnClickListener onCancelClick = new OnClickListener(){
		public void onClick(View view) {
			mListener.onFragmentEvent(Consts.MAIN_NEW_LOC_CANCEL, null);
		}
	};
	

// Private helper methods ---------------------------------
	
	private void initializeComponents(View view){
		submit_btn = (Button)(view.findViewById(R.id.new_task_submit));
		cancel_btn = (Button)(view.findViewById(R.id.new_task_cancel));
		input_title = (EditText)(view.findViewById(R.id.new_task_name));
		input_description = (EditText)(view.findViewById(R.id.new_task_description));
		date_picker = (DatePicker) (view.findViewById(R.id.new_task_effective_date));
		repost_option = (Spinner)(view.findViewById(R.id.new_task_repost));

        // Initialize Spinner
        repost_option = (Spinner) view.findViewById(R.id.new_task_repost);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.repost_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repost_option.setAdapter(adapter);

		// Set on click listener for submit and cancel button
		submit_btn.setOnClickListener(onSubmitClick);	
		cancel_btn.setOnClickListener(onCancelClick);
		
		// Initialize database helper object
		database = new DatabaseHelper(getActivity());
	}

	private boolean validateInput(){
		if(input_title.getText().length() > 0)
			return true;
		else{
            // Empty title, alert user
        	AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        	builder.setMessage("Please enter a title").setPositiveButton("OK", null);
        	builder.show();
			return false;
		}
	}
	
	private PlaceIt createNewPlaceIt(){
		PlaceIt pi = null;
		String title, description;
		int state, frequency;

		title = input_title.getText().toString();
		description = input_description.getText().toString();
		
		// Get date
		int year = date_picker.getYear();
		int month = date_picker.getMonth();
		int day = date_picker.getDayOfMonth();
		Calendar now = new GregorianCalendar();
		Calendar post = new GregorianCalendar(year,month,day);
		
		// Get state (
		if(post.before(now)){
			state = Consts.ACTIVE;
			post = now;
		}
		else {
			state = Consts.SLEEP;
		}
		
		// Determine which type of PlaceIt to create
		frequency = repost_option.getSelectedItemPosition();
		// Normal Location PlaceIt
		if(frequency == 0){
			pi = new NormalPlaceIt(user, title, description, state,
					location, now.getTime() , post.getTime());
		}
		// Recurring Location PlaceIt
		else{
			pi = new RecurringPlaceIt(user, title, description, state, location,
					now.getTime(), post.getTime(), frequency);
		}
		return pi;
	}
	
	private GoogleMapData gatherInputData(PlaceIt pi, int piId){
		GoogleMapData result = null;
		String frequency;
		
		// Get frequency info
		if(pi instanceof NormalPlaceIt){
			frequency = "false";
		}
		else{
			int freq = ((RecurringPlaceIt)pi).getFrequency();	
			frequency = String.valueOf(freq);
		}
		
		HashMap<String,String> info = new HashMap<String, String>();
		info.put(Consts.DATA_PLACEIT_TITLE, pi.getTitle());
		info.put(Consts.DATA_PLACEIT_ID, String.valueOf(piId));
		info.put(Consts.DATA_PLACEIT_DESCRIPTION, pi.getDesc());
		info.put(Consts.DATA_PLACEIT_STATE, String.valueOf(pi.getState()));
		info.put(Consts.DATA_PLACEIT_DATE, String.valueOf(pi.getPostDate().getTime()));
		info.put(Consts.DATA_PLACEIT_FREQUENCY, frequency);
		
		result = new GoogleMapData(location, info);
		return result;
	}
}
