package com.hro.museapp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class CacheHandler {

	private JSONParser jParser = new JSONParser();
	private Context context;
	private String localVersion;
	private String remoteVersion;
	private JSONArray places;

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_VERSION = "version";
	private static final String TAG_PLACES = "places";

	private static final String version = "version.txt";
	private static final String cache = "cache.txt";
	private static final String charityCache = "charity_cache.txt";
	private static final boolean FAIL = false;
	private static final boolean SUCCESS = true;

	private static final String URL_DATABASE = "http://jsonapp.tk/get_all_places.php";
	private static final String URL_VERSION = "http://jsonapp.tk/get_version.php";
	private static final String URL_CHARITY = "http://jsonapp.tk/get_all_charities.php";

	public static final int PLACES_CACHE = 0;
	public static final int CHARITY_CACHE = 1;

	/*
	 * CacheHandler class
	 * 
	 * We use a simple caching system for our database. Since it's pretty large,
	 * downloading it every time would take too long So we save the database
	 * locally, and only download if the online database gets updated
	 */

	public CacheHandler(Context con) {
		// We need the application context for reading and writing files
		context = con;
	}

	public boolean check() {
		// Simple check function
		// First checks if we have an internet connection
		// Then fetches local and remote version, and compares them
		// Also check if cachefile exists
		Log.d("CACHEHANDLER", "Checking cache...");
		if (isOnline()) {
			getRemoteVersion();
			getLocalVersion();
			if (localVersion.equals("")) {
				return true;
			}
			double local = Double.parseDouble(localVersion);
			double remote = Double.parseDouble(remoteVersion);

			// Check cache file
			File cacheFile = context.getFileStreamPath(cache);
			File cacheFileCharity = context.getFileStreamPath(charityCache);
			if (!cacheFile.isFile()) {
				return true;
			} else if (cacheFile.length() == 0) {
				return true;
			}

			if (!cacheFileCharity.isFile()) {
				return true;
			} else if (cacheFile.length() == 0) {
				return true;
			}

			if (local < remote) {
				// update();
				return true;
			}
		}
		return false;
	}

	public void update() {
		// Check function, update local version, download new database and save
		// version
		Log.d("CACHEHANDLER", "Updating cache...");
		if (remoteVersion == null)
			localVersion = getRemoteVersion();
		else
			localVersion = remoteVersion;
		downloadLatest();
		boolean saveResult = saveFile(version, localVersion);
		if (saveResult == FAIL) {
			// Error handling
		}
	}

	public String getLocalVersion() {
		// Read the version from the version file
		localVersion = readFile(version);
		Log.d("CACHEHANDLER", "Local version:");
		Log.d("CACHEHANDLER", localVersion);
		if (localVersion.equals("")) {
			// update();
		}
		return localVersion;
	}

	public String getRemoteVersion() {
		// Fetch the remote version from the server
		JSONArray result = getData(URL_VERSION, TAG_VERSION);
		try {
			remoteVersion = result.getJSONObject(0).getString(TAG_VERSION);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.d("CACHEHANDLER", "Remote version:");
		Log.d("CACHEHANDLER", remoteVersion);
		return remoteVersion;
	}

	public JSONArray getCache(int type) {
		//Read the cache from the file
		String file = null;
		
		switch(type) {
			case PLACES_CACHE:
				file = cache;
		break;
			case CHARITY_CACHE:
				file = charityCache;
		break;
		default:
			file = cache;
		}

		String result = readFile(file);
		JSONArray places = new JSONArray();
		try {
			places = new JSONArray(result);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.d("TEST", places.toString());
		Log.d("FILE", file);
		return places;
	}

	private void downloadLatest() {
		// Download latest database
		String result = getData(URL_DATABASE, TAG_PLACES).toString();
		boolean saveResult = saveFile(cache, result);
		if (saveResult == FAIL) {
			// Error handling
		}
		// Download latest charities
		result = getData(URL_CHARITY, TAG_PLACES).toString();
		saveResult = saveFile(charityCache, result);
		if (saveResult == FAIL) {
			// Error handling
		}
	}

	private JSONArray getData(String url, String tag, String... args) {
		// Function to handle the downloading and parsing of data
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
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

	private String readFile(String file) {
		// Helper function to read files
		StringBuilder text = new StringBuilder();

		try {
			FileInputStream fileToRead = context.openFileInput(file);
			InputStreamReader inputStreamReader = new InputStreamReader(
					fileToRead);
			BufferedReader br = new BufferedReader(inputStreamReader);
			String line;

			while ((line = br.readLine()) != null) {
				text.append(line);
				// text.append('\n');
			}
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		return text.toString();
	}

	private boolean saveFile(String file, String str) {
		// Helper function to save files
		try {
			FileOutputStream fileToWrite = context.openFileOutput(file,
					context.MODE_PRIVATE);
			fileToWrite.write(str.getBytes());
			fileToWrite.close();
		} catch (IOException e) {
			e.printStackTrace();
			return FAIL;
		}
		return SUCCESS;
	}

	public boolean isOnline() {
		// Helper function to determine if we have an internet connection
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}
}
