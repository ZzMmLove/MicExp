package cn.gdgst.palmtest.imagecache;

import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * 功能描述：通过HTTP协议发送POST请求
 * @author android_ls
 *
 */
public class AsyncHttpPost extends AsyncBaseRequest {

	/**
	 *
	 */
	private static final long serialVersionUID = 3L;

	public AsyncHttpPost(String url, Map<String, String> parameter,
						 ParseCallback handler, ResultCallback requestCallback) {
		super(url, parameter, handler, requestCallback);
	}

	@Override
	protected InputStream getRequestResult() throws IOException {
		StringBuilder sb = new StringBuilder();
		if(parameter!=null && !parameter.isEmpty()){
			for(Map.Entry<String, String> entry : parameter.entrySet()){
				sb.append(entry.getKey()).append('=')
						.append(URLEncoder.encode(entry.getValue(), HTTP.UTF_8)).append('&');
			}
			sb.deleteCharAt(sb.length()-1);
		}

		// 得到实体的二进制数据
		byte[] entitydata = sb.toString().getBytes();
		URL url = new URL(requestUrl);
		mHttpURLConn = (HttpURLConnection)url.openConnection();
		mHttpURLConn.setRequestMethod("POST");
		mHttpURLConn.setConnectTimeout(5 * 1000);
		// 如果通过post提交数据，必须设置允许对外输出数据
		mHttpURLConn.setDoOutput(true);
		mHttpURLConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		mHttpURLConn.setRequestProperty("Content-Length", String.valueOf(entitydata.length));
		OutputStream outStream = mHttpURLConn.getOutputStream();
		outStream.write(entitydata);
		outStream.flush();
		outStream.close();
		if(mHttpURLConn.getResponseCode()== HttpURLConnection.HTTP_OK){
			return mHttpURLConn.getInputStream();
		}
		return null;
	}

}
