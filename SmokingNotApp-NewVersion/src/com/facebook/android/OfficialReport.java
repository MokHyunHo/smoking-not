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
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.official_report);
		Init();
		
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
		switch (v.getId()) {
		case R.id.bReport:
			Dialog d=new Dialog(this);
			d.setCanceledOnTouchOutside(true);
			d.setTitle("Your Report Has Been Sent!");
			TextView sTV=new TextView(this);
			sTV.setText("please check out your profile.");
			d.setContentView(sTV);
			d.show();
			break;
		}
		
	}

}
