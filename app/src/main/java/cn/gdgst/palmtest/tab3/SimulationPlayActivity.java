package cn.gdgst.palmtest.tab3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.File;

import cn.gdgst.palmtest.R;
import cn.gdgst.palmtest.recorder.ScreenRecorder;
import cn.gdgst.palmtest.utils.FileUtils;

/**
 * 仿真实验的操作页面，嵌入了HTML JavaScript页面
 */
@SuppressLint("JavascriptInterface")
public class SimulationPlayActivity extends Activity {
	private static final int REQUEST_CODE = 1;
	/**承载HTML的视图组件*/
	private WebView mWebView;
	/**从仿真列表传过来的仿真实验在服务器的路径*/
	private String murl;
	/**仿真实验的标题名称*/
	private String name;
	private Handler mHandler = new Handler();


	//此部分代码用于录屏功能 // FIXME: 2017/3/3 JenfeeMa
	private MediaProjectionManager mMediaProjectionManager;
	private MediaProjection mMediaProjction;
	private ScreenRecorder screenRecorder;



	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.webview);
		mWebView = (WebView) findViewById(R.id.webView1);
		WebSettings webSettings = mWebView.getSettings();
		// 横屏显示
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		// 要访问本地文件
		webSettings.setAllowFileAccess(true);
		webSettings.setSupportZoom(true);
		webSettings.setBuiltInZoomControls(true);

		// //将图片调整到适合webview的大小
		// webSettings.setUseWideViewPort(true);
		// 支持缩放
		webSettings.setBuiltInZoomControls(true);
		// 隐藏缩放工具
		webSettings.setDisplayZoomControls(false);

		// 要与javascript交互
		webSettings.setJavaScriptEnabled(true);

		// //设置加载进来的页面自适应手机屏幕（可缩放）
		 webSettings.setUseWideViewPort(true);
		// //设置载入页面自适应手机屏幕，居中显示
		 webSettings.setLoadWithOverviewMode(true);
		// WebView自适应屏幕大小
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

		// mWebView.addJavascriptInterface(new Object() {
		// public void clickOnAndroid() {
		// mHandler.post(new Runnable() {
		// public void run() {
		// mWebView.loadUrl("javascript:wave()");
		// }
		// });
		// }
		// }, "demo");
		Intent intent = getIntent();
		murl = intent.getStringExtra("url");
		name = intent.getStringExtra("name");
		mWebView.loadUrl(murl);


/*
		//此部分代码用于录屏功能
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) { // 竖屏
			// doSomrthing
		} else {
			// 横屏时dosomething
			mMediaProjectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
			Intent intentRecorder = mMediaProjectionManager.createScreenCaptureIntent();
			startActivityForResult(intentRecorder, REQUEST_CODE);
		}*/




		mWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return false;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
			}
		});
	}

	@Override
	protected void onResume() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		super.onResume();
	}

	//此部分代码用于录屏功能 // FIXME: 2017/3/3
	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("Simulation", "调试:requestCode码是:"+requestCode+"resultCode码是:"+resultCode);
		*//**
		 * 调用MediaProjectionManager中的getMediaProjection获得媒体投影
		 *//*
		MediaProjection mediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
		if (mediaProjection == null) {
			Log.e("@@", "media projection is null");
			return;
		}
		// video size
		final int width = 1280;
		final int height = 720;
		//视频存储的路径和文件名
		File file = new File(Environment.getExternalStorageDirectory(),		//文件的路径
				name +"-" + width + "x" + height + "-" + System.currentTimeMillis() + ".mp4");   //文件名

		Log.d("SimulationPlayActivity", "filePath"+file.getAbsolutePath());
		final int bitrate = 6000000;
		screenRecorder = new ScreenRecorder(width, height, bitrate, 1, mediaProjection, file.getAbsolutePath());
		screenRecorder.start();
		//createFloatView();
		Toast.makeText(this, "Screen recorder is running...", Toast.LENGTH_SHORT).show();
		//moveTaskToBack(true);
		//把视频文件转换成流的形式，上传到服务器
		ByteArrayInputStream bis = FileUtils.getByteArrayInputStream(file);
	}


	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (screenRecorder != null) {
			Toast.makeText(this, "保存视频...", Toast.LENGTH_SHORT).show();
			screenRecorder.quit();
			screenRecorder = null;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (screenRecorder != null) {
			screenRecorder.quit();
			screenRecorder = null;
		}
	}*/

}