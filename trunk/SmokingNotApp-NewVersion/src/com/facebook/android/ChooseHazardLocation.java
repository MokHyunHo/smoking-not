package com.facebook.android;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.*;

public class ChooseHazardLocation extends MapActivity {

	private MapView mapView;
	private MapOverlay mapOverlay;
	private List<Overlay> listOfOverlays;
	private MarkerOverlay myLocationPoint, tappedPoint;

	private Button btnChoose;
	private ImageButton mRefreshButton;
	private TextView tvAddress;
	private Location mLocation;
	private Location chosenLocation;
	private GooglePlacesAPI mGooglePlacesAPI;
	private LocationEngine mLocEng;
	private Context context;

	private ProgressDialog mProgress;

	private final int M_LS_DOWN = 0;
	private final int M_UPD_LOC_ERR = 1;
	@SuppressWarnings("unused")
	private final int M_ADDRESS_NA = 2;
	private final int M_LOC_OK = 3;
	@SuppressWarnings("unused")
	private final int M_ADDRESS_OK = 4;
	private final int M_LOC_NA = 9;
	private final int CYCLES_TO_WAIT = 3;
	
	private CountDownLatch latch = new CountDownLatch(1);
	
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
		public boolean onTap(GeoPoint point, MapView mapView) {

			if (chosenLocation == null)
				chosenLocation = new Location(
						LocationManager.PASSIVE_PROVIDER);

			chosenLocation.setLatitude(point.getLatitudeE6() / 1E6);
			chosenLocation.setLongitude(point.getLongitudeE6() / 1E6);

			listOfOverlays.remove(tappedPoint);

			tappedPoint = new MarkerOverlay(point, R.drawable.marker);
			listOfOverlays.add(tappedPoint);
			mapView.invalidate();

			return true;
		}

	}

	class MarkerOverlay extends Overlay {
		private GeoPoint p;
		private int resid;

		public MarkerOverlay(GeoPoint p, int bmpResId) {
			this.p = p;
			this.resid = bmpResId;
		}

		@Override
		public boolean draw(Canvas canvas, MapView mapView, boolean shadow,
				long when) {
			super.draw(canvas, mapView, shadow);

			// ---translate the GeoPoint to screen pixels---
			Point screenPts = new Point();
			mapView.getProjection().toPixels(p, screenPts);

			// ---add the marker---
			Bitmap bmp = BitmapFactory.decodeResource(getResources(),
					resid);
			canvas.drawBitmap(bmp, screenPts.x, screenPts.y, null);
			return true;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.choose_hazard_location);

		mProgress = new ProgressDialog(this);

		mGooglePlacesAPI = new GooglePlacesAPI(this);
		
		tvAddress = (TextView) findViewById(R.id.tvAddress);
		mRefreshButton = (ImageButton) findViewById(R.id.ib_Refresh);
		btnChoose = (Button) findViewById(R.id.btn_choose);
		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		mRefreshButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					mLocEng = null;
					mLocEng = new LocationEngine(context);
					updateLocation();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		btnChoose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				
				if (tappedPoint == null) {
					Toast.makeText(context, "Please tap location on map",
							Toast.LENGTH_SHORT).show();
				}
				try {
					if (chosenLocation == null)
						return;
					
					String addr = mGooglePlacesAPI.mGeoEng.getAddressFromLocation(chosenLocation);
					
					Intent data = new Intent();

					
					data.putExtra("address", addr);
					data.putExtra("location", chosenLocation);
					setResult(RESULT_OK, data);
					finish();

				} catch (Throwable e) {
					e.printStackTrace();
				}

			}
		});
		
		updateLocation();


	}
	
	public void updateLocation() {
		mProgress.setMessage("Retrieving location...");
		mProgress.show();
		new Thread() {
			@Override
			public void run() {
				int what = M_LOC_OK;
				Looper.prepare();
				Log.i("ERIC", "upc loc thread");
				try {
					mLocEng = new LocationEngine(context);
					mLocation = null;
					int counter = 0;
					if (!mLocEng.isServiceEnabled()) {
						what = M_LS_DOWN;
					} else {
						while ((mLocation == null)
								&& (counter < CYCLES_TO_WAIT)) {
							mLocation = mLocEng.getCurrentLocation();
							if (mLocation != null)
								break;
							sleep(1000);
							counter++;
							Log.i("ERIC", "counter=" + counter);
						}
						if (mLocation == null) {
							mLocation = mLocEng.getLastKnownLocation();
						}
						if (mLocation == null) {
							mHandler.sendMessage(mHandler.obtainMessage(M_LOC_NA));
							mLocEng.setDebugLocation();
							mLocation = mLocEng.getCurrentLocation();
						}
						counter = 0;
						while ((!mGooglePlacesAPI.mGeoEng.addressEnabled)
								&& (counter < CYCLES_TO_WAIT)) {
							mGooglePlacesAPI.mGeoEng
									.getAddressFromLocation(mLocation);
							if (mGooglePlacesAPI.mGeoEng.addressEnabled)
								break;
							sleep(1000);
							counter++;
							Log.i("ERIC", "counter=" + counter);
						}
					}
				} catch (Exception e) {
					what = M_UPD_LOC_ERR;
					e.printStackTrace();
				}
				latch.countDown();
				mHandler.sendMessage(mHandler.obtainMessage(what));
			}
		}.start();
		Log.i("ERIC", "upd loc thread started");
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.i("ERIC", "what: " + msg.what);
			switch (msg.what) {
			case M_LS_DOWN:
				Toast.makeText(ChooseHazardLocation.this, "Location Service is unavailable",
						Toast.LENGTH_LONG).show();

				break;
			case M_UPD_LOC_ERR:
				Toast.makeText(ChooseHazardLocation.this, "Error while getting location",
						Toast.LENGTH_LONG).show();
				break;
			case M_LOC_NA:
				Toast.makeText(
						context,
						"Location couldn't be determined. Putting you somewhere in Tel Aviv...",
						Toast.LENGTH_SHORT).show();
				break;
			case M_LOC_OK:
				if (mLocation != null) {
					tvAddress.setText(mGooglePlacesAPI.mGeoEng
							.getAddressFromLocation(mLocation));
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
					
					listOfOverlays.remove(myLocationPoint);

					myLocationPoint = new MarkerOverlay(p, R.drawable.blue_marker);
					listOfOverlays.add(myLocationPoint);
					mapView.invalidate();

					Log.i("ERIC", "location: " + mLocation.toString());
					btnChoose.setEnabled(true);
					
				} else
					Toast.makeText(ChooseHazardLocation.this, "Unable to get location",
							Toast.LENGTH_LONG).show();
				break;

			}
			mProgress.dismiss();
			latch.countDown();
			Log.i("ERIC", "unlatched");
		}
	};	
}
