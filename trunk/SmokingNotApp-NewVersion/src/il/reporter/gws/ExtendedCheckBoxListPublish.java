package il.reporter.gws;

import com.facebook.android.R;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class ExtendedCheckBoxListPublish extends ListActivity implements View.OnClickListener{
	private ExtendedCheckBoxListAdapter mListAdapter;
	private Button select;
	private final int NumOfWays=4;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// initialization of all the objects
		setContentView(R.layout.listmain);
		select = (Button) findViewById(R.id.bSelect);
		
		// initialization of the list options
		mListAdapter = new ExtendedCheckBoxListAdapter(this);
		for (int i = 1; i < NumOfWays; i++) {
			String newItem = "Publishing way Num. " + i;
			// adding a new checkbox (with the string, unchecked) to the list
			mListAdapter.addItem(new ExtendedCheckBox(newItem, false));
		}
		setListAdapter(mListAdapter);
		
		select.setOnClickListener(this);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (v != null) {
			ExtendedCheckBoxListView CurrentView = (ExtendedCheckBoxListView) v;
			if (CurrentView != null) {
				CurrentView.toggleCheckBoxState();
			}
		}
		super.onListItemClick(l, v, position, id);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.bSelect:
			//passing values to the 'Send' action
			String[] checked=new String[NumOfWays];
			ExtendedCheckBox cb;
			int j=0;
			for(int i=0;i<mListAdapter.getCount();i++)
			{
				cb=(ExtendedCheckBox) mListAdapter.getItem(i);
				if(cb.getChecked())
				{
					checked[j]=cb.getText();
					j++;
				}
			}
			Bundle returnBundle=new Bundle();
			returnBundle.putStringArray("publishingOptions", checked);
			Intent int_a=new Intent(getApplicationContext(),Report.class);
			int_a.putExtras(returnBundle);
			startActivity(int_a);
			break;
		}
		
	}
}
