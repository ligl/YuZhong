package cn.amose.yuzhong;

import java.util.ArrayList;

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
import cn.amose.yuzhong.http.GetActivities;
import cn.amose.yuzhong.model.Activity;
import cn.amose.yuzhong.model.User;
import cn.amose.yuzhong.util.Constant;
import cn.amose.yuzhong.util.Utils;
import cn.amose.yuzhong.widget.AsyncImageView;
import cn.amose.yuzhong.widget.PullToRefreshBase.OnLoadMoreListener;
import cn.amose.yuzhong.widget.PullToRefreshBase.OnRefreshListener;
import cn.amose.yuzhong.widget.PullToRefreshListView;

public class ActivityActivity extends YZBaseActivity {
	private GetAsyncTask mGetAsyncTask;
	private PullToRefreshListView mPullToRefreshListView;
	private ActivityListAdapter mListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity);
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_activity);
		mPullToRefreshListView.setOnRefreshListener(mOnRefreshListener);
		mPullToRefreshListView.setOnLoadMoreListener(mOnLoadMoreListener);
		mListAdapter = new ActivityListAdapter();
		mListAdapter.setDataSource(new ArrayList<Activity>(0));
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
		int position = Integer.parseInt(v.getTag().toString());
		Activity bulletin = mListAdapter.getDataSource().get(position);

	}

	public void onNoInterestButtonClick(View v) {

	}

	public void onLookMemberButtonClick(View v) {

	}

	public void onCallClick(View v) {
		String phone = ((View) v.getParent()).getTag().toString();
		Utils.dial(this, phone);
	}

	public void onSMSClick(View v) {
		String phone = ((View) v.getParent()).getTag().toString();
		String msg = "";
		Utils.sms(this, phone, msg);
	}

	class ActivityListAdapter extends BaseAdapter {
		private int mPageNumber;
		private int mPageSize = 5;
		private ArrayList<Activity> mActivityList;

		public void reset() {
			mPageNumber = 0;
			mPageSize = 5;
		}

		public void setDataSource(ArrayList<Activity> bulletinList) {
			mActivityList = bulletinList;
			notifyDataSetChanged();
		}

		public void appendDataSource(ArrayList<Activity> bulletinList) {
			mActivityList.addAll(bulletinList);
			notifyDataSetChanged();
		}

		public ArrayList<Activity> getDataSource() {
			return mActivityList;
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
			return mActivityList.size();
		}

		@Override
		public Object getItem(int position) {
			return mActivityList.get(position);
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
				viewHolder.mPhotoIv = (AsyncImageView) convertView
						.findViewById(R.id.iv_activity_image);
				viewHolder.mTipsLableTv = (TextView) convertView
						.findViewById(R.id.tv_activity_tipslabel);
				viewHolder.mManagerTv = (TextView) convertView
						.findViewById(R.id.tv_activity_manager);
				viewHolder.mJoinView = convertView
						.findViewById(R.id.btn_activity_join);
				viewHolder.mNoInterestView = convertView
						.findViewById(R.id.btn_activity_nointerest);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			Activity activity = mActivityList.get(position);
			viewHolder.mTitleTv.setText(activity.getTitle());
			viewHolder.mTimeTv.setText("活动时间："
					+ Utils.formatDateTime(activity.getActivityTime()));
			viewHolder.mAddrTv.setText("活动地点：" + activity.getAddr());
			if ("".equals(activity.getPhoto())) {
				viewHolder.mPhotoIv.setVisibility(View.GONE);
			} else {
				viewHolder.mPhotoIv.setVisibility(View.VISIBLE);
				viewHolder.mPhotoIv.setUrl(activity.getPhoto());
			}
			if (activity.getDesc() == null) {
				viewHolder.mDescTv.setVisibility(View.GONE);
			} else {
				viewHolder.mDescTv.setVisibility(View.VISIBLE);
				viewHolder.mDescTv.setText(activity.getDesc());
			}
			long curTime = System.currentTimeMillis();
			if (curTime < activity.getSignupBeginTime()) {
				// 没开始
				viewHolder.mTipsLableTv.setVisibility(View.VISIBLE);
				viewHolder.mJoinView.setVisibility(View.GONE);
				viewHolder.mNoInterestView.setVisibility(View.GONE);
				viewHolder.mTipsLableTv.setText("活动尚未开始报名，如需帮助，请联系^");
			} else if (curTime > activity.getSignupEndTime()) {
				// 已结束
				viewHolder.mTipsLableTv.setVisibility(View.VISIBLE);
				viewHolder.mJoinView.setVisibility(View.GONE);
				viewHolder.mNoInterestView.setVisibility(View.GONE);
				viewHolder.mTipsLableTv.setText("活动报名时间已结束，如需帮助，请联系^");
			} else {
				// 活动期间
				viewHolder.mTipsLableTv.setVisibility(View.GONE);
				viewHolder.mJoinView.setVisibility(View.VISIBLE);
				viewHolder.mNoInterestView.setVisibility(View.VISIBLE);
			}
			User user = activity.getManager();
			viewHolder.mManagerTv.setText(user.getName());
			((View) viewHolder.mManagerTv.getParent()).setTag(user.getMobile());
			viewHolder.mJoinView.setTag(position);
			viewHolder.mNoInterestView.setTag(position);
			return convertView;
		}
	}

	class ViewHolder {
		AsyncImageView mPhotoIv;
		TextView mTitleTv;
		TextView mAddrTv;
		TextView mTimeTv;
		TextView mDescTv;
		TextView mManagerTv;
		TextView mTipsLableTv;
		View mJoinView;
		View mNoInterestView;
	}

	private void getActivities() {
		JSONObject jsonHolder = new JSONObject();
		try {
			jsonHolder.put(Constant.JSON_KEY_UID,
					PreferenceHelper.getAccountId());
			cancelAsyncTaskIfNeed();
			mGetAsyncTask = new GetAsyncTask(new GetActivities(this),
					jsonHolder);
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
						ArrayList<Activity> bulletinList = (ArrayList<Activity>) result;
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

	private void join() {
		JSONObject jsonHolder = new JSONObject();
		try {
			jsonHolder.put(Constant.JSON_KEY_UID,
					PreferenceHelper.getAccountId());
			mGetAsyncTask = new GetAsyncTask(new GetActivities(this),
					jsonHolder);
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
						ArrayList<Activity> bulletinList = (ArrayList<Activity>) result;
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