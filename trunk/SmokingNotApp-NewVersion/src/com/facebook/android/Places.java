package com.facebook.android;

//import java.io.IOException;

import java.util.ArrayList;

import com.facebook.android.R;
import com.facebook.android.FacebookMain;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
//import android.widget.ImageView;
//import android.widget.TextView;
import android.widget.TextView;

public class Places extends Activity implements View.OnClickListener {
	/** Called when the activity is first created. */

	private TextView tvReport, tvPlaces, tvProfile, tvAddress;
	private EditText latitudeEt, longitudeEt, radiusEt;
	private Button exitButton, goBtn, updLocBtn;

	private FoursquareApp mFsqApp;
	private ListView mListView;
	private NearbyAdapter mAdapter;
	private ArrayList<FsqVenue> mNearbyList;
	private ProgressDialog mProgress;

	private Location mLocation;

	public static final String CLIENT_ID = "YP3ZQVYTZWNQVEWUNZV2LNIP0EKOLPSG40IVT4BT2TVKS5TP";
	public static final String CLIENT_SECRET = "SJMMUOXSX0FOUF5UHJYWBCUN3VQOPAO2CCCBUA4FPBCBEGDA";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// initialization of all the objects
		setContentView(R.layout.places);
		Init();

		updateLocationFields();
		// connection between XML & JAVA

		// first-up menu
		tvReport.setOnClickListener(this);
		tvPlaces.setOnClickListener(this);
		tvProfile.setOnClickListener(this);

		// START MENU BUTTON
		exitButton = (Button) findViewById(R.id.exitButton);
		exitButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent myIntent = new Intent(getApplicationContext(),
						FacebookMain.class);
				if (Utility.mFacebook.isSessionValid()) {
					Utility.objectID = "me";
				}
				startActivity(myIntent);

			}
		});

		
		goBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				double lat, lon;
				/*String radius = radiusEt.getText().toString();
				int rad = Integer.valueOf(radius);*/
				int rad = 100;
				try {
				if (mFsqApp.isLocationEnabled())
					updateLocationFields();
				// updLocBtn.
				/*
				 * lat = mLocation.getLatitude(); lon = mLocation.getLatitude();
				 * Toast.makeText(Places.this, "geting by location" + lat + " "
				 * + lon, Toast.LENGTH_SHORT).show(); loadNearbyPlaces(lat, lon,
				 * rad);
				 */

				String latitude = latitudeEt.getText().toString();
				String longitude = longitudeEt.getText().toString();
				Log.i("ERIC", "vals: " + latitude + " " + longitude);
				if (latitude.isEmpty() || longitude.isEmpty()) {
					Toast.makeText(Places.this,
							"Latitude or longitude is empty",
							Toast.LENGTH_SHORT).show();
				} else {
					lat = Double.valueOf(latitude);
					lon = Double.valueOf(longitude);
					loadNearbyPlaces(lat, lon, rad);
					//Toast.makeText(Places.this, "geting by manual location",
						//	Toast.LENGTH_SHORT).show();
				}
				} catch (Exception ex) {
					Toast.makeText(Places.this, "Something bad happened: " + ex.getMessage(),
						Toast.LENGTH_SHORT).show();
				}
			}

		});

		updLocBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mFsqApp.isLocationEnabled()) {

					updateLocationFields();
				}
			}
		});

	}

	private void updateLocationFields() {
		try {
		mLocation = mFsqApp.getLocation();
		if (mLocation != null) {
			latitudeEt.setText(String.valueOf(mLocation.getLatitude()));
			longitudeEt.setText(String.valueOf(mLocation.getLongitude()));
		}
		tvAddress.setText(mFsqApp.getAddress());
		} catch (Exception Ex)
		{
			Log.i("ERIC", "BAD! " + Ex.getMessage());
		}
	}

	private void Init() {
		tvReport = (TextView) findViewById(R.id.tvPlaReport);
		tvPlaces = (TextView) findViewById(R.id.tvPlaPlaces);
		tvProfile = (TextView) findViewById(R.id.tvPlaProfile);
		
		tvAddress = (TextView) findViewById(R.id.textView2);
		latitudeEt = (EditText) findViewById(R.id.et_latitude);
		longitudeEt = (EditText) findViewById(R.id.et_longitude);
		radiusEt = (EditText) findViewById(R.id.et_radius);
		goBtn = (Button) findViewById(R.id.b_go);
		updLocBtn = (Button) findViewById(R.id.b_updateLocation);
		mListView = (ListView) findViewById(R.id.lv_places);

		mFsqApp = new FoursquareApp(this, CLIENT_ID, CLIENT_SECRET);

		mAdapter = new NearbyAdapter(this);
		mNearbyList = new ArrayList<FsqVenue>();
		mProgress = new ProgressDialog(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent myIntent;
		switch (v.getId()) {
		case R.id.tvPlaReport:
			/*
			 * tvReport.setBackgroundResource(R.drawable.orange);
			 * tvReport.setBackgroundColor(android.R.color.black);
			 */
			myIntent = new Intent(getApplicationContext(), Report.class);
			if (Utility.mFacebook.isSessionValid()) {
				Utility.objectID = "me";
				startActivity(myIntent);
			}
			break;
		case R.id.tvPlaPlaces:
			/*
			 * tvPlaces.setBackgroundResource(R.drawable.orange);
			 * tvPlaces.setBackgroundColor(android.R.color.black);
			 */
			break;
		case R.id.tvPlaProfile:
			/*
			 * tvProfile.setBackgroundResource(R.drawable.orange);
			 * tvProfile.setBackgroundColor(android.R.color.black);
			 */
			myIntent = new Intent(getApplicationContext(), Profile.class);
			if (Utility.mFacebook.isSessionValid()) {
				Utility.objectID = "me";
				startActivity(myIntent);
			}
			break;
		}
	}

	private void loadNearbyPlaces(final double latitude,
			final double longitude, final int radius) {
		mProgress.show();

		new Thread() {
			@Override
			public void run() {
				int what = 0;

				try {
					mNearbyList = mFsqApp
							.getNearby(latitude, longitude, radius);
				} catch (Throwable e) {
					what = 1;
					e.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(what));
			}
		}.start();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mProgress.dismiss();

			if (msg.what == 0) {
				if (mNearbyList.size() == 0) {
					Toast.makeText(Places.this, "No nearby places available",
							Toast.LENGTH_SHORT).show();
					return;
				}

				mAdapter.setData(mNearbyList);
				mListView.setAdapter(mAdapter);
			} else {
				Toast.makeText(Places.this,
						"Failed to load nearby places: " + msg.what,
						Toast.LENGTH_SHORT).show();
			}
		}
	};
}
