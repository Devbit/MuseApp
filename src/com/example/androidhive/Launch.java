package com.example.androidhive;

import com.example.androidhive.R;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.Menu;

public class Launch extends Activity 
{
	private ProgressDialog pDialog;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
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
			pDialog.setMessage("Checking for updates...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All places from url
		 * */
		protected String doInBackground(String... args) {
			CacheHandler cache = new CacheHandler(getApplicationContext());
			boolean update = cache.check();
			if (update) {
				cache.update();
			}
			PlacesLoader.setCache(cache.getCache());
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all places
			pDialog.dismiss();
			
			Thread runStartup = new Thread()
			{
				public void run()
				{
					Intent intent = new Intent("com.example.androidhive.START");
					startActivity(intent);
					finish();
				}
			};
			
			runStartup.start();
			SystemClock.sleep(500);
			
		}

	}

    
}