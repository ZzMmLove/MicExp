package cn.gdgst.palmtest.search;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import cn.gdgst.palmtest.API.APIWrapper;
import cn.gdgst.palmtest.Entitys.AppSearchEntity;
import cn.gdgst.palmtest.R;
import cn.gdgst.palmtest.base.NetworkBaseActivity;
import cn.gdgst.palmtest.bean.HttpResult;
import cn.gdgst.palmtest.main.Tab2Activity;
import cn.gdgst.palmtest.tab2.Vid_Play_Activity;
import cn.gdgst.palmtest.utils.NetworkDialogUtils;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SearchActivity extends NetworkBaseActivity implements OnClickListener {
	private static final int FEEDKEY_START = 1;
	private final int 	INTERNET_SUCCESS = 2;
	private final int INTERNET_FALSE = 3;
	private ImageView back_arrow;
	private Animation shakeAnim;
	private DeletableEditText searchEdit;
	private KeywordsFlow keywordsFlow;
	private DeletableEditText deletableEditText;
	private Button button_Search;
	private ListView listView;
	private String keyword;
	private List<AppSearchEntity> list_AppSearchEntity;
	private int STATE = 1;

	private static String[] keywords = new String[] { "中考热点", "实验解密", "高考热点",
			"高考指点", "高考指南", "有机化合物", "离子方程", "中考分析指南", "元素周期律", "基因工程", "有丝分裂", "运动与力",
			"高考迷津指点", "中学高分指南", "中学知识宝典", "物态变化", "光合作用", " 呼吸作用", " 速成自学", "自学宝典",
			"欧姆定律", "初中物理", "电磁作用", "氧化反应", "染色体变异", "化合作用" };

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case FEEDKEY_START:
					keywordsFlow.rubKeywords();
					feedKeywordsFlow(keywordsFlow, keywords);
					keywordsFlow.go2Show(KeywordsFlow.ANIMATION_OUT);
					sendEmptyMessageDelayed(FEEDKEY_START, 5000);
					break;
				case INTERNET_SUCCESS:
					NetworkDialogUtils.getInstance().HideNetworkDialog();
					keywordsFlow.setVisibility(View.GONE);
					//ArrayAdapter arrayAdapter = new ArrayAdapter(SearchActivity.this, R.layout.item_search_result_adapter,list_AppSearchEntity);
					SearchResultAdapter searchResultAdapter = new SearchResultAdapter();
					listView.setAdapter(searchResultAdapter);
					break;
				case INTERNET_FALSE:
					NetworkDialogUtils.getInstance().HideNetworkDialog();
					break;
			}
		};
	};

	private static void feedKeywordsFlow(KeywordsFlow keywordsFlow, String[] arr) {
		Random random = new Random();
		for (int i = 0; i < KeywordsFlow.MAX; i++) {
			int ran = random.nextInt(arr.length);
			String tmp = arr[ran];
			keywordsFlow.feedKeyword(tmp);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("搜索");
		setContentView(R.layout.activity_search);
		NetworkDialogUtils.init(SearchActivity.this);
		shakeAnim = AnimationUtils.loadAnimation(this, R.anim.shake_y);
		initView();
	}

	private void initView() {
		listView = (ListView) findViewById(R.id.activity_search_listView);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String url = list_AppSearchEntity.get(position).getUrl();
				if (url.substring(url.lastIndexOf(".")).equals(".mp4")) {
					Intent myIntent3 = new Intent();
					myIntent3.putExtra("video_path", "http://www.shiyan360.cn"+url);
					myIntent3.putExtra("video_name", list_AppSearchEntity.get(position).getTitle());
					myIntent3.setClass(SearchActivity.this, Vid_Play_Activity.class);
					SearchActivity.this.startActivity(myIntent3);
				}
			}
		});
		keywordsFlow = (KeywordsFlow) findViewById(R.id.keywordsflow);
		keywordsFlow.setDuration(1000l);
		keywordsFlow.setOnItemClickListener(this);
		back_arrow = (ImageView) findViewById(R.id.back_arrow);
		back_arrow.setAnimation(shakeAnim);
		searchEdit = (DeletableEditText) findViewById(R.id.search_view);
		deletableEditText = (DeletableEditText) findViewById(R.id.search_view);
		button_Search = (Button) findViewById(R.id.activity_search_button);
		button_Search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				keyword = deletableEditText.getText().toString();
				if (keyword != null || !keyword.equals("")) {
					NetworkDialogUtils.getInstance().ShowNetworkDialog();
					APIWrapper.getInstance().appSearch(keyword, 1)
							.subscribeOn(Schedulers.io())
							.observeOn(AndroidSchedulers.mainThread())
							.subscribe(new Subscriber<HttpResult<List<AppSearchEntity>>>() {
								@Override
								public void onCompleted() {
									handler.sendEmptyMessage(INTERNET_SUCCESS);
								}

								@Override
								public void onError(Throwable e) {
									handler.sendEmptyMessage(INTERNET_FALSE);
								}

								@Override
								public void onNext(HttpResult<List<AppSearchEntity>> listHttpResult) {
									if (listHttpResult.getSuccess()) {
										list_AppSearchEntity = new ArrayList<AppSearchEntity>();
										list_AppSearchEntity = listHttpResult.getData();
										if (!list_AppSearchEntity.isEmpty()) {
											for (AppSearchEntity ase: list_AppSearchEntity) {
												if (ase.getUrl() == null || ase.getUrl().equals("")) {
													list_AppSearchEntity.remove(ase);
												}
												Log.d("SearchActivity", "测试返回的搜索结果:"+ase.getTitle());
											}
										}
									}
								}
							});
				}
			}
		});
		feedKeywordsFlow(keywordsFlow, keywords);
		keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
		handler.sendEmptyMessageDelayed(FEEDKEY_START, 5000);
	}

	@Override
	public void onClick(View v) {
		if (v instanceof TextView) {
			String keyword = ((TextView) v).getText().toString().trim();
			searchEdit.setText(keyword);
			searchEdit.setSelection(keyword.length());
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		back_arrow.clearAnimation();
		handler.removeMessages(FEEDKEY_START);
		STATE = 0;
	}

	@Override
	protected void onStop() {
		super.onStop();
		handler.removeMessages(FEEDKEY_START);
		STATE = 0;
	}

	@Override
	public void onPause() {
		super.onPause();
		handler.removeMessages(FEEDKEY_START);
		STATE = 0;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (STATE == 0) {
			keywordsFlow.rubKeywords();
			handler.sendEmptyMessageDelayed(FEEDKEY_START, 3000);
		}

	}

	private class SearchResultAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return list_AppSearchEntity.size();
		}

		@Override
		public Object getItem(int position) {
			return list_AppSearchEntity.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView textview_result = null;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.item_search_result_adapter, null);
				textview_result = (TextView) convertView.findViewById(R.id.item_search_result_adapter_text);
			}else {
				textview_result = (TextView) convertView.findViewById(R.id.item_search_result_adapter_text);
			}
			textview_result.setText(list_AppSearchEntity.get(position).getTitle());
			return convertView;
		}
	}

}
