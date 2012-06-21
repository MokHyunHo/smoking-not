package il.reporter.gws;

//import java.io.IOException;

import il.reporter.gws.FacebookMain;

import java.util.Collections;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.R;
import com.google.gson.Gson;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.Toast;

public class Profile extends Activity {
	/** Called when the activity is first created. */

	private Context context;
	private Button exitButton;
	private TextView mText;
	private TextView rank;
	private ImageView mUserPic;
	private ProgressBar pb, pbLoading;
	private TextView total_score, tvNoReports, tvLastReports, tvLastHazards;
	private ListView lvLastList;
	private UserReportsAdapter mReportsAdapter;
	private UserHazardsAdapter mHazardsAdapter;
	private LastUserReports lstReports;
	private LastUserHazards lstHazards;

	private UserRequest ur_updated = null;

	// added---------------------------------------------------------------------
	private Button mQuestionButton;
	private View tmpView;

	private String userId = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.profile);
		init();

		if (FacebookMain.email != null)
			userId = FacebookMain.email;

		new GooglePlacesAPI(this);

		// added---------------------------------------------------------------------
		mQuestionButton = (Button) findViewById(R.id.question);

		mQuestionButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				tmpView = v;
				showQuestionDialog(tmpView);

			}
		});

		getUserDetails();
		tvLastReports.performClick();

	}

	public void getUserDetails() {

		new Thread() {
			public void run() {

				// get user info from server
				Gson gson2 = new Gson();
				WebRequest req = new WebRequest();
				String str = "";

				try {
					JSONObject json2 = req
							.readJsonFromUrl(getString(R.string.DatabaseUrl)
									+ "/GetUser?mail=" + userId);
					str = (String) json2.get("user_req");
					Log.w("str=", str);
					ur_updated = gson2.fromJson(str, UserRequest.class);
				} catch (JSONException e) {
					Log.e("Profile error, can't get response from server, JSON exception",
							e.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(2));
			}
		}.start();

	}

	private void fillUserDetails() {
		try {
			// user's progress
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

		} catch (Exception e) {
			Toast.makeText(context, "Error retrieving data...",
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}

	}

	public void onPause() {
		super.onPause();
		finish();
	}

	private void swapButtons(int now_clicked) {

		int color1 = (now_clicked == 1 ? Color.BLACK : Color.GRAY);
		int color2 = (now_clicked == 1 ? Color.GRAY : Color.BLACK);

		tvLastHazards.setBackgroundColor(color1);
		tvLastReports.setBackgroundColor(color2);

	}

	private class onLastReportsClick implements OnClickListener {

		public void onClick(View v) {

			swapButtons(0);
			lvLastList.setAdapter(null);
			tvNoReports.setVisibility(View.INVISIBLE);
			mReportsAdapter = new UserReportsAdapter(context);
			pbLoading.setVisibility(View.VISIBLE);
			getUserReports(userId);
		}

	}

	private class onLastHazardsClick implements OnClickListener {

		public void onClick(View v) {
			swapButtons(1);
			lvLastList.setAdapter(null);
			tvNoReports.setVisibility(View.INVISIBLE);
			mReportsAdapter = new UserReportsAdapter(context);
			pbLoading.setVisibility(View.VISIBLE);
			getUserHazards(userId);
		}

	}

	private void init() {
		mText = (TextView) findViewById(R.id.txt);
		tvNoReports = (TextView) findViewById(R.id.tvNoReports);
		rank = (TextView) findViewById(R.id.rank);
		mUserPic = (ImageView) findViewById(R.id.user_pic);

		tvLastReports = (TextView) findViewById(R.id.tvLastReports);
		tvLastHazards = (TextView) findViewById(R.id.tvLastHazards);

		pb = (ProgressBar) findViewById(R.id.progressbar);
		total_score = (TextView) findViewById(R.id.tv_score);

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

		lvLastList = (ListView) findViewById(R.id.lvLastReports);
		pbLoading = (ProgressBar) findViewById(R.id.pbLoading);

		tvLastReports.setOnClickListener(new onLastReportsClick());
		tvLastHazards.setOnClickListener(new onLastHazardsClick());
	}

	private void getUserReports(final String userId) {

		new Thread() {
			public void run() {

				Gson gson2 = new Gson();
				WebRequest req = new WebRequest();
				String str = null;

				try {
					JSONObject json2 = req
							.readJsonFromUrl(getString(R.string.DatabaseUrl)
									+ "/GetLastPlaces" + "?mail=" + userId);
					str = (String) json2.get("report_request");
					Log.w("str=", str);
					lstReports = gson2.fromJson(str, LastUserReports.class);

					Collections.sort(lstReports.getLst(),
							new ReportDateComparator());

				} catch (Exception e) {
					e.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(0));

			}
		}.start();
	}

	private void getUserHazards(final String userId) {

		new Thread() {
			public void run() {

				Gson gson2 = new Gson();
				WebRequest req = new WebRequest();
				String str = null;

				try {
					JSONObject json2 = req
							.readJsonFromUrl(getString(R.string.DatabaseUrl)
									+ "/GetLastHazards" + "?mail=" + userId);
					str = (String) json2.get("hazard_request");
					Log.w("str=", str);
					lstHazards = gson2.fromJson(str, LastUserHazards.class);

					Collections.sort(lstHazards.getLst(),
							new HazardDateComparator());

					mHandler.sendMessage(mHandler.obtainMessage(1));

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}.start();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
				case 0:
					pbLoading.setVisibility(View.INVISIBLE);

					if (lstReports.getLst().size() > 0) {
						mReportsAdapter.setData(lstReports);
						lvLastList.setAdapter(mReportsAdapter);
						lvLastList.setVisibility(View.VISIBLE);
					} else {
						tvNoReports.setVisibility(View.VISIBLE);
					}
					break;

				case 1:
					pbLoading.setVisibility(View.INVISIBLE);

					if (lstHazards.getLst().size() > 0) {
						mHazardsAdapter.setData(lstHazards);
						lvLastList.setAdapter(mHazardsAdapter);
						lvLastList.setVisibility(View.VISIBLE);
					} else {
						tvNoReports.setVisibility(View.VISIBLE);
					}
					break;
				case 2:
					fillUserDetails();
					break;

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	};

	// added---------------------------------------------------------------------
	private void showQuestionDialog(View v) {
		AlertDialog alertDialog = new AlertDialog.Builder(v.getContext())
				.create();
		alertDialog.setTitle("Manage a personal profile.");
		String str = "Here you can see your reports' history and your number of points.\n"
				+ "You get points this way:\n"
				+ "•	Positive report - 2 points.\n"
				+ "•	Negative report - 1 point.\n"
				+ "•	Hazards report  - 1 point.\n"
				+ "The more points you'll get, the higher your level will be and your report will have a bigger influence because we consider you as very credible.\n";
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
