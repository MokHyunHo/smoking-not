package com.facebook.android;

import android.content.Context;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ImageView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class NearbyAdapter extends BaseAdapter {
	private ArrayList<FsqVenue> mVenueList;
	private LayoutInflater mInflater;

	public NearbyAdapter(Context c) {
        mInflater 			= LayoutInflater.from(c);
    }

	public void setData(ArrayList<FsqVenue> poolList) {
		mVenueList = poolList;
	}
	
	@Override
	public int getCount() {
		return mVenueList.size();
	}

	@Override
	public Object getItem(int position) {
		return mVenueList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}


	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		Random rnd = new Random();
		
		if (convertView == null) {
			convertView	=  mInflater.inflate(R.layout.nearby_list, null);
			
			holder = new ViewHolder();
			
			holder.mNameTxt 		= (TextView) convertView.findViewById(R.id.tv_name);
			holder.mAddressTxt 		= (TextView) convertView.findViewById(R.id.tv_address);
			//holder.mHereNowTxt 		= (TextView) convertView.findViewById(R.id.tv_here_now);
			holder.mDistanceTxt 	= (TextView) convertView.findViewById(R.id.tv_distance);
			holder.mRaiting			= (ProgressBar) convertView.findViewById(R.id.pb_Raiting);
			//holder.mRibbonImg		= (ImageView) convertView.findViewById(R.id.iv_ribbon);
			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		FsqVenue venue 	= mVenueList.get(position);
	
		holder.mNameTxt.setText(venue.name);
		holder.mAddressTxt.setText(venue.address);
		//holder.mHereNowTxt.setText("(" + String.valueOf(venue.herenow) + " people here)");
		holder.mDistanceTxt.setText(formatDistance((double)venue.distance));
		//holder.mDistanceTxt.setText(String.valueOf(venue.distance));
		holder.mRaiting.setProgress(rnd.nextInt(holder.mRaiting.getMax()));
		Log.i("ERIC", String.valueOf(holder.mRaiting.getProgress()));

		//holder.mRibbonImg.setVisibility((venue.type.equals("trending")) ? View.VISIBLE : View.INVISIBLE);
	
        return convertView;
	}
	
	private String formatDistance(double distance) {
		String result = "";
		
		DecimalFormat dF = new DecimalFormat("00");
		
		dF.applyPattern("0.#");
		
		if (distance < 1000)
			result = dF.format(distance) + " m";
		else {
			distance = distance / 1000.0;
			result   = dF.format(distance) + " km";
		}
		
		return result;
	}
	static class ViewHolder {
		TextView mNameTxt;
		TextView mAddressTxt;
		TextView mDistanceTxt;
		ProgressBar mRaiting;
	}
}