package com.facebook.android;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.maps.*;

public class AddPlace extends MapActivity {

	MapView mapView;
	MapOverlay mapOverlay;
	List<Overlay> listOfOverlays;
	MarkerOverlay markerOverlay;
	
	EditText etName;
	Button btnSubmit;
	
	Location newPlaceLocation;
	
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	class MapOverlay extends Overlay {

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			;
		}

		@Override
		 public boolean onTap(GeoPoint point, MapView mapView) 
		 {
		     
		   if (newPlaceLocation ==null)
			   newPlaceLocation  = new Location(LocationManager.PASSIVE_PROVIDER);
		   
		   newPlaceLocation.setLatitude(point.getLatitudeE6()/1E6);
		   newPlaceLocation.setLongitude(point.getLongitudeE6()/1E6);
		   
		   listOfOverlays.remove(markerOverlay);
		   
		   markerOverlay =  new MarkerOverlay(point);
		   listOfOverlays.add(markerOverlay);
           mapView.invalidate();
				  
		   
		  return true;
		 }

	}

	class MarkerOverlay extends Overlay{
	     private GeoPoint p; 
	     public MarkerOverlay(GeoPoint p){
	         this.p = p;
	     }

	     @Override
	     public boolean draw(Canvas canvas, MapView mapView, 
	            boolean shadow, long when){
	        super.draw(canvas, mapView, shadow);                   

	        //---translate the GeoPoint to screen pixels---
	        Point screenPts = new Point();
	        mapView.getProjection().toPixels(p, screenPts);

	        //---add the marker---
	        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.marker);            
	        canvas.drawBitmap(bmp, screenPts.x, screenPts.y, null);         
	        return true;
	     }
	 }
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.add_place);
		
		etName = (EditText) findViewById(R.id.et_Name);
		btnSubmit = (Button) findViewById(R.id.btn_submit);
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);
		
		Intent intent = getIntent();

		Location mLocation = (Location) intent.getExtras().get("location");

		GeoPoint p = new GeoPoint((int) (mLocation.getLatitude() * 1000000),
				(int) (mLocation.getLongitude() * 1000000));

		MapController mc = mapView.getController();
		mc.setCenter(p);
		mc.setZoom(18);
		// mc.zoomIn();

		mapOverlay = new MapOverlay();
		listOfOverlays = mapView.getOverlays();
		listOfOverlays.clear();
		listOfOverlays.add(mapOverlay);
		
		btnSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				GooglePlace newPlace = new GooglePlace();
				
				Intent data = new Intent();
				
				data.putExtra("new_place_location", newPlace);
				
				setResult(RESULT_OK, data);
				
			}
		});

	}

}
