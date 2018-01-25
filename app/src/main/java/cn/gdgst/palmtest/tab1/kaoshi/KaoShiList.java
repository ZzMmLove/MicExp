package cn.gdgst.palmtest.tab1.kaoshi;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import cn.gdgst.entity.KaoShi;
import cn.gdgst.palmtest.R;
import com.orhanobut.logger.Logger;
import cn.gdgst.palmtest.API.APIWrapper;
import cn.gdgst.palmtest.DB.DbService;
import cn.gdgst.palmtest.Entitys.Sub;
import cn.gdgst.palmtest.bean.HttpResult;
import cn.gdgst.palmtest.rewrite.ProgressWheel;
import cn.gdgst.palmtest.servers.GetSortList;
import cn.gdgst.palmtest.service.HistoryService;
import cn.gdgst.palmtest.tab2.Vid_Play_Activity;
import cn.gdgst.palmtest.tab3.MyAdapter;
import cn.gdgst.palmtest.tab3.SubAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.gdgst.palmtest.utils.NetworkCheck;
import cn.gdgst.palmtest.utils.NetworkCheckDialog;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class KaoShiList extends Activity implements OnDismissListener, OnClickListener {
	private PullToRefreshListView MSListview;
	private List<KaoShi> ChuangKeList = new ArrayList<KaoShi>();
	private List<KaoShi> ChuangKeListEntities = new ArrayList<KaoShi>();
	private KaoShi ChuangKeListEntity;
	private SharedPreferences sp;
	private String accessToken;
	private KaoShiAdapter adapter;
	private ImageView iv_back;
	private TextView tv_grade, tv_sorting_latest, tv_loading;
	private LinearLayout ll_grade, ll_sorting_latest, lv1_layout;
	private ImageView icon1, icon3;
	private ListView lv1, lv2;
	private MyAdapter myadapter;
	private int idx;
	private ProgressWheel progress_bar;
	private String cities[][];
	private SubAdapter subAdapter;

	private List<Sub> BK_Cate_List;
	private List<String> highList = new ArrayList<String>();
	private List<String> juniList = new ArrayList<String>();

	private int desctype = 0;
	private String ksid = null; // 专辑ID
	private int page = 1;

	//greendao
	private DbService db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.kaoshilist);
		findview();
		initListView();
		db = DbService.getInstance(this);

		cities = new String[][] { null, this.getResources().getStringArray(R.array.sub_gkshipin),
				this.getResources().getStringArray(R.array.sub_zkshipin) };

		ChuangKeList.clear();
//		getCacheCollectdata();
		readKaoShiDB();
		getckcateList();
		sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		accessToken = sp.getString("accessToken", "");
	}

	private void findview() {
		// TODO Auto-generated method stub
		MSListview = (PullToRefreshListView) findViewById(R.id.exp_display);
		// MSListview.setEmptyView(findViewById(R.id.empty));
		// 文本
		tv_grade = (TextView) findViewById(R.id.tv_grade);
		tv_sorting_latest = (TextView) findViewById(R.id.tv_sorting_latest);
		// 布局
		ll_grade = (LinearLayout) findViewById(R.id.ll_grade);
		ll_sorting_latest = (LinearLayout) findViewById(R.id.ll_sorting_latest);

		ll_grade.setOnClickListener((OnClickListener) this);
		ll_sorting_latest.setOnClickListener((OnClickListener) this);
		// 图片
		icon1 = (ImageView) findViewById(R.id.icon1);
		icon3 = (ImageView) findViewById(R.id.icon3);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);

		progress_bar = (ProgressWheel) findViewById(R.id.progress_bar);
		progress_bar.setBarColor(Color.parseColor("#63c5fe"));
		progress_bar.spin();

		tv_loading = (TextView) findViewById(R.id.tv_loading);
	}

	private void initListView() {
		// TODO Auto-generated method stub

		adapter = new KaoShiAdapter(this, ChuangKeList);
		MSListview.setMode(Mode.BOTH);

		ListView actualListView = MSListview.getRefreshableView();
		MSListview.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				page = 1;
				ChuangKeList.clear();
//				getExperimentList();
				getKsList();
				getckcateList();
				adapter.notifyDataSetChanged();

			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub

				if (ChuangKeListEntities.size() < 20) {
					// TODO Auto-generated method stub
					MSListview.postDelayed(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							adapter.notifyDataSetChanged();
							MSListview.onRefreshComplete();
							Toast.makeText(KaoShiList.this, "没有更多数据了", Toast.LENGTH_SHORT).show();
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
					getKsList();
					adapter.notifyDataSetChanged();
				}
			}
		});
		actualListView.setAdapter(adapter);
		MSListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				// 添加浏览记录
				final String videoid = ChuangKeList.get(position - 1).getId().toString();
				String model = "kaoshi";
				try {
					HistoryService historyService = new HistoryService(KaoShiList.this);
					historyService.addHistory(accessToken, videoid, model);
				} catch (Exception e) {
					e.printStackTrace();
				}

				String url = ChuangKeList.get(position - 1).getVideo_url();
				Intent myIntent3 = new Intent();
				myIntent3.putExtra("video_path", url);
				myIntent3.putExtra("video_name", ChuangKeList.get(position - 1).getName());
				myIntent3.setClass(KaoShiList.this, Vid_Play_Activity.class);
				startActivity(myIntent3);
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
				case R.id.ll_sorting_latest:
					idx = 3;
					icon3.setImageResource(R.mipmap.icon_up);
					if (check.Network()){

						showPopupWindow(findViewById(R.id.ll_layout), 3);
					}else NetworkCheckDialog.dialog(this);
					break;
				case R.id.iv_back:
					this.finish();
					break;
			}
	}

//	private void getExperimentList() {
//		// TODO Auto-generated method stub
//
//		new Thread() {
//			public void run() {
//				String urlStr = "http://www.shiyan360.cn/index.php/api/kaoshi_list";
//				String desc_type = String.valueOf(desctype);
//				String category_id = ksid;
//				String paged = String.valueOf(page);
//
//				NetworkCheck check = new NetworkCheck(KaoShiList.this);
//				boolean isalivable = check.Network();
//				if (isalivable) {
//					// 封装请求参数
//					Map<String, String> rawParams = new HashMap<String, String>();
//					rawParams.put("desc_type", desc_type);
//					rawParams.put("category_id", category_id);
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
//							ChuangKeListEntities = JSON.parseArray(array, KaoShi.class);
//
//							if (!ChuangKeListEntities.isEmpty()) {
//								mHandler.sendEmptyMessage(0);
//								ACache mCache = ACache.get(KaoShiList.this);
//								mCache.put("KaoShiList", json);
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
//					NetworkCheckDialog.dialog(KaoShiList.this);
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

					for (int i = 0; i < ChuangKeListEntities.size(); i++) {
						ChuangKeListEntity = new KaoShi();
						ChuangKeListEntity.setId(ChuangKeListEntities.get(i).getId());
						ChuangKeListEntity.setImg_url(ChuangKeListEntities.get(i).getImg_url().trim());
						ChuangKeListEntity.setName(ChuangKeListEntities.get(i).getName());
						ChuangKeListEntity.setVideo_url(ChuangKeListEntities.get(i).getVideo_url().trim());
						ChuangKeList.add(ChuangKeListEntity);

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
							Toast.makeText(KaoShiList.this, "获取列表失败,请先进行登录", Toast.LENGTH_SHORT).show();
						}
					});

					break;
				case 2:
//					getExperimentList();
					getKsList();
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
							Toast.makeText(KaoShiList.this, "暂无相关内容！", Toast.LENGTH_SHORT).show();
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

		View contentView = LayoutInflater.from(KaoShiList.this).inflate(R.layout.windows_popupwindow, null);
		final PopupWindow popupWindow = new PopupWindow(contentView);

		lv1 = (ListView) contentView.findViewById(R.id.lv1);
		lv2 = (ListView) contentView.findViewById(R.id.lv2);
		lv1_layout = (LinearLayout) contentView.findViewById(R.id.lv1_layout);
		switch (flag) {
			case 1:
				myadapter = new MyAdapter(KaoShiList.this, initArrayData(R.array.sub_kaoshi));
				break;
			case 3:
				myadapter = new MyAdapter(KaoShiList.this, initArrayData(R.array.sub_sorting_latest));
				break;
		}
		lv1.setAdapter(myadapter);
		lv1.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (parent.getAdapter() instanceof MyAdapter) {
					myadapter.setSelectItem(position);
					myadapter.notifyDataSetChanged();
					lv2.setVisibility(View.INVISIBLE);
					if (lv2.getVisibility() == View.INVISIBLE) {
						lv2.setVisibility(View.VISIBLE);
						switch (idx) {
							case 1:
								lv1_layout.getLayoutParams().width = 0; // 全部分类 高中物理
								switch (position) {
									case 0:
										ksid = "0";
										ChuangKeList.clear();
										MSListview.setAdapter(adapter);
//										getExperimentList();
										getKsList();
										adapter.notifyDataSetChanged();

										subAdapter = null;
										// 当没有下级时直接将信息设置textview中
										String name2 = (String) parent.getAdapter().getItem(position);
										setHeadText(idx, name2);
										popupWindow.dismiss();
										break;
									case 1:
										subAdapter = new SubAdapter(getApplicationContext(), cities[position]);
										lv2.setAdapter(subAdapter);
										subAdapter.notifyDataSetChanged();
										lv2.setOnItemClickListener(new OnItemClickListener() {

											@Override
											public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
												if (position == 0) {
													ksid = "408";
													ChuangKeList.clear();
													MSListview.setAdapter(adapter);
//													getExperimentList();
													getKsList();
													adapter.notifyDataSetChanged();
												} else {
													ksid = highList.get(position - 1);
													ChuangKeList.clear();
													MSListview.setAdapter(adapter);
//													getExperimentList();
													getKsList();
													adapter.notifyDataSetChanged();
												}

												String name = (String) parent.getAdapter().getItem(position);
												setHeadText(idx, name);
												popupWindow.dismiss();
												subAdapter = null;
											}
										});
										break;
									case 2:
										subAdapter = new SubAdapter(getApplicationContext(), cities[position]);
										lv2.setAdapter(subAdapter);
										subAdapter.notifyDataSetChanged();
										lv2.setOnItemClickListener(new OnItemClickListener() {

											@Override
											public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
												if (position == 0) {
													ksid = "407";
													ChuangKeList.clear();
													MSListview.setAdapter(adapter);
//													getExperimentList();
													getKsList();
													adapter.notifyDataSetChanged();
												} else {
													ksid = juniList.get(position - 1);
													Logger.i("juniList"+ksid);
													ChuangKeList.clear();
													MSListview.setAdapter(adapter);
//													getExperimentList();
													getKsList();
													adapter.notifyDataSetChanged();
												}

												String name = (String) parent.getAdapter().getItem(position);
												setHeadText(idx, name);
												popupWindow.dismiss();
												subAdapter = null;
											}
										});
										break;
								}
								break;

							case 3:
								lv1_layout.getLayoutParams().width = LayoutParams.MATCH_PARENT; // 顺序
								// 按最新
								switch (position) {
									case 0:
										desctype = 0;
										ChuangKeList.clear();
										MSListview.setAdapter(adapter);
//										getExperimentList();
										getKsList();
										adapter.notifyDataSetChanged();
										break;
									case 1:
										desctype = 2; // 按访问记录 2
										ChuangKeList.clear();
										MSListview.setAdapter(adapter);
//										getExperimentList();
										getKsList();
										adapter.notifyDataSetChanged();

										break;
									case 2:
										desctype = 3; // 按最多评论 3
										ChuangKeList.clear();
										MSListview.setAdapter(adapter);
//										getExperimentList();
										getKsList();
										adapter.notifyDataSetChanged();
										break;

								}
								subAdapter = null;
								// 当没有下级时直接将信息设置textview中
								String name3 = (String) parent.getAdapter().getItem(position);
								setHeadText(idx, name3);
								popupWindow.dismiss();
								break;
						}

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
		icon3.setImageResource(R.mipmap.icon_down);
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
			case 3:
				tv_sorting_latest.setText(text);
				break;
		}

	}

	public void getckcateList() {
		new Thread() {
			public void run() {
				String url = "http://www.shiyan360.cn/index.php/api/kaoshi_category_list";
				try {
					Map<String, String> rawParams = new HashMap<String, String>();
					BK_Cate_List = GetSortList.getSubList(url, rawParams);
					for (int i = 0; i < 3; i++) {
						highList.add(BK_Cate_List.get(i).getId());
					}
					for (int i = 3; i < 6; i++) {
						juniList.add(BK_Cate_List.get(i).getId());
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

		}.start();

	}

	// 缓存中获取数据
//	public void getCacheCollectdata() {
//		ACache mCache = ACache.get(KaoShiList.this);
//		String value = mCache.getAsString("KaoShiList");
//		Logger.json(value);
//		if (value != null) {
//			com.alibaba.fastjson.JSONObject jsonobj = JSON.parseObject(value);
//			com.alibaba.fastjson.JSONArray jsondata = jsonobj.getJSONArray("data");
//			String array = JSON.toJSONString(jsondata);
//			ChuangKeListEntities = JSON.parseArray(array, KaoShi.class);
//			mHandler.sendEmptyMessage(0);
//		} else {
//			getExperimentList();
//
//		}
//	}
	public void getKsList(){
		String desc_type = String.valueOf(desctype);
		String category_id = ksid;
		String paged = String.valueOf(page);
		Logger.i("page" + paged);

		APIWrapper.getInstance().getKaoShiList(desc_type, category_id, paged)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<HttpResult<List<KaoShi>>>() {
					@Override
					public void onCompleted() {
						if (ChuangKeListEntities==null||ChuangKeListEntities.size()<=0){
							mHandler.sendEmptyMessage(4);
							Logger.i("mhander 4");
						}else {
							mHandler.sendEmptyMessage(0);
							Logger.i("mhander 0");
							saveKaoShiDB();
						}
					}

					@Override
					public void onError(Throwable e) {

						mHandler.sendEmptyMessage(1);
						Logger.i("mhander 1");



					}

					@Override
					public void onNext(HttpResult<List<KaoShi>> listHttpResult) {

						ChuangKeListEntities = listHttpResult.getData();
						Logger.i("ididid:"+listHttpResult.getData().get(0).getId());

//						ACache mCache = ACache.get(ChuangKeList.this);
//						mCache.put("ChuangKeList", listHttpResult.getData().toString());




					}
				});
	}

	public void saveKaoShiDB() {

		List<KaoShi> list = new ArrayList<>();
		for (int i = 0; i < ChuangKeListEntities.size(); i++) {
			KaoShi chuangKelist = new KaoShi();
			chuangKelist.setId(ChuangKeListEntities.get(i).getId());
			chuangKelist.setName(ChuangKeListEntities.get(i).getName());
			chuangKelist.setImg_url(ChuangKeListEntities.get(i).getImg_url());
			chuangKelist.setImg_url_s(ChuangKeListEntities.get(i).getImg_url_s());
			chuangKelist.setVideo_url(ChuangKeListEntities.get(i).getVideo_url());
			chuangKelist.setCateid(ChuangKeListEntities.get(i).getCateid());
			list.add(chuangKelist);
		}
		db.saveKaoShiLists(list);

	}

	public void readKaoShiDB() {
		if (!db.loadAllKaoShi().isEmpty()) {
			ChuangKeListEntities=db.loadAllKaoShiByOrder();
			mHandler.sendEmptyMessage(0);

		}
		else{
			getKsList();
		}

	}
}
