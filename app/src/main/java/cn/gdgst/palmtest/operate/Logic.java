package cn.gdgst.palmtest.operate;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * 后台进行交互类
 *
 * @author admin
 *
 */
public class Logic {
	@SuppressWarnings("rawtypes")
	public static String operator(Context cx, RequestHeader header, String url,
								  HashMap<String, String> maps) {
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		// params.put("key", "values");// 设置请求的参数名和参数值
		// 设置请求的参数名和参数值
		if (header != null) {
			params.put("a_curDateTime", header.getA_curDateTime());
			params.put("a_cid", header.getA_cid());
			params.put("a_singInfo", header.getA_singInfo());
			params.put("a_imei", header.getA_imei());
			params.put("a_imsi", header.getA_imsi());
			params.put("a_phone", header.getA_phone());
			params.put("a_lng", header.getA_lng());
			params.put("a_lat", header.getA_lat());
		}
		// 设置请求的参数名和参数值
		if (maps != null && maps.size() > 0) {
			Iterator iterator = maps.keySet().iterator();
			while (iterator.hasNext()) {
				Entry  obj = (Entry) iterator.next();
				params.put((String)obj.getKey(),(String)obj.getValue());
			}
		}
		String data = null;
		try {
			client.post(cx, url, params, new AsyncHttpResponseHandler() {
				public void onSuccess(int status, final String object) {
					super.onSuccess(status, object);
					if (status == HttpStatus.SC_OK) {// 成功处理的方法
						System.out.println(object);

					}
				}

				public void onFailure(Throwable arg0, String arg1) {// 失败处理的方法
					super.onFailure(arg0, arg1);
				}

				public void onFinish() { // 完成后调用，失败，成功，都要掉
				};

			});
		} catch (Exception e) {
		}
		return data;
	}
	public static void main(String[]args)
	{

	}
}
