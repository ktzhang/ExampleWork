package edu.ucsd.placeit.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import edu.ucsd.placeit.db.util.GetRequestFactory;
import edu.ucsd.placeit.db.util.IUrlBuilder;
import edu.ucsd.placeit.model.PlaceIt;
import edu.ucsd.placeit.model.impl.CategoricalPlaceIt;
import edu.ucsd.placeit.model.impl.NormalPlaceIt;
import edu.ucsd.placeit.model.impl.RecurringPlaceIt;
import edu.ucsd.placeit.util.Cfg;
import edu.ucsd.placeit.util.Consts;

public class OnlineDatabaseHelper {

	Context mContext;

	public OnlineDatabaseHelper(Context context) {
		mContext = context;
	}

	/**
	 * Retrieves all the placeIts from the server
	 * 
	 * @return
	 */
	public List<PlaceIt> getAllPlaceIts() {
		List<PlaceIt> list = null;
		String data = getRequest("placeIts");
		list = parsePlaceItsJSON(data);
		return list;
	}

	public Date getDbVersion() {
		String data = getRequest("version");
		return parseVersionJson(data);
	}

	/**
	 * Makes a get request that returns a JSON object
	 * 
	 * @param type
	 * @return
	 */
	private String getRequest(String type) {
		GetRequestFactory rf = new GetRequestFactory();
		IUrlBuilder urlBuilder = rf.createBuilder(type);
		String data = "";
		String url = urlBuilder.buildUrl();
		HttpClient client = new DefaultHttpClient();
		HttpGet request = new HttpGet(url);
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

	private Date parseVersionJson(String data) {
		JSONObject myjson;
		Date date = null;
		try {
			myjson = new JSONObject(data);
			JSONArray array = myjson.getJSONArray("data");
			JSONObject obj = array.getJSONObject(0);
			String dateString = obj.get("date").toString();
			date = Consts.DATE_FORMAT_SIMPLE.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * Parsing the JSON string with retrieves
	 * 
	 * @param data
	 * @return
	 */
	private List<PlaceIt> parsePlaceItsJSON(String data) {
		JSONObject myjson;
		List<PlaceIt> list = new ArrayList<PlaceIt>();
		try {
			myjson = new JSONObject(data);
			JSONArray array = myjson.getJSONArray("data");
			for (int i = 0; i < array.length(); i++) {
				// Initializing the variables
				int id, type, state, frequency = 0;
				String user, title, desc, longitude, latitude, dateCreatedString, datePostedString;
				Date dateCreated = null, datePosted = null;
				String[] categories = new String[3];
				PlaceIt placeIt = null;

				// Retrieving values from the array
				JSONObject obj = array.getJSONObject(i);
				id = Integer.parseInt(obj.get("name").toString());
				user = obj.get("user").toString();
				type = Integer.parseInt(obj.get("type").toString());
				state = Integer.parseInt(obj.get("state").toString());
				title = obj.get("title").toString();
				desc = obj.get("description").toString();
				longitude = obj.get("longitude").toString();
				latitude = obj.get("latitude").toString();
				dateCreatedString = obj.get("date_created").toString();
				datePostedString = obj.get("date_to_post").toString();
				String frequencyString = obj.get("frequency").toString();
				if (frequencyString == "") {
					frequency = Integer.parseInt(frequencyString);
				}
				categories[0] = obj.get("cat_1").toString();
				categories[1] = obj.get("cat_2").toString();
				categories[2] = obj.get("cat_3").toString();
				LatLng coord = new LatLng(Double.parseDouble(latitude),
						Double.parseDouble(longitude));
				Log.d(Consts.TAG_TEST, "type of placeit=" + type);

				try {
					dateCreated = Consts.DATE_FORMAT_SIMPLE
							.parse(dateCreatedString);
					datePosted = Consts.DATE_FORMAT_SIMPLE
							.parse(datePostedString);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				switch (type) {
				case Consts.TYPE_NORMAL:
					placeIt = new NormalPlaceIt(id, user, title, desc, state,
							coord, dateCreated, datePosted);
					list.add(placeIt);
					Log.d(Consts.TAG_TEST, "new Normal placeIT");
					break;
				case Consts.TYPE_CATEGORICAL:
					placeIt = new CategoricalPlaceIt(id, user, title, desc,
							state, dateCreated, datePosted, categories);
					list.add(placeIt);
					Log.d(Consts.TAG_TEST, "new cat placeIT");

					break;
				case Consts.TYPE_RECURRING:
					placeIt = new RecurringPlaceIt(id, user, title, desc,
							state, coord, dateCreated, datePosted, frequency);
					list.add(placeIt);
					Log.d(Consts.TAG_TEST, "new recurring placeIT");

					break;
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	private class UpdaterTask extends AsyncTask<String, Void, String> {
		List<String> list = new ArrayList<String>();

		@Override
		protected String doInBackground(String... url) {

			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet(url[0]);
			try {
				HttpResponse response = client.execute(request);
				HttpEntity entity = response.getEntity();
				String data = EntityUtils.toString(entity);
				JSONObject myjson;

				try {
					myjson = new JSONObject(data);
					JSONArray array = myjson.getJSONArray("data");
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						list.add(obj.get("name").toString());
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return data;
			} catch (ClientProtocolException e) {

				Log.d(Consts.TAG,
						"ClientProtocolException while trying to connect to GAE");
			} catch (IOException e) {

				Log.d(Consts.TAG, "IOException while trying to connect to GAE");
			}
			return null;
		}

		protected void onPostExecute(String data) {
			Log.d(Consts.TAG, "Setting Text");
			String dataString = "";
			for (int i = 0; i < list.size(); i++) {
				dataString += " " + list.get(i) + ":";
			}
		}
	}

	/**
	 * Delete all the PlaceIts in the GoogleDataStore
	 */
	public void deleteAllPlaceIts() {
		this.addUpdateVersion(new Date());

		// List<PlaceIt> list = this.getAllPlaceIts();
		//
		// for (int i = 1; i <= list.size(); i++) {
		// this.deletePlaceIt(i);
		// }
		Log.d(Consts.TAG_TEST, "starting the online delete sequence");
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(Cfg.PRODUCT_API);

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("action", "deleteAll"));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(post);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			String line = "";
			while ((line = rd.readLine()) != null) {
				Log.d(Consts.TAG_TEST, line);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.d(Consts.TAG_TEST, "IOException while trying to conect to GAE");
		}
		Log.d(Consts.TAG_TEST, "finish the online deleteAll sequence");
	}

	/**
	 * Deletes a placeIt based on its ID
	 * 
	 * @param id
	 */
	public void deletePlaceIt(final int id) {
		this.addUpdateVersion(new Date());

		// final ProgressDialog dialog = ProgressDialog.show(mContext,
		// "Posting Data...", "Please wait...", false);
		// Thread t = new Thread() {
		//
		// public void run() {
		Log.d(Consts.TAG_TEST, "starting the online adding sequence");
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(Cfg.PRODUCT_API);

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("id", Integer
					.toString(id)));

			nameValuePairs.add(new BasicNameValuePair("action", "delete"));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(post);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			String line = "";
			while ((line = rd.readLine()) != null) {
				Log.d(Consts.TAG_TEST, line);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.d(Consts.TAG_TEST, "IOException while trying to conect to GAE");
		}
		// dialog.dismiss();
		Log.d(Consts.TAG_TEST, "finish the online deleting sequence");

		// }
		// };

		// t.start();
		// dialog.show();

	}

	/**
	 * Adds to database based on the placeIt passed in the parameter
	 * 
	 * @param placeIt
	 */
	public void createUpdatePlaceIt(final PlaceIt placeIt) {
		this.addUpdateVersion(new Date());
		// final ProgressDialog dialog = ProgressDialog.show(mContext,
		// "Posting Data...", "Please wait...", false);
		// Thread t = new Thread() {
		//
		// public void run() {
		Log.d(Consts.TAG_TEST, "starting the online adding sequence");
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(Cfg.PRODUCT_API);

		// Getting type and categories
		int type = 0;
		String[] cats = new String[3];
		String freq = "";
		if (placeIt instanceof NormalPlaceIt) {
			type = Consts.TYPE_NORMAL;

		} else if (placeIt instanceof CategoricalPlaceIt) {
			type = Consts.TYPE_CATEGORICAL;
			cats = ((CategoricalPlaceIt) placeIt).getCategories();

		} else if (placeIt instanceof RecurringPlaceIt) {
			type = Consts.TYPE_RECURRING;
			freq = Integer
					.toString(((RecurringPlaceIt) placeIt).getFrequency());
		}
		String typeString = Integer.toString(type);

		// Formatting date
		String creation_date = Consts.DATE_FORMAT_SIMPLE.format(placeIt
				.getCreationDate());
		String post_date = Consts.DATE_FORMAT_SIMPLE.format(placeIt
				.getPostDate());

		// Getting categories

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("id", Integer
					.toString(placeIt.getId())));
			nameValuePairs
					.add(new BasicNameValuePair("user", placeIt.getUser()));
			nameValuePairs.add(new BasicNameValuePair("type", typeString));
			nameValuePairs.add(new BasicNameValuePair("title", placeIt
					.getTitle()));
			nameValuePairs.add(new BasicNameValuePair("state", Integer
					.toString(placeIt.getState())));
			nameValuePairs.add(new BasicNameValuePair("description", placeIt
					.getDesc()));
			nameValuePairs.add(new BasicNameValuePair("longitude", Double
					.toString(placeIt.getCoord().longitude)));
			nameValuePairs.add(new BasicNameValuePair("latitude", Double
					.toString(placeIt.getCoord().latitude)));
			nameValuePairs.add(new BasicNameValuePair("latitude", placeIt
					.getDesc()));
			nameValuePairs.add(new BasicNameValuePair("date_created",
					creation_date));
			nameValuePairs
					.add(new BasicNameValuePair("date_to_post", post_date));
			nameValuePairs.add(new BasicNameValuePair("date_freq_start",
					post_date));
			nameValuePairs.add(new BasicNameValuePair("frequency", freq));
			nameValuePairs.add(new BasicNameValuePair("cat_1", cats[0]));
			nameValuePairs.add(new BasicNameValuePair("cat_2", cats[1]));
			nameValuePairs.add(new BasicNameValuePair("cat_3", cats[2]));
			nameValuePairs.add(new BasicNameValuePair("action", "put"));

			Log.d(Consts.TAG_TEST, "1 the online adding sequence");

			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			Log.d(Consts.TAG_TEST, "2 the online adding sequence");

			HttpResponse response = client.execute(post);

			Log.d(Consts.TAG_TEST, "3 the online adding sequence");

			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			String line = "";
			while ((line = rd.readLine()) != null) {
				Log.d(Consts.TAG_TEST, line);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.d(Consts.TAG_TEST, "IOException while trying to conect to GAE");
		}
		// dialog.dismiss();
		Log.d(Consts.TAG_TEST, "finish the online adding sequence");

		// }
		// };

		// t.start();
		// dialog.show();
	}

	/**
	 * Adds to database based on the placeIt passed in the parameter
	 * 
	 * @param placeIt
	 */
	public Date addUpdateVersion(Date date) {
		// final ProgressDialog dialog = ProgressDialog.show(mContext,
		// "Posting Data...", "Please wait...", false);
		// Thread t = new Thread() {
		//
		// public void run() {
		if (date == null) {
			date = new Date();
		}
		Log.d(Consts.TAG_TEST, "starting the online adding sequence");
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(Cfg.PRODUCT_API);

		// Formatting the date
		String dateString = Consts.DATE_FORMAT_SIMPLE.format(date);
		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("version_date",
					dateString));
			nameValuePairs.add(new BasicNameValuePair("action", "version"));
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse response = client.execute(post);
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			String line = "";
			while ((line = rd.readLine()) != null) {
				Log.d(Consts.TAG_TEST, line);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.d(Consts.TAG_TEST, "IOException while trying to conect to GAE");
		}
		// dialog.dismiss();
		Log.d(Consts.TAG_TEST, "finish the online adding sequence");

		return date;
		// }
		// };

		// t.start();
		// dialog.show();
	}
}
