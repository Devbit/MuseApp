package com.hro.museapp;

import com.hro.museapp.map.GPSTracker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;

public class Launch extends Activity {
	private ProgressDialog pDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
		getActionBar().hide();
		new BackgroundUpdate().execute();
	}

	class BackgroundUpdate extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Launch.this);
			pDialog.setMessage(getString(R.string.update_check));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All places from url
		 * */
		protected String doInBackground(String... args) {
			CacheHandler cache = new CacheHandler(getApplicationContext());
//			GPSTracker gps = new GPSTracker(Launch.this);
//			PlacesLoader.setGPS(gps);
			boolean update = cache.check();
			if (update) {
				publishProgress();
				cache.update();
			}
			PlacesLoader.setCache(CacheHandler.PLACES_CACHE, cache.getCache(CacheHandler.PLACES_CACHE));
			PlacesLoader.setCache(CacheHandler.CHARITY_CACHE, cache.getCache(CacheHandler.CHARITY_CACHE));
			return null;
		}
		
		@Override
		protected void onProgressUpdate(String... values) {
			pDialog.setMessage(getString(R.string.update));
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all places
			pDialog.dismiss();

			Thread runStartup = new Thread() {
				public void run() {
					Intent intent = new Intent("com.hro.museapp.START");
					startActivity(intent);
					finish();
				}
			};
			SystemClock.sleep(500);
			runStartup.start();

		}

	}

}