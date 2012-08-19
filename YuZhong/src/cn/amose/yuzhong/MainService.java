package cn.amose.yuzhong;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import cn.amose.yuzhong.database.PreferenceHelper;
import cn.amose.yuzhong.http.GetNoti;
import cn.amose.yuzhong.model.Noti;
import cn.amose.yuzhong.util.Constant;

public class MainService extends Service {
	private final int NOTIFICATION_BULLETIN = 19880822;
	private final int NOTIFICATION_ACTIVITY = 19881003;
	private NotificationManager mNM;
	private final IBinder mBinder = new MainBinder();
	private final Thread mWorkerThread = new WorkerThread();

	private class WorkerThread extends Thread {
		private static final int INTERVAL = 5 * 60 * 1000;
		private GetNoti mGetNoti;
		private JSONObject mNotiJSONObject;

		private void handleNoti() {
			try {
				// 检查新的活动
				if (mGetNoti == null) {
					mGetNoti = new GetNoti(MainService.this);
					mNotiJSONObject = new JSONObject();
					mNotiJSONObject.put(Constant.JSON_KEY_UID,
							PreferenceHelper.getAccountId());
				}
				mGetNoti.start(mNotiJSONObject);
				if (mGetNoti.getErrorMessage() == null) {
					ArrayList<Noti> list = mGetNoti.getResult();
					if (list != null && !list.isEmpty()) {
						// 发现新的activity
						for (Noti noti : list) {
							switch (noti.getType()) {
							case Noti.NOTI_TYPE_ACTIVITY:
								showNotification(NOTIFICATION_ACTIVITY,
										noti.getTitle(), noti.getText());
								break;
							case Noti.NOTI_TYPE_PRODUCT:
							case Noti.NOTI_TYPE_BULLETIN:
							default:
								showNotification(NOTIFICATION_BULLETIN,
										noti.getTitle(), noti.getText());
								break;
							}
						}
					}
				}
			} catch (JSONException e) {
				if (Constant.DEBUG) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void run() {
			while (true) {
				try {
					// TODO 检查网络
					if (Constant.DEBUG) {
						System.out.println("Yu Zhong service is running .....");
					}
					handleNoti();
					Thread.sleep(INTERVAL);
				} catch (InterruptedException e) {
					if (Constant.DEBUG) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public class MainBinder extends Binder {
		MainService getService() {
			return MainService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		if (!mWorkerThread.isAlive()) {
			try {
				mWorkerThread.start();
			} catch (Exception e) {
				if (Constant.DEBUG) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// mWorkerThread.interrupt();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	private void showNotification(int notifcationId, String title, String text) {
		Class<? extends Object> activity;
		if (notifcationId == NOTIFICATION_ACTIVITY) {
			activity = ActivityActivity.class;
		} else {
			activity = BulletinActivity.class;
		}
		// Set the icon, scrolling text and timestamp
		Notification notification = new Notification(R.drawable.ic_launcher,
				text, System.currentTimeMillis());

		// The PendingIntent to launch our activity if the user selects this
		// notification
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, activity), 0);

		// Set the info for the views that show in the notification panel.
		notification.setLatestEventInfo(this, title, text, contentIntent);

		// Send the notification.
		mNM.notify(notifcationId, notification);
	}
}
