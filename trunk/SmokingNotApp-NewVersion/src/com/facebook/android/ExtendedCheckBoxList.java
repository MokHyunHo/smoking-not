package com.facebook.android;

import com.facebook.android.R;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class ExtendedCheckBoxList extends ListActivity implements
		View.OnClickListener {

	private ExtendedCheckBoxListAdapter mListAdapter;
	private Button select;
	private int NumOfOptions;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// initialization of all the objects
		setContentView(R.layout.listmain);
		select = (Button) findViewById(R.id.bSelect);

		// initialization of the list options
		mListAdapter = new ExtendedCheckBoxListAdapter(this);

		// adding a new checkbox (with the string, unchecked) to the list
		String[] items = getResources().getStringArray(
				R.array.complaint_reasons);
		NumOfOptions = items.length;
		for (String str : items) {
			mListAdapter.addItem(new ExtendedCheckBox(str, false));
		}

		setListAdapter(mListAdapter);

		select.setOnClickListener(this);
	}

	
	public void onPause(Bundle savedInstanceState) {
		super.onPause();
		finish();
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
			// passing values to the 'Send' action
			String[] checked = new String[NumOfOptions];
			ExtendedCheckBox cb;
			int j = 0;
			for (int i = 0; i < mListAdapter.getCount(); i++) {
				cb = (ExtendedCheckBox) mListAdapter.getItem(i);
				if (cb.getChecked()) {
					checked[j] = cb.getText();
					j++;
				}
			}
			Intent int_a = new Intent();
			Log.i("ERIC", checked.toString());
			int_a.putExtra("checkedOptions", checked);
			
			setResult(Activity.RESULT_OK, int_a);
			finish();
			break;
		}

	}
}