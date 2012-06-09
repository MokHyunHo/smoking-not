package com.facebook.android;

//import java.io.IOException;

import java.util.Collections;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.R;
import com.facebook.android.FacebookMain;
import com.google.gson.Gson;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Profile extends Activity {
	/** Called when the activity is first created. */

	private Button exitButton;
	private TextView mText;
	private TextView rank;
	private ImageView mUserPic;
	private ProgressBar pb;
	private TextView total_score;
	private ListView lvUserReports;
	private UserReportsAdapter mAdapter;
	private GooglePlacesAPI mGooglePlacesAPI;

	private LastUserReports lst;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// connection between XML & JAVA

		// initialization of all the objects
		setContentView(R.layout.profile);
		init();


		mGooglePlacesAPI = new GooglePlacesAPI(this);
		// get user info from server
		Gson gson2 = new Gson();
		WebRequest req = new WebRequest();
		String str = "";
		UserRequest ur_updated = null;
		try {
			JSONObject json2 = req
					.readJsonFromUrl(getString(R.string.DatabaseUrl)
							+ "/GetUser?mail=" + FacebookMain.email);
			str = (String) json2.get("user_req");
			Log.w("str=", str);
			ur_updated = gson2.fromJson(str, UserRequest.class);
		} catch (JSONException e) {
			Log.e("Profile error, can't get response from server, JSON exception",
					e.toString());
			// Log.w("str=", str);
		} catch (Exception e) {
			Log.e("Profile error, can't get response from server", e.toString());
			// Log.w("str=", str);
		}

		// user's progress
		pb = (ProgressBar) findViewById(R.id.progressbar);
		total_score = (TextView) findViewById(R.id.tv_score);

		pb.setProgress(ur_updated.GetScore());
		total_score.setText(ur_updated.GetScore() + "/100");

		// display current stage
		if ((ur_updated.GetScore() >= 0) && (ur_updated.GetScore() < 45))
			rank.setText("Beginner");
		if ((ur_updated.GetScore() >= 45) && (ur_updated.GetScore() < 135))
			rank.setText("Active");
		if ((ur_updated.GetScore() >= 135) && (ur_updated.GetScore() < 270))
			rank.setText("Advanced");
		if ((ur_updated.GetScore() >= 270) && (ur_updated.GetScore() < 405))
			rank.setText("Expert");
		if (ur_updated.GetScore() >= 405)
			rank.setText("Supervisor");

		// PROFILE INFORMATION
		mText.setText("Welcome " + FacebookMain.name);
		mUserPic.setImageBitmap(Utility.getBitmap(FacebookMain.picURL));
		Log.i("ERIC", FacebookMain.email);
		try {
			if (FacebookMain.email.compareTo("") != 0) {
				lst = getUserReports(FacebookMain.email);
				mAdapter.setData(lst);
				lvUserReports.setAdapter(mAdapter);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// START MENU BUTTON
		exitButton = (Button) findViewById(R.id.exitButton);
		exitButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent myIntent = new Intent(getApplicationContext(),
						FacebookMain.class);
				if (Utility.mFacebook.isSessionValid()) {
					Utility.objectID = "me";
				}
				startActivity(myIntent);

			}
		});

	}

	public void onBackPressed() {
		Intent myIntent = new Intent(getApplicationContext(),
				FacebookMain.class);
		startActivity(myIntent);
	}
	
	private void init() {
		mText = (TextView) findViewById(R.id.txt);
		rank = (TextView) findViewById(R.id.rank);
		mUserPic = (ImageView) findViewById(R.id.user_pic);

		lvUserReports = (ListView) findViewById(R.id.lvLastReports);
		mAdapter = new UserReportsAdapter(this);
	}

	
	private LastUserReports getUserReports(String userId) {
		LastUserReports LastReportsLst = null;
		Gson gson2 = new Gson();
		WebRequest req = new WebRequest();
		String str = null;
		
		try {
			JSONObject json2 = req
					.readJsonFromUrl(getString(R.string.DatabaseUrl)
							+ "/GetLastPlaces?mail=" + FacebookMain.email);
			str = (String) json2.get("report_request");
			Log.w("str=", str);
			LastReportsLst = gson2.fromJson(str, LastUserReports.class);

		} catch (JSONException e) {
			Log.e("Profile error, can't get response from server, JSON exception",
					e.toString());

		} catch (Exception e) {
			Log.e("Profile error, can't get response from server", e.toString());
		}

		Collections.sort(LastReportsLst.getLst(), new ReportDateComparator());
		return LastReportsLst;

	}
}
