package cn.gdgst.palmtest.utils;
import cn.gdgst.palmtest.base.BaseApplication;
import android.widget.Toast;

public class ToastUtil {

	public static void show(String info) {
		Toast.makeText(BaseApplication.getApplication(), info, Toast.LENGTH_LONG).show();
	}
	public static void show(int info) {
		Toast.makeText(BaseApplication.getApplication(), info, Toast.LENGTH_LONG).show();
	}

}
