package cn.amose.yuzhong.convert;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.amose.yuzhong.model.User;

public class UserJSONConvert {

	public static ArrayList<User> convertJsonArrayToItemList(JSONArray jsonArray)
			throws JSONException {
		int length = jsonArray.length();
		ArrayList<User> userList = new ArrayList<User>();
		for (int i = 0; i < length; i++) {
			userList.add(convertJsonToItem(jsonArray.getJSONObject(i)));
		}
		return userList;
	}

	public static User convertJsonToItem(JSONObject json) throws JSONException {
		return new User(json.getInt("uid"), json.getString("mobile"),
				json.getString("name"), json.optString("avatar", null),
				json.optLong("birthday"), json.optInt("gender",
						User.USER_GENDER_UNKOWN), null, json.optInt("status",
						User.USER_STATUS_UNKOWN));
	}
}
