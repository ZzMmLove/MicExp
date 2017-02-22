package cn.gdgst.palmtest.imagecache;

/**
 * 功能描述：网络请求返回的结果回调接口
 * @author android_ls
 *
 */
public interface ResultCallback {
	public void onSuccess(Object obj);

	public void onFail(int errorCode);

}
