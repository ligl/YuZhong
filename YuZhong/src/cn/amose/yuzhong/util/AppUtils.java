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
package cn.amose.yuzhong.util;

import java.util.concurrent.ExecutorService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import cn.amose.yuzhong.BulletinActivity;
import cn.amose.yuzhong.YZApplication;
import cn.amose.yuzhong.image.ImageCache;

/**
 * Class that provides several utility methods related to GreenDroid.
 * 
 * @author Cyril Mottier
 */
public class AppUtils {

	private AppUtils() {
	}

	/**
	 * Return the current {@link YZApplication}
	 * 
	 * @param context
	 *            The calling context
	 * @return The {@link YZApplication} the given context is linked to.
	 */
	public static YZApplication getYZApplication(Context context) {
		return (YZApplication) context.getApplicationContext();
	}

	/**
	 * Return the {@link YZApplication} image cache
	 * 
	 * @param context
	 *            The calling context
	 * @return The image cache of the current {@link YZApplication}
	 */
	public static ImageCache getImageCache(Context context) {
		return getYZApplication(context).getImageCache();
	}

	/**
	 * Return the {@link YZApplication} executors pool.
	 * 
	 * @param context
	 *            The calling context
	 * @return The executors pool of the current {@link YZApplication}
	 */
	public static ExecutorService getExecutor(Context context) {
		return getYZApplication(context).getExecutor();
	}

	public static void startMainActivity(Activity activity) {
		activity.startActivity(new Intent(activity, BulletinActivity.class));
	}
}
