package com.facebook.android;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.EditText;
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
	private EditText etSearch;
	private ImageButton mSearch, mAdd;
	
	private String searchStr;
	
	final static int iAddPlace = 0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_place);

		etSearch = (EditText) findViewById(R.id.et_Search);
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
		if (mLocation == null)
			mLocation = mLocEng.getLastKnownLocation();
		if (mLocation == null)
		{
			mLocation = new Location(LocationManager.PASSIVE_PROVIDER);
			mLocation.setLatitude(32.06);
			mLocation.setLongitude(34.77);
		}
		mProgress.setMessage("Getting places around...");
		loadPlaces(false);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v,
					int position, long id) {
				GooglePlace place = (GooglePlace) mAdapter.getItem(position);

				Intent data = new Intent();

				data.putExtra("placeID", place.id);
				data.putExtra("placeReference", place.refrence);
				data.putExtra("placeName", place.name);
				data.putExtra("placeVicinity", place.vicinity);
				if (place.location != null)
					data.putExtra("placeLocation", place.location);
				else
					data.putExtra("placeLocation", mLocation);
				
				setResult(RESULT_OK, data);
				Log.i("ERIC", "set result: " + place.name + " " + place.location.toString());
				finish();

			}
		});
		
		mSearch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				searchStr = etSearch.getText().toString(); 
				//if (searchStr.isEmpty()) return;
				
				try
				{
					mProgress.setMessage("Searching for places...");
					
					loadPlaces(true);

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

		});
		
		mAdd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
	 				 Intent myIntent = new Intent(getApplicationContext(), AddPlace.class);
	 				 
	 				 Log.i("ERIC", mLocation.toString());
	 				 myIntent.putExtra("location", mLocation);
	 				 
                     startActivityForResult(myIntent, iAddPlace);
				} catch (Throwable t) {
					;
				}

			}
		});
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			super.onActivityResult(requestCode, resultCode, data);
			if (resultCode == RESULT_OK) {
				//Bundle extras = data.getExtras();
				switch (requestCode) {
				case iAddPlace:
					setResult(RESULT_OK, data);
					Log.i("ERIC", "set result: " + data.getExtras().toString());
					finish();
					break;
				}
			}
		} catch (Throwable Ex) {
			Log.i("ERIC", "msg: " + Ex.toString());
		}
	}

	private void loadPlaces(final boolean query)
	{
		mProgress.show();
		new Thread() {
			@Override
			public void run() {
				int what = 0;
				Looper.prepare();
				try {
					//int radius = GooglePlacesAPI.ALLOWED_RADIUS;
					int radius = GooglePlacesAPI.ALLOWED_RADIUS; 
					if (!query)
						mNearbyList = mGooglePlacesAPI.getNearby(mLocation, radius);
					else
						mNearbyList = mGooglePlacesAPI.searchPlaces(mLocation, (mLocation != null), searchStr, radius);
					
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

			if (msg.what == 0) {
				if (mNearbyList.size() == 0) {
					Toast.makeText(ChoosePlace.this,
							"No places available", Toast.LENGTH_SHORT)
							.show();
					mProgress.dismiss();
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
			mProgress.dismiss();
		}
	};

}
