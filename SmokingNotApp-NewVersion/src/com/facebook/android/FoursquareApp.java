package com.facebook.android;

import com.facebook.android.FoursquareDialog.FsqDialogListener;

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
import org.json.JSONTokener;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Dialog;
import android.app.ProgressDialog;

import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;

public class FoursquareApp {

	private Context context;
	private FoursquareSession mSession;
	private FoursquareDialog mDialog;
	private FsqAuthListener mListener;
	private ProgressDialog mProgress;
	private String mTokenUrl;
	private String mAccessToken;

	public static final String CALLBACK_URL = "http://code.google.com/p/smoking-not/";
	private static final String AUTH_URL = "https://foursquare.com/oauth2/authenticate?response_type=code";
	private static final String TOKEN_URL = "https://foursquare.com/oauth2/access_token?grant_type=authorization_code";

	private static final String API_URL = "https://api.foursquare.com/v2";

	private static final String TAG = "FoursquareApi";

	public FoursquareApp(Context context, String clientId, String clientSecret) {
		this.context = context;

		mSession = new FoursquareSession(context);

		mAccessToken = mSession.getAccessToken();

		mTokenUrl = TOKEN_URL + "&client_id=" + clientId + "&client_secret="
				+ clientSecret + "&redirect_uri=" + CALLBACK_URL;

		String url = AUTH_URL + "&client_id=" + clientId + "&redirect_uri="
				+ CALLBACK_URL;

		FsqDialogListener listener = new FsqDialogListener() {
			@Override
			public void onComplete(String code) {
				getAccessToken(code);
			}

			@Override
			public void onError(String error) {
				mListener.onFail("Authorization failed");
			}
		};

		mDialog = new FoursquareDialog(context, url, listener);
		mProgress = new ProgressDialog(context);

		mProgress.setCancelable(false);

	}

	private void getAccessToken(final String code) {
		mProgress.setMessage("Getting access token ...");
		mProgress.show();

		new Thread() {
			@Override
			public void run() {
				Log.i(TAG, "Getting access token");

				int what = 0;

				try {
					URL url = new URL(mTokenUrl + "&code=" + code);

					Log.i(TAG, "Opening URL " + url.toString());

					HttpURLConnection urlConnection = (HttpURLConnection) url
							.openConnection();

					urlConnection.setRequestMethod("GET");
					urlConnection.setDoInput(true);
					urlConnection.setDoOutput(true);

					urlConnection.connect();

					JSONObject jsonObj = (JSONObject) new JSONTokener(
							streamToString(urlConnection.getInputStream()))
							.nextValue();
					mAccessToken = jsonObj.getString("access_token");

					Log.i(TAG, "Got access token: " + mAccessToken);
				} catch (Exception ex) {
					what = 1;

					ex.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(what, 1, 0));
			}
		}.start();
	}

	private void fetchUserName() {
		mProgress.setMessage("Finalizing ...");

		new Thread() {
			@Override
			public void run() {
				Log.i(TAG, "Fetching user name");
				int what = 0;

				try {
					URL url = new URL(API_URL + "/users/self?oauth_token="
							+ mAccessToken);

					Log.d(TAG, "Opening URL " + url.toString());

					HttpURLConnection urlConnection = (HttpURLConnection) url
							.openConnection();

					urlConnection.setRequestMethod("GET");
					urlConnection.setDoInput(true);
					urlConnection.setDoOutput(true);

					urlConnection.connect();

					String response = streamToString(urlConnection
							.getInputStream());
					JSONObject jsonObj = (JSONObject) new JSONTokener(response)
							.nextValue();

					JSONObject resp = (JSONObject) jsonObj.get("response");
					JSONObject user = (JSONObject) resp.get("user");

					String firstName = user.getString("firstName");
					String lastName = user.getString("lastName");

					Log.i(TAG, "Got user name: " + firstName + " " + lastName);

					mSession.storeAccessToken(mAccessToken, firstName + " "
							+ lastName);
				} catch (Exception ex) {
					what = 1;

					ex.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(what, 2, 0));
			}
		}.start();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (msg.arg1 == 1) {
				if (msg.what == 0) {
					fetchUserName();
				} else {
					mProgress.dismiss();

					mListener.onFail("Failed to get access token");
				}
			} else {
				mProgress.dismiss();

				mListener.onSuccess();
			}
		}
	};

	public boolean hasAccessToken() {
		Log.i(TAG, "Checking access token...");
		return (mAccessToken == null) ? false : true;
	}

	public void setListener(FsqAuthListener listener) {
		mListener = listener;
	}

	public String getUserName() {
		return mSession.getUsername();
	}

	public void authorize() {
		mDialog.show();
	}

	public class distanceComparator implements Comparator<FsqVenue> {

		public int compare(FsqVenue venue1, FsqVenue venue2) {
			return venue1.distance - venue2.distance;
		}
	}

	public ArrayList<FsqVenue> getNearby(double latitude, double longitude,
			int radius) throws Throwable {

		String ll = String.valueOf(latitude) + "," + String.valueOf(longitude);
		URL url = new URL(
				API_URL
						+ "/venues/search?ll="
						+ ll
						+ "&intent=browse&radius="
						+ radius
						+ "&limit="
						+ 50
						+ "&oauth_token=WZ3B1CIMNVEPEEOJ1RNMF32515ETOCCEMRAQPMGFBK0QX4BI&v=20120331");

		return getVenues(url);
	}

	public ArrayList<FsqVenue> searchVenues(double latitude, double longitude,
			int radius, String searchStr) throws Throwable {

		String ll = String.valueOf(latitude) + "," + String.valueOf(longitude);
		URL url = new URL(
				API_URL
						+ "/venues/search?ll="
						+ ll
						+ "&query="
						+ searchStr
						+ "&intent=browse"
						+ "&radius=100000"
						+ "&limit="
						+ 50
						+ "&oauth_token=WZ3B1CIMNVEPEEOJ1RNMF32515ETOCCEMRAQPMGFBK0QX4BI&v=20120331");

		return getVenues(url);
	}

	public ArrayList<FsqVenue> getVenues(URL url) throws Throwable {

		ArrayList<FsqVenue> venueList = new ArrayList<FsqVenue>();

		try {

			Log.d(TAG, "Opening URL " + url.toString());

			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();

			urlConnection.setRequestMethod("GET");
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);

			urlConnection.connect();

			String response = streamToString(urlConnection.getInputStream());
			JSONObject jsonObj = (JSONObject) new JSONTokener(response)
					.nextValue();

			JSONArray venues = jsonObj.getJSONObject("response").getJSONArray(
					"venues");

			int length = venues.length();

			if (length > 0) {
				for (int i = 0; i < length; i++) {
					JSONObject item = (JSONObject) venues.get(i);

					FsqVenue venue = new FsqVenue();

					venue.id = item.getString("id");
					venue.name = item.getString("name");

					JSONObject location = item.getJSONObject("location");

					Location loc = new Location(
							LocationManager.PASSIVE_PROVIDER);

					loc.setLatitude(Double.valueOf(location.getString("lat")));
					loc.setLongitude(Double.valueOf(location.getString("lng")));

					venue.location = loc;
					StringBuilder sb = new StringBuilder();
					if (location.has("address"))
						sb.append(location.getString("address"));
					if (location.has("city"))
					{
						if (!sb.toString().isEmpty())
							sb.append(", ");
						sb.append(location.getString("city"));
					}
					if (sb.toString().isEmpty())
						venue.address = "(No address)";
					else
						venue.address = sb.toString();

					venue.distance = Integer.valueOf(location
							.getString("distance"));
					venueList.add(venue);

				}
			}
		} catch (Throwable ex) {
			Toast.makeText(context, "catched", Toast.LENGTH_SHORT).show();
		}

		Collections.sort(venueList, new distanceComparator());
		return venueList;
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

	public interface FsqAuthListener {
		public abstract void onSuccess();

		public abstract void onFail(String error);
	}

	/*
	 * public Location getLocation() { return mLocation;
	 * 
	 * }
	 * 
	 * public void obtainAddress() { List<Address> addresses; try {
	 * 
	 * if (mLocation == null) addresses = gc.getFromLocation(32.06, 34.77, 1);
	 * else addresses = gc.getFromLocation(mLocation.getLatitude(),
	 * mLocation.getLongitude(), 1); if (addresses == null) return;
	 * 
	 * StringBuilder sb = new StringBuilder(); Log.i("ERRIC", "addresses size: "
	 * + String.valueOf(addresses.size())); if (addresses.size() > 0) { Address
	 * address = addresses.get(0);
	 * 
	 * for (int i = 0; i < address.getMaxAddressLineIndex(); i++)
	 * sb.append(address.getAddressLine(i)).append("\n");
	 * 
	 * sb.append(address.getCountryName()); } addressString = sb.toString(); }
	 * catch (IOException e) { Log.i("ERIC", "BAD! address: " + e.getMessage());
	 * }
	 * 
	 * 
	 * }
	 * 
	 * public String getAddress() { Log.i("ERIC", "getAddress: " +
	 * addressString); return addressString; }
	 * 
	 * public void setLocation(double lat, double lon) {
	 * mLocation.setProvider(LocationManager.PASSIVE_PROVIDER);
	 * mLocation.setLatitude(lat); mLocation.setLongitude(lon);
	 * 
	 * }
	 * 
	 * public boolean isLocationEnabled() { return (mLocation != null); }
	 */
	private void showDialog(String title, String message) {
		Dialog d = new Dialog(this.context);
		d.setCanceledOnTouchOutside(true);
		d.setTitle(title);
		TextView sTV = new TextView(context);
		sTV.setText(message);
		d.setContentView(sTV);
		d.show();
	}
}