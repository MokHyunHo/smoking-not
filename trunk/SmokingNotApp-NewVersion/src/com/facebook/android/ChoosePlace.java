package com.facebook.android;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class ChoosePlace extends Activity {
	public static final String CLIENT_ID = "YP3ZQVYTZWNQVEWUNZV2LNIP0EKOLPSG40IVT4BT2TVKS5TP";
	public static final String CLIENT_SECRET = "SJMMUOXSX0FOUF5UHJYWBCUN3VQOPAO2CCCBUA4FPBCBEGDA";

	private GooglePlacesAPI mGooglePlacesAPI; 
	private LocationEngine mLocEng;
	private ListView mListView;
	private ArrayList<GooglePlace> mNearbyList;
	private NearbyAdapter mAdapter;
	private ProgressDialog mProgress;
	private final int rad = 100;
	private Location mLocation;
	private ImageButton mSearch, mAdd;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_place);

		
		mSearch = (ImageButton) findViewById(R.id.ib_Search);	
		mAdd = (ImageButton) findViewById(R.id.ib_Add);
		
		mGooglePlacesAPI = new GooglePlacesAPI(this);
		mLocEng = new LocationEngine(this);
		mAdapter = new NearbyAdapter(this, true);
		mProgress = new ProgressDialog(this);
		mListView = (ListView) findViewById(R.id.places_list);
		mNearbyList = new ArrayList<GooglePlace>();
		if (mLocEng.isLocationEnabled())
			mLocation = mLocEng.getCurrentLocation();
		else {
			mLocation = new Location(LocationManager.PASSIVE_PROVIDER);
			mLocation.setLatitude(32.06);
			mLocation.setLongitude(34.77);
		}
		mProgress.setMessage("Getting places around...");
		mProgress.show();
		new Thread() {
			@Override
			public void run() {
				int what = 0;
				Looper.prepare();
				try {
					mNearbyList = mGooglePlacesAPI.getNearby(mLocation);
				} catch (Throwable e) {
					what = 1;
					// e.printStackTrace();
					Log.i("ERIC", "Catched " + e.toString());
				}

				mHandler.sendMessage(mHandler.obtainMessage(what));
			}
		}.start();
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				GooglePlace place = (GooglePlace) mAdapter.getItem(position);

				Intent data = new Intent();

				data.putExtra("placeID", place.id);
				data.putExtra("placeName", place.name);
				setResult(RESULT_OK, data);
				Log.i("ERIC", "set result: " + data.getExtras().toString());
				finish();

			}
		});
		
		mAdd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
	 				 Intent myIntent = new Intent(getApplicationContext(), AddPlace.class);
                     startActivity(myIntent);
				} catch (Throwable t) {
					;
				}

			}
		});
		
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mProgress.dismiss();

			if (msg.what == 0) {
				if (mNearbyList.size() == 0) {
					Toast.makeText(ChoosePlace.this,
							"No nearby places available", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				mAdapter.setData(mNearbyList);
				Log.i("ERIC", "adapter: " + mAdapter.toString() + "lv: "
						+ mListView.toString());
				mListView.setAdapter(mAdapter);

			} else {
				Toast.makeText(ChoosePlace.this,
						"Failed to load nearby places: " + msg.what,
						Toast.LENGTH_SHORT).show();
			}
		}
	};

}
