package com.facebook.android;

//import java.io.IOException;

import java.util.Collections;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.R;
import com.facebook.android.FacebookMain;
import com.google.gson.Gson;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

	private Context context;
	private Button exitButton;
	private TextView mText;
	private TextView rank;
	private ImageView mUserPic;
	private ProgressBar pb, pbLoading;
	private TextView total_score, tvNoReports;
	private ListView lvUserReports;
	private UserReportsAdapter mAdapter;
	private LastUserReports lst;
	// added---------------------------------------------------------------------
	private Button mQuestionButton;
	private View tmpView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.profile);
		init();

		new GooglePlacesAPI(this);
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

		// added---------------------------------------------------------------------
		mQuestionButton = (Button) findViewById(R.id.question);

		mQuestionButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				tmpView = v;
				showQuestionDialog(tmpView);

			}
		});

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
				getUserReports(FacebookMain.email);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void onPause() {
		super.onPause();
		finish();
	}

	private void init() {
		mText = (TextView) findViewById(R.id.txt);
		tvNoReports = (TextView) findViewById(R.id.tvNoReports);
		rank = (TextView) findViewById(R.id.rank);
		mUserPic = (ImageView) findViewById(R.id.user_pic);

		// START MENU BUTTON
		exitButton = (Button) findViewById(R.id.exitButton);
		exitButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (Utility.mFacebook.isSessionValid()) {
					Utility.objectID = "me";
				}
				finish();

			}
		});

		lvUserReports = (ListView) findViewById(R.id.lvLastReports);
		mAdapter = new UserReportsAdapter(context);

		pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
	}

	private void getUserReports(String userId) {

		new Thread() {
			public void run() {

				Gson gson2 = new Gson();
				WebRequest req = new WebRequest();
				String str = null;

				try {
					JSONObject json2 = req
							.readJsonFromUrl(getString(R.string.DatabaseUrl)
									+ "/GetLastPlaces?mail="
									+ FacebookMain.email);
					str = (String) json2.get("report_request");
					Log.w("str=", str);
					lst = gson2.fromJson(str, LastUserReports.class);

				} catch (JSONException e) {
					Log.e("Profile error, can't get response from server, JSON exception",
							e.toString());

				} catch (Exception e) {
					Log.e("Profile error, can't get response from server",
							e.toString());
				}

				Collections.sort(lst.getLst(), new ReportDateComparator());

				mHandler.sendMessage(mHandler.obtainMessage(0));

			}
		}.start();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				pbLoading.setVisibility(View.INVISIBLE);
				Log.i("Eric lst", "size: " + lst.getLst().size());
				if (lst.getLst().size() > 0) {
					mAdapter.setData(lst);
					lvUserReports.setAdapter(mAdapter);
					lvUserReports.setVisibility(View.VISIBLE);
				} else {
					tvNoReports.setVisibility(View.VISIBLE);
				}
				break;

			}

		}

	};

	// added---------------------------------------------------------------------
	private void showQuestionDialog(View v) {
		AlertDialog alertDialog = new AlertDialog.Builder(v.getContext())
				.create();
		alertDialog.setTitle("Your Report Has Been Sent!");
		String str = "By selecting the Profile option, you'll be able to see your reports' history and your number of points. you get points in following ways:\n"
				+ "•	For every positive report, you'll get 2 points.\n"
				+ "•	For every negative report, you'll get 1 point.\n"
				+ "•	For every hazards report, you'll get 1 point.\n"
				+ "The more points you'll get, the higher your level will be and the higher the value of your report will get. In other words, in the highest level, your report will have a bigger influence because we consider you as very credible.\n";
		alertDialog.setMessage(str);

		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// here you can add functions
			}
		});
		alertDialog.setIcon(R.drawable.qm);
		alertDialog.show();

	}
}
