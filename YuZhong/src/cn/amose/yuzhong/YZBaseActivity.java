package cn.amose.yuzhong;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.emilsjolander.components.StickyListHeaders.StickyListHeadersBaseAdapter;
import com.emilsjolander.components.StickyListHeaders.StickyListHeadersListView;
import com.slidingmenu.lib.SlidingMenu;

public abstract class YZBaseActivity extends Activity {
	private SlidingMenu mSlidingMenu;
	protected LayoutInflater mLayoutInflater;
	private View mBehindView;
	private View mAboveView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.yzbase);
		mLayoutInflater = getLayoutInflater();
		mSlidingMenu = (SlidingMenu) super.findViewById(R.id.sm_zbase);
		mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		mBehindView = mLayoutInflater.inflate(R.layout.main, null);
		mSlidingMenu.setViewBehind(mBehindView);
		StickyListHeadersListView lvMain = (StickyListHeadersListView) mBehindView
				.findViewById(R.id.lv_main);

		lvMain.setAdapter(new TestBaseAdapter(this));
		lvMain.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Toast.makeText(getApplicationContext(),
						"click position: " + position, Toast.LENGTH_SHORT)
						.show();

			}

		});
	}

	@Override
	public void setContentView(int layoutResID) {
		mAboveView = mLayoutInflater.inflate(layoutResID, null);
		mSlidingMenu.setViewAbove(mAboveView);
	}

	@Override
	public View findViewById(int id) {
		return mAboveView.findViewById(id);
	}

	public SlidingMenu getSlidingMenu() {
		return mSlidingMenu;
	}

	private class TestBaseAdapter extends StickyListHeadersBaseAdapter {

		private String[] countries;
		private LayoutInflater inflater;

		public TestBaseAdapter(Context context) {
			super(context);
			inflater = LayoutInflater.from(context);
			countries = context.getResources()
					.getStringArray(R.array.countries);
		}

		public int getCount() {
			return countries.length;
		}

		public Object getItem(int position) {
			return countries[position];
		}

		public long getItemId(int position) {
			return position;
		}

		// remember that these have to be static, postion=1 should walys return
		// the
		// same Id that is.
		@Override
		public long getHeaderId(int position) {
			// return the first character of the country as ID because this is
			// what
			// headers are based upon
			return countries[position].subSequence(0, 1).charAt(0);
		}

		// works in the same way as getview()
		@Override
		public View getHeaderView(int position, View convertView) {
			HeaderViewHolder holder;

			if (convertView == null) {
				holder = new HeaderViewHolder();
				convertView = inflater.inflate(R.layout.header, null);
				holder.text = (TextView) convertView.findViewById(R.id.text);
				convertView.setTag(holder);
			} else {
				holder = (HeaderViewHolder) convertView.getTag();
			}

			// set header text as first char in name
			holder.text.setText(countries[position].subSequence(0, 1));

			return convertView;
		}

		class HeaderViewHolder {
			TextView text;
		}

		// note that i do not ovveride getView(int position, View convertView,
		// ViewGroup parent);
		// as this would break listheader functionality
		@Override
		protected View getView(int position, View convertView) {
			ViewHolder holder;

			if (convertView == null) {
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.test_list_item_layout,
						null);
				holder.text = (TextView) convertView.findViewById(R.id.text);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.text.setText(countries[position]);

			return convertView;
		}

		class ViewHolder {
			TextView text;
		}

	}

}