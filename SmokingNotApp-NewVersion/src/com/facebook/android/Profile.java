package com.facebook.android;

//import java.io.IOException;

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
import android.widget.ProgressBar;
import android.widget.TextView;

public class Profile extends Activity implements View.OnClickListener {
	/** Called when the activity is first created. */

	private TextView tvReport, tvPlaces, tvProfile;
	private Button exitButton;
	private TextView mText;
	private TextView rank;
	private ImageView mUserPic;
	 private ProgressBar pb;
	 private TextView total_score;

	 

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
		
		
        // get user info from server
		Gson gson2 = new Gson();
    	WebRequest req=new WebRequest();
    	String str=null;
    	UserRequest ur_updated=null;
        try {
			JSONObject json2=req.readJsonFromUrl(getString(R.string.DatabaseUrl) + "/GetUser?mail="+FacebookMain.email);
			str=(String)json2.get("user_req");
			Log.w("str=",str);
			ur_updated=gson2.fromJson(str, UserRequest.class);
        	}catch (JSONException e) {
					Log.e("Profile error, can't get response from server, JSON exception",e.toString());
					Log.w("str=",str);
				}
		    catch (Exception e) {
			Log.e("Profile error, can't get response from server",e.toString());
			Log.w("str=",str);
		}
        
		
		 //user's progress
        pb=(ProgressBar) findViewById(R.id.progressbar);
        total_score=(TextView)findViewById(R.id.tv_score);

              
        pb.setProgress(ur_updated.GetScore());
        total_score.setText(ur_updated.GetScore()+"/100");
        
        //display current stage
        if ((ur_updated.GetScore()>=0) && (ur_updated.GetScore() <45))
        	rank.setText("Beginner");
        if ((ur_updated.GetScore()>=45) && (ur_updated.GetScore() <135))
        	rank.setText("Active");
        if ((ur_updated.GetScore()>=135) && (ur_updated.GetScore() <270))
        	rank.setText("Advanced"); 	
        if ((ur_updated.GetScore()>=270) && (ur_updated.GetScore() <405))
        	rank.setText("Expert");
        if (ur_updated.GetScore()>=405)
        	rank.setText("Supervisor");
        
		// PROFILE INFORMATION
		mText.setText("Welcome " + FacebookMain.name);
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
		rank = (TextView) findViewById(R.id.rank);
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
