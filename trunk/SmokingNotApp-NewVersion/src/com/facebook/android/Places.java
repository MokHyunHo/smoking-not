package com.facebook.android;


//import java.io.IOException;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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

public class Places extends Activity {
	/** Called when the activity is first created. */

	private Button mProfileButton;
	private Button mReportButton;
	private Button mPlacesButton;

	private Button exitButton;

	private FoursquareApp mFsqApp;
	private ListView mListView;
	private NearbyAdapter mAdapter;
	private ArrayList<FsqVenue> mNearbyList;
	private ProgressDialog mProgress;

	public static final String CLIENT_ID = "YP3ZQVYTZWNQVEWUNZV2LNIP0EKOLPSG40IVT4BT2TVKS5TP";
	public static final String CLIENT_SECRET = "SJMMUOXSX0FOUF5UHJYWBCUN3VQOPAO2CCCBUA4FPBCBEGDA";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.places);

		// connection between XML & JAVA

		// first-up menu
		mProfileButton = (Button) findViewById(R.id.profileButton);
		mReportButton = (Button) findViewById(R.id.reportButton);
		mPlacesButton = (Button) findViewById(R.id.placesButton);

		final EditText latitudeEt = (EditText) findViewById(R.id.et_latitude);
		final EditText longitudeEt = (EditText) findViewById(R.id.et_longitude);
		final EditText radiusEt		= (EditText) findViewById(R.id.et_radius);
		Button goBtn = (Button) findViewById(R.id.b_go);
		mListView = (ListView) findViewById(R.id.lv_places);

		mFsqApp = new FoursquareApp(this, CLIENT_ID, CLIENT_SECRET);

		mAdapter = new NearbyAdapter(this);
		mNearbyList = new ArrayList<FsqVenue>();
		mProgress = new ProgressDialog(this);

		// listeners for FIRST-UP MENU

		mProfileButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(getApplicationContext(),
						Profile.class);
				if (Utility.mFacebook.isSessionValid()) {
					Utility.objectID = "me";
					startActivity(myIntent);
				}
			}
		});

		mReportButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent myIntent = new Intent(getApplicationContext(),
						Report.class);
				if (Utility.mFacebook.isSessionValid()) {
					Utility.objectID = "me";
					startActivity(myIntent);
				}
			}
		});

		/**
		 * mPlacesButton.setOnClickListener(new OnClickListener() { public void
		 * onClick(View v) { Intent myIntent = new
		 * Intent(getApplicationContext(), Places.class); if
		 * (Utility.mFacebook.isSessionValid()) { Utility.objectID = "me";
		 * startActivity(myIntent); }} });
		 **/

		// START MENU BUTTON
		exitButton = (Button) findViewById(R.id.exitButton);
		exitButton.setOnClickListener(new OnClickListener() {

			@Override
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
				String latitude = latitudeEt.getText().toString();
				String longitude = longitudeEt.getText().toString();
				String radius = radiusEt.getText().toString();

				if (latitude.equals("") || longitude.equals("")) {
					Toast.makeText(Places.this,
							"Latitude or longitude is empty",
							Toast.LENGTH_SHORT).show();
					return;
				}

				double lat = Double.valueOf(latitude);
				double lon = Double.valueOf(longitude);
				int rad = Integer.valueOf(radius);
				Log.i("Eric", "Pipi0");
				loadNearbyPlaces(lat, lon, rad);
			}
		});
	}

	private void loadNearbyPlaces(final double latitude, final double longitude, final int radius) {
		mProgress.show();

		new Thread() {
			@Override
			public void run() {
				int what = 0;

				try {
					Log.i("Eric", "Pipi1");
					mNearbyList = mFsqApp.getNearby(latitude, longitude, radius);
				} catch (Exception e) {
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
					Toast.makeText(Places.this,
							"No nearby places available", Toast.LENGTH_SHORT)
							.show();
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
