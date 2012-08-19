package cn.amose.yuzhong.convert;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.amose.yuzhong.model.Noti;

public class NotiJSONConvert {

	public static ArrayList<Noti> convertJsonArrayToItemList(JSONArray jsonArray)
			throws JSONException {
		int length = jsonArray.length();
		ArrayList<Noti> list = new ArrayList<Noti>();
		for (int i = 0; i < length; i++) {
			list.add(convertJsonToItem(jsonArray.getJSONObject(i)));
		}
		return list;
	}

	public static Noti convertJsonToItem(JSONObject json) throws JSONException {
		return new Noti(json.getInt("nid"), json.getInt("type"),
				json.getString("title"), json.getString("text"));
	}
}
