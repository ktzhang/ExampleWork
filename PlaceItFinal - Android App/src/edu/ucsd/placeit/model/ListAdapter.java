package edu.ucsd.placeit.model;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.ucsd.placeit.R;
import edu.ucsd.placeit.util.Consts;

public class ListAdapter extends ArrayAdapter<PlaceItItem>{
	private int layoutResourceId;
	private Context context;
	private ArrayList<PlaceItItem> data = null;
	
	public ListAdapter(Context context, int resource) {
		super(context, resource);
		// TODO Auto-generated constructor stub
	}
	public ListAdapter(Context context, int layoutResourceId, ArrayList<PlaceItItem> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View row = convertView;
		
		if(row == null){
			LayoutInflater inflater = ((Activity)context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);
		}
		// Set UI handler for each view
		TextView title = (TextView) row.findViewById(R.id.listViewTitle);
		TextView id = (TextView) row.findViewById(R.id.listViewId);
		TextView state = (TextView) row.findViewById(R.id.listViewState);
		
		PlaceItItem pi = data.get(position);
		title.setText(pi.getTitle());
		id.setText(pi.getId());
		
		int intState = pi.getState();
		String strState = null;
		if(intState == Consts.ACTIVE) strState = "Active";
		else if(intState == Consts.COMPLETE) strState = "Completed";
		else if(intState == Consts.SLEEP) strState = "Inactive";
		else strState = "Unknown";
		
		state.setText(strState);
		return row;
	}

}
