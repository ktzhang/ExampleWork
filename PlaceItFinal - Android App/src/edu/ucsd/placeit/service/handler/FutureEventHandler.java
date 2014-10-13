package edu.ucsd.placeit.service.handler;

import java.util.Calendar;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import edu.ucsd.placeit.model.GoogleMapData;
import edu.ucsd.placeit.util.Consts;

public class FutureEventHandler {
	private Activity activity;
	private AlarmManager am;
	private PendingIntent pendingIntent;
	
	
	public FutureEventHandler(Activity activity){
		this.activity = activity;
		am = (AlarmManager)activity.getSystemService(Context.ALARM_SERVICE);
	}
	
	public void assignFutureTask(GoogleMapData data){
		String postTimeStr = data.getData().get(Consts.DATA_PLACEIT_DATE);
		long postTime = Long.valueOf(postTimeStr);

		Bundle bundle = packPopupAlertBundle(data);
		bundle.putString(Consts.ALARM_ACTION, Consts.ALARM_DISPLAY_MARKER);
		Intent intent = new Intent(Consts.INTENT_DELAYED_NAME);
		intent.putExtras(bundle);
//		Log.d("Frankie", "about to send pending intnet");
//		Log.d("Frankie", "Packing title = " + bundle.getString(Consts.DATA_PLACEIT_TITLE));
//		Log.d("Frankie", "Packing it = "  + bundle.getString(Consts.DATA_PLACEIT_ID));
        sendPendingIntent(intent, postTime);
	}
	public void assignRepeatingTask(GoogleMapData data, String recurring){
		HashMap<String,String> info = data.getData();
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		int frequency = Integer.valueOf(recurring);
		long timeInterval = frequency * 60 * 1000;
		long postTime = calendar.getTimeInMillis() + timeInterval;
		
		Bundle bundle = packPopupAlertBundle(data);
		bundle.putString(Consts.ALARM_ACTION, Consts.ALARM_CREATE_PLACEIT);
		bundle.putString(Consts.DATA_PLACEIT_DESCRIPTION, info.get(Consts.DATA_PLACEIT_DESCRIPTION));
		bundle.putParcelable(Consts.DATA_PLACEIT_LOCATION, data.getLocation());
		Intent intent = new Intent(Consts.INTENT_DELAYED_NAME);
		intent.putExtras(bundle);
		
		// Schedule future recurring place its
		PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, Consts.DELAYED_INTENT_CODE, intent, 0);
		am.setInexactRepeating(AlarmManager.RTC, postTime, timeInterval, pendingIntent);
	}
	
	public Bundle packPopupAlertBundle(GoogleMapData data){
		Bundle bundle = new Bundle();
		HashMap<String,String> info = data.getData();
		bundle.putString(Consts.DATA_PLACEIT_TITLE, info.get(Consts.DATA_PLACEIT_TITLE));
		bundle.putString(Consts.DATA_PLACEIT_ID, info.get(Consts.DATA_PLACEIT_ID));
	//	Log.d("Frankie", "Packing from sub function, id="+info.get(Consts.DATA_PLACEIT_ID));
		return bundle;
	}
	
	void sendPendingIntent(Intent data, long miliTime){
		pendingIntent = PendingIntent.getBroadcast(activity, Consts.DELAYED_INTENT_CODE, data, 0);
        long time = System.currentTimeMillis();
        Log.d("Debug", "System time: " + String.valueOf(time));
        Log.d("Debug", "Post time: " + String.valueOf(miliTime));
        
        am.set(AlarmManager.RTC, miliTime, pendingIntent);
//        //Debugging Mode
//        Log.d("Frankie", "Firing alarm manager");
//        am.set(AlarmManager.ELAPSED_REALTIME, 40000, pendingIntent);
//        Log.d("Frankie", "Sent pending intent");
    }
}
