package cn.gdgst.palmtest.main;

import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;

import cn.gdgst.palmtest.API.APIWrapper;
import cn.gdgst.palmtest.Entitys.UserEntity;
import cn.gdgst.palmtest.R;
import cn.gdgst.palmtest.base.AppConstant;
import cn.gdgst.palmtest.base.BaseApplication;
import cn.gdgst.palmtest.base.NetworkBaseActivity;
import cn.gdgst.palmtest.bean.HttpResult;
import cn.gdgst.palmtest.search.SearchActivity;
import cn.gdgst.palmtest.utils.ToastUtil;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 主界面
 * @author admin
 *
 */
@SuppressWarnings("deprecation")
public class TabMainActivity extends NetworkBaseActivity {

	/** 本地窗口管理对象 **/
	LocalActivityManager manager = null;
	/** ViewPager对象实现页面滑动效果 **/
	ViewPager viewPager = null;
	/** 分别对应首页，发布买车，搜索，我的车源 **/
	RelativeLayout firstPageLoad, sallCarLoad, searchLoad, myCarLoad;
	/** 底部四个文字控件 **/
	public TextView firstText, sellText, searchText, myCarText;
	/** 底部图片空间 **/
	public ImageView home, sellCar, search, myCar;
	/** 动画图片偏移量 **/
	private int offset = 0;
	/** 当前页卡编号 **/
	private int currIndex = 0;
	/** 动画图片宽度 **/
	private int bmpW;
	/** 动画图片游标 **/
	private ImageView cursor;
	/** 字体颜色值 */
	int colorh = Color.rgb(70, 150, 211);
	int colord = Color.rgb(141, 149, 164);
	private Context context;
	private MyOnPageChangeListener myOnPageChangeListener;

	/**
	 * 退出时间
	 */
	private long mExitTime = 0;
	/**
	 * 退出间隔
	 */
	private static final int INTERVAL = 2000;
	// 标题
	//TextView title;
	//ImageView openRight;

	private BaseApplication baseApplication;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.activity_tabmain);
		autoLogin();
		getOverflowMenu();
		baseApplication = BaseApplication.getApplication();
		baseApplication.addActivity(this);
		manager = new LocalActivityManager(this, true);
		manager.dispatchCreate(savedInstanceState);
		initView();
		InitImageView();
		initPagerViewer();
	}

	private void getOverflowMenu() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			if(menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.main_menu_search:
				Intent intent_SearchActivity = new Intent();
				intent_SearchActivity.setClass(TabMainActivity.this, SearchActivity.class);
				startActivity(intent_SearchActivity);
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		//openRight = (ImageView) findViewById(R.id.openRight);
		//title = (TextView) findViewById(R.id.title);
		viewPager = (ViewPager) findViewById(R.id.main_viewpage);
		firstPageLoad = (RelativeLayout) findViewById(R.id.main_load_first);
		sallCarLoad = (RelativeLayout) findViewById(R.id.main_load_sell_car);
		searchLoad = (RelativeLayout) findViewById(R.id.main_load_search);//第三个fragment布局的按钮-showmark
		myCarLoad = (RelativeLayout) findViewById(R.id.main_load_collect);


		home = (ImageView) findViewById(R.id.main_first_image);
		sellCar = (ImageView) findViewById(R.id.main_sell_car_image);
		search = (ImageView) findViewById(R.id.main_search_image);//第三个fragment按钮的图片
		myCar = (ImageView) findViewById(R.id.main_collect_image);


		firstText = (TextView) findViewById(R.id.first_page_text);
		sellText = (TextView) findViewById(R.id.publish_car_page_text);
		searchText = (TextView) findViewById(R.id.search_page_text);//第三个fragment按钮的文本
		myCarText = (TextView) findViewById(R.id.my_car_page_text);

		// 设置监听器
		/*openRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent i = new Intent();
				i.setClass(TabMainActivity.this, SearchActivity.class);
				startActivity(i);
			}
		});*/
		firstPageLoad.setOnClickListener(new MyOnClickListener(0));
		sallCarLoad.setOnClickListener(new MyOnClickListener(1));
		searchLoad.setOnClickListener(new MyOnClickListener(2));
		myCarLoad.setOnClickListener(new MyOnClickListener(3));
		//myCarLoad.setOnClickListener(new MyOnClickListener(2));


	}

	/**
	 * 初始化PageViewer
	 */
	private void initPagerViewer() {
		viewPager = (ViewPager) findViewById(R.id.main_viewpage);
		final ArrayList<View> list = new ArrayList<View>();
		Intent intent = new Intent(context, Tab1Activity.class);//Tab1Activity是实验首页界面
		list.add(getView("A", intent));
		Intent intent2 = new Intent(context, Tab2Activity.class);//ShipinActivity是同步视频界面
		list.add(getView("B", intent2));
		Intent intent3 = new Intent(context, Tab3Activity.class);//ExperimentList是仿真实验界面
		list.add(getView("C", intent3));
		Intent intent4 = new Intent(context, Tab4Activity.class);//Tab4Activity更多设置界面
		list.add(getView("D", intent4));
		PagerViewAdapter adapter = new PagerViewAdapter(list);
		viewPager.setAdapter(adapter);
		viewPager.setCurrentItem(0);
		myOnPageChangeListener = new MyOnPageChangeListener();
		viewPager.setOnPageChangeListener(myOnPageChangeListener);
	}

	/**
	 * 初始化动画
	 */
	private void InitImageView() {
		cursor = (ImageView) findViewById(R.id.main_bottom_select);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 获取分辨率宽度
		bmpW = screenW / 4;
//		bmpW = screenW / 3;
		offset = (screenW / 4 - bmpW) / 2;// 计算偏移量
//		offset = (screenW / 3 - bmpW) / 2;// 计算偏移量
		offset += 0;
		int height = getResources().getDimensionPixelSize(
				R.dimen.main_bottom_tab_selected_height);
		cursor.setLayoutParams(new RelativeLayout.LayoutParams(bmpW, height));
		Matrix matrix = new Matrix();
		matrix.postTranslate(offset + 100, 0);
		cursor.setImageMatrix(matrix);// 设置动画初始位置
	}

	/**
	 * 通过activity获取视图
	 *
	 * @param id
	 * @param intent
	 * @return
	 */
	private View getView(String id, Intent intent) {
		return manager.startActivity(id, intent).getDecorView();
	}

	/**
	 * 页卡切换监听
	 */
	private class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset * 2 + bmpW;// 页卡1 -> 页卡2 偏移量
		int two = one * 2;// 页卡1 -> 页卡3 偏移量
		int three = one * 3; // 卡页偏移3个位

		@Override
		public void onPageSelected(int arg0) {

			Animation animation = null;// 动画
			setBottomPic(arg0);
			switch (arg0) {
				case 0:
					if (currIndex == 1) {
						animation = new TranslateAnimation(one, 0, 0, 0);
					} else if (currIndex == 2) {
						animation = new TranslateAnimation(two, 0, 0, 0);
					}
				else if (currIndex == 3) {
					animation = new TranslateAnimation(three, 0, 0, 0);
				}
					break;
				case 1:

					if (currIndex == 0) {
						animation = new TranslateAnimation(offset, one, 0, 0);
					} else if (currIndex == 2) {
						animation = new TranslateAnimation(two, one, 0, 0);
					}
				else if (currIndex == 3) {
					animation = new TranslateAnimation(three, one, 0, 0);
				}
					break;
				case 2:
					if (currIndex == 0) {
						animation = new TranslateAnimation(offset, two, 0, 0);
					} else if (currIndex == 1) {
						animation = new TranslateAnimation(one, two, 0, 0);
					}
				else if (currIndex == 3) {
					animation = new TranslateAnimation(three, two, 0, 0);
				}
					break;
			case 3: {
				if (currIndex == 0) {
					animation = new TranslateAnimation(offset, three, 0, 0);
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, three, 0, 0);
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, three, 0, 0);
				}
			}
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			cursor.startAnimation(animation);
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}

	public void setBottomPic(int i) {
		switch (i) {
			case 0: {
				home.setBackgroundResource(R.mipmap.icon_home_sel);
				firstText.setTextColor(colorh);
				sellCar.setBackgroundResource(R.mipmap.icon_video);
				sellText.setTextColor(colord);
			    search.setBackgroundResource(R.mipmap.icon_experiment);//showmark
			    searchText.setTextColor(colord);
				myCar.setBackgroundResource(R.mipmap.icon_setting);
				myCarText.setTextColor(colord);
				//openRight.setVisibility(View.INVISIBLE);
				//title.setText("实验首页");
			}
			break;
			case 1: {
				home.setBackgroundResource(R.mipmap.icon_home);
				firstText.setTextColor(colord);
				sellText.setTextColor(colorh);
     			searchText.setTextColor(colord);
				myCarText.setTextColor(colord);
				sellCar.setBackgroundResource(R.mipmap.icon_video_sel);
			    search.setBackgroundResource(R.mipmap.icon_experiment);//showmark
				myCar.setBackgroundResource(R.mipmap.icon_setting);
				//title.setText("同步视频");
				//openRight.setVisibility(View.VISIBLE);
				//openRight.setImageResource(R.drawable.search);
				break;
			}
		case 2: {
			home.setBackgroundResource(R.mipmap.icon_home);
			sellCar.setBackgroundResource(R.mipmap.icon_video);
			search.setBackgroundResource(R.mipmap.icon_experiment_sel);
			myCar.setBackgroundResource(R.mipmap.icon_setting);
			firstText.setTextColor(colord);
			sellText.setTextColor(colord);
			searchText.setTextColor(colorh);
			myCarText.setTextColor(colord);
			//title.setText("更多设置");
			//openRight.setVisibility(View.INVISIBLE);
			break;
		}
			case 3: {
				home.setBackgroundResource(R.mipmap.icon_home);
				sellCar.setBackgroundResource(R.mipmap.icon_video);
			    search.setBackgroundResource(R.mipmap.icon_experiment);
				myCar.setBackgroundResource(R.mipmap.icon_setting_sel);
				firstText.setTextColor(colord);
				sellText.setTextColor(colord);
			    searchText.setTextColor(colord);
				myCarText.setTextColor(colorh);
				//openRight.setVisibility(View.INVISIBLE);
				//title.setText("更多设置");
			}
			break;
		}
	}

	/**
	 * 图标点击监听
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		@Override
		public void onClick(View v) {
			viewPager.setCurrentItem(index);
		}
	};

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
				exit();
			}
			return true;
		}
		return super.dispatchKeyEvent(event);
	}

	/**
	 * 判断两次返回时间间隔,小于两秒则退出程序
	 */
	private void exit() {
		if (System.currentTimeMillis() - mExitTime > INTERVAL) {
			ToastUtil.show("再按一次,退出程序");
			mExitTime = System.currentTimeMillis();
		} else {
			if (myApplication.removerAll()) {
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(0);
			}
		}
	}

	@Override
	public void finish() {
		Log.v("showmark","TabAnimation已经finish..........");
		super.finish();
	}

	private void autoLogin() {
		SharedPreferences sharedPreferences = getSharedPreferences(AppConstant.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
		String username = sharedPreferences.getString("username", null);
		String password = sharedPreferences.getString("password", null);
		Observable<HttpResult<UserEntity>> observable = APIWrapper.getInstance().login(username, password);
		 observable.subscribeOn(Schedulers.io())
		 .observeOn(AndroidSchedulers.mainThread())
		 .subscribe(new Subscriber<HttpResult<UserEntity>>() {
				@Override
				public void onCompleted() {

				}

				@Override
				public void onError(Throwable e) {

				}

				@Override
				public void onNext(HttpResult<UserEntity> userEntityHttpResult) {
				boolean success = userEntityHttpResult.getSuccess();
				if (success) {
					UserEntity userEntity = userEntityHttpResult.getData();
					Log.d("TabActivity", "检测用户信息更新"+userEntity.toString());
				}
				}
		});
	}
}
