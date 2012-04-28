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

	/*
	 * private enum handlerMessages { M_LS_DOWN, M_UPD_LOC_ERR, M_ADDRESS_NA,
	 * M_LOC_OK, M_ADDRESS_OK;
	 * 
	 * private final int msg; handlerMessages(int msg) { this.msg = msg; } }
	 */

	private final int M_LS_DOWN = 0;
	private final int M_UPD_LOC_ERR = 1;
	private final int M_ADDRESS_NA = 2;
	private final int M_LOC_OK = 3;
	private final int M_ADDRESS_OK = 4;
	private final int M_GET_PLACES_ERR = 5;
	private final int M_GET_PLACES_OK = 6;

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
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// initialization of all the objects
		setContentView(R.layout.places);
		Init();


		updateLocation();
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
					if (!mLocEng.isLocationSent()) {
						Log.i("ERIC", "latched");
						latch = new CountDownLatch(1);
						updateLocation();
						latch.await();
					}
					if (mLocation != null)
						loadNearbyPlaces(mLocation.getLatitude(),
								mLocation.getLongitude(), rad);
					else
						Toast.makeText(Places.this, "Location is unknown",
								Toast.LENGTH_SHORT).show();

				} catch (Exception ex) {
					Toast.makeText(Places.this,
							"Something bad happened: " + ex.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
			}

		});

	}

	public void updateLocation() {
		mProgress.setMessage("Retrieving location...");
		mProgress.show();

		new Thread() {
			@Override
			public void run() {
				int what = M_LOC_OK;
				Looper.prepare();
				try {

					int counter = 0;
					if (!mLocEng.isLocationEnabled()) {
						what = M_LS_DOWN;

					} else {
						while ((mLocation == null) && (counter < 5)) 
						{
							mLocation = mLocEng.getCurrentLocation();
							if (mLocation != null)
								break;
							sleep(1000);
							counter++;
							Log.i("ERIC", "counter=" + counter);
						}
					}

				} catch (Exception Ex) {
					what = M_UPD_LOC_ERR;
					Log.i("ERIC", "BAD UPDATE LOC: " + Ex.getMessage());

				}
				mHandler.sendMessage(mHandler.obtainMessage(what));

			}
		}.start();

	}



	private void loadNearbyPlaces(final double latitude,
			final double longitude, final int radius) {
		mProgress.setMessage("Retrieving nearby places");
		mProgress.show();
		new Thread() {
			@Override
			public void run() {
				int what = M_GET_PLACES_OK;
				Looper.prepare();
				try {
					mNearbyList = mFsqApp
							.getNearby(latitude, longitude, radius);

				} catch (Throwable e) {
					what = M_GET_PLACES_ERR;
				}
				mHandler.sendMessage(mHandler.obtainMessage(what));
			}
		}.start();

	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mProgress.dismiss();
			Log.i("ERIC", "what: " + msg.what);
			switch (msg.what) {
			case M_LS_DOWN:
				Toast.makeText(Places.this, "Location Service is unavailable",
						Toast.LENGTH_LONG).show();
				break;
			case M_UPD_LOC_ERR:
				Toast.makeText(Places.this, "Error while getting location",
						Toast.LENGTH_LONG).show();
				break;
			case M_LOC_OK:
				if (mLocation != null) {
					tvAddress
							.setText(mLocEng.getAddressFromLocation(mLocation));
					Log.i("ERIC", "location: " + mLocation.toString());
				} else
					Toast.makeText(Places.this, "Unable to get location",
							Toast.LENGTH_LONG).show();
				break;

			case M_GET_PLACES_OK:
				if (mNearbyList.size() == 0) {
					Toast.makeText(Places.this, "No nearby places available",
							Toast.LENGTH_LONG).show();
					break;
				}

				mAdapter.setData(mNearbyList);
				mListView.setAdapter(mAdapter);
				break;

			case M_GET_PLACES_ERR:
				Toast.makeText(Places.this, "Failed to load nearby places",
						Toast.LENGTH_LONG).show();
				break;

			}
			latch.countDown();
			Log.i("ERIC", "unlatched");
		}

	};

}
