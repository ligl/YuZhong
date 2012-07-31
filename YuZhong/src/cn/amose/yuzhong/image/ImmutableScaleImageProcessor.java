package cn.amose.yuzhong.image;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import cn.amose.yuzhong.util.Constant;

public class ImmutableScaleImageProcessor implements ImageProcessor {

	private int mWidth;
	private int mHeight;
	private final Matrix mMatrix = new Matrix();

	public ImmutableScaleImageProcessor(int width, int height) {
		mWidth = width;
		mHeight = height;
	}

	public Bitmap processImage(Bitmap bitmap) {

		if (bitmap == null) {
			return null;
		}
		mMatrix.reset();

		int bWidth = bitmap.getWidth();
		int bHeight = bitmap.getHeight();
		if (mWidth >= bWidth && mWidth >= bHeight) {
			int dx = (int) ((mWidth - bWidth) * 0.5f + 0.5f);
			int dy = (int) ((mWidth - bHeight) * 0.5f + 0.5f);
			mMatrix.setTranslate(dx, dy);
			Bitmap result = Bitmap.createBitmap(mWidth, mWidth,
					Config.ARGB_8888);
			Canvas canvas = new Canvas(result);
			canvas.drawBitmap(bitmap, mMatrix, null);
			return result;
		} else {
			// 第一步，调整宽高，以width为基准

			// 第二步，等比缩放，以min(width,height)为基准
			float scale = bWidth < bHeight ? mWidth / (float) bWidth : mWidth
					/ (float) bHeight;
			int destWidth = (int) (bWidth * scale);
			int destHeight = (int) (bHeight * scale);
			if (Constant.DEBUG) {
				System.out.println(".....ori w/h ....." + bWidth + " / "
						+ bHeight
						+ " ........width/height.................... "
						+ destWidth + " / " + destHeight);
			}
			mMatrix.setScale(scale, scale);
			Bitmap scaledBitmap = Bitmap.createBitmap(destWidth, destHeight,
					Config.ARGB_8888);
			Canvas canvas = new Canvas(scaledBitmap);
			canvas.drawBitmap(bitmap, mMatrix, null);

			// 第三步，裁剪-居中
			mMatrix.reset();
			int dx = (int) ((mWidth - destWidth) * 0.5f + 0.5f);
			int dy = (int) ((mWidth - destHeight) * 0.5f + 0.5f);
			mMatrix.setTranslate(dx, dy);
			Bitmap result = Bitmap.createBitmap(mWidth, mWidth,
					Config.ARGB_8888);
			canvas = new Canvas(result);
			canvas.drawBitmap(scaledBitmap, mMatrix, null);
			return result;
		}
	}
}
