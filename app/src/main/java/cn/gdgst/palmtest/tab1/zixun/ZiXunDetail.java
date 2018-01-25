package cn.gdgst.palmtest.tab1.zixun;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;


import cn.gdgst.palmtest.API.APIWrapper;
import cn.gdgst.palmtest.R;
import cn.gdgst.palmtest.bean.HttpResult;

import cn.gdgst.palmtest.Entitys.ZX_Detail_Entity;
import cn.gdgst.palmtest.rewrite.ProgressWheel;
import cn.gdgst.palmtest.utils.NetworkCheck;
import cn.gdgst.palmtest.utils.ToastUtil;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ZiXunDetail extends Activity implements OnClickListener {
	private Long id = null;
	private ZX_Detail_Entity zX_Detail_Entity;
	private TextView tv_title, tv_loading;
	private WebView webView ;
	private ImageView iv_back;
	private ProgressWheel progress_bar;
	private String title=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wenku_detail);
		Intent intent = getIntent();
		id = intent.getLongExtra("id",0);
		title=intent.getStringExtra("tv_title");
		findview();
		progress_bar.spin();
	}

	private void findview() {
		// TODO Auto-generated method stub
//		tv1 = (TextView) findViewById(R.id.tv1);
        progress_bar=(ProgressWheel) findViewById(R.id.progress_bar);
        progress_bar.setBarColor(Color.parseColor("#28a3ed"));
		webView= (WebView) findViewById(R.id.webView1);
		tv_loading = (TextView) findViewById(R.id.tv_loading);

		WebSettings webSettings = webView .getSettings();
		webSettings.setLoadWithOverviewMode(true);
		webView.getSettings().setUseWideViewPort(true);// 关键点
		webView.getSettings().setDefaultFontSize(50);

		webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setDisplayZoomControls(true);
//		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		//webView.setBackgroundColor(Color.rgb(252, 230, 201));
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(true);
		webSettings.setSupportMultipleWindows(true);

		iv_back=(ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);

		tv_title=(TextView) findViewById(R.id.tv_title);
		tv_title.setText(title);

		//http://shiyan360.cn/api/news?id=1204
		webView.loadUrl("http://shiyan360.cn/api/news?id="+id);
        webView.addJavascriptInterface(new JavaScriptInterface(this), "imagelistner");
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String request) {
				if (request.equals("shiyan360.cn")){
					view.loadUrl(request);
					return false;
				}
				//如果不是自己的站点则launch别的Activity来处理
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(request));
				startActivity(intent);
				return true;
			}

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progress_bar.spin();
				tv_loading.setText(0+"%");
            }

            @Override
			public void onPageFinished(WebView view, String url) {
                progress_bar.setVisibility(View.GONE);
				tv_loading.setVisibility(View.GONE);
				imgReset();
                addImageClickListner();
			}
        });

		webView.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress <= 100){
					tv_loading.setText(newProgress+"%");
				}
			}
		});

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.iv_back:
				this.finish();
				break;
		}
	}

	/**
	 * 重置webview中img标签的图片大小
	 */
	private void imgReset() {
		webView.loadUrl("javascript:(function(){" +
				"var objs = document.getElementsByTagName('img');" +
				"for(var i = 0; i < objs.length; i++){" +
				"var img = objs[i];" +
				"img.style.width = '100%';" +
				"img.style.height = 'auto';" +
				"}" +
				"})()");
	}


    @Override
    protected void onDestroy() {
        if (webView != null){
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

    /**
     * html加载完成之后，添加监听图片的点击js函数
     */
    private void addImageClickListner() {
        // 这段js函数的功能就是，遍历所有的img节点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
        webView.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\");" +
                "for(var i = 0; i < objs.length; i++){" +
                "objs[i].onclick = function(){" +
                "window.imagelistner.openImage(this.src);" +
                "}" +
                "}" +
                "})()");
    }


    public static class JavaScriptInterface {

        private Context context;

        public JavaScriptInterface(Context context) {
            this.context = context;
        }

        //点击图片回调方法
        //必须添加注解,否则无法响应
        @JavascriptInterface
        public void openImage(String img) {
            Log.i("TAG", "响应点击事件!");
//            Intent intent = new Intent();
//            intent.putExtra(AppConstant.PHOTO_DETAIL, img);
//            //查看大图的类
//            intent.setClass(context, PhotosDetailActivity.class);
//            context.startActivity(intent);
        }
    }

	/**
	 * 通过网络请求框架来进行网络请求，效率高
	 */
	private void getExperimentListByRetrofit(){
		NetworkCheck check = new NetworkCheck(ZiXunDetail.this);
		boolean isAlivable = check.Network();
		if (isAlivable){
			APIWrapper.getInstance().getArtDetail(String.valueOf(id))
					.subscribeOn(Schedulers.io())
					.observeOn(AndroidSchedulers.mainThread())
					.subscribe(new Subscriber<HttpResult<ZX_Detail_Entity>>() {
						@Override
						public void onCompleted() {
							if (zX_Detail_Entity == null){
								mHandler.sendEmptyMessage(4);  //没有内容
							}else {
								mHandler.sendEmptyMessage(0);   //有内容
							}
						}

						@Override
						public void onError(Throwable e) {
							mHandler.sendEmptyMessage(1);
							ToastUtil.show(e.getMessage());
						}

						@Override
						public void onNext(HttpResult<ZX_Detail_Entity> zx_detail_entityHttpResult) {
							zX_Detail_Entity = zx_detail_entityHttpResult.getData();    //把返回的结果封装到ZX——Detail_Entity对象中
						}
					});
		}
	}


	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0:
					progress_bar.stopSpinning();

					StringBuilder sb = new StringBuilder();
					sb.append(zX_Detail_Entity.getContent());

					webView.setVisibility(View.VISIBLE);
					webView.loadDataWithBaseURL(null, sb.toString(), "text/html", "utf-8", null);

					break;
				case 1:
					ToastUtil.show("获取列表失败，请重试...");
					break;
				case 2:
					//getExperimentList();
					//getExperimentListByRetrofit();
					break;
				case 3:
					progress_bar.spin();
					break;
				case 4:
					progress_bar.stopSpinning();
					ToastUtil.show("暂无相关内容");
					break;
				default:
					break;
			}
		}
	};

}

