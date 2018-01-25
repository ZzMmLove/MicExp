package cn.gdgst.palmtest.tab1.huizhan;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import cn.gdgst.entity.HuiZhan;
import cn.gdgst.palmtest.R;
import com.orhanobut.logger.Logger;
import cn.gdgst.palmtest.API.APIWrapper;
import cn.gdgst.palmtest.DB.DbService;
import cn.gdgst.palmtest.bean.HttpResult;
import cn.gdgst.palmtest.rewrite.ProgressWheel;
import cn.gdgst.palmtest.tab1.zixun.ZiXunDetail;

import java.util.ArrayList;
import java.util.List;

import cn.gdgst.palmtest.utils.NetworkCheck;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class HuiZhanListActivity extends Activity implements OnClickListener {
	private PullToRefreshListView MSListview;
	private List<HuiZhan> HuiZhanList = new ArrayList<HuiZhan>();
	private List<HuiZhan> HuiZhanListEntities = new ArrayList<>();
	private HuiZhan HuiZhanListEntity;
	private SharedPreferences sp;
	private HuiZhanAdapter adapter;

	private ImageView iv_back;
	private ProgressWheel progress_bar;
	private TextView tv_loading;
	private int desctype = 0;
	private int page = 1;
	private TextView tv_title;

	//greendao
	private DbService db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zixun_list);
		findview();
		initListView();
		db = DbService.getInstance(this);
		HuiZhanList.clear();
        NetworkCheck check = new NetworkCheck(this);
        if (!check.Network()){
            readHuiZhanDB();
        }else {
            getHzList(page);
        }
	}

	private void findview() {
		// TODO Auto-generated method stub
		MSListview = (PullToRefreshListView) findViewById(R.id.exp_display);
		// MSListview.setEmptyView(findViewById(R.id.empty));

		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);

		progress_bar = (ProgressWheel) findViewById(R.id.progress_bar);
		progress_bar.setBarColor(Color.parseColor("#63c5fe"));
		progress_bar.spin();

		tv_loading = (TextView) findViewById(R.id.tv_loading);
		tv_title = (TextView) findViewById(R.id.tv_title);
		Intent it = getIntent();
		tv_title.setText(it.getStringExtra("title"));
	}

	private void initListView() {
		adapter = new HuiZhanAdapter(this, HuiZhanList);
		MSListview.setMode(Mode.BOTH);// 使列表可以同时下拉和上拉刷新
		ListView actualListView = MSListview.getRefreshableView();

		MSListview.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override // 下拉监听
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				page = 1;
				getHzList(page);
				HuiZhanList.clear();
				adapter.notifyDataSetChanged();
			}

			@Override // 上拉监听
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {

				if (HuiZhanListEntities.size() < 10) {
					// TODO Auto-generated method stub
					MSListview.postDelayed(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							adapter.notifyDataSetChanged();
							MSListview.onRefreshComplete();
							Toast.makeText(HuiZhanListActivity.this, "没有更多数据了", Toast.LENGTH_SHORT).show();
						}
					}, 500);

				} else {
					MSListview.getLoadingLayoutProxy(false, true).setLastUpdatedLabel("");
					MSListview.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载");
					MSListview.getLoadingLayoutProxy(false, true).setReleaseLabel("放开以加载");
					MSListview.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
					page = page + 1;
					Logger.i("page"+ "page" + page);
					getHzList(page);
				}
			}
		});
		actualListView.setAdapter(adapter);

		MSListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("id", HuiZhanList.get(position - 1).getId());
				intent.putExtra("tv_title", "新闻详情");
				Logger.i("传值id" + HuiZhanList.get(position - 1).getId());
				intent.setClass(HuiZhanListActivity.this, ZiXunDetail.class);
				startActivity(intent);
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

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0:
                    progress_bar.stopSpinning();
                    tv_loading.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    MSListview.onRefreshComplete();
					for (int i = 0; i < HuiZhanListEntities.size(); i++) {
						HuiZhanListEntity = new HuiZhan();
						//HuiZhanListEntity.setImg_url(HuiZhanListEntities.get(i).getImg_url().trim());
						HuiZhanListEntity.setTitle(HuiZhanListEntities.get(i).getTitle());
						HuiZhanListEntity.setId(HuiZhanListEntities.get(i).getId());
						HuiZhanListEntity.setDateline(HuiZhanListEntities.get(i).getDateline());
						HuiZhanList.add(HuiZhanListEntity);
					}

					break;
				case 1:

					progress_bar.stopSpinning();
					tv_loading.setVisibility(View.GONE);
					adapter.notifyDataSetChanged();
					MSListview.onRefreshComplete();
					MSListview.setEmptyView(findViewById(R.id.empty));
					Toast.makeText(HuiZhanListActivity.this, "网络连接超时…", Toast.LENGTH_SHORT).show();

					break;
				case 2:
					getHzList(page);
					break;
				case 3:
					tv_loading.setVisibility(View.VISIBLE);
					progress_bar.spin();
					break;
				case 4:
					progress_bar.stopSpinning();
					tv_loading.setVisibility(View.GONE);
					adapter.notifyDataSetChanged();
					MSListview.onRefreshComplete();
					MSListview.setEmptyView(findViewById(R.id.empty));
					Toast.makeText(HuiZhanListActivity.this, "暂无相关内容！", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
			}

		}

	};

	// 缓存中获取数据
//	public void getCacheCollectdata() {
//		ACache mCache = ACache.get(HuiZhanListActivity.this);
//		String value = mCache.getAsString("HuiZhanListActivity");
//		Logger.json(value);
//		if (value != null) {
//			com.alibaba.fastjson.JSONObject jsonobj = JSON.parseObject(value);
//			com.alibaba.fastjson.JSONArray jsondata = jsonobj.getJSONArray("data");
//			String array = JSON.toJSONString(jsondata);
//			HuiZhanListEntities = JSON.parseArray(array, HuiZhan.class);
//			mHandler.sendEmptyMessage(0);
//		} else {
//			getExperimentList();
//		}
//
//	}
	public void getHzList(int page){
		String paged = String.valueOf(page);
		APIWrapper.getInstance().getArticleListhz(paged)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<HttpResult<List<HuiZhan>>>() {
					@Override
					public void onCompleted() {
						if (HuiZhanListEntities==null||HuiZhanListEntities.size()<=0){
							mHandler.sendEmptyMessage(4);
						}else {
							mHandler.sendEmptyMessage(0);
							saveHuiZhanDB();
						}
					}

					@Override
					public void onError(Throwable e) {
						mHandler.sendEmptyMessage(1);
					}

					@Override
					public void onNext(HttpResult<List<HuiZhan>> listHttpResult) {
						HuiZhanListEntities = listHttpResult.getData();
//						ACache mCache = ACache.get(ChuangKeList.this);
//						mCache.put("ChuangKeList", listHttpResult.getData().toString());
					}
				});
	}

	public void saveHuiZhanDB() {

		List<HuiZhan> list = new ArrayList<>();
		for (int i = 0; i < HuiZhanListEntities.size(); i++) {
			HuiZhan huiZhanlist = new HuiZhan();
			huiZhanlist.setId(HuiZhanListEntities.get(i).getId());
			huiZhanlist.setTitle(HuiZhanListEntities.get(i).getTitle());
            huiZhanlist.setDateline(HuiZhanListEntities.get(i).getDateline());
//			huiZhanlist.setImg_url(HuiZhanListEntities.get(i).getImg_url());
//			huiZhanlist.setImg_url_s(HuiZhanListEntities.get(i).getImg_url_s());
//			huiZhanlist.setVideo_url(HuiZhanListEntities.get(i).getVideo_url());
//			huiZhanlist.setPid(HuiZhanListEntities.get(i).getPid());
			list.add(huiZhanlist);
		}
		db.saveHuiZhanLists(list);

	}

	public void readHuiZhanDB() {
		if (!db.loadAllHuiZhan().isEmpty()) {
			HuiZhanListEntities=db.loadAllHuiZhanByOrder();
			mHandler.sendEmptyMessage(0);
		}
		else{
			getHzList(page);
		}
	}

}
