package com.facebook.android;

import com.facebook.android.R;
import com.facebook.android.FacebookMain;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

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
			e.printStackTrace();
		}
		try {
			String loc;
			loc = gotChecked.getString("StrLocation");
			et1.setText(loc);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		try {
			bmp = (Bitmap) getIntent().getParcelableExtra("BitmapImage");
			iv.setImageBitmap(bmp);
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
		// TODO Auto-generated method stub
		Intent myIntent;
		switch (v.getId()) {
		case R.id.ibReport:
			i = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(i, iData);
			break;
		case R.id.bReport:
			String location = et1.getText().toString();

			// send email
			if (c3.isChecked()) {
				myIntent = new Intent(Report.this, OfficialReport.class);
				Bundle returnBundle = new Bundle();
				returnBundle.putStringArray("checkedOptions", checked);
				returnBundle.putString("StrLocation", location);
				myIntent.putExtras(returnBundle);
				startActivity(myIntent);
			} else {
				String emailaddress[] = { "eladcoo@gmail.com" };
				String message = "Hello, \n" + "The Report about " + location
						+ " Has been Sent! \n"
						+ "The Reasons you've pointed were:\n";
				for (int j = 0; j < checked.length; j++) {
					if (checked[j] != null)
						message += checked[j].toString() + "\n";
				}
				message += "Have a pleasant Day!";
				myIntent = new Intent(android.content.Intent.ACTION_SEND);
				myIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
						emailaddress);
				myIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
						"Smoking-Not Update!");
				myIntent.setType("plain/text");
				myIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);

				// pop-up view
				showDialog(v);

				startActivity(myIntent);

				// send notification to user
				sendNotification();

				int score = 0;

				// calculate new score
				String reason = s1.getSelectedItem().toString();
				if (reason.compareTo("Positive Report") == 0)
					score += 2;
				if (reason.compareTo("Complaint") == 0)
					score += 1;

				// set place info
				setPlaceRating(reason, location);
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
					Log.i("ERIC", "name: " + mGooglePlace.name);
					et1.setText(mGooglePlace.name);
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

	private void setPlaceRating(String reason, String location) {
		int j;
		double myRate = 0;
		for (j = 0; places[j] != null; j++)
			if (places[j].name.compareTo(location) == 0)
				myRate = places[j].rate;

		if (reason.compareTo("Positive Report") == 0)
			myRate++;
		if (reason.compareTo("Complaint") == 0) {
			myRate--;
			if (myRate < 0)
				myRate = 0;
		}
		if (places[j] == null)
			places[j] = mGooglePlace;
		places[j].rate = myRate;
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
}