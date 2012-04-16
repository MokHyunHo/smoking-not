package com.facebook.android;

//import java.io.IOException;

import com.facebook.android.R;
import com.facebook.android.R.id;
import com.facebook.android.R.layout;
import com.facebook.android.FacebookMain;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Profile extends Activity implements View.OnClickListener {
	/** Called when the activity is first created. */

	private TextView tvReport, tvPlaces, tvProfile;
	private Button exitButton;
	private TextView mText;
	private TextView email;
	private ImageView mUserPic;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// connection between XML & JAVA

		// initialization of all the objects
		setContentView(R.layout.profile);
		init();

		// Top Menu and switching between activities
		tvReport.setOnClickListener(this);
		tvPlaces.setOnClickListener(this);
		tvProfile.setOnClickListener(this);

		// PROFILE INFORMATION
		mText.setText("Welcome " + FacebookMain.name);
		email.setText("Email: " + FacebookMain.email);
		mUserPic.setImageBitmap(Utility.getBitmap(FacebookMain.picURL));

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

	private void init() {
		tvReport = (TextView) findViewById(R.id.tvProReport);
		tvPlaces = (TextView) findViewById(R.id.tvProPlaces);
		tvProfile = (TextView) findViewById(R.id.tvProProfile);
		mText = (TextView) findViewById(R.id.txt);
		email = (TextView) findViewById(R.id.email);
		mUserPic = (ImageView) findViewById(R.id.user_pic);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent myIntent;
		switch (v.getId()) {
		case R.id.tvProReport:
			/*tvReport.setBackgroundResource(R.drawable.orange);
			tvReport.setBackgroundColor(android.R.color.black);*/
            myIntent = new Intent(getApplicationContext(), Report.class);
            if (Utility.mFacebook.isSessionValid()) {
                Utility.objectID = "me";
                startActivity(myIntent);
            }
			break;
		case R.id.tvProPlaces:
			/*tvPlaces.setBackgroundResource(R.drawable.orange);*/
            myIntent = new Intent(getApplicationContext(), Places.class);
            if (Utility.mFacebook.isSessionValid()) {
                Utility.objectID = "me";
                startActivity(myIntent);
            }
			break;
		case R.id.tvProProfile:
			/*tvProfile.setBackgroundResource(R.drawable.orange);*/
			break;

		}
	}
}
