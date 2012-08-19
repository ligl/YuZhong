package cn.amose.yuzhong.model;

public class Noti {
	public static final int NOTI_TYPE_ACTIVITY = 1;
	public static final int NOTI_TYPE_BULLETIN = 2;
	public static final int NOTI_TYPE_PRODUCT = 3;
	private int mId;
	private int mType;
	private String mTitle;
	private String mText;

	public Noti(int id, int type, String title, String text) {
		this.mId = id;
		this.mType = type;
		this.mTitle = title;
		this.mText = text;
	}

	public int getId() {
		return mId;
	}

	public void setId(int mId) {
		this.mId = mId;
	}

	public int getType() {
		return mType;
	}

	public void setType(int mType) {
		this.mType = mType;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public String getText() {
		return mText;
	}

	public void setText(String mText) {
		this.mText = mText;
	}

}
