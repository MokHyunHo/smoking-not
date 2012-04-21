package com.facebook.android;

import com.facebook.android.R;
import com.facebook.android.FacebookMain;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.app.Dialog;
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
	String[] checked;
	TextView tvReport, tvPlaces, tvProfile;
	Button report;
	ImageButton ib;
	ImageView iv;
	EditText et1;
	Spinner s1;
	CheckBox c1, c2, c3;
	Intent i, profileIntent;
	final static int iData = 0;
	final static int iVenue = 1;
	Bitmap bmp;
	String[] cbl = { "Positive Report", "Complaint" };
	private Button exitButton;
	static final int uniqueId = 1234;
	NotificationManager nm;
	private FsqVenue mFsqVenue = new FsqVenue();
	
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
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		try {
			String loc;
			loc = gotChecked.getString("StrLocation");
			et1.setText(loc);
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
		c1 = (CheckBox) findViewById(R.id.checkBox1);
		c2 = (CheckBox) findViewById(R.id.checkBox2);
		c3 = (CheckBox) findViewById(R.id.checkBox3);
		et1 = (EditText) findViewById(R.id.etLocation);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent myIntent;
		switch (v.getId()) {
		case R.id.ibReport:
			i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(i, iData);
			break;
		case R.id.bReport:
			 String location = et1.getText().toString();
			/*
			 * String location = et1.getText().toString(); if (c3.isChecked()) {
			 * myIntent = new Intent(Report.this, OfficialReport.class); Bundle
			 * returnBundle=new Bundle();
			 * returnBundle.putStringArray("checkedOptions", checked);
			 * returnBundle.putString("StrLocation", location);
			 * myIntent.putExtras(returnBundle); startActivity(myIntent); } else
			 * { String emailaddress[] = { "eladcoo@gmail.com" }; String message
			 * = "Hello, \n" + "The Report about " + location +
			 * " Has been Sent! \n" + "The Reasons you've pointed were:\n"; for
			 * (int j = 0; j < checked.length; j++) { message +=
			 * checked[j].toString() + "\n"; } message +=
			 * "Have a pleasant Day!"; myIntent = new
			 * Intent(android.content.Intent.ACTION_SEND);
			 * myIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
			 * emailaddress);
			 * myIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
			 * "Smoking-Not Update!"); myIntent.setType("plain/text");
			 * myIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
			 */

			// pop-up view
			Dialog d = new Dialog(this);
			d.setCanceledOnTouchOutside(true);
			d.setTitle("Your Report Has Been Sent!");
			TextView sTV = new TextView(this);
			sTV.setText("please check out your profile.");
			d.setContentView(sTV);
			d.show();
			/* startActivity(myIntent); */

			// send notification to user
			Intent new_intent = new Intent(this, Profile.class);
			PendingIntent pi = PendingIntent
					.getActivity(this, 0, new_intent, 0);
			String body = "You sccore has been increased by 1 point!";
			String title = "New Message";
			Notification n = new Notification(R.drawable.statusbar, title,
					System.currentTimeMillis());
			n.setLatestEventInfo(this, title, body, pi);
			// n.defaults=Notification.
			nm.notify(uniqueId, n);
			finish();
			
			int score=0;
			//read data from sd card
			try {
				File myReadFile = new File("/sdcard/mysdfile.txt");
				if(myReadFile.exists())
				{
					FileInputStream fIn = new FileInputStream(myReadFile);
					BufferedReader myReader = new BufferedReader(new InputStreamReader(fIn));
					String aDataRow = "";
					String[] aBuffer = new String[3];
					for (int i=0;(aDataRow = myReader.readLine()) != null;i++) {
						aBuffer[i] = aDataRow;
					}
					score=Integer.parseInt(aBuffer[2]);
					myReader.close();
					fIn.close();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//calculate new score
			String reason=s1.getSelectedItem().toString();
			if(reason.compareTo("Positive Report")==0)
				score+=2;
			if(reason.compareTo("Complaint")==0)
				score+=1;
			
			// save data to the sd card
			try {
				String sdData = location+'\n'
								+reason+'\n'
								+score;
				File myFile = new File("/sdcard/mysdfile.txt");
				if(!myFile.exists())
					myFile.createNewFile();
				FileOutputStream fOut = new FileOutputStream(myFile);
				OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
				myOutWriter.append(sdData);
				myOutWriter.close();
				fOut.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			// }
			
			
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
			myIntent = new Intent(getApplicationContext(), ChooseVenue.class);
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
			switch (requestCode)
			{
			case iData:
				bmp = (Bitmap) extras.get("data");
				iv.setImageBitmap(bmp);
				break;
			case iVenue:
				Log.i("ERIC", "getting venue");
				Log.i("ERIC", "bundle: " + extras.getString("venueName"));
				mFsqVenue.id = extras.getString("venueID");
				mFsqVenue.name = extras.getString("venueName");
				Log.i("ERIC", "name: " + mFsqVenue.name);
				et1.setText(mFsqVenue.name);
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
		case 1:
			i = new Intent(Report.this, ExtendedCheckBoxList.class);
			Bundle cbBundle = new Bundle();
			String strlocation = et1.getText().toString();
			cbBundle.putString("StrLocation", strlocation);
			i.putExtras(cbBundle);
			startActivityForResult(i, iData);
			break;

		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}
}