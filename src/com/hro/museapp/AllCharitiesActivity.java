package com.hro.museapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnActionExpandListener;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.SectionIndexer;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class AllCharitiesActivity extends ListActivity {

	// Progress Dialog
	private ProgressDialog pDialog;
	private boolean isExecuting = false;

	ArrayList<HashMap<String, String>> charitiesList;
	
	private int listType;

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PLACES = "places";
	private static final String TAG_MID = "ID";
	private static final String TAG_NAME = "title";
	private static final String TAG_LAT = "latitude";
	private static final String TAG_LONG = "longitude";
	private static final String TAG_CAT = "category";
	private static final String TAG_PHONE = "phone";
	private static final String TAG_WEB = "website";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_places);

		// Hashmap for ListView
		charitiesList = new ArrayList<HashMap<String, String>>();
		
		ActionBar actionBar = getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		new LoadAllPlaces().execute();

		// Get listview
		ListView lv = getListView();
		lv.setFastScrollEnabled(true);

		// on seleting single place
		// launching Edit Place Screen
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			
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
			if (isExecuting) {
				cancel(true);
				return;
			}
			isExecuting = true;
			pDialog = new ProgressDialog(AllCharitiesActivity.this);
			pDialog.setMessage(getString(R.string.loadingPlaces));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All places from url
		 * */
		protected String doInBackground(String... args) {
			charitiesList = PlacesLoader.makeListFromPlaces(PlacesLoader.getCharities());
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all places
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					LinkedList<String> mLinked = new LinkedList<String>();
					
					for (int i = 0; i < charitiesList.size(); i++) {
						HashMap<String, String> map = charitiesList.get(i);
						mLinked.add((String) map.get(TAG_NAME));
					}

					setListAdapter(new MyListAdaptor(AllCharitiesActivity.this, mLinked, charitiesList));
				}
			});
			isExecuting = false;
			listType = PlacesLoader.TYPE_ALL;
		}

	}

	/**
	 * The List row creator
	 */
	class MyListAdaptor extends ArrayAdapter<String> implements SectionIndexer {

		HashMap<String, Integer> alphaIndexer;
		String[] sections;
		LinkedList<String> listItems;
		ArrayList<HashMap<String, String>> charitiesList;
		
		public MyListAdaptor(Context context, LinkedList<String> items, ArrayList<HashMap<String, String>> charitiesList) {
			super(context, R.layout.list_item, R.id.name, items);
			listItems = items;
			this.charitiesList = charitiesList;
			alphaIndexer = new HashMap<String, Integer>();
			int size = items.size();

			for (int x = 0; x < size; x++) {
				String s = items.get(x);

				// get the first letter of the store
				String ch = s.substring(0, 1);
				Character test = ch.charAt(0);
				if (Character.isLetter(test)) {
					ch = ch.toUpperCase();
				}
				// convert to uppercase otherwise lowercase a -z will be sorted
				// after upper A-Z
				//ch = ch.toUpperCase();

				// HashMap will prevent duplicates
				alphaIndexer.put(ch, x);
			}

			Set<String> sectionLetters = alphaIndexer.keySet();

			// create a list from the set to sort
			ArrayList<String> sectionList = new ArrayList<String>(
					sectionLetters);

			Collections.sort(sectionList);

			sections = new String[sectionList.size()];

			sectionList.toArray(sections);
		}
		
		@Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        View v = convertView;
	        if (v == null) {
	                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	                v = vi.inflate(R.layout.list_item, null);
	        }
	        //Lesson o = lessons.get(position);
	        HashMap<String, String> map = charitiesList.get(position);

	        TextView tt = (TextView) v.findViewById(R.id.mid);
	        TextView bt = (TextView) v.findViewById(R.id.name);

	        /*v.setClickable(true);
	        v.setFocusable(true);*/
	        /*bt.setFocusable(true);
	        bt.setClickable(true);*/
	        
	        tt.setText(map.get(TAG_MID));
	        bt.setText(map.get(TAG_NAME));
	        return v;
	    }

		public int getPositionForSection(int section) {
			if (section > 0)
				section = section - 1;
			return alphaIndexer.get(sections[section]);
		}

		public int getSectionForPosition(int position) {
			return 0;
		}

		public Object[] getSections() {
			return sections;
		}
	}
}
