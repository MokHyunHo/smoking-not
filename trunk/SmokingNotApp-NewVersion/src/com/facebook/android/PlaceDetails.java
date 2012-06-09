package com.facebook.android;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

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

	private TextView tvPlaceName, tvPlaceAddress, tvNumRaiting, tvReportsLbl,
			tvReasons;
	private ListView lvReports;
	private ProgressBar pbRaitings;
	private ImageButton ibShowOnMap;
	private Location location;

	private ReportsListAdapter mAdapter;
	private int num_raitings;

	private String reasons_str[];
	private int reasons[];
	int num_of_reasons;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.place_details);

		reasons_str = getResources().getStringArray(R.array.complaint_reasons);
		num_of_reasons = reasons_str.length;
		reasons = new int[num_of_reasons];

		tvPlaceName = (TextView) findViewById(R.id.tvPlaceName);
		tvPlaceAddress = (TextView) findViewById(R.id.tvPlaceAddress);
		tvNumRaiting = (TextView) findViewById(R.id.tv_raitings);
		tvReportsLbl = (TextView) findViewById(R.id.tvReportsLbl);
		tvReasons = (TextView) findViewById(R.id.tvReasons);
		lvReports = (ListView) findViewById(R.id.lstReports);
		pbRaitings = (ProgressBar) findViewById(R.id.pb_Raiting);
		pbRaitings.setProgressDrawable(getResources().getDrawable(
				R.drawable.my_progress));
		ibShowOnMap = (ImageButton) findViewById(R.id.ib_ShowOnMap);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();

		tvPlaceName.setText(extras.getString("PlaceName"));
		tvPlaceAddress.setText(extras.getString("PlaceAddress"));

		String PlaceID = extras.getString("PlaceID");

		location = extras.getParcelable("PlaceLocation");
		int badRate = extras.getInt("BadRate");
		int goodRate = extras.getInt("GoogRate");
		num_raitings = badRate + goodRate;
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

		if (num_raitings > 0) {
			mAdapter = new ReportsListAdapter(this);
			try {
				LastUserReports lst = getPlaceReports(PlaceID);
				Log.i("ERIC", "XX size: " + lst.getLst().size());
				mAdapter.setData(lst);
				lvReports.setAdapter(mAdapter);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			tvReportsLbl.setVisibility(View.INVISIBLE);
		}

	}

	private LastUserReports getPlaceReports(String PlaceId) {
		LastUserReports mPlaceReports = null;
		Gson gson2 = new Gson();
		WebRequest req = new WebRequest();
		String str = null;
		try {
			JSONObject json2 = req
					.readJsonFromUrl(getString(R.string.DatabaseUrl)
							+ "/GetHistoryPlaces?locationid=" + PlaceId);
			str = (String) json2.get("report_request");
			Log.w("str=", str);
			mPlaceReports = gson2.fromJson(str, LastUserReports.class);

			for (ReportDetails r : mPlaceReports.getLst()) {
				Log.i("Reasons", "num reasons: " + r.getReasons().length);
				if (r.getReasons().length == num_of_reasons) 
				{
					Log.i("Reasons", "Entered summing: " + r.getDate());
					for (int i = 0; i < num_of_reasons; i++) {
						Log.i("Reasons", "summed: " + i);
						reasons[i] += r.getReasons()[i];
					}
				}
			}
			StringBuilder my_reasons = new StringBuilder("");

			for (int i = 0; i < num_of_reasons; i++) 
			{
				Log.i("Reasons", "reasons[i]=" + reasons[i]); 
				if (reasons[i] > 0) {
					my_reasons.append(reasons_str[i]).append(": ").append(reasons[i]).append("\n");
				}
			}
			Log.i("Reasons", "my_reasons: " + my_reasons.toString()); 
			tvReasons.setText(my_reasons.toString());

		} catch (JSONException e) {
			Log.e("Profile error, can't get response from server, JSON exception",
					e.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}

		Collections.sort(mPlaceReports.getLst(), new ReportDateComparator());
		return mPlaceReports;

	}

}
