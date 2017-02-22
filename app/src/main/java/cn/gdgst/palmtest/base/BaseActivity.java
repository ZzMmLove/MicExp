package cn.gdgst.palmtest.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Window;
import com.lidroid.xutils.ViewUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 功能描述：应用中界面（Activity）的基类
 * 对原有的Activity类进行扩展
 * @author admin
 */
public abstract class BaseActivity extends AppCompatActivity {

	protected BaseApplication myApplication;
	/**
	 * 屏幕的宽度和高度
	 */
	protected int mScreenWidth;
	protected int mScreenHeight;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		mScreenWidth = metric.widthPixels;
		mScreenHeight = metric.heightPixels;
		myApplication=(BaseApplication) this.getApplication();
		myApplication.addActivity(this);
		setLayout();
		getWindow().setBackgroundDrawable(null);
		ViewUtils.inject(this);
		init();
		loadData();
	}

	protected void loadData() {
		// TODO Auto-generated method stub

	}

	protected void init() {
		// TODO Auto-generated method stub

	}

	protected void setLayout() {
		// TODO Auto-generated method stub

	}

	/**
	 * 作用于友盟统计
	 */
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	/**
	 * 作用于友盟统计
	 */
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
