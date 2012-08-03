package cn.amose.yuzhong;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import cn.amose.yuzhong.asynctask.GetAsyncTask;
import cn.amose.yuzhong.asynctask.GetAsyncTask.OnDownloadListener;
import cn.amose.yuzhong.database.PreferenceHelper;
import cn.amose.yuzhong.http.Register;
import cn.amose.yuzhong.model.User;
import cn.amose.yuzhong.util.Constant;
import cn.amose.yuzhong.util.Md5Util;
import cn.amose.yuzhong.widget.AsyncImageView;

public class ProcessUserActivity extends Activity {
	private GetAsyncTask mGetAsyncTask;
	private EditText mMobileEt;
	private EditText mNameEt;
	private EditText mPasswordEt;
	private EditText mRepasswordEt;
	private Button mBirthdayBtn;
	private RadioGroup mGenderRg;
	private AsyncImageView mAvatarIv;
	private DatePickerDialog mBirthdayDatePickerDialog;
	private Calendar mBirthdayDate;
	private boolean mAvatarBitmapUpdated;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.process_user);
		mMobileEt = (EditText) findViewById(R.id.et_process_user_mobile);
		mAvatarIv = (AsyncImageView) findViewById(R.id.iv_process_user_avatar);
		mNameEt = (EditText) findViewById(R.id.et_process_user_name);
		mPasswordEt = (EditText) findViewById(R.id.et_process_user_password);
		mRepasswordEt = (EditText) findViewById(R.id.et_process_user_repassword);
		mBirthdayBtn = (Button) findViewById(R.id.btn_process_user_birthday);
		mGenderRg = (RadioGroup) findViewById(R.id.rg_process_user_gender);

	}

	public void onCommitClick(View v) {
		String mobile = mMobileEt.getText().toString().trim();
		String name = mNameEt.getText().toString().trim();
		String password = mPasswordEt.getText().toString().trim();
		String repassword = mRepasswordEt.getText().toString().trim();
		if (mobile.equals("")) {
			mMobileEt.requestFocus();
			mMobileEt.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.shake));
		} else if (name.equals("")) {
			mNameEt.requestFocus();
			mNameEt.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.shake));
		} else if (password.equals("")) {
			mPasswordEt.requestFocus();
			mPasswordEt.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.shake));
		} else if (repassword.equals("")) {
			mRepasswordEt.requestFocus();
			mRepasswordEt.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.shake));
		} else if (!password.equals(repassword)) {
			Toast.makeText(this, R.string.processuser_toast_passworddiffer,
					Toast.LENGTH_SHORT).show();
		} else {
			long birthday = (mBirthdayBtn.getText().equals("") ? 0
					: mBirthdayDate.getTimeInMillis());
			int gender;
			switch (mGenderRg.getCheckedRadioButtonId()) {
			case R.id.rbtn_process_user_gender_female:
				gender = 1;
				break;
			case R.id.rbtn_process_user_gender_male:
				gender = 0;
				break;
			case R.id.rbtn_process_user_gender_unkown:
			default:
				gender = -1;
				break;
			}
			byte[] avatarContent = null;
			if (mAvatarBitmapUpdated) {
				Bitmap bitmap = mAvatarIv.getBitmap();
				//TODO 修改AsyncImageView 可以加载本地文件系统图片setUrl
				if (bitmap == null) {
					bitmap = drawableToBitmap(mAvatarIv.getDrawable());
				}
				avatarContent = bitmap2Bytes(bitmap, mAvatarIv.getImageFormat());
			}
			register(mobile, name, password, birthday, gender, avatarContent);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		switch (requestCode) {
		case Constant.REQUEST_CODE_SELECTFILE:
		case Constant.REQUEST_CODE_TAKEAPICTURE:
			if (resultCode == Activity.RESULT_OK) {
				Uri filePath = data.getData();
				if (filePath == null) {
					// TODO get image failed
					Toast.makeText(this, R.string.common_toast_getimagefailed,
							Toast.LENGTH_SHORT).show();

				} else {
					mAvatarIv.setImageURI(filePath);
					mAvatarBitmapUpdated = true;
				}
			}
			break;
		default:
			// setResult(Activity.RESULT_CANCELED);
			// finish();
			break;
		}

	}

	public byte[] bitmap2Bytes(Bitmap bm, Bitmap.CompressFormat format) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(format, 100, baos);
		return baos.toByteArray();
	}

	private String getPath(Context context, Uri uri) {
		if ("content".equalsIgnoreCase(uri.getScheme())) {
			String[] projection = { "_data" };
			Cursor cursor = null;

			try {
				cursor = context.getContentResolver().query(uri, projection,
						null, null, null);
				int column_index_data = cursor.getColumnIndexOrThrow("_data");
				if (cursor.moveToFirst()) {
					return cursor.getString(column_index_data);
				}
			} catch (Exception e) {
				// Eat it
			}
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			// TODO get file size
			return uri.getPath();
		}

		return null;
	}

	public void onSelectFileClick(View v) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, Constant.REQUEST_CODE_SELECTFILE);
	}

	public void onTakeaPhotoClick(View v) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT,
				MediaStore.getMediaScannerUri()); // output,Uri.parse("content://mms/scrapSpace");
		startActivityForResult(intent, Constant.REQUEST_CODE_TAKEAPICTURE);
	}

	public void onBirthdaySettingClick(View v) {

		if (mBirthdayDatePickerDialog == null) {
			mBirthdayDate = Calendar.getInstance();
			mBirthdayDatePickerDialog = new DatePickerDialog(this,
					new OnDateSetListener() {

						@Override
						public void onDateSet(DatePicker arg0, int year,
								int monthOfYear, int dayOfMonth) {
							mBirthdayDate.set(year, monthOfYear, dayOfMonth);
							mBirthdayBtn.setText(mBirthdayDate.toString());
						}
					}, mBirthdayDate.get(Calendar.YEAR),
					mBirthdayDate.get(Calendar.MONTH),
					mBirthdayDate.get(Calendar.DAY_OF_MONTH));
		}
		mBirthdayDatePickerDialog.show();
	}

	public static Bitmap drawableToBitmap(Drawable drawable) {
		// 取 drawable 的长宽
		int w = drawable.getIntrinsicWidth();
		int h = drawable.getIntrinsicHeight();

		// 取 drawable 的颜色格式
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		// 建立对应 bitmap
		Bitmap bitmap = Bitmap.createBitmap(w, h, config);
		// 建立对应 bitmap 的画布
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, w, h);
		// 把 drawable 内容画到画布中
		drawable.draw(canvas);
		return bitmap;
	}

	public void onBackClick(View v) {
		finish();
	}

	public void onAvatarChooserClick(View v) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, Constant.REQUEST_CODE_SELECTFILE);
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