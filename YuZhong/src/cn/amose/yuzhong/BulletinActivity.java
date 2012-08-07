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
import cn.amose.yuzhong.widget.PullToRefreshListView;

public class BulletinActivity extends YZBaseActivity {
	private GetAsyncTask mGetAsyncTask;
	private PullToRefreshListView mPullToRefreshListView;
	private ArrayList<Bulletin> mBulletinList;
	private BulletinListAdapter mBulletinListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bulletin);
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_bulletin);
		mBulletinListAdapter = new BulletinListAdapter();
		mBulletinList = new ArrayList<Bulletin>(0);
		mPullToRefreshListView.setAdapter(mBulletinListAdapter);
		getBulletins();
	}

	class BulletinListAdapter extends BaseAdapter {

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
			jsonHolder.put(Constant.JSON_KEY_TYPE,
					Bulletin.TYPE_TEXT);
			mGetAsyncTask = new GetAsyncTask(new GetBulletins(this), jsonHolder);
			mGetAsyncTask.setOnDownloadListener(new OnDownloadListener() {

				@Override
				public <T> void onDownloadComplete(T result, String errorMessage) {
					if (errorMessage == null) {
						ArrayList<Bulletin> bulletinList = (ArrayList<Bulletin>) result;
						if (bulletinList == null || bulletinList.isEmpty()) {
							// no data

						} else {
							mBulletinList.addAll(bulletinList);
							mBulletinListAdapter.notifyDataSetChanged();
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