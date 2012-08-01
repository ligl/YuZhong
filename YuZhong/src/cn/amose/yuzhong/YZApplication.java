package cn.amose.yuzhong;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import cn.amose.yuzhong.database.PreferenceHelper;
import cn.amose.yuzhong.image.ImageCache;

/**
 * Defines various methods that should be overridden in order to style your
 * application.
 * 
 * @author Cyril Mottier
 */
public class YZApplication extends Application {

	/**
	 * Used for receiving low memory system notification. You should definitely
	 * use it in order to clear caches and not important data everytime the
	 * system need memory.
	 * 
	 * @author Cyril Mottier
	 * @see YZApplication#registerOnLowMemoryListener(OnLowMemoryListener)
	 * @see YZApplication#unregisterOnLowMemoryListener(OnLowMemoryListener)
	 */
	public static interface OnLowMemoryListener {
		public void onLowMemoryReceived();
	}

	private static final int CORE_POOL_SIZE = 5;

	private static final ThreadFactory sThreadFactory = new ThreadFactory() {
		private final AtomicInteger mCount = new AtomicInteger(1);

		public Thread newThread(Runnable r) {
			return new Thread(r, "GreenDroid thread #"
					+ mCount.getAndIncrement());
		}
	};

	@Override
	public void onCreate() {
		super.onCreate();
		PreferenceHelper.init(getApplicationContext());
	}

	private ExecutorService mExecutorService;
	private ImageCache mImageCache;
	private ArrayList<WeakReference<OnLowMemoryListener>> mLowMemoryListeners;

	public YZApplication() {
		mLowMemoryListeners = new ArrayList<WeakReference<OnLowMemoryListener>>();
	}

	public ExecutorService getExecutor() {
		if (mExecutorService == null) {
			mExecutorService = Executors.newFixedThreadPool(CORE_POOL_SIZE,
					sThreadFactory);
		}
		return mExecutorService;
	}

	public ImageCache getImageCache() {
		if (mImageCache == null) {
			mImageCache = new ImageCache(this);
		}
		return mImageCache;
	}

	/**
	 * Returns the class of the home {@link Activity}. The home {@link Activity}
	 * is the main entrance point of your application. This is usually where the
	 * dashboard/general menu is displayed.
	 * 
	 * @return The Class of the home {@link Activity}
	 */
	public Class<?> getHomeActivityClass() {
		return null;
	}

	/**
	 * Each application may have an "application intent" which will be used when
	 * the user clicked on the application button.
	 * 
	 * @return The main application {@link Intent} (may be null if you don't
	 *         want to use the main application {@link Intent} feature)
	 */
	public Intent getMainApplicationIntent() {
		return null;
	}

	/**
	 * Adds a new listener to the list
	 * 
	 * @param listener
	 *            The listener to unregister
	 * @see {@link OnLowMemoryListener}
	 */
	public void registerOnLowMemoryListener(OnLowMemoryListener listener) {
		if (listener != null) {
			mLowMemoryListeners.add(new WeakReference<OnLowMemoryListener>(
					listener));
		}
	}

	/**
	 * Removes a previously registered listener
	 * 
	 * @param listener
	 *            The listener to unregister
	 * @see {@link OnLowMemoryListener}
	 */
	public void unregisterOnLowMemoryListener(OnLowMemoryListener listener) {
		if (listener != null) {
			int i = 0;
			while (i < mLowMemoryListeners.size()) {
				final OnLowMemoryListener l = mLowMemoryListeners.get(i).get();
				if (l == null || l == listener) {
					mLowMemoryListeners.remove(i);
				} else {
					i++;
				}
			}
		}
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		int i = 0;
		while (i < mLowMemoryListeners.size()) {
			final OnLowMemoryListener listener = mLowMemoryListeners.get(i)
					.get();
			if (listener == null) {
				mLowMemoryListeners.remove(i);
			} else {
				listener.onLowMemoryReceived();
				i++;
			}
		}
	}
}
