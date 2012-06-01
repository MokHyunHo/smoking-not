package com.facebook.android;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class PlaceDetails extends Activity {

	TextView tvPlaceName, tvPlaceAddress, tvNumRaiting;
	ListView lstReports;
	ProgressBar pbRaitings;
	ImageButton ibShowOnMap;
	Location location;

	ReportsListAdapter mAdapter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.place_details);

		tvPlaceName = (TextView) findViewById(R.id.tvPlaceName);
		tvPlaceAddress = (TextView) findViewById(R.id.tvPlaceAddress);
		tvNumRaiting = (TextView) findViewById(R.id.tv_raitings);
		lstReports = (ListView) findViewById(R.id.lstReports);
		pbRaitings = (ProgressBar) findViewById(R.id.pb_Raiting);
		pbRaitings.setProgressDrawable(getResources().getDrawable(
				R.drawable.my_progress));
		ibShowOnMap = (ImageButton) findViewById(R.id.ib_ShowOnMap);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();

		tvPlaceName.setText(extras.getString("PlaceName"));
		tvPlaceAddress.setText(extras.getString("PlaceAddress"));

		location = extras.getParcelable("PlaceLocation");
		int badRate = extras.getInt("BadRate");
		int goodRate = extras.getInt("GoogRate");
		int num_raitings = badRate + goodRate;
		String num_r;
		if (num_raitings > 0) {
			double rating = ((double) goodRate / ((double) badRate + (double) goodRate)) * 100;
			pbRaitings.setVisibility(View.VISIBLE);
			num_r = "Likes: " + goodRate + ", Dislikes: " + badRate;
			pbRaitings.setProgress((int) rating);
		} else {
			pbRaitings.setVisibility(View.INVISIBLE);
			num_r = "No reports for this place";
		}
		tvNumRaiting.setText(num_r);

		ibShowOnMap.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					Uri mUri = Uri.parse("geo:0,0?q=" + location.getLatitude()
							+ "," + location.getLongitude());
					Log.i("Eric", mUri.toString());
					Intent i = new Intent(Intent.ACTION_VIEW, mUri);
					startActivity(i);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

	}
}
