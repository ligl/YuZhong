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
import cn.amose.yuzhong.http.GetUsers;
import cn.amose.yuzhong.model.User;
import cn.amose.yuzhong.util.Constant;
import cn.amose.yuzhong.util.Utils;
import cn.amose.yuzhong.widget.AsyncImageView;
import cn.amose.yuzhong.widget.PullToRefreshBase.OnLoadMoreListener;
import cn.amose.yuzhong.widget.PullToRefreshBase.OnRefreshListener;
import cn.amose.yuzhong.widget.PullToRefreshListView;

public class MemberActivity extends YZBaseActivity {
	private GetAsyncTask mGetAsyncTask;
	private PullToRefreshListView mPullToRefreshListView;
	private UserListAdapter mUserListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.member);
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_member);
		mPullToRefreshListView.setOnRefreshListener(mOnRefreshListener);
		mPullToRefreshListView.setOnLoadMoreListener(mOnLoadMoreListener);
		mUserListAdapter = new UserListAdapter();
		mUserListAdapter.setDataSource(new ArrayList<User>(0));
		mPullToRefreshListView.setAdapter(mUserListAdapter);
		mPullToRefreshListView.onLoadMoreStart();
		getUsers();
	}

	private OnRefreshListener mOnRefreshListener = new OnRefreshListener() {

		@Override
		public void onRefresh() {
			mUserListAdapter.reset();
			if (mPullToRefreshListView.isLoadingMore()) {
				mPullToRefreshListView.onLoadMoreComplete();
			}
			getUsers();
		}

	};
	private OnLoadMoreListener mOnLoadMoreListener = new OnLoadMoreListener() {

		@Override
		public void onLoadMore() {
			getUsers();
		}

	};

	public void onCallClick(View v) {
		String phone = ((View) v.getParent()).getTag().toString();
		Utils.dial(this, phone);
	}

	public void onSMSClick(View v) {
		String phone = ((View) v.getParent()).getTag().toString();
		String msg = "";
		Utils.sms(this, phone, msg);
	}

	class UserListAdapter extends BaseAdapter {
		private int mPageNumber;
		private int mPageSize = 5;
		private ArrayList<User> mUserList;

		public void reset() {
			mPageNumber = 0;
			mPageSize = 5;
		}

		public void setDataSource(ArrayList<User> userList) {
			mUserList = userList;
			notifyDataSetChanged();
		}

		public void appendDataSource(ArrayList<User> userList) {
			mUserList.addAll(userList);
			notifyDataSetChanged();
		}

		public ArrayList<User> getDataSource() {
			return mUserList;
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
			return mUserList.size();
		}

		@Override
		public Object getItem(int position) {
			return mUserList.get(position);
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
				convertView = mLayoutInflater.inflate(R.layout.listitem_member,
						null);
				viewHolder.mNameTv = (TextView) convertView
						.findViewById(R.id.tv_member_name);
				viewHolder.mAvatarIv = (AsyncImageView) convertView
						.findViewById(R.id.iv_member_avatar);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			User user = mUserList.get(position);
			viewHolder.mNameTv.setText(user.getName());
			viewHolder.mAvatarIv.setUrl(user.getAvatarUrl());
			((View) viewHolder.mAvatarIv.getParent()).setTag(user.getMobile());
			return convertView;
		}
	}

	class ViewHolder {
		AsyncImageView mAvatarIv;
		TextView mNameTv;
	}

	private void getUsers() {
		JSONObject jsonHolder = new JSONObject();
		try {
			jsonHolder.put(Constant.JSON_KEY_UID,
					PreferenceHelper.getAccountId());
			// jsonHolder.put(Constant.JSON_KEY_TYPE, );
			mGetAsyncTask = new GetAsyncTask(new GetUsers(this), jsonHolder);
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
						ArrayList<User> userList = (ArrayList<User>) result;
						if (userList == null || userList.isEmpty()) {
							// no data
							Toast.makeText(getApplicationContext(), "nothing",
									Toast.LENGTH_SHORT).show();
						} else {
							if (mUserListAdapter.incPageNumber() == 1) {
								mUserListAdapter.setDataSource(userList);
							} else {
								mUserListAdapter.appendDataSource(userList);
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