package il.reporter.gws;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import com.facebook.android.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;
import android.widget.TextView;

public class Places extends Activity implements View.OnClickListener {
	/** Called when the activity is first created. */

	private final int M_LS_DOWN = 0;
	private final int M_UPD_LOC_ERR = 1;
	@SuppressWarnings("unused")
	private final int M_ADDRESS_NA = 2;
	private final int M_LOC_OK = 3;
	@SuppressWarnings("unused")
	private final int M_ADDRESS_OK = 4;
	private final int M_GET_PLACES_ERR = 5;
	private final int M_GET_PLACES_OK = 6;
	private final int M_GET_SUGGESTIONS_OK = 7;
	private final int M_GET_SUGGESTIONS_ERR = 8;
	private final int M_LOC_NA = 9;
	private final int AUTOCOMPLETE_MINIMUM_INTERVAL = 1000000000;
	private final int CYCLES_TO_WAIT = 1;
	private TextView tvReport, tvPlaces, tvStats, tvAddress;
	private Button goBtn;
	private GooglePlacesAPI mGooglePlacesAPI;
	private LocationEngine mLocEng;
	private ListView mListView;
	private NearbyAdapter mAdapter;
	private ArrayList<GooglePlace> mNearbyList;
	private ProgressDialog mProgress;
	private ImageButton exitButton, mShowMeOnMap, mRefreshButton, searchBtn,
			btnCategories;
	private Location mLocation, userLocation;
	private boolean near_me;
	private AutoCompleteTextView etSearch;
	private RadioGroup rg;
	// private AlertDialog.Builder addressAlert;
	private AutoCompleteTextView addressInput;
	private CountDownLatch latch = new CountDownLatch(1);
	private Context context;
	boolean m_chosen_cats[];
//  added---------------------------------------------------------------------
	private Button mQuestionButton;
	private View tmpView;

	long lastTime;
	String input_str;

	ArrayAdapter<String> ac_adapter;

	private void Init() {
		context = this;
		tvReport = (TextView) findViewById(R.id.tvReport);
		tvPlaces = (TextView) findViewById(R.id.tvPlaces);
		tvStats= (TextView) findViewById(R.id.tvStats);
		tvAddress = (TextView) findViewById(R.id.tvAddress);
		goBtn = (Button) findViewById(R.id.b_go);
		searchBtn = (ImageButton) findViewById(R.id.b_search);
		btnCategories = (ImageButton) findViewById(R.id.b_categoies);
		mListView = (ListView) findViewById(R.id.lv_places);
		mGooglePlacesAPI = new GooglePlacesAPI(this);
		mAdapter = new NearbyAdapter(this);
		mNearbyList = new ArrayList<GooglePlace>();
		mProgress = new ProgressDialog(this);
		//mProgress.setCancelable(false);
		mShowMeOnMap = (ImageButton) findViewById(R.id.ib_ShowMeOnMap);
		mRefreshButton = (ImageButton) findViewById(R.id.ib_Refresh);
		etSearch = (AutoCompleteTextView) findViewById(R.id.et_Search);
		mShowMeOnMap.setVisibility(View.INVISIBLE);
		goBtn.setEnabled(false);
		lastTime = System.nanoTime();
		near_me = true;
		addressInput = (AutoCompleteTextView) findViewById(R.id.etAddress);
		rg = (RadioGroup) findViewById(R.id.radioGroup1);
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				Log.i("ERIC", "checked: " + arg1);

				if (arg1 == R.id.radio0) {
					addressInput.clearFocus();
					etSearch.requestFocus();
					addressInput.setEnabled(false);
					near_me = true;

				} else {
					addressInput.setEnabled(true);
					near_me = false;
					addressInput.requestFocus();
				}
			}
		});

		addressInput.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {

				if (event.getAction() == KeyEvent.ACTION_UP) {
					lastTime = System.nanoTime();
					Log.i("ERIC", "last key at:" + lastTime);
					new Thread() {
						@Override
						public void run() {
							try {
								sleep(AUTOCOMPLETE_MINIMUM_INTERVAL / 1000000);
							} catch (InterruptedException e1) {
								e1.printStackTrace();
							}
							long now = System.nanoTime();
							long interval = now - lastTime;
							Log.i("ERIC", "inerval: " + interval);
							if (interval >= AUTOCOMPLETE_MINIMUM_INTERVAL) {
								int what = M_GET_SUGGESTIONS_OK;
								input_str = addressInput.getText().toString();
								if (input_str.length() > 1) {
									try {
										ac_adapter = new ArrayAdapter<String>(
												context,
												android.R.layout.simple_dropdown_item_1line,
												mGooglePlacesAPI.mGeoEng
														.getAddressSuggestions(
																input_str,
																mLocation));
									} catch (Exception e) {
										what = M_GET_SUGGESTIONS_ERR;
										e.printStackTrace();
									}
									mHandler.sendMessage(mHandler
											.obtainMessage(what));
								}
							}
						}
					}.start();
				}
				return false;
			}
		});

		// connection between XML & JAVA

		// first-up menu
		tvReport.setOnClickListener(this);
		tvPlaces.setOnClickListener(this);
		tvStats.setOnClickListener(this);

		
		//added---------------------------------------------------------------------
		mQuestionButton= (Button) findViewById(R.id.question);
		
		mQuestionButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				tmpView = v;
				showQuestionDialog(tmpView);
				
			}
		});

				
		
		// START MENU BUTTON
		exitButton = (ImageButton) findViewById(R.id.exitButton);
		exitButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (Utility.mFacebook.isSessionValid()) {
					Utility.objectID = "me";
				}
				finish();
			}
		});
	}

	
	public void onPause()
	{
		super.onPause();
		//finish();
	}
	
	@Override
	public void onClick(View v) {

		Intent myIntent;
		switch (v.getId()) {
		case R.id.tvReport:
			myIntent = new Intent(getApplicationContext(), Report.class);
			if (Utility.mFacebook.isSessionValid()) {
				Utility.objectID = "me";
				startActivity(myIntent);
			}
			break;
		case R.id.tvPlaces:
			break;
		case R.id.tvStats:
			myIntent = new Intent(getApplicationContext(), Stats.class);
			startActivity(myIntent);
			break;
			
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// initialization of all the objects
		setContentView(R.layout.places);
		Init();

		updateLocation();
		goBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Location theLoc;
				try {
					if (!near_me) {
						input_str = addressInput.getText().toString();
						if (input_str.compareTo("")==0) {
							Toast.makeText(Places.this,
									"Please enter an address first",
									Toast.LENGTH_SHORT).show();
						
							return;
						}
						fixAddress();
						userLocation = mGooglePlacesAPI.mGeoEng
								.getLocationFromAddress(input_str);
						theLoc = userLocation;
					} else {
						if (!mLocEng.isLocationEnabled()) {
							Toast.makeText(Places.this, "Location is unknown",
									Toast.LENGTH_SHORT).show();
							return;
						}
						if (!mLocEng.isLocationSent()) {
							Log.i("ERIC", "latched");
							latch = new CountDownLatch(1);
							updateLocation();
							latch.await();
						}
						theLoc = mLocation;
					}
					if (theLoc != null) {
						mProgress.setMessage("Retrieving nearby places...");
						loadNearbyPlaces(theLoc, false, null,
								GooglePlacesAPI.LOOK_AROUND_RADIUS);
					} else
						Toast.makeText(Places.this, "Location is unknown",
								Toast.LENGTH_SHORT).show();
				} catch (Exception ex) {
					Toast.makeText(Places.this,
							"Something bad happened: " + ex.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		searchBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String searchStr = etSearch.getText().toString();
				if (searchStr.compareTo("")==0)
					return;
				try {
					mProgress.setMessage("Searching for places...");
					if (!near_me) {
						input_str = addressInput.getText().toString();
						if (input_str.compareTo("")==0) {
							Toast.makeText(Places.this,
									"Please enter an address first",
									Toast.LENGTH_SHORT).show();
						
							return;
						}
						fixAddress();
						userLocation = mGooglePlacesAPI.mGeoEng
								.getLocationFromAddress(input_str);
						loadNearbyPlaces(userLocation, true, searchStr,
								GooglePlacesAPI.MAX_RADIUS);
						Log.i("ERIC", "searching near " + input_str + ", "
								+ userLocation.getLatitude() + ","
								+ userLocation.getLongitude());
					} else {
						loadNearbyPlaces(mLocation, true, searchStr,
								GooglePlacesAPI.MAX_RADIUS);
					}
				} catch (Exception ex) {
					Toast.makeText(Places.this,
							"Something bad happened: " + ex.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		mShowMeOnMap.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					Uri mUri = Uri.parse("geo:0,0?q=" + mLocation.getLatitude()
							+ "," + mLocation.getLongitude());
					Intent i = new Intent(Intent.ACTION_VIEW, mUri);
					startActivity(i);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		mRefreshButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					mLocEng = null;
					mLocEng = new LocationEngine(context);
					updateLocation();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		btnCategories.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							context);
					builder.setTitle("Categories");
					m_chosen_cats = mGooglePlacesAPI.chosen_cats;
					builder.setMultiChoiceItems(R.array.types_array,
							mGooglePlacesAPI.chosen_cats,
							new OnMultiChoiceClickListener() {
								@Override
								public void onClick(DialogInterface arg0,
										int arg1, boolean arg2) {
									m_chosen_cats[arg1] = arg2;

								}
							});
					builder.setNeutralButton("Apply",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialogInterface,
										int item) {
									mGooglePlacesAPI
											.setChosenCats(m_chosen_cats);
									mGooglePlacesAPI.getChosenCats();
								}
							});
					builder.create().show();
				} catch (Throwable t) {
					t.printStackTrace();
				}

			}
		});
	
	}


	
	
	public void updateLocation() {
		mProgress.setMessage("Retrieving location...");
		mProgress.show();
		new Thread() {
			@Override
			public void run() {
				int what = M_LOC_OK;
				Looper.prepare();
				Log.i("ERIC", "upc loc thread");
				try {
					mLocEng = new LocationEngine(context);
					mLocation = null;
					int counter = 0;
					if (!mLocEng.isServiceEnabled()) {
						what = M_LS_DOWN;
					} else {
						while ((mLocation == null)
								&& (counter < CYCLES_TO_WAIT)) {
							mLocation = mLocEng.getCurrentLocation();
							if (mLocation != null)
								break;
							sleep(1000);
							counter++;
							Log.i("ERIC", "counter=" + counter + "mloc ok: " + (mLocation != null));
						}
						if (mLocation == null) {
							mLocation = mLocEng.getLastKnownLocation();
						}
						if (mLocation == null) {
							mHandler.sendMessage(mHandler.obtainMessage(M_LOC_NA));
							mLocEng.setDebugLocation();
							mLocation = mLocEng.getCurrentLocation();
						}
						counter = 0;
						while ((!mGooglePlacesAPI.mGeoEng.addressEnabled)
								&& (counter < CYCLES_TO_WAIT)) {
							mGooglePlacesAPI.mGeoEng
									.getAddressFromLocation(mLocation);
							if (mGooglePlacesAPI.mGeoEng.addressEnabled)
								break;
							sleep(1000);
							counter++;
							Log.i("ERIC", "counter address=" + counter);
						}
					}
				} catch (Exception e) {
					what = M_UPD_LOC_ERR;
					e.printStackTrace();
				}
				latch.countDown();
				mHandler.sendMessage(mHandler.obtainMessage(what));
			}
		}.start();
		Log.i("ERIC", "upd loc thread started");
	}

	private void loadNearbyPlaces(final Location location, final boolean query,
			final String searchStr, final int radius) {
		mProgress.show();
		new Thread() {
			@Override
			public void run() {
				int what = M_GET_PLACES_OK;
				Log.i("ERIC", mGooglePlacesAPI.getChosenCatsStr());
				Looper.prepare();
				try {
					if (query == true) {
						Log.i("ERIC", searchStr);
						mNearbyList = mGooglePlacesAPI.searchPlaces(location,
								mLocEng.isLocationEnabled(), searchStr, radius, true);
					} else
						mNearbyList = mGooglePlacesAPI.getNearby(location,
								radius, true);

				} catch (Throwable e) {
					what = M_GET_PLACES_ERR;
					e.printStackTrace();
				}
				mHandler.sendMessage(mHandler.obtainMessage(what));
			}
		}.start();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.i("ERIC", "what: " + msg.what);
			switch (msg.what) {
			case M_LS_DOWN:
				Toast.makeText(Places.this, "Location Service is unavailable",
						Toast.LENGTH_LONG).show();

				break;
			case M_UPD_LOC_ERR:
				Toast.makeText(Places.this, "Error while getting location",
						Toast.LENGTH_LONG).show();
				mShowMeOnMap.setVisibility(View.INVISIBLE);
				break;
			case M_LOC_NA:
				Toast.makeText(
						context,
						"Location couldn't be determined. Putting you somewhere in Tel Aviv...",
						Toast.LENGTH_SHORT).show();
				break;
			case M_LOC_OK:
				if (mLocation != null) {
					tvAddress.setText(mGooglePlacesAPI.mGeoEng
							.getAddressFromLocation(mLocation));
					Log.i("ERIC", "location: " + mLocation.toString());
					goBtn.setEnabled(true);
					mShowMeOnMap.setVisibility(View.VISIBLE);
				} else
					Toast.makeText(Places.this, "Unable to get location",
							Toast.LENGTH_LONG).show();
				break;

			case M_GET_PLACES_OK:
				if (mNearbyList.size() == 0) {
					Toast.makeText(Places.this, "No places", Toast.LENGTH_LONG)
							.show();
					break;
				}
				mAdapter.setData(mNearbyList);
				mListView.setAdapter(mAdapter);
				break;

			case M_GET_PLACES_ERR:
				Toast.makeText(Places.this, "Failed to load places",
						Toast.LENGTH_LONG).show();
				break;
			case M_GET_SUGGESTIONS_OK:
				addressInput.setAdapter(ac_adapter);
				try {
					addressInput.showDropDown();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			}
			latch.countDown();
			mProgress.dismiss();
			Log.i("ERIC", "unlatched");
		}
	};
	
	private void fixAddress() {
		input_str = mGooglePlacesAPI.mGeoEng.getAddressFromAddress(input_str);
		addressInput.setText(input_str);
	}

	
	//  added---------------------------------------------------------------------
	private void showQuestionDialog(View v) {
		AlertDialog alertDialog = new AlertDialog.Builder(v.getContext()).create();
		alertDialog.setTitle(" Places around a specific location");
		String str= "By choosing the Places category, you can get a list of pubs, bars, restaurants and etc.\n" 
				+ " Every place will be presented with its current rating.\n";

		alertDialog.setMessage(str);
		
		 alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		       // here you can add functions
		    }
		 });
		 alertDialog.setIcon(R.drawable.qm);
		 alertDialog.show();
		 
	} 
}
