package il.reporter.gws;

import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.R;
import com.google.gson.Gson;

public class Stats extends Activity implements View.OnClickListener {

	private TextView tvReport, tvPlaces, tvStats, tvCaption;

	//Elad - to replace textviews with whatever you wanted
	private TextView tvBest, tvWorst, tvLast;
	
	
	private ListView lvPlaces;
	private ProgressBar pbLoading;
	private TenPlaces mTenPlaces;
	private LastReports mLastReports;
	private TopListAdapter mTopAdapter;
	private LastReportsAdapter mReportsAdapter;

	private class onBestClick implements OnClickListener {
		public void onClick(View v) {
			tvCaption.setText("Best 10 Places");			
			lvPlaces.setAdapter(null);
			pbLoading.setVisibility(View.VISIBLE);
			loadTopList("/GetTenTopPlaces?action=top_ten");
		}

	}
	private class onWorstClick implements OnClickListener {
		public void onClick(View v) {
			tvCaption.setText("Worst 10 Places");
			lvPlaces.setAdapter(null);
			pbLoading.setVisibility(View.VISIBLE);
			loadTopList("/GetTenTopPlaces?action=bad_top_ten");
		}

	}
	
	private class onLastClick implements OnClickListener {
		public void onClick(View v) {
			tvCaption.setText("Last 10 reports");
			lvPlaces.setAdapter(null);
			pbLoading.setVisibility(View.VISIBLE);
			loadLastList("/GetLastReports");
		}

	}

	private void init() {
		tvReport = (TextView) findViewById(R.id.tvPlaReport);
		tvPlaces = (TextView) findViewById(R.id.tvPlaPlaces);
		tvStats = (TextView) findViewById(R.id.tvStats);
		tvCaption = (TextView) findViewById(R.id.tvCaption);
		
		//Elad - to replace textviews with whatever you wanted
		tvBest = (TextView) findViewById(R.id.tvBest);
		tvWorst = (TextView) findViewById(R.id.tvWorst);
		tvLast = (TextView) findViewById(R.id.tvLast);
				

		lvPlaces = (ListView) findViewById(R.id.lstTopPlaces);
		pbLoading = (ProgressBar) findViewById(R.id.pbLoading);

		tvReport.setOnClickListener(this);
		tvPlaces.setOnClickListener(this);
		tvStats.setOnClickListener(this);
		
		tvBest.setOnClickListener(new onBestClick());
		tvWorst.setOnClickListener(new onWorstClick());
		tvLast.setOnClickListener(new onLastClick());
	
		
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.stats);

		init();

		mTopAdapter = new TopListAdapter(this);
		mReportsAdapter = new LastReportsAdapter(this, true);
		
		
		tvBest.performClick();

	}
	

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}
	
		

	private void loadTopList(final String db_request) {
		new Thread() {
			public void run() {

				Gson gson2 = new Gson();
				WebRequest req = new WebRequest();
				String str = null;

				try {
					JSONObject json2 = req
							.readJsonFromUrl(getString(R.string.DatabaseUrl)
									+ db_request);
					str = (String) json2.get("Top_Ten");
					mTenPlaces = gson2.fromJson(str, TenPlaces.class);

				} catch (Exception e) {
					e.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(0));

			}

		}.start();
	}
	
	private void loadLastList(final String db_request) {
		new Thread() {
			public void run() {

				Gson gson2 = new Gson();
				WebRequest req = new WebRequest();
				String str = null;

				try {
					JSONObject json2 = req
							.readJsonFromUrl(getString(R.string.DatabaseUrl)
									+ db_request);
					str = (String) json2.get("report_request");
					mLastReports = gson2.fromJson(str, LastReports.class);

				} catch (Exception e) {
					e.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(1));

			}

		}.start();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pbLoading.setVisibility(View.INVISIBLE);
			try {
				switch (msg.what) {
				case 0:
					if (mTenPlaces.getTenPlaces().size() > 0) {
						mTopAdapter.setData(mTenPlaces);
						lvPlaces.setAdapter(mTopAdapter);
					}
					break;
				case 1:
					if (mLastReports.getLst().size() > 0) {
						mReportsAdapter.setData(mLastReports);
						lvPlaces.setAdapter(mReportsAdapter);
					}
					break;

				}
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(getApplicationContext(), "Error occured...",
						Toast.LENGTH_SHORT).show();
			}

		}

	};

	@Override
	public void onClick(View v) {

		Intent myIntent;
		switch (v.getId()) {
		case R.id.tvPlaReport:
			myIntent = new Intent(getApplicationContext(), Report.class);
			if (Utility.mFacebook.isSessionValid()) {
				Utility.objectID = "me";
				startActivity(myIntent);
			}
			break;
		case R.id.tvPlaPlaces:
			myIntent = new Intent(getApplicationContext(), Places.class);
			if (Utility.mFacebook.isSessionValid()) {
				Utility.objectID = "me";
				startActivity(myIntent);
			}
			break;
		case R.id.tvStats:
			break;

		}
	}

}
