package com.example.androidhive;

import com.example.androidhive.AllPlacesActivity.LoadAllPlaces;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

public class Start extends Activity 
{
	Button button1, button2, button3, button4, button5;
	private ProgressDialog pDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		new BackgroundUpdate().execute();
		
		button1 = (Button)findViewById(R.id.button1); // Top button
		button2 = (Button)findViewById(R.id.button2); // Mid left
		button3 = (Button)findViewById(R.id.button3); // Mid right
		button4 = (Button)findViewById(R.id.button4); // Bottom left
		button5 = (Button)findViewById(R.id.button5); // Bottom right
		
		
		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(), AllPlacesActivity.class);
				startActivity(i);
				
			}});
			
		button2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// Launching All places Activity
				Intent i = new Intent("com.example.androidhive.MAIN");
				startActivity(i);
				
			}});
	}
	
	class BackgroundUpdate extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(Start.this);
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
			// updating UI from Background Thread
			

		}

	}
	
}