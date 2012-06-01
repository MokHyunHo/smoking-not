package com.facebook.android;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.opengl.Visibility;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

public class NearbyAdapter extends BaseAdapter {
	private ArrayList<GooglePlace> mPlacesList;
	private LayoutInflater mInflater;
	private Context caller;
	private boolean isShortAdapter = false;
	private boolean recolor = false;
	private int new_color;

	Random rnd;

	private void init(Context c) {
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

	public void Recolor(int color) {
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
		
		final GooglePlace place = mPlacesList.get(position);
		Log.i("ERIC", "place: " + place.name);
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
				holder.mRaiting.setProgressDrawable(caller.getResources()
						.getDrawable(R.drawable.my_progress));
				holder.mInfo = (ImageButton) convertView
						.findViewById(R.id.ib_Info);
				holder.mNumberRatings = (TextView) convertView
						.findViewById(R.id.tv_raitings);
			}
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (recolor) {

			holder.mNameTxt.setTextColor(new_color);
			holder.mAddressTxt.setTextColor(new_color);

		}

		convertView.setTag(holder);

		try {
			holder.position = position;
			holder.mNameTxt.setText(place.name);
			holder.mAddressTxt.setText(place.vicinity);
			holder.mDistanceTxt
					.setText(formatDistance((double) place.distance));
			if (!isShortAdapter) {

				// add progressbar for ratings
				int num_raitings = place.badRate + place.goodRate;

				String num_r;
				// holder.mRaiting.setMax(num_raitings);
				if (num_raitings > 0) {
					double rating = ((double) place.goodRate / ((double) place.badRate + (double) place.goodRate)) * 100;
					holder.mRaiting.setVisibility(View.VISIBLE);
					num_r = "Likes: " + place.goodRate + ", Dislikes: "
							+ place.badRate;
					holder.mRaiting.setProgress((int) rating);
					Log.i("ERIC", "rating: " + rating);
				} else {
					holder.mRaiting.setVisibility(View.INVISIBLE);
					num_r = "No reports for this place";
				}
				Log.i("ERIC", "Place raitings: " + num_raitings);
				holder.mNumberRatings.setText(num_r);

				holder.mInfo.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Log.i("ERIC", "clicked!");
						Intent intent = new Intent(caller, PlaceDetails.class);
						Bundle bundle = new Bundle();
						bundle.putString("PlaceName", place.name);
						bundle.putString("PlaceAddress", place.vicinity);
						bundle.putInt("GoogRate", place.goodRate);
						bundle.putInt("BadRate", place.badRate);
						bundle.putParcelable("PlaceLocation", place.location);
						intent.putExtras(bundle);
						caller.startActivity(intent);
						
					}
				});
				
				
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}

	private String formatDistance(double distance) {
		String result = "";

		DecimalFormat dF = new DecimalFormat("00");

		if (distance < 1000) {
			dF.applyPattern("0");
			result = dF.format(distance) + " m";
		} else {
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
		ImageButton mInfo;
	}

}