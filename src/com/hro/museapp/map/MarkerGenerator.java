/*
 * Copyright (C) 2013 Maciej GÃ³rski
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

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import pl.mg6.android.maps.extensions.GoogleMap;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hro.museapp.PlacesLoader;

public class MarkerGenerator {
	static final String TAG_MID = "ID";
	static final String TAG_NAME = "title";
	private static final String TAG_LAT = "latitude";
	private static final String TAG_LONG = "longitude";
	private static final String TAG_CAT = "category";
	
	private static JSONArray places;
	static HashMap<String, MarkerOptions> mapPlaceToId;
	
	public static HashMap<String, MarkerOptions> addMarkers(GoogleMap map) {
		if (PlacesLoader.hasSingle()) {
			places = PlacesLoader.getSinglePlace();
		} else {
			if (PlacesLoader.getLastSearch().equals("")) {
				places = PlacesLoader.getPlaces();
			} else {
				places = PlacesLoader.getSearchResults();
			}
		}
		
		mapPlaceToId = new HashMap<String, MarkerOptions>();
		try {
			for (int i = 0; i < places.length(); i++) {
				JSONObject c = places.getJSONObject(i);
				String title = c.getString(TAG_NAME);
				String lat = c.getString(TAG_LAT);
				String lon = c.getString(TAG_LONG);
				String mid = c.getString(TAG_MID);
				String cat = c.getString(TAG_CAT);
				LatLng loc = new LatLng(0, 0);
				try {
					loc = new LatLng(Double.parseDouble(lat),
							Double.parseDouble(lon));
				} catch (NumberFormatException nfe) {
					continue;
				}
				//mapPlaceToId.put(title, mid);
				/*Marker m = map
						.addMarker(new MarkerOptions()
								.position(loc)
								.title(title)
								.icon(BitmapDescriptorFactory
										.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
				m.setData(mid);*/
				
				MarkerOptions m = new MarkerOptions().position(loc).title(title);
				
				if(cat.equals("Monument")) {
					m.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
				}
				else {
					m.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
				}
				mapPlaceToId.put(mid, m);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mapPlaceToId;
		}
	}

