/*
 * Copyright (C) 2010 Cyril Mottier (http://www.cyrilmottier.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.amose.yuzhong.image;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import cn.amose.yuzhong.YZApplication.OnLowMemoryListener;
import cn.amose.yuzhong.util.Constant;
import cn.amose.yuzhong.util.AppUtils;
import cn.amose.yuzhong.util.Md5Util;

public class ImageCache implements OnLowMemoryListener {
	private final HashMap<String, SoftReference<Bitmap>> mSoftCache;

	public void onLowMemoryReceived() {
		if (Constant.DEBUG) {
			System.out.println("...........memory lower .... recycle : "
					+ mSoftCache.size());
		}
		mSoftCache.clear();
	}

	// file cache
	private final File mCacheFolder;

	public ImageCache(Context context) {
		mCacheFolder = context.getCacheDir();
		mSoftCache = new HashMap<String, SoftReference<Bitmap>>();
		AppUtils.getYZApplication(context).registerOnLowMemoryListener(this);
	}

	public static ImageCache from(Context context) {
		return AppUtils.getImageCache(context);
	}

	public Bitmap get(String url) {
		final SoftReference<Bitmap> ref = mSoftCache.get(url);
		if (ref != null) {
			final Bitmap bitmap = ref.get();
			if (bitmap == null) {
				mSoftCache.remove(url);
			} else {
				return bitmap;
			}
		}
		String fileName = Md5Util.md5(url);
		File file = new File(mCacheFolder, fileName);
		if (file.exists()) {
			return BitmapFactory.decodeFile(file.getAbsolutePath());
		}
		return null;
	}

	public File getFile(String url) {
		String fileName = Md5Util.md5(url);
		return new File(mCacheFolder, fileName);
	}

	public void put(String url, Bitmap bitmap) {
		mSoftCache.put(url, new SoftReference<Bitmap>(bitmap));
		String fileName = Md5Util.md5(url);
		try {
			File newFile = new File(mCacheFolder, fileName);
			if (newFile.exists() == false) {
				OutputStream out = new FileOutputStream(newFile);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
