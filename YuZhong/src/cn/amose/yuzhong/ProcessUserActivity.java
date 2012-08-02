package cn.amose.yuzhong;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import cn.amose.yuzhong.asynctask.GetAsyncTask;
import cn.amose.yuzhong.asynctask.GetAsyncTask.OnDownloadListener;
import cn.amose.yuzhong.database.PreferenceHelper;
import cn.amose.yuzhong.http.Register;
import cn.amose.yuzhong.model.User;
import cn.amose.yuzhong.util.Md5Util;

public class ProcessUserActivity extends Activity {
	private GetAsyncTask mGetAsyncTask;
	private EditText mMobileEt;
	private EditText mNameEt;
	private EditText mPasswordEt;
	private EditText mRepasswordEt;
	private Button mBirthdayBtn;
	private RadioGroup mGenderRg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.process_user);
	}

	public void onCommitClick(View v) {
		String mobile = "15977672564";
		String name = "ligl";
		String password = "0";
		long birthday = System.currentTimeMillis();
		int gender = 0;
		byte[] avatar = null;
		register(mobile, name, password, birthday, gender, avatar);
	}

	public void onBackClick(View v) {

	}

	public void onAvatarChooserClick(View v) {

	}

	private void register(String mobile, String name, String password,
			long birthday, int gender, byte[] avatar) {
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("mobile", mobile);
			jsonObject.put("name", name);
			jsonObject.put("birthday", birthday);
			jsonObject.put("gender", gender);
			jsonObject.put("password", Md5Util.md5(password));
			jsonObject.put("avatar", avatar);
			cancelAsyncTaskIfNeed();
			mGetAsyncTask = new GetAsyncTask(new Register(this), jsonObject);
			mGetAsyncTask.setOnDownloadListener(new OnDownloadListener() {

				@Override
				public <T> void onDownloadComplete(T result, String errorMessage) {
					if (errorMessage == null) {
						PreferenceHelper.initDefaultAccount((User) result);
						Toast.makeText(ProcessUserActivity.this,
								((User) result).getName(), Toast.LENGTH_SHORT)
								.show();
					} else {
						Toast.makeText(ProcessUserActivity.this, errorMessage,
								Toast.LENGTH_SHORT).show();
					}
				}
			});
			mGetAsyncTask.execute();
		} catch (JSONException e) {
			e.printStackTrace();
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