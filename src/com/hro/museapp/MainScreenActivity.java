package com.hro.museapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.hro.museapp.map.ClusteringMapActivity;
import com.hro.museapp.map.MyMapActivity;

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
