package com.facebook.android;

import java.io.IOException;
import com.facebook.android.FacebookMain;

import com.facebook.android.FacebookMain.PhotoUploadListener;

import android.app.Activity;
import android.content.Intent;



import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Profile extends Activity {
    /** Called when the activity is first created. */
	

	 private Button exitButton;
	 private TextView mText;
	 private TextView email;
	 private ImageView mUserPic;

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        
       
        //connection between XML & JAVA
   
       exitButton= (Button) findViewById(R.id.exitButton);
        
        mText = (TextView) findViewById(R.id.txt);
        email = (TextView) findViewById(R.id.email);
        mUserPic = (ImageView) findViewById(R.id.user_pic);
        
    
        mText.setText("Welcome " + FacebookMain.name );
        email.setText("Email: " + FacebookMain.email);
        mUserPic.setImageBitmap(Utility.getBitmap(FacebookMain.picURL));
        
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
