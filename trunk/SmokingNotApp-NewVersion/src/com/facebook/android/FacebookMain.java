/*
 * Copyright 2004 - Present Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook.android;

import java.io.IOException;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.facebook.android.SessionEvents.AuthListener;
import com.facebook.android.SessionEvents.LogoutListener;

public class FacebookMain extends Activity {

	/*
	 * Your Facebook Application ID must be set before running this example See
	 * http://www.facebook.com/developers/createapp.php
	 */
	public static final String APP_ID = "306456662758902";

	private ImageView mUserPic;
	private LoginButton mLoginButton;
	private TextView mText;
	private Handler mHandler;
	ProgressDialog dialog;

	final static int AUTHORIZE_ACTIVITY_RESULT_CODE = 0;
	final static int PICK_EXISTING_PHOTO_RESULT_CODE = 1;

	public static String picURL;
	public static String name;
	public static String email;
	public static  int   fetching=0;

	private Button mProfileButton;
	private Button mReportButton;
	private Button mHazardsButton;
	Intent i;

	String[] permissions = { "publish_stream", "email", "user_photos" };

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		if (APP_ID == null) {
			Util.showAlert(this, "Warning", "Facebook Applicaton ID must be "
					+ "specified before running this example: see FbAPIs.java");
			return;
		}
		mHandler = new Handler();

		mText = (TextView) FacebookMain.this.findViewById(R.id.txt);
		mUserPic = (ImageView) FacebookMain.this.findViewById(R.id.user_pic);

		// Create the Facebook Object using the app id.
		Utility.mFacebook = new Facebook(APP_ID);
		// Instantiate the asynrunner object for asynchronous api calls.
		Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);

		mLoginButton = (LoginButton) findViewById(R.id.login);
		mProfileButton = (Button) findViewById(R.id.profileButton);
		mReportButton = (Button) findViewById(R.id.reportButton);
		mHazardsButton = (Button) findViewById(R.id.hazardsButton);

		// restore session if one exists
		SessionStore.restore(Utility.mFacebook, this);
		SessionEvents.addAuthListener(new FbAPIsAuthListener());
		SessionEvents.addLogoutListener(new FbAPIsLogoutListener());

		/*
		 * Source Tag: login_tag
		 */
		mLoginButton.init(this, AUTHORIZE_ACTIVITY_RESULT_CODE,
				Utility.mFacebook, permissions);

		if (Utility.mFacebook.isSessionValid()) {
			
			if (fetching==0)
			{
				fetching=requestUserData();
			}
			fetching=requestUserData();  //delete after it
			
		}

		mProfileButton.setOnClickListener(new OnClickListener() {
			
			
			public void onClick(View v) {
				
					Intent myIntent = new Intent(getApplicationContext(),
							Profile.class);
					if (!Utility.mFacebook.isSessionValid()) {
						mText.setText("Please login first!");
						mText.setTextColor(Color.BLUE);

					}
					if (Utility.mFacebook.isSessionValid()) {
						if (fetching==1)
						{
							Utility.objectID = "me";
							startActivity(myIntent);
						}
			}}
		});
		

		mReportButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
					Intent myIntent = new Intent(getApplicationContext(),
							Report.class);
					if (!Utility.mFacebook.isSessionValid()) {
						mText.setText("Please login first!");
						mText.setTextColor(Color.BLUE);
					}
					if (Utility.mFacebook.isSessionValid()) {
						if (fetching==1)
						{
							Utility.objectID = "me";
							startActivity(myIntent);
						}
			}}
		});

		mHazardsButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
					Intent myIntent = new Intent(getApplicationContext(),
							Hazards.class);
					if (!Utility.mFacebook.isSessionValid()) {
						mText.setText("Please login first!");
						mText.setTextColor(Color.BLUE);
					}
					if (Utility.mFacebook.isSessionValid()) {
						if (fetching==1)
						{
							Utility.objectID = "me";
							startActivity(myIntent);
						}
			}}
		});

	}

	@Override
	public void onResume() {
		super.onResume();
		if (Utility.mFacebook != null) {
			if (!Utility.mFacebook.isSessionValid()) {
				mText.setText("You are logged out! ");
				mUserPic.setImageBitmap(null);
			} else {
				Utility.mFacebook.extendAccessTokenIfNeeded(this, null);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		/*
		 * if this is the activity result from authorization flow, do a call
		 * back to authorizeCallback Source Tag: login_tag
		 */
		case AUTHORIZE_ACTIVITY_RESULT_CODE: {
			Utility.mFacebook.authorizeCallback(requestCode, resultCode, data);
			break;
		}
			/*
			 * if this is the result for a photo picker from the gallery, upload
			 * the image after scaling it. You can use the Utility.scaleImage()
			 * function for scaling
			 */
		case PICK_EXISTING_PHOTO_RESULT_CODE: {
			if (resultCode == Activity.RESULT_OK) {
				Uri photoUri = data.getData();
				if (photoUri != null) {
					Bundle params = new Bundle();
					try {
						params.putByteArray("photo", Utility.scaleImage(
								getApplicationContext(), photoUri));
					} catch (IOException e) {
						e.printStackTrace();
					}
					params.putString("caption",
							"FbAPIs Sample App photo upload");
					Utility.mAsyncRunner.request("me/photos", params, "POST",
							new PhotoUploadListener(), null);
				} else {
					Toast.makeText(getApplicationContext(),
							"Error selecting image from the gallery.",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(getApplicationContext(),
						"No image selected for upload.", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		}
		}
	}
	
	public void onBackPressed()
	{
		finish();
	}
	/*
	 * callback for the photo upload
	 */
	public class PhotoUploadListener extends BaseRequestListener {

		@Override
		public void onComplete(final String response, final Object state) {
			dialog.dismiss();
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					new UploadPhotoResultDialog(FacebookMain.this,
							"Upload Photo executed", response).show();
				}
			});
		}

		public void onFacebookError(FacebookError error) {
			dialog.dismiss();
			Toast.makeText(getApplicationContext(),
					"Facebook Error: " + error.getMessage(), Toast.LENGTH_LONG)
					.show();
		}
	}

	/*
	 * Callback for fetching current user's name, picture, uid.
	 */
	public class UserRequestListener extends BaseRequestListener {

		@Override
		public void onComplete(final String response, final Object state) {
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(response);

				picURL = jsonObject.getString("picture");
				name = jsonObject.getString("name");
				email = jsonObject.getString("email");
				Utility.userUID = jsonObject.getString("id");

				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mText.setText("Welcome " + name + "!");
						picURL = "https://graph.facebook.com/"
								+ Utility.userUID + "/picture?type=normal";
						mUserPic.setImageBitmap(Utility.getBitmap(picURL));

					}
				});

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/*
	 * The Callback for notifying the application when authorization succeeds or
	 * fails.
	 */

	public class FbAPIsAuthListener implements AuthListener {

		@Override
		public void onAuthSucceed() {
			if(fetching==0)
			{
				fetching= requestUserData();
			}
			fetching=requestUserData();  //delete after it
		}

		@Override
		public void onAuthFail(String error) {
			mText.setText("Login Failed: " + error);
		}
	}

	/*
	 * The Callback for notifying the application when log out starts and
	 * finishes.
	 */
	public class FbAPIsLogoutListener implements LogoutListener {
		@Override
		public void onLogoutBegin() {
			mText.setText("Logging out...");
		}

		@Override
		public void onLogoutFinish() {
			mText.setText("You have logged out! ");
			mUserPic.setImageBitmap(null);

		}
	}

	/*
	 * Request user name, and picture to show on the main screen.
	 */
	public int requestUserData() {
		mText.setText("Fetching user name, profile pic...");
		Bundle params = new Bundle();
		params.putString("fields", "name, picture, email");
		Utility.mAsyncRunner.request("me", params, new UserRequestListener());
		return 1;
	}

	private boolean haveNetworkConnection() {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;
	}
}
