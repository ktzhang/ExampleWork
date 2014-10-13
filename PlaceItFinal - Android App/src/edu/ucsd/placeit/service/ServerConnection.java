package edu.ucsd.placeit.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import edu.ucsd.placeit.main.MainActivity;
import edu.ucsd.placeit.util.Consts;

public class ServerConnection extends AsyncTask<String, Integer, String> {
	private Activity activity;
	private TextView loginMessage;
	private SharedPreferences cookie;
	private String user;
	
	public ServerConnection(Activity activity, TextView loginMessage){
		this.activity = activity;
		this.loginMessage = loginMessage;
	}
	
	public void setArgument(SharedPreferences cookie){
		this.cookie = cookie;
	}
	
	
	protected String doInBackground(String... urls) {
		// Extract the first url since we only passed in one.
		String url = urls[0];
		// Retrieve result from server
		String result = null;
		try {
			// Executes server connection and retrieves result
			result = getServerResult(url);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
    }

    protected void onPostExecute(String result) {
    	loginMessage.setVisibility(TextView.INVISIBLE);
    	
		if(result.charAt(0) != 'W'){
			// Error message
			Toast.makeText(activity, result, Toast.LENGTH_LONG).show();
		}
		else{
			// Success!
			Toast.makeText(activity, result, Toast.LENGTH_LONG).show();
			Log.d("Frankie", "Upon successful return, we should go to main now");
			// Modify shared preferences
			SharedPreferences.Editor editor = cookie.edit();
			editor.putString(Consts.USER_LOGGED_IN, user);
			editor.commit();
			gotoMain(user);
		}
    }
    
	private void gotoMain(String user){
    	// Start Main Activity
		// The extraString tells main which user is logged in
		Log.d("Frankie", "Username is: " + user);
    	Intent intent = new Intent(activity, MainActivity.class);
    	intent.putExtra(Consts.MAIN_ENTRANCE, user);
    	activity.startActivity(intent);
	}
    
    
    // Will connect to the given url and retrieve result
	private String getServerResult(String serverPath) throws IOException{
		URL url = new URL(serverPath);
		String connectionResult = null;
		InputStream is = null;
		int length = 100;
		
		try{
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000);
			conn.setConnectTimeout(15000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.connect();

			int response = conn.getResponseCode();
			Log.d(Consts.TAG_OTHER, "The response is " + response);
			
			is = conn.getInputStream();
			connectionResult = readInputStream(is, length);
			Log.d(Consts.TAG_TEST, "Raw string:"+connectionResult);
			connectionResult = handleServerResult(connectionResult);
			Log.d(Consts.TAG_TEST, "Processed string:"+connectionResult);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			if (is != null)
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return connectionResult;
	}
    
	// Extracts the message from successful logins
    private String handleServerResult(String result){
    	Log.d("Frankie", "lol, Raw message: " + result + ",  result length="+result.length());
	    if(result.charAt(0) != 'S'){
			return result;
		}
		// If successful
		int index;
		for(index = 0; index < result.length(); index++){
			if(result.charAt(index) == 'W') break;
		}
		String message = "";
		user = "";
		int userIndex = 0;
		for(; index < result.length(); index++){
			message += result.charAt(index);
			if(result.charAt(index) == ':'){
				userIndex = index+2;
			}
		}
		for(; userIndex < result.length(); userIndex++){
			user += result.charAt(userIndex);
		}
		Log.d("frankie","lol, user is: " + user);
		return message;
    }
    
    // Reads the input stream
	private String readInputStream(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
	    Reader reader = null;
	    reader = new InputStreamReader(stream, "UTF-8");        
	    char[] buffer = new char[len];
	    int charRead = reader.read(buffer);
	    Log.d("Frankie","lol, num of char read is: " + charRead);
	    String result = "";
	    for(int i = 0; i < charRead; i++){
	    	result += buffer[i];
	    }
	    Log.d("Frankie","lol, result is " + result);
	    return result;
	}

}
