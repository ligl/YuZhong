package cn.amose.yuzhong.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {
	public static final String INTENT_ACTION_USER = "user";
	private int mId;
	private String mMobile;
	private String mName;
	private String mAvatarUrl;
	private int mAge;
	private int mGender;
	private String mPassword;

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

	public int getAge() {
		return mAge;
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

	/**
	 * unit week
	 * 
	 * @param age
	 */
	public void setAge(int age) {
		this.mAge = age;
	}

	/**
	 * male : 1, female : 0
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
		dest.writeInt(mAge);
		dest.writeInt(mGender);
		dest.writeString(mPassword);
	}

	public User(Parcel in) {
		mId = in.readInt();
		mAvatarUrl = in.readString();
		mMobile = in.readString();
		mName = in.readString();
		mAge = in.readInt();
		mGender = in.readInt();
		mPassword = in.readString();
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
