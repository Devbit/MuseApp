package com.example.androidhive;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ShowPlaceActivity extends Activity {

	TextView txtTitle;
	TextView txtAddress;
	TextView txtCity;
	TextView txtInfo;
	Button btnSave;
	Button btnDelete;

	String mid;

	// Progress Dialog
	private ProgressDialog pDialog;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	// single place url
	private static final String url_place_detials = "http://jsonapp.tk/get_place_details.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PLACE = "places";
	private static final String TAG_MID = "ID";
	private static final String TAG_TITLE = "title";
	private static final String TAG_ADDRESS = "address";
	private static final String TAG_CITY = "city";
	private static final String TAG_INFO = "otherinfo";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_place);

		// getting place details from intent
		Intent i = getIntent();
		
		// getting place id (mid) from intent
		mid = i.getStringExtra(TAG_MID);

		// Getting complete place details in background thread
		new GetPlaceDetails().execute();
	}

	/**
	 * Background Async Task to Get complete place details
	 * */
	class GetPlaceDetails extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ShowPlaceActivity.this);
			pDialog.setMessage("Loading place details. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		/**
		 * Getting place details in background thread
		 * */
		protected String doInBackground(String... params) {

			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					// Check for success tag
					int success;
					try {
						// Building Parameters
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("mid", mid));

						// getting place details by making HTTP request
						// Note that place details url will use GET request
						JSONObject json = jsonParser.makeHttpRequest(
								url_place_detials, "GET", params);

						// check your log for json response
						Log.d("Single Place Details", json.toString());
						
						// json success tag
						success = json.getInt(TAG_SUCCESS);
						if (success == 1) {
							// successfully received place details
							JSONArray placeObj = json
									.getJSONArray(TAG_PLACE); // JSON Array
							
							// get first place object from JSON Array
							JSONObject place = placeObj.getJSONObject(0);

							// place with this mid found
							// Edit Text
							
							TextView txtTitle = (TextView) findViewById(R.id.inputTitle);
							TextView txtAddress = (TextView) findViewById(R.id.inputAddress);
							TextView txtCity = (TextView) findViewById(R.id.inputCity);
							TextView txtInfo = (TextView) findViewById(R.id.inputInfo);

							// display place data in TextView
							txtTitle.setText(place.getString(TAG_TITLE));
							txtAddress.setText(place.getString(TAG_ADDRESS));
							txtCity.setText(place.getString(TAG_CITY));
							txtInfo.setText(place.getString(TAG_INFO));

						}else{
							// place with mid not found
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});

			return null;
		}


		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once got all details
			pDialog.dismiss();
		}
	}
}
