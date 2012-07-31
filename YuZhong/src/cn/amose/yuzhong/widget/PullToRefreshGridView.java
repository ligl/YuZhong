package cn.amose.yuzhong.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class PullToRefreshGridView extends PullToRefreshBase<GridView> {

	public PullToRefreshGridView(Context context) {
		super(context);
	}

	public PullToRefreshGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected final GridView createAdapterView(Context context,
			AttributeSet attrs) {
		return new GridView(context, attrs);
	}

}
