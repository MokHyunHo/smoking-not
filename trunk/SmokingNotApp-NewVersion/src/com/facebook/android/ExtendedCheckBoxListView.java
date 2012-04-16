package com.facebook.android;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;


public class ExtendedCheckBoxListView extends LinearLayout {
	private TextView mText;
	private CheckBox mCheckBox;
	private ExtendedCheckBox mCheckBoxText;

	public ExtendedCheckBoxListView(Context context,
			ExtendedCheckBox aCheckBoxifiedText) {
		super(context);
		// Set orientation to be horizontal
		this.setOrientation(HORIZONTAL);
		mCheckBoxText = aCheckBoxifiedText;
		mCheckBox = new CheckBox(context);
		mCheckBox.setPadding(0, 0, 20, 0);
		
		// Set the initial state of the checkbox.
		mCheckBox.setChecked(aCheckBoxifiedText.getChecked());

		// Set the right listener for the checkbox, used to update
		// our data holder to change it's state after a click too
		mCheckBox.setOnClickListener(new OnClickListener()
		{
			/**
			 * 
			 * When clicked change the state of the 'mCheckBoxText' too!
			 */
			@Override
			public void onClick(View v) {
				mCheckBoxText.setChecked(getCheckBoxState());
			}
		});

		// Add the checkbox
		addView(mCheckBox, new LinearLayout.LayoutParams(
		LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		mText = new TextView(context);
		mText.setText(aCheckBoxifiedText.getText());
		addView(mText, new LinearLayout.LayoutParams(
		LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		// Remove some controls in order to prevent a strange flickering when
		// clicking on the TextView!

		mText.setClickable(false);
		mText.setFocusable(false);
		mText.setFocusableInTouchMode(false);
		setOnClickListener(new OnClickListener()
		{

			/**
			 * 
			 * Check or unchecked the current checkbox!
			 */

			@Override
			public void onClick(View v) {
				toggleCheckBoxState();
			}
		});
	}
	public void setText(String words) {
		mText.setText(words);
	}
	public void toggleCheckBoxState()
	{
		setCheckBoxState(!getCheckBoxState());
	}
	public void setCheckBoxState(boolean bool)
	{
		mCheckBox.setChecked(bool);
		mCheckBoxText.setChecked(bool);
	}
	public boolean getCheckBoxState()
	{
		return mCheckBox.isChecked();
	}
}
