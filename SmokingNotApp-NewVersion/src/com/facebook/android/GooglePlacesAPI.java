package com.facebook.android;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class GooglePlacesAPI {
	private Context context;
	private WebRequest req;
	private SharedPreferences sh_pref;
	public GeocoderEngine mGeoEng;

	public static final int ALLOWED_RADIUS = 100;
	public static final int LOOK_AROUND_RADIUS = 100;
	public static final int MAX_RADIUS = 50000;

	public boolean[] chosen_cats;
	public String[] cats;

	public GooglePlacesAPI(Context context) {
		this.context = context;
		req = new WebRequest();
		mGeoEng = new GeocoderEngine(context);
		cats = context.getResources().getStringArray(R.array.types_array);
		chosen_cats = new boolean[cats.length];

		sh_pref = context.getSharedPreferences("Places", 0);
		for (int i = 0; i < cats.length; i++) {
			chosen_cats[i] = sh_pref.getBoolean(cats[i],
					(cats[i].equals("other") ? false : true));
		}
	}

	public void setChosenCats(boolean[] ch_cats) {

		chosen_cats = ch_cats;
		SharedPreferences.Editor pref_edit = sh_pref.edit();
		for (int i = 0; i < cats.length; i++) {
			pref_edit.putBoolean(cats[i], ch_cats[i]);
		}
		pref_edit.commit();

	}

	public ArrayList<String> getChosenCats() {
		ArrayList<String> chosen_cats_list = new ArrayList<String>();

		for (int i = 0; i < chosen_cats.length; i++) {
			if (chosen_cats[i])
				chosen_cats_list.add(cats[i]);
		}
		Log.i("ERIC", chosen_cats_list.toString());
		return chosen_cats_list;
	}

	public String getChosenCatsStr() {
		ArrayList<String> lst = getChosenCats();
		if (lst.contains("other"))
			return "";
		else {
			StringBuilder strb = new StringBuilder("&types=");
			for (int i = 0; i < lst.size() - 1; i++)
				strb.append(lst.get(i)).append("|");
			strb.append(lst.get(lst.size() - 1));
			return strb.toString();
		}
	}

	public ArrayList<GooglePlace> getNearby(Location location, int radius)
			throws Throwable {

		String ll = String.valueOf(location.getLatitude()) + ","
				+ String.valueOf(location.getLongitude());

		URL url = new URL(context.getString(R.string.GooglePlacesApiUrl)
				+ "/search/json?key="
				+ context.getString(R.string.GooglePlacesAPIKey) + "&location="
				+ ll + "&sensor=true" + "&radius=" + radius
				+ getChosenCatsStr());

		return getPlaces(url, true, location);
	}

	public ArrayList<GooglePlace> searchPlaces(Location location,
			boolean hasLocation, String searchStr, int radius) throws Throwable {

		String ll = String.valueOf(location.getLatitude()) + ","
				+ String.valueOf(location.getLongitude());

		String url_str = context.getString(R.string.GooglePlacesApiUrl)
				+ "/search/json?key="
				+ context.getString(R.string.GooglePlacesAPIKey) + "&location="
				+ ll + "&sensor=" + String.valueOf(hasLocation) + "&keyword="
				+ searchStr + "&radius=" + radius + getChosenCatsStr();

		url_str = url_str.replace(" ", "%20");
		URL url = new URL(url_str);

		// Log.d("ERIC", url.toURI().toString());

		return getPlaces(url, hasLocation, location);
	}

	public ArrayList<GooglePlace> getPlaces(URL url, boolean by_location,
			Location my_loc) throws Throwable {

		ArrayList<GooglePlace> placesList = new ArrayList<GooglePlace>();

		try {

			Log.d("ERIC", "Opening URL " + url.toString());

			JSONObject jsonObj = req.readJsonFromUrl(url.toString());
			JSONArray places = jsonObj.getJSONArray("results");

			int length = places.length();
			Log.d("ERIC", "length: " + length);
			if (length > 0) {
				for (int i = 0; i < length; i++) {
					JSONObject item = (JSONObject) places.get(i);

					GooglePlace place = new GooglePlace();

					place.id = item.getString("id");
					place.name = item.getString("name");

					JSONObject location = item.getJSONObject("geometry")
							.getJSONObject("location");

					Location loc = new Location(
							LocationManager.PASSIVE_PROVIDER);

					loc.setLatitude(Double.valueOf(location.getString("lat")));
					loc.setLongitude(Double.valueOf(location.getString("lng")));

					place.location = loc;

					place.vicinity = item.optString("vicinity");
					if (place.vicinity.equals(""))
						place.vicinity = mGeoEng.getAddressFromLocation(loc);

					if (by_location)
						place.distance = calculateDistance(my_loc, loc);
					else
						place.distance = -1.0;

					// get location's rating from server

					Gson gson1 = new Gson();
					WebRequest req = new WebRequest();
					String str = null;
					LocationRequest loc_updated = null;
					try {
						JSONObject json2 = req.readJsonFromUrl(context
								.getString(R.string.DatabaseUrl)
								+ "/GetLocation?locationid=" + place.id);
						str = (String) json2.get("location_req");
						Log.w("str=", str);
						if (str.compareTo("NotinDataBase") != 0)
							loc_updated = gson1.fromJson(str,
									LocationRequest.class);
					} catch (JSONException e) {
						Log.e("NearbyAdapter error, can't get response from server, JSON exception",
								e.toString());
						Log.w("str=", str);
					} catch (Exception e) {
						Log.e("NearbyAdapter error, can't get response from server",
								e.toString());
						Log.w("str=", str);
					}
					if (loc_updated != null) {
						place.goodRate = loc_updated.getGoodRate();
						place.badRate = loc_updated.getBadRate();
					} else {
						place.goodRate = 0;
						place.badRate = 0;
					}
					placesList.add(place);
					Log.i("ERIC", "printing place - " + place.toString());

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Collections.sort(placesList, new distanceComparator());
		return placesList;
	}

	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	/* :: This function converts decimal degrees to radians : */
	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	/* :: This function converts radians to decimal degrees : */
	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	private double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}

	private double calculateDistance(Location loc1, Location loc2) {
		Log.i("ERIC", "loc1: " + loc1.toString() + "loc2: " + loc2.toString());
		
		if ((loc1.getLatitude() == loc2.getLatitude()) && (loc1.getLongitude() == loc2.getLongitude()))
			return 0;
		
		double theta = loc1.getLongitude() - loc2.getLongitude();
		double dist = Math.sin(deg2rad(loc1.getLatitude()))
				* Math.sin(deg2rad(loc2.getLatitude()))
				+ Math.cos(deg2rad(loc1.getLatitude()))
				* Math.cos(deg2rad(loc2.getLatitude()))
				* Math.cos(deg2rad(theta));
		dist = Math.acos(dist);
		dist = rad2deg(dist);
		dist = dist * 60 * 1.1515;
		dist = dist * 1.609344 * 1000;// meters
		return (dist);
	}

	public class distanceComparator implements Comparator<GooglePlace> {

		public int compare(GooglePlace place1, GooglePlace place2) {
			return (int) (place1.distance - place2.distance);
		}
	}

	public JSONObject AddPlace(String place_name, String place_type,
			Location placeLocation) {
		JSONObject newPlaceObject = new JSONObject();
		try {
			newPlaceObject.put("name", place_name);
			newPlaceObject.put("accuracy", 20);

			JSONObject newPlaceLocation = new JSONObject();
			newPlaceLocation.put("lat", placeLocation.getLatitude());
			newPlaceLocation.put("lng", placeLocation.getLongitude());

			newPlaceObject.put("location", newPlaceLocation);

			JSONArray placeTypes = new JSONArray();
			placeTypes.put(place_type);
			newPlaceObject.put("types", placeTypes);

			URL url = new URL(context.getString(R.string.GooglePlacesApiUrl)
					+ "/add/json?key="
					+ context.getString(R.string.GooglePlacesAPIKey)
					+ "&sensor=true");
			JSONObject jsonResponse = HttpClient.SendHttpPost(url.toString(),
					newPlaceObject);

			return jsonResponse;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public JSONObject getPlaceDetails(String placeReference) {
		try {

			String requestUrl = context.getString(R.string.GooglePlacesApiUrl)
					+ "/details/json?key="
					+ context.getString(R.string.GooglePlacesAPIKey)
					+ "&sensor=true&reference=" + placeReference;
			JSONObject jsonResponse = req.readJsonFromUrl(requestUrl);

			return jsonResponse;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	class GooglePlaceType {
		public String TypeId;
		public String DisplayName;

		public GooglePlaceType(String id, String name) {
			TypeId = id;
			DisplayName = name;
		}
	}

	public class GooglePlaceTypeAdapter extends ArrayAdapter<GooglePlaceType> {

		private ArrayList<GooglePlaceType> items;
		private Context context;

		public GooglePlaceTypeAdapter(Context context, int textViewResourceId,
				ArrayList<GooglePlaceType> items) {
			super(context, textViewResourceId, items);
			this.context = context;
			this.items = items;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(android.R.layout.simple_list_item_1,
						null);
			}

			GooglePlaceType item = items.get(position);
			if (item != null) {
				// My layout has only one TextView
				TextView itemView = (TextView) view
						.findViewById(R.id.textView1);
				if (itemView != null) {
					// do whatever you want with your string and long
					itemView.setText(String.format(item.DisplayName));
				}
			}

			return view;
		}
	}

}
