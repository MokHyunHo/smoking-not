package com.facebook.android;

import android.os.Bundle;

import com.google.android.maps.*;

public class AddPlace extends MapActivity {

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_place);

		MapView mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		double latitude = 18.9599990845, longitude = 72.819999694;
		GeoPoint p = new GeoPoint((int) (latitude * 1000000),
				(int) (longitude * 1000000));
		
		MapController mc = mapView.getController();
		mc.setCenter(p);
		mc.setZoom(14);
		
	}

}
