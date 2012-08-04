package cn.amose.yuzhong.util;

public final class Constant {
	// TODO change values when release
	public static final String SERVER = "http://yuzhong.sinaapp.com/s/";
	public static final boolean DEBUG = true;

	public static final String JSON_KEY_CODE = "code";
	public static final String JSON_KEY_MSG = "msg";
	public static final String JSON_KEY_DATA = "data";
	public static final int JSON_CODE_SUCCESS = 200;
	public static final int JSON_LOGIN_USER_PASSWORD_ERROR = 402;
	public static final int JSON_REGISTER_MOBILE_INVALID = 403;
	public static final int JSON_REGISTER_EMPTY_PASSWORD = 404;
	public static final int JSON_REGISTER_EMPTY_NAME = 405;
	public static final int JSON_REGISTER_FAILED = 406;
	public static final int JSON_UPDATEPROFILE_FAILED = 407;
	public static final int JSON_UPDATEUSERSTATUS_INVALID = 408;
	public static final int JSON_UPDATEUSERSTATUS_FAILED = 409;
	public static final int JSON_UPDATEUSERSTATUS_NOTPERMISSION = 410;

	public static final String JSON_KEY_PAGE_NUMBER = "pagenumber";
	public static final String JSON_KEY_PAGE_SIZE = "pagesize";

	// request code
	public static final int REQUEST_CODE_TAKEAPICTURE = 1;
	public static final int REQUEST_CODE_SELECTFILE = 2;
	public static final int REQUEST_CODE_IMAGECROP = 3;

	// version update
	public static final String JSON_KEY_UPDATER = "updater";
	public static final String JSON_KEY_VERSIONNAME = "versionname";
	public static final String JSON_KEY_VERSIONCODE = "versioncode";
	public static final String JSON_KEY_DESCRIPTION = "description";
	public static final String JSON_KEY_APKURL = "url";
	public static final String JSON_KEY_DATE = "date";
	public static final String JSON_KEY_LANG = "lang";

	// 7天
	public static final int UPDATE_VERSION_PEROID = 7 * 24 * 60 * 60 * 1000;// ms
	public static final int DOWNLOAD_NEW_MOVIE_PEROID = 1 * 24 * 60 * 60 * 1000;// ms

}
