package com.facebook.android;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONStringer;

import com.google.gson.Gson;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class OfficialReport extends Activity implements View.OnClickListener {
	private EditText name, phone, add, mail;
	private Button bRep, exitButton;
	private String[] checked;
	private String loc;
	Bitmap bmp;
	
	private SharedPreferences sh_pref;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.official_report);
		Init();

		// Receive the sent data
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

		bmp = (Bitmap) getIntent().getParcelableExtra("BitmapImage");

		
		// START MENU BUTTON
		exitButton = (Button) findViewById(R.id.exitButton);
		exitButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
			}
		});

		// Submit Button
		bRep.setOnClickListener(this);

	}

	private void Init() {
		bRep = (Button) findViewById(R.id.bReport);
		phone = (EditText) findViewById(R.id.etORPhone);
		name = (EditText) findViewById(R.id.etORName);
		mail = (EditText) findViewById(R.id.etOREmail);
		add = (EditText) findViewById(R.id.etORAdd);
		sh_pref = this.getSharedPreferences("OfficialReport", 0);
		name.setText(sh_pref.getString("first_last_name", ""));
		add.setText(sh_pref.getString("address", ""));
		phone.setText(sh_pref.getString("phone", ""));
		mail.setText(sh_pref.getString("mail", ""));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String details = "";
		switch (v.getId()) {
		case R.id.bReport:
			EmailDetails ed;

			if (!validity()) {

				details = name.getText().toString() + "\n";
				details += add.getText().toString() + "\n";
				details += phone.getText().toString() + "\n";
				details += mail.getText().toString() + "\n";

		try {
				

					File myFile = new File("/sdcard/TRDetails.txt");
					myFile.createNewFile();
					FileOutputStream fOut = new FileOutputStream(myFile);
					OutputStreamWriter myOutWriter = new OutputStreamWriter(
							fOut);
					myOutWriter.append(details);
					myOutWriter.close();
					fOut.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();

				}


			
				String checked_str = "";
				StringBuilder sb = new StringBuilder("");
		
				for (int i = 0; i < checked.length; i++) {
					if (checked[i] != null)
						sb.append(checked[i]).append(" ");
				}
		
				checked_str = sb.toString();
				Log.i("ERIC ortal", checked_str);
				Log.i("ortal", name.getText().toString());
				ed = new EmailDetails(name.getText().toString(),phone.getText().toString(),
						add.getText().toString(),mail.getText().toString(),checked_str,loc);
				
				
				if (bmp!=null) {
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					bmp.compress(Bitmap.CompressFormat.PNG, 100, bos);
					byte [] barr= bos.toByteArray();
					ed.setPicture(barr);
				}

				
				WebRequest req = new WebRequest();
				
				Gson gson2 = new Gson();
				String EmailStr = gson2.toJson(ed);
				JSONStringer json2 = null;

				// prepare Json
				try {

					json2 = new JSONStringer().object().key("send_email")
							.value(EmailStr).endObject();
			

				} catch (JSONException e) {
					Log.e("json exeption-can't create jsonstringer with email",
							e.toString());
				}

				// send json to web server

		
				try {
					req.getInternetData(json2,
							getString(R.string.DatabaseUrl) + "/EmailReport");
				} catch (Exception e) {
					Log.w("couldn't send email to servlet",
							e.toString());
				}
						
				// pop-up view
				showDialog(v);
				saveFields();
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

	private boolean validity() {
		boolean flag = false;
		Dialog d = new Dialog(this);
		d.setCanceledOnTouchOutside(true);
		d.setTitle("Error!");
		TextView sTV = new TextView(this);
		if (name.getText().toString().compareTo("") == 0) {
			sTV.setText("You have to fill the name field\n");
			flag = true;
		}
		if (mailValidity(sTV)) {
			flag = true;
		}
		if (phoneValidity(sTV)) {
			flag = true;
		}
		if (add.getText().toString().compareTo("") == 0) {
			sTV.setText("You have to fill the address field\n");
			flag = true;
		}
		d.setContentView(sTV);
		if (flag)
			d.show();
		return flag;
	}

	private boolean mailValidity(TextView sTV) {
		String str = "";
		boolean flag = false;
		if (mail.getText().toString().compareTo("") == 0) {
			str += "You have to fill the email field\n";
			flag = true;
		}
		if (!mail.getText().toString().contains("@")) {
			str += "Unvalid email entry\n";
			flag = true;
		}
		sTV.setText(str);
		return flag;
	}

	private boolean phoneValidity(TextView sTV) {
		String str = "";
		String digits = "1234567890-";
		String myPhone = phone.getText().toString();
		boolean flag = false;
		if (myPhone.compareTo("") == 0) {
			str += "You have to fill the phone field\n";
			flag = true;
		}
		for (int i = 0; i < myPhone.length(); i++) {
			if (!digits.contains("" + myPhone.charAt(i))) {
				str += "Unvalid phone entry\n";
				flag = true;
			}
		}
		sTV.setText(str);
		return flag;
	}

	private void saveFields() {
		SharedPreferences.Editor pref_edit = sh_pref.edit();
		
		pref_edit.putString("first_last_name", name.getText().toString());
		pref_edit.putString("address", add.getText().toString());
		pref_edit.putString("phone", phone.getText().toString());
		pref_edit.putString("mail", mail.getText().toString());
		
		pref_edit.commit();
	}

}
