package cn.amose.yuzhong.http;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import cn.amose.yuzhong.R;
import cn.amose.yuzhong.convert.BulletinJSONConvert;
import cn.amose.yuzhong.model.Bulletin;
import cn.amose.yuzhong.util.Constant;

public class GetBulletins extends HttpService {
	private static final String ACTION = Constant.SERVER + "getbulletins";
	private Context mContext;
	private ArrayList<Bulletin> mBulletinList;

	public GetBulletins(Context context) {
		super(context);
		mContext = context;
		mRequestMethod = REQUEST_METHOD_POST;
	}

	@Override
	protected void process(String result) {
		try {
			JSONObject jsonHolder = new JSONObject(result);
			int jsonCode = jsonHolder.getInt(Constant.JSON_KEY_CODE);
			switch (jsonCode) {
			case Constant.JSON_CODE_SUCCESS:
				mErrorMessage = null;
				mBulletinList = BulletinJSONConvert
						.convertJsonArrayToItemList(jsonHolder
								.getJSONArray("bulletins"));
				break;
			case Constant.JSON_LOGIN_USER_PASSWORD_ERROR:
				mBulletinList = null;
				mErrorMessage = mContext
						.getString(R.string.login_toast_namepassworderror);
				break;
			default:
				mBulletinList = null;
				mErrorMessage = mContext.getString(R.string.common_toast_error);
				break;
			}
		} catch (JSONException e) {
			// How, my gad!!
			mErrorMessage = mContext
					.getString(R.string.common_toast_jsonparseerror);
			if (Constant.DEBUG) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getResult() {
		return (T) mBulletinList;
	}

	@Override
	protected String getServiceUri() {
		return ACTION;
	}
}
