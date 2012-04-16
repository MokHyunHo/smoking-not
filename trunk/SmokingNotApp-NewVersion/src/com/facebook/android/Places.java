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
//import android.widget.ImageView;
//import android.widget.TextView;
import android.widget.TextView;


public class Places extends Activity implements View.OnClickListener {
    /** Called when the activity is first created. */
	
	private TextView tvReport, tvPlaces, tvProfile;
	private Button exitButton;

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		// initialization of all the objects
        setContentView(R.layout.places);
		Init();
       
        //connection between XML & JAVA
   
       
       // first-up menu
		tvReport.setOnClickListener(this);
		tvPlaces.setOnClickListener(this);
		tvProfile.setOnClickListener(this);
        
        
        // START MENU BUTTON
        exitButton= (Button) findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				 Intent myIntent = new Intent(getApplicationContext(), FacebookMain.class);
	                if (Utility.mFacebook.isSessionValid()) {
	                    Utility.objectID = "me";
	                }
	                startActivity(myIntent);
				
			}
		});
			
   
    }


	private void Init() {
		tvReport=(TextView) findViewById(R.id.tvPlaReport);
		tvPlaces=(TextView) findViewById(R.id.tvPlaPlaces);
		tvProfile=(TextView) findViewById(R.id.tvPlaProfile);
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent myIntent;
		switch (v.getId()) {
		case R.id.tvPlaReport:
			/*tvReport.setBackgroundResource(R.drawable.orange);
			tvReport.setBackgroundColor(android.R.color.black);*/
            myIntent = new Intent(getApplicationContext(), Report.class);
            if (Utility.mFacebook.isSessionValid()) {
                Utility.objectID = "me";
                startActivity(myIntent);
            }
			break;
		case R.id.tvPlaPlaces:
			/*tvPlaces.setBackgroundResource(R.drawable.orange);
			tvPlaces.setBackgroundColor(android.R.color.black);*/
			break;
		case R.id.tvPlaProfile:
			/*tvProfile.setBackgroundResource(R.drawable.orange);
			tvProfile.setBackgroundColor(android.R.color.black);*/
            myIntent = new Intent(getApplicationContext(), Profile.class);
            if (Utility.mFacebook.isSessionValid()) {
                Utility.objectID = "me";
                startActivity(myIntent);
            }
			break;
		}
	}
}
