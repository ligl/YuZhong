package cn.amose.yuzhong.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.amose.yuzhong.R;

public class PullToRefreshListView extends PullToRefreshBase<ListView> {
	private ProgressBar footerProgress;
	private TextView footerText;
	private ListView lv;

	public PullToRefreshListView(Context context) {
		super(context);
	}

	public PullToRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected final ListView createAdapterView(Context context,
			AttributeSet attrs) {
		lv = new ListView(context, attrs);

		// Set it to this so it can be used in ListActivity/ListFragment
		lv.setId(android.R.id.list);
		// Footer view
		View footer = LayoutInflater.from(context).inflate(
				R.layout.pull_to_refresh_footer, null);
		footerText = (TextView) footer.findViewById(R.id.pull_to_refresh_text);
		footerProgress = (ProgressBar) footer
				.findViewById(R.id.pull_to_refresh_progress);
		lv.addFooterView(footer);
		lv.setFooterDividersEnabled(false);
		footer.setClickable(true);
		footer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		return lv;
	}

	public void showFooter() {
		footerText.setVisibility(View.VISIBLE);
		footerProgress.setVisibility(View.VISIBLE);
	}

	public void hideFooter() {
		footerText.setVisibility(View.GONE);
		footerProgress.setVisibility(View.GONE);
	}

	@Override
	public void onLoadMoreComplete() {
		hideFooter();
		super.onLoadMoreComplete();
	}

	@Override
	public void onLoadMoreStart() {
		showFooter();
		super.onLoadMoreStart();
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		lv.setOnItemClickListener(onItemClickListener);
	}

	public void setAdapter(ListAdapter listAdapter) {
		lv.setAdapter(listAdapter);
	}
}
