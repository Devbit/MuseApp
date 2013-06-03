package com.example.androidhive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MyMapActivity extends Activity implements LocationListener{
	private static final String TAG_MID = "ID";
	private static final String TAG_NAME = "title";
	private static final String TAG_LAT = "latitude";
	private static final String TAG_LONG = "longitude";

    public static double latitude;
    public static double longitude;
    public LatLng cameraLatLng;
    public float cameraZoom;
    private GoogleMap googleMap;
    private int mapType = GoogleMap.MAP_TYPE_NORMAL;
    private JSONArray places;
    private HashMap<String, String> mapPlaceToId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mymap);

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment =  (MapFragment) fragmentManager.findFragmentById(R.id.map);
        googleMap = mapFragment.getMap();
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        
        places = PlacesLoader.getPlaces();
        mapPlaceToId = new HashMap<String, String>();
        
        // Get Current location and center
        LocationManager mlocManager=null;
        LocationListener mlocListener;
        mlocManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyMapActivity();
        mlocManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 0, 0, mlocListener);

       if (mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
           if(MyMapActivity.latitude>0)
           {
        	cameraLatLng = new LatLng(MyMapActivity.latitude,MyMapActivity.longitude);
        	cameraZoom = 12;
           	final AlertDialog.Builder alert=new AlertDialog.Builder(this);
            alert.setTitle("Wait");
            alert.setMessage("Latitude:- " + MyMapActivity.latitude + '\n' + "Longitude:- " + MyMapActivity.longitude + '\n');
            alert.setPositiveButton("OK", null);
            alert.show();
            }
            else
            {
           	 cameraLatLng = new LatLng(52.113252,5.361328);
           	 cameraZoom = 7;
            	final AlertDialog.Builder alert=new AlertDialog.Builder(this);
                 alert.setTitle("Wait");
                 alert.setMessage("GPS in progress, please wait.");
                 alert.setPositiveButton("OK", null);
                 alert.show();
             }
         } else {
        	 Toast.makeText(getApplicationContext(),"GPS is not turned on...", Toast.LENGTH_LONG).show();
        	 cameraLatLng = new LatLng(52.113252,5.361328);
        	 cameraZoom = 7;
         }
       // End getting location
     
        
        
        try {
        	for (int i = 0; i < places.length(); i++) {
				JSONObject c = places.getJSONObject(i);
				String title = c.getString(TAG_NAME);
				String lat = c.getString(TAG_LAT);
				String lon = c.getString(TAG_LONG);
				String mid = c.getString(TAG_MID);
				LatLng loc = new LatLng(0, 0);
				try {
					loc = new LatLng(Double.parseDouble(lat),
							Double.parseDouble(lon));
				} catch (NumberFormatException nfe) {
					continue;
				}
				mapPlaceToId.put(title, mid);
				googleMap
						.addMarker(new MarkerOptions()
								.position(loc)
								.title(title)
								/* .snippet(mid) */
								.icon(BitmapDescriptorFactory
										.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		googleMap.getUiSettings().setCompassEnabled(true);
		googleMap.getUiSettings().setZoomControlsEnabled(true);
		googleMap.getUiSettings().setMyLocationButtonEnabled(true);

		float cameraZoom = 10;

		if (savedInstanceState != null) {
			mapType = savedInstanceState.getInt("map_type",
					GoogleMap.MAP_TYPE_NORMAL);

			double savedLat = savedInstanceState.getDouble("lat");
			double savedLng = savedInstanceState.getDouble("lng");
			cameraLatLng = new LatLng(savedLat, savedLng);

			cameraZoom = savedInstanceState.getFloat("zoom", 10);
		}

		googleMap.setMapType(mapType);
		googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cameraLatLng,
				cameraZoom));
		googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker marker) {
				String title = marker.getTitle();
				String mid = mapPlaceToId.get(title);
				// Starting new intent
				Intent in = new Intent(getApplicationContext(),
						ShowPlaceActivity.class);
				// sending mid to next activity
				in.putExtra(TAG_MID, mid);

				// starting new activity and expecting some response back
				startActivityForResult(in, 100);

			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.map_styles_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);

		switch (item.getItemId()) {
		case R.id.normal_map:
			mapType = GoogleMap.MAP_TYPE_NORMAL;
			break;

		case R.id.satellite_map:
			mapType = GoogleMap.MAP_TYPE_SATELLITE;
			break;

		case R.id.terrain_map:
			mapType = GoogleMap.MAP_TYPE_TERRAIN;
			break;

		case R.id.hybrid_map:
			mapType = GoogleMap.MAP_TYPE_HYBRID;
			break;
		}

		googleMap.setMapType(mapType);
		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		// save the map type so when we change orientation, the mape type can be restored
		LatLng cameraLatLng = googleMap.getCameraPosition().target;
		float cameraZoom = googleMap.getCameraPosition().zoom;
		outState.putInt("map_type", mapType);
		outState.putDouble("lat", cameraLatLng.latitude);
		outState.putDouble("lng", cameraLatLng.longitude);
		outState.putFloat("zoom", cameraZoom);
        	
        }


	@Override
	public void onLocationChanged(Location loc) {
        loc.getLatitude();
        loc.getLongitude();
        latitude=loc.getLatitude();
        longitude=loc.getLongitude();
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}
}
