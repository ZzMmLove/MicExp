package cn.gdgst.palmtest.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 *  * 检测网络是否可用的工具类
 * Context 上下文
 * @author Don
 *返回true网络正常，返回false网络出错
 */
public class NetworkCheck {
	private Context context;
	public NetworkCheck(Context context){
		this.context=context;

	}
	public boolean Network(){
		ConnectivityManager connectivity=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity!=null) {
			NetworkInfo info=connectivity.getActiveNetworkInfo();
			if (null!=info &&info.isConnected()) {

				if (info.getState()==NetworkInfo.State.CONNECTED) {
					return true;
				}


//				for (int i = 0; i < info.length; i++) {
//					if (info[i].getState()==NetworkInfo.State.CONNECTED) {
//						return true;
//					}
//				}
			}
		}
		return false;

	}

	/**
	 * 判断是否是wifi连接
	 */
	public static boolean isWifi(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkINfo = cm.getActiveNetworkInfo();
		if (networkINfo != null
				&& networkINfo.getType() == ConnectivityManager.TYPE_WIFI) {
			return true;
		}
		return false;
	}

	/**
	 * 打开网络设置界面
	 */
	public static void openSetting(Activity activity)
	{
		Intent intent = new Intent("/");
		ComponentName cm = new ComponentName("com.android.settings",
				"com.android.settings.WirelessSettings");
		intent.setComponent(cm);
		intent.setAction("android.intent.action.VIEW");
		activity.startActivityForResult(intent, 0);
	}

}
