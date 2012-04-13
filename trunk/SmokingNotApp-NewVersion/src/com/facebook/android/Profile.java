package com.facebook.android;

//import java.io.IOException;



import com.facebook.android.FacebookMain;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;


public class Profile extends Activity   {
    /** Called when the activity is first created. */
	//just for checking
	private int score=40;
	
	
	private Button mProfileButton;
    private Button mReportButton;
    private Button mPlacesButton;
	
	 private Button exitButton;
	 private TextView mText;
	 private TextView email;
	 private ImageView mUserPic;
	 private ProgressBar pb;
	 private TextView total_score;
	 private RatingBar rb;
	 
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        
       
        //connection between XML & JAVA
   
       
       // first-up menu
       mProfileButton= (Button)findViewById(R.id.profileButton);  
       mReportButton= (Button) findViewById(R.id.reportButton);
       mPlacesButton= (Button)findViewById(R.id.placesButton);
        
      
       //PROFILE INFORMATION
        mText = (TextView) findViewById(R.id.txt);
        email = (TextView) findViewById(R.id.email);
        mUserPic = (ImageView) findViewById(R.id.user_pic);
        
      //user's progress
        pb=(ProgressBar) findViewById(R.id.progressbar);
        total_score=(TextView)findViewById(R.id.tv_score);
        //rb=(RatingBar)findViewById(R.id.ratingBar1);
        
        
        mText.setText("Welcome " + FacebookMain.name );
        email.setText("Email: " + FacebookMain.email);
        mUserPic.setImageBitmap(Utility.getBitmap(FacebookMain.picURL));
        
       
    
        pb.setProgress(score);
        total_score.setText(score+"/100");
        
        if(score % 20 ==0)
        	rb.setRating(score/20);

        
        //listeners for FIRST-UP MENU
        
        mProfileButton.setOnClickListener(new OnClickListener() {
    			public void onClick(View v) {
    	            Intent myIntent = new Intent(getApplicationContext(), Profile.class);
                    if (Utility.mFacebook.isSessionValid()) {
                        Utility.objectID = "me";
                        startActivity(myIntent);
    			}}
    		});
    			
    		
             mReportButton.setOnClickListener(new OnClickListener() {
    			public void onClick(View v) {
    				 Intent myIntent = new Intent(getApplicationContext(), Report.class);
    	                if (Utility.mFacebook.isSessionValid()) {
    	                    Utility.objectID = "me";
    	                    startActivity(myIntent);
    	                }}
    		});
            
             
             mPlacesButton.setOnClickListener(new OnClickListener() {
     			public void onClick(View v) {
     				 Intent myIntent = new Intent(getApplicationContext(), Places.class);
                    if (Utility.mFacebook.isSessionValid()) {
                        Utility.objectID = "me";
                        startActivity(myIntent);
     			}}
     		});
        
        
        
        
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
    

}
