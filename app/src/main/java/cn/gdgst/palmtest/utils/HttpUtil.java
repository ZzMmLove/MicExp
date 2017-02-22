package cn.gdgst.palmtest.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpUtil {
	// 创建HttpClient对象
	public static DefaultHttpClient httpClient;
	public static String PHPSESSID = null;

	/**
	 *
	 * @param url
	 *            发送请求的URL
	 * @return 服务器响应字符串
	 * @throws Exception
	 */
	public static String getRequest(String url) throws Exception {
		httpClient = new DefaultHttpClient();

		try {
			// 创建HttpGet对象。
			HttpGet get = new HttpGet(url);
			// 发送GET请求
			HttpResponse httpResponse = httpClient.execute(get);
			// 如果服务器成功地返回响应
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				// 获取服务器响应字符串
				String result = EntityUtils.toString(httpResponse.getEntity());
				return result;
			}
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}

		return null;
	}

	/**
	 *
	 * @param url
	 *            发送请求的URL
	 * @param rawParams
	 *            请求参数
	 * @return 服务器响应字符串
	 * @throws Exception
	 */
	public static String postRequest(String url, Map<String, String> rawParams) {
		String result = null;
		try {
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 60*1000);
			HttpConnectionParams.setSoTimeout(httpParams, 60*1000);
			HttpClient client = new DefaultHttpClient(httpParams);
			HttpPost httppost = new HttpPost(url);
			// 如果传递参数个数比较多的话可以对传递的参数进行封装
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			for (String key : rawParams.keySet()) {
				// 封装请求参数
				params.add(new BasicNameValuePair(key, rawParams.get(key)));
			}
			httppost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

			//
			// 第一次一般是还未被赋值，若有值则将SessionId发给服务器
			if (null != PHPSESSID) {
				httppost.setHeader("Cookie", "PHPSESSID=" + PHPSESSID);
			}
			//
			HttpResponse response = client.execute(httppost);
			HttpEntity entity = response.getEntity();

			//
			CookieStore mCookieStore = ((AbstractHttpClient) client).getCookieStore();
			List<Cookie> cookies = mCookieStore.getCookies();
			for (int i = 0; i < cookies.size(); i++) {
				// 这里是读取Cookie['PHPSESSID']的值存在静态变量中，保证每次都是同一个值
				if ("PHPSESSID".equals(cookies.get(i).getName())) {
					PHPSESSID = cookies.get(i).getValue();
					break;
				}

			}
			//

			// If the response does not enclose an entity, there is no need
			if (entity != null) {
				InputStream instream = entity.getContent();
				result = convertStreamToString(instream);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (ConnectTimeoutException e) {

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
