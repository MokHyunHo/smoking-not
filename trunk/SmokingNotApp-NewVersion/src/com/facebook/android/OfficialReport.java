package com.facebook.android;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class OfficialReport extends Activity implements View.OnClickListener {
	
	private EditText name,phone,add,mail;
	private Button bRep,exitButton;
	private String[] checked;
	private String loc;
	static final int uniqueId = 1234;
	NotificationManager nm;
	
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
		
		// create notification manager
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(uniqueId);
		
	}
	
	private void Init() {
		bRep = (Button) findViewById(R.id.bReport);
		phone = (EditText) findViewById(R.id.etORPhone);
		name = (EditText) findViewById(R.id.etORName);
		mail = (EditText) findViewById(R.id.etOREmail);
		add = (EditText) findViewById(R.id.etORAdd);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent myIntent;
		switch (v.getId()) {
		case R.id.bReport:
			if(!validity())
			{
				String emailaddress[]={"eladcoo@gmail.com"};
				String message="Hello, \n" +
						"The Report about " + loc +" Has been Sent! \n" +
						"The Reasons you've pointed were:\n";
				for(int j=0;j<checked.length;j++) { 
					if(checked[j]!=null)
						message+=checked[j].toString()+"\n";
				}
				message+="Have a pleasant Day!";
				myIntent=new Intent(android.content.Intent.ACTION_SEND);
				myIntent.putExtra(android.content.Intent.EXTRA_EMAIL, emailaddress);
				myIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Smoking-Not Update!");
				myIntent.setType("plain/text");
				myIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
				Trial t=new Trial(name.getText().toString(), phone.getText().toString(), mail.getText().toString(), add.getText().toString());
				File f=t.CreatePdf();
				Uri uri = Uri.fromFile(f);
				myIntent.putExtra(Intent.EXTRA_STREAM, uri);
				
				// pop-up view
				showDialog(v);
				
				startActivity(myIntent);
			}
			break;
		}
		
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
	
	private boolean validity()
	{
		boolean flag=false;
		Dialog d = new Dialog(this);
		d.setCanceledOnTouchOutside(true);
		d.setTitle("Error!");
		TextView sTV = new TextView(this);
		if(name.getText().toString().compareTo("")==0)
		{
			sTV.setText("You have to fill the name field");
			flag=true;
		}
		if(mail.getText().toString().compareTo("")==0)
		{
			sTV.setText("You have to fill the email field");
			flag=true;
		}
		if(phone.getText().toString().compareTo("")==0)
		{
			sTV.setText("You have to fill the phone field");
			flag=true;
		}
		if(add.getText().toString().compareTo("")==0)
		{
			sTV.setText("You have to fill the address field");
			flag=true;
		}
		d.setContentView(sTV);
		if(flag)
			d.show();
		return flag;
	}

}
