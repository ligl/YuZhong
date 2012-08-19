package cn.amose.yuzhong;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;
import cn.amose.yuzhong.asynctask.GetAsyncTask;
import cn.amose.yuzhong.asynctask.GetAsyncTask.OnDownloadListener;
import cn.amose.yuzhong.database.PreferenceHelper;
import cn.amose.yuzhong.http.GetBulletins;
import cn.amose.yuzhong.model.Bulletin;
import cn.amose.yuzhong.util.Constant;
import cn.amose.yuzhong.widget.AsyncImageView;
import cn.amose.yuzhong.widget.PullToRefreshBase.OnLoadMoreListener;
import cn.amose.yuzhong.widget.PullToRefreshBase.OnRefreshListener;
import cn.amose.yuzhong.widget.PullToRefreshListView;

public class BulletinActivity extends YZBaseActivity {
	private GetAsyncTask mGetAsyncTask;
	private PullToRefreshListView mPullToRefreshListView;
	private BulletinListAdapter mBulletinListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bulletin);
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_bulletin);
		mPullToRefreshListView.setOnRefreshListener(mOnRefreshListener);
		mPullToRefreshListView.setOnLoadMoreListener(mOnLoadMoreListener);
		mBulletinListAdapter = new BulletinListAdapter();
		mBulletinListAdapter.setDataSource(new ArrayList<Bulletin>(0));
		mPullToRefreshListView.setAdapter(mBulletinListAdapter);
		mPullToRefreshListView.onLoadMoreStart();
		getBulletins();
		// TODO test
		Intent bootIntent = new Intent(this, MainService.class);
		startService(bootIntent);
	}

	private OnRefreshListener mOnRefreshListener = new OnRefreshListener() {

		@Override
		public void onRefresh() {
			mBulletinListAdapter.reset();
			if (mPullToRefreshListView.isLoadingMore()) {
				mPullToRefreshListView.onLoadMoreComplete();
			}
			getBulletins();
		}

	};
	private OnLoadMoreListener mOnLoadMoreListener = new OnLoadMoreListener() {

		@Override
		public void onLoadMore() {
			getBulletins();
		}

	};

	class BulletinListAdapter extends BaseAdapter {
		private int mPageNumber;
		private int mPageSize = 5;
		private ArrayList<Bulletin> mBulletinList;

		public void reset() {
			mPageNumber = 0;
			mPageSize = 5;
		}

		public void setDataSource(ArrayList<Bulletin> bulletinList) {
			mBulletinList = bulletinList;
			notifyDataSetChanged();
		}

		public void appendDataSource(ArrayList<Bulletin> bulletinList) {
			mBulletinList.addAll(bulletinList);
			notifyDataSetChanged();
		}

		public ArrayList<Bulletin> getDataSource() {
			return mBulletinList;
		}

		public synchronized int incPageNumber() {
			return ++mPageNumber;
		}

		public int getPageNumber() {
			return mPageNumber;
		}

		public void setPageNumber(int pageNumber) {
			this.mPageNumber = pageNumber;
		}

		public int getPageSize() {
			return mPageSize;
		}

		public void setPageSize(int pageSize) {
			this.mPageSize = pageSize;
		}

		@Override
		public int getCount() {
			return mBulletinList.size();
		}

		@Override
		public Object getItem(int position) {
			return mBulletinList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				viewHolder = new ViewHolder();
				convertView = mLayoutInflater.inflate(
						R.layout.listitem_bulletin, null);
				viewHolder.mCaptionTv = (TextView) convertView
						.findViewById(R.id.tv_bulletin_caption);
				viewHolder.mDateTv = (TextView) convertView
						.findViewById(R.id.tv_bulletin_date);
				viewHolder.mTimeTv = (TextView) convertView
						.findViewById(R.id.tv_bulletin_time);
				viewHolder.mTitleTv = (TextView) convertView
						.findViewById(R.id.tv_bulletin_title);
				viewHolder.mImageIv = (AsyncImageView) convertView
						.findViewById(R.id.iv_bulletin_image);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			Bulletin bulletin = mBulletinList.get(position);
			viewHolder.mTitleTv.setText(bulletin.getTitle());
			try {
				JSONObject contentJson = new JSONObject(
						bulletin.getContentJson());
				viewHolder.mImageIv.setUrl(contentJson.getString("image"));
				viewHolder.mCaptionTv.setText(contentJson.getString("caption"));
				((View) viewHolder.mImageIv.getParent()).setTag(contentJson
						.get("clickurl"));
			} catch (JSONException e) {
				if (Constant.DEBUG) {
					e.printStackTrace();
				}
			}
			Date date = new Date(bulletin.getTime());
			viewHolder.mDateTv.setText(String.format("%d月%d日",
					date.getMonth() + 1, date.getDate()));
			viewHolder.mTimeTv.setText(String.format("%2d:%2d",
					date.getHours(), date.getMinutes()));

			return convertView;
		}
	}

	class ViewHolder {
		AsyncImageView mImageIv;
		TextView mTitleTv;
		TextView mDateTv;
		TextView mTimeTv;
		TextView mCaptionTv;
	}

	private void getBulletins() {
		JSONObject jsonHolder = new JSONObject();
		try {
			jsonHolder.put(Constant.JSON_KEY_UID,
					PreferenceHelper.getAccountId());
			jsonHolder.put(Constant.JSON_KEY_TYPE, Bulletin.TYPE_TEXT);
			mGetAsyncTask = new GetAsyncTask(new GetBulletins(this), jsonHolder);
			mGetAsyncTask.setOnDownloadListener(new OnDownloadListener() {

				@Override
				public <T> void onDownloadComplete(T result, String errorMessage) {
					if (mPullToRefreshListView.isRefreshing()) {
						mPullToRefreshListView.onRefreshComplete();
					}
					if (mPullToRefreshListView.isLoadingMore()) {
						mPullToRefreshListView.onLoadMoreComplete();
					}
					if (errorMessage == null) {
						ArrayList<Bulletin> bulletinList = (ArrayList<Bulletin>) result;
						if (bulletinList == null || bulletinList.isEmpty()) {
							// no data
							Toast.makeText(getApplicationContext(), "nothing",
									Toast.LENGTH_SHORT).show();
						} else {
							if (mBulletinListAdapter.incPageNumber() == 1) {
								mBulletinListAdapter
										.setDataSource(bulletinList);
							} else {
								mBulletinListAdapter
										.appendDataSource(bulletinList);
							}
						}
					} else {
						Toast.makeText(getApplicationContext(), errorMessage,
								Toast.LENGTH_SHORT).show();
					}

				}
			});
			mGetAsyncTask.execute();
		} catch (JSONException e) {
			if (Constant.DEBUG) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onDestroy() {
		cancelAsyncTaskIfNeed();
		super.onDestroy();
	}

	private void cancelAsyncTaskIfNeed() {
		if (mGetAsyncTask != null
				&& mGetAsyncTask.getStatus() != AsyncTask.Status.FINISHED) {
			mGetAsyncTask.cancel(true);
		}
	}
}