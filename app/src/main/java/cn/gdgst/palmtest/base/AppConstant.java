package cn.gdgst.palmtest.base;
/**
 * 存放一些全局的一些通用的变量值
 * @author admin
 *
 */
public class AppConstant {
	/**
	 * URL解释
	 * 服务器端：CXFservlet 在web.xml中拦截/CXFService的地址
	 * SERVICENAME 是applicationContext.xml中配置的地址
	 */
	/*public static final String URL="http://192.168.43.154:8080/ADP/CXFService/";
	public static final String NAMESPACE="http://webservice.tutu.com/";
	public static final String SERVICENAME="CXFService";
	public static final String EMAIL_KEY="email_key";
	public static final String APP_HAS_UPDATE_KEY="has_update";
	public static final String APP_UPDATE_URL="update_url";
	public static final String APP_UPDATE_REMARK="update_remark";
	public static final String SCORE="score";
	public static final String BEST_SCORE="best_score";
	public static final boolean DEBUG = false;
	public static final int FAMOUS_BOOK=100;
	public static final int BEST_HEAD=101;
	public static final int EXAM=102;
	public static final boolean DEV_MODE = false;*/

	//这些图片不存在，根据实际需求替换为自己的
/*	public static String[] imgsUrl = {
			"http://img.my.csdn.net/uploads/201407/26/1406383299_1976.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383291_6518.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383291_8239.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383290_9329.jpg",
			"http://img.my.csdn.net/uploads/201407/26/1406383290_1042.jpg"
	};*/

	public final static int NETWORK_REQUEST_RETUN_NULL=0;
	public final static int NETWORK_REQUEST_RESULT_PARSE_ERROR=1;
	public final static int NETWORK_REQUEST_IOEXCEPTION_CODE=2;

	//利用SharedPreferences储存用户信息的xml文件名 // FIXME: 2017/2/11
	public final static String SHARED_PREFERENCES_USER = "userInfo";
	public final static String SERVER_URL = "http://shiyan360.cn";
}
