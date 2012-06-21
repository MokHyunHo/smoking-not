package il.reporter.gws;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.R;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

public class GeocoderEngine {
	boolean addressEnabled = false;
	private Context context;
	private WebRequest req;

	public GeocoderEngine(Context context) {
		this.context = context;
		req = new WebRequest();
	}

	public ArrayList<String> getAddressSuggestions(String request, Location loc) {
		ArrayList<String> lst = new ArrayList<String>();
		try {

			String loc_str;
			if (loc != null)
				loc_str = "&location=" + loc.getLatitude() + ","
						+ loc.getLongitude();
			else
				loc_str = "";

			String requestUrl = context.getString(R.string.AutocompleteApiUrl)
					+ "json?sensor=false&key="
					+ context.getString(R.string.GooglePlacesAPIKey)
					+ "&input=" +  URLEncoder.encode(request, "UTF-8") + "&types=geocode" + loc_str;

			Log.i("ERIC", requestUrl);
			JSONObject jsonResponse = getGeocoderResponse(requestUrl);
			JSONArray predictions = jsonResponse.getJSONArray("predictions");
			for (int i = 0; i < predictions.length(); i++) {
				lst.add(predictions.getJSONObject(i).getString("description"));
			}
			Log.i("ERIC", jsonResponse.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return lst;
	}

	public String getAddressFromLocation(Location location) {
		String addressString = "(address unavailable)";

		addressEnabled = false;

		OBTAIN: try {

			if (location == null)
				break OBTAIN;
			String requestUrl = context.getString(R.string.GeocoderApiUrl)
					+ "/json?sensor=true&latlng=" + location.getLatitude()
					+ "," + location.getLongitude();

			JSONObject geocoderResponse = getGeocoderResponse(requestUrl);

			addressString = geocoderResponse.getJSONArray("results")
					.getJSONObject(0).getString("formatted_address");

			addressEnabled = true;

		} catch (Exception e) {
			e.printStackTrace();
			break OBTAIN;
		}

		return addressString;
	}

	public Location getLocationFromAddress(String str) {
		Location loc = null;

		try {

			String requestUrl = context.getString(R.string.GeocoderApiUrl)
					+ "/json?sensor=true&address=" + URLEncoder.encode(str, "UTF-8");

			Log.i("ERIC", requestUrl);
			JSONObject geocoderResponse = getGeocoderResponse(requestUrl);
			JSONObject json_loc = geocoderResponse.getJSONArray("results").getJSONObject(0)
					.getJSONObject("geometry").getJSONObject("location");
			
			if (json_loc != null)
			{
				loc = new Location(LocationManager.PASSIVE_PROVIDER);
				loc.setLatitude(Double.valueOf(json_loc.getString("lat")));
				loc.setLongitude(Double.valueOf(json_loc.getString("lng")));

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return loc;
	}

	public String getAddressFromAddress(String str) {
		try {

			String requestUrl = context.getString(R.string.GeocoderApiUrl)
					+ "/json?sensor=true&address=" + URLEncoder.encode(str, "UTF-8");

			requestUrl = requestUrl.replace(" ", "+");
			Log.i("ERIC", requestUrl);
			JSONObject geocoderResponse = getGeocoderResponse(requestUrl);
			return geocoderResponse.getJSONArray("results").getJSONObject(0)
					.getString("formatted_address");
			


		} catch (Exception e) {
			e.printStackTrace();
			return str;
		}

	}
	JSONObject getGeocoderResponse(String request) throws IOException,
			JSONException {
		return req.readJsonFromUrl(request);
	}
}
