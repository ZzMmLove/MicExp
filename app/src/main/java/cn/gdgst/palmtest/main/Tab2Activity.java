package cn.gdgst.palmtest.main;

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
import android.view.ViewGroup.LayoutParams;
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
import com.orhanobut.logger.Logger;

import org.afinal.simplecache.ACache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.gdgst.entity.Video;
import cn.gdgst.palmtest.API.APIWrapper;
import cn.gdgst.palmtest.DB.DbService;
import cn.gdgst.palmtest.Entitys.GradeEntity;
import cn.gdgst.palmtest.Entitys.Sub;
import cn.gdgst.palmtest.R;
import cn.gdgst.palmtest.bean.HttpResult;
import cn.gdgst.palmtest.rewrite.ProgressWheel;
import cn.gdgst.palmtest.servers.GetSortList;
import cn.gdgst.palmtest.service.HistoryService;
import cn.gdgst.palmtest.tab2.Vid_Play_Activity;
import cn.gdgst.palmtest.tab2.Video_List_Adapter;
import cn.gdgst.palmtest.tab3.MyAdapter;
import cn.gdgst.palmtest.tab3.SubAdapter;
import cn.gdgst.palmtest.utils.NetworkCheck;
import cn.gdgst.palmtest.utils.NetworkCheckDialog;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 这是底部4个导航栏中的第二个导航栏页面
 */
public class Tab2Activity extends Activity implements OnDismissListener, OnClickListener {
	/**下拉刷新的ListView*/
	private PullToRefreshListView pullToRefreshListView;
	/** 存放视频的*/
	private List<Video> Video_List = new ArrayList<Video>();
	private List<Video> video_List_Entities = new ArrayList<Video>();
	private Video video_List_Entity;
	private SharedPreferences sp;
	/***/
	private String accessToken;
	private Video_List_Adapter video_List_Adapter;

	private TextView tv_grade, tv_category, tv_sorting_latest, tv_loading;
	private LinearLayout ll_grade, ll_category, ll_sorting_latest, lv1_layout;
	private ImageView icon1, icon2, icon3;
	private ListView lv1, lv2;
	private MyAdapter myadapter;
	private int idx;
	private String cities[][];
	private SubAdapter subAdapter;
	View view;
	Context context;
	/**从服务器获取视频所需要的参数*/
	private int desctype = 0;
	/**年级ID*/
	private String gradeid = "0";
	/**分类ID*/
	private String categoryid = "0";
	/**页数*/
	private int page = 1;
	/**视频进度条*/
	private ProgressWheel progress_bar;
	/**视频分类的参数*/
	private List<GradeEntity> GradeList;
	/**视频排序*/
	private List<Sub> Subsort;
	//高中
	private List<String> highList = new ArrayList<String>();
	//初中
	private List<String> juniList = new ArrayList<String>();
	//小学
	private List<String> primList = new ArrayList<String>();

	//greendao
	private DbService dbService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tab2);
		dbService = DbService.getInstance(this);
		findview();
		initListView();
		cities = new String[][] { null, this.getResources().getStringArray(R.array.sub_highcate),
				this.getResources().getStringArray(R.array.sub_junicate),
				this.getResources().getStringArray(R.array.sub_primcate) };

		video_List_Adapter.notifyDataSetChanged();

		sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		accessToken = sp.getString("accessToken", "");

		sp=getSharedPreferences("addInfo", Context.MODE_PRIVATE);

	}

	private void findview() {
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.video_display);
		// VideoListview.setEmptyView(view.findViewById(R.id.empty));
		// 文本
		tv_grade = (TextView) findViewById(R.id.tv_grade);
		tv_category = (TextView) findViewById(R.id.tv_category);
		tv_sorting_latest = (TextView) findViewById(R.id.tv_sorting_latest);
		// 布局
		ll_grade = (LinearLayout) findViewById(R.id.ll_grade);
		ll_category = (LinearLayout) findViewById(R.id.ll_category);
		ll_sorting_latest = (LinearLayout) findViewById(R.id.ll_sorting_latest);

		ll_grade.setOnClickListener(this);
		ll_category.setOnClickListener(this);
		ll_sorting_latest.setOnClickListener(this);
		// 图片
		icon1 = (ImageView) findViewById(R.id.icon1);
		icon2 = (ImageView) findViewById(R.id.icon2);
		icon3 = (ImageView) findViewById(R.id.icon3);
		progress_bar = (ProgressWheel) findViewById(R.id.progress_bar);
		progress_bar.setBarColor(Color.parseColor("#63c5fe"));
		//让进度条转起来
		progress_bar.spin();
		tv_loading = (TextView) findViewById(R.id.tv_loading);
	}

	private void initListView() {
		// TODO Auto-generated method stub
		video_List_Adapter = new Video_List_Adapter(Tab2Activity.this, Video_List);
		ListView actualListView = pullToRefreshListView.getRefreshableView();
		pullToRefreshListView.setMode(Mode.BOTH); // 两端刷新

		// Set a listener to be invoked when the list should be refreshed.
		pullToRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				page = 1;
				Video_List.clear();
//				getAlbumList();
				getVideo();
				getSubList();
				getGraList();
				video_List_Adapter.notifyDataSetChanged();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {

				if (video_List_Entities.size() < 20) {
					pullToRefreshListView.postDelayed(new Runnable() {
						@Override
						public void run() {
							video_List_Adapter.notifyDataSetChanged();
							pullToRefreshListView.onRefreshComplete();
							Toast.makeText(Tab2Activity.this, "没有更多数据了", Toast.LENGTH_SHORT).show();
						}
					}, 500);

				} else {
					pullToRefreshListView.getLoadingLayoutProxy(false, true).setLastUpdatedLabel("");
					pullToRefreshListView.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载");
					pullToRefreshListView.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
					pullToRefreshListView.getLoadingLayoutProxy(false, true).setReleaseLabel("放开以加载");
					page = page + 1;
					Logger.i("page" + page);
//					getAlbumList();
					getVideo();
					video_List_Adapter.notifyDataSetChanged();
				}
			}
		});
		actualListView.setAdapter(video_List_Adapter);
		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// 添加浏览记录
				final String videoid = Video_List.get(position - 1).getId().toString();
				Logger.i("videoid:"+ Video_List.get(position - 1).getId());
				Logger.i("videoid gid:"+ Video_List.get(position - 1).getGid());
				String model = "play";
				try {
					HistoryService historyService = new HistoryService(Tab2Activity.this);
					historyService.addHistory(accessToken, videoid, model);
				} catch (Exception e) {
					e.printStackTrace();
				}

				String url = Video_List.get(position - 1).getVideo_url();
				Intent myIntent3 = new Intent();
				myIntent3.putExtra("video_path", url);
				myIntent3.putExtra("video_name", Video_List.get(position - 1).getName());
				myIntent3.setClass(Tab2Activity.this, Vid_Play_Activity.class);
				Tab2Activity.this.startActivity(myIntent3);

			}
		});

//		getCacheCollectdata();
		/**
		 * 进入此视频Activity时,首先从本地数据库中获取视频对象列表,如果本地数据库中获取不到,则从服务器中获取
		 */
		readVideoDB();
		video_List_Adapter.notifyDataSetChanged();
		getSubList();
		getGraList();

	}

	@Override
	public void onClick(View v) {
		NetworkCheck check = new NetworkCheck(this);
		if (check.Network()) {
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
		}else NetworkCheckDialog.dialog(this);
	}

	/**
	 * 分类条件选择弹出框
	 * @param anchor
	 * @param flag
     */
	private void showPopupWindow(View anchor, int flag) {
		View contentView = LayoutInflater.from(Tab2Activity.this).inflate(R.layout.windows_popupwindow, null);
		final PopupWindow popupWindow = new PopupWindow(contentView);

		lv1 = (ListView) contentView.findViewById(R.id.lv1);
		lv2 = (ListView) contentView.findViewById(R.id.lv2);
		lv1_layout = (LinearLayout) contentView.findViewById(R.id.lv1_layout);
		switch (flag) {
			case 1:
				myadapter = new MyAdapter(Tab2Activity.this, initArrayData(R.array.sub_grade2));
				break;
			case 2:
				myadapter = new MyAdapter(Tab2Activity.this, initArrayData(R.array.sub_category));
				break;
			case 3:
				myadapter = new MyAdapter(Tab2Activity.this, initArrayData(R.array.sub_sorting_latest));
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
									//不限
									gradeid = "0";
									//清空数据，下同
									Video_List.clear();
									pullToRefreshListView.setAdapter(video_List_Adapter);
//									getAlbumList();
									getVideo();
									//刷新数据
									video_List_Adapter.notifyDataSetChanged();
								} else {
									//获得年级学科分类id，这里是表示物理或者化学或者生物或者通用技术
									gradeid = GradeList.get(position - 1).getId();
									Logger.i( "gradeid:" +gradeid );
									Logger.i("dianjigraid:"+ gradeid );
									Video_List.clear();
									pullToRefreshListView.setAdapter(video_List_Adapter);
//									getAlbumList();
									getVideo();
									video_List_Adapter.notifyDataSetChanged();
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
										//不限的分类项
										categoryid = "0";
										Video_List.clear();
										pullToRefreshListView.setAdapter(video_List_Adapter);
//										getAlbumList();
										getVideo();
										video_List_Adapter.notifyDataSetChanged();

										subAdapter = null;
										// 当没有下级时直接将信息设置textview中
										String name2 = (String) parent.getAdapter().getItem(position);
										setHeadText(idx, name2);
										popupWindow.dismiss();
										break;
									case 1:
										subAdapter = new SubAdapter(Tab2Activity.this.getApplicationContext(),
												cities[position]);
										lv2.setAdapter(subAdapter);
										subAdapter.notifyDataSetChanged();
										lv2.setOnItemClickListener(new OnItemClickListener() {

											@Override
											public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
												if (position == 0) {
													//高中
													categoryid = "368";
													Video_List.clear();
													pullToRefreshListView.setAdapter(video_List_Adapter);
//													getAlbumList();
													getVideo();
													video_List_Adapter.notifyDataSetChanged();
												} else {
													categoryid = highList.get(position - 1);
													Video_List.clear();
													pullToRefreshListView.setAdapter(video_List_Adapter);
//													getAlbumList();
													getVideo();
													video_List_Adapter.notifyDataSetChanged();
												}

												String name = (String) parent.getAdapter().getItem(position);
												setHeadText(idx, name);
												popupWindow.dismiss();
												subAdapter = null;
											}
										});
										break;
									case 2:
										subAdapter = new SubAdapter(Tab2Activity.this.getApplicationContext(),
												cities[position]);
										lv2.setAdapter(subAdapter);
										subAdapter.notifyDataSetChanged();
										lv2.setOnItemClickListener(new OnItemClickListener() {

											@Override
											public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
												if (position == 0) {
													//初中
													categoryid = "369";
													Video_List.clear();
													pullToRefreshListView.setAdapter(video_List_Adapter);
//													getAlbumList();
													getVideo();
													video_List_Adapter.notifyDataSetChanged();
												} else {
													categoryid = juniList.get(position - 1);
													Video_List.clear();
													pullToRefreshListView.setAdapter(video_List_Adapter);
//													getAlbumList();
													getVideo();
													video_List_Adapter.notifyDataSetChanged();
												}

												String name = (String) parent.getAdapter().getItem(position);
												setHeadText(idx, name);
												popupWindow.dismiss();
												subAdapter = null;
											}
										});
										break;
									case 3:
										subAdapter = new SubAdapter(Tab2Activity.this.getApplicationContext(),
												cities[position]);
										lv2.setAdapter(subAdapter);
										subAdapter.notifyDataSetChanged();
										lv2.setOnItemClickListener(new OnItemClickListener() {

											@Override
											public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
												if (position == 0) {
													//小学
													categoryid = "370";
													Video_List.clear();
													pullToRefreshListView.setAdapter(video_List_Adapter);
//													getAlbumList();
													getVideo();
													video_List_Adapter.notifyDataSetChanged();
												} else {
													categoryid = primList.get(position - 1);
													Video_List.clear();
													pullToRefreshListView.setAdapter(video_List_Adapter);
//													getAlbumList();
													getVideo();
													video_List_Adapter.notifyDataSetChanged();
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
										Video_List.clear();
										pullToRefreshListView.setAdapter(video_List_Adapter);
//										getAlbumList();
										getVideo();

										video_List_Adapter.notifyDataSetChanged();
										break;
									case 1:
										desctype = 2; // 按访问记录 2
										Video_List.clear();
										pullToRefreshListView.setAdapter(video_List_Adapter);
//										getAlbumList();
										getVideo();
										video_List_Adapter.notifyDataSetChanged();

										break;
									case 2:
										desctype = 3; // 按最多评论 3
										Video_List.clear();
										pullToRefreshListView.setAdapter(video_List_Adapter);
//										getAlbumList();
										getVideo();
										video_List_Adapter.notifyDataSetChanged();
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
		// popupWindow.setBackgroundDrawable(new PaintDrawable());
		// popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setOutsideTouchable(true);
		popupWindow.showAsDropDown(anchor);
	}

	/**
	 * 获取专辑列表的网络线程
	 */
//	private void getAlbumList() {
//		// TODO Auto-generated method stub
//		new Thread() {
//			public void run() {
//				// 获取视频专辑列表URL
//				String urlStr = "http://www.shiyan360.cn/index.php/api/video_list";
//				String paged = String.valueOf(page);
//				String desc_type = String.valueOf(desctype);
//				String category_id = categoryid;
//				NetworkCheck check = new NetworkCheck(Tab2Activity.this);
//				boolean isalivable = check.Network();
//				if (isalivable) {
//					// 封装请求参数
//					Map<String, String> rawParams = new HashMap<String, String>();
//					rawParams.put("page", paged);
//					rawParams.put("gradeid", gradeid);
//					rawParams.put("desc_type", desc_type);
//					rawParams.put("category_id", category_id);
//
//					try {
//						mHandler.sendEmptyMessage(3);
//						Thread.sleep(1300);
//						// 设置请求参数项 发送请求返回json
//						String json = HttpUtil.postRequest(urlStr, rawParams);
//						Logger.json(json);
//
//						// 解析json数据
//						com.alibaba.fastjson.JSONObject jsonobj = JSON.parseObject(json);
//						Boolean response = (Boolean) jsonobj.get("success");
//
//						// 判断是否请求成功
//						if (response) {
//							// 解析截取“data”中的内容
//							com.alibaba.fastjson.JSONArray jsondata = jsonobj.getJSONArray("data");
//							String array = JSON.toJSONString(jsondata);
//							video_List_Entities = JSON.parseArray(array, Video.class);
//							if (!video_List_Entities.isEmpty()) {
//								mHandler.sendEmptyMessage(0);
//								ACache mCache = ACache.get(Tab2Activity.this);
//								mCache.put("VideoList", json);
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
//					NetworkCheckDialog.dialog(Tab2Activity.this);
//				}
//
//			}
//		}.start();
//	}

	/**
	 * 通过handle接收数据并更新界面
	 */
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0:
					for (int i = 0; i < video_List_Entities.size(); i++) {
						video_List_Entity = new Video();
						video_List_Entity.setId(video_List_Entities.get(i).getId());
						video_List_Entity.setGradeid(video_List_Entities.get(i).getGradeid());
						video_List_Entity.setImg_url(video_List_Entities.get(i).getImg_url().trim());
						video_List_Entity.setName(video_List_Entities.get(i).getName());
						video_List_Entity.setVideo_url(video_List_Entities.get(i).getVideo_url());
						Video_List.add(video_List_Entity);
					}
					pullToRefreshListView.post(new Runnable() {
						@Override
						public void run() {
							progress_bar.stopSpinning();
							tv_loading.setVisibility(View.GONE);
							video_List_Adapter.notifyDataSetChanged();
							pullToRefreshListView.onRefreshComplete();
							// Toast.makeText(Tab2Activity.this, "获取列表成功",
							// Toast.LENGTH_SHORT).show();
						}
					});
					break;
				case 1:
					progress_bar.stopSpinning();
					tv_loading.setVisibility(View.GONE);
					video_List_Adapter.notifyDataSetChanged();
					pullToRefreshListView.onRefreshComplete();
					pullToRefreshListView.setEmptyView(findViewById(R.id.empty));
					Toast.makeText(Tab2Activity.this, "获取列表失败,请先进行登录", Toast.LENGTH_SHORT).show();
					break;
				case 2:
//					getAlbumList();
					getVideo();
					break;
				case 3:
					tv_loading.setVisibility(View.VISIBLE);
					progress_bar.spin();
					break;
				case 4:
					pullToRefreshListView.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							progress_bar.stopSpinning();
							tv_loading.setVisibility(View.GONE);
							video_List_Adapter.notifyDataSetChanged();
							pullToRefreshListView.onRefreshComplete();
							pullToRefreshListView.setEmptyView(findViewById(R.id.empty));
							Toast.makeText(Tab2Activity.this, "暂无相关内容！", Toast.LENGTH_SHORT).show();
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

	/**
	 * 从资源文件中获取分类选项的数组
	 * @param id 在values文件中的arrays的列表ID名
	 * @return
     */
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
	 * @return void 空返回值
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
				String url = "http://www.shiyan360.cn/index.php/api/category_list";// 二级分类
				// 封装请求参数
				try {
					Map<String, String> rawParams = new HashMap<String, String>();
					Subsort = GetSortList.getSubList(url, rawParams);

					for (int i = 0; i < 5; i++) {
						highList.add(Subsort.get(i).getId());
					}
					for (int i = 5; i < 9; i++) {
						juniList.add(Subsort.get(i).getId());
					}
					for (int i = 9; i < 11; i++) {
						primList.add(Subsort.get(i).getId());
					}
				} catch (Exception e) {
					// TODO: handle exception
				}

			}
		}.start();
	}

	public void getGraList() {
		new Thread() {
			public void run() {
				String url = "http://www.shiyan360.cn/index.php/api/grade_list"; // 年级分类
				try {
					Map<String, String> rawParams = new HashMap<String, String>();
					GradeList = GetSortList.getGradelist(url, rawParams);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

		}.start();

	}

	// 缓存中获取数据
	public void getCacheCollectdata() {
		ACache mCache = ACache.get(Tab2Activity.this);
		String value = mCache.getAsString("VideoList");
		Logger.json(value);
		//如果缓存中有数据就从缓存中拿取数据
		if (value != null) {
			com.alibaba.fastjson.JSONObject jsonobj = JSON.parseObject(value);
			com.alibaba.fastjson.JSONArray jsondata = jsonobj.getJSONArray("data");
			String array = JSON.toJSONString(jsondata);
			video_List_Entities = JSON.parseArray(array, Video.class);
			mHandler.sendEmptyMessage(0);
		} else {
//			getAlbumList();
			//若是没有就开启网络访问加载视频
			getVideo();
		}

	}

	/**
	 * 执行从服务器中获取视频列表
	 */
	public void getVideo(){
		//排序类型
		String desc_type = String.valueOf(desctype);
		//高中，初中，小学的分类类型
		String category_id = categoryid;
		//年级学科分类id，这里是表示物理或者化学或者生物或者通用技术
		String grade_id = gradeid;
		Logger.i(" getvideo grade_id:" + grade_id);
		Logger.i("categoryid"+categoryid);
		//页码、页数
		String paged = String.valueOf(page);
		Logger.i("page" + paged);
		APIWrapper.getInstance().getVideoList(desc_type, category_id,grade_id, paged)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<HttpResult<List<Video>>>() {
					@Override
					public void onCompleted() {
						//没有视频数据
						if (video_List_Entities==null||video_List_Entities.size()<=0){
							//发送消息到主线程让主线程做相应的处理
							mHandler.sendEmptyMessage(4);
							Logger.i("mhander 4");
						}else {
							//否则有数据也发送消息到主线程让主线程更新视图
							mHandler.sendEmptyMessage(0);
							Logger.i("mhander 0");
							//把数据保存到数据库缓存中
							saveVideoDB();
						}
					}

					@Override
					public void onError(Throwable e) {
						//mHandler.sendEmptyMessage(1);
						//访问出错就从数据库中读取视频，为了提高用户的体验性
						readVideoDB();
						Logger.i("mhander 1");
					}

					@Override
					public void onNext(HttpResult<List<Video>> listHttpResult) {
						video_List_Entities = listHttpResult.getData();
//						ACache mCache = ACache.get(ChuangKeList.this);
//						mCache.put("ChuangKeList", listHttpResult.getData().toString());
					}
				});
	}

	/**
	 * 把视频数据保存到数据库中
	 */
	public void saveVideoDB() {
		List<Video> list = new ArrayList<>();
		for (int i = 0; i < video_List_Entities.size(); i++) {
			Video chuangKelist = new Video();
			chuangKelist.setId(video_List_Entities.get(i).getId());
			chuangKelist.setName(video_List_Entities.get(i).getName());
			chuangKelist.setImg_url(video_List_Entities.get(i).getImg_url());
			chuangKelist.setImg_url_s(video_List_Entities.get(i).getImg_url_s());
			chuangKelist.setVideo_url(video_List_Entities.get(i).getVideo_url());
			chuangKelist.setCateid(video_List_Entities.get(i).getCateid());
			list.add(chuangKelist);
		}
		dbService.saveVideoLists(list);

	}

	/**
	 * 从数据库中读取视频数据，没有就从网路中加载
	 */
	public void readVideoDB() {
		if (!dbService.loadAllVideo().isEmpty()) {
			video_List_Entities=dbService.loadAllVideoyOrder();
			Logger.i("dbService id:"+video_List_Entities.get(0).getId());
			Logger.i("dbService gid:"+video_List_Entities.get(0).getGid());
			mHandler.sendEmptyMessage(0);
		}else{
			getVideo();
		}
	}

}
