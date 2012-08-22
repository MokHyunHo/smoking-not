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

package il.reporter.gws;

import il.reporter.gws.SessionEvents.AuthListener;

import il.reporter.gws.SessionEvents.LogoutListener;

import java.io.IOException;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.R;
import com.facebook.android.Util;

public class FacebookMain extends Activity {

	/*
	 * Your Facebook Application ID must be set before running this example See
	 * http://www.facebook.com/developers/createapp.php
	 */
	public static final String APP_ID = "306456662758902";
	public static int LAST_ALLOWED_RADIUS = GooglePlacesAPI.ALLOWED_RADIUS;

	private ImageView mUserPic;
	private LoginButton mLoginButton;
	private TextView mText;
	private TextView mText2;
	private ProgressBar mPb;
	private Handler mHandler;
	private RelativeLayout mRL;
	ProgressDialog dialog;

	final static int AUTHORIZE_ACTIVITY_RESULT_CODE = 0;
	final static int PICK_EXISTING_PHOTO_RESULT_CODE = 1;

	public static String picURL;
	public static String name;
	public static String email;
	public static int fetching = 0;

	private Button mProfileButton;
	private Button mReportButton;
	private Button mHazardsButton;
	// added---------------------------------------------------------------------
	private ImageButton mQuestionButton;
	private View tmpView;
	Intent i;

	String[] permissions = { "publish_stream", "email", "user_photos" };

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);

		LAST_ALLOWED_RADIUS = GooglePlacesAPI.ALLOWED_RADIUS;

		if (APP_ID == null) {
			Util.showAlert(this, "Warning", "Facebook Applicaton ID must be "
					+ "specified before running this example: see FbAPIs.java");
			return;
		}
		mHandler = new Handler();

		mText = (TextView) FacebookMain.this.findViewById(R.id.txt);
		mText2 = (TextView) FacebookMain.this.findViewById(R.id.txt2);
		mUserPic = (ImageView) FacebookMain.this.findViewById(R.id.user_pic);
		mPb = (ProgressBar) FacebookMain.this.findViewById(R.id.progressBar1);
		mRL = (RelativeLayout) FacebookMain.this
				.findViewById(R.id.MainSecondRL);

		// Create the Facebook Object using the app id.
		Utility.mFacebook = new Facebook(APP_ID);
		// Instantiate the asynrunner object for asynchronous api calls.
		Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);

		mLoginButton = (LoginButton) findViewById(R.id.login);
		mProfileButton = (Button) findViewById(R.id.profileButton);
		mReportButton = (Button) findViewById(R.id.reportButton);
		mHazardsButton = (Button) findViewById(R.id.hazardsButton);
		// added---------------------------------------------------------------------
		mQuestionButton = (ImageButton) findViewById(R.id.question);

		// restore session if one exists
		SessionStore.restore(Utility.mFacebook, this);
		SessionEvents.addAuthListener(new FbAPIsAuthListener());
		SessionEvents.addLogoutListener(new FbAPIsLogoutListener());

		/*
		 * Source Tag: login_tag
		 */
		mLoginButton.init(this, AUTHORIZE_ACTIVITY_RESULT_CODE,
				Utility.mFacebook, permissions);
		if (Utility.mFacebook.isSessionValid())
			;
			//SwitchVisibility(true, false);
		else
			SwitchVisibility(false, false);

		if (Utility.mFacebook.isSessionValid()) {

			/**
			 * if (fetching==0) { fetching=requestUserData(); }
			 **/
			fetching = requestUserData(); // delete after it

			/**
			 * if (fetching==1) { WebRequest req = new WebRequest();
			 * 
			 * try { req.readJsonFromUrl(getString(R.string.DatabaseUrl)+
			 * "/CreateNewUser?mail=" + email); } catch(Exception e) {
			 * Log.w("Ortal", "can't send user email to database"); } }
			 **/
		}

		mProfileButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				Intent myIntent = new Intent(getApplicationContext(),
						Profile.class);
				if (!Utility.mFacebook.isSessionValid()) {
					mText.setText("Please login first!");
					mText.setTextColor(Color.YELLOW);

				}
				if (Utility.mFacebook.isSessionValid()) {
					if (fetching == 1) {
						Utility.objectID = "me";
						startActivity(myIntent);
					}
				}
			}
		});

		mReportButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				Intent myIntent = new Intent(getApplicationContext(),
						MainSmoking.class);
				if (!Utility.mFacebook.isSessionValid()) {
					mText.setText("Please login first!");
					mText.setTextColor(Color.YELLOW);
				}
				if (Utility.mFacebook.isSessionValid()) {
					if (fetching == 1) {
						Utility.objectID = "me";
						startActivity(myIntent);
					}
				}
			}
		});

		mHazardsButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				Intent myIntent = new Intent(getApplicationContext(),
						Hazards.class);
				if (!Utility.mFacebook.isSessionValid()) {
					mText.setText("Please login first!");
					mText.setTextColor(Color.YELLOW);
				}
				if (Utility.mFacebook.isSessionValid()) {
					if (fetching == 1) {
						Utility.objectID = "me";
						startActivity(myIntent);
					}
				}
			}
		});

		// added---------------------------------------------------------------------
		mQuestionButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				tmpView = v;
				showQuestionDialog(tmpView);

			}
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
		 * if this is the result for a photo picker from the gallery, upload the
		 * image after scaling it. You can use the Utility.scaleImage() function
		 * for scaling
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

	public void onBackPressed() {
		super.finish();
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

						mText.setTextColor(Color.WHITE);

						mText.setText("Welcome " + name + "!");
						// mText.setText(email); //test to show email

						picURL = "https://graph.facebook.com/"
								+ Utility.userUID + "/picture?type=normal";
						mUserPic.setImageBitmap(Utility.getBitmap(picURL));
						// ///////////////////////////////////////////////////////////
						WebRequest req = new WebRequest();

						try {
							req.readJsonFromUrl(getString(R.string.DatabaseUrl)
									+ "/CreateNewUser?mail=" + email);
						} catch (Exception e) {
							Log.w("Ortal", "can't send user email to database");
						}
						
						SwitchVisibility(true, false);
						// ///////////////////////////////////////////////////////////////////////////////
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
			/**
			 * if(fetching==0) { fetching= requestUserData(); }
			 **/

			fetching = requestUserData(); // delete after it

			/**
			 * if (fetching==1) { WebRequest req2 = new WebRequest();
			 * 
			 * try { req2.readJsonFromUrl(getString(R.string.DatabaseUrl)+
			 * "/CreateNewUser?mail=" + email); } catch(Exception e) {
			 * Log.w("Ortal", "can't send user email to database"); } }
			 **/
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
			SwitchVisibility(false, false);

		}
	}

	/*
	 * Request user name, and picture to show on the main screen.
	 */
	public int requestUserData() {
		SwitchVisibility(false, true);
		// mText.setText("Fetching user name, profile pic...");
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

	// added---------------------------------------------------------------------
	private void showQuestionDialog(View v) {
		AlertDialog alertDialog = new AlertDialog.Builder(v.getContext())
				.create();
		alertDialog.setTitle("The Report App!");
		String str = "The Reporter offers you the ultimate tool- a real time report. \n\n"
				+ "The application allows you to:\n"
				+ " 1.	Report places that are/aren't enforcing the rules concerning smoking. \n"
				+ " 2.	Watch a full list of places around a specific location. (Smoking)\n"
				+ " 3.	Report safety hazards.\n"
				+ " 4.	Manage a personal profile.\n";
		alertDialog.setMessage(str);

		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// here you can add functions
			}
		});
		alertDialog.setIcon(R.drawable.qm);
		alertDialog.show();
	}

	public void SwitchVisibility(boolean buttonsVisible, boolean progressVisible) {

		int mVis1, mVis2;
		mVis1 = (buttonsVisible ? View.VISIBLE : View.INVISIBLE);
		mVis2 = (progressVisible ? View.VISIBLE : View.INVISIBLE);

		mRL.setVisibility(mVis1);
		mText2.setVisibility(mVis2);
		mPb.setVisibility(mVis2);
	}
}

/**
 * AlertDialog alertDialog = new AlertDialog.Builder(this).create();
 * alertDialog.setTitle("Reset..."); alertDialog.setMessage("Are you sure?");
 * alertDialog.setButton("OK", new DialogInterface.OnClickListener() { public
 * void onClick(DialogInterface dialog, int which) { // here you can add
 * functions } }); alertDialog.setIcon(R.drawable.icon); alertDialog.show();
 **/
