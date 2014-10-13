package edu.ucsd.placeit.fragment;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import edu.ucsd.placeit.R;
import edu.ucsd.placeit.db.DatabaseHelper;
import edu.ucsd.placeit.model.PlaceIt;
import edu.ucsd.placeit.model.impl.CategoricalPlaceIt;
import edu.ucsd.placeit.service.OnFragmentEventListener;
import edu.ucsd.placeit.service.handler.VersionHandler;
import edu.ucsd.placeit.util.Consts;
import edu.ucsd.placeit.util.GooglePlacesTypes;

public class NewCategoryPlaceIt extends Fragment {

	private OnFragmentEventListener mListener;
	private DatabaseHelper database;
	private EditText input_title;
	private EditText input_description;
	private DatePicker date_picker;
	private Spinner first_cat;
	private Spinner second_cat;
	private Spinner third_cat;
	// private Spinner repost_option;
	private Button submit_btn;
	private Button cancel_btn;
	private String user;

	// NewCategoryPlaceIt fragment life cycle
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Get view and retrieve bundles
		View view = inflater.inflate(R.layout.new_category_placeit, container,
				false);
		Bundle extras = getArguments();
		user = extras.getString(Consts.USER_LOGGED_IN);

		// Create handles to UI components
		initializeComponents(view);
		database = new DatabaseHelper(getActivity());
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mListener = (OnFragmentEventListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnArticleSelectedListener");
		}
	}

	// On click listeners setup -------------------------------
	public OnClickListener onSubmitClick = new OnClickListener() {
		public void onClick(View v) {
			// If invalid, validateInput() will alert user
			if (validateInput()) {
				PlaceIt pi = createNewPlaceIt();
				database.createPlaceIt(pi);
				database.closeDB();
				//Handler the version
				VersionHandler versionHandler = new VersionHandler(getActivity());
				versionHandler.handleConflicts();
				mListener.onFragmentEvent(Consts.MAIN_NEW_CAT_SUCCESS, null);
			}
		}
	};
	public OnClickListener onCancelClick = new OnClickListener() {
		public void onClick(View view) {
			mListener.onFragmentEvent(Consts.MAIN_NEW_CAT_RETURN, null);
		}
	};

	// Private helper methods
	private void initializeComponents(View view) {
		// initialize buttons
		input_title = (EditText) view.findViewById(R.id.new_task_name);
		input_description = (EditText) view
				.findViewById(R.id.new_task_description);
		date_picker = (DatePicker) view
				.findViewById(R.id.new_task_effective_date);
		first_cat = (Spinner) view.findViewById(R.id.new_task_category_1);
		second_cat = (Spinner) view.findViewById(R.id.new_task_category_2);
		third_cat = (Spinner) view.findViewById(R.id.new_task_category_3);
		submit_btn = (Button) view.findViewById(R.id.new_task_submit);
		cancel_btn = (Button) view.findViewById(R.id.new_task_cancel);
		// repost_option = (Spinner) view.findViewById(R.id.new_task_repost);

		// Initialize first category spinner
		initializeCategorySpinner(first_cat, R.id.new_task_category_1, view);
		initializeCategorySpinner(second_cat, R.id.new_task_category_2, view);
		initializeCategorySpinner(third_cat, R.id.new_task_category_3, view);

		// Initialize repost spinner
		// repost_option = (Spinner) view.findViewById(R.id.new_task_repost);
		// ArrayAdapter<CharSequence> adapter_repost =
		// ArrayAdapter.createFromResource(getActivity(),
		// R.array.repost_options, android.R.layout.simple_spinner_item);
		// adapter_repost.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// repost_option.setAdapter(adapter_repost);

		// Set onclick listeners for submit/cancel buttons
		submit_btn.setOnClickListener(onSubmitClick);
		cancel_btn.setOnClickListener(onCancelClick);
	}

	private void initializeCategorySpinner(Spinner category, int resourceID,
			View view) {
		// Get all the categories
		ArrayList<String> categories = new ArrayList<String>();
		categories.add("None");
		categories.addAll((ArrayList<String>) GooglePlacesTypes.CATEGORIES);

		// Assign to dropdown list
		category = (Spinner) view.findViewById(resourceID);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, categories);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		category.setAdapter(adapter);
	}

	private boolean validateInput() {
		boolean categoryOk = false;
		if (first_cat.getSelectedItemPosition() > 0)
			categoryOk = true;
		else if (second_cat.getSelectedItemPosition() > 0)
			categoryOk = true;
		else if (third_cat.getSelectedItemPosition() > 0)
			categoryOk = true;

		if (input_title.getText().length() > 0 && categoryOk)
			return true;
		else {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			// Empty title, alert user
			if (input_title.getText().length() == 0)
				builder.setMessage(R.string.invalid_input).setPositiveButton(
						"OK", null);
			else
				builder.setMessage(R.string.invalid_category)
						.setPositiveButton("OK", null);
			builder.show();
			return false;
		}
	}

	private PlaceIt createNewPlaceIt() {
		PlaceIt pi = null;
		int state;

		// Get basic strings
		String title = input_title.getText().toString();
		String description = input_description.getText().toString();

		// Get category
		String cat_1 = first_cat.getSelectedItem().toString();
		String cat_2 = second_cat.getSelectedItem().toString();
		String cat_3 = third_cat.getSelectedItem().toString();
		String categories[] = { cat_1, cat_2, cat_3 };

		// Get date
		int year = date_picker.getYear();
		int month = date_picker.getMonth();
		int day = date_picker.getDayOfMonth();
		Calendar now = new GregorianCalendar();
		Calendar post = new GregorianCalendar(year, month, day);

		// Get state (
		if (post.before(now)) {
			state = Consts.ACTIVE;
			post = now;
		} else {
			state = Consts.SLEEP;
		}

		
		pi = new CategoricalPlaceIt(user, title, description, state,
				now.getTime(), post.getTime(), categories);

		return pi;
	}

}
