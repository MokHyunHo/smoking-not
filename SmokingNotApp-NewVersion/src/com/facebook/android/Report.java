package com.facebook.android;

import com.facebook.android.R;
import com.facebook.android.FacebookMain;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import java.io.InputStream;
import android.app.Activity;
import android.app.Dialog;
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
	Bitmap bmp;
	String[] cbl = { "Good Report", "Complaint" };
	private Button exitButton;

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
			// passing values to the 'Send' action

			/*
			 * Bundle nextBundle = new Bundle(); String[] ways=new String[3];
			 * int j=0; String location = et1.getText().toString();
			 * nextBundle.putString("keyLocation", location);
			 * 
			 * if(c1.isChecked()) { ways[j]=c1.getText().toString(); j++; }
			 * if(c2.isChecked()) { ways[j]=c2.getText().toString(); j++; }
			 * if(c3.isChecked()) { ways[j]=c3.getText().toString(); j++; }
			 * nextBundle.putStringArray("PublishWays", ways); if(checked!=null)
			 * nextBundle.putStringArray("chosenReasons", checked); else {
			 * String[] reason=new String[2]; reason[0]=
			 * s1.getSelectedItem().toString();
			 * nextBundle.putStringArray("chosenReasons",reason); } Intent int_a
			 * = new Intent(Report.this, AcceptReport.class);
			 * int_a.putExtras(nextBundle); startActivity(int_a);
			 */
			if (c3.isChecked()) {
				myIntent = new Intent(Report.this, OfficialReport.class);
				startActivity(myIntent);	
			} else {
				Dialog d = new Dialog(this);
				d.setCanceledOnTouchOutside(true);
				d.setTitle("Your Report Has Been Sent!");
				TextView sTV = new TextView(this);
				sTV.setText("please check out your profile.");
				d.setContentView(sTV);
				d.show();
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
			/*
			 * tvProfile.setBackgroundResource(R.drawable.orange);
			 * tvProfile.setBackgroundColor(android.R.color.black);
			 */
			myIntent = new Intent(getApplicationContext(), Profile.class);
			if (Utility.mFacebook.isSessionValid()) {
				Utility.objectID = "me";
				startActivity(myIntent);
			}
			break;
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			bmp = (Bitmap) extras.get("data");
			iv.setImageBitmap(bmp);
		}

	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		int position = s1.getSelectedItemPosition();
		switch (position) {
		case 1:
			try {
				Class cbClass = Class
						.forName("com.facebook.android.ExtendedCheckBoxList");
				i = new Intent(Report.this, cbClass);
				Bundle cbBundle = new Bundle();
				String strlocation = et1.getText().toString();
				cbBundle.putString("StrLocation", strlocation);
				i.putExtras(cbBundle);
				startActivityForResult(i, iData);

			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;

		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}
}