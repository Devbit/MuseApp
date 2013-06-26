package com.hro.museapp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.hro.museapp.map.ClusteringMapActivity;
import com.hro.museapp.map.GPSTracker;

public class PlacesLoader {

	// Creating JSON Parser object
	private static JSONParser jParser = new JSONParser();
	private static GPSTracker tracker;

	private static JSONArray places;
	private static JSONArray charities;
	private static JSONArray searches;
	private static JSONArray nearbyPlaces;
	private static JSONArray singlePlace;
	private static ArrayList<HashMap<String, String>> placesList;
	private static ArrayList<HashMap<String, String>> searchList;
	private static ArrayList<HashMap<String, String>> nearbyList;
	
	private static String lastSearch = "";
	private static boolean hasSingle = false;

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PLACES = "places";
	private static final String TAG_MID = "ID";
	private static final String TAG_NAME = "title";
	private static final String TAG_LAT = "latitude";
	private static final String TAG_LONG = "longitude";
	
	public static final int TYPE_ALL = 0;
	public static final int TYPE_SEARCH = 1;
	public static final int TYPE_NEARBY = 2;
	public static final int TYPE_SINGLE = 3;

	private static final int NEARBY = 0;
	private static final int SEARCH = 1;
	
	/*
	 * PlacesLoader class
	 * 
	 * Static class used to be able to share lists of places easily inbetween activities
	 * Saves all the created lists, so when a search is made, the results are saved
	 * This is so that the map activity can easily retrieve those results
	 * 
	 */
	
	//Sets the places from cache
	public static void setCache(int type, JSONArray cache) {
		if (type == CacheHandler.PLACES_CACHE)
			places = cache;
		else if (type == CacheHandler.CHARITY_CACHE)
			charities = cache;
	}
	
	//Set GPSTracker, only needed for nearby places
	public static void setGPS(GPSTracker gps) {
		tracker = gps;
	}
	
	//Get the GPSTracker
	public static GPSTracker getGPS() {
		return tracker;
	}
	
	//Function to get a listview ready arraylist of all places
	public static ArrayList<HashMap<String, String>> loadPlacesList() {
		placesList = makeListFromPlaces(places);
		return placesList;
	}
	
	//Return all places
	public static JSONArray getPlaces() {
		return places;
	}
	
	//Return all charities
	public static JSONArray getCharities() {
		return charities;
	}
	
	//Return listview ready list arraylist of all places
	public static ArrayList<HashMap<String, String>> getPlacesList() {
		return placesList;
	}
	
	//Get the nearby places, optional limit argument
	public static JSONArray getNearby(int max) {
		String URL = "http://jsonapp.tk/get_places_nearby.php";
		double lat = 0;
		double lon = 0;
		
		// check if GPS enabled
		if (tracker.canGetLocation()) {

			double latitude1 = tracker.getLatitude();
			double longitude1 = tracker.getLongitude();
			
			lat = latitude1;
			lon = longitude1;

		} else {
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			tracker.showSettingsAlert();
		}
		
		String[] args;
		
		JSONArray result = getData(URL, TAG_PLACES, NEARBY, String.valueOf(lat), String.valueOf(lon), String.valueOf(max));
		nearbyPlaces = result;
		return result;
	}
	
	//Get the results of the getNearby function
	public static JSONArray getNearbyResults() {
		return nearbyPlaces;
	}
	
	//Search function. Saves and returns search input
	public static JSONArray search(String input) {
		//JSONArray searchResult = PlacesLoader.search(query)
		//om te zoeken, returned hele JSON
		//PlacesLoader.makeListFromPlaces(searchResult) voor list compatible ArrayList
		/*String URL = "http://jsonapp.tk/search.php?s=%s";
		URL = String.format(URL, input);*/
		String URL = "http://jsonapp.tk/search.php";
		JSONArray result = getData(URL, TAG_PLACES, SEARCH, input);
		lastSearch = input;
		searches = result;
		return result;
	}
	
	//Return last search query
	public static String getLastSearch() {
		return lastSearch;
	}
	
	//Clear last search query
	public static void clearLastSearch() {
		lastSearch = "";
	}
	
	//Clear all searchresults
	public static void clearSearches() {
		searches = null;
		searchList = null;
	}
	
	//Return searchresults
	public static JSONArray getSearchResults() {
		return searches;
	}
	
	//Save a single place
	public static void setSinglePlace(JSONArray place) {
		singlePlace = place;
		hasSingle = true;
	}
	
	//Return the saved single place
	public static JSONArray getSinglePlace() {
		return singlePlace;
	}
	
	//Clear single places
	public static void clearSinglePlace() {
		hasSingle = false;
		singlePlace = null;
	}
	
	//Returns whether a single places has been saved or not
	public static boolean hasSingle() {
		return hasSingle;
	}
	
	//Make a listview ready arraylist from a JSONArray of places
	public static ArrayList<HashMap<String, String>> makeListFromPlaces(JSONArray places) {
		ArrayList<HashMap<String, String>> result = new ArrayList<HashMap<String, String>>();
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
				result.add(map);

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;
	}
	
	//Helper function to download data. Used in search and nearby places
	private static JSONArray getData(String url, String tag, int type, String... args) {
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		if (type == SEARCH) {
			params.add(new BasicNameValuePair("s", args[0]));
		} else if (type == NEARBY) {
			params.add(new BasicNameValuePair("lat", args[0]));
			params.add(new BasicNameValuePair("lng", args[1]));
			params.add(new BasicNameValuePair("dist", args[2]));
		}
		// getting JSON string from URL
		JSONObject json = jParser.makeHttpRequest(url, "GET", params);
		JSONArray result = new JSONArray();

		try {
			// Checking for SUCCESS TAG
			int success = json.getInt(TAG_SUCCESS);

			if (success == 1) {
				result = json.getJSONArray(tag);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return result;
	}

}
