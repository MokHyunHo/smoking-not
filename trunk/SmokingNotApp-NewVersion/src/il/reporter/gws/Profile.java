package il.reporter.gws;

//import java.io.IOException;

import il.reporter.gws.FacebookMain;

import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;
import org.json.JSONStringer;

import com.facebook.android.R;
import com.google.gson.Gson;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

public class Profile extends Activity {
	/** Called when the activity is first created. */

	private Context context;
	private ImageButton exitButton;
	private TextView mText;
	private TextView rank, tvNextRank;
	private ImageView mUserPic;
	private ProgressBar pb, pbLoading;
	private TextView total_score, tvNoReports, tvLastReports, tvLastHazards,
			tvNotification;
	private Button btnNext, btnPrev, btnClear;
	private TextSwitcher mSwitcher;
	private ListView lvLastList;
	private LastReportsAdapter mReportsAdapter;
	private UserHazardsAdapter mHazardsAdapter;
	private LastReports lstReports;
	private LastUserHazards lstHazards;

	private UserRequest ur_updated = null;

	private int mPosition = 0;

	// added---------------------------------------------------------------------
	private Button mQuestionButton;
	private View tmpView;

	@SuppressWarnings("null")
	private String userId = "";

	private String notifications[];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.profile);
		init();

		if (FacebookMain.email != null)
			userId = FacebookMain.email;

		// new GooglePlacesAPI(this);

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
				try {
					// get user info from server
					Gson gson2 = new Gson();
					WebRequest req = new WebRequest();
					String str = "";

					JSONObject json2 = req
							.readJsonFromUrl(getString(R.string.DatabaseUrl)
									+ "/GetUser?mail=" + userId);

					str = (String) json2.get("user_req");
					Log.w("str=", str);

					ur_updated = gson2.fromJson(str, UserRequest.class);

					String message = ur_updated.GetMessage();

					if (message.compareTo("empty") != 0)
						buildNotifications(message);

				} catch (Exception e) {
					e.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(2));
			}
		}.start();
		// user has a notification

	}

	private void buildNotifications(String message) {

		Log.i("ERIC", "has notifications");
		String delimiter = "#";
		String reports_id[] = null;
		reports_id = message.split(delimiter);
		notifications = new String[reports_id.length];
		mPosition = 0;

		for (int i = 0; i < reports_id.length; i++) {

			notifications[i] = "Some user reported same report as you on\n\""
					+ reports_id[i] + "\"\nYou got 1 point for this!";
		}

	}

	private void fillUserDetails() {
		try {

			// display current stage
			ur_updated.handleRanks(this);
			rank.setText("Your rank is: " + ur_updated.GetRank());
			
			if (ur_updated.GetNextRank().compareTo("") != 0)
				tvNextRank.setText("Next rank: " + ur_updated.GetNextRank());
			else
				tvNextRank.setText("You reached the highest rank!");
			
			// user's progress
			pb.setMax(ur_updated.GetNextRankScore());
			pb.setProgress(ur_updated.GetScore());
			total_score.setText(ur_updated.GetScore() + "/" + ur_updated.GetNextRankScore());

			// PROFILE INFORMATION
			mText.setText("Welcome " + FacebookMain.name);
			mUserPic.setImageBitmap(Utility.getBitmap(FacebookMain.picURL));

			if (notifications != null) {

				if (notifications.length > 0) {
					tvNotification.setText(notifications[0]);
					btnClear.setEnabled(true);

					if (notifications.length > 1)
						btnNext.setEnabled(true);
				}
			}

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
			mReportsAdapter = new LastReportsAdapter(context, false);
			pbLoading.setVisibility(View.VISIBLE);
			getUserReports(userId);
		}

	}

	private class onLastHazardsClick implements OnClickListener {

		public void onClick(View v) {
			swapButtons(1);
			lvLastList.setAdapter(null);
			tvNoReports.setVisibility(View.INVISIBLE);
			mHazardsAdapter = new UserHazardsAdapter(context);
			pbLoading.setVisibility(View.VISIBLE);
			getUserHazards(userId);
		}

	}

	private class onNextPrevClick implements OnClickListener {

		private int direction;

		public onNextPrevClick(int direction)

		{
			this.direction = direction;
		}

		public void onClick(View v) {

			mPosition += direction;

			btnNext.setEnabled(mPosition < notifications.length - 1);
			btnPrev.setEnabled(mPosition > 0);

			tvNotification.setText(notifications[mPosition]);

		}

	}

	private class onClearClick implements OnClickListener {

		public void onClick(View v) {

			btnPrev.setEnabled(false);
			btnNext.setEnabled(false);
			btnClear.setEnabled(false);

			tvNotification.setText("No notifications");
			notifications = null;

			// convert report request to gson string
			Gson gson5 = new Gson();
			String UserStr = gson5.toJson(ur_updated);
			JSONStringer json5 = null;
			WebRequest req2 = new WebRequest();

			// prepare Json
			try {
				json5 = new JSONStringer().object().key("action")
						.value("clear").key("user_request").value(UserStr)
						.endObject();

				// send json to web server
				req2.getInternetData(json5, getString(R.string.DatabaseUrl)
						+ "/UpdateScoring");
			} catch (Exception e) {
				e.printStackTrace();
			}

			getUserDetails();

		}

	}

	private void init() {
		mText = (TextView) findViewById(R.id.txt);
		tvNoReports = (TextView) findViewById(R.id.tvNoReports);
		rank = (TextView) findViewById(R.id.rank);
		tvNextRank = (TextView) findViewById(R.id.tvNextRank);
		mUserPic = (ImageView) findViewById(R.id.user_pic);

		tvNotification = (TextView) findViewById(R.id.tvNotification);

		btnNext = (Button) findViewById(R.id.btnNext);
		btnPrev = (Button) findViewById(R.id.btnPrev);
		btnClear = (Button) findViewById(R.id.btnClear);

		btnNext.setOnClickListener(new onNextPrevClick(1));
		btnPrev.setOnClickListener(new onNextPrevClick(-1));
		btnClear.setOnClickListener(new onClearClick());

		tvLastReports = (TextView) findViewById(R.id.tvLastReports);
		tvLastHazards = (TextView) findViewById(R.id.tvLastHazards);

		pb = (ProgressBar) findViewById(R.id.progressbar);
		total_score = (TextView) findViewById(R.id.tv_score);

		rank.setText("");
		tvNextRank.setText("");
		mUserPic.setImageBitmap(null);
		total_score.setText("");

		// START MENU BUTTON
		exitButton = (ImageButton) findViewById(R.id.exitButton);
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
					lstReports = gson2.fromJson(str, LastReports.class);

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



	/*
	 * public View makeView() { TextView t = new TextView(this);
	 * t.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL);
	 * t.setTextSize(70); t.setTextColor(Color.RED); return t; }
	 */
}
