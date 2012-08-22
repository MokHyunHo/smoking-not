package il.reporter.gws;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ExtendedCheckBoxListAdapter extends BaseAdapter {
	/** Remember our context so we can use it when constructing views. */
	
	private Context mContext;
	private List<ExtendedCheckBox> mItems = new ArrayList<ExtendedCheckBox>();

	public ExtendedCheckBoxListAdapter(Context context) {
		mContext = context;
	}

	public void addItem(ExtendedCheckBox it) {
		mItems.add(it);
	}

	public void setListItems(List<ExtendedCheckBox> lit) {
		mItems = lit;
	}

	public int getCount() {
		return mItems.size();
	}

	public Object getItem(int position) {
		return mItems.get(position);
	}

	public int GetPosition(ExtendedCheckBox item) {
		int count = getCount();
		for (int i = 0; i < count; i++)
		{
			if (item.compareTo((ExtendedCheckBox) getItem(i)) == 0)
				return i;
		}
		return -1;
	}

	public void setChecked(boolean value, int position) {
		mItems.get(position).setChecked(value);
	}

	public void selectAll() {
		for (ExtendedCheckBox cboxtxt : mItems)
			cboxtxt.setChecked(true);
		/* Things have changed, do a redraw. */
		this.notifyDataSetInvalidated();
	}
	
	public void deselectAll() {
		for (ExtendedCheckBox cboxtxt : mItems)
			cboxtxt.setChecked(false);
		/* Things have changed, do a redraw. */
		this.notifyDataSetInvalidated();
	}

	public long getItemId(int position) {
		return position;
	}
	public View getView(int position, View convertView, ViewGroup parent) {
		return new ExtendedCheckBoxListView(mContext, mItems.get(position));
	}

}