package cn.amose.yuzhong.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;

public final class Utils {

	public static String formatDateTime(long timestamp) {
		return android.text.format.DateFormat.format("yyyy-MM-dd hh:mm",
				timestamp).toString();
	}

	public static boolean checkNetwork(Context context) {
		boolean result = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo != null
				&& networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
			result = true;
		}
		return result;
	}

	public static Bitmap ajustBitmap(Uri uri, Context context, int maxWidth) {
		int orientation = Utils.getImageOrientation(uri.getPath());
		Bitmap bmRaw = null;
		Bitmap result = null;
		try {
			ContentResolver cr = context.getContentResolver();
			// avoid occurs error -- OOM
			// first, create null bitmap for obtain bitmap's width/height;
			// second,create real bitmap and have scaled.
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(cr.openInputStream(uri), null, options);
			int srcWidth = options.outWidth;
			int srcHeight = options.outHeight;
			if (orientation == ExifInterface.ORIENTATION_ROTATE_90
					|| orientation == ExifInterface.ORIENTATION_ROTATE_270) {
				// each other bitmap's width & height if need rotate
				srcWidth = options.outHeight;
				srcHeight = options.outWidth;
			}
			options.inJustDecodeBounds = false;
			if (srcWidth > maxWidth) {
				// zoom-in
				float sx = srcWidth / (float) maxWidth;
				int maxHeight = (int) (srcHeight / sx);
				bmRaw = BitmapFactory.decodeStream(cr.openInputStream(uri),
						null, options);
				result = Bitmap.createScaledBitmap(bmRaw, maxWidth, maxHeight,
						false);
				bmRaw.recycle();
			} else {
				result = BitmapFactory.decodeStream(cr.openInputStream(uri),
						null, options);
			}

			if (result == null) {
				// select non-image
				return null;
			}
		} catch (Exception e) {
			return null;
		}

		if (orientation != ExifInterface.ORIENTATION_NORMAL
				&& orientation != ExifInterface.ORIENTATION_UNDEFINED) {
			Matrix matrix = new Matrix();
			matrix.reset();
			if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
				matrix.postRotate(90);
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
				matrix.postRotate(180);
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
				matrix.postRotate(270);
			}
			Bitmap cropBitmap = Bitmap.createBitmap(result, 0, 0,
					bmRaw.getWidth(), bmRaw.getHeight(), matrix, true);
			return cropBitmap;
		}
		return result;
	}

	public static Bitmap ajustBitmap(Uri uri, Context context, int dstWidth,
			int dstHeight) {
		int orientation = Utils.getImageOrientation(uri.getPath());
		Bitmap bmRaw = null;
		try {
			ContentResolver cr = context.getContentResolver();
			// avoid occurs error -- OOM
			// first, create null bitmap for obtain bitmap's width/height;
			// second,create real bitmap and have scaled.
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(cr.openInputStream(uri), null, options);
			int srcWidth = options.outWidth;
			int srcHeight = options.outHeight;
			if (orientation == ExifInterface.ORIENTATION_ROTATE_90
					|| orientation == ExifInterface.ORIENTATION_ROTATE_270) {
				// each other bitmap's width & height if need rotate
				srcWidth = options.outHeight;
				srcHeight = options.outWidth;
			}
			if (srcWidth > dstWidth || srcHeight > dstHeight) {
				// zoom-in
				float rate = computeScaleRate(srcWidth, srcHeight, dstWidth,
						dstHeight);
				options.inSampleSize = (int) Math.floor(rate);
			} else {
				options.inSampleSize = 1;
			}
			options.inJustDecodeBounds = false;
			bmRaw = BitmapFactory.decodeStream(cr.openInputStream(uri), null,
					options);
			if (bmRaw == null) {
				// select non-image
				return null;
			}
		} catch (Exception e) {
			return null;
		}

		if (orientation != ExifInterface.ORIENTATION_NORMAL
				&& orientation != ExifInterface.ORIENTATION_UNDEFINED) {
			Matrix matrix = new Matrix();
			matrix.reset();
			if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
				matrix.postRotate(90);
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
				matrix.postRotate(180);
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
				matrix.postRotate(270);
			}
			Bitmap cropBitmap = Bitmap.createBitmap(bmRaw, 0, 0,
					bmRaw.getWidth(), bmRaw.getHeight(), matrix, true);
			return cropBitmap;
		}
		return bmRaw;
	}

	public static Bitmap roateBitmap(Bitmap src, int degrees) {
		Matrix matrix = new Matrix();
		matrix.reset();
		matrix.postRotate(degrees);
		Bitmap cropBitmap = Bitmap.createBitmap(src, 0, 0, src.getWidth(),
				src.getHeight(), matrix, true);
		return cropBitmap;
	}

	public static float computeScaleRate(int srcWidth, int srcHeight,
			int dstWidth, int dstHeight) {
		float xRate = srcWidth / (float) dstWidth;
		float yRate = srcHeight / (float) dstHeight;
		return xRate > yRate ? xRate : yRate;
	}

	public static int getImageOrientation(String filePath) {
		int result = ExifInterface.ORIENTATION_NORMAL;
		ExifInterface exif;
		try {
			exif = new ExifInterface(filePath);
			result = Integer.valueOf(exif
					.getAttribute(ExifInterface.TAG_ORIENTATION));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		switch (result) {
		case ExifInterface.ORIENTATION_NORMAL:
			System.out.println("ORIENTATION_NORMAL");
			break;
		case ExifInterface.ORIENTATION_ROTATE_90:
			System.out.println("ORIENTATION_ROTATE_90");
			break;
		case ExifInterface.ORIENTATION_ROTATE_180:
			System.out.println("ORIENTATION_ROTATE_180");
			break;
		case ExifInterface.ORIENTATION_ROTATE_270:
			System.out.println("ORIENTATION_ROTATE_270");
			break;
		default:
			break;
		}
		return result;
	}

	/**
	 * save to cache
	 * 
	 * @param context
	 * @param bitmap
	 * @return Uri if saved successed, others null
	 */
	public static Uri savePhoto(Context context, Bitmap bitmap) {
		File appFolder = new File(context.getCacheDir().getPath());
		File photoFile = new File(appFolder, String.valueOf("mksns_"
				+ System.currentTimeMillis())
				+ ".jpg");
		try {
			if (bitmap == null) {
				return null;
			} else {
				bitmap.compress(Bitmap.CompressFormat.JPEG, 80,
						new FileOutputStream(photoFile));
				return Uri.fromFile(photoFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean externalStorageStateAvailable() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	public static void dumpJSONObject(JSONObject jsonHolder) {
		if (Constant.DEBUG) {
			if (jsonHolder == null) {
				jsonHolder = new JSONObject();
			}
			Iterator<?> iterator = jsonHolder.keys();
			System.out.println("######## dump json object : #######");
			while (iterator.hasNext()) {
				try {
					String key = iterator.next().toString();
					String value = jsonHolder.get(key).toString();
					System.out.println(key + "\t" + value);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			System.out.println("####################################");
		}
	}

	public static void dial(Context context, String phone) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_DIAL);
		intent.setData(Uri.parse("tel:" + phone));
		context.startActivity(intent);
	}

	public static void sms(Context context, String phone, String msg) {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_SENDTO);
		intent.setData(Uri.parse("smsto:" + phone));
		intent.putExtra("sms_body", "#羽众#" + msg);
		context.startActivity(intent);
	}
}
