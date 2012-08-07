package cn.amose.yuzhong.model;

public class Bulletin {
	public static final int TYPE_ALL = 0;
	public static final int TYPE_TEXT = 1;
	public static final int TYPE_ACTIVITY = 2;
	public static final int TYPE_NEWS = 3;
	private int mBId;
	private int mUId;
	private String mUName;
	private String mUMobile;
	private String mTitle;
	private String mContentJson;
	private int mType;
	private long mTime;

	public Bulletin(int ｂid, int uid, String uname, String umobile,
			String title, String contentJson, int type, long time) {
		super();
		this.mBId = ｂid;
		this.mUId = uid;
		this.mUName = uname;
		this.mUMobile = umobile;
		this.mTitle = title;
		this.mContentJson = contentJson;
		this.mType = type;
		this.mTime = time;
	}

	public int getBId() {
		return mBId;
	}

	public void setBId(int bid) {
		this.mBId = bid;
	}

	public int getUId() {
		return mUId;
	}

	public void setUId(int uid) {
		this.mUId = uid;
	}

	public String getUName() {
		return mUName;
	}

	public void setUName(String uname) {
		this.mUName = uname;
	}

	public String getUMobile() {
		return mUMobile;
	}

	public void setUMobile(String umobile) {
		this.mUMobile = umobile;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		this.mTitle = title;
	}

	public String getContentJson() {
		return mContentJson;
	}

	public void setContentJson(String contentJson) {
		this.mContentJson = contentJson;
	}

	public int getType() {
		return mType;
	}

	public void setType(int type) {
		this.mType = type;
	}

	public long getTime() {
		return mTime;
	}

	public void setTime(long time) {
		this.mTime = time;
	}

}
