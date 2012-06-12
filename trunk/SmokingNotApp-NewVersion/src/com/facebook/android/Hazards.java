package com.facebook.android;

import com.facebook.android.FacebookMain;
import com.google.gson.Gson;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONStringer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class Hazards extends Activity implements View.OnClickListener {

	String location, points;
	Button report;
	ImageButton ib;
	ImageView iv;
	EditText et1, comments;
	CheckBox c1;
	Intent i, profileIntent;
	final static int iData = 0;
	final static int iLocation = 1;
	Bitmap bmp;
	private Button exitButton;
	private ProgressDialog mProgress;
	private View tmpView;
	private int isMain=0;

	private Location chosenLocation = null;
	private String chosenAddress = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// initialization of all the objects
		setContentView(R.layout.hazards);
		Init();

		et1.setOnClickListener(this);

		report.setOnClickListener(this);
		ib.setOnClickListener(this);
		InputStream is = getResources().openRawResource(R.drawable.imageplace);
		bmp = BitmapFactory.decodeStream(is);

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

	private void Init() {
		report = (Button) findViewById(R.id.bReport);
		ib = (ImageButton) findViewById(R.id.ibReport);
		iv = (ImageView) findViewById(R.id.ivReport);
		et1 = (EditText) findViewById(R.id.etLocation);
		comments = (EditText) findViewById(R.id.comments);
		c1 = (CheckBox) findViewById(R.id.checkBox1);
		mProgress = new ProgressDialog(this);
		mProgress.setCancelable(false);
	}

	@Override
	public void onClick(View v) {
		Intent myIntent;
		switch (v.getId()) {
		case R.id.ibReport:
			i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(i, iData);
			break;

		case R.id.etLocation:
			myIntent = new Intent(getApplicationContext(),
					ChooseHazardLocation.class);
			startActivityForResult(myIntent, iLocation);
			break;
			
		case R.id.bhReport:
			mProgress.setMessage("Sending report...");
			mProgress.show();
			report.setEnabled(false);
			tmpView = v;
			new Thread() {
				public void run() {
					Looper.prepare();
					SimpleDateFormat s = new SimpleDateFormat(
							"dd/MM/yyyy hh:mm:ss");
					String date = s.format(new Date());
					String comments_str = comments.getText().toString();
					UserRequest ur = new UserRequest(FacebookMain.email, 1,
							null, date);
					HazardRequest hr = new HazardRequest(FacebookMain.email,
							date, chosenAddress, chosenLocation.getLatitude(),
							chosenLocation.getLongitude(), comments_str);
					WebRequest req = new WebRequest();

					/* Send UserRequest to Database */

					// convert UserRequest request to gson string
					Gson gson2 = new Gson();
					String UserStr = gson2.toJson(ur);
					JSONStringer json2 = null;

					// prepare Json
					try {

						json2 = new JSONStringer().object().key("action")
								.value("update_ur").key("user_request")
								.value(UserStr).endObject();

					} catch (JSONException e) {
						Log.e("json exeption-can't create jsonstringer with user request",
								e.toString());
					}

					// send json to web server
					try {
						req.getInternetData(json2,
								getString(R.string.DatabaseUrl)
										+ "/UpdateHazard");
					} catch (Exception e) {
						Log.w("couldn't send user request to servlet",
								e.toString());
					}

					/* Send HazardRequest to Database */

					// convert location request to gson string
					Gson gson1 = new Gson();
					String LocationStr = gson1.toJson(hr);
					JSONStringer json1 = null;

					// prepare Json
					try {
						json1 = new JSONStringer().object().key("action")
								.value("update_hazard").key("hazard_request")
								.value(LocationStr).endObject();

					} catch (JSONException e) {
						Log.e("json exeption-can't create jsonstringer with hazard",
								e.toString());
					}

					// send json to web server
					try {
						req.getInternetData(json1,
								getString(R.string.DatabaseUrl)
										+ "/UpdateHazard");
					} catch (Exception e) {
						Log.w("couldn't send hazard to servlet", e.toString());
					}

					if (c1.isChecked()) {
						String msg = MSG
								+ "\nReported a hazard at address "
								+ chosenAddress
								+ (comments_str.compareTo("") == 0 ? "\n "
										+ comments_str : "");
						PostStatusToFeed(msg);
					}
					mHandler.sendMessage(mHandler.obtainMessage(0));

				}

			}.start();
			
			Intent repIntent = new Intent(Hazards.this,
					OfficialReport.class);
			Bundle returnBundle = new Bundle();
			returnBundle.putString("StrLocation", location);
			repIntent.putExtras(returnBundle);
			repIntent.putExtra("BitmapImage", bmp);
			startActivity(repIntent);
			
			break;

		}

	}


	public void onPause()
	{
			isMain=0;
			super.onPause();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			super.onActivityResult(requestCode, resultCode, data);
			if (resultCode == RESULT_OK) {
				Bundle extras = data.getExtras();
				switch (requestCode) {
				case iData:
					bmp = (Bitmap) extras.get("data");
					iv.setImageBitmap(bmp);
					break;
				case iLocation:

					chosenLocation = extras.getParcelable("location");
					chosenAddress = extras.getString("address");

					et1.setText(chosenAddress);
					break;
				}
			}
		} catch (Throwable Ex) {
			Log.i("ERIC", "msg: " + Ex.toString());
		}
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

	private void showDialog(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
		builder.setTitle("Your Hazard report Has Been Sent!");
		builder.setMessage("Please check out your profile.");
		builder.setCancelable(true);

		final AlertDialog dlg = builder.create();
		dlg.show();
		final Timer t = new Timer();
		t.schedule(new TimerTask() {
			public void run() {
				dlg.dismiss(); // when the task active then close the dialog
				t.cancel(); // also just top the timer thread, otherwise, you
							// may receive a crash report
			}
		}, 2000); // after 2 second (or 2000 miliseconds), the task will be
					// active

	}

	private void showConflict(View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
		builder.setTitle("Error");
		builder.setMessage("A report about this place has already been accepted by you");
		builder.setCancelable(true);

		final AlertDialog dlg = builder.create();
		dlg.show();
		final Timer t = new Timer();
		t.schedule(new TimerTask() {
			public void run() {
				dlg.dismiss(); // when the task active then close the dialog
				t.cancel(); // also just top the timer thread, otherwise, you
							// may receive a crash report
			}
		}, 2000); // after 2 second (or 2000 miliseconds), the task will be
					// active

	}

	// was added
	// --------------- Post to Wall-----------------------
	public static final String imageURL = "https://lh4.googleusercontent.com/Z0JYXrl0MWPiyutFRTP5CONfIJoNi_-E52SDJAnKnoS9gi1kVPlcBNseDL87ykr54Ew5u_AEd00";
	// "http://www.facebookmobileweb.com/hackbook/img/facebook_icon_large.png";
	public static final String linkURL = "http://smokingnot2012.appspot.com";

	private static final String MSG = "Hazard report!";

	private final Handler msgPoster = new Handler();
	final Runnable mChoosePlaceNotification = new Runnable() {
		public void run() {
			Toast.makeText(getBaseContext(), "You have to choose a place !",
					Toast.LENGTH_LONG).show();
		}
	};

	// posts a string on users wall
	public void PostStatusToFeed(String msg)

	{
		Log.d("Tests", "Testing graph API wall post");
		try {
			String response = Utility.mFacebook.request("me");
			Bundle parameters = new Bundle();
			parameters.putString("message", msg);

			parameters.putString("name", "Smoking Not App!");
			parameters.putString("caption", " ");

			parameters.putString("description", "Gained " + points
					+ " points for the hazard report");

			parameters.putString("picture", imageURL);
			parameters.putString("link", linkURL);

			response = Utility.mFacebook
					.request("/me/feed", parameters, "POST");

			Log.d("Tests", "got response: " + response);
			if (response == null || response.equals("")
					|| response.equals("false")) {
				Log.v("Error", "Blank response");
			}
		} catch (Exception e) {
			Log.v("Error", e.toString());
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.i("ERIC", "what: " + msg.what);
			switch (msg.what) {
			case 0:
				report.setEnabled(true);
				mProgress.dismiss();
				showDialog(tmpView);
				break;
			}

		}

	};
}