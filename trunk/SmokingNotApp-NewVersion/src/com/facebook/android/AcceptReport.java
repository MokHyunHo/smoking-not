package com.facebook.android;

import com.facebook.android.R;
import android.app.ActivityGroup;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class AcceptReport extends ActivityGroup {
	
	TextView tvlocation, tvway, tvreas1,tvreas2;
	String[] reason;
	String location,way;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accept_report);
		Init();
		Bundle SentData = getIntent().getExtras();
		reason = SentData.getStringArray("chosenReasons");
		location = SentData.getString("keyLocation");
		way = SentData.getString("ChosenWay");
		
		tvlocation.setText(location);
		tvway.setText(way);
		tvreas1.setText(reason[0]);
		tvreas2.setText(reason[1]);
	}
	
	private void Init() {
		tvlocation = (TextView) findViewById(R.id.tvRecivedLocation);
		tvway = (TextView) findViewById(R.id.tvRecivedWay);
		tvreas1 = (TextView) findViewById(R.id.tvRecivedReason1);
		tvreas2=(TextView) findViewById(R.id.tvRecivedReason2);
	}
}
