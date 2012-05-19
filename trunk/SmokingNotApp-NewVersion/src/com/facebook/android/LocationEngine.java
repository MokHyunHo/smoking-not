package com.facebook.android;

import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class LocationEngine implements LocationListener {

	private LocationManager lm;

	private boolean locationSent;

	String provider = LocationManager.NETWORK_PROVIDER;

	private Location mLocation;
	private boolean serviceEnabled = false;
	private boolean locationEnabled = false;

	private Context context;

	private WebRequest req;

	public LocationEngine(Context context) {
		this.context = context;
		lm = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

		if (!lm.isProviderEnabled(provider)) {

			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			context.startActivity(intent);
			return;
		} else
			serviceEnabled = true;

		req = new WebRequest();

		setLocationSent(false);

		lm.requestLocationUpdates(provider, 0, 0, this);

		locationEnabled = (mLocation != null);

	}

	@Override
	public void onLocationChanged(Location location) {
		Log.i("ERIC", "loc changed: " + location.toString());
		setLocationSent(false);
		mLocation = location;

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

	public boolean isLocationEnabled() {
		return locationEnabled;
	}

	public boolean isServiceEnabled() {
		return serviceEnabled;
	}

	public Location getCurrentLocation() {
		setLocationSent(true);
		return mLocation;
	}



	public boolean isLocationSent() {
		return locationSent;
	}

	public void setLocationSent(boolean locationSent) {
		this.locationSent = locationSent;
	}

	void setDebugLocation() {
		mLocation = new Location(LocationManager.PASSIVE_PROVIDER);
		mLocation.setLatitude(32.06);
		mLocation.setLongitude(34.77);
		locationEnabled = true;

		Toast.makeText(
				context,
				"Location is unavailable. Placing you somewhere is Tel Aviv... (debug)",
				Toast.LENGTH_LONG);
	}

}
