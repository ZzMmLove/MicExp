package cn.gdgst.palmtest.utils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;

/**
 * @author Don
 *
 */
public class NetworkCheckDialog {
	public static void dialog(final Context context){
		AlertDialog.Builder builder=new Builder(context);
		builder.setTitle("网络提醒");
		builder.setMessage("当前网络不可用是否跳转到网络设置页面");
		builder.setPositiveButton("确定", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent intent =null;
				//Android3.0以上使用此方法
				if (android.os.Build.VERSION.SDK_INT>10) {
					intent=new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);

				}
				else {
					intent=new Intent();
					intent.setClassName("com.android.settings", "com.android.settings.WirelessSettings");
				}
				context.startActivity(intent);
				dialog.dismiss();

			}
		});
		builder.setNegativeButton("取消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		builder.create().show();
	}




}
