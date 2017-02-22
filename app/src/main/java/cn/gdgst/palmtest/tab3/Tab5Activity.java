package cn.gdgst.palmtest.tab3;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import cn.gdgst.palmtest.R;

@SuppressLint("JavascriptInterface")
public class Tab5Activity extends Activity {
	private WebView mWebView;
	private String murl;
	private Handler mHandler = new Handler();

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.webview);
		mWebView = (WebView) findViewById(R.id.webView1);
		WebSettings webSettings = mWebView.getSettings();
		// 横屏显示
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		// 要访问本地文件
		webSettings.setAllowFileAccess(true);

		// //将图片调整到适合webview的大小
		// webSettings.setUseWideViewPort(true);
		// 支持缩放
		webSettings.setBuiltInZoomControls(true);
		// 隐藏缩放工具
		webSettings.setDisplayZoomControls(false);
		// 要与javascript交互
		webSettings.setJavaScriptEnabled(true);

		// //设置加载进来的页面自适应手机屏幕（可缩放）
		// webSettings.setUseWideViewPort(true);
		// //设置载入页面自适应手机屏幕，居中显示
		// webSettings.setLoadWithOverviewMode(true);
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
		mWebView.loadUrl(murl);

		mWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return false;

			}
		});

	}

	@Override
	protected void onResume() {

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		super.onResume();
	}

}