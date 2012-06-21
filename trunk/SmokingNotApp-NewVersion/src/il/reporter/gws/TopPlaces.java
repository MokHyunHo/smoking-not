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

public class TopPlaces extends Activity implements View.OnClickListener {

	private TextView tvReport, tvPlaces, tvTopTen, tvCaption;

	//Elad - to replace textviews with whatever you wanted
	private TextView tvBest, tvWorst;
	
	
	private ListView lvPlaces;
	private ProgressBar pbLoading;
	private TenPlaces mTenPlaces;
	private List<LocationRequest> lstPlaces;
	private TopPlacesAdapter mAdapter;

	private class onBestClick implements OnClickListener {
		public void onClick(View v) {
			tvCaption.setText("Best 10 Places");			
			lvPlaces.setAdapter(null);
			pbLoading.setVisibility(View.VISIBLE);
			loadList("/GetTenTopPlaces?action=top_ten");
		}

	}
	private class onWorstClick implements OnClickListener {
		public void onClick(View v) {
			tvCaption.setText("Worst 10 Places");
			lvPlaces.setAdapter(null);
			pbLoading.setVisibility(View.VISIBLE);
			loadList("/GetTenTopPlaces?action=bad_top_ten");
		}

	}

	private void init() {
		tvReport = (TextView) findViewById(R.id.tvPlaReport);
		tvPlaces = (TextView) findViewById(R.id.tvPlaPlaces);
		tvTopTen = (TextView) findViewById(R.id.tvTopTen);
		tvCaption = (TextView) findViewById(R.id.tvCaption);
		
		//Elad - to replace textviews with whatever you wanted
		tvBest = (TextView) findViewById(R.id.tvBest);
		tvWorst = (TextView) findViewById(R.id.tvWorst);
				

		lvPlaces = (ListView) findViewById(R.id.lstTopPlaces);
		pbLoading = (ProgressBar) findViewById(R.id.pbLoading);

		tvReport.setOnClickListener(this);
		tvPlaces.setOnClickListener(this);
		tvTopTen.setOnClickListener(this);
		
		tvBest.setOnClickListener(new onBestClick());
		tvWorst.setOnClickListener(new onWorstClick());
		
		
		
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.top_places);

		init();

		mAdapter = new TopPlacesAdapter(this);
		
		
		tvBest.performClick();

	}
	

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}
	
		

	private void loadList(final String db_request) {
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

					lstPlaces = mTenPlaces.getTenPlaces();

				} catch (Exception e) {
					e.printStackTrace();
				}

				mHandler.sendMessage(mHandler.obtainMessage(0));

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
					if (lstPlaces.size() > 0) {
						mAdapter.setData(lstPlaces);
						lvPlaces.setAdapter(mAdapter);
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
		case R.id.tvTopTen:
			break;

		}
	}

}
