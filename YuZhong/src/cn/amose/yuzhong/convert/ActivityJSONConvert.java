package cn.amose.yuzhong.convert;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.amose.yuzhong.model.Activity;

public class ActivityJSONConvert {

	public static ArrayList<Activity> convertJsonArrayToItemList(
			JSONArray jsonArray) throws JSONException {
		int length = jsonArray.length();
		ArrayList<Activity> list = new ArrayList<Activity>();
		for (int i = 0; i < length; i++) {
			list.add(convertJsonToItem(jsonArray.getJSONObject(i)));
		}
		return list;
	}

	public static Activity convertJsonToItem(JSONObject json)
			throws JSONException {
		return new Activity(json.getInt("aid"), json.getString("title"),
				json.getString("addr"), json.getLong("signupbegintime"),
				json.getLong("signupendtime"), json.getLong("atime"),
				json.getString("desc"), json.getString("photo"),
				json.getLong("regtime"),
				UserJSONConvert.convertJsonToItem(json));
	}
}
