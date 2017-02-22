package cn.gdgst.palmtest.base;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.frontia.FrontiaApplication;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.lidroid.xutils.util.LogUtils;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import cn.gdgst.dao.DaoMaster;
import cn.gdgst.dao.DaoSession;
import cn.gdgst.palmtest.utils.NetworkDialogUtils;
import cn.gdgst.palmtest.utils.Utils;

/**
 * 主要存放全局变量
 *
 * @author wangsheng
 *
 */
public class BaseApplication extends FrontiaApplication {
	private ArrayList<Activity> activities;
	public static BaseApplication myApplication;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = null;
	private LocationMode tempMode = LocationMode.Hight_Accuracy;

	private static DaoMaster.OpenHelper helper;
	private static DaoMaster daoMaster;
	private static DaoSession daoSession;

	@Override
	public void onCreate() {
		super.onCreate();
		if (isFirstTimeUse()) {
			boolean isSuccess = copyDatabase();
			if (isSuccess) {
				Toast.makeText(this,"初始化数据库成功", Toast.LENGTH_SHORT).show();
			}else {
				Toast.makeText(this,"初始化数据库失败", Toast.LENGTH_SHORT).show();
			}
		}

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable()) {
			File file_PalmTest = new File(Environment.getExternalStorageDirectory()+"/PalmTest");
			if (!file_PalmTest.exists()) {
				file_PalmTest.mkdir();
			}
		}

		activities = new ArrayList<Activity>();
		myApplication = this;
		initPush();
		getInfo();
		initBaiDuMap();
		initImageLoader(getApplicationContext());
        Logger.init("Dontag");
	}
	public static void initImageLoader(Context context) {
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheInMemory()
				.cacheOnDisc()
				.build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
		        .defaultDisplayImageOptions(defaultOptions)
				.threadPriority(Thread.NORM_PRIORITY - 2)//加载图片的线程数
				.denyCacheImageMultipleSizesInMemory()//解码图像的大尺寸将在内存中缓存先前解码图像的小尺寸。
				.discCacheFileNameGenerator(new Md5FileNameGenerator())//设置磁盘缓存文件名称
				.tasksProcessingOrder(QueueProcessingType.LIFO)//设置加载显示图片队列进程
				.writeDebugLogs()// Remove for release app
				.build();
		ImageLoader.getInstance().init(config);
	}

	private void initBaiDuMap() {
		// TODO Auto-generated method stub
		System.out.println("initBaiDuMap");
		mLocationClient = new LocationClient(myApplication);
		//声明LocationClient类
		myListener = new MyLocationListener();
		mLocationClient.registerLocationListener( myListener );
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);//设置定位模式
		option.setCoorType("gcj02");//返回的定位结果是百度经纬度，默认值gcj02
//		option.setScanSpan(50000);//设置发起定位请求的间隔时间为5000ms
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
		mLocationClient.start();
		System.out.println("overinitBaiDuMap");
	}

	public class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation){
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
				sb.append("\ndirection : ");
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				sb.append(location.getDirection());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				//运营商信息
				sb.append("\noperationers : ");
				sb.append(location.getOperators());
			}
//			Log.i("BaiduLocationApiDem", sb.toString());
		}
	}

	private void initPush() {
		// TODO Auto-generated method stub
		LogUtils.e("initPush");
		PushManager.startWork(getApplicationContext(),
				PushConstants.LOGIN_TYPE_API_KEY,
				Utils.getMetaValue(myApplication, "api_key"));
	}

	public static BaseApplication getApplication() {
		return myApplication;
	}

	/**
	 * 添加Activity到ArrayList<Activity>管理集合
	 *
	 * @param activity
	 */
	public void addActivity(Activity activity) {
		String className = activity.getClass().getName();
		for (Activity at : activities) {
			if (className.equals(at.getClass().getName())) {
				activities.remove(at);
				break;
			}
		}
		activities.add(activity);
	}

	// 退出时，把这些资源都释放掉
	public boolean removerAll() {
		if (activities != null && activities.size() > 0) {
			for (Activity activity : activities) {
				activity.finish();
			}
		}
		activities = null;
		//定位结束
		mLocationClient.stop();
		return true;
	}

	/**
	 * 获取IMEI号，IESI号，手机型号
	 */
	private void getInfo() {
		TelephonyManager mTm = (TelephonyManager) this
				.getSystemService(TELEPHONY_SERVICE);
		String imei = mTm.getDeviceId();
		String imsi = mTm.getSubscriberId();
		String mtype = android.os.Build.MODEL; // 手机型号
		String mtyb = android.os.Build.BRAND;// 手机品牌
		String numer = mTm.getLine1Number(); // 手机号码，有的可得，有的不可得
		LogUtils.e("手机IMEI号：" + imei + "手机IESI号：" + imsi + "手机型号：" + mtype
				+ "手机品牌：" + mtyb + "手机号码:" + numer);
	}

	/**
	 * 加密
	 *
	 * @param data
	 *            主要就是用户名Id与密码拼接在一起
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String md5(String data) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(data.getBytes());
		StringBuffer buf = new StringBuffer();
		byte[] bits = md.digest();
		for (int i = 0; i < bits.length; i++) {
			int a = bits[i];
			if (a < 0)
				a += 256;
			if (a < 16)
				buf.append("0");
			buf.append(Integer.toHexString(a));
		}
		return buf.toString();
	}

	/**
	 * 获取程序版本号
	 */
	private int getVersionCode(Context context) {
		int versionCode = 0;
		try {
			versionCode = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}


	/**
	 * 取得DaoMaster
	 *
	 * @param context        上下文
	 * @return               DaoMaster
	 */
	public static DaoMaster getDaoMaster(Context context) {
		if (daoMaster == null) {
			helper = new DaoMaster.DevOpenHelper(context,"myDb.db",null);
			daoMaster = new DaoMaster(helper.getWritableDatabase());
		}
		return daoMaster;
	}

	/**
	 * 取得DaoSession
	 *
	 * @param context        上下文
	 * @return               DaoSession
	 */
	public static DaoSession getDaoSession(Context context) {
		if (daoSession == null) {
			if (daoMaster == null) {
				daoMaster = getDaoMaster(context);
			}
			daoSession = daoMaster.newSession();
		}
		return daoSession;
	}


	/**
	 * 把数据库文件从assets文件夹中复制到/data/data/package/databases/中
	 */
	public boolean copyDatabase(){
		String mDatabasePath;
		final String DB_NAME = "myDb.db";
		String databasePath = this.getApplicationContext().getFilesDir().getAbsolutePath();

		databasePath = databasePath.substring(0, databasePath.lastIndexOf("/")) + "/databases";
		mDatabasePath = databasePath + "/" + DB_NAME;

		File dir = new File(databasePath);
		if(!dir.exists()){
			dir.mkdir();
		}

		File file = new File(mDatabasePath);
		if(!file.exists() || file.length() == 0){
			try {
				InputStream is = this.getAssets().open("database/"+DB_NAME);
				FileOutputStream fos = new FileOutputStream(mDatabasePath);
				byte[] buffer = new byte[8192];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		File isExistsFile = new File(mDatabasePath);
		return isExistsFile.exists();
	}

	/**
	 * 此方法用于判断用户是否是首次使用该APP
	 * @return
     */
	private boolean isFirstTimeUse() {
		SharedPreferences sharedPreferences = this.getSharedPreferences(AppConstant.SHARED_PREFERENCES_USER, MODE_PRIVATE);
		if (sharedPreferences.getString("FirstTimeUseId", null) == null) {
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString("FirstTimeUseId", "isFirstTimeUseId");
			editor.commit();
			return true;
		} else {
			return false;
		}
	}
}
