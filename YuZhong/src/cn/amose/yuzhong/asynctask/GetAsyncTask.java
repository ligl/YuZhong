package cn.amose.yuzhong.asynctask;

import org.json.JSONObject;

import android.os.AsyncTask;
import cn.amose.yuzhong.http.HttpService;

/**
 * <p>
 * async-download data, return format List<?>
 * </p>
 * 
 * @author ligl
 * 
 */
public class GetAsyncTask extends AsyncTask<Void, Void, Void> {
	private HttpService mDownloadService;
	private JSONObject mJsonHolder;
	private OnDownloadListener mDownloadListener;

	public GetAsyncTask(HttpService httpService, JSONObject jsonHolder) {
		mDownloadService = httpService;
		mJsonHolder = jsonHolder;
	}

	protected Void doInBackground(Void... params) {
		mDownloadService.start(mJsonHolder);
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		if (mDownloadListener != null) {
			mDownloadListener.onDownloadComplete(mDownloadService.getResult(),
					mDownloadService.getErrorMessage());
		}
	}

	public void setOnDownloadListener(OnDownloadListener onDownloadListener) {
		mDownloadListener = onDownloadListener;
	}

	public static interface OnDownloadListener {
		<T> void onDownloadComplete(T result, String errorMessage);
	}
}