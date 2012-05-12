package com.facebook.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONTokener;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.Toast;

public class GooglePlacesAPI {
	private Context context;
	private HttpClient mHttpClient; 

	private static final String GooglePlacesAPIKey = "AIzaSyD9T3UGo2qMo0Vena6fhLZg9QyX2ALSejw";
	private static final String API_URL = "https://maps.googleapis.com/maps/api/place";

	public static final int ALLOWED_RADIUS = 100;
	public static final int MAX_RADIUS = 50000;

	/*
	 * public GooglePlaceType placesTypes[] = { new GooglePlaceType("bar",
	 * "Bar"), new GooglePlaceType("cafe", "Cafe"), new GooglePlaceType("food",
	 * "Food"), new GooglePlaceType("night_club", "Night Club"), new
	 * GooglePlaceType("restaurant", "Restaurant"), new GooglePlaceType("store",
	 * "Store") };
	 */
	public GooglePlacesAPI(Context context) {
		this.context = context;
		mHttpClient = new HttpClient();
	}

	public ArrayList<GooglePlace> getNearby(Location location, int radius) throws Throwable {

		String ll = String.valueOf(location.getLatitude()) + ","
				+ String.valueOf(location.getLongitude());
		
		URL url = new URL(API_URL + "/search/json?key=" + GooglePlacesAPIKey
				+ "&location=" + ll + "&sensor=true" + "&radius=" + radius);

		return getPlaces(url, true, location);
	}

	public ArrayList<GooglePlace> searchPlaces(Location location,
			boolean hasLocation, String searchStr, int radius) throws Throwable {

		String ll = String.valueOf(location.getLatitude()) + ","
				+ String.valueOf(location.getLongitude());
		String url_str = API_URL + "/search/json?key=" + GooglePlacesAPIKey
				+ "&location=" + ll + "&sensor=" + String.valueOf(hasLocation)
				+ "&keyword=" + searchStr + "&radius=" + radius;

		url_str = url_str.replace(" ", "%20");
		URL url = new URL(url_str);

		Log.d("ERIC", url.toURI().toASCIIString());

		return getPlaces(url, hasLocation, location);
	}

	public ArrayList<GooglePlace> getPlaces(URL url, boolean by_location,
			Location my_loc) throws Throwable {

		ArrayList<GooglePlace> placesList = new ArrayList<GooglePlace>();

		try {

			Log.d("ERIC", "Opening URL " + url.toString());

			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();

			urlConnection.setRequestMethod("GET");
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);

			urlConnection.connect();

			String response = streamToString(urlConnection.getInputStream());
			JSONObject jsonObj = (JSONObject) new JSONTokener(response)
					.nextValue();

			Log.d("ERIC", jsonObj.toString());
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

					place.vicinity = item.getString("vicinity");
					if (by_location)
						place.distance = calculateDistance(my_loc, loc);
					else
						place.distance = -1.0;

					placesList.add(place);

				}
			}
		} catch (Throwable ex) {
			Toast.makeText(context, "catched " + ex.getMessage(),
					Toast.LENGTH_SHORT).show();

			Log.d("ERIC", "bad search: " + ex.getMessage());
		}

		Collections.sort(placesList, new distanceComparator());
		return placesList;
	}

	private String streamToString(InputStream is) throws IOException {
		String str = "";

		if (is != null) {
			StringBuilder sb = new StringBuilder();
			String line;

			try {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));

				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}

				reader.close();
			} finally {
				is.close();
			}

			str = sb.toString();
		}
		return str;
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

	/*
	 * class GooglePlaceType { public String TypeId; public String DisplayName;
	 * 
	 * public GooglePlaceType(String id, String name) { TypeId = id; DisplayName
	 * = name; } }
	 */

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

			URL url = new URL(API_URL + "/add/json?key=" + GooglePlacesAPIKey
					+ "&sensor=true");
			JSONObject jsonResponse =  mHttpClient.SendHttpPost(url.toString(), newPlaceObject);
			
			return jsonResponse;

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
