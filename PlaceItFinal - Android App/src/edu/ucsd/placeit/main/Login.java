package edu.ucsd.placeit.main;



import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import edu.ucsd.placeit.R;
import edu.ucsd.placeit.service.LocationService;
import edu.ucsd.placeit.service.ServerConnection;
import edu.ucsd.placeit.util.Consts;

/*
 *  For login here are the usernames and passwords:
 *  
 *  Username   Password
 *  ---------|---------
 *  Frankie  | 12345
 *  Kevin    | 12345
 *  Alex     | 12345
 *  Daniel   | 12345
 *  Jin      | 12345
 *  
 */
public class Login extends Activity implements OnClickListener{
	private EditText loginUser;
	private EditText loginPass;	
	private Button loginSubmit;
	private TextView loginMessage;
	private String username;
	private String password;
	private SharedPreferences cookie;
	
	public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        
        
        String action;
        String user;
        cookie = getPreferences(MODE_PRIVATE);
        
        // Entering login, check origin
        Intent intent = getIntent();
    	action = intent.getStringExtra(Consts.LOGIN_LOG_USER_OUT);
        
    	
    	// App started by System
    	if(action == null){
			Log.d("Frankie", "action is null");
			// Handle sharedPreferences
			user = cookie.getString(Consts.USER_LOGGED_IN, "null");
    	}
    	
    	// Returned from Main, log user out and stop LocationService
    	else{
   		 	stopService(new Intent(this, LocationService.class));
        	SharedPreferences.Editor editor = cookie.edit();
    		editor.putString(Consts.USER_LOGGED_IN, "null");
    		editor.commit();
    		user = "null";
    	}
        

        Log.d("Frankie","See what happens to Login");
        // If a user has logged in before, transfer to Main activity
        if(!user.equals("null")){

            Log.d("Frankie","About to go to main now");
        	gotoMain(user);
        }
        else{
        	// No users have logged in yet
	        // Initialize UI controls
            Log.d("Frankie","Initialize UI wait for user to login");
	        initialize();
	    }
    }
	
	public void gotoMain(String user){
    	// Start Main Activity
		// The extraString tells main which user is logged in
		Log.d("Frankie", "Username is: " + user);
    	Intent intent = new Intent(getBaseContext(), MainActivity.class);
    	intent.putExtra(Consts.MAIN_ENTRANCE, user);
    	startActivity(intent);
	}
	
	
	private void initialize(){
		loginUser = (EditText) findViewById(R.id.login_username);
		loginPass = (EditText) findViewById(R.id.login_password);
		loginSubmit = (Button) findViewById(R.id.login_submit);
		loginMessage = (TextView) findViewById(R.id.login_message);
		
		loginSubmit.setOnClickListener(this);
	}

	
	public void onClick(View view) {
		if(!inputIsValid()){
			return;
		}
		// Username and Password are non-empty
		username = loginUser.getText().toString();
		password = loginPass.getText().toString();
		
		// Check network status
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo == null || !networkInfo.isConnected()) {
			// No connection, display error, return.
			Toast.makeText(this, "No internet connection.. Please try again later", Toast.LENGTH_LONG).show();
			return;
		}
		// Confirmed that we have internet connection
		loginMessage.setVisibility(TextView.VISIBLE);
		
		String link = Consts.LOGIN_SERVER_URL;
		link += "?user=" + username + "&pass=" + password;
		ServerConnection sc = new ServerConnection(this, loginMessage);
		sc.setArgument(cookie);
		sc.execute(link);	
	}
	
	private boolean inputIsValid(){
		if(loginUser.getText().toString().length() == 0) return false;
		if(loginPass.getText().toString().length() == 0) return false;
		return true;
	}
}
