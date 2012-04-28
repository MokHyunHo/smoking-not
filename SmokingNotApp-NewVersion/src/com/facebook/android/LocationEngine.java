package com.facebook.android;

import java.io.IOException;
import java.text.BreakIterator;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocationEngine implements LocationListener {
	
	private LocationManager lm;
	private Geocoder gc;
	
	private boolean locationSent;
	
	String provider = LocationManager.NETWORK_PROVIDER;
	
	private Location mLocation;
	private boolean locationEnabled = false;
	private boolean geocoderEnabled = false;
	public LocationEngine(Context context)
	{
		lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		
		if (!lm.isProviderEnabled(provider))
			return;
		
		gc = new Geocoder(context, Locale.getDefault());
		
		if (Geocoder.isPresent())
			geocoderEnabled = true;
		
		Log.i("ERIC", "geocoder enabled = " + geocoderEnabled);
		
		setLocationSent(false);
		mLocation = lm.getLastKnownLocation(provider);
		locationEnabled = (lm != null);
		
		lm.requestLocationUpdates(provider, 1000L, 20F, this);
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
	
	public Location getCurrentLocation() {
		setLocationSent(true);
		return mLocation;
	}
	
	public String getAddressFromLocation(Location location)
	{
		String addressString = "(address unavailable)";
		List<Address> addresses;
	
		OBTAIN:
		try {
			
			if (!geocoderEnabled)
				break OBTAIN;
			
			if (mLocation == null)
				break OBTAIN;
			
			addresses = gc.getFromLocation(location.getLatitude(),
						location.getLongitude(), 1);
			
			if (addresses == null)
				break OBTAIN;
		
			StringBuilder sb = new StringBuilder();
			
			if (addresses.size() < 1) 
				break OBTAIN;
			
			Address address = addresses.get(0);
			int i;
			for (i = 0; i < address.getMaxAddressLineIndex() - 1; i++)
				sb.append(address.getAddressLine(i)).append(",");
			sb.append(address.getAddressLine(i));
			
			addressString = sb.toString();   
		} catch (IOException e) {
			Log.i("ERIC", "BAD! address: " + e.getMessage());
			break OBTAIN;
		}

		return addressString;
	}

	public boolean isLocationSent() {
		return locationSent;
	}

	public void setLocationSent(boolean locationSent) {
		this.locationSent = locationSent;
	}

}
