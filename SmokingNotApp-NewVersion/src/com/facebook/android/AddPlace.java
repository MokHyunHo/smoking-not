package com.facebook.android;

import java.util.ArrayList;
import java.util.List;

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
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.maps.*;

public class AddPlace extends MapActivity {

	private MapView mapView;
	private MapOverlay mapOverlay;
	private List<Overlay> listOfOverlays;
	private MarkerOverlay markerOverlay;

	private EditText etName;
	private Button btnSubmit;
	private Spinner lstTypes;

	private Location newPlaceLocation;
	private GooglePlacesAPI mGooglePlacesAPI;

	private Context context;

	private NearbyAdapter mAdapter;
	private ProgressDialog mProgress;
	String name;
	String type;

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

			if (newPlaceLocation == null)
				newPlaceLocation = new Location(
						LocationManager.PASSIVE_PROVIDER);

			newPlaceLocation.setLatitude(point.getLatitudeE6() / 1E6);
			newPlaceLocation.setLongitude(point.getLongitudeE6() / 1E6);

			listOfOverlays.remove(markerOverlay);

			markerOverlay = new MarkerOverlay(point);
			listOfOverlays.add(markerOverlay);
			mapView.invalidate();

			return true;
		}

	}

	class MarkerOverlay extends Overlay {
		private GeoPoint p;

		public MarkerOverlay(GeoPoint p) {
			this.p = p;
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
					R.drawable.marker);
			canvas.drawBitmap(bmp, screenPts.x, screenPts.y, null);
			return true;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.add_place);

		mProgress = new ProgressDialog(this);

		mGooglePlacesAPI = new GooglePlacesAPI(this);
		etName = (EditText) findViewById(R.id.et_Name);
		btnSubmit = (Button) findViewById(R.id.btn_submit);
		lstTypes = (Spinner) findViewById(R.id.lst_types);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter
				.createFromResource(this, R.array.types_array,
						android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		lstTypes.setAdapter(adapter);

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

		context = this;

		btnSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// GooglePlace newPlace = new GooglePlace();

				name = etName.getText().toString();
				type = (String) lstTypes.getSelectedItem();
				/*
				 * if (name.isEmpty()) { Toast.makeText(context,
				 * "Please enter a name", Toast.LENGTH_SHORT); return; }
				 */
				if (markerOverlay == null) {
					Toast.makeText(context, "Please tap location on map",
							Toast.LENGTH_SHORT).show();
				}
				assert (newPlaceLocation != null);
				try {
					ArrayList<GooglePlace> suspected = new ArrayList<GooglePlace>();
					suspected = mGooglePlacesAPI.searchPlaces(newPlaceLocation,
							true, name, GooglePlacesAPI.ALLOWED_RADIUS);

					if (suspected.size() > 0) {
						mAdapter = new NearbyAdapter(context, true);
						mAdapter.Recolor(Color.BLACK);
						mAdapter.setData(suspected);

						AlertDialog.Builder builder = new AlertDialog.Builder(
								context);
						builder.setTitle("Maybe already exist?");
						builder.setNegativeButton("Add new anyway",
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface dialogInterface,
											int item) {
										addNew();
									}
								});
						builder.setAdapter(mAdapter,
								new DialogInterface.OnClickListener() {
									public void onClick(
											DialogInterface dialogInterface,
											int item) {
										GooglePlace chosen_item = (GooglePlace) mAdapter
												.getItem(item);

										TransferPlaceAndFinish(
												chosen_item.name,
												chosen_item.id,
												chosen_item.refrence,
												chosen_item.vicinity,
												chosen_item.location);

										return;
									}
								});
						builder.create().show();
					} else {
						addNew();
					}

				} catch (Throwable e) {
					e.printStackTrace();
				}

			}
		});

	}

	private void addNew() {

		mProgress.setMessage("Adding place...");
		mProgress.show();
		new Thread() {
			@Override
			public void run() {
				try {
					JSONObject jsonResponse = mGooglePlacesAPI.AddPlace(name,
							type, newPlaceLocation);
					if (jsonResponse != null) {
						String status = jsonResponse.getString("status");
						if (status.compareTo("OK") == 0) {
							// String reference =
							// jsonResponse.getString("reference");
							// JSONObject details =
							// mGooglePlacesAPI.getPlaceDetails(reference);
							String address = mGooglePlacesAPI.mGeoEng
									.getAddressFromLocation(newPlaceLocation);
							TransferPlaceAndFinish(name,
									jsonResponse.getString("id"),
									jsonResponse.getString("reference"),
									address,
									newPlaceLocation);

						} else {
							Toast.makeText(context, "Failed to add place :(",
									Toast.LENGTH_SHORT).show();
							return;
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				mHandler.sendMessage(mHandler.obtainMessage(0));
			}
		}.start();

	}

	private void TransferPlaceAndFinish(String placeName, String placeId,
			String placeReference, String vicinity, Location placeLocation) {

		Intent data = new Intent();

		data.putExtra("placeName", placeName);
		data.putExtra("placeID", placeId);
		data.putExtra("placeVicinity", vicinity);
		data.putExtra("placeLocation", placeLocation);
		setResult(RESULT_OK, data);
		finish();

	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.i("ERIC", "what: " + msg.what);

			mProgress.dismiss();

		}
	};
}
