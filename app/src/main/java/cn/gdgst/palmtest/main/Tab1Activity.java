package cn.gdgst.palmtest.main;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.lidroid.xutils.view.annotation.ViewInject;
import com.tbruyelle.rxpermissions.RxPermissions;

import cn.gdgst.palmtest.API.APIWrapper;
import cn.gdgst.palmtest.BuildConfig;
import cn.gdgst.palmtest.Entitys.VoteAction;
import cn.gdgst.palmtest.R;
import cn.gdgst.palmtest.Entitys.UpdateInfo;
import cn.gdgst.palmtest.base.NetworkBaseActivity;
import cn.gdgst.palmtest.bean.HttpResult;
import cn.gdgst.palmtest.service.UpdateAppService;
import cn.gdgst.palmtest.tab1.Grid_Item;
import cn.gdgst.palmtest.tab1.MyGridAdapter;
import cn.gdgst.palmtest.tab1.SlideImageLayout;
import cn.gdgst.palmtest.tab1.chuangke.ChuangKeList;
import cn.gdgst.palmtest.tab1.huizhan.HuiZhanListActivity;
import cn.gdgst.palmtest.tab1.kaoshi.KaoShiList;
import cn.gdgst.palmtest.tab1.mingshi.MingShiList;
import cn.gdgst.palmtest.tab1.examsystem.ExamListActivity;
import cn.gdgst.palmtest.tab1.vote.TestActivity;
import cn.gdgst.palmtest.tab1.vote.VoteActivity;
import cn.gdgst.palmtest.tab1.wenku.WenKuList;
import cn.gdgst.palmtest.tab1.zhuangbei.ZhuangBeiList;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 这是底部4个导航栏中的第一个导航栏页面
 */
public class Tab1Activity extends NetworkBaseActivity implements View.OnClickListener {
	//@ViewInject(R.id.main_grid)
	//GridView gridView;
	private MyGridAdapter mAdapter = null;
	// 滑动图片的集合
	private ArrayList<View> imagePageViews = null;
	@ViewInject(R.id.image_slide_page)
	private ViewPager viewPager = null;
	// 当前ViewPager索引
	private int pageIndex = 0;
	// 包含圆点图片的View
	private ViewGroup imageCircleView = null;
	private ImageView[] imageCircleViews = null;
	List<Grid_Item> lists;
	private SlideImageLayout slideLayout = null;
	private Button buttonSyncVideo,buttonSimulation,buttonInnovate,buttonExamineSystem,buttonTestLibrary,buttonLecture,buttonTrainCollege,buttonTestEquepment,buttonExhibitionCenter;
	//"名师讲堂", "考试备考", "实验文库", "实验资讯", "创客空间", "会展中心",  "培训学院", "实验装备"
	private String titleArr[] = { "同步视频", "实验文库", "创新实验", "测评系统", "活动赛事", "名师讲堂",  "考评系统", "实验装备", "新闻动态"};

	private boolean adAuto = true;
	// 广告正在滑动
	boolean adScrolling = false;
	// 更新版本要用到的一些信息
	private UpdateInfo info;
	private ProgressDialog pBar;
	/**
	 * 更新版本
	 */
	private Intent updateServiceIntent;
	private static int REQUESTPERMISSION = 110 ;

	// 广告移动处理
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				//更新
				if (isNeedUpdate()) {
					showUpdateDialog();
				}
				break;
			case 1:
				updateAddSelecedStatus();
				viewPager.setCurrentItem(pageIndex);
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().hide();
		getUpdateInfoByRetrofit();

/*		new Thread() {
			public void run() {
				try {
					Thread.sleep(500);
					UpdateInfoService updateInfoService = new UpdateInfoService(Tab1Activity.this);
					SharedPreferences sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
					String accessToken = sp.getString("accessToken", "");
					String version_code= BuildConfig.VERSION_NAME;
					Map<String, String> rawParams = new HashMap<String, String>();
					rawParams.put("accessToken", accessToken);
					rawParams.put("version_code", version_code);
					info = updateInfoService.getUpDateInfo(rawParams);
					handler.sendEmptyMessage(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();*/
	}


	private void getUpdateInfoByRetrofit() {
		String version_code= BuildConfig.VERSION_NAME;
		APIWrapper.getInstance().updateInfoRemark(version_code)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<HttpResult<UpdateInfo>>() {
					@Override
					public void onCompleted() {
						handler.sendEmptyMessage(0);
					}

					@Override
					public void onError(Throwable e) {

					}

					@Override
					public void onNext(HttpResult<UpdateInfo> updateInfoHttpResult) {
						boolean success = updateInfoHttpResult.getSuccess();
						if (success){
							info = updateInfoHttpResult.getData();
							Log.d("Application1","通过Retrofit访问"+info.toString());
						}
					}
				});
	}

	@Override
	protected void setLayout() {
		super.setLayout();
		setContentView(R.layout.activity_tab1);
	}

	@Override
	protected void init() {
		super.init();
		adAuto = true;
		adThread.start();
		initViewPagerView();
		//initGridView();
		buttonSyncVideo = (Button) findViewById(R.id.tab1_button_sync_video);
		buttonSimulation = (Button) findViewById(R.id.tab1_button_simulation_test);
		buttonInnovate = (Button) findViewById(R.id.tab1_button_innovate);
		buttonExamineSystem = (Button) findViewById(R.id.tab1_button_examine_system);
		buttonTestLibrary = (Button) findViewById(R.id.tab1_button_test_library);
		buttonLecture = (Button) findViewById(R.id.tab1_button_lecture);
		buttonTrainCollege = (Button) findViewById(R.id.tab1_button_train_college);
		buttonTestEquepment = (Button) findViewById(R.id.tab1_button_test_equepment);
		buttonExhibitionCenter = (Button) findViewById(R.id.tab1_button_exhibition_center);

		buttonSyncVideo.setOnClickListener(this);
		buttonSimulation.setOnClickListener(this);
		buttonInnovate.setOnClickListener(this);
		buttonExamineSystem.setOnClickListener(this);
		buttonTestLibrary.setOnClickListener(this);
		buttonLecture.setOnClickListener(this);
		buttonTrainCollege.setOnClickListener(this);
		buttonTestEquepment.setOnClickListener(this);
		buttonExhibitionCenter.setOnClickListener(this);
	}
	// 线程来自动播放图片
	Thread adThread = new Thread() {
		public void run() {
			while (adAuto) {
				try {
					sleep(4000);
					if (adScrolling == false) {
						pageIndex++;
						if (pageIndex > imagePageViews.size() - 1)
							pageIndex = 0;
						handler.sendEmptyMessage(1);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
	};

	/**
	 * 加载头部广告
	 */
	private void initViewPagerView() {
		// 滑动图片区域
		imagePageViews = new ArrayList<View>();
		viewPager = (ViewPager) this.findViewById(R.id.image_slide_page);
		// 圆点图片区域
		int length = 4;
		imageCircleViews = new ImageView[length];
		imageCircleView = (ViewGroup) this.findViewById(R.id.layout_circle_images);
		slideLayout = new SlideImageLayout(this);
		slideLayout.setCircleImageLayout(length);
		for (int i = 0; i < length; i++) {
			int defId = R.mipmap.ad01;
			switch (i) {
			case 1:
				defId = R.mipmap.ad02;
				break;
			case 2:
				defId = R.mipmap.ad03;
				break;
			case 3:
				defId = R.mipmap.ad04;
				break;
			}
			View ImageView = slideLayout.getSlideImageLayout(null, i, defId);
			imagePageViews.add(ImageView);
			imageCircleViews[i] = slideLayout.getCircleImageLayout(i);
			imageCircleView.addView(slideLayout.getLinearLayout(imageCircleViews[i], 10, 10));
		}
		viewPager.setAdapter(new SlideImageAdapter());
		viewPager.setOnPageChangeListener(new ImagePageChangeListener());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.tab1_button_sync_video://同步视频
				Intent i1 = new Intent();
				i1.setClass(Tab1Activity.this, Tab2Activity.class);
				startActivity(i1);
				break;
			case R.id.tab1_button_simulation_test:
				Intent i3 = new Intent();
				i3.setClass(Tab1Activity.this, WenKuList.class);
				startActivity(i3);
				break;
			case R.id.tab1_button_innovate://考评系统
				Intent PeiSunList_intent = new Intent();
				PeiSunList_intent.setClass(Tab1Activity.this, ExamListActivity.class);
				startActivity(PeiSunList_intent);
				break;
			case R.id.tab1_button_examine_system://测评系统
				Intent KaoShiList_intent = new Intent();
				KaoShiList_intent.setClass(Tab1Activity.this, KaoShiList.class);
				startActivity(KaoShiList_intent);
				break;
			case R.id.tab1_button_test_library://实验文库
				Intent WenKuList_intent = new Intent();
				WenKuList_intent.setClass(Tab1Activity.this, ZhuangBeiList.class);
				startActivity(WenKuList_intent);
				break;
			case R.id.tab1_button_lecture://名师讲堂
				Intent MingShiList_intent = new Intent();
				MingShiList_intent.setClass(Tab1Activity.this, MingShiList.class);
				MingShiList_intent.putExtra("title", "会展中心");
				startActivity(MingShiList_intent);
				break;
			/**
			 * 创新实验
			 */
			case R.id.tab1_button_train_college:
				Intent ChuangKeList_intent = new Intent();
				ChuangKeList_intent.setClass(Tab1Activity.this, ChuangKeList.class);
				startActivity(ChuangKeList_intent);
				break;
			//实验装备
			case R.id.tab1_button_test_equepment:

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String result = null;
                        OkHttpClient okHttpClient = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url("http://shiyan360.cn/api/activity")
                                .build();
                        Response response = null;
                        try {
                            response = okHttpClient.newCall(request).execute();
                            result = response.body().string();
                        } catch (IOException e) {
                            e.printStackTrace();
                            result = "";
                        }
                        Intent ZhuangBeiList_intent = new Intent();
                        ZhuangBeiList_intent.putExtra("votedetail", result);
                        ZhuangBeiList_intent.setClass(Tab1Activity.this, VoteActivity.class);
                        //ZhuangBeiList_intent.setClass(Tab1Activity.this, TestActivity.class);
                        startActivity(ZhuangBeiList_intent);
                    }
                }).start();


				break;
			//会展中心
			case R.id.tab1_button_exhibition_center:
				Intent HuiZhanList_intent = new Intent();
				HuiZhanList_intent.setClass(Tab1Activity.this, HuiZhanListActivity.class);
				HuiZhanList_intent.putExtra("category_id", "396"); // 小学科学396
				HuiZhanList_intent.putExtra("title", "新闻动态");
				startActivity(HuiZhanList_intent);
				break;
		}
	}

	// 滑动页面更改事件监听器
	private class ImagePageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int index) {
			pageIndex = index;
			updateAddSelecedStatus();
		}
	}

	/**
	 * 更新广告选项中状态
	 */
	private void updateAddSelecedStatus() {
		slideLayout.setPageIndex(pageIndex);
		for (int i = 0; i < imageCircleViews.length; i++) {
			if (i == pageIndex) {
				imageCircleViews[pageIndex].setBackgroundResource(R.mipmap.page_indicator_focused);
			} else {
				imageCircleViews[i].setBackgroundResource(R.mipmap.page_indicator_unfocused);
			}
		}
	}

	// 滑动图片数据适配器
	private class SlideImageAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			// adScrolling = true;
			return imagePageViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;

		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(imagePageViews.get(arg1));
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(imagePageViews.get(arg1));

			return imagePageViews.get(arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

		@Override
		public void finishUpdate(View arg0) {

		}
	}

	@Override
	protected void loadData() {
		super.loadData();
	}

	/**
	 * 展示更新版本的对话框
	 * 更新版本
	 */
	private void showUpdateDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		builder.setTitle("请升级APP至版本" + info.getVersion());
		builder.setCancelable(false);
		builder.setTitle("检测到有新的版本");
		builder.setMessage(info.getUpgrade_remark());
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
//					open();
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					//downFile(info.getUpgrade_url());
					upDate();
				} else {
					Toast.makeText(Tab1Activity.this, "SD卡不可用，请插入SD卡", Toast.LENGTH_SHORT).show();
				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.create().show();
	}

	private void upDate() {

		RxPermissions.getInstance(Tab1Activity.this).request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
				.subscribe(new Subscriber<Boolean>() {
					@Override
					public void onCompleted() {

					}

					@Override
					public void onError(Throwable e) {

					}

					@Override
					public void onNext(Boolean aBoolean) {
						if (aBoolean) {
							Intent service = new Intent(Tab1Activity.this, UpdateAppService.class);
							service.putExtra("url", info.getUpgrade_url());
							Toast.makeText(Tab1Activity.this, "正在下载中", Toast.LENGTH_SHORT).show();
							startService(service);
						} else {
							Toast.makeText(Tab1Activity.this, "SD卡下载权限被拒绝", Toast.LENGTH_SHORT).show();
						}
					}
				});

//		updateServiceIntent = new Intent(Tab1Activity.this, UpdateAppService.class);
//		updateServiceIntent.putExtra("url", info.getUpgrade_url());
//		if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
//			//申请权限
//			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUESTPERMISSION);
//			startService(updateServiceIntent);
//			Toast.makeText(this, "请允许下载权限", Toast.LENGTH_LONG).show();
//		}else {
//			startService(updateServiceIntent);
//		}
	}

	private boolean isNeedUpdate() {
		int v = info.getIs_upgrade(); // 最新版本的版本号
		if (v == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 获取当前版本的版本号
	 * @return
     */
	private String getVersion() {
		try {
			PackageManager packageManager = getPackageManager();
			PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "版本号未知";
		}
	}

	private void downFile(final String url) {
		pBar = new ProgressDialog(Tab1Activity.this);    //进度条，在下载的时候实时更新进度，提高用户友好度
		pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pBar.setTitle("正在下载");
		pBar.show();
		pBar.setProgress(0);
		new Thread() {
			public void run() {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(url);
				HttpResponse response;
				try {
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
					int length = (int) entity.getContentLength(); //获取文件大小
//	                pBar.setMax(length); //设置进度条的总长度
					InputStream is = entity.getContent();
					FileOutputStream fileOutputStream = null;
					if (is != null) {
						File file = new File(Environment.getExternalStorageDirectory(), "MicExp.apk");
						fileOutputStream = new FileOutputStream(file);
						//这个是缓冲区，即一次读取10个比特，我弄的小了点，因为在本地，所以数值太大一下就下载完了,
						//看不出progressbar的效果。
						byte[] buf = new byte[1024 * 100];
						int ch = -1;
						int process = 0;
						while ((ch = is.read(buf)) != -1) {
							fileOutputStream.write(buf, 0, ch);
							process += ch;
							pBar.setProgress(process / (1024 * 100)); //这里就是关键的实时更新进度了！
						}
					}
					fileOutputStream.flush();
					if (fileOutputStream != null) {
						fileOutputStream.close();
					}
					down();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	private void down() {
		handler.post(new Runnable() {
			public void run() {
				pBar.cancel();
				//update();
			}
		});
	}

//	private void update() {
//		Intent intent = new Intent();
//		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent.setAction(Intent.ACTION_VIEW);
//		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//		intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "MicExp.apk")), "application/vnd.android.package-archive");
//		startActivity(intent);
//	}


	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if(requestCode == REQUESTPERMISSION){
			if(permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
				if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
					if(updateServiceIntent!=null)
						startService(updateServiceIntent);
				}else{
					//提示没有权限，安装不了咯
					Toast.makeText(this, "没有权限，无法安装", Toast.LENGTH_LONG).show();
				}
			}
		}
	}
}
