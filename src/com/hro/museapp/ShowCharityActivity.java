package com.hro.museapp;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hro.museapp.map.ClusteringMapActivity;
import com.hro.museapp.map.GPSTracker;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class ShowCharityActivity extends Activity {

	TextView txtTitle;
	TextView txtAddress;
	TextView txtCity;
	TextView txtInfo;
	Button btnSave;
	Button btnDelete;

	String mid;

	// Progress Dialog
	private ProgressDialog pDialog;

	private Context con;

	int duration = Toast.LENGTH_SHORT;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	GPSTracker gps;

	// single place url
	private static final String url_place_detials = "http://jsonapp.tk/get_charity_details.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PLACE = "places";
	private static final String TAG_MID = "ID";
	private static final String TAG_TITLE = "title";
	private static final String TAG_INFO = "info";
	private static final String TAG_IMAGE = "thumb";
	private static final String TAG_PHONE = "phone";
	private static final String TAG_WEB = "website";

	private static String titelMonument;
	private static String idMonument;
	private static String beschrijvingMonument;
	private static String afbeeldingMonument;
	private static String websiteMonument;
	private static String telefoonMonument;

	Bitmap bitmap = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_charity);

		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		// getting place details from intent
		Intent i = getIntent();

		// getting place id (mid) from intent
		mid = i.getStringExtra(TAG_MID);

		con = this.getApplicationContext();
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
			pDialog = new ProgressDialog(ShowCharityActivity.this);
			pDialog.setMessage(getString(R.string.loadingDetails));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * Getting place details in background thread
		 * */

		protected String doInBackground(String... params1) {

			// Check for success tag
			int success;
			try {
				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("mid", mid));

				// getting place details by making HTTP request
				// Note that place details url will use GET request
				JSONObject json = jsonParser.makeHttpRequest(url_place_detials,
						"GET", params);

				// json success tag
				success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					// successfully received place details
					JSONArray placeObj = json.getJSONArray(TAG_PLACE); // JSON
																		// Array
					PlacesLoader.setSinglePlace(placeObj);

					// get first place object from JSON Array
					JSONObject place = placeObj.getJSONObject(0);

					// place with this mid found

					titelMonument = place.getString(TAG_TITLE);
					idMonument = place.getString(TAG_MID);
					
					beschrijvingMonument = Html.fromHtml(
							place.getString(TAG_INFO)).toString();

					
					websiteMonument = place.getString(TAG_WEB);
					telefoonMonument = place.getString(TAG_PHONE);

					afbeeldingMonument = place.getString(TAG_IMAGE);

					try {
						bitmap = BitmapFactory
								.decodeStream((InputStream) new URL(
										afbeeldingMonument).getContent());
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					TextView txtTitle = (TextView) findViewById(R.id.inputTitle);

					
					TextView txtInfo = (TextView) findViewById(R.id.inputInfo);
					TextView txtWebsite = (Button) findViewById(R.id.inputWebsite);
					TextView labelWebsite = (TextView) findViewById(R.id.labelWebsite);
					TextView txtPhone = (Button) findViewById(R.id.inputPhone);
					TextView labelPhone = (TextView) findViewById(R.id.labelPhone);
					Button callButton = (Button) findViewById(R.id.callBtn);


					if (websiteMonument.equals("")) {
						txtWebsite.setVisibility(View.GONE);
						labelWebsite.setVisibility(View.GONE);
					}

					if (telefoonMonument.equals("")) {						
						txtPhone.setVisibility(View.GONE);
						labelPhone.setVisibility(View.GONE);
						callButton.setBackgroundColor(Color.GRAY);
						callButton.setEnabled(false);
					}

					// display place data in TextView
					txtTitle.setText(titelMonument);
					txtInfo.setText(beschrijvingMonument);
					txtPhone.setText(telefoonMonument);
					txtWebsite.setText(websiteMonument);

					setTitle(titelMonument);

					ImageView img = (ImageView) findViewById(R.id.afbeelding);

					LinearLayout bg = (LinearLayout) findViewById(R.id.bg);

					img.setImageBitmap(bitmap);

					if (bitmap == null) {
						img.setVisibility(View.GONE);
						bg.setVisibility(View.GONE);
					}

					final Dialog nagDialog = new Dialog(ShowCharityActivity.this,
							android.R.style.Theme_Black);
					nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					nagDialog.setCancelable(true);
					nagDialog.setContentView(R.layout.preview_image);
					ImageView ivPreview = (ImageView) nagDialog
							.findViewById(R.id.preview_image);
					ivPreview.setImageBitmap(bitmap);

					img.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {

							nagDialog.show();
						}
					});

					ivPreview.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View arg0) {

							nagDialog.dismiss();
						}
					});

					
					txtPhone.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent callIntent = new Intent(Intent.ACTION_CALL);
						    callIntent.setData(Uri.parse("tel:" + telefoonMonument));
						    startActivity(callIntent);
						}
					});
					
					txtWebsite.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent i = new Intent(Intent.ACTION_VIEW);
							i.setData(Uri.parse(websiteMonument));
							startActivity(i);
						}
					});
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.charity_details_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case android.R.id.home:
			// app icon in action bar clicked; go home
			Intent intentHome = new Intent(this, AllCharitiesActivity.class);
			intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intentHome);
			break;

		default:
			break;
		}

		return true;
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	public static void clearBitmap(Bitmap bitmap) {

		bitmap.recycle();

		System.gc();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (bitmap != null) {
			clearBitmap(bitmap);
		}
		PlacesLoader.clearSinglePlace();
	}

}
