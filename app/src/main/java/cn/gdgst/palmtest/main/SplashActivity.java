package cn.gdgst.palmtest.main;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

import cn.gdgst.palmtest.R;
import cn.gdgst.palmtest.base.AppConstant;
import cn.gdgst.palmtest.base.BaseActivity;
import cn.gdgst.palmtest.tab4.LoginActivity;

/**
 * 软件启动界面
 *
 * */
public class SplashActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		getSupportActionBar().hide();
		new Handler().postDelayed(runnable, 500);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
		}
		return true;
	}

	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			checkNativeUserInfo();
		}
	};

	private void checkNativeUserInfo() {
		SharedPreferences sharedPreferences = getSharedPreferences(AppConstant.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
		String username = sharedPreferences.getString("username", null);
		String password = sharedPreferences.getString("password", null);
		String accessToken = sharedPreferences.getString("accessToken", null);
		Intent intent = new Intent();
		//如果本地SharedPreferences文件中有储存用户的信息,就直接跳到主页
		if (username != null && password != null && accessToken != null) {
			intent.setClass(SplashActivity.this, TabMainActivity.class);
			startActivity(intent);
			finish();
		}else {//否则跳向登录界面
			intent.setClass(this, TabMainActivity.class);
			startActivity(intent);
			finish();
		}
	}
}
