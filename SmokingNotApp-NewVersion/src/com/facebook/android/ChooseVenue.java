package com.facebook.android;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class ChooseVenue extends Activity {
	public static final String CLIENT_ID = "YP3ZQVYTZWNQVEWUNZV2LNIP0EKOLPSG40IVT4BT2TVKS5TP";
	public static final String CLIENT_SECRET = "SJMMUOXSX0FOUF5UHJYWBCUN3VQOPAO2CCCBUA4FPBCBEGDA";

	private FoursquareApp mFsqApp;
	private LocationEngine mLocEng;
	private ListView mListView;
	private ArrayList<FsqVenue> mNearbyList;
	private NearbyAdapter mAdapter;
	private ProgressDialog mProgress;
	private final int rad = 100;
	private Location mLocation = new Location(LocationManager.PASSIVE_PROVIDER);

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_venue);

		mFsqApp = new FoursquareApp(this, CLIENT_ID, CLIENT_SECRET);
		mLocEng = new LocationEngine(this);
		mAdapter = new NearbyAdapter(this, true);
		mProgress = new ProgressDialog(this);
		mListView = (ListView) findViewById(R.id.venue_list);
		mNearbyList = new ArrayList<FsqVenue>();
		if (mLocEng.isLocationEnabled())
			mLocation = mLocEng.getCurrentLocation();
		else {
			mLocation.setLatitude(32.06);
			mLocation.setLongitude(34.77);
		}
		mProgress.setMessage("Getting venue around...");
		mProgress.show();
		new Thread() {
			@Override
			public void run() {
				int what = 0;
				Looper.prepare();
				try {
					mNearbyList = mFsqApp.getNearby(mLocation.getLatitude(),
							mLocation.getLongitude(), rad);
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
				FsqVenue venue = (FsqVenue) mAdapter.getItem(position);

				Intent data = new Intent();

				data.putExtra("venueID", venue.id);
				data.putExtra("venueName", venue.name);
				setResult(RESULT_OK, data);
				Log.i("ERIC", "set result: " + data.getExtras().toString());
				finish();

			}
		});
		
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mProgress.dismiss();

			if (msg.what == 0) {
				if (mNearbyList.size() == 0) {
					Toast.makeText(ChooseVenue.this,
							"No nearby places available", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				mAdapter.setData(mNearbyList);
				Log.i("ERIC", "adapter: " + mAdapter.toString() + "lv: "
						+ mListView.toString());
				mListView.setAdapter(mAdapter);

			} else {
				Toast.makeText(ChooseVenue.this,
						"Failed to load nearby places: " + msg.what,
						Toast.LENGTH_SHORT).show();
			}
		}
	};

}
