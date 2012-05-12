package com.facebook.android;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

public class NearbyAdapter extends BaseAdapter {
	private ArrayList<GooglePlace> mPlacesList;
	private LayoutInflater mInflater;
	private Context caller;
	private boolean isShortAdapter = false;
	private boolean recolor = false;
	private int new_color;
	// rate for a place should be in database
	private int rate = 0;
	Random rnd;
	
	private void init(Context c)
	{
		mInflater = LayoutInflater.from(c);
		caller = c;
	}
	
	public NearbyAdapter(Context c) {
		init(c);
	}

	public NearbyAdapter(Context c, boolean shortAdapter) {
		init(c);
		isShortAdapter = shortAdapter;
	}

	public void Recolor(int color)
	{
		recolor = true;
		new_color = color;
	}
	public void setData(ArrayList<GooglePlace> poolList) {
		mPlacesList = poolList;
	}

	@Override
	public int getCount() {
		return mPlacesList.size();
	}

	@Override
	public Object getItem(int position) {
		return mPlacesList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) {
			if (!isShortAdapter)
				convertView = mInflater.inflate(R.layout.nearby_list, null);
			else
				convertView = mInflater.inflate(R.layout.places_list, null);

			holder = new ViewHolder();

			holder.mNameTxt = (TextView) convertView.findViewById(R.id.tv_name);
			holder.mAddressTxt = (TextView) convertView
					.findViewById(R.id.tv_address);
			
			holder.mDistanceTxt = (TextView) convertView
					.findViewById(R.id.tv_distance);
			if (!isShortAdapter) {
				holder.mRaiting = (ProgressBar) convertView
						.findViewById(R.id.pb_Raiting);
				holder.mShowOnMap = (ImageButton) convertView
						.findViewById(R.id.ib_ShowOnMap);
				holder.mNumberRatings = (TextView) convertView
						.findViewById(R.id.tv_raitings);
			}
			
			if (recolor)
			{
				
				holder.mNameTxt.setTextColor(new_color);
				holder.mAddressTxt.setTextColor(new_color);
				
			}
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final GooglePlace place = mPlacesList.get(position);
		Log.i("ERIC", "place: " + place.name);
		holder.position = position;
		holder.mNameTxt.setText(place.name);
		holder.mAddressTxt.setText(place.vicinity);
		holder.mAddressTxt.setTextColor(Color.BLACK);
		holder.mDistanceTxt.setText(formatDistance((double) place.distance));
		if (!isShortAdapter) {
			// find rating of corresponding place
			
			try {
				holder.mNumberRatings.setText("Number of raitings: " + String.valueOf(rnd.nextInt(10)));

			} catch (Exception Ex) {
				Log.i("ERIC", "Bad! places ratings... " + Ex.getMessage());
			}
			holder.mShowOnMap.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					try {
						Uri mUri = Uri.parse("geo:0,0?q="
								+ place.location.getLatitude() + ","
								+ place.location.getLongitude());
						Log.i("Eric", mUri.toString());
						Intent i = new Intent(Intent.ACTION_VIEW, mUri);
						caller.startActivity(i);
					} catch (Throwable t) {
						;
					}

				}
			});

			Log.i("ERIC", String.valueOf(holder.mRaiting.getProgress()));
		}
		return convertView;
	}

	private String formatDistance(double distance) {
		String result = "";

		DecimalFormat dF = new DecimalFormat("00");

		if (distance < 1000)
		{
			dF.applyPattern("0");
			result = dF.format(distance) + " m";
		}
		else {
			dF.applyPattern("0.#");
			distance = distance / 1000.0;
			result = dF.format(distance) + " km";
		}

		return result;
	}

	static class ViewHolder {
		int position;
		TextView mNameTxt;
		TextView mAddressTxt;
		TextView mDistanceTxt;
		TextView mNumberRatings;
		ProgressBar mRaiting;
		ImageButton mShowOnMap;
	}
}