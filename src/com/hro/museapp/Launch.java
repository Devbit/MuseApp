package com.hro.museapp;

import java.io.File;

import com.hro.museapp.map.GPSTracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

public class Launch extends Activity {
	private ProgressDialog pDialog;
	
	private boolean noInternet() {
	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
		getActionBar().hide();
		if(!noInternet()) {
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Geen internetverbinding");
			alertDialog.setMessage("U heeft een internetverbinding nodig om deze applicatie te gebruiken");
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   System.exit(0);
		           }
		       });
			alertDialog.show();
			return;
		}
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
			GPSTracker gps = new GPSTracker(Launch.this);
			
			Log.d("DEBUG", "Boven de context");
			boolean update = cache.check();
			Log.d("DEBUG", "Onder de context");
			
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