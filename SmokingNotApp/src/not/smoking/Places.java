package not.smoking;

//import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Places extends Activity {
	/** Called when the activity is first created. */

	private Button mProfileButton;
	private Button mReportButton;
	private Button mPlacesButton;

	private Button exitButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.places);

		// connection between XML & JAVA

		// first-up menu
		mProfileButton = (Button) findViewById(R.id.profileButton);
		mReportButton = (Button) findViewById(R.id.reportButton);
		mPlacesButton = (Button) findViewById(R.id.placesButton);

		// listeners for FIRST-UP MENU

		mProfileButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent myIntent = new Intent(getApplicationContext(),
						Profile.class);
				if (Utility.mFacebook.isSessionValid()) {
					Utility.objectID = "me";
					startActivity(myIntent);
				}
			}
		});

		mReportButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent myIntent = new Intent(getApplicationContext(),
						Report.class);
				if (Utility.mFacebook.isSessionValid()) {
					Utility.objectID = "me";
					startActivity(myIntent);
				}
			}
		});

		/**
		 * mPlacesButton.setOnClickListener(new OnClickListener() { public void
		 * onClick(View v) { Intent myIntent = new
		 * Intent(getApplicationContext(), Places.class); if
		 * (Utility.mFacebook.isSessionValid()) { Utility.objectID = "me";
		 * startActivity(myIntent); }} });
		 **/

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
}
