package edu.ucsd.placeit.service.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import edu.ucsd.placeit.db.DatabaseHelper;
import edu.ucsd.placeit.db.DatabaseHelper;
import edu.ucsd.placeit.model.PlaceIt;
import edu.ucsd.placeit.model.PlaceItBank;
import edu.ucsd.placeit.service.IAsyncResponse;
import edu.ucsd.placeit.service.ICategoryHandler;
import edu.ucsd.placeit.util.Cfg;
import edu.ucsd.placeit.util.Consts;

public class CategoryHandler implements ICategoryHandler, IAsyncResponse {
	private final String API_URL = "https://maps.googleapis.com/maps/api/place/search/json?"
			+ "location=%s" // COORDINATES_IN_LAT_LONG
			+ "&radius=%s" // DISTANCE_IN_METERS
			+ "&sensor=%s" // TRUE_OR_FALSE
			+ "&key=%s"; // API KEY

	private Context mContext;
	private List<String> mCurrentCategories;
	private String urlContents;
	private String mUserName;

	public CategoryHandler(Context context) {
		mContext = context;
		mCurrentCategories = new ArrayList<String>();
	}

	/**
	 * Called whenever the location change and sees if any of the locations near
	 * the person match the category.
	 * 
	 * @param location
	 * @return list of categories that need to be checked
	 */
	public List<PlaceIt> onLocationChanged(Location location, String userName) {

		Log.d("CATEGORYTAG", "------>");
		mUserName = userName;
		List<String> newList = new ArrayList<String>();
		// Building the longitude and latitude
		String urlString = buildURLString(location);
		urlContents = getUrlContents(urlString);
		Log.d("CATEGORYTAG", urlContents);
		// DownloadAysncTask aysncTask = new DownloadAysncTask();
		// // aysncTask.get(2000, TimeUnit.MILLISECONDS);
		// aysncTask.execute(urlString);
		// try {
		// Thread.sleep(2000);
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		List<String> categories = processFinish(urlContents);

		for (String category : categories) {
			Log.d("CATEGORYTAG", "hit this category: " + category);
			if (!mCurrentCategories.contains(category)) {
				newList.add(category);
			}
		}

		// resetting the categories
		mCurrentCategories = categories;

		// find the placeIts based on the categories
		List<PlaceIt> findPlaceIts = findPlaceIts(newList);
		Log.d("CATEGORYTAG", "found this many placeIts: " + findPlaceIts.size());
		return findPlaceIts;
		// return null;
		// getUrlContents(urlString);
	}

	/**
	 * Returns a list of categorical placeIts matching the category
	 * 
	 * @param categories
	 * @return
	 */
	private List<PlaceIt> findPlaceIts(List<String> categories) {
		DatabaseHelper db = new DatabaseHelper(mContext);
		List<PlaceIt> placeIts = new ArrayList<PlaceIt>();
		// Getting all the placeIts based on the category
		for (String category : categories) {
			placeIts.addAll(db.getAllPlaceIts(0, category, 0, mUserName));
		}
		db.closeDB();
		return placeIts;

	}

	/**
	 * Method which builds the GooglePlaces URL string using the location
	 * 
	 * @param location
	 * @return properly formatted string
	 */
	private String buildURLString(Location location) {
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();

		StringBuilder locBuilder = new StringBuilder();
		locBuilder.append(Double.toString(latitude));
		locBuilder.append(",");
		locBuilder.append(Double.toString(longitude));
		String locationString = locBuilder.toString();

		String urlString = String.format(API_URL, locationString,
				Cfg.CATEGORY_RADIUS, Cfg.GOOGLE_PLACES_SENSOR,
				Cfg.GOOGLE_PlACES_KEY);
		return urlString;
	}

	/**
	 * AsyncTask which retrieves the JSON string based on the input URL.
	 * 
	 * @author Kevin
	 * 
	 */
	private class DownloadAysncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... url) {

			String data = "";
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url[0]);
			try {
				HttpResponse response = client.execute(request);
				HttpEntity entity = response.getEntity();
				data = EntityUtils.toString(entity);
			} catch (ClientProtocolException e) {
				Log.d(Consts.TAG,
						"ClientProtocolException while trying to connect to GAE");
			} catch (IOException e) {

				Log.d(Consts.TAG, "IOException while trying to connect to GAE");
			}
			return data;
		}

		/**
		 * Passes the finished process to outer method
		 */
		protected void onPostExecute(String data) {
			Log.d(Consts.TAG_TEST, "reaching the end of thread");
			urlContents = data;
		}
	}

	@Override
	public List<String> processFinish(String data) {
		Log.d(Consts.TAG_TEST, "starting process finish");

		List<String> list = new ArrayList<String>();
		try {
			JSONObject myjson = new JSONObject(data);
			// Retreiving the results array
			String status = myjson.getString("status");
			Log.d(Consts.TAG_TEST, "Status of API: " + status);
			if (status.toString().equals("OK")) {
				JSONArray resultsArray = myjson.getJSONArray("results");
				if (resultsArray.length() > 0) {
					for (int i = 0; i < resultsArray.length(); i++) {
						JSONObject result = resultsArray.getJSONObject(i);
						JSONArray catArray = result.getJSONArray("types");

						for (int j = 0; j < catArray.length(); j++) {
							String categoryString = catArray.getString(j)
									.toString();
							if (!list.contains(categoryString)) {
								list.add(categoryString);
							}
						}
					}
				}
			} else if (status.toString().equals("OVER_QUERY_LIMIT")) {
				if (Cfg.SHOW_CAT_LOG) {
					int duration = Toast.LENGTH_SHORT;
					Toast toast = Toast.makeText(mContext,
							"Google Places is Over API", duration);
					toast.show();
				}
			}
		} catch (JSONException e) {
			Log.d(Consts.TAG_TEST, "error caught");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return list;
	}

	class RetreiveFeedTask extends AsyncTask<String, Void, String> {

		private Exception exception;

		protected String doInBackground(String... urls) {
			try {
				return getUrlContents(urls[0]);
			} catch (Exception e) {
				this.exception = e;
				return null;
			}
		}

		protected void onPostExecute(String jString) {
			// TODO: check this.exception
			// TODO: do something with the feed
		}
	}

	/**
	 * Private method to retrieve the PlaceIt contents from GooglePlaces API,
	 * while passing in the built string.
	 * 
	 * @param theUrl
	 * @return
	 */
	private String getUrlContents(String theUrl) {
		StringBuilder content = new StringBuilder();
		try {
			URL url = new URL(theUrl);
			URLConnection urlConnection = url.openConnection();
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(urlConnection.getInputStream()), 8);
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				content.append(line);
			}
			bufferedReader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return content.toString();
	}
}
