package il.reporter.gws;

import com.facebook.android.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.BaseAdapter;
import android.widget.TextView;

public class ReportsListAdapter extends BaseAdapter {
	private LastReports mLst;
	private LayoutInflater mInflater;
	private Context caller;

	private void init(Context c) {
		mInflater = LayoutInflater.from(c);
		caller = c;
	}

	public ReportsListAdapter(Context c) {
		init(c);
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
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.reports_list, null);

			holder = new ViewHolder();

			holder.mTypeTxt = (TextView) convertView
					.findViewById(R.id.tvReportType);
			holder.mDateTxt = (TextView) convertView
					.findViewById(R.id.tvReportDate);
			holder.mCommentTxt = (TextView) convertView
					.findViewById(R.id.tvReportComment);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		convertView.setTag(holder);

		holder.position = position;
		try {
			holder.mTypeTxt.setText(report.getReportKind());
			holder.mDateTxt.setText(report.getDate());
			holder.mCommentTxt
					.setText(report.getComment().compareTo("") == 0 ? "No comment"
							: report.getComment());
			holder.mUserRankTxt.setText(" - by " + report.getUserRank()
					+ " user");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return convertView;
	}

	static class ViewHolder {
		int position;
		TextView mTypeTxt;
		TextView mDateTxt;
		TextView mCommentTxt;
		TextView mUserRankTxt;

	}
}