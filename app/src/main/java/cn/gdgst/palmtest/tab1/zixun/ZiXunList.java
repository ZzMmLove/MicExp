package cn.gdgst.palmtest.tab1.zixun;

import android.app.Activity;
import android.content.Intent;
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
import cn.gdgst.entity.ZiXun;
import cn.gdgst.palmtest.R;
import com.orhanobut.logger.Logger;
import cn.gdgst.palmtest.API.APIWrapper;
import cn.gdgst.palmtest.DB.DbService;
import cn.gdgst.palmtest.bean.HttpResult;
import cn.gdgst.palmtest.rewrite.ProgressWheel;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ZiXunList extends Activity implements OnClickListener {
	private PullToRefreshListView MSListview;
	private List<ZiXun> ZiXunList = new ArrayList<ZiXun>();
	private List<ZiXun> ZiXunListEntities = new ArrayList<ZiXun>();
	private ZiXun ZiXunListEntity;
	private ZiXunAdapter adapter;
	private ProgressWheel progress_bar;
	private ImageView iv_back;
	private TextView tv_loading;

	private int desctype = 0;
	private int page = 1;

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
		ZiXunList.clear();
//		getCacheCollectdata();
		readZiXunDB();
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
	}

	private void initListView() {
		// TODO Auto-generated method stub
		adapter = new ZiXunAdapter(this, ZiXunList);

		MSListview.setMode(Mode.BOTH);// 使列表可以同时下拉和上拉刷新
		ListView actualListView = MSListview.getRefreshableView();

		MSListview.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override // 下拉监听
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				page = 1;
				ZiXunList.clear();
//				getExperimentList();
				getZxList();
				adapter.notifyDataSetChanged();
			}

			@Override // 上拉监听
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {

				if (ZiXunListEntities.size() < 20) {
					// TODO Auto-generated method stub
					MSListview.postDelayed(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							adapter.notifyDataSetChanged();
							MSListview.onRefreshComplete();
							Toast.makeText(ZiXunList.this, "没有更多数据了", Toast.LENGTH_SHORT).show();
						}
					}, 500);

				} else {
					MSListview.getLoadingLayoutProxy(false, true).setLastUpdatedLabel("");
					MSListview.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载");
					MSListview.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
					MSListview.getLoadingLayoutProxy(false, true).setReleaseLabel("放开以加载");
					page = page + 1;
					Logger.i("page" + page);
//					getExperimentList();
					getZxList();
					adapter.notifyDataSetChanged();
				}

			}
		});
		actualListView.setAdapter(adapter);

		MSListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("id", ZiXunList.get(position - 1).getId());
				intent.putExtra("tv_title", "实验资讯详情");
				Logger.i( "传值id" + ZiXunList.get(position - 1).getId());
				intent.setClass(ZiXunList.this, ZiXunDetail.class);
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

//	private void getExperimentList() {
//		// TODO Auto-generated method stub
//		new Thread() {
//			public void run() {
//				String urlStr = "http://www.shiyan360.cn/index.php/api/article_list";
//				String category_id = String.valueOf(99);// 99为实验资讯列表
//				String paged = String.valueOf(page);
//
//				NetworkCheck check = new NetworkCheck(ZiXunList.this);
//				boolean isalivable = check.Network();
//				if (isalivable) {
//					// 封装请求参数
//					Map<String, String> rawParams = new HashMap<String, String>();
//					rawParams.put("category_id", category_id);
//					rawParams.put("page", paged);
//
//					try {
//						// mHandler.sendEmptyMessage(3);
//						Thread.sleep(2000);
//						// 设置请求参数项
//						// 发送请求返回json
//						String json = HttpUtil.postRequest(urlStr, rawParams);
//						Logger.json(json);
//						// 解析json数据
//						com.alibaba.fastjson.JSONObject jsonobj = JSON.parseObject(json);
//						Boolean response = (Boolean) jsonobj.get("success");
//
//						// 判断是否请求成功
//						if (response) {
//							// 解析截取“data”中的内容
//							com.alibaba.fastjson.JSONArray jsondata = jsonobj.getJSONArray("data");
//							String array = JSON.toJSONString(jsondata);
//							ZiXunListEntities = JSON.parseArray(array, ZiXun.class);
//							Logger.i( ZiXunListEntities.size() + "");
//							if (!ZiXunListEntities.isEmpty()) {
//								mHandler.sendEmptyMessage(0);
//								ACache mCache = ACache.get(ZiXunList.this);
//								mCache.put("ZiXunList", json);
//
//							}
//
//						} else {
//							mHandler.sendEmptyMessage(1);
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//						mHandler.sendEmptyMessage(4);
//					}
//
//				} else {
//					NetworkCheckDialog.dialog(ZiXunList.this);
//				}
//
//			}
//
//		}.start();
//
//	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0:

					for (int i = 0; i < ZiXunListEntities.size(); i++) {
						ZiXunListEntity = new ZiXun();
						ZiXunListEntity.setImg_url(ZiXunListEntities.get(i).getImg_url().trim());
						ZiXunListEntity.setTitle(ZiXunListEntities.get(i).getTitle());
						ZiXunListEntity.setId(ZiXunListEntities.get(i).getId());
						ZiXunList.add(ZiXunListEntity);

					}
					MSListview.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							progress_bar.stopSpinning();
							tv_loading.setVisibility(View.GONE);
							adapter.notifyDataSetChanged();
							MSListview.onRefreshComplete();

							// Toast.makeText(Tab3Activity.this, "获取列表成功",
							// Toast.LENGTH_SHORT).show();
						}
					});

					break;
				case 1:
					MSListview.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							progress_bar.stopSpinning();
							tv_loading.setVisibility(View.GONE);
							adapter.notifyDataSetChanged();
							MSListview.onRefreshComplete();
							MSListview.setEmptyView(findViewById(R.id.empty));
							Toast.makeText(ZiXunList.this, "获取列表失败,请先进行登录", Toast.LENGTH_SHORT).show();
						}
					});

					break;
				case 2:
//					getExperimentList();
					getZxList();
					break;
				case 3:
					tv_loading.setVisibility(View.VISIBLE);
					progress_bar.spin();
					break;
				case 4:
					MSListview.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							progress_bar.stopSpinning();
							tv_loading.setVisibility(View.GONE);
							adapter.notifyDataSetChanged();
							MSListview.onRefreshComplete();
							MSListview.setEmptyView(findViewById(R.id.empty));
							Toast.makeText(ZiXunList.this, "暂无相关内容！", Toast.LENGTH_SHORT).show();
						}
					});

					break;
				default:
					break;
			}

		}

	};

	// 缓存中获取数据
//	public void getCacheCollectdata() {
//		ACache mCache = ACache.get(ZiXunList.this);
//		String value = mCache.getAsString("ZiXunList");
//		Logger.json(value);
//		if (value != null) {
//			com.alibaba.fastjson.JSONObject jsonobj = JSON.parseObject(value);
//			com.alibaba.fastjson.JSONArray jsondata = jsonobj.getJSONArray("data");
//			String array = JSON.toJSONString(jsondata);
//			ZiXunListEntities = JSON.parseArray(array, ZiXun.class);
//			mHandler.sendEmptyMessage(0);
//		} else {
////			getExperimentList();
//			getZxList();
//		}
//
//	}

	public void getZxList(){
		String category_id = String.valueOf(99);// 99为实验资讯列表
		String paged = String.valueOf(page);
		Logger.i("page" + paged);

		APIWrapper.getInstance().getArticleListzx(category_id, paged)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<HttpResult<List<ZiXun>>>() {
					@Override
					public void onCompleted() {
						if (ZiXunListEntities==null||ZiXunListEntities.size()<=0){
							mHandler.sendEmptyMessage(4);
							Logger.i("mhander 4");
						}else {
							mHandler.sendEmptyMessage(0);
							Logger.i("mhander 0");
							saveZiXunDB();
						}
					}

					@Override
					public void onError(Throwable e) {

						mHandler.sendEmptyMessage(1);
						Logger.i("mhander 1");



					}

					@Override
					public void onNext(HttpResult<List<ZiXun>> listHttpResult) {

						ZiXunListEntities = listHttpResult.getData();

//						ACache mCache = ACache.get(ChuangKeList.this);
//						mCache.put("ChuangKeList", listHttpResult.getData().toString());




					}
				});
	}

	public void saveZiXunDB() {

		List<ZiXun> list = new ArrayList<>();
		for (int i = 0; i < ZiXunListEntities.size(); i++) {
			ZiXun ziXunlist = new ZiXun();
			ziXunlist.setId(ZiXunListEntities.get(i).getId());
			ziXunlist.setTitle(ZiXunListEntities.get(i).getTitle());
			ziXunlist.setImg_url(ZiXunListEntities.get(i).getImg_url());
			ziXunlist.setImg_url_s(ZiXunListEntities.get(i).getImg_url_s());
			ziXunlist.setVideo_url(ZiXunListEntities.get(i).getVideo_url());
			ziXunlist.setPid(ZiXunListEntities.get(i).getPid());
			list.add(ziXunlist);
		}
		db.saveZiXunLists(list);

	}

	public void readZiXunDB() {
		if (!db.loadAllZiXun().isEmpty()) {
			ZiXunListEntities=db.loadAllZiXunByOrder();
			mHandler.sendEmptyMessage(0);

		}
		else{
			getZxList();
		}

	}
}
