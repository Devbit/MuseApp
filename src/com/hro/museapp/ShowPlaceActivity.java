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

	private Context con;

	int duration = Toast.LENGTH_SHORT;

	// JSON parser class
	JSONParser jsonParser = new JSONParser();

	GPSTracker gps;

	// single place url
	private static final String url_place_detials = "http://jsonapp.tk/get_place_details.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PLACE = "places";
	private static final String TAG_MID = "ID";
	private static final String TAG_NAME = "name";
	private static final String TAG_TITLE = "title";
	private static final String TAG_ADDRESS = "address";
	private static final String TAG_CITY = "city";
	private static final String TAG_INFO = "otherinfo";
	private static final String TAG_IMAGE = "thumb";
	private static final String TAG_LAT = "latitude";
	private static final String TAG_LON = "longitude";
	private static final String TAG_PHONE = "phone";
	private static final String TAG_WEB = "website";

	private static String titelMonument;
	private static String idMonument;
	private static String beschrijvingMonument;
	private static String locatieMonument;
	private static String afbeeldingMonument;
	private static String longMonument;
	private static String latMonument;
	private static String websiteMonument;
	private static String telefoonMonument;

	Bitmap bitmap = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_place);

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
			pDialog = new ProgressDialog(ShowPlaceActivity.this);
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
					longMonument = place.getString(TAG_LON);
					latMonument = place.getString(TAG_LAT);
					beschrijvingMonument = Html.fromHtml(
							place.getString(TAG_INFO)).toString();

					if (place.getString(TAG_ADDRESS).equals("")) {
						locatieMonument = place.getString(TAG_CITY);
					} else {
						locatieMonument = place.getString(TAG_ADDRESS) + ", "
								+ place.getString(TAG_CITY);
					}
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
					TextView labelAddress = (TextView) findViewById(R.id.address);
					TextView txtAddress = (TextView) findViewById(R.id.inputAddress);
					TextView txtInfo = (TextView) findViewById(R.id.inputInfo);
					TextView txtWebsite = (Button) findViewById(R.id.inputWebsite);
					TextView labelWebsite = (TextView) findViewById(R.id.labelWebsite);
					TextView txtPhone = (Button) findViewById(R.id.inputPhone);
					TextView labelPhone = (TextView) findViewById(R.id.labelPhone);

					Button mapButton = (Button) findViewById(R.id.mapBtn);
					Button navButton = (Button) findViewById(R.id.navBtn);
					Button callButton = (Button) findViewById(R.id.callBtn);

					if (locatieMonument.equals("")) {
						labelAddress.setVisibility(View.GONE);
						txtAddress.setVisibility(View.GONE);
					}

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
					txtAddress.setText(locatieMonument);
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

					final Dialog nagDialog = new Dialog(ShowPlaceActivity.this,
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

					mapButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent i = new Intent(getApplicationContext(),
									ClusteringMapActivity.class);
							i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
							i.putExtra("type", PlacesLoader.TYPE_SINGLE);
							startActivity(i);
						}
					});

					navButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							gps = new GPSTracker(ShowPlaceActivity.this);
							double latitude = 0;
							double longitude = 0;

							// check if GPS enabled
							if (gps.canGetLocation()) {

								double latitude1 = gps.getLatitude();
								double longitude1 = gps.getLongitude();

								latitude = latitude1;
								longitude = longitude1;

							} else {
								// can't get location
								// GPS or Network is not enabled
								// Ask user to enable GPS/network in settings
								gps.showSettingsAlert();
							}

							Intent intent = new Intent(Intent.ACTION_VIEW, Uri
									.parse("http://maps.google.com/maps?"
											+ "saddr=" + latitude + ","
											+ longitude + "&daddr="
											+ latMonument + "," + longMonument));
							intent.setClassName("com.google.android.apps.maps",
									"com.google.android.maps.MapsActivity");
							startActivity(intent);
						}
					});

					callButton.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Intent callIntent = new Intent(Intent.ACTION_CALL);
						    callIntent.setData(Uri.parse("tel:" + telefoonMonument));
						    startActivity(callIntent);
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

	// Method:
	private Intent generateCustomChooserIntent(Intent prototype,
			String[] forbiddenChoices) {
		List<Intent> targetedShareIntents = new ArrayList<Intent>();
		List<HashMap<String, String>> intentMetaInfo = new ArrayList<HashMap<String, String>>();
		Intent chooserIntent;

		Intent dummy = new Intent(prototype.getAction());
		dummy.setType(prototype.getType());
		List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(
				dummy, 0);

		if (!resInfo.isEmpty()) {
			for (ResolveInfo resolveInfo : resInfo) {
				if (resolveInfo.activityInfo == null
						|| Arrays.asList(forbiddenChoices).contains(
								resolveInfo.activityInfo.packageName))
					continue;

				HashMap<String, String> info = new HashMap<String, String>();
				info.put("packageName", resolveInfo.activityInfo.packageName);
				info.put("className", resolveInfo.activityInfo.name);
				info.put("simpleName", String.valueOf(resolveInfo.activityInfo
						.loadLabel(getPackageManager())));
				intentMetaInfo.add(info);
			}

			if (!intentMetaInfo.isEmpty()) {
				// sorting for nice readability
				Collections.sort(intentMetaInfo,
						new Comparator<HashMap<String, String>>() {
							@Override
							public int compare(HashMap<String, String> map,
									HashMap<String, String> map2) {
								return map.get("simpleName").compareTo(
										map2.get("simpleName"));
							}
						});

				// create the custom intent list
				for (HashMap<String, String> metaInfo : intentMetaInfo) {
					Intent targetedShareIntent = (Intent) prototype.clone();
					targetedShareIntent.setPackage(metaInfo.get("packageName"));
					targetedShareIntent.setClassName(
							metaInfo.get("packageName"),
							metaInfo.get("className"));
					targetedShareIntents.add(targetedShareIntent);
				}

				chooserIntent = Intent.createChooser(targetedShareIntents
						.remove(targetedShareIntents.size() - 1),
						getString(R.string.share));
				chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
						targetedShareIntents.toArray(new Parcelable[] {}));
				return chooserIntent;
			}
		}

		return Intent.createChooser(prototype, getString(R.string.hello));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.places_details_menu, menu);

		SharedPreferences preferences = con.getSharedPreferences("myAppPrefs",
				Context.MODE_PRIVATE);
		String test = preferences.getString(idMonument, "");
		MenuItem favItem = menu.getItem(0);
		if (!(test == "")) {
			favItem.setIcon(R.drawable.ic_action_important_normal);

		} else {
			favItem.setIcon(R.drawable.ic_action_not_important);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_share:

			// blacklist
			String[] blacklist = new String[] { "com.any.package",
					"net.other.package" };
			// your share intent
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_TEXT, beschrijvingMonument);
			intent.putExtra(android.content.Intent.EXTRA_SUBJECT, titelMonument);
			// ... anything else you want to add
			// invoke custom chooser
			startActivity(generateCustomChooserIntent(intent, blacklist));

			break;

		case R.id.action_favourite:

			SharedPreferences preferences = this.getSharedPreferences(
					"myAppPrefs", Context.MODE_PRIVATE);
			String test = preferences.getString(idMonument, "");
			if (test.equals("")) {
				Toast addFav = Toast.makeText(con, "Favoriet toegevoegd",
						duration);
				addFav.show();
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString(idMonument, titelMonument);
				editor.commit();
				item.setIcon(R.drawable.ic_action_important_normal);
			} else {
				Toast delFav = Toast.makeText(con, "Favoriet verwijderd",
						duration);
				delFav.show();
				SharedPreferences.Editor editor = preferences.edit();
				editor.putString(idMonument, "");
				editor.commit();
				item.setIcon(R.drawable.ic_action_not_important);
				setResult(100);
			}
			break;

		case R.id.action_cal:
			Calendar cal = Calendar.getInstance();
			Intent calIntent = new Intent(Intent.ACTION_EDIT);
			calIntent.setType("vnd.android.cursor.item/event");
			calIntent.putExtra("beginTime", cal.getTimeInMillis());
			calIntent.putExtra("title", titelMonument);
			calIntent.putExtra("eventLocation", locatieMonument);
			startActivity(calIntent);

			break;

		case android.R.id.home:
			// app icon in action bar clicked; go home
			Intent intentHome = new Intent(this, AllPlacesActivity.class);
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
