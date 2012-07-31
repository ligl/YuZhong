package cn.amose.yuzhong;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
		ListView lvMain = (ListView) mBehindView.findViewById(R.id.lv_main);
		final List<String> data = new ArrayList<String>();
		data.add("abc");
		data.add("string");
		data.add("s33");
		data.add("abc");
		data.add("string");
		data.add("s33");
		data.add("abc");
		data.add("string");
		data.add("s33");
		data.add("abc");
		data.add("string");
		data.add("s33");
		lvMain.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, data));
		lvMain.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				Toast.makeText(getApplicationContext(), data.get(position),
						Toast.LENGTH_SHORT).show();

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
}