package com.example.androidhive;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.androidhive.ShowPlaceActivity.GetPlaceDetails;
import com.example.androidhive.map.ClusteringMapActivity;
import com.example.androidhive.map.MyMapActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainScreenActivity extends Activity {
	Button btnViewPlaces;
	Button btnShowVersion;
	Button btnMapButton;
	Button btnCluster;
	Button btnFavourites;

	JSONParser jParser = new JSONParser();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);

		// Buttons
		btnViewPlaces = (Button) findViewById(R.id.btnViewPlaces);
		btnShowVersion = (Button) findViewById(R.id.btnShowVersion);
		btnMapButton = (Button) findViewById(R.id.btnMapButton);
		btnCluster = (Button) findViewById(R.id.btnCluster); 
		btnFavourites = (Button) findViewById(R.id.btnFavourites);

		// view places click event
		btnViewPlaces.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// Launching All places Activity
				Intent i = new Intent(getApplicationContext(),
						AllPlacesActivity.class);
				startActivity(i);

			}
		});

		btnShowVersion.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// Launching All places Activity
				Intent i = new Intent(getApplicationContext(), null);
				startActivity(i);

			}
		});

		btnMapButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// Launching All places Activity
				Intent i = new Intent(getApplicationContext(),
						MyMapActivity.class);
				startActivity(i);

			}
		});
		
		btnCluster.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// Launching All places Activity
				Intent i = new Intent(getApplicationContext(),
						ClusteringMapActivity.class);
				startActivity(i);
			}
		});
		
		btnFavourites.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// Launching All places Activity
				Intent i = new Intent(getApplicationContext(),
						FavouritesList.class);
				startActivity(i);
			}
		});
		

	}
}
