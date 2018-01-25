package cn.gdgst.palmtest.tab1.wenku;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import cn.gdgst.entity.WenKu;
import cn.gdgst.palmtest.R;
import com.orhanobut.logger.Logger;
import cn.gdgst.palmtest.API.APIWrapper;
import cn.gdgst.palmtest.DB.DbService;
import cn.gdgst.palmtest.Entitys.Wk_Cate_Entity;
import cn.gdgst.palmtest.bean.HttpResult;
import cn.gdgst.palmtest.rewrite.ProgressWheel;
import cn.gdgst.palmtest.servers.GetSortList;
import cn.gdgst.palmtest.tab3.MyAdapter;

import org.afinal.simplecache.ACache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.gdgst.palmtest.utils.NetworkCheck;
import cn.gdgst.palmtest.utils.NetworkCheckDialog;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class WenKuList extends Activity implements OnDismissListener, OnClickListener {
	private PullToRefreshListView MSListview;
	private List<WenKu> WenKuList = new ArrayList<WenKu>();
	private List<WenKu> WenKuListEntities = new ArrayList<WenKu>();
	private WenKu WenKuListEntity;
	private SharedPreferences sp;
	private WenKuAdapter adapter;
	private ProgressWheel progress_bar;
	private TextView tv_grade, tv_loading;
	private LinearLayout ll_grade, lv1_layout;
	private ImageView icon1, iv_back;
	private ListView lv1;
	private MyAdapter myadapter;
	private int idx;
	private List<Wk_Cate_Entity> WK_Cate_List;

	private int desctype = 0;
	private String wkid = null; // 专辑ID
	private int page = 1;
	private Boolean isRefreshing = false;

	//greendao
	private DbService db;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wenku_list);
		findview();
		initListView();
		db = DbService.getInstance(this);
		WenKuList.clear();
//		getCacheCollectdata();
		readWenKuDB();  //先从缓存，数据库中取数据
		getwkcateList();

	}

	private void findview() {
		// TODO Auto-generated method stub
		MSListview = (PullToRefreshListView) findViewById(R.id.exp_display);
		// MSListview.setEmptyView(findViewById(R.id.empty));
		// 文本
		tv_grade = (TextView) findViewById(R.id.tv_grade);
		// 布局
		ll_grade = (LinearLayout) findViewById(R.id.ll_grade);

		ll_grade.setOnClickListener((OnClickListener) this);
		// 图片
		icon1 = (ImageView) findViewById(R.id.icon1);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		progress_bar = (ProgressWheel) findViewById(R.id.progress_bar);
		progress_bar.setBarColor(Color.parseColor("#63c5fe"));
		progress_bar.spin();
		tv_loading = (TextView) findViewById(R.id.tv_loading);
	}

	private void initListView() {
		// TODO Auto-generated method stub
		adapter = new WenKuAdapter(this, WenKuList);

		MSListview.setMode(Mode.BOTH);// 使列表可以同时下拉和上拉刷新
		ListView actualListView = MSListview.getRefreshableView();

		MSListview.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override // 下拉监听
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				page = 1;
				WenKuList.clear();
//				getExperimentList();
				getWkList();
				getwkcateList();
				adapter.notifyDataSetChanged();

			}

			@Override // 上拉监听
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {

				if (WenKuListEntities.size() < 20) {
					// TODO Auto-generated method stub
					MSListview.postDelayed(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							adapter.notifyDataSetChanged();
							MSListview.onRefreshComplete();
							Toast.makeText(WenKuList.this, "没有更多数据了", Toast.LENGTH_SHORT).show();
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
					getWkList();
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
				intent.putExtra("id", WenKuList.get(position - 1).getId());
				intent.putExtra("tv_title", "文库详情");
				Logger.i("传值id" + WenKuList.get(position - 1).getId());
				intent.setClass(WenKuList.this, WenKuDetail.class);
				startActivity(intent);
			}
		});

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		NetworkCheck check = new NetworkCheck(this);

			switch (v.getId()) {
				case R.id.ll_grade:
					idx = 1;
					icon1.setImageResource(R.mipmap.icon_up);
					if (check.Network()) {

						showPopupWindow(findViewById(R.id.ll_layout), 1);
					}else NetworkCheckDialog.dialog(this);
					break;
				case R.id.iv_back:
					this.finish();
					break;
			}
	}

//	private void getExperimentList() {
//		// TODO Auto-generated method stub
//		new Thread() {
//			public void run() {
//				String urlStr = "http://www.shiyan360.cn/index.php/api/wenku_list";
//				String desc_type = String.valueOf(desctype);
//				String category_id = wkid;
//				NetworkCheck check = new NetworkCheck(WenKuList.this);
//				boolean isalivable = check.Network();
//				if (isalivable) {
//					// 封装请求参数
//					Map<String, String> rawParams = new HashMap<String, String>();
//					rawParams.put("desc_type", desc_type);
//					rawParams.put("category_id", category_id);
//					String paged = String.valueOf(page);
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
//							WenKuListEntities = JSON.parseArray(array, WenKu.class);
//							Logger.i(WenKuListEntities.size() + "");
//							if (!WenKuListEntities.isEmpty()) {
//								mHandler.sendEmptyMessage(0);
//								ACache mCache = ACache.get(WenKuList.this);
//								mCache.put("WenKuList", json);
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
//					NetworkCheckDialog.dialog(WenKuList.this);
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

					for (int i = 0; i < WenKuListEntities.size(); i++) {
						WenKuListEntity = new WenKu();
						WenKuListEntity.setImg_url(WenKuListEntities.get(i).getImg_url().trim());
						WenKuListEntity.setTitle(WenKuListEntities.get(i).getTitle());
						WenKuListEntity.setId(WenKuListEntities.get(i).getId());
						WenKuListEntity.setTime(WenKuListEntities.get(i).getTime());
						WenKuList.add(WenKuListEntity);

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
							Toast.makeText(WenKuList.this, "获取列表失败,请先进行登录", Toast.LENGTH_SHORT).show();
						}
					});

					break;
				case 2:
//					getExperimentList();
					getWkList();
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
							Toast.makeText(WenKuList.this, "暂无相关内容！", Toast.LENGTH_SHORT).show();
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

		View contentView = LayoutInflater.from(WenKuList.this).inflate(R.layout.windows_popupwindow, null);
		final PopupWindow popupWindow = new PopupWindow(contentView);

		lv1 = (ListView) contentView.findViewById(R.id.lv1);
		lv1_layout = (LinearLayout) contentView.findViewById(R.id.lv1_layout);
		switch (flag) {
			case 1:
				myadapter = new MyAdapter(WenKuList.this, initArrayData(R.array.sub_wenku));
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
								wkid = null;
								WenKuList.clear();
								MSListview.setAdapter(adapter);
//								getExperimentList();
								getWkList();
								adapter.notifyDataSetChanged();
							} else {
								wkid = WK_Cate_List.get(position - 1).getId();
								Logger.i("ckid"+ wkid+WK_Cate_List.get(position-1).getName());
								WenKuList.clear();
								MSListview.setAdapter(adapter);
//								getExperimentList();
								getWkList();
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
				String url = "http://www.shiyan360.cn/index.php/api/wenku_category_list"; // 年级分类
				try {
					Map<String, String> rawParams = new HashMap<String, String>();
					WK_Cate_List = GetSortList.getwkcateList(url, rawParams);

				} catch (Exception e) {
					// TODO: handle exception
				}
			}

		}.start();

	}

	// 缓存中获取数据
	public void getCacheCollectdata() {
		ACache mCache = ACache.get(WenKuList.this);
		String value = mCache.getAsString("WenKuList");
		Logger.json(value);
		if (value != null) {
			com.alibaba.fastjson.JSONObject jsonobj = JSON.parseObject(value);
			com.alibaba.fastjson.JSONArray jsondata = jsonobj.getJSONArray("data");
			String array = JSON.toJSONString(jsondata);
			WenKuListEntities = JSON.parseArray(array, WenKu.class);
			mHandler.sendEmptyMessage(0);
		} else {
//			getExperimentList();
			getWkList();
		}

	}
     public void getWkList(){
		 String desc_type = String.valueOf(desctype);
		 String category_id = wkid;
		 String paged = String.valueOf(page);
		 Logger.i("page" + paged);

		 APIWrapper.getInstance().getWenKuList(desc_type, category_id, paged)
				 .subscribeOn(Schedulers.io())
				 .observeOn(AndroidSchedulers.mainThread())
				 .subscribe(new Subscriber<HttpResult<List<WenKu>>>() {
					 @Override
					 public void onCompleted() {
						 if (WenKuListEntities==null||WenKuListEntities.size()<=0){
							 mHandler.sendEmptyMessage(4);
							 Logger.i("mhander 4");
						 }else {
							 mHandler.sendEmptyMessage(0);
							 Logger.i("mhander 0");
							 saveWenKuDB();
						 }
					 }

					 @Override
					 public void onError(Throwable e) {

						 mHandler.sendEmptyMessage(1);
						 Logger.i("mhander 1");



					 }

					 @Override
					 public void onNext(HttpResult<List<WenKu>> listHttpResult) {

						 WenKuListEntities = listHttpResult.getData();
//						ACache mCache = ACache.get(ChuangKeList.this);
//						mCache.put("ChuangKeList", listHttpResult.getData().toString());




					 }
				 });
	 }

	public void saveWenKuDB() {

		List<WenKu> list = new ArrayList<>();
		for (int i = 0; i < WenKuListEntities.size(); i++) {
			WenKu wenKulist = new WenKu();
			wenKulist.setId(WenKuListEntities.get(i).getId());
			wenKulist.setTitle(WenKuListEntities.get(i).getTitle());
			wenKulist.setImg_url(WenKuListEntities.get(i).getImg_url());
			wenKulist.setImg_url_s(WenKuListEntities.get(i).getImg_url_s());
			wenKulist.setFile_url(WenKuListEntities.get(i).getFile_url());
			wenKulist.setCateid(WenKuListEntities.get(i).getCateid());
			wenKulist.setTime(WenKuListEntities.get(i).getTime());
			list.add(wenKulist);
		}
		db.saveWenKuLists(list);

	}

	public void readWenKuDB() {
		if (!db.loadAllWenKu().isEmpty()) {
			WenKuListEntities=db.loadAllWenKuByOrder();
			mHandler.sendEmptyMessage(0);
		}
		else{
			getWkList();
		}
	}


}
