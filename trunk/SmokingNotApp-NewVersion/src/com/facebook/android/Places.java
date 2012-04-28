package com.facebook.android;

//import java.io.IOException;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import com.facebook.android.R;
import com.facebook.android.FacebookMain;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
//import android.widget.ImageView;
//import android.widget.TextView;
import android.widget.TextView;

public class Places extends Activity implements View.OnClickListener {
	/** Called when the activity is first created. */

	private TextView tvReport, tvPlaces, tvProfile, tvAddress;
	// private EditText latitudeEt, longitudeEt, radiusEt;
	private Button exitButton, goBtn;// , updLocBtn;

	private FoursquareApp mFsqApp;
	private LocationEngine mLocEng;
	private ListView mListView;
	private NearbyAdapter mAdapter;
	private ArrayList<FsqVenue> mNearbyList;
	private ProgressDialog mProgress;

	private Location mLocation;

	CountDownLatch latch = new CountDownLatch(1);
	public static final String CLIENT_ID = "YP3ZQVYTZWNQVEWUNZV2LNIP0EKOLPSG40IVT4BT2TVKS5TP";
	public static final String CLIENT_SECRET = "SJMMUOXSX0FOUF5UHJYWBCUN3VQOPAO2CCCBUA4FPBCBEGDA";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// initialization of all the objects
		setContentView(R.layout.places);
		Init();
		try {
			updateLocation();
		} catch (InterruptedException Ex) {
			;
		}
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
				finish();
				startActivity(myIntent);

			}
		});

		goBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				int rad = 150;
				try {
					if (!mLocEng.isLocationEnabled()) {
						Toast.makeText(Places.this, "Location is unknown",
								Toast.LENGTH_SHORT).show();
						return;
					}
					if (mLocEng.isLocationSent())
						updateLocation();

					loadNearbyPlaces(mLocation.getLatitude(),
							mLocation.getLongitude(), rad);

				} catch (Exception ex) {
					Toast.makeText(Places.this,
							"Something bad happened: " + ex.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
			}

		});

	}

	public void updateLocation() throws InterruptedException {
		mProgress.setMessage("Retrieving location...");
		mProgress.show();

		new Thread() {
			@Override
			public void run() {
				int what = 0;
				Log.i("ERIC", "Thread started");
				Looper.prepare();
				try {

					int counter = 0;
					if (!mLocEng.isLocationEnabled()) {
						Toast.makeText(Places.this,
								"Location service is not available",
								Toast.LENGTH_SHORT);

					} else {
						while ((mLocation == null) && (counter < 5)) {
							mLocation = mLocEng.getCurrentLocation();
							if (mLocation != null)
								break;
							sleep(1000);
							counter++;
							Log.i("ERIC", "counter=" + counter);
						}
					}

				} catch (Exception Ex) {
					what = 1;
					Log.i("ERIC", "BAD UPDATE LOC: " + Ex.getMessage());

				}
				mHandler.sendMessage(mHandler.obtainMessage(what));

			}
		}.start();

	}

	private void Init() {
		tvReport = (TextView) findViewById(R.id.tvPlaReport);
		tvPlaces = (TextView) findViewById(R.id.tvPlaPlaces);
		tvProfile = (TextView) findViewById(R.id.tvPlaProfile);

		tvAddress = (TextView) findViewById(R.id.tvAddress);
		goBtn = (Button) findViewById(R.id.b_go);
		mListView = (ListView) findViewById(R.id.lv_places);

		mFsqApp = new FoursquareApp(this, CLIENT_ID, CLIENT_SECRET);
		mLocEng = new LocationEngine(this);

		mAdapter = new NearbyAdapter(this);
		mNearbyList = new ArrayList<FsqVenue>();
		mProgress = new ProgressDialog(this);
	}

	@Override
	public void onClick(View v) {

		Intent myIntent;
		switch (v.getId()) {
		case R.id.tvPlaReport:
			myIntent = new Intent(getApplicationContext(), Report.class);
			if (Utility.mFacebook.isSessionValid()) {
				Utility.objectID = "me";
				startActivity(myIntent);
			}
			break;
		case R.id.tvPlaPlaces:
			break;
		case R.id.tvPlaProfile:
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
		mProgress.setMessage("Retrieving nearby places");
		mProgress.show();

		try {
			mNearbyList = mFsqApp.getNearby(latitude, longitude, radius);

			if (mNearbyList.size() == 0) {
				Toast.makeText(Places.this, "No nearby places available",
						Toast.LENGTH_SHORT).show();
				return;
			}

			mAdapter.setData(mNearbyList);
			mListView.setAdapter(mAdapter);

		} catch (Throwable e) {
			Toast.makeText(Places.this, "Failed to load nearby places",
					Toast.LENGTH_SHORT).show();
		}
		mProgress.dismiss();

	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mProgress.dismiss();

			if (msg.what == 0) {
				if (mLocation != null) {
					tvAddress
							.setText(mLocEng.getAddressFromLocation(mLocation));
					Log.i("ERIC", "location: " + mLocation.toString());
				} else
					Toast.makeText(Places.this, "Unable to get location",
							Toast.LENGTH_LONG).show();

				/*
				 * if (mNearbyList.size() == 0) { Toast.makeText(Places.this,
				 * "No nearby places available", Toast.LENGTH_SHORT).show();
				 * return; }
				 * 
				 * mAdapter.setData(mNearbyList);
				 * mListView.setAdapter(mAdapter);
				 */
			} else {
				/*
				 * Toast.makeText(Places.this, "Failed to load nearby places: "
				 * + msg.what, Toast.LENGTH_SHORT).show();
				 */
				Toast.makeText(Places.this, "Error while getting location",
						Toast.LENGTH_LONG).show();
			}
		}
	};

}
