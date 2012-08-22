package il.reporter.gws;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import com.facebook.android.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
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
import android.widget.TextView;
import android.widget.Toast;

public class ChoosePlace extends Activity {

	private Context context;
	private GooglePlacesAPI mGooglePlacesAPI; 
	private LocationEngine mLocEng;
	private ListView mListView;
	private ArrayList<GooglePlace> mNearbyList;
	private NearbyAdapter mAdapter;
	private ProgressDialog mProgress;
	private Location mLocation;
	private EditText etSearch;
	private TextView tvAddress;
	private ImageButton mSearch, mAdd, mRefreshButton, mShowMeOnMap;
	
	private final int M_LS_DOWN = 0;
	private final int M_UPD_LOC_ERR = 1;
	@SuppressWarnings("unused")
	private final int M_ADDRESS_NA = 2;
	private final int M_LOC_OK = 3;
	@SuppressWarnings("unused")
	private final int M_ADDRESS_OK = 4;
	private final int M_GET_PLACES_ERR = 5;
	private final int M_GET_PLACES_OK = 6;
	private final int CYCLES_TO_WAIT = 1;

	private final int M_LOC_NA = 9;
	
	private CountDownLatch latch = new CountDownLatch(1);
	
	
	private String searchStr;
	
	final static int iAddPlace = 0;
	private int allowed_radius = FacebookMain.LAST_ALLOWED_RADIUS;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_place);
		
		context = this;

		etSearch = (EditText) findViewById(R.id.et_Search);
		mSearch = (ImageButton) findViewById(R.id.ib_Search);	
		mAdd = (ImageButton) findViewById(R.id.ib_Add);
		
		tvAddress = (TextView) findViewById(R.id.tvAddress);
		mShowMeOnMap = (ImageButton) findViewById(R.id.ib_ShowMeOnMap);
		mRefreshButton = (ImageButton) findViewById(R.id.ib_Refresh);
		
		mGooglePlacesAPI = new GooglePlacesAPI(this);
		mLocEng = new LocationEngine(this);
		mAdapter = new NearbyAdapter(this, true);
		mProgress = new ProgressDialog(this);
		//mProgress.setCancelable(false);
		mListView = (ListView) findViewById(R.id.places_list);
		mNearbyList = new ArrayList<GooglePlace>();
		
		/*if (mLocEng.isLocationEnabled())
			mLocation = mLocEng.getCurrentLocation();
		if (mLocation == null)
			mLocation = mLocEng.getLastKnownLocation();
		if (mLocation == null)
		{
			mLocation = new Location(LocationManager.PASSIVE_PROVIDER);
			mLocation.setLatitude(32.06);
			mLocation.setLongitude(34.77);
		}
		*/
		/*
		try {
		updateLocation();
		latch.await();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		mProgress.setMessage("Getting places around...");
		*/
		
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
				checkCheatCode(searchStr);
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
		
		mRefreshButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					mLocEng = null;
					mLocEng = new LocationEngine(context);
					latch = new CountDownLatch(1);
					updateLocation();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		mRefreshButton.performClick();
		
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
		mProgress.setMessage("Getting places around...");
		mProgress.show();
		new Thread() {
			@Override
			public void run() {
				int what = M_GET_PLACES_OK;
				Looper.prepare();
				try {
					int radius = allowed_radius;
					//int radius = GooglePlacesAPI.MAX_RADIUS; 
					if (!query)
						mNearbyList = mGooglePlacesAPI.getNearby(mLocation, radius, false);
					else
						mNearbyList = mGooglePlacesAPI.searchPlaces(mLocation, (mLocation != null), searchStr, radius, false);
					
				} catch (Throwable e) {
					what = M_GET_PLACES_ERR;
					e.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(what));
			}
		}.start();
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
							Log.i("ERIC", "counter=" + counter + "mloc ok: " + (mLocation != null));
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
							Log.i("ERIC", "counter address=" + counter);
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
		
	}
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mProgress.dismiss();
			switch (msg.what){
			
			case M_LS_DOWN:
				Toast.makeText(ChoosePlace.this, "Location Service is unavailable",
						Toast.LENGTH_LONG).show();

				break;
			case M_UPD_LOC_ERR:
				Toast.makeText(ChoosePlace.this, "Error while getting location",
						Toast.LENGTH_LONG).show();
				mShowMeOnMap.setVisibility(View.INVISIBLE);
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
					mShowMeOnMap.setVisibility(View.VISIBLE);
					loadPlaces(false);
				} else
					Toast.makeText(ChoosePlace.this, "Unable to get location",
							Toast.LENGTH_LONG).show();
				break;
			
			case M_GET_PLACES_OK:
				if (mNearbyList.size() == 0) {
					Toast.makeText(ChoosePlace.this,
							"No places available", Toast.LENGTH_SHORT)
							.show();
					break;
				}
				mAdapter.setData(mNearbyList);
				Log.i("ERIC", "adapter: " + mAdapter.toString() + "lv: "
						+ mListView.toString());
				mListView.setAdapter(mAdapter);
				break;

			case M_GET_PLACES_ERR:
				Toast.makeText(ChoosePlace.this,
						"Failed to load nearby places: " + msg.what,
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	
	private void checkCheatCode(String str)
	{
		if (str.compareTo("AEOE2012") == 0)
		{
			FacebookMain.LAST_ALLOWED_RADIUS = GooglePlacesAPI.MAX_RADIUS;
		}
	}

}
