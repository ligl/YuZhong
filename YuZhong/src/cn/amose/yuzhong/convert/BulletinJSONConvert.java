package cn.amose.yuzhong.convert;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.amose.yuzhong.model.Bulletin;

public class BulletinJSONConvert {

	public static ArrayList<Bulletin> convertJsonArrayToItemList(
			JSONArray jsonArray) throws JSONException {
		int length = jsonArray.length();
		ArrayList<Bulletin> list = new ArrayList<Bulletin>();
		for (int i = 0; i < length; i++) {
			list.add(convertJsonToItem(jsonArray.getJSONObject(i)));
		}
		return list;
	}

	public static Bulletin convertJsonToItem(JSONObject json)
			throws JSONException {
		return new Bulletin(json.getInt("bid"), json.getInt("uid"),
				json.getString("name"), json.getString("mobile"),
				json.getString("title"), json.getString("content"),
				json.getInt("type"), json.getLong("time"));
	}
}
