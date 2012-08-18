package cn.amose.yuzhong.http;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import cn.amose.yuzhong.R;
import cn.amose.yuzhong.convert.ActivityJSONConvert;
import cn.amose.yuzhong.model.Activity;
import cn.amose.yuzhong.util.Constant;

public class GetActivities extends HttpService {
	private static final String ACTION = Constant.SERVER + "getactivities";
	private ArrayList<Activity> mActivityList;

	public GetActivities(Context context) {
		super(context);
	}

	@Override
	protected void process(String result) {
		try {
			JSONObject jsonHolder = new JSONObject(result);
			int jsonCode = jsonHolder.getInt(Constant.JSON_KEY_CODE);
			switch (jsonCode) {
			case Constant.JSON_CODE_SUCCESS:
				mErrorMessage = null;
				mActivityList = ActivityJSONConvert
						.convertJsonArrayToItemList(jsonHolder
								.getJSONArray("activities"));
				break;
			case Constant.JSON_PARAMSINVALID:
				mActivityList = null;
				mErrorMessage = mContext
						.getString(R.string.common_toast_paramsinvalid);
				break;
			default:
				mActivityList = null;
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
		return (T) mActivityList;
	}

	@Override
	protected String getServiceUri() {
		return ACTION;
	}
}
