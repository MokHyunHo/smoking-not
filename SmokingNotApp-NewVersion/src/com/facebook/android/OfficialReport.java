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
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String[] readDel = new String[4];
		int i = 0;
		setContentView(R.layout.official_report);
		Init();

		try {
			File myFile = new File("/sdcard/mysdfile.txt");
			FileInputStream fIn = new FileInputStream(myFile);
			BufferedReader myReader = new BufferedReader(new InputStreamReader(
					fIn));
			String aDataRow = "";
			while ((aDataRow = myReader.readLine()) != null) {
				readDel[i] = aDataRow;
				i++;
			}
			name.setText(readDel[0]);
			add.setText(readDel[1]);
			phone.setText(readDel[2]);
			mail.setText(readDel[3]);
			myReader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
				Intent myIntent = new Intent(getApplicationContext(),
						FacebookMain.class);
				if (Utility.mFacebook.isSessionValid()) {
					Utility.objectID = "me";
				}
				startActivity(myIntent);
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
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String details = "";
		switch (v.getId()) {
		case R.id.bReport:
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

				String email = null;
				WebRequest req = new WebRequest();

				try {
					String checked_str = "";
					StringBuilder sb = new StringBuilder("");

					for (int i = 0; i < checked.length; i++) {
						if (checked[i] != null)
							sb.append(checked[i]).append(" ");
					}

					checked_str = sb.toString();
					Log.i("ERIC ortal", checked_str);

					if (bmp != null) {
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						bmp.compress(Bitmap.CompressFormat.PNG, 100, bos);
						byte[] barr = bos.toByteArray();
						email = getString(R.string.DatabaseUrl)
								+ "/EmailReport?name="
								+ name.getText().toString() + "&phone="
								+ phone.getText().toString() + "&mail="
								+ mail.getText().toString() + "&address="
								+ add.getText().toString() + "&location=" + loc
								+ "&reasons=" + checked_str + "&pic=No_picture";
					} else
						email = getString(R.string.DatabaseUrl)
								+ "/EmailReport?name="
								+ name.getText().toString() + "&phone="
								+ phone.getText().toString() + "&mail="
								+ mail.getText().toString() + "&address="
								+ add.getText().toString() + "&location=" + loc
								+ "&reasons=" + checked_str + "&pic=No_picture";

					// create string email

				} catch (Exception e) {
					Log.w("ortal", "can't send email to server");
				}
				try {
					req.SendEmail(email);

				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					Log.w("error while sending email to database-ClientProtocolException",
							e.getMessage());
					e.printStackTrace();
				} catch (URISyntaxException e) {
					// TODO Auto-generated catch block
					Log.w("error while sending email to database-URISyntaxException",
							e.getMessage());
					e.printStackTrace();
				} catch (IOException e) {
					Log.w("error while sending email to database-IOException",
							e.getMessage());
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					Log.w("error while sending email to database-exception",
							e.getMessage());
					e.printStackTrace();
				}

				// pop-up view
				showDialog(v);
				clearForm();
			}
			break;
		}

	}

	private void clearForm() {
		phone.setText("");
		name.setText("");
		mail.setText("");
		add.setText("");
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

}
