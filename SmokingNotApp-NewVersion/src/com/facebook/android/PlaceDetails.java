package com.facebook.android;

import java.util.ArrayList;
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

	TextView tvPlaceName, tvPlaceAddress, tvNumRaiting, tvReportsLbl;
	ListView lvReports;
	ProgressBar pbRaitings;
	ImageButton ibShowOnMap;
	Location location;

	ReportsListAdapter mAdapter;
	int num_raitings;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.place_details);

		tvPlaceName = (TextView) findViewById(R.id.tvPlaceName);
		tvPlaceAddress = (TextView) findViewById(R.id.tvPlaceAddress);
		tvNumRaiting = (TextView) findViewById(R.id.tv_raitings);
		tvReportsLbl = (TextView) findViewById(R.id.tvReportsLbl);
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
				ArrayList<String[]> lst = getPlaceReports(PlaceID);
				Log.i("ERIC", "XX size: " + lst.size());
				mAdapter.setData(lst);
				lvReports.setAdapter(mAdapter);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else
		{
			tvReportsLbl.setVisibility(View.INVISIBLE);
		}

	}

	private ArrayList<String[]> getPlaceReports(String PlaceId) {
		ArrayList<String[]> lstReports = new ArrayList<String[]>();
		PlaceReports mPlaceReports = null;
		Gson gson2 = new Gson();
		WebRequest req = new WebRequest();
		String str = null;
		String[] tmp;
		try {
			JSONObject json2 = req
					.readJsonFromUrl(getString(R.string.DatabaseUrl)
							+ "/GetLastPlaces?mail=" + FacebookMain.email);
			str = (String) json2.get("report_request");
			Log.w("str=", str);
			mPlaceReports = gson2.fromJson(str, PlaceReports.class);

		} catch (JSONException e) {
			Log.e("Profile error, can't get response from server, JSON exception",
					e.toString());
			//Log.w("str=", str);

		} catch (Exception e) {
			Log.e("Profile error, can't get response from server", e.toString());
			//Log.w("str=", str);
		}
		int i = 0;
		// for (ReportRequest item: mPlaceReports.getLst() ) {
		// Random rnd = new Random();
		// int num = rnd.nextInt(20);// fiveLst.getLst().size();
		for (i = 0; i < num_raitings; i++) {
			tmp = new String[3];
			// i++;
			// ReportRequest item = fiveLst.getLst().get(i);
			tmp[0] = "Report date #" + i; // fiveLst.getLst().get(i).getLocationId();
			tmp[1] = "Report type #" + i; // fiveLst.getLst().get(i).getReportdate();
			tmp[2] = "Report details #" + i; // fiveLst.getLst().get(i).getReportkind();

			/*
			 * tmp[0] = item.getLocationId(); tmp[1] = item.getReportdate();
			 * tmp[2] = item.getReportkind();
			 */
			// JSONObject pl_det = mGooglePlacesAPI.getPlaceDetails(item
			// .getLocationId());
			lstReports.add(tmp);

			// if (i > 4)
			// break;
		}

		Log.i("ERIC", "size: " + lstReports.size());

		return lstReports;

	}

}
