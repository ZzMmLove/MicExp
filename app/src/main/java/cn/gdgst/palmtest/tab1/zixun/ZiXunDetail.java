package cn.gdgst.palmtest.tab1.zixun;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import cn.gdgst.palmtest.R;
import cn.gdgst.palmtest.utils.HttpUtil;

import com.orhanobut.logger.Logger;
import cn.gdgst.palmtest.Entitys.ZX_Detail_Entity;
import cn.gdgst.palmtest.rewrite.ProgressWheel;
import cn.gdgst.palmtest.utils.NetworkCheck;
import cn.gdgst.palmtest.utils.NetworkCheckDialog;

import java.util.HashMap;
import java.util.Map;

public class ZiXunDetail extends Activity implements OnClickListener {
	private SharedPreferences sp;
	private Long id = null;
	private ZX_Detail_Entity ZX_Detail_Entity;
	private TextView tv_title,tv_loading;
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
		Logger.i("传值过来的id"+id);
		title=intent.getStringExtra("tv_title");
		Logger.i("传值过来的id"+title);

		findview();
		initListView();
		webView.setVisibility(View.INVISIBLE);
		progress_bar.spin();

		getExperimentList();


	}

	private void findview() {
		// TODO Auto-generated method stub
//		tv1 = (TextView) findViewById(R.id.tv1);
		webView= (WebView) findViewById(R.id.webView1);

		WebSettings webSettings =   webView .getSettings();
		webSettings.setLoadWithOverviewMode(true);
		webView.getSettings().setUseWideViewPort(true);// 关键点
		webView.getSettings().setDefaultFontSize(50);

		webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		webView.setBackgroundColor(Color.rgb(252, 230, 201));
		webSettings.setJavaScriptEnabled(false);


		iv_back=(ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);

		tv_title=(TextView) findViewById(R.id.tv_title);
		tv_title.setText(title);

		progress_bar=(ProgressWheel) findViewById(R.id.progress_bar);
		progress_bar.setBarColor(Color.parseColor("#63c5fe"));
		progress_bar.spin();


		tv_loading=(TextView) findViewById(R.id.tv_loading);
	}

	private void initListView() {
		// TODO Auto-generated method stub
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

	private void getExperimentList() {
		// TODO Auto-generated method stub
		new Thread() {
			public void run() {
				String urlStr = "http://www.shiyan360.cn/index.php/api/article_detail";
				NetworkCheck check = new NetworkCheck(ZiXunDetail.this);
				boolean isalivable = check.Network();
				if (isalivable) {
					// 封装请求参数
					Map<String, String> rawParams = new HashMap<String, String>();
					rawParams.put("id", id.toString());

					try {
//						mHandler.sendEmptyMessage(3);
						Thread.sleep(2000);
						// 设置请求参数项
						// 发送请求返回json
						String json = HttpUtil.postRequest(urlStr, rawParams);
						Logger.json(json);
						// 解析json数据
						com.alibaba.fastjson.JSONObject jsonobj = JSON.parseObject(json);
						Boolean response = (Boolean) jsonobj.get("success");

						// 判断是否请求成功
						if (response) {
							// 解析截取“data”中的内容
							com.alibaba.fastjson.JSONObject jsondata = jsonobj.getJSONObject("data");
							String js = jsondata.toString();
							// Json解析出单个对象
							ZX_Detail_Entity = JSON.parseObject(js, ZX_Detail_Entity.class);

							if (jsondata.equals("null")) {
								mHandler.sendEmptyMessage(4);
							} else {
								mHandler.sendEmptyMessage(0);
							}

						} else {
							mHandler.sendEmptyMessage(1);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				} else {
					NetworkCheckDialog.dialog(ZiXunDetail.this);
				}

			}

		}.start();

	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0:
					progress_bar.stopSpinning();

					StringBuilder sb=new StringBuilder();
					sb.append(ZX_Detail_Entity.getContent());

					webView.setVisibility(View.VISIBLE);
					webView.loadDataWithBaseURL(null, sb.toString(), "text/html", "utf-8", null);

					break;
				case 1:
					Toast.makeText(ZiXunDetail.this, "获取列表失败,请先进行登录", Toast.LENGTH_SHORT).show();
					break;
				case 2:
					getExperimentList();
					break;
				case 3:
					progress_bar.spin();
					break;
				case 4:
					progress_bar.stopSpinning();
					Toast.makeText(ZiXunDetail.this, "暂无相关内容！", Toast.LENGTH_SHORT).show();

					break;
				default:
					break;
			}

		}

	};




}

