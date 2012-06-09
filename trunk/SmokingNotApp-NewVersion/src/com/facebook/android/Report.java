package com.facebook.android;

import com.facebook.android.FacebookMain;
import com.google.gson.Gson;

import android.content.Context;
import android.content.Intent;
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
import java.util.Timer;
import java.util.TimerTask;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;
import org.json.JSONStringer;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class Report extends Activity implements View.OnClickListener {

	public static GooglePlace[] places = new GooglePlace[10];

	String[] checked;
	String location, reason, points;
	TextView tvReport, tvPlaces;
	Button report;
	ImageButton ib;
	ImageView iv;
	EditText et1, comments;
	RadioGroup rg;
	RadioButton r1, r2;
	CheckBox c1, c3;
	Intent i, profileIntent;
	final static int iData = 0;
	final static int iVenue = 1;
	final static int iEmail = 2;
	Bitmap bmp;
	private Button exitButton;
	private static GooglePlace mGooglePlace = new GooglePlace();
	private ProgressDialog mProgress;
	private View tmpView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// initialization of all the objects
		setContentView(R.layout.report);
		Init();
		Bundle gotChecked = getIntent().getExtras();
		try {
			checked = gotChecked.getStringArray("checkedOptions");
			if (checked != null)
				c3.setVisibility(View.VISIBLE);
		} catch (NullPointerException e) {
			Log.w("user didn't check any box", e.toString());
		}
		try {
			location = gotChecked.getString("StrLocation");
			et1.setText(location);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		try {
			if (bmp != null) {
				bmp = (Bitmap) getIntent().getParcelableExtra("BitmapImage");
				iv.setImageBitmap(bmp);
			}
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		// Top Menu and switching between activities
		tvReport.setOnClickListener(this);
		tvPlaces.setOnClickListener(this);
		et1.setOnClickListener(this);
		r1.setOnClickListener(this);
		r2.setOnClickListener(this);

		report.setOnClickListener(this);
		ib.setOnClickListener(this);
		InputStream is = getResources().openRawResource(R.drawable.imageplace);
		bmp = BitmapFactory.decodeStream(is);

		// set spinner selected value
		if (checked != null) {
			r2.setChecked(true);
		}

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

	public void onBackPressed() {
		Intent myIntent = new Intent(getApplicationContext(),
				FacebookMain.class);
		startActivity(myIntent);
	}

	private void Init() {
		report = (Button) findViewById(R.id.bReport);
		ib = (ImageButton) findViewById(R.id.ibReport);
		iv = (ImageView) findViewById(R.id.ivReport);
		tvReport = (TextView) findViewById(R.id.tvRep);
		tvPlaces = (TextView) findViewById(R.id.tvPla);
		rg = (RadioGroup) findViewById(R.id.ReasonSp);
		r1 = (RadioButton) findViewById(R.id.ReasonRB1);
		r2 = (RadioButton) findViewById(R.id.ReasonRB2);
		et1 = (EditText) findViewById(R.id.etLocation);
		comments = (EditText) findViewById(R.id.comments);
		c1 = (CheckBox) findViewById(R.id.checkBox1);
		c3 = (CheckBox) findViewById(R.id.checkBox3);
		mProgress = new ProgressDialog(this);
	}

	@Override
	public void onClick(View v) {
		Intent myIntent;
		switch (v.getId()) {
		case R.id.ReasonRB1:
			c3.setVisibility(View.INVISIBLE);
			c3.setChecked(false);
			break;

		case R.id.ReasonRB2:
			i = new Intent(Report.this, ExtendedCheckBoxList.class);
			Bundle cbBundle = new Bundle();
			location = et1.getText().toString();
			cbBundle.putString("StrLocation", location);
			i.putExtras(cbBundle);
			i.putExtra("BitmapImage", bmp);
			startActivityForResult(i, iData);
			break;
		case R.id.ibReport:
			i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(i, iData);
			break;
		case R.id.bReport:
			// check
			/*if (!haveNetworkConnection()) {
				final Runnable mInternetNotification = new Runnable() {
					public void run() {
						Toast.makeText(getBaseContext(),
								"You have no internet connection!",
								Toast.LENGTH_LONG).show();
					}
				};
				msgPoster.post(mInternetNotification);
				break;
			}*/
			// check that the location field is not empty
			if (et1.getText().toString().compareTo("<<< Choose place >>>") == 0) {
				msgPoster.post(mChoosePlaceNotification);
				break;
			}
			if (c3.isChecked())
				mProgress.setMessage("Please Wait...");
			else
				mProgress.setMessage("Sending report...");
			mProgress.show();
			report.setEnabled(false);
			tmpView = v;
			new Thread() {
				public void run() {
					Looper.prepare();
					location = et1.getText().toString();
					RadioButton chosen = (RadioButton) findViewById(rg
							.getCheckedRadioButtonId());
					reason = chosen.getText().toString();
					int user_score = 0;
					int goodplace_rate = 0;
					int badplace_rate = 0;
					boolean is_positive = false;
					String locid = null;
					// calculate new score

					if (reason.compareTo("Positive Report") == 0) {
						user_score = 2;
						goodplace_rate = 1;
						is_positive = true;
					}
					if (reason.compareTo("Complaint") == 0) {
						user_score = 1;
						badplace_rate = 1;
						is_positive = false;
					}
					points = "" + user_score;
					locid = mGooglePlace.id;

					int conflict = 0;
					SimpleDateFormat s = new SimpleDateFormat(
							"dd/MM/yyyy hh:mm:ss");

					String date = s.format(new Date());
					if (locid != null)
						Log.w("google places id is", locid);
					else
						locid = "NoPlaceFound";

					// Change this
					// mGooglePlace.refrence="Non";
					String comment = "No comment"; // should be text from user
					// //////////
					int reasons[];
					if (!is_positive) {
						reasons = new int[checked.length];
						int f = 0;
						for (String str : checked) {
							reasons[f] = (str == null ? 0 : 1);
							f++;
						}
					}
					else
						reasons = null;
					LocationRequest loc = new LocationRequest(locid,
							mGooglePlace.refrence, mGooglePlace.name,
							mGooglePlace.vicinity,
							mGooglePlace.location.getLatitude(),
							mGooglePlace.location.getLongitude(),
							goodplace_rate, badplace_rate);
					UserRequest ur = new UserRequest(FacebookMain.email,
							user_score, locid, date);
					ReportRequest rr = new ReportRequest(FacebookMain.email,
							locid, reason, date, reasons, comment);
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
								getString(R.string.DatabaseUrl) + "/UpdateUser");
					} catch (Exception e) {
						Log.w("couldn't send user request to servlet",
								e.toString());
					}

					/* check if the report already exists */
					// get user info from server
					Gson gson4 = new Gson();
					WebRequest res = new WebRequest();
					String str = null;
					UserRequest ur_check = null;
					try {
						JSONObject json4 = res
								.readJsonFromUrl("http://www.smokingnot2012.appspot.com/GetUser?mail="
										+ FacebookMain.email);
						str = (String) json4.get("user_req");
						Log.w("str=", str);
						ur_check = gson4.fromJson(str, UserRequest.class);
					} catch (JSONException e) {
						Log.e("Report error, can't get response from server, JSON exception",
								e.toString());
						Log.w("str=", str);
					} catch (Exception e) {
						Log.e("Report error, can't get response from server",
								e.toString());
						Log.w("str=", str);
					}
					if (ur_check.GetMessage().compareTo("Report Exsits") == 0) {
						showConflict(tmpView);
						conflict = 1;
					}

					/* Send location to Database */

					if (conflict == 0) {
						// convert location request to gson string
						Gson gson1 = new Gson();
						String LocationStr = gson1.toJson(loc);
						JSONStringer json1 = null;

						// prepare Json
						try {
							json1 = new JSONStringer().object().key("action")
									.value("update_location")
									.key("location_request").value(LocationStr)
									.endObject();

						} catch (JSONException e) {
							Log.e("json exeption-can't create jsonstringer with location",
									e.toString());
						}

						// send json to web server
						try {
							req.getInternetData(json1,
									getString(R.string.DatabaseUrl)
											+ "/UpdateUser");
						} catch (Exception e) {
							Log.w("couldn't send location to servlet",
									e.toString());
						}

						/* Send ReportRequest to Database */

						// convert report request to gson string
						Gson gson3 = new Gson();
						String ReportStr = gson3.toJson(rr);
						JSONStringer json3 = null;

						// prepare Json
						try {
							json3 = new JSONStringer().object().key("action")
									.value("update_report")
									.key("report_request").value(ReportStr)
									.endObject();

						} catch (JSONException e) {
							Log.e("json exeption-can't create jsonstringer with report",
									e.toString());
						}

						// send json to web server
						try {
							req.getInternetData(json3,
									getString(R.string.DatabaseUrl)
											+ "/UpdateUser");
						} catch (Exception e) {
							Log.w("couldn't send report to servlet",
									e.toString());
						}

					}

					// send email
					if (c3.isChecked()) {
						Intent repIntent = new Intent(Report.this,
								OfficialReport.class);
						Bundle returnBundle = new Bundle();
						returnBundle.putStringArray("checkedOptions", checked);
						returnBundle.putString("StrLocation", location);
						repIntent.putExtras(returnBundle);
						startActivity(repIntent);
					} else {
						/*
						 * String emailaddress[] = { FacebookMain.email };
						 * String message = "Hello, \n" + "The Report about " +
						 * location + " Has been Sent! \n" +
						 * "The Reasons you've pointed were:\n"; for (int k = 0;
						 * k < checked.length; k++) { if (checked[k] != null)
						 * message += checked[k].toString() + "\n"; } message +=
						 * "Have a pleasant Day!"; myIntent = new
						 * Intent(android.content.Intent.ACTION_SEND);
						 * myIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
						 * emailaddress);
						 * myIntent.putExtra(android.content.Intent
						 * .EXTRA_SUBJECT, "Smoking-Not Update!");
						 * myIntent.setType("plain/text");
						 * myIntent.putExtra(android.content.Intent.EXTRA_TEXT,
						 * message);
						 */
						if (c1.isChecked())
							PostStatusToFeed(MSG);

						if (conflict == 0) {
							mHandler.sendMessage(mHandler.obtainMessage(1));

						}

					}
					mHandler.sendMessage(mHandler.obtainMessage(0));
				}
			}.start();
			break;
		case R.id.tvRep:
			break;
		case R.id.tvPla:
			myIntent = new Intent(getApplicationContext(), Places.class);
			if (Utility.mFacebook.isSessionValid()) {
				Utility.objectID = "me";
				startActivity(myIntent);
			}
			break;
		case R.id.etLocation:
			myIntent = new Intent(getApplicationContext(), ChoosePlace.class);
			startActivityForResult(myIntent, iVenue);
			Log.i("ERIC", "Should show ChooseVenue");
			break;
		}

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
				case iVenue:
					Log.i("ERIC", "getting place");
					Log.i("ERIC", "bundle: " + extras.getString("placeName"));
					mGooglePlace.id = extras.getString("placeID");
					mGooglePlace.refrence = extras.getString("placeReference");
					mGooglePlace.name = extras.getString("placeName");
					mGooglePlace.vicinity = extras.getString("placeVicinity");
					mGooglePlace.location = extras
							.getParcelable("placeLocation");
					et1.setText(mGooglePlace.name + "\n"
							+ mGooglePlace.vicinity);
					break;
				case iEmail:

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
		builder.setTitle("Your Report Has Been Sent!");
		builder.setMessage("please check out your profile.");
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

	private static final String MSG = "Report:";

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
			if (reason.compareTo("Positive Report") == 0) {
				parameters.putString("caption", "Reported " + location
						+ " with " + reason);
			} else {
				parameters.putString("caption", " ");

			}
			parameters.putString("description", "Gained " + points
					+ " points for the report");

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
				break;
			case 1:
				showDialog(tmpView);

				// send notification to user
				break;
			}

		}

	};
}