package com.facebook.android;

//import java.io.IOException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

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
	private TextView email;
	private ImageView mUserPic;
	private ImageView ratingbar1,ratingbar2,ratingbar3,ratingbar4,ratingbar5,ratingbarfull;
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
        ratingbar1=(ImageView)findViewById(R.id.iv_ratingbar1);
        ratingbar2=(ImageView)findViewById(R.id.iv_ratingbar2);
        ratingbar3=(ImageView)findViewById(R.id.iv_ratingbar3);
        ratingbar4=(ImageView)findViewById(R.id.iv_ratingbar4);
        ratingbar5=(ImageView)findViewById(R.id.iv_ratingbar5);
        ratingbarfull=(ImageView)findViewById(R.id.iv_ratingbarfull);
        
      
        pb.setProgress(ur_updated.GetScore());
        total_score.setText(ur_updated.GetScore()+"/100");
        ratingbar1.setVisibility(View.INVISIBLE);
    	ratingbar2.setVisibility(View.INVISIBLE);
    	ratingbar3.setVisibility(View.INVISIBLE);
    	ratingbar4.setVisibility(View.INVISIBLE);
    	ratingbar5.setVisibility(View.INVISIBLE);
    	ratingbarfull.setVisibility(View.INVISIBLE);
        
        //display current stage
        if ((ur_updated.GetScore()>=0) && (ur_updated.GetScore() <15))
        	ratingbar1.setVisibility(View.VISIBLE);
        if ((ur_updated.GetScore()>=15) && (ur_updated.GetScore() <45))
        	ratingbar2.setVisibility(View.VISIBLE);
        if ((ur_updated.GetScore()>=45) && (ur_updated.GetScore() <135))
        	ratingbar3.setVisibility(View.VISIBLE); 	
        if ((ur_updated.GetScore()>=135) && (ur_updated.GetScore() <405))
        	ratingbar4.setVisibility(View.VISIBLE);
        if ((ur_updated.GetScore()>=405) && (ur_updated.GetScore() <1215))
        	ratingbar5.setVisibility(View.VISIBLE);
        if (ur_updated.GetScore()>=1215) 
        	ratingbarfull.setVisibility(View.VISIBLE);
        
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
