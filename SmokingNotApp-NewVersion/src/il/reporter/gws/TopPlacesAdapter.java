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
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.facebook.android.R;

public class TopPlacesAdapter extends BaseAdapter {
	private List<LocationRequest> mPlacesList;
	private LayoutInflater mInflater;
	private Context caller;

	private void init(Context c) {
		mInflater = LayoutInflater.from(c);
		caller = c;
	}

	public TopPlacesAdapter(Context c) {
		init(c);
	}

	public void setData(List<LocationRequest> poolList) {
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

		final LocationRequest place = mPlacesList.get(position);

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.top_places_list, null);

			holder = new ViewHolder();

			holder.mNameTxt = (TextView) convertView.findViewById(R.id.tv_name);
			holder.mAddressTxt = (TextView) convertView
					.findViewById(R.id.tv_address);
			holder.mRaiting = (ProgressBar) convertView
					.findViewById(R.id.pb_Raiting);
			holder.mRaiting.setProgressDrawable(caller.getResources()
					.getDrawable(R.drawable.my_progress));
			holder.mInfo = (ImageButton) convertView.findViewById(R.id.ib_Info);
			holder.mNumberRatings = (TextView) convertView
					.findViewById(R.id.tv_raitings);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		convertView.setTag(holder);

		try {
			holder.position = position;
			holder.mNameTxt.setText(position + 1 + ". " + place.getName());
			holder.mAddressTxt.setText(place.getAddress());

			int num_raitings = place.getBadRate() + place.getGoodRate();

			String num_r;

			if (num_raitings > 0) {
				double rating = ((double) place.getGoodRate() / ((double) place.getBadRate() + (double) place.getGoodRate())) * 100;
				holder.mRaiting.setVisibility(View.VISIBLE);
				num_r = "Likes: " + place.getGoodRate() + ", Dislikes: "
						+ place.getBadRate();
				holder.mRaiting.setProgress((int) rating);
			} else {
				holder.mRaiting.setVisibility(View.INVISIBLE);
				num_r = "No reports for this place (Strange!!)";
			}
			Log.i("ERIC", "Place raitings: " + num_raitings);
			holder.mNumberRatings.setText(num_r);

			holder.mInfo.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(caller, PlaceDetails.class);
					Bundle bundle = new Bundle();
					bundle.putString("PlaceID", place.getId());
					bundle.putString("PlaceName", place.getName());
					bundle.putString("PlaceAddress", place.getAddress());
					bundle.putInt("GoogRate", place.getGoodRate());
					bundle.putInt("BadRate", place.getBadRate());
					Location loc = new Location(LocationManager.PASSIVE_PROVIDER);
					loc.setLatitude(place.getLatitude());
					loc.setLongitude(place.getLongitude());
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
		TextView mAddressTxt;
		TextView mNumberRatings;
		ProgressBar mRaiting;
		ImageButton mInfo;
	}

}