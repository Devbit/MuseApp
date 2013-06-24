/*
 * Copyright (C) 2013 Maciej Górski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hro.museapp.map;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import pl.mg6.android.maps.extensions.ClusteringSettings;
import pl.mg6.android.maps.extensions.GoogleMap;
import pl.mg6.android.maps.extensions.GoogleMap.InfoWindowAdapter;
import pl.mg6.android.maps.extensions.GoogleMap.OnInfoWindowClickListener;
import pl.mg6.android.maps.extensions.Marker;
import pl.mg6.android.maps.extensions.SupportMapFragment;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.LatLngBounds.Builder;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hro.museapp.AllPlacesActivity;
import com.hro.museapp.PlacesLoader;
import com.hro.museapp.R;
import com.hro.museapp.ShowPlaceActivity;
import com.hro.museapp.Start;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class ClusteringMapActivity extends FragmentActivity {

	private static final double[] CLUSTER_SIZES = new double[] { 180, 160, 144,
			120, 96 };

	private GoogleMap map;
	private View mapView;
	private ProgressDialog pDialog;

	GPSTracker gps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cluster_map);
		
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);

		FragmentManager fm = getSupportFragmentManager();
		SupportMapFragment f = (SupportMapFragment) fm
				.findFragmentById(R.id.map);
		map = f.getExtendedMap();
		
		gps = new GPSTracker(ClusteringMapActivity.this);

		// mapView = (MapView) this.findViewById(R.id.map);

		float cameraZoom = 8;
		LatLng cameraLatLng = new LatLng(52.281602, 5.503235);
		if (savedInstanceState != null) {
			double savedLat = savedInstanceState.getDouble("lat");
			double savedLng = savedInstanceState.getDouble("lng");
			cameraLatLng = new LatLng(savedLat, savedLng);

			cameraZoom = savedInstanceState.getFloat("zoom", 12);
		}
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraLatLng,
				cameraZoom));

		map.setClustering(new ClusteringSettings().iconDataProvider(
				new DemoIconProvider(getResources())).addMarkersDynamically(
				true));
		map.setMyLocationEnabled(true);

		map.setInfoWindowAdapter(new InfoWindowAdapter() {

			private TextView tv;
			{
				tv = new TextView(ClusteringMapActivity.this);
				tv.setTextColor(Color.BLACK);
			}

			private Collator collator = Collator.getInstance();
			private Comparator<Marker> comparator = new Comparator<Marker>() {
				public int compare(Marker lhs, Marker rhs) {
					String leftTitle = lhs.getTitle();
					String rightTitle = rhs.getTitle();
					if (leftTitle == null && rightTitle == null) {
						return 0;
					}
					if (leftTitle == null) {
						return 1;
					}
					if (rightTitle == null) {
						return -1;
					}
					return collator.compare(leftTitle, rightTitle);
				}
			};

			@Override
			public View getInfoWindow(Marker marker) {
				return null;
			}

			@Override
			public View getInfoContents(Marker marker) {
				if (marker.isCluster()) {
					List<Marker> markers = marker.getMarkers();
					int i = 0;
					String text = "";
					while (i < 3 && markers.size() > 0) {
						Marker m = Collections.min(markers, comparator);
						String title = m.getTitle();
						if (title == null) {
							break;
						}
						text += title + "\n";
						markers.remove(m);
						i++;
					}
					if (text.length() == 0) {
						text = "Markers with mutable data";
					} else if (markers.size() > 0) {
						text += "and " + markers.size() + " more...";
					} else {
						text = text.substring(0, text.length() - 1);
					}
					tv.setText(text);
					return tv;
				} else {
					String title = marker.getTitle();
					tv.setText(title);
					return tv;
				}
			}
		});

		map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker marker) {
				if (marker.isCluster()) {
					List<Marker> markers = marker.getMarkers();
					Builder builder = LatLngBounds.builder();
					for (Marker m : markers) {
						builder.include(m.getPosition());
					}
					LatLngBounds bounds = builder.build();
					map.animateCamera(CameraUpdateFactory.newLatLngBounds(
							bounds,
							getResources().getDimensionPixelSize(
									R.dimen.padding)));
				} else {
					// String title = marker.getTitle();
					// String mid = MarkerGenerator.mapPlaceToId.get(title);
					String mid = (String) marker.getData();

					Intent in = new Intent(getApplicationContext(),
							ShowPlaceActivity.class);
					// sending mid to next activity
					in.putExtra(MarkerGenerator.TAG_MID, mid);

					// starting new activity and expecting some response back
					startActivityForResult(in, 100);
				}
			}
		});

		// MarkerGenerator.addMarkers(map);
		new AddMarkersInBackground().execute();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.map_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.list_view:
			Intent i = new Intent(getApplicationContext(),
					AllPlacesActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			startActivity(i);

			break;
			
		case android.R.id.home:
			// app icon in action bar clicked; go home
            Intent intentHome = new Intent(this, Start.class);
            intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentHome);
            break;

		default:
			break;
		}

		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private void onDataUpdate() {
		Marker m = map.getMarkerShowingInfoWindow();
		if (m != null && !m.isCluster() && m.getData() instanceof String) {
			m.showInfoWindow();
		}
	}

	void updateClustering(int clusterSizeIndex, boolean enabled) {
		ClusteringSettings clusteringSettings = new ClusteringSettings();
		clusteringSettings.addMarkersDynamically(true);

		if (enabled) {
			clusteringSettings.iconDataProvider(new DemoIconProvider(
					getResources()));

			double clusterSize = CLUSTER_SIZES[clusterSizeIndex];
			clusteringSettings.clusterSize(clusterSize);
		} else {
			clusteringSettings.enabled(false);
		}
		map.setClustering(clusteringSettings);
	}

	class AddMarkersInBackground extends
			AsyncTask<String, String, HashMap<String, MarkerOptions>> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(ClusteringMapActivity.this);
			pDialog.setMessage("Adding places...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All places from url
		 * 
		 * @return
		 * */
		protected HashMap<String, MarkerOptions> doInBackground(String... args) {
			Log.d("MarkerGenerator", "Started");
			HashMap<String, MarkerOptions> markers = MarkerGenerator
					.addMarkers(map);
			Log.d("MarkerGenerator", "Done");
			return markers;
		}

		protected void onPostExecute(HashMap<String, MarkerOptions> result) {
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

			pDialog.dismiss();
			Iterator it = result.entrySet().iterator();
			boolean getMarkerLoc = (!PlacesLoader.getLastSearch().equals("") || PlacesLoader.hasSingle());
			boolean firstEntry = true;
			while (it.hasNext()) {
				HashMap.Entry pairs = (HashMap.Entry) it.next();
				// Log.d("Test", pairs.getKey() + " = " + pairs.getValue());
				
				MarkerOptions opt = (MarkerOptions) pairs.getValue();
				if (opt.getPosition().equals(new LatLng(-1.0,-1.0))) {
					continue;
				}
				
				if (getMarkerLoc && firstEntry) {
					latitude = opt.getPosition().latitude;
					longitude = opt.getPosition().longitude;
					firstEntry = false;
				}
				Marker m = map.addMarker(opt);
				m.setData(pairs.getKey());
				it.remove(); // avoids a ConcurrentModificationException
			}

			float cameraZoom = 16;
			
			//Location loc = map.getMyLocation();
/*			LatLng cameraLatLng = new LatLng(loc.getLatitude(),
					loc.getLongitude());*/
			//if (loc == null)
			LatLng loc = new LatLng(latitude, longitude);
			Log.d("LAT", String.valueOf(latitude));
			Log.d("LON", String.valueOf(longitude));
			Log.d("LATLNG", String.valueOf(loc));
			map.animateCamera(
					CameraUpdateFactory.newLatLngZoom(loc, cameraZoom),
					2000, null);
		}

	}

}
