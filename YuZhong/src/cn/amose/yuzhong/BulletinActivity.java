package cn.amose.yuzhong;

import android.os.AsyncTask;
import android.os.Bundle;
import cn.amose.yuzhong.asynctask.GetAsyncTask;

public class BulletinActivity extends YZBaseActivity {
	private GetAsyncTask mGetAsyncTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bulletin);
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