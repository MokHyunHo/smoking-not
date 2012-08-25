package il.reporter.gws;

import android.content.*;
import android.view.*;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class MainSmoking extends TabActivity {
	
	private TabHost mTabHost;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainsmoking);

		/*TabHost tabHost = getTabHost();
		
		// Tab for Smoking Report
        TextView tview1=new TextView(this); 
        tview1.setText("Report"); 
        tview1.setHeight(60);
        tview1.setGravity(Gravity.CENTER);
        TabSpec reportspec = tabHost.newTabSpec("Report");
        // setting Title and Icon for the Tab
        reportspec.setIndicator(tview1);
        Intent reportIntent = new Intent(this, Report.class);
        reportspec.setContent(reportIntent);
 
        // Tab for Places
        TextView tview2=new TextView(this); 
        tview2.setText("Places"); 
        tview2.setHeight(60);
        tview2.setGravity(Gravity.CENTER);
        TabSpec placesspec = tabHost.newTabSpec("Places");
        placesspec.setIndicator(tview2);
        Intent placesIntent = new Intent(this, Places.class);
        placesspec.setContent(placesIntent);
 
        // Tab for Videos
        TextView tview3=new TextView(this); 
        tview3.setText("Stats"); 
        tview3.setHeight(60);
        tview3.setGravity(Gravity.CENTER);
        TabSpec statsspec = tabHost.newTabSpec("Stats");
        statsspec.setIndicator(tview3);
        Intent statsIntent = new Intent(this, Stats.class);
        statsspec.setContent(statsIntent);
 
        // Adding all TabSpec to TabHost
        tabHost.addTab(reportspec); // Adding report tab
        tabHost.addTab(placesspec); // Adding places tab
        tabHost.addTab(statsspec); // Adding stats tab*/
        
		setupTabHost();
		mTabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);

		TabSpec reportspec = mTabHost.newTabSpec("Report");
	    // setting Title and Icon for the Tab
		View tabview1 = createTabView(mTabHost.getContext(), "Report");
	    reportspec.setIndicator(tabview1);
	    Intent reportIntent = new Intent(this, Report.class);
	    reportspec.setContent(reportIntent);
		
		TabSpec placesspec = mTabHost.newTabSpec("Places");
		View tabview2 = createTabView(mTabHost.getContext(), "Places");
        placesspec.setIndicator(tabview2);
        Intent placesIntent = new Intent(this, Places.class);
        placesspec.setContent(placesIntent);
        
        TabSpec statsspec = mTabHost.newTabSpec("Stats");
		View tabview3 = createTabView(mTabHost.getContext(), "Stats");
        statsspec.setIndicator(tabview3);
        Intent statsIntent = new Intent(this, Stats.class);
        statsspec.setContent(statsIntent);

		mTabHost.addTab(reportspec); // Adding report tab
		mTabHost.addTab(placesspec); // Adding places tab
		mTabHost.addTab(statsspec); // Adding stats tab
		
		
	}
	
	private void setupTabHost() {
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
	}

	private static View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
	}
}