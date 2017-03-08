package cn.gdgst.palmtest.main;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Environment;
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
import cn.gdgst.entity.Experiment;
import cn.gdgst.palmtest.R;
import com.orhanobut.logger.Logger;
import cn.gdgst.palmtest.API.APIWrapper;
import cn.gdgst.palmtest.DB.DbService;
import cn.gdgst.palmtest.Entitys.GradeEntity;
import cn.gdgst.palmtest.Entitys.Sub;
import cn.gdgst.palmtest.bean.HttpResult;
import cn.gdgst.palmtest.recorder.ScreenRecorder;
import cn.gdgst.palmtest.rewrite.ProgressWheel;
import cn.gdgst.palmtest.servers.GetSortList;

import org.afinal.simplecache.ACache;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.gdgst.palmtest.tab3.ExperimentAdapter;
import cn.gdgst.palmtest.tab3.MyAdapter;
import cn.gdgst.palmtest.tab3.SubAdapter;
import cn.gdgst.palmtest.tab3.SimulationPlayActivity;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class Tab3Activity extends Activity implements OnDismissListener, OnClickListener {
	private PullToRefreshListView ExpListview;
	private List<Experiment> ExperimentList = new ArrayList<Experiment>();
	private List<Experiment> experiment_ListEntities = new ArrayList<Experiment>();
	private Experiment experiment_ListEntity;
	private SharedPreferences sp;
	private ExperimentAdapter adapter;

	private TextView tv_grade, tv_category, tv_sorting_latest, tv_loading;
	private LinearLayout ll_grade, ll_category, ll_sorting_latest, lv1_layout;
	private ImageView icon1, icon2, icon3;
	private ListView lv1, lv2;
	private MyAdapter myadapter;
	private int idx;
	private String cities[][];
	private SubAdapter subAdapter;

	private int desctype = 0;
	private String gradeid = "0";
	private String categoryid = "0";
	private int page = 1;
	private ProgressWheel progress_bar;
	private List<GradeEntity> GradeList;
	private List<Sub> Subsort;
	private List<String> highList = new ArrayList<String>();
	private List<String> juniList = new ArrayList<String>();
	private List<String> primList = new ArrayList<String>();

	//greendao
	private DbService db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab3);
		db = DbService.getInstance(this);

		findview();
		initListView();
		cities = new String[][] { null, this.getResources().getStringArray(R.array.sub_exphighcate),
				this.getResources().getStringArray(R.array.sub_junicate),
				this.getResources().getStringArray(R.array.sub_primcate) };

		ExperimentList.clear();
		//getCacheCollectdata();
		readExpDB();
		// adapter.notifyDataSetChanged();
		getSubList();
		getGraList();
	}

	private void findview() {
		ExpListview = (PullToRefreshListView) findViewById(R.id.exp_display);
		// ExpListview.setEmptyView(findViewById(R.id.empty));
		// 文本
		tv_grade = (TextView) findViewById(R.id.tv_grade);
		tv_category = (TextView) findViewById(R.id.tv_category);
		tv_sorting_latest = (TextView) findViewById(R.id.tv_sorting_latest);
		// 布局
		ll_grade = (LinearLayout) findViewById(R.id.ll_grade);
		ll_category = (LinearLayout) findViewById(R.id.ll_category);
		ll_sorting_latest = (LinearLayout) findViewById(R.id.ll_sorting_latest);

		ll_grade.setOnClickListener((OnClickListener) this);
		ll_category.setOnClickListener((OnClickListener) this);
		ll_sorting_latest.setOnClickListener((OnClickListener) this);
		// 图片
		icon1 = (ImageView) findViewById(R.id.icon1);
		icon2 = (ImageView) findViewById(R.id.icon2);
		icon3 = (ImageView) findViewById(R.id.icon3);
		progress_bar = (ProgressWheel) findViewById(R.id.progress_bar);
		progress_bar.setBarColor(Color.parseColor("#63c5fe"));
		progress_bar.spin();
		tv_loading = (TextView) findViewById(R.id.tv_loading);
	}

	private void initListView() {
		// View headerView =LinearLayout.inflate(this, R.id., null);
		adapter = new ExperimentAdapter(this, ExperimentList);
		ExpListview.setMode(Mode.BOTH); // 两端刷新

		ListView actualListView = ExpListview.getRefreshableView();
		// Set a listener to be invoked when the list should be refreshed.
		ExpListview.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				page = 1;
				ExperimentList.clear();
				//getExperimentList();
				getExpList();
				getSubList();
				getGraList();
				adapter.notifyDataSetChanged();

			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				if (experiment_ListEntities.size() < 20) {
					ExpListview.postDelayed(new Runnable() {
						@Override
						public void run() {
							adapter.notifyDataSetChanged();
							ExpListview.onRefreshComplete();
							Toast.makeText(Tab3Activity.this, "没有更多数据了", Toast.LENGTH_SHORT).show();
						}
					}, 500);

				} else {
					ExpListview.getLoadingLayoutProxy(false, true).setLastUpdatedLabel("");
					ExpListview.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载");
					ExpListview.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
					ExpListview.getLoadingLayoutProxy(false, true).setReleaseLabel("放开以加载");
					page = page + 1;
					Logger.i( "page" + page);
//					getExperimentList();
					getExpList();
					adapter.notifyDataSetChanged();
				}
			}
		});
		actualListView.setAdapter(adapter);
		ExpListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String url = ExperimentList.get(position - 1).getHtml5_url();
				Intent myIntent3 = new Intent();
				myIntent3.putExtra("url", url);
				myIntent3.setClass(Tab3Activity.this, SimulationPlayActivity.class);
				startActivity(myIntent3);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ll_grade:
				idx = 1;
				icon1.setImageResource(R.mipmap.icon_up);
				showPopupWindow(findViewById(R.id.ll_layout), 1);
				break;
			case R.id.ll_category:
				idx = 2;
				icon2.setImageResource(R.mipmap.icon_up);
				showPopupWindow(findViewById(R.id.ll_layout), 2);
				break;
			case R.id.ll_sorting_latest:
				idx = 3;
				icon3.setImageResource(R.mipmap.icon_up);
				showPopupWindow(findViewById(R.id.ll_layout), 3);
				break;

		}

	}

	/**
	 *
	 * @Title: showPopupWindow
	 * @Description: PopupWindow
	 * @author yimei
	 * @return void 返回类型
	 */
	public void showPopupWindow(View anchor, int flag) {

		View contentView = LayoutInflater.from(Tab3Activity.this).inflate(R.layout.windows_popupwindow, null);
		final PopupWindow popupWindow = new PopupWindow(contentView);

		lv1 = (ListView) contentView.findViewById(R.id.lv1);
		lv2 = (ListView) contentView.findViewById(R.id.lv2);
		lv1_layout = (LinearLayout) contentView.findViewById(R.id.lv1_layout);
		switch (flag) {
			case 1:
				myadapter = new MyAdapter(Tab3Activity.this, initArrayData(R.array.sub_grade2));
				break;
			case 2:
				myadapter = new MyAdapter(Tab3Activity.this, initArrayData(R.array.sub_category));
				break;
			case 3:
				myadapter = new MyAdapter(Tab3Activity.this, initArrayData(R.array.sub_sorting_latest));
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
								lv1_layout.getLayoutParams().width = 0; // 年级分类
								if (position == 0) {
									gradeid = "0";
									ExperimentList.clear();
									ExpListview.setAdapter(adapter);
//									getExperimentList();
									getExpList();
									adapter.notifyDataSetChanged();
								} else {
									gradeid = GradeList.get(position - 1).getId();
									Logger.i( ":" + gradeid);
									ExperimentList.clear();
									ExpListview.setAdapter(adapter);
//									getExperimentList();
									getExpList();
									adapter.notifyDataSetChanged();
								}
								subAdapter = null;
								// 当没有下级时直接将信息设置textview中
								String name = (String) parent.getAdapter().getItem(position);
								setHeadText(idx, name);
								popupWindow.dismiss();

								break;
							case 2:
								lv1_layout.getLayoutParams().width = 0; // 全部分类 高中物理
								switch (position) {
									case 0:
										categoryid = "0";
										ExperimentList.clear();
										ExpListview.setAdapter(adapter);
//										getExperimentList();
										getExpList();
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
													categoryid = "368";// 高中
													ExperimentList.clear();
													ExpListview.setAdapter(adapter);
//													getExperimentList();
													getExpList();
													adapter.notifyDataSetChanged();
												} else {
													categoryid = highList.get(position - 1);
													ExperimentList.clear();
													ExpListview.setAdapter(adapter);
//													getExperimentList();
													getExpList();
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
													categoryid = "369";// 初中
													ExperimentList.clear();
													ExpListview.setAdapter(adapter);
//													getExperimentList();
													getExpList();
													adapter.notifyDataSetChanged();
												} else {
													categoryid = juniList.get(position - 1);
													ExperimentList.clear();
													ExpListview.setAdapter(adapter);
//													getExperimentList();
													getExpList();
													adapter.notifyDataSetChanged();
												}

												String name = (String) parent.getAdapter().getItem(position);
												setHeadText(idx, name);
												popupWindow.dismiss();
												subAdapter = null;
											}
										});
										break;
									case 3:
										subAdapter = new SubAdapter(getApplicationContext(), cities[position]);
										lv2.setAdapter(subAdapter);
										subAdapter.notifyDataSetChanged();
										lv2.setOnItemClickListener(new OnItemClickListener() {

											@Override
											public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
												if (position == 0) {
													categoryid = "370";// 小学
													ExperimentList.clear();
													ExpListview.setAdapter(adapter);
//													getExperimentList();
													getExpList();
													adapter.notifyDataSetChanged();
												} else {
													categoryid = primList.get(position - 1);
													ExperimentList.clear();
													ExpListview.setAdapter(adapter);
//													getExperimentList();
													getExpList();
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
										ExperimentList.clear();
										ExpListview.setAdapter(adapter);
//										getExperimentList();
										getExpList();
										adapter.notifyDataSetChanged();
										break;
									case 1:
										desctype = 2; // 按访问记录 2
										ExperimentList.clear();
										ExpListview.setAdapter(adapter);
//										getExperimentList();
										getExpList();
										adapter.notifyDataSetChanged();

										break;
									case 2:
										desctype = 3; // 按最多评论 3
										ExperimentList.clear();
										ExpListview.setAdapter(adapter);
//										getExperimentList();
										getExpList();
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
				popupWindow.dismiss();
				return false;
			}
		});
		popupWindow.setFocusable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setOutsideTouchable(true);
		popupWindow.showAsDropDown(anchor);

	}

//	private void getExperimentList() {
//		new Thread() {
//			public void run() {
//				String urlStr = "http://www.shiyan360.cn/index.php/api/experiment_list";
//				String desc_type = String.valueOf(desctype);
//				String grade_id = gradeid;
//				String category_id = categoryid;
//				String paged = String.valueOf(page);
//				NetworkCheck check = new NetworkCheck(Tab3Activity.this);
//				boolean isalivable = check.Network();
//				if (isalivable) {
//					// 封装请求参数
//					Map<String, String> rawParams = new HashMap<String, String>();
//					rawParams.put("desc_type", desc_type);
//					rawParams.put("gradeid", grade_id);
//					rawParams.put("category_id", category_id);
//					rawParams.put("page", paged);
//					try {
//						mHandler.sendEmptyMessage(3);
//						Thread.sleep(1200);
//						// 设置请求参数项 发送请求返回json
//						String json = HttpUtil.postRequest(urlStr, rawParams);
//						Logger.json(json);
//						// 解析json数据
//						com.alibaba.fastjson.JSONObject jsonobj = JSON.parseObject(json);
//						Boolean response = (Boolean) jsonobj.get("success");
//						// 判断是否请求成功
//						if (response) {
//							// 解析截取“data”中的内容
//							com.alibaba.fastjson.JSONArray jsondata = jsonobj.getJSONArray("data");
//							String array = JSON.toJSONString(jsondata);
//							experiment_ListEntities = JSON.parseArray(array, Experiment.class);
//							if (!experiment_ListEntities.isEmpty()) {
//								mHandler.sendEmptyMessage(0);
//								ACache mCache = ACache.get(Tab3Activity.this);
//								mCache.put("Tab3Activity", json);
//							}
//						} else {
//							mHandler.sendEmptyMessage(1);
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//						mHandler.sendEmptyMessage(4);
//					}
//
//				} else {
//					NetworkCheckDialog.dialog(Tab3Activity.this);
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

					for (int i = 0; i < experiment_ListEntities.size(); i++) {
						experiment_ListEntity = new Experiment();
						experiment_ListEntity.setImg_url(experiment_ListEntities.get(i).getImg_url().trim());
						experiment_ListEntity.setName(experiment_ListEntities.get(i).getName());
						experiment_ListEntity.setHtml5_url(experiment_ListEntities.get(i).getHtml5_url().trim());
						ExperimentList.add(experiment_ListEntity);

					}
					ExpListview.post(new Runnable() {

						@Override
						public void run() {
							progress_bar.stopSpinning();
							tv_loading.setVisibility(View.GONE);
							adapter.notifyDataSetChanged();
							ExpListview.onRefreshComplete();
							// Toast.makeText(Tab3Activity.this, "获取列表成功",
							// Toast.LENGTH_SHORT).show();
						}
					});

					break;
				case 1:
					ExpListview.post(new Runnable() {

						@Override
						public void run() {
							progress_bar.stopSpinning();
							tv_loading.setVisibility(View.GONE);
							adapter.notifyDataSetChanged();
							ExpListview.onRefreshComplete();
							ExpListview.setEmptyView(findViewById(R.id.empty));
							Toast.makeText(Tab3Activity.this, "获取列表失败,请先进行登录", Toast.LENGTH_SHORT).show();
						}
					});

					break;
				case 2:
//					getExperimentList();
					getExpList();
					break;
				case 3:
					tv_loading.setVisibility(View.VISIBLE);
					progress_bar.spin();
					break;
				case 4:
					ExpListview.post(new Runnable() {

						@Override
						public void run() {
							progress_bar.stopSpinning();
							tv_loading.setVisibility(View.GONE);
							adapter.notifyDataSetChanged();
							ExpListview.onRefreshComplete();
							ExpListview.setEmptyView(findViewById(R.id.empty));
							Toast.makeText(Tab3Activity.this, "暂无相关内容！", Toast.LENGTH_SHORT).show();
						}
					});

					break;
				default:
					break;
			}

		}

	};

	@Override
	public void onDismiss() {
		icon1.setImageResource(R.mipmap.icon_down);
		icon2.setImageResource(R.mipmap.icon_down);
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
			case 2:
				tv_category.setText(text);
				break;
			case 3:
				tv_sorting_latest.setText(text);
				break;
		}

	}

	private void getSubList() {
		new Thread() {
			public void run() {
				String url = "http://www.shiyan360.cn/index.php/api/experiment_category_list";// 二级分类
				// 封装请求参数
				try {
					Map<String, String> rawParams = new HashMap<String, String>();
					Subsort = GetSortList.getSubList(url, rawParams);

					for (int i = 0; i < 4; i++) {
						highList.add(Subsort.get(i).getId());
					}
					for (int i = 4; i < 8; i++) {
						juniList.add(Subsort.get(i).getId());
					}
					for (int i = 8; i < 10; i++) {
						primList.add(Subsort.get(i).getId());
					}

				} catch (Exception e) {
				}

			}
		}.start();
	}

	public void getGraList() {
		new Thread() {
			public void run() {
				String url = "http://www.shiyan360.cn/index.php/api/experiment_grade_list"; // 年级分类
				try {
					Map<String, String> rawParams = new HashMap<String, String>();
					GradeList = GetSortList.getGradelist(url, rawParams);

				} catch (Exception e) {
				}
			}

		}.start();

	}

	// 缓存中获取数据
	public void getCacheCollectdata() {
		ACache mCache = ACache.get(Tab3Activity.this);
		String value = mCache.getAsString("Tab3Activity");
		Logger.json(value);
		if (value != null) {
			com.alibaba.fastjson.JSONObject jsonobj = JSON.parseObject(value);
			com.alibaba.fastjson.JSONArray jsondata = jsonobj.getJSONArray("data");
			String array = JSON.toJSONString(jsondata);
			experiment_ListEntities = JSON.parseArray(array, Experiment.class);
			mHandler.sendEmptyMessage(0);
		} else {
//			getExperimentList();
		}
	}


	public void getExpList() {

		String desc_type = String.valueOf(desctype);
		String category_id = categoryid;
		String grade_id = gradeid;
		String paged = String.valueOf(page);
		Logger.i("categoryid"+categoryid);
		APIWrapper.getInstance().geExperimentList(desc_type, category_id,grade_id, paged)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<HttpResult<List<Experiment>>>() {
					@Override
					public void onCompleted() {
						if (experiment_ListEntities == null || experiment_ListEntities.size() <= 0) {
							mHandler.sendEmptyMessage(4);
							Logger.i("mhander 4");
						} else {
							mHandler.sendEmptyMessage(0);
							saveExpDB();
						}
					}

					@Override
					public void onError(Throwable e) {

						mHandler.sendEmptyMessage(1);
						Logger.i("mhander 1");
					}

					@Override
					public void onNext(HttpResult<List<Experiment>> listHttpResult) {
						experiment_ListEntities = listHttpResult.getData();
//						ACache mCache = ACache.get(ChuangKeList.this);
//						mCache.put("ChuangKeList", listHttpResult.getData().toString());
					}
				});
	}

	public void saveExpDB() {

		List<Experiment> list = new ArrayList<>();
		for (int i = 0; i < experiment_ListEntities.size(); i++) {
			Experiment experiment = new Experiment();
			experiment.setId(experiment_ListEntities.get(i).getId());
			experiment.setName(experiment_ListEntities.get(i).getName());
			experiment.setImg_url(experiment_ListEntities.get(i).getImg_url());
			experiment.setImg_url_s(experiment_ListEntities.get(i).getImg_url_s());
			experiment.setHtml5_url(experiment_ListEntities.get(i).getHtml5_url());
			experiment.setCateid(experiment_ListEntities.get(i).getCateid());
			list.add(experiment);
		}
		db.saveExpLists(list);

	}

	public void readExpDB() {
		if (!db.loadAllChuangKe().isEmpty()) {
			experiment_ListEntities = db.loadAllExpOrder();
			mHandler.sendEmptyMessage(0);
		} else {
			getExpList();
		}
	}
}
