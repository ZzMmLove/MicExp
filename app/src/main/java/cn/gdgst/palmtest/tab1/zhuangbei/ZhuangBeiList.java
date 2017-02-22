package cn.gdgst.palmtest.tab1.zhuangbei;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import cn.gdgst.entity.ZhuangBei;
import cn.gdgst.palmtest.R;
import com.orhanobut.logger.Logger;
import cn.gdgst.palmtest.API.APIWrapper;
import cn.gdgst.palmtest.DB.DbService;
import cn.gdgst.palmtest.Entitys.PX_Cate_Entity;
import cn.gdgst.palmtest.bean.HttpResult;
import cn.gdgst.palmtest.rewrite.ProgressWheel;
import cn.gdgst.palmtest.servers.GetSortList;
import cn.gdgst.palmtest.tab2.Vid_Play_Activity;
import cn.gdgst.palmtest.tab3.MyAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ZhuangBeiList extends AppCompatActivity implements OnDismissListener, OnClickListener {
	private PullToRefreshListView MSListview;
	private List<ZhuangBei> ZhuangBeiList = new ArrayList<ZhuangBei>();
	private List<ZhuangBei> List_ZhuangBeiEntities = new ArrayList<ZhuangBei>();
	private ZhuangBei ZhuangBeiListEntity;
	private SharedPreferences sp;
	private ZhuangBeiAdapter adapter;
	private ProgressWheel progress_bar;
	private TextView tv_grade, tv_loading;
	private LinearLayout ll_grade, lv1_layout;
	private ImageView icon1;
	private ListView lv1;
	private MyAdapter myadapter;
	private int idx;
	private List<PX_Cate_Entity> PX_Cate_List;

	private int desctype = 0;
	private String wkid = "135"; // 专辑ID
	private int page = 1;
	private Boolean isRefreshing = false;
	//private TextView tv_title;


	//greendao
	private DbService db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exam_list);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("实验装备");
		db = DbService.getInstance(this);
		findview();
		initListView();

		ZhuangBeiList.clear();
//		getCacheCollectdata();
		readZhuangBeiDB();
		getwkcateList();

	}

	private void findview() {
		// TODO Auto-generated method stub
		MSListview = (PullToRefreshListView) findViewById(R.id.exp_display);
		// MSListview.setEmptyView(findViewById(R.id.empty));
		// 文本
		tv_grade = (TextView) findViewById(R.id.tv_grade);
		tv_grade.setText("装备分类");
		// 布局
		ll_grade = (LinearLayout) findViewById(R.id.ll_grade);

		ll_grade.setOnClickListener((OnClickListener) this);
		// 图片
		icon1 = (ImageView) findViewById(R.id.icon1);
		//iv_back = (ImageView) findViewById(R.id.iv_back);
		//iv_back.setOnClickListener(this);
		progress_bar = (ProgressWheel) findViewById(R.id.progress_bar);
		progress_bar.setBarColor(Color.parseColor("#63c5fe"));
		progress_bar.spin();
		tv_loading = (TextView) findViewById(R.id.tv_loading);
		//tv_title = (TextView) findViewById(R.id.tv_title);
		//tv_title.setText("实验装备");
	}

	private void initListView() {
		// TODO Auto-generated method stub
		adapter = new ZhuangBeiAdapter(this, ZhuangBeiList);

		MSListview.setMode(Mode.BOTH);// 使列表可以同时下拉和上拉刷新
		ListView actualListView = MSListview.getRefreshableView();

		MSListview.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override // 下拉监听
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				page = 1;
				ZhuangBeiList.clear();
//				getExperimentList();
				getZbList();
				getwkcateList();
				adapter.notifyDataSetChanged();

			}

			@Override // 上拉监听
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {

				if (List_ZhuangBeiEntities.size() < 20) {
					// TODO Auto-generated method stub
					MSListview.postDelayed(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							adapter.notifyDataSetChanged();
							MSListview.onRefreshComplete();
							Toast.makeText(ZhuangBeiList.this, "没有更多数据了", Toast.LENGTH_SHORT).show();
						}
					}, 500);

				} else {
					MSListview.getLoadingLayoutProxy(false, true).setLastUpdatedLabel("");
					MSListview.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载");
					MSListview.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
					MSListview.getLoadingLayoutProxy(false, true).setReleaseLabel("放开以加载");
					page = page + 1;
					Logger.i( "page" + page);
//					getExperimentList();
					getZbList();
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
				intent.putExtra("video_path", ZhuangBeiList.get(position - 1).getVideo_url());
				intent.putExtra("video_name", ZhuangBeiList.get(position - 1).getTitle());
				intent.setClass(ZhuangBeiList.this, Vid_Play_Activity.class);
				Logger.i( "传值vidurl" + ZhuangBeiList.get(position - 1).getVideo_url());
				startActivity(intent);
			}
		});

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.ll_grade:
				idx = 1;
				icon1.setImageResource(R.mipmap.icon_up);
				showPopupWindow(findViewById(R.id.ll_layout), 1);
				break;
			/*case R.id.iv_back:
				this.finish();
				break;*/

		}
	}

//	private void getExperimentList() {
//		// TODO Auto-generated method stub
//		new Thread() {
//			public void run() {
//				String urlStr = "http://www.shiyan360.cn/index.php/api/article_list";
//				// String category_id = String.valueOf(139);//139为实验资讯列表
//				String paged = String.valueOf(page);
//				String desc_type = String.valueOf(desctype);
//
//				NetworkCheck check = new NetworkCheck(ZhuangBeiList.this);
//				boolean isalivable = check.Network();
//				if (isalivable) {
//					// 封装请求参数
//					Map<String, String> rawParams = new HashMap<String, String>();
//					rawParams.put("desc_type", desc_type);
//					rawParams.put("category_id", wkid);
//					rawParams.put("page", paged);
//					try {
//						mHandler.sendEmptyMessage(3);
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
//							List_ZhuangBeiEntities = JSON.parseArray(array, ZhuangBei.class);
//							Logger.i("size"+List_ZhuangBeiEntities.size() + "");
//							if (!List_ZhuangBeiEntities.isEmpty()) {
//								mHandler.sendEmptyMessage(0);
//
//								ACache mCache = ACache.get(ZhuangBeiList.this);
//								mCache.put("ZhuangBeiList", json);
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
//					NetworkCheckDialog.dialog(ZhuangBeiList.this);
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

					for (int i = 0; i < List_ZhuangBeiEntities.size(); i++) {
						ZhuangBeiListEntity = new ZhuangBei();
						ZhuangBeiListEntity.setId(List_ZhuangBeiEntities.get(i).getId());
						ZhuangBeiListEntity.setVideo_url(List_ZhuangBeiEntities.get(i).getVideo_url());
						ZhuangBeiListEntity.setTitle(List_ZhuangBeiEntities.get(i).getTitle());
						ZhuangBeiListEntity.setImg_url(List_ZhuangBeiEntities.get(i).getImg_url());
						ZhuangBeiList.add(ZhuangBeiListEntity);

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
							Toast.makeText(ZhuangBeiList.this, "获取列表失败,请先进行登录", Toast.LENGTH_SHORT).show();
						}
					});

					break;
				case 2:
//					getExperimentList();
					getZbList();
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
							Toast.makeText(ZhuangBeiList.this, "暂无相关内容！", Toast.LENGTH_SHORT).show();
						}
					});

					break;
				default:
					break;
			}

		}

	};

	/**
	 *
	 * @Title: showPopupWindow
	 * @Description: PopupWindow
	 * @author yimei
	 * @return void 返回类型
	 */
	public void showPopupWindow(View anchor, int flag) {

		View contentView = LayoutInflater.from(ZhuangBeiList.this).inflate(R.layout.windows_popupwindow, null);
		final PopupWindow popupWindow = new PopupWindow(contentView);

		lv1 = (ListView) contentView.findViewById(R.id.lv1);
		lv1_layout = (LinearLayout) contentView.findViewById(R.id.lv1_layout);
		switch (flag) {
			case 1:
				myadapter = new MyAdapter(ZhuangBeiList.this, initArrayData(R.array.sub_zhuangbei));
				break;
		}
		lv1.setAdapter(myadapter);
		lv1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (parent.getAdapter() instanceof MyAdapter) {
					myadapter.setSelectItem(position);
					myadapter.notifyDataSetChanged();
					switch (idx) {
						case 1:
							lv1_layout.getLayoutParams().width = 0; // 年级分类
							// 当没有下级时直接将信息设置textview中
							if (position == 0) {
								wkid = "135";
								ZhuangBeiList.clear();
								MSListview.setAdapter(adapter);
//								getExperimentList();
								getZbList();
								adapter.notifyDataSetChanged();
							} else {
								wkid = PX_Cate_List.get(position - 1).getId();
								Logger.i("ckid"+wkid);
								ZhuangBeiList.clear();
								MSListview.setAdapter(adapter);
//								getExperimentList();
								getZbList();
								adapter.notifyDataSetChanged();
							}
							String name = (String) parent.getAdapter().getItem(position);
							setHeadText(idx, name);
							popupWindow.dismiss();

							break;

					}
				}

			}
		});
		popupWindow.setOnDismissListener(this);
		popupWindow.setWidth(LayoutParams.FILL_PARENT);
		popupWindow.setHeight(LayoutParams.FILL_PARENT);
		ColorDrawable dw = new ColorDrawable(00000);
		popupWindow.setBackgroundDrawable(dw);
		popupWindow.setContentView(contentView);
		contentView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				popupWindow.dismiss();
				return false;
			}
		});
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.showAsDropDown(anchor);

	}

	@Override
	public void onDismiss() {
		// TODO Auto-generated method stub
		icon1.setImageResource(R.mipmap.icon_down);
	}

	private List<String> initArrayData(int id) {
		List<String> list = new ArrayList<String>();
		String[] array = this.getResources().getStringArray(id);
		for (int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}
		return list;
	}

	/**
	 * @Title: setHeadText
	 * @Description: 点击之后设置在上边的TextView里
	 * @author yimei
	 * @return void 返回类型
	 */
	private void setHeadText(int idx, String text) {
		switch (idx) {
			case 1:
				tv_grade.setText(text);
				break;

		}

	}

	public void getwkcateList() {
		new Thread() {
			public void run() {
				String url = "http://www.shiyan360.cn/index.php/api/article_category_sub"; // 二级分类
				try {
					Map<String, String> rawParams = new HashMap<String, String>();
					rawParams.put("id", "135");
					PX_Cate_List = GetSortList.getpxcateList(url, rawParams);

				} catch (Exception e) {
					// TODO: handle exception
				}
			}

		}.start();

	}

	// 缓存中获取数据
//	public void getCacheCollectdata() {
//		ACache mCache = ACache.get(ZhuangBeiList.this);
//		String value = mCache.getAsString("ZhuangBeiList");
//		Logger.json(value);
//		if (value != null) {
//			com.alibaba.fastjson.JSONObject jsonobj = JSON.parseObject(value);
//			com.alibaba.fastjson.JSONArray jsondata = jsonobj.getJSONArray("data");
//			String array = JSON.toJSONString(jsondata);
//			List_ZhuangBeiEntities = JSON.parseArray(array, ZhuangBei.class);
//			mHandler.sendEmptyMessage(0);
//		} else {
//			getExperimentList();
//		}
//
//	}

	public void getZbList(){

		String desc_type = String.valueOf(desctype);
		String category_id =wkid ;
		String paged = String.valueOf(page);
		Logger.i("page" + paged);

		APIWrapper.getInstance().getArticleList(desc_type, category_id, paged)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<HttpResult<List<ZhuangBei>>>() {
					@Override
					public void onCompleted() {
						if (List_ZhuangBeiEntities==null||List_ZhuangBeiEntities.size()<=0){
							mHandler.sendEmptyMessage(4);
							Logger.i("mhander 4");
						}else {
							mHandler.sendEmptyMessage(0);
							Logger.i("mhander 0");
							saveZhuangBeiDB();
						}
					}

					@Override
					public void onError(Throwable e) {
						mHandler.sendEmptyMessage(1);
						Logger.i("mhander 1");
					}

					@Override
					public void onNext(HttpResult<List<ZhuangBei>> listHttpResult) {
						List_ZhuangBeiEntities = listHttpResult.getData();
						for (int i = 0; i < List_ZhuangBeiEntities.size(); i++) {
							ZhuangBei zhuangBei = List_ZhuangBeiEntities.get(i);
							Log.v("ZhuangBeiList", String.valueOf(zhuangBei.getId())+zhuangBei.getTitle()+zhuangBei.getVideo_url());
							if (zhuangBei.getVideo_url() == null || zhuangBei.getVideo_url().length() <= 0) {
								List_ZhuangBeiEntities.remove(i);
							}
							if (zhuangBei.getId() == 1071) {
								List_ZhuangBeiEntities.remove(i);
							}
						}
//						ACache mCache = ACache.get(ChuangKeList.this);
//						mCache.put("ChuangKeList", listHttpResult.getData().toString());
					}
				});
	}

	public void saveZhuangBeiDB() {

		List<ZhuangBei> list = new ArrayList<>();
		for (int i = 0; i < List_ZhuangBeiEntities.size(); i++) {
			ZhuangBei chuangKelist = new ZhuangBei();
			chuangKelist.setId(List_ZhuangBeiEntities.get(i).getId());
			chuangKelist.setTitle(List_ZhuangBeiEntities.get(i).getTitle());
			chuangKelist.setContent(List_ZhuangBeiEntities.get(i).getContent());
			chuangKelist.setImg_url(List_ZhuangBeiEntities.get(i).getImg_url());
			chuangKelist.setImg_url_s(List_ZhuangBeiEntities.get(i).getImg_url_s());
			chuangKelist.setVideo_url(List_ZhuangBeiEntities.get(i).getVideo_url());
			chuangKelist.setPid(List_ZhuangBeiEntities.get(i).getPid());
			list.add(chuangKelist);
		}
		db.saveZhuangBeiLists(list);

	}

	public void readZhuangBeiDB() {
		if (!db.loadAllZhuangBei().isEmpty()) {
			List_ZhuangBeiEntities=db.loadAllZhuangBeiByOrder();
			mHandler.sendEmptyMessage(0);
		}
		else{
			getZbList();
			Logger.i("read fail");
		}

	}
}
