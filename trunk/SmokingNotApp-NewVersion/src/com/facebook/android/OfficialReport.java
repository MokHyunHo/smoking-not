package com.facebook.android;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class OfficialReport extends Activity implements View.OnClickListener {
	
	private EditText id;
	private EditText name;
	private Button bRep,exitButton;
	private String[] checked;
	private String loc;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.official_report);
		Init();
		
		//Receive the sent data
		Bundle gotChecked = getIntent().getExtras();
		try {
			checked = gotChecked.getStringArray("checkedOptions");
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		try {
			loc = gotChecked.getString("StrLocation");
		} catch (NullPointerException e) {
			e.printStackTrace();
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
		
		//Submit Button
		bRep.setOnClickListener(this);
		
	}
	
	private void Init() {
		bRep = (Button) findViewById(R.id.bReport);
		id = (EditText) findViewById(R.id.etORID);
		name = (EditText) findViewById(R.id.etORNAME);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent myIntent;
		switch (v.getId()) {
		case R.id.bReport:
			String emailaddress[]={"eladcoo@gmail.com"};
			String message="Hello, \n" +
					"The Report about " + loc +" Has been Sent! \n" +
					"The Reasons you've pointed were:\n";
			for(int j=0;j<checked.length;j++) { 
				message+=checked[j].toString()+"\n";
			}
			message+="Have a pleasant Day!";
			myIntent=new Intent(android.content.Intent.ACTION_SEND);
			myIntent.putExtra(android.content.Intent.EXTRA_EMAIL, emailaddress);
			myIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Smoking-Not Update!");
			myIntent.setType("plain/text");
			myIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
			
			Dialog d=new Dialog(this);
			d.setCanceledOnTouchOutside(true);
			d.setTitle("Your Report Has Been Sent!");
			TextView sTV=new TextView(this);
			sTV.setText("please check out your profile.");
			d.setContentView(sTV);
			d.show();
			startActivity(myIntent);
			break;
		}
		
	}

}
