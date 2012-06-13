package com.facebook.android;

import com.facebook.android.FacebookMain;
import com.google.gson.Gson;

import android.content.Context;
import android.content.DialogInterface;
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

import java.io.ByteArrayOutputStream;
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
import android.widget.RadioGroup;
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
	final static int iEmail = 2;
	Bitmap bmp;
	private Button exitButton;
	private ProgressDialog mProgress;
	private View tmpView;
	private int isMain = 0;
	// added---------------------------------------------------------------------
	private Button mQuestionButton;
	// private View tmpView;

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

		// added---------------------------------------------------------------------
		mQuestionButton = (Button) findViewById(R.id.question);

		mQuestionButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				tmpView = v;
				showQuestionDialog(tmpView);

			}
		});

		// START MENU BUTTON
		exitButton = (Button) findViewById(R.id.exitButton);
		exitButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		});

	}

	private void Init() {
		report = (Button) findViewById(R.id.bhReport);
		ib = (ImageButton) findViewById(R.id.ibhReport);
		iv = (ImageView) findViewById(R.id.ivReport);
		et1 = (EditText) findViewById(R.id.etLocation);
		comments = (EditText) findViewById(R.id.comments);
		c1 = (CheckBox) findViewById(R.id.hCB);
		mProgress = new ProgressDialog(this);
		mProgress.setCancelable(false);
	}

	@Override
	public void onClick(View v) {
		Intent myIntent;
		switch (v.getId()) {
		case R.id.ibhReport:
			i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(i, iData);
			break;

		case R.id.etLocation:
			myIntent = new Intent(getApplicationContext(),
					ChooseHazardLocation.class);
			startActivityForResult(myIntent, iLocation);
			break;

		case R.id.bhReport:

			if (et1.getText().toString().compareTo("<<< Choose location >>>") == 0) {
				msgPoster.post(mChoosePlaceNotification);
				break;
			}

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
						StringBuilder msg = new StringBuilder(MSG);

						msg.append("\nReported a hazard at address "
								+ chosenAddress);
						if (comments_str.compareTo("") != 0)
							msg.append("\n ").append(comments_str);
						
						Log.i("Eric hzrd", "c: " + comments_str + ", msg: "
								+ msg);
						PostStatusToFeed(msg.toString());
					}

					Intent repIntent = new Intent(Hazards.this,
							OfficialReport.class);
					startActivityForResult(repIntent, iEmail);

				}

			}.start();
			/*
			 * Intent repIntent = new Intent(Hazards.this,
			 * OfficialReport.class); Bundle returnBundle = new Bundle();
			 * returnBundle.putString("StrLocation", location);
			 * repIntent.putExtras(returnBundle);
			 * repIntent.putExtra("BitmapImage", bmp); startActivity(repIntent);
			 */
			break;

		}

	}

	public void onPause() {
		isMain = 0;
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
				case iEmail:
					String orname = extras.getString("Name");
					String orphone = extras.getString("Phone");
					String oradd = extras.getString("Address");
					String oremail = extras.getString("Email");
					String comment = comments.getText().toString();
					String loc = et1.getText().toString();

					EmailDetails ed = new EmailDetails(orname, orphone, oradd,
							oremail, null, loc, comment);

					if (bmp != null) {
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						bmp.compress(Bitmap.CompressFormat.PNG, 100, bos);
						byte[] barr = bos.toByteArray();
						ed.setPicture(barr);
					}

					WebRequest req = new WebRequest();

					Gson gson2 = new Gson();
					String EmailStr = gson2.toJson(ed);
					JSONStringer json2 = null;

					// prepare Json
					try {
						json2 = new JSONStringer().object().key("action")
								.value("send_hazardemail").key("hazard_email")
								.value(EmailStr).endObject();

					} catch (JSONException e) {
						Log.e("json exeption-can't create jsonstringer with email",
								e.toString());
					}

					// send json to web server

					try {
						req.getInternetData(json2,
								getString(R.string.DatabaseUrl)
										+ "/EmailReport");
					} catch (Exception e) {
						Log.w("couldn't send email to servlet", e.toString());
					}

					mHandler.sendMessage(mHandler.obtainMessage(0));
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
	public static final String imageURL = "http://i47.tinypic.com/29peplz.png";
	// "http://www.facebookmobileweb.com/hackbook/img/facebook_icon_large.png";
	public static final String linkURL = "http://smokingnot2012.appspot.com";

	private static final String MSG = "Hazard report!";

	private final Handler msgPoster = new Handler();
	final Runnable mChoosePlaceNotification = new Runnable() {
		public void run() {
			Toast.makeText(getBaseContext(), "You have to choose a location!",
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

			parameters.putString("name", "The Reporter!");
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

	private void clearForm() {
		Bitmap bmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.imageplace);
		iv.setImageBitmap(bmp);

		et1.setText("<<< Choose location >>>");
		comments.setText("");
		c1.setSelected(true);

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
				clearForm();
				break;
			}

		}

	};

	// added---------------------------------------------------------------------
	private void showQuestionDialog(View v) {
		AlertDialog alertDialog = new AlertDialog.Builder(v.getContext())
				.create();
		alertDialog.setTitle("Report safety hazards");
		String str = "- By selecting the Hazards option, you can add a photo of the hazard, the location of the hazard and a free text describing it.\n"
				+ "- The report will be sent to the municipal service center and will be taken care as soon as possible.\n";
		alertDialog.setMessage(str);

		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// here you can add functions
			}
		});
		alertDialog.setIcon(R.drawable.qm);
		alertDialog.show();
	}
}
