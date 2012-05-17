package com.facebook.android;

import com.facebook.android.R;
import com.facebook.android.FacebookMain;
import com.google.gson.Gson;

import android.content.Intent;
import android.os.Bundle;
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
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class Report extends Activity implements View.OnClickListener,
		OnItemSelectedListener {

	public static GooglePlace[] places = new GooglePlace[10];

	String[] checked;
	TextView tvReport, tvPlaces, tvProfile;
	Button report;
	ImageButton ib;
	ImageView iv;
	EditText et1;
	Spinner s1;
	CheckBox c1, c3;
	Intent i, profileIntent;
	final static int iData = 0;
	final static int iVenue = 1;
	final static int iEmail = 2;
	Bitmap bmp;
	String[] cbl = { "Positive Report", "Complaint" };
	private Button exitButton;
	static final int uniqueId = 1234;
	NotificationManager nm;
	private static GooglePlace mGooglePlace = new GooglePlace();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(Report.this,
				android.R.layout.simple_spinner_item, cbl);

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
			String loc;
			loc = gotChecked.getString("StrLocation");
			et1.setText(loc);
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
		tvProfile.setOnClickListener(this);
		et1.setOnClickListener(this);
		// TBA
		s1.setAdapter(adapter);
		s1.setOnItemSelectedListener(this);

		report.setOnClickListener(this);
		ib.setOnClickListener(this);
		InputStream is = getResources().openRawResource(R.drawable.imageplace);
		bmp = BitmapFactory.decodeStream(is);

		// set spinner selected value
		if (checked != null) {
			s1.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}

			});
			s1.setSelection(1);
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
		// create notification manager
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(uniqueId);
	}

	private void Init() {
		report = (Button) findViewById(R.id.bReport);
		ib = (ImageButton) findViewById(R.id.ibReport);
		iv = (ImageView) findViewById(R.id.ivReport);
		tvReport = (TextView) findViewById(R.id.tvRep);
		tvPlaces = (TextView) findViewById(R.id.tvPla);
		tvProfile = (TextView) findViewById(R.id.tvPro);
		s1 = (Spinner) findViewById(R.id.ReasonSp);
		et1 = (EditText) findViewById(R.id.etLocation);
		c1 = (CheckBox) findViewById(R.id.checkBox1);
		c3 = (CheckBox) findViewById(R.id.checkBox3);
	}

	@Override
	public void onClick(View v) {
		Intent myIntent;
		switch (v.getId()) {
		case R.id.ibReport:
			i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(i, iData);
			break;
		case R.id.bReport:
			String location = et1.getText().toString();
			String reason = s1.getSelectedItem().toString();
			int user_score = 0;
			int goodplace_rate=0;
			int badplace_rate = 0;
			String locid = null;
			// calculate new score

			if (reason.compareTo("Positive Report") == 0) {
				user_score = 2;
				goodplace_rate = 2;
			}
			if (reason.compareTo("Complaint") == 0) {
				user_score = 1;
				badplace_rate = 1;
			}
			locid = mGooglePlace.id;
			int conflict=0;
			SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy");
			String date = s.format(new Date());
			if (locid != null)
				Log.w("google places id is", locid);
			else
				locid = "NoPlaceFound";
			LocationRequest loc = new LocationRequest(locid, goodplace_rate,badplace_rate);
			UserRequest ur = new UserRequest(FacebookMain.email, user_score,
					locid,date);
			ReportRequest rr = new ReportRequest(FacebookMain.email, locid,
					reason, date);
			WebRequest req = new WebRequest();

			/* Send UserRequest to Database */

			// convert UserRequest request to gson string
			Gson gson2 = new Gson();
			String UserStr = gson2.toJson(ur);
			JSONStringer json2 = null;

			// prepare Json
			try {
				json2 = new JSONStringer().object().key("action")
						.value("update_ur").key("user_request").value(UserStr)
						.endObject();

			} catch (JSONException e) {
				Log.e("json exeption-can't create jsonstringer with user request",
						e.toString());
			}

			// send json to web server
			try {
				req.getInternetData(json2);
			} catch (Exception e) {
				Log.w("couldn't send user request to servlet", e.toString());
			}
			
			
			/* check if the report already exists */
	        // get user info from server
			Gson gson4 = new Gson();
	    	WebRequest res=new WebRequest();
	    	String str=null;
	    	UserRequest ur_check=null;
	        try {
				JSONObject json4=res.readJsonFromUrl("http://www.smokingnot2012.appspot.com/GetUser?mail="+FacebookMain.email);
				str=(String)json4.get("user_req");
				Log.w("str=",str);
				ur_check=gson4.fromJson(str, UserRequest.class);
	        	}catch (JSONException e) {
						Log.e("Report error, can't get response from server, JSON exception",e.toString());
						Log.w("str=",str);
					}
			    catch (Exception e) {
				Log.e("Report error, can't get response from server",e.toString());
				Log.w("str=",str);
			}
	        if (ur_check.GetMessage().compareTo("Report Exsits") ==0) {
	        	showConflict(v);
	        	conflict=1;
	        }
			
			
			/* Send location to Database */

	        if (conflict==0) 
	        {
				// convert location request to gson string
				Gson gson1 = new Gson();
				String LocationStr = gson1.toJson(loc);
				JSONStringer json1 = null;
	
				// prepare Json
				try {
					json1 = new JSONStringer().object().key("action")
							.value("update_location").key("location_request")
							.value(LocationStr).endObject();
	
				} catch (JSONException e) {
					Log.e("json exeption-can't create jsonstringer with location",
							e.toString());
				}
	
				// send json to web server
				try {
					req.getInternetData(json1);
				} catch (Exception e) {
					Log.w("couldn't send location to servlet", e.toString());
				}
	
				
	
				/* Send ReportRequest to Database */
	
				// convert report request to gson string
				Gson gson3 = new Gson();
				String ReportStr = gson3.toJson(rr);
				JSONStringer json3 = null;
	
				// prepare Json
				try {
					json3 = new JSONStringer().object().key("action")
							.value("update_report").key("report_request")
							.value(ReportStr).endObject();
	
				} catch (JSONException e) {
					Log.e("json exeption-can't create jsonstringer with report",
							e.toString());
				}
	
				// send json to web server
				try {
					req.getInternetData(json3);
				} catch (Exception e) {
					Log.w("couldn't send report to servlet", e.toString());
				}
			
	        }

	        
	        
			// send email
			if (c3.isChecked()) {
				myIntent = new Intent(Report.this, OfficialReport.class);
				Bundle returnBundle = new Bundle();
				returnBundle.putStringArray("checkedOptions", checked);
				returnBundle.putString("StrLocation", location);
				myIntent.putExtras(returnBundle);
				startActivity(myIntent);
			} else {
				/*String emailaddress[] = { FacebookMain.email };
				String message = "Hello, \n" + "The Report about " + location
						+ " Has been Sent! \n"
						+ "The Reasons you've pointed were:\n";
				for (int k = 0; k < checked.length; k++) {
					if (checked[k] != null)
						message += checked[k].toString() + "\n";
				}
				message += "Have a pleasant Day!";
				myIntent = new Intent(android.content.Intent.ACTION_SEND);
				myIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
						emailaddress);
				myIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						"Smoking-Not Update!");
				myIntent.setType("plain/text");
				myIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);*/
				
				if (conflict==0){
					// pop-up view 
					showDialog(v); 
					
					// send notification to user
					sendNotification();
				}

				// start email activity
				//startActivity(myIntent);
			}

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
		case R.id.tvPro:
			myIntent = new Intent(getApplicationContext(), Profile.class);
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
					mGooglePlace.name = extras.getString("placeName");
					mGooglePlace.vicinity = extras.getString("placeVicinity");
					et1.setText(mGooglePlace.name + "\n" + mGooglePlace.vicinity);
					break;
				case iEmail:

					break;
				}
			}
		} catch (Throwable Ex) {
			Log.i("ERIC", "msg: " + Ex.toString());
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		int position = s1.getSelectedItemPosition();
		switch (position) {
		case 0:
			c3.setVisibility(View.INVISIBLE);
			c3.setChecked(false);
			break;

		case 1:
			i = new Intent(Report.this, ExtendedCheckBoxList.class);
			Bundle cbBundle = new Bundle();
			String strlocation = et1.getText().toString();
			cbBundle.putString("StrLocation", strlocation);
			i.putExtras(cbBundle);
			i.putExtra("BitmapImage", bmp);
			startActivityForResult(i, iData);
			break;

		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

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

	private void sendNotification() {
		Intent new_intent = new Intent(this, Profile.class);
		PendingIntent pi = PendingIntent.getActivity(this, 0, new_intent, 0);
		String body = "You sccore has been increased by 1 point!";
		String title = "New Message";
		Notification n = new Notification(R.drawable.statusbar, title,
				System.currentTimeMillis());
		n.setLatestEventInfo(this, title, body, pi);
		// n.defaults=Notification.
		nm.notify(uniqueId, n);
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

}