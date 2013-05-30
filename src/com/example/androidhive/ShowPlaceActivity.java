package com.example.androidhive;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
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
	private static final String TAG_IMAGE = "image";

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
		
		
		 
		// Method:
		private Intent generateCustomChooserIntent(Intent prototype, String[] forbiddenChoices) {
			List<Intent> targetedShareIntents = new ArrayList<Intent>();
			List<HashMap<String, String>> intentMetaInfo = new ArrayList<HashMap<String, String>>();
			Intent chooserIntent;
		 
			Intent dummy = new Intent(prototype.getAction());
			dummy.setType(prototype.getType());
			List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(dummy, 0);
		 
			if (!resInfo.isEmpty()) {
				for (ResolveInfo resolveInfo : resInfo) {
					if (resolveInfo.activityInfo == null || Arrays.asList(forbiddenChoices).contains(resolveInfo.activityInfo.packageName))
						continue;
		 
					HashMap<String, String> info = new HashMap<String, String>();
					info.put("packageName", resolveInfo.activityInfo.packageName);
					info.put("className", resolveInfo.activityInfo.name);
					info.put("simpleName", String.valueOf(resolveInfo.activityInfo.loadLabel(getPackageManager())));
					intentMetaInfo.add(info);
				}
		 
				if (!intentMetaInfo.isEmpty()) {
					// sorting for nice readability
					Collections.sort(intentMetaInfo, new Comparator<HashMap<String, String>>() {
						@Override
						public int compare(HashMap<String, String> map, HashMap<String, String> map2) {
							return map.get("simpleName").compareTo(map2.get("simpleName"));
						}
					});
		 
					// create the custom intent list
					for (HashMap<String, String> metaInfo : intentMetaInfo) {
						Intent targetedShareIntent = (Intent) prototype.clone();
						targetedShareIntent.setPackage(metaInfo.get("packageName"));
						targetedShareIntent.setClassName(metaInfo.get("packageName"), metaInfo.get("className"));
						targetedShareIntents.add(targetedShareIntent);
					}
		 
					chooserIntent = Intent.createChooser(targetedShareIntents.remove(targetedShareIntents.size() - 1), getString(R.string.hello));
					chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
					return chooserIntent;
				}
			}
		 
			return Intent.createChooser(prototype, getString(R.string.hello));
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
							
							
							final String titelMonument;
							final String beschrijvingMonument;
							
							String imgId;
							try {
								  ImageView i = (ImageView)findViewById(R.id.afbeelding);
								  String imageUrl = "http://www.4en5mei.nl/";
								imgId = place.getString(TAG_IMAGE);
								Bitmap bitmap = BitmapFactory.decodeStream((InputStream)new URL(imageUrl + imgId).getContent());
								  i.setImageBitmap(bitmap);
								  
								  final Dialog nagDialog = new Dialog(ShowPlaceActivity.this,android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
						            nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE); 
						            nagDialog.setCancelable(false);
						            nagDialog.setContentView(R.layout.preview_image);
						            Button btnClose = (Button)nagDialog.findViewById(R.id.btnClose);
						            ImageView ivPreview = (ImageView)nagDialog.findViewById(R.id.preview_image);
						            ivPreview.setImageBitmap(bitmap);
						            
									  i.setOnClickListener(new OnClickListener() {
							                @Override
							                public void onClick(View arg0) {

							                	nagDialog.show();
							                }
							            });

						            btnClose.setOnClickListener(new OnClickListener() {
						                @Override
						                public void onClick(View arg0) {

						                    nagDialog.dismiss();
						                }
						            }); 
						            
						            
						            titelMonument = place.getString(TAG_TITLE);
						            beschrijvingMonument = place.getString(TAG_TITLE);
						            
									Button share = (Button)findViewById(R.id.share);
									share.setOnClickListener(new OnClickListener() {
						                @Override
						                public void onClick(View arg0) {
						                	// blacklist
											String[] blacklist = new String[]{"com.any.package", "net.other.package"};
											// your share intent
											Intent intent = new Intent(Intent.ACTION_SEND);
											intent.setType("text/plain");
											intent.putExtra(Intent.EXTRA_TEXT, beschrijvingMonument);
											intent.putExtra(android.content.Intent.EXTRA_SUBJECT, titelMonument);
											// ... anything else you want to add
											// invoke custom chooser
											startActivity(generateCustomChooserIntent(intent, blacklist));
						                }
						            });
								  
								} catch (MalformedURLException e) {
								  e.printStackTrace();
								} catch (IOException e) {
								  e.printStackTrace();
								}

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
