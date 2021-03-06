package cn.amose.yuzhong.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
	public static final String INTENT_ACTION_USER = "user";
	public static final int USER_STATUS_WATINGAUDIT = 0;
	public static final int USER_STATUS_NORMAL = 1;
	public static final int USER_STATUS_FREEZE = -1;
	public static final int USER_STATUS_UNAUDIT = 2;
	public static final int USER_STATUS_UNKOWN = -999;
	public static final int USER_GENDER_MALE = 0;
	public static final int USER_GENDER_FEMALE = 1;
	public static final int USER_GENDER_UNKOWN = -1;

	private int mId;
	private String mMobile;
	private String mName;
	private String mAvatarUrl;
	private long mBirthday;
	private int mGender;
	private String mPassword;
	private int mStatus;

	public User(int id, String mobile, String name, String avatarUrl,
			long birthday, int gender, String password, int status) {
		this.mId = id;
		this.mMobile = mobile;
		this.mName = name;
		this.mAvatarUrl = avatarUrl;
		this.mBirthday = birthday;
		this.mGender = gender;
		this.mPassword = password;
		this.mStatus = status;
	}

	public User() {
	}

	public int getId() {
		return mId;
	}

	public void setId(int id) {
		this.mId = id;
	}

	public String getMobile() {
		return mMobile;
	}

	public String getName() {
		return mName;
	}

	public String getAvatarUrl() {
		return mAvatarUrl;
	}

	public long getBirthday() {
		return mBirthday;
	}

	public int getGender() {
		return mGender;
	}

	public void setMobile(String mobile) {
		this.mMobile = mobile;
	}

	public void setName(String name) {
		this.mName = name;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.mAvatarUrl = avatarUrl;
	}

	public void setBirthday(long birthday) {
		this.mBirthday = birthday;
	}

	/**
	 * male : 1, female : 0, -1:unkown
	 * 
	 * @param gender
	 */
	public void setGender(int gender) {
		this.mGender = gender;
	}

	public String getPassword() {
		return mPassword;
	}

	public void setPassword(String password) {
		this.mPassword = password;
	}

	public int getStatus() {
		return mStatus;
	}

	public void setStatus(int status) {
		this.mStatus = status;
	}

	/** -----------parcelable-------- */
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(mId);
		dest.writeString(mAvatarUrl);
		dest.writeString(mMobile);
		dest.writeString(mName);
		dest.writeLong(mBirthday);
		dest.writeInt(mGender);
		dest.writeString(mPassword);
		dest.writeInt(mStatus);
	}

	public User(Parcel in) {
		mId = in.readInt();
		mAvatarUrl = in.readString();
		mMobile = in.readString();
		mName = in.readString();
		mBirthday = in.readLong();
		mGender = in.readInt();
		mPassword = in.readString();
		mStatus = in.readInt();
	}

	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		public User createFromParcel(Parcel in) {
			return new User(in);
		}

		public User[] newArray(int size) {
			return new User[size];
		}
	};
}
