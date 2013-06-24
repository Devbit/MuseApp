package com.hro.museapp;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.example.androidhive.R;
import com.hro.museapp.AllPlacesActivity.LoadAllPlaces;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FavouritesList extends ListActivity {

	private ProgressDialog pDialog;

	ArrayList<HashMap<String, String>> favouritesList;

	SharedPreferences preferences = null;

	ArrayList<HashMap<String, String>> favourites;

	private static final String TAG_MID = "ID";
	private static final String TAG_TITLE = "title";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_places);

		preferences = this.getSharedPreferences("myAppPrefs",
				Context.MODE_PRIVATE);

		favourites = new ArrayList<HashMap<String, String>>();

		new LoadAllPlaces().execute();

		ListView lv = getListView();

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// getting values from selected ListItem
				String mid = ((TextView) view.findViewById(R.id.mid)).getText()
						.toString();

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

	// Response from Edit Place Activity
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if result code 100
		if (resultCode == 100) {
			// if result code 100 is received
			// means user edited/deleted place
			// reload this screen again
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}

	}

	/**
	 * Background Async Task to Load all place by making HTTP Request
	 * */
	class LoadAllPlaces extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			/*
			 * pDialog = new ProgressDialog(FavouritesList.this);
			 * pDialog.setMessage("Loading places. Please wait...");
			 * pDialog.setIndeterminate(false); pDialog.setCancelable(false);
			 * pDialog.show();
			 */
		}

		/**
		 * getting All places from url
		 * */
		protected String doInBackground(String... args) {
			Map<String, String> favs = (Map<String, String>) preferences
					.getAll();

			ArrayList<String> ids = new ArrayList<String>();
			ArrayList<String> names = new ArrayList<String>();

			for (Map.Entry<String, String> entry : favs.entrySet()) {
				String id = entry.getKey();
				String name = entry.getValue();
				
				if (name.equals("")) {
					continue;
				}

				Log.d("FavList", id);
				Log.d("FavList", name);

				HashMap<String, String> fav = new HashMap<String, String>();

				fav.put(TAG_MID, id);
				fav.put(TAG_TITLE, name);

				favourites.add(fav);

				/*
				 * if (id.equals("id")) ids.add(name); else if
				 * (id.equals("name")) names.add(name);
				 */

			}

			/*
			 * for (int i = 0; i < ids.size(); i++) { String id = ids.get(i);
			 * String name = names.get(i);
			 * 
			 * Log.d("FavList2", id); Log.d("FavList2", name);
			 * 
			 * 
			 * }
			 */

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all places
			// pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					ListAdapter adapter = new SimpleAdapter(
							FavouritesList.this, favourites,
							R.layout.list_item, new String[] { TAG_MID,
									TAG_TITLE }, new int[] { R.id.mid,
									R.id.name });
					// updating listview
					setListAdapter(adapter);
				}
			});

		}

	}

}
