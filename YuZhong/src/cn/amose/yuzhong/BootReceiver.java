package cn.amose.yuzhong;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import cn.amose.yuzhong.util.Constant;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent arg1) {
		if (Constant.DEBUG) {
			Log.d("YuZhong", "YuZhong 开机自启动服务开启...");
		}
		Intent bootIntent = new Intent(context, MainService.class);
		context.startService(bootIntent);
	}
}