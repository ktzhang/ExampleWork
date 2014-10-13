package edu.ucsd.placeit.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import edu.ucsd.placeit.R;
import edu.ucsd.placeit.db.DatabaseHelper;
import edu.ucsd.placeit.model.GoogleMapData;
import edu.ucsd.placeit.model.ListAdapter;
import edu.ucsd.placeit.model.PlaceIt;
import edu.ucsd.placeit.model.PlaceItItem;
import edu.ucsd.placeit.service.OnFragmentEventListener;
import edu.ucsd.placeit.util.Consts;


/* Improvement needed:
 * Add clickable events to each PlaceIt  DONE
 * 
 */
public class PlaceItListFragment extends Fragment{
	//List view UIs and adapters
	private ListView locationList;
	private ListView categoryList;
	private ListView recurringList;
	private ListAdapter locationAdapter;
	private ListAdapter categoryAdapter;
	private ListAdapter recurringAdapter;

	private DatabaseHelper database;
	private TabHost tabHost;
	private Button backButton;
	private OnFragmentEventListener mListener;
	private String username;
	
// UI Interaction Listener and methods
	public void refreshAllLists(){
		locationAdapter = null;
		categoryAdapter = null;
		recurringAdapter = null;
		initializeListViews();
	}
	
	
// Fragment LifeCycle Management Section ------------------ 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Get view and retrieve bundles
		View view = inflater.inflate(R.layout.placeit_list, container, false);
		Bundle bundle = getArguments();
		username = bundle.getString(Consts.USER_LOGGED_IN);
		// Initialize components
		initialization(view);
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
	
	
// Private helper functions such as initialization.. ------
	private void initialization(View view){
		tabHost = (TabHost) (view.findViewById(R.id.myTabhost));
		tabHost.setup();
		tabHost.addTab(tabHost.newTabSpec("LocationList").setIndicator("Location Place-It").setContent(R.id.locationTab));
		tabHost.addTab(tabHost.newTabSpec("CategoryList").setIndicator("Category Place-It").setContent(R.id.categoryTab));
		tabHost.addTab(tabHost.newTabSpec("RecurringList").setIndicator("Recurring Place-It").setContent(R.id.recurringTab));
		
		backButton = (Button) (view.findViewById(R.id.listViewBackBtn));
		categoryList = (ListView) (view.findViewById(R.id.category_list));
		locationList = (ListView) (view.findViewById(R.id.location_list));
		recurringList = (ListView) (view.findViewById(R.id.recurring_list));
		
		// Assign onclick listener
		backButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View view) {
				mListener.onFragmentEvent(Consts.MAIN_RETURN_FROM_VIEW, null);
			}
		});
		
		initializeListViews();
	}
	private void initializeListViews(){
		//populate list
		database = new DatabaseHelper(getActivity());
		locationAdapter = populateListView(locationList, Consts.TYPE_NORMAL);
		categoryAdapter = populateListView(categoryList, Consts.TYPE_CATEGORICAL);
		recurringAdapter = populateListView(recurringList, Consts.TYPE_RECURRING);
		database.closeDB();
		
		// Assign on click listener to each row in list
		setClickEventHandler(locationList, locationAdapter);
		setClickEventHandler(categoryList, categoryAdapter);
		setClickEventHandler(recurringList, recurringAdapter);
	}
	
	private ListAdapter populateListView(ListView listView, int type){
		Log.d("Frankie", "Populating list type=" + String.valueOf(type));
		
		ArrayList<PlaceIt> pis = (ArrayList<PlaceIt>) database.getAllPlaceIts(0, null, type, username);
		Log.d("Frankie","Retrieved List size = " + String.valueOf(pis.size()));
		
		ArrayList<PlaceItItem> input = new ArrayList<PlaceItItem>();
		for(int i = 0; i < pis.size(); i++){
			String id = String.valueOf(pis.get(i).getId());
			String title = pis.get(i).getTitle();
			int state = pis.get(i).getState();
			PlaceItItem pi = new PlaceItItem(id, title, state);
			input.add(pi);
		}
						
		ListAdapter adapter = new ListAdapter(getActivity(), R.layout.list_view_row, input);
		listView.setAdapter(adapter);
		return adapter;
	}
	
	private void setClickEventHandler(ListView list, final ListAdapter adapter){
		list.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
				Log.d("frankie","lol, clicked on an item");
				PlaceItItem pi =  adapter.getItem(position);
				Log.d("Frankie","Inside click listener");
				//Construct GoogleMapData then tell main to popup alert
				HashMap<String,String> info = new HashMap<String,String>();
				info.put(Consts.DATA_PLACEIT_ID, pi.getId());
				info.put(Consts.DATA_PLACEIT_TITLE, pi.getTitle());
				Log.d("Frankie", "Created hashMap");
				GoogleMapData data = new GoogleMapData(null,info);
				Log.d("Frankie", "lol, clicked on an item, now tell main to alert it to send it to main");
				mListener.onFragmentEvent(Consts.MAIN_MARKER_CLICK, data);
			}
		});
	}
	
}
