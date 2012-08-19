package cn.amose.yuzhong.http;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import cn.amose.yuzhong.R;
import cn.amose.yuzhong.convert.NotiJSONConvert;
import cn.amose.yuzhong.model.Noti;
import cn.amose.yuzhong.util.Constant;

public class GetNoti extends HttpService {
	private static final String ACTION = Constant.SERVER + "getnoti";
	private ArrayList<Noti> mNotiList;

	public GetNoti(Context context) {
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
				mNotiList = NotiJSONConvert
						.convertJsonArrayToItemList(jsonHolder
								.getJSONArray("noti"));
				break;
			case Constant.JSON_PARAMSINVALID:
				mNotiList = null;
				mErrorMessage = mContext
						.getString(R.string.common_toast_paramsinvalid);
				break;
			default:
				mNotiList = null;
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
		return (T) mNotiList;
	}

	@Override
	protected String getServiceUri() {
		return ACTION;
	}
}
