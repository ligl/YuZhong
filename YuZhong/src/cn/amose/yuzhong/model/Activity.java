package cn.amose.yuzhong.model;

public class Activity {
	private int mAId;
	private String mTitle;
	private String mAddr;
	private long mSignupBeginTime;
	private long mSignupEndTime;
	private long mActivityTime;
	private String mDesc;
	private String mPhoto;
	private long mRegTime;
	private User mManager;

	public Activity() {

	}

	public Activity(int aid, String title, String addr, long signupBeginTime,
			long signupEndTime, long activityTime, String desc, String photo,
			long regTime, User manager) {
		this.mAId = aid;
		this.mTitle = title;
		this.mAddr = addr;
		this.mSignupBeginTime = signupBeginTime;
		this.mSignupEndTime = signupEndTime;
		this.mActivityTime = activityTime;
		this.mDesc = desc;
		this.mPhoto = photo;
		this.mRegTime = regTime;
		this.mManager = manager;
	}

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		this.mTitle = title;
	}

	public int getAId() {
		return mAId;
	}

	public void setAId(int aid) {
		this.mAId = aid;
	}

	public String getAddr() {
		return mAddr;
	}

	public void setAddr(String addr) {
		this.mAddr = addr;
	}

	public long getSignupBeginTime() {
		return mSignupBeginTime;
	}

	public void setSignupBeginTime(long signupBeginTime) {
		this.mSignupBeginTime = signupBeginTime;
	}

	public long getSignupEndTime() {
		return mSignupEndTime;
	}

	public void setSignupEndTime(long signupEndTime) {
		this.mSignupEndTime = signupEndTime;
	}

	public long getActivityTime() {
		return mActivityTime;
	}

	public void setActivityTime(long activityTime) {
		this.mActivityTime = activityTime;
	}

	public String getDesc() {
		return mDesc;
	}

	public void setDesc(String desc) {
		this.mDesc = desc;
	}

	public String getPhoto() {
		return mPhoto;
	}

	public void setPhoto(String photo) {
		this.mPhoto = photo;
	}

	public long getRegTime() {
		return mRegTime;
	}

	public void setRegTime(long regTime) {
		this.mRegTime = regTime;
	}

	public User getManager() {
		return mManager;
	}

	public void setManager(User manager) {
		this.mManager = manager;
	}

}
