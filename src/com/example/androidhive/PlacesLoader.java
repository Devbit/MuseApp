package com.example.androidhive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

public class PlacesLoader {

	// Creating JSON Parser object
	private static JSONParser jParser = new JSONParser();

	private static JSONArray places;
	private static ArrayList<HashMap<String, String>> placesList;

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PLACES = "places";
	private static final String TAG_MID = "ID";
	private static final String TAG_NAME = "title";
	private static final String TAG_LAT = "latitude";
	private static final String TAG_LONG = "longitude";
	
	public static void setCache(JSONArray cache) {
		places = cache;
	}

	public static ArrayList<HashMap<String, String>> loadPlacesList() {
		makeListFromPlaces();
		return placesList;
	}

	public static JSONArray getPlaces() {
		return places;
	}

	public static ArrayList<HashMap<String, String>> getPlacesList() {
		return placesList;
	}

	private static ArrayList<HashMap<String, String>> makeListFromPlaces() {
		placesList = new ArrayList<HashMap<String, String>>();
		try {
			for (int i = 0; i < places.length(); i++) {
				JSONObject c = places.getJSONObject(i);

				// Storing each json item in variable
				String id = c.getString(TAG_MID);
				String name = c.getString(TAG_NAME);

				// creating new HashMap
				HashMap<String, String> map = new HashMap<String, String>();

				// adding each child node to HashMap key => value
				map.put(TAG_MID, id);
				map.put(TAG_NAME, name);

				// adding HashList to ArrayList
				placesList.add(map);

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return placesList;
	}

}
