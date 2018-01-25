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
import cn.gdgst.palmtest.imagecache.ImageLoader;
import cn.gdgst.palmtest.main.Tab2Activity;
import cn.gdgst.palmtest.tab2.Vid_Play_Activity;
import cn.gdgst.palmtest.tab3.SimulationPlayActivity;
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
	private SearchResultAdapter searchResultAdapter;
	/**
	 * 浮动关键字的视图
	 */
	private KeywordsFlow keywordsFlow;
	/**
	 * 带有删除的EditText
	 */
	private DeletableEditText deletableEditText;
	/**
	 * 搜索按钮
	 */
	private Button button_Search;
	private ListView listView;
	private TextView tvNull;
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
					if (list_AppSearchEntity.isEmpty()){

						feedKeywordsFlow(keywordsFlow, keywords);
						keywordsFlow.setVisibility(View.VISIBLE);

					}
					listView.setAdapter(searchResultAdapter);
					break;
				case INTERNET_FALSE:
					NetworkDialogUtils.getInstance().HideNetworkDialog();
					break;
			}
		};
	};

	/**
	 *在这个页面初始化3D滚动页面的关键字，吧关键字传过去
	 * @param keywordsFlow
	 * @param arr
     */
	private static void feedKeywordsFlow(KeywordsFlow keywordsFlow, String[] arr) {
		//用随机数来放置关键字
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
		searchResultAdapter = new SearchResultAdapter();
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String url = list_AppSearchEntity.get(position).getUrl();
				Log.e("TAG", "======"+url);

				String testStr = url.substring(url.length() - 3, url.length());

				if (testStr.equals("mp4")) {
					Intent myIntent3 = new Intent();
					//需要把视频播放的路径和名称传过去
					myIntent3.putExtra("video_path", "http://www.shiyan360.cn"+url);
					myIntent3.putExtra("video_name", list_AppSearchEntity.get(position).getTitle());
					myIntent3.setClass(SearchActivity.this, Vid_Play_Activity.class);
					SearchActivity.this.startActivity(myIntent3);
				}else {
					Intent myIntent3 = new Intent();
					//需要把视频播放的路径和名称传过去
					myIntent3.putExtra("url", "http://www.shiyan360.cn"+url);
					myIntent3.putExtra("name", list_AppSearchEntity.get(position).getTitle());
					myIntent3.setClass(SearchActivity.this, SimulationPlayActivity.class);
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
		tvNull = (TextView) findViewById(R.id.tv_null);
		button_Search = (Button) findViewById(R.id.activity_search_button);

		button_Search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				list_AppSearchEntity = new ArrayList<AppSearchEntity>();
				list_AppSearchEntity.clear();
				searchResultAdapter.notifyDataSetChanged();
				keyword = deletableEditText.getText().toString().trim();
				if (keyword != null || !keyword.equals("")) {
					NetworkDialogUtils.getInstance().ShowNetworkDialog();
					APIWrapper.getInstance().appSearch(keyword,1)
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
										list_AppSearchEntity = listHttpResult.getData();
										if (!list_AppSearchEntity.isEmpty()) {
											keywordsFlow.setVisibility(View.GONE);
											tvNull.setVisibility(View.GONE);
											for (int i = 0; i <= list_AppSearchEntity.size() - 1; i++){
												if ((list_AppSearchEntity.get(i).getUrl()).equals("") || list_AppSearchEntity.get(i).getUrl() == null){
													list_AppSearchEntity.remove(i);
													i = i - 1;
												}
											}
										}else {
												keywordsFlow.setVisibility(View.VISIBLE);
												tvNull.setVisibility(View.VISIBLE);
										}
									}
								}
							});
				}
				feedKeywordsFlow(keywordsFlow, keywords);
				keywordsFlow.setVisibility(View.VISIBLE);
			}
		});
		feedKeywordsFlow(keywordsFlow, keywords);
		keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
		handler.sendEmptyMessageDelayed(FEEDKEY_START, 5000);
	}

	/**
	 * 点击滚动的关键字就放到EditText中去
	 * @param v
     */
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
			ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = getLayoutInflater().inflate(R.layout.item_search_result_adapter, null);
				holder.textView_tile = (TextView) convertView.findViewById(R.id.item_search_result_adapter_text);
				holder.textView_catergory = (TextView) convertView.findViewById(R.id.text_catergory);
				holder.imageView = (ImageView) convertView.findViewById(R.id.imageview);
				convertView.setTag(holder);
			}else {
				holder = (ViewHolder) convertView.getTag();
			}
			AppSearchEntity searchEntity = list_AppSearchEntity.get(position);
			//Log.e("TAG", "-------------->"+searchEntity.toString());
			holder.textView_tile.setText(searchEntity.getTitle());
			String model = "";
			switch (searchEntity.getModel()){
				case "Play":
					model = "同步视频";
					break;
				case "Shiyan":
					model = "实验";
					break;
				case "Mingshi":
					model = "名师讲谈";
					break;
				case "Chuangke":
					model = "创客";
					break;
			}
			holder.textView_catergory.setText(model);
			String url = "http://www.shiyan360.cn"+searchEntity.getImg();

			com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(url, holder.imageView);
			return convertView;
		}
		class ViewHolder{
			ImageView imageView;
			TextView textView_tile;
			TextView textView_catergory;
		}
	}

}
