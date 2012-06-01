package com.facebook.android;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.opengl.Visibility;
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

public class ReportsListAdapter extends BaseAdapter {
	private ArrayList<String[]> mLst;
	private LayoutInflater mInflater;
	private Context caller;

	private void init(Context c) {
		mInflater = LayoutInflater.from(c);
		caller = c;
	}

	public ReportsListAdapter(Context c) {
		init(c);
	}

	public void setData(ArrayList<String[]> poolList) {
		//Log.i("ERIC", "X size: " + poolList.size());
		mLst = poolList;
		/*for (int j = 0; j < mLst.size(); j++)
		{
			Log.i("ERIC", mLst.get(j)[0]);
		}
		*/
	}

	@Override
	public int getCount() {
		return mLst.size();
	}

	@Override
	public Object getItem(int position) {
		return mLst.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		String[] report = mLst.get(position);
		Log.i("ERIC", "position: " + position + "string[]: " + report.toString());
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.reports_list, null);

			holder = new ViewHolder();

			holder.mTypeTxt = (TextView) convertView.findViewById(R.id.tvReportType);
			holder.mDateTxt = (TextView) convertView
					.findViewById(R.id.tvReportDate);

			holder.mDetailsTxt = (TextView) convertView
					.findViewById(R.id.tvReportDetails);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		convertView.setTag(holder);

		holder.position = position;
		try {
			holder.mTypeTxt.setText(report[0]);
			holder.mDateTxt.setText(report[1]);
			holder.mDetailsTxt.setText(report[2]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return convertView;
	}

	

	static class ViewHolder {
		int position;
		TextView mTypeTxt;
		TextView mDateTxt;
		TextView mDetailsTxt;

	}
}