package il.reporter.gws;

import com.facebook.android.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class OfficialReport extends Activity implements View.OnClickListener {
	private EditText name, phone, add, mail;
	private Button bRep;
	Bitmap bmp;

	private SharedPreferences sh_pref;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.official_report);
		Init();

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
		//name.setText(sh_pref.getString("first_last_name", ""));
		name.setText(FacebookMain.name);
		mail.setText(FacebookMain.email);
		add.setText(sh_pref.getString("address", ""));
		phone.setText(sh_pref.getString("phone", ""));
		//mail.setText(sh_pref.getString("mail", ""));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.bReport:
			if (!validity()) {
				saveFields();
				Intent data = new Intent();

				data.putExtra("Name", name.getText().toString());
				data.putExtra("Phone", phone.getText().toString());
				data.putExtra("Address", add.getText().toString());
				data.putExtra("Email", mail.getText().toString());
				setResult(Activity.RESULT_OK, data);
				//Log.i("Elad", "set result: " + name.getText().toString()+phone.getText().toString()+mail.getText().toString()+mail.getText().toString());
				finish();
			}
			break;
		}
	}
	
	public void onBackPressed()
	{
		setResult(Activity.RESULT_CANCELED);
		finish();
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
