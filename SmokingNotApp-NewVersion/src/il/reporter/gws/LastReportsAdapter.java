package il.reporter.gws;

import android.content.Context;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import il.reporter.gws.R;
import com.google.gson.Gson;

public class LastReportsAdapter extends BaseAdapter {
	private LastReports mLst;
	private LayoutInflater mInflater;
	private Context caller;
	private boolean showRank;

	private void init(Context c) {
		mInflater = LayoutInflater.from(c);
		caller = c;
	}

	public LastReportsAdapter(Context c, boolean showRank) {
		init(c);
		this.showRank = showRank;
	}

	public void setData(LastReports poolList) {
		mLst = poolList;
	}

	@Override
	public int getCount() {
		return mLst.getLst().size();
	}

	@Override
	public Object getItem(int position) {
		return mLst.getLst().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		final ReportDetails report = mLst.getLst().get(position);
		Log.i("ERIC",
				"position: " + position + "string[]: " + report.toString());
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.last_reports_list, null);

			holder = new ViewHolder();

			holder.mNameTxt = (TextView) convertView
					.findViewById(R.id.tvPlaceName);
			holder.mTypeTxt = (TextView) convertView
					.findViewById(R.id.tvReportType);
			holder.mDateTxt = (TextView) convertView
					.findViewById(R.id.tvReportDate);
			holder.mCommentTxt = (TextView) convertView
					.findViewById(R.id.tvReportComment);
			holder.ibDetails = (ImageButton) convertView
					.findViewById(R.id.ib_Info);

			holder.mUserRankTxt = (TextView) convertView
					.findViewById(R.id.tvUserRank);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		convertView.setTag(holder);

		holder.position = position;
		try {
			holder.mNameTxt.setText(report.getPlaceName());
			holder.mTypeTxt.setText(report.getReportKind());
			holder.mDateTxt.setText(report.getDate());
			holder.mCommentTxt
					.setText(report.getComment().compareTo("") == 0 ? "No comment"
							: report.getComment());

			if (showRank)
				holder.mUserRankTxt.setText(" - by " + report.getUserRank()
						+ " user");
			else
				holder.mUserRankTxt.setHeight(0);

			holder.ibDetails.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					WebRequest req = new WebRequest();
					LocationRequest loc_updated = null;
					Gson gson1 = new Gson();
					String str;
					try {
						JSONObject json2 = req.readJsonFromUrl(caller
								.getString(R.string.DatabaseUrl)
								+ "/GetLocation?locationid="
								+ report.getLocationId());
						str = (String) json2.get("location_req");
						Log.w("str=", str);
						if (str.compareTo("NotinDataBase") != 0)
							loc_updated = gson1.fromJson(str,
									LocationRequest.class);
					} catch (JSONException e) {
						Log.e("NearbyAdapter error, can't get response from server, JSON exception",
								e.toString());
					} catch (Exception e) {
						Log.e("NearbyAdapter error, can't get response from server",
								e.toString());
					}

					Intent intent = new Intent(caller, PlaceDetails.class);
					Bundle bundle = new Bundle();
					bundle.putString("PlaceID", loc_updated.getId());
					bundle.putString("PlaceName", loc_updated.getName());
					bundle.putString("PlaceAddress", loc_updated.getAddress());
					bundle.putInt("GoogRate", loc_updated.getGoodRate());
					bundle.putInt("BadRate", loc_updated.getBadRate());
					Location loc = new Location(
							LocationManager.PASSIVE_PROVIDER);
					loc.setLatitude(loc_updated.getLatitude());
					loc.setLongitude(loc_updated.getLongitude());
					bundle.putParcelable("PlaceLocation", loc);
					intent.putExtras(bundle);
					caller.startActivity(intent);

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}

	static class ViewHolder {
		int position;
		TextView mNameTxt;
		TextView mTypeTxt;
		TextView mDateTxt;
		TextView mCommentTxt;
		TextView mUserRankTxt;
		ImageButton ibDetails;

	}
}