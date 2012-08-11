package cn.amose.yuzhong;

import java.util.ArrayList;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

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

public class ActivityActivity extends YZBaseActivity {
	private GetAsyncTask mGetAsyncTask;
	private PullToRefreshListView mPullToRefreshListView;
	private BulletinListAdapter mListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity);
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_activity);
		mPullToRefreshListView.setOnRefreshListener(mOnRefreshListener);
		mPullToRefreshListView.setOnLoadMoreListener(mOnLoadMoreListener);
		mListAdapter = new BulletinListAdapter();
		mListAdapter.setDataSource(new ArrayList<Bulletin>(0));
		mPullToRefreshListView.setAdapter(mListAdapter);
		mPullToRefreshListView.onLoadMoreStart();
		getActivities();
	}

	private OnRefreshListener mOnRefreshListener = new OnRefreshListener() {

		@Override
		public void onRefresh() {
			mListAdapter.reset();
			if (mPullToRefreshListView.isLoadingMore()) {
				mPullToRefreshListView.onLoadMoreComplete();
			}
			getActivities();
		}

	};
	private OnLoadMoreListener mOnLoadMoreListener = new OnLoadMoreListener() {

		@Override
		public void onLoadMore() {
			getActivities();
		}

	};

	public void onJoinButtonClick(View v) {

	}

	public void onNoInterestButtonClick(View v) {

	}

	public void onLookMemberButtonClick(View v) {

	}

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
						R.layout.listitem_activity, null);
				viewHolder.mDescTv = (TextView) convertView
						.findViewById(R.id.tv_activity_desc);
				viewHolder.mTimeTv = (TextView) convertView
						.findViewById(R.id.tv_activity_time);
				viewHolder.mAddrTv = (TextView) convertView
						.findViewById(R.id.tv_activity_addr);
				viewHolder.mTitleTv = (TextView) convertView
						.findViewById(R.id.tv_activity_title);
				viewHolder.mImageIv = (AsyncImageView) convertView
						.findViewById(R.id.iv_activity_image);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			Bulletin bulletin = mBulletinList.get(position);
			viewHolder.mTitleTv.setText(bulletin.getTitle());
			try {
				JSONObject contentJson = new JSONObject(
						bulletin.getContentJson());
				Date date = new Date(contentJson.getLong("time"));
				viewHolder.mTimeTv.setText(date.toLocaleString());
				viewHolder.mAddrTv.setText(contentJson.getString("addr"));
				String imageUrl = contentJson.optString("image", null);
				if (imageUrl == null) {
					viewHolder.mImageIv.setVisibility(View.GONE);
				} else {
					viewHolder.mImageIv.setVisibility(View.VISIBLE);
					viewHolder.mImageIv.setUrl(imageUrl);
				}
				String desc = contentJson.optString("desc", null);
				if (desc == null) {
					viewHolder.mDescTv.setVisibility(View.GONE);
				} else {
					viewHolder.mDescTv.setVisibility(View.VISIBLE);
					viewHolder.mDescTv.setText(desc);
				}
			} catch (JSONException e) {
				if (Constant.DEBUG) {
					e.printStackTrace();
				}
			}
			Date date = new Date(bulletin.getTime());
			viewHolder.mTimeTv.setText(date.toLocaleString());
			return convertView;
		}
	}

	class ViewHolder {
		AsyncImageView mImageIv;
		TextView mTitleTv;
		TextView mAddrTv;
		TextView mTimeTv;
		TextView mDescTv;
	}

	private void getActivities() {
		JSONObject jsonHolder = new JSONObject();
		try {
			jsonHolder.put(Constant.JSON_KEY_UID,
					PreferenceHelper.getAccountId());
			jsonHolder.put(Constant.JSON_KEY_TYPE, Bulletin.TYPE_ACTIVITY);
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
							if (mListAdapter.incPageNumber() == 1) {
								mListAdapter.setDataSource(bulletinList);
							} else {
								mListAdapter.appendDataSource(bulletinList);
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