package cn.amose.yuzhong.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import cn.amose.yuzhong.R;
import cn.amose.yuzhong.util.Constant;
import cn.amose.yuzhong.util.Utils;

/**
 * Creates json service
 */
public abstract class HttpService {
	public static final String REQUEST_METHOD_GET = "GET";
	public static final String REQUEST_METHOD_POST = "POST";
	protected Context mContext;
	protected String mErrorMessage;
	protected String mRequestMethod;

	public HttpService(Context context) {
		mContext = context;
		mRequestMethod = REQUEST_METHOD_GET;
	}

	public Context getContext() {
		return mContext;
	}

	/**
	 * Uses the BufferedReader.readLine() to convert the InputStream to String
	 * 
	 * @param is
	 * @return
	 */
	private String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is),
				8 * 1024);
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			if (Constant.DEBUG) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	protected String generateJSONHolderString(JSONObject holder) {
		StringBuilder sbReqeustQueryString;
		try {
			if (holder == null) {
				return "";
			}
			sbReqeustQueryString = new StringBuilder();
			Iterator<?> iterator = holder.keys();
			String key;
			String value;
			while (iterator.hasNext()) {
				try {
					key = iterator.next().toString();
					value = URLEncoder.encode(holder.get(key).toString(),
							"utf-8");
					sbReqeustQueryString.append(key).append("=").append(value)
							.append("&");
				} catch (JSONException e) {
					if (Constant.DEBUG) {
						e.printStackTrace();
					}
				}
			}
			if (sbReqeustQueryString.length() > 0) {
				sbReqeustQueryString
						.setLength(sbReqeustQueryString.length() - 1);
				return sbReqeustQueryString.toString();
			} else {
				return "";
			}
		} catch (UnsupportedEncodingException e) {
			if (Constant.DEBUG) {
				e.printStackTrace();
			}
		}
		return "";
	}

	/**
	 * Creates urlconnection
	 * 
	 * @param httpAction
	 * @param jsonHolder
	 * @return
	 */
	private HttpURLConnection createURLConnection(String httpAction,
			JSONObject jsonHolder) {
		HttpURLConnection connection = null;
		if (Utils.checkNetwork(mContext)) {
			try {
				String paramsJsonString = generateJSONHolderString(jsonHolder);
				if (mRequestMethod.equals(REQUEST_METHOD_GET)) {
					httpAction += "?" + paramsJsonString;
				}
				URL url = new URL(httpAction);
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod(mRequestMethod);
				connection.setConnectTimeout(10000);
				connection.setUseCaches(false);
				connection.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				if (!mRequestMethod.equals(REQUEST_METHOD_GET)) {
					connection.setDoInput(true);
					connection.setDoOutput(true);
					connection.getOutputStream().write(
							paramsJsonString.getBytes());
				}
			} catch (SocketTimeoutException e) {
				if (Constant.DEBUG) {
					e.printStackTrace();
				}
				if (connection != null) {
					connection.disconnect();
				}
			} catch (IOException e) {
				if (Constant.DEBUG) {
					e.printStackTrace();
				}
				if (connection != null) {
					connection.disconnect();
				}
			}
		}

		return connection;
	}

	public void start(JSONObject jsonHolder) {
		clearErrorMessage();
		if (!beforeCreateConnection(jsonHolder)) {
			return;
		}
		if (Constant.DEBUG) {
			dump(jsonHolder);
		}
		HttpURLConnection connection = createURLConnection(getServiceUri(),
				jsonHolder);
		try {
			// System.out.println("jsonHolder " + jsonHolder.toString());
			if (connection != null) {
				InputStream instream = connection.getInputStream();
				int code = connection.getResponseCode();
				if (code == HttpURLConnection.HTTP_OK) {
					String stringResult = convertStreamToString(instream);
					if (Constant.DEBUG) {
						System.out
								.println(" ############### htttp response result : #######################\r\n\r\n"
										+ stringResult
										+ "\r\n\r\n#########################################");
					}
					if (stringResult.length() > 0) {
						process(stringResult);
					} else {
						mErrorMessage = mContext
								.getString(R.string.common_toast_connectionnodata);
					}
				}
				instream.close();
			} else {
				// Connection failed
				mErrorMessage = mContext
						.getString(R.string.common_toast_connectionfailed);
			}
		} catch (IOException e) {
			mErrorMessage = mContext
					.getString(R.string.common_toast_connectionfailed);
			if (Constant.DEBUG) {
				e.printStackTrace();
			}
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	private void dump(JSONObject jsonHolder) {
		if (jsonHolder == null) {
			jsonHolder = new JSONObject();
		}
		Iterator<?> iterator = jsonHolder.keys();
		System.out.println("\r\n#### input params start: ####");
		System.out.println("URI : " + getServiceUri());
		System.out.println("REQUEST METHOD : " + mRequestMethod);
		while (iterator.hasNext()) {
			try {
				String key = iterator.next().toString();
				String value = jsonHolder.get(key).toString();
				System.out.println(key + "\t" + value);
			} catch (JSONException e) {
				if (Constant.DEBUG) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("#### input params end: ####");
	}

	public String getErrorMessage() {
		return mErrorMessage;
	}

	public void clearErrorMessage() {
		mErrorMessage = null;
	}

	/**
	 * Do something before create connection
	 * 
	 * @param jsonHolder
	 *            　transform it and hand in {@link #process(JSONObject)}
	 * @return create connection and start if return true ,otherwise not start.
	 */
	protected boolean beforeCreateConnection(JSONObject jsonHolder) {
		return true;
	}

	protected abstract String getServiceUri();

	protected abstract void process(String result);

	public abstract <T> T getResult();

	public void setRequestMethod(String method) {
		mRequestMethod = method;
	}
}
