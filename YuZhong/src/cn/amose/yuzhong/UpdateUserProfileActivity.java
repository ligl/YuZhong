package cn.amose.yuzhong;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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
import cn.amose.yuzhong.util.AppUtils;
import cn.amose.yuzhong.util.Constant;
import cn.amose.yuzhong.widget.AsyncImageView;

public class UpdateUserProfileActivity extends Activity {
	private GetAsyncTask mGetAsyncTask;
	private EditText mMobileEt;
	private EditText mNameEt;
	private Button mBirthdayBtn;
	private RadioGroup mGenderRg;
	private AsyncImageView mAvatarIv;
	private DatePickerDialog mBirthdayDatePickerDialog;
	private Calendar mBirthdayDate;
	private boolean mAvatarBitmapUpdated;
	private ProgressDialog mProgressDialog;
	private User mUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.update_user_profile);
		mMobileEt = (EditText) findViewById(R.id.et_update_user_profile_mobile);
		mAvatarIv = (AsyncImageView) findViewById(R.id.iv_update_user_profile_avatar);
		mNameEt = (EditText) findViewById(R.id.et_update_user_profile_name);
		mBirthdayBtn = (Button) findViewById(R.id.btn_update_user_profile_birthday);
		mGenderRg = (RadioGroup) findViewById(R.id.rg_update_user_profile_gender);
		if (PreferenceHelper.getAccountId() == 0) {
			Toast.makeText(this, R.string.common_toast_error,
					Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		mUser = PreferenceHelper.getDefaultAccount();
		mAvatarIv.setUrl(mUser.getAvatarUrl());
		mMobileEt.setText(mUser.getMobile());
		mNameEt.setText(mUser.getName());
		switch (mUser.getGender()) {
		case User.USER_GENDER_FEMALE:
			mGenderRg.check(R.id.rbtn_update_user_profile_gender_female);
			break;
		case User.USER_GENDER_MALE:
			mGenderRg.check(R.id.rbtn_update_user_profile_gender_male);
			break;
		default:
			mGenderRg.check(R.id.rbtn_update_user_profile_gender_unkown);
			break;
		}

	}

	public void onCommitClick(View v) {
		String mobile = mMobileEt.getText().toString().trim();
		String name = mNameEt.getText().toString().trim();
		if (name.equals("")) {
			mNameEt.requestFocus();
			mNameEt.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.shake));
		} else if (mobile.equals("")) {
			mMobileEt.requestFocus();
			mMobileEt.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.shake));
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
			String avatarContent = null;
			if (mAvatarBitmapUpdated) {
				Bitmap bitmap = mAvatarIv.getBitmap();
				// TODO 修改AsyncImageView 可以加载本地文件系统图片setUrl
				if (bitmap == null) {
					bitmap = drawableToBitmap(mAvatarIv.getDrawable());
				}
				avatarContent = Base64.encodeToString(
						bitmap2Bytes(bitmap, mAvatarIv.getImageFormat()),
						Base64.DEFAULT);
			}
			updateUserProfile(mobile, name, birthday, gender, avatarContent);
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
							mBirthdayBtn.setText(String.format("%d-%d-%d",
									year, monthOfYear, dayOfMonth));
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

	private void updateUserProfile(String mobile, String name, long birthday,
			int gender, String avatar) {
		if (mProgressDialog == null) {
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setTitle(R.string.common_dg_title_info);
			mProgressDialog
					.setMessage(getString(R.string.common_dg_msg_operation));
			mProgressDialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface arg0) {
					cancelAsyncTaskIfNeed();
				}
			});
		}
		JSONObject jsonObject = new JSONObject();
		try {
			if (!mUser.getMobile().equals(mobile)) {
				jsonObject.put("mobile", mobile);
			}
			if (!mUser.getName().equals(name)) {
				jsonObject.put("name", name);
			}
			if (mUser.getBirthday() != birthday) {
				jsonObject.put("birthday", birthday);
			}
			if (mUser.getGender() != gender) {
				jsonObject.put("gender", gender);
			}
			if (avatar != null) {
				jsonObject.put("avatar", avatar);
			}
			if (jsonObject.length() == 0) {
				Toast.makeText(UpdateUserProfileActivity.this,
						R.string.processuser_toast_updateprofilesuccess,
						Toast.LENGTH_SHORT).show();
				return;
			}
			jsonObject.put("uid", mUser.getId());
			cancelAsyncTaskIfNeed();
			mGetAsyncTask = new GetAsyncTask(new Register(this), jsonObject);
			mGetAsyncTask.setOnDownloadListener(new OnDownloadListener() {

				@Override
				public <T> void onDownloadComplete(T result, String errorMessage) {
					if (mProgressDialog.isShowing()) {
						mProgressDialog.dismiss();
					}
					if (errorMessage == null) {
						Toast.makeText(
								UpdateUserProfileActivity.this,
								R.string.processuser_toast_updateprofilesuccess,
								Toast.LENGTH_SHORT).show();
						PreferenceHelper.initDefaultAccount((User) result);
						PreferenceHelper.initDefaultAccount((User) result);
						AppUtils.startMainActivity(UpdateUserProfileActivity.this);
						finish();

					} else {
						Toast.makeText(UpdateUserProfileActivity.this,
								errorMessage, Toast.LENGTH_SHORT).show();
					}
				}
			});
			mGetAsyncTask.execute();
			mProgressDialog.show();
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