package cn.amose.yuzhong.database;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import cn.amose.yuzhong.model.User;
import cn.amose.yuzhong.util.Constant;

public final class PreferenceHelper {
	private static final String PREFERENCES_ACCOUNT_ID = "account_id";
	private static final String PREFERENCES_ACCOUNT_LOGIN_NAME = "account_loginname";
	private static final String PREFERENCES_ACCOUNT_NICKNAME = "account_nickname";
	private static final String PREFERENCES_ACCOUNT_BIRTHDAY = "account_age";
	private static final String PREFERENCES_ACCOUNT_GENDER = "account_gender";
	private static final String PREFERENCES_ACCOUNT_AVATAR = "account_avatarurl";
	private static final String PREFERENCES_ACCOUNT_PASSWORD = "account_password";
	private static final String PREFERENCES_VERSION_UPDATE_TIME = "update_time";
	private static final String PREFERENCES_FIRST_STARTUP = "fiststartup";

	private static SharedPreferences sSHARED_REFERENCES = null;
	private static User sUSER;
	private static Context sAPPLICATION_CONTEXT;

	private PreferenceHelper() {
	}

	public static void init(Context context) {
		if (sSHARED_REFERENCES == null) {
			sAPPLICATION_CONTEXT = context.getApplicationContext();
			sSHARED_REFERENCES = PreferenceManager
					.getDefaultSharedPreferences(sAPPLICATION_CONTEXT);
		}
	}

	public static boolean initDefaultAccount(User user) {
		return updateDefaultAccount(user);
	}

	public static boolean updateDefaultAccount(User user) {
		Editor editor = sSHARED_REFERENCES.edit();
		editor.putLong(PREFERENCES_ACCOUNT_BIRTHDAY, user.getBirthday());
		editor.putString(PREFERENCES_ACCOUNT_LOGIN_NAME, user.getMobile());
		editor.putString(PREFERENCES_ACCOUNT_NICKNAME, user.getName());
		editor.putInt(PREFERENCES_ACCOUNT_ID, user.getId());
		editor.putInt(PREFERENCES_ACCOUNT_GENDER, user.getGender());
		editor.putString(PREFERENCES_ACCOUNT_AVATAR, user.getAvatarUrl());
		editor.putString(PREFERENCES_ACCOUNT_PASSWORD, user.getPassword());

		boolean flag = editor.commit();
		if (flag) {
			sUSER = user;
		}
		return flag;
	}

	public static User getDefaultAccount() {
		if (sUSER == null) {
			sUSER = new User();
			sUSER.setBirthday(sSHARED_REFERENCES.getLong(
					PREFERENCES_ACCOUNT_BIRTHDAY, 0));
			sUSER.setGender(sSHARED_REFERENCES.getInt(
					PREFERENCES_ACCOUNT_GENDER, 0));
			sUSER.setId(sSHARED_REFERENCES.getInt(PREFERENCES_ACCOUNT_ID, 0));
			sUSER.setMobile(sSHARED_REFERENCES.getString(
					PREFERENCES_ACCOUNT_LOGIN_NAME, null));
			sUSER.setName(sSHARED_REFERENCES.getString(
					PREFERENCES_ACCOUNT_NICKNAME, null));
			sUSER.setAvatarUrl(sSHARED_REFERENCES.getString(
					PREFERENCES_ACCOUNT_AVATAR, null));
			sUSER.setPassword(sSHARED_REFERENCES.getString(
					PREFERENCES_ACCOUNT_PASSWORD, null));
		}
		return sUSER;
	}

	public static SharedPreferences getSharedPreferences() {
		return sSHARED_REFERENCES;
	}

	public static int getAccountId() {
		if (sUSER == null) {
			sUSER = getDefaultAccount();
		}
		return sUSER.getId();
	}

	public static void clear() {
		if (Constant.DEBUG) {
			dump();
		}
		// clear internal profile
		sUSER = null;
		Editor editor = sSHARED_REFERENCES.edit();
		editor.clear();
		editor.commit();
		setFirstStarup(false);
	}

	private static void dump() {

		StringBuilder sbWeibo = new StringBuilder();
		sbWeibo.append("########################## MOMKID PREFRENCES {"
				+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(
						System.currentTimeMillis()))
				+ "}##########################\r\n");
		Map<String, ?> prefs = PreferenceHelper.sSHARED_REFERENCES.getAll();
		Iterator<?> iterator = prefs.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String, Object> entry = (Entry<String, Object>) iterator
					.next();
			sbWeibo.append("\t").append(entry.getKey()).append("\t")
					.append(entry.getValue()).append("\r\n");
		}
		sbWeibo.append("########################## MOMKID PREFRENCES ##########################\r\n");
		System.out.println(sbWeibo.toString());
	}

	// version control
	public static boolean setVersionUpdateTime(long time) {
		Editor editor = sSHARED_REFERENCES.edit();
		editor.putLong(PREFERENCES_VERSION_UPDATE_TIME, time);
		return editor.commit();
	}

	public static long getVersionUpdateTime() {
		return sSHARED_REFERENCES.getLong(PREFERENCES_VERSION_UPDATE_TIME, 0);
	}

	public static boolean isFirstStarup() {
		return sSHARED_REFERENCES.getBoolean(PREFERENCES_FIRST_STARTUP, true);
	}

	public static boolean setFirstStarup(boolean isFirstStartup) {
		Editor editor = sSHARED_REFERENCES.edit();
		editor.putBoolean(PREFERENCES_FIRST_STARTUP, isFirstStartup);
		return editor.commit();
	}

}
