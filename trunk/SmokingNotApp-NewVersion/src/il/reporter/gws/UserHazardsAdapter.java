package il.reporter.gws;

import android.content.Context;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import il.reporter.gws.R;

public class UserHazardsAdapter extends BaseAdapter {
	private LastUserHazards mLst;
	private LayoutInflater mInflater;
	private Context caller;

	private void init(Context c) {
		mInflater = LayoutInflater.from(c);
		caller = c;
	}

	public UserHazardsAdapter(Context c) {
		init(c);
	}

	public void setData(LastUserHazards lstHazards) {
		mLst = lstHazards;
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

		final HazardRequest hazard = mLst.getLst().get(position);

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.user_hazards_list, null);

			holder = new ViewHolder();

			holder.mAddressTxt = (TextView) convertView
					.findViewById(R.id.tvAddress);
			holder.mDateTxt = (TextView) convertView
					.findViewById(R.id.tvReportDate);
			holder.mCommentTxt = (TextView) convertView
					.findViewById(R.id.tvReportComment);
			holder.ibShowOnMap = (ImageButton) convertView
					.findViewById(R.id.ib_ShowOnMap);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		convertView.setTag(holder);

		holder.position = position;
		try {
			holder.mAddressTxt.setText(hazard.getAddress());
			holder.mDateTxt.setText(hazard.getDate());
			holder.mCommentTxt.setText(hazard.getComment());

			holder.ibShowOnMap.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					try {
						Uri mUri = Uri.parse("geo:0,0?q="
								+ hazard.getLatitude() + ","
								+ hazard.getLongitude());
						Intent i = new Intent(Intent.ACTION_VIEW, mUri);
						caller.startActivity(i);
						
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}

	static class ViewHolder {
		int position;
		TextView mAddressTxt;
		TextView mDateTxt;
		TextView mCommentTxt;
		ImageButton ibShowOnMap;

	}
}