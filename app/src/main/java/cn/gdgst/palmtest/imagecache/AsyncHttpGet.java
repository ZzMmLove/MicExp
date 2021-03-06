package cn.gdgst.palmtest.imagecache;

import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * 功能描述：通过HTTP协议发送GET请求
 * @author android_ls
 *
 */
public class AsyncHttpGet extends AsyncBaseRequest {

    /**
     *
     */
    private static final long serialVersionUID = 2L;

    public AsyncHttpGet(String url, Map<String, String> parameter, ParseCallback handler, ResultCallback requestCallback) throws IOException {
        super(url, parameter, handler, requestCallback);
    }

    @Override
    protected InputStream getRequestResult() throws IOException {
        StringBuilder sb = new StringBuilder(requestUrl);
        if (parameter != null && !parameter.isEmpty()) {
            sb.append('?');
            for (Map.Entry<String, String> entry : parameter.entrySet()) {
                sb.append(entry.getKey()).append('=').append(URLEncoder.encode(entry.getValue(), HTTP.UTF_8)).append('&');
            }
            sb.deleteCharAt(sb.length() - 1);
        }

        URL url = new URL(sb.toString());
        mHttpURLConn = (HttpURLConnection) url.openConnection();
        mHttpURLConn.setConnectTimeout(connectTimeout);
        mHttpURLConn.setRequestMethod("GET");
        if (mHttpURLConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return mHttpURLConn.getInputStream();
        }
        return null;
    }

}
