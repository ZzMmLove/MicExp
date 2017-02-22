package cn.gdgst.palmtest.imagecache;

import cn.gdgst.palmtest.base.AppConstant;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.util.Map;

/**
 * 功能描述：网络请求线程基类
 *
 * @author android_ls
 *
 */
public abstract class AsyncBaseRequest implements Runnable, Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 网络连接超时，默认值为5秒
	 */
	protected int connectTimeout = 5 * 1000;
	/**
	 * 网络数据读取超时，默认值为5秒
	 */
	protected int readTimeout = 5 * 1000;

	private boolean interrupted;
	public boolean isInterrupted() {
		return interrupted;
	}

	public void setInterrupted(boolean interrupted) {
		this.interrupted = interrupted;
	}

	protected void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	protected void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	protected String requestUrl;

	protected Map<String, String> parameter;

	private ParseCallback parseHandler;

	private ResultCallback requestCallback;

	protected HttpURLConnection mHttpURLConn;

	protected InputStream mInStream;

	public AsyncBaseRequest(String url, Map<String, String> parameter,
							ParseCallback handler, ResultCallback requestCallback) {
		this.parseHandler = handler;
		this.requestUrl = url;
		this.parameter = parameter;
		this.requestCallback = requestCallback;
	}

	/**
	 * 发送网络请求
	 *
	 * @return 网络请求返回的InputStream数据流
	 * @throws IOException
	 */
	protected abstract InputStream getRequestResult() throws IOException;

	@Override
	public void run() {
		if (interrupted) {
			return;
		}

		try {
			mInStream = getRequestResult();
			if (mInStream != null) {
				if (interrupted) {
					return;
				}

				Object obj = null;
				if (parseHandler != null) {
					byte[] data = StreamTool.read(mInStream);
					if (interrupted) {
						return;
					}

					String result = new String(data);
					obj = parseHandler.parse(result);
				}

				if (interrupted) {
					return;
				}

				if (obj != null) {
					requestCallback.onSuccess(obj);
				} else {
					requestCallback.onSuccess(mInStream);
				}

			} else {
				requestCallback.onFail(AppConstant.NETWORK_REQUEST_RETUN_NULL); // 网络请求返回NULL
			}
		} catch (IOException e) {
			requestCallback.onFail(AppConstant.NETWORK_REQUEST_IOEXCEPTION_CODE); // IO异常标识
			e.printStackTrace();
		}

	}

	public HttpURLConnection getRequestConn() {
		return mHttpURLConn;
	}

}
