package cn.gdgst.palmtest.tab1.examsystem;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
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
import android.view.ViewGroup;
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

import cn.gdgst.entity.ExamPaper;
import cn.gdgst.entity.PeiXun;
import cn.gdgst.palmtest.R;
import com.orhanobut.logger.Logger;

import cn.gdgst.palmtest.API.APIWrapper;
import cn.gdgst.palmtest.DB.DbService;
import cn.gdgst.palmtest.Entitys.PX_Cate_Entity;
import cn.gdgst.palmtest.base.AppConstant;
import cn.gdgst.palmtest.bean.HttpResult;
import cn.gdgst.palmtest.rewrite.ProgressWheel;
import cn.gdgst.palmtest.servers.GetSortList;
import cn.gdgst.palmtest.tab3.MyAdapter;

import org.afinal.simplecache.ACache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.gdgst.palmtest.tab3.SubAdapter;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ExamListActivity extends AppCompatActivity implements OnDismissListener, OnClickListener {
	private PullToRefreshListView pullToRefreshListView;
	private List<PeiXun> WenKuList = new ArrayList<PeiXun>();
	private List<PeiXun> WenKuListEntities = new ArrayList<PeiXun>();
	private PeiXun WenKuListEntity;
	private SharedPreferences sp;
	private PeiXunAdapter adapter;
	private ProgressWheel progress_bar;
	private TextView tv_grade, tv_loading;
	/**
	 * 这个LinearLayout是当被点击时弹出PopupWindow
	 */
	private LinearLayout ll_grade;
	private LinearLayout lv1_layout;
	private ImageView icon1;
	/**
	 * 这个是PopupWindow里面用到的ListView，用于展示一级分类的
	 */
	private ListView lv1;
	/**
	 * 这个是PopupWindow里面用到的LinstView,用于展示二级分类的
	 */
	private ListView lv2;
	private MyAdapter myadapter;
	private int idx;
	private List<PX_Cate_Entity> PX_Cate_List;

	private int desctype = 0;
	private String wkid = "139"; // 专辑ID
	private int page = 1;
	private Boolean isRefreshing = false;
	private TextView tv_title;

	//greendao
	private DbService db;

	/**
	 * 用于保存从网络获取下来的试卷列表
	 */
	private List<ExamPaper> examPaperList = new ArrayList<>();

	private ListView actualListView;

	private SubAdapter subAdapter;

	/**
	 * 初中和高中的下一级科目分类
	 */
	private String[][] subject;
	PeiXunAdapter peiXunAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getExaminPaperList(0,0);
		setContentView(R.layout.activity_exam_list);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("考评系统");
		subject = new String[][] {null,this.getResources().getStringArray(R.array.exam_paper_junior_subject),this.getResources().getStringArray(R.array.exam_paper_height_subject)};
		findview();
		initListView();
		db = DbService.getInstance(this);
		WenKuList.clear();
//		getCacheCollectdata();
		//readPeiXunDB();
		//getwkcateList();
	}

	private void findview() {
		pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.exp_display);
		// pullToRefreshListView.setEmptyView(findViewById(R.id.empty));
		// 文本
		tv_grade = (TextView) findViewById(R.id.tv_grade);
		// 布局
		ll_grade = (LinearLayout) findViewById(R.id.ll_grade);
		ll_grade.setOnClickListener(this);
		// 图片
		icon1 = (ImageView) findViewById(R.id.icon1);
		progress_bar = (ProgressWheel) findViewById(R.id.progress_bar);
		progress_bar.setBarColor(Color.parseColor("#63c5fe"));
		progress_bar.spin();
		tv_loading = (TextView) findViewById(R.id.tv_loading);
	}

	private void initListView() {
		adapter = new PeiXunAdapter(this, examPaperList);
		pullToRefreshListView.setMode(Mode.BOTH);// 使列表可以同时下拉和上拉刷新
		actualListView = pullToRefreshListView.getRefreshableView();
		actualListView.setAdapter(adapter);
		actualListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			}
		});
		pullToRefreshListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override // 下拉监听
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				page = 1;
				//WenKuList.clear();
//				getExperimentList();
				//getPxList();
				//getwkcateList();
				if (examPaperList != null) {
					examPaperList.clear();
				}
				getExaminPaperList(0,0);
				Log.v("jenfee's", "测试执行从网络获取试卷列表");
				adapter.notifyDataSetChanged();
			}

			@Override // 上拉监听
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {

				if (WenKuList.size() < 20) {
					Log.v("showmark","测试空指针异常");
					pullToRefreshListView.postDelayed(new Runnable() {
						@Override
						public void run() {
							adapter.notifyDataSetChanged();
							pullToRefreshListView.onRefreshComplete();
							Toast.makeText(ExamListActivity.this, "没有更多数据了", Toast.LENGTH_SHORT).show();
						}
					}, 500);

				} else {
					pullToRefreshListView.getLoadingLayoutProxy(false, true).setLastUpdatedLabel("");
					pullToRefreshListView.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载");
					pullToRefreshListView.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
					pullToRefreshListView.getLoadingLayoutProxy(false, true).setReleaseLabel("放开以加载");
					page = page + 1;
					Logger.i("page" + page);
//					getExperimentList();
					getPxList();
					adapter.notifyDataSetChanged();
				}

			}
		});

		pullToRefreshListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent();
				intent.putExtra("exampaperItemId", examPaperList.get(position - 1).getId());
				intent.putExtra("exampaperItemTitle", examPaperList.get(position - 1).getPaper());
				intent.setClass(ExamListActivity.this, ExamPaperDetailActivity.class);
				startActivity(intent);
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ll_grade:
				idx = 1;
				icon1.setImageResource(R.mipmap.icon_up);
				showPopupWindow2(findViewById(R.id.ll_layout), 1);
				break;
		}
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0:
					for (int i = 0; i < WenKuListEntities.size(); i++) {
						WenKuListEntity = new PeiXun();
						WenKuListEntity.setVideo_url(WenKuListEntities.get(i).getVideo_url());
						WenKuListEntity.setTitle(WenKuListEntities.get(i).getTitle());
						WenKuList.add(WenKuListEntity);
					}
					pullToRefreshListView.post(new Runnable() {
						@Override
						public void run() {
							progress_bar.stopSpinning();
							tv_loading.setVisibility(View.GONE);
							adapter.notifyDataSetChanged();
							pullToRefreshListView.onRefreshComplete();
							// Toast.makeText(Tab3Activity.this, "获取列表成功",
							// Toast.LENGTH_SHORT).show();
						}
					});
					break;
				case 1:
					pullToRefreshListView.post(new Runnable() {

						@Override
						public void run() {
							progress_bar.stopSpinning();
							tv_loading.setVisibility(View.GONE);
							adapter.notifyDataSetChanged();
							pullToRefreshListView.onRefreshComplete();
							pullToRefreshListView.setEmptyView(findViewById(R.id.empty));
							Toast.makeText(ExamListActivity.this, "获取列表失败,请先进行登录", Toast.LENGTH_SHORT).show();
						}
					});
					break;
				case 2:
					getPxList();
					break;
				case 3:
					progress_bar.spin();
					progress_bar.setVisibility(View.VISIBLE);
					break;
				case 4:
					pullToRefreshListView.post(new Runnable() {
						@Override
						public void run() {
							progress_bar.stopSpinning();
							tv_loading.setVisibility(View.GONE);
							adapter.notifyDataSetChanged();
							pullToRefreshListView.onRefreshComplete();
							pullToRefreshListView.setEmptyView(findViewById(R.id.empty));
							Toast.makeText(ExamListActivity.this, "暂无相关内容！", Toast.LENGTH_SHORT).show();
						}
					});
					break;
				case 5:
					pullToRefreshListView.post(new Runnable() {
						@Override
						public void run() {
							progress_bar.stopSpinning();
							tv_loading.setVisibility(View.GONE);
							adapter.notifyDataSetChanged();
							pullToRefreshListView.onRefreshComplete();
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
		View contentView = LayoutInflater.from(ExamListActivity.this).inflate(R.layout.windows_popupwindow, null);
		final PopupWindow popupWindow = new PopupWindow(contentView);
		lv1 = (ListView) contentView.findViewById(R.id.lv1);
		lv2 = (ListView) contentView.findViewById(R.id.lv2);
		lv1_layout = (LinearLayout) contentView.findViewById(R.id.lv1_layout);
		switch (flag) {
			case 1:
				myadapter = new MyAdapter(ExamListActivity.this, initArrayData(R.array.sub_peixun));
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
							lv1_layout.getLayoutParams().width = 0;
							switch (position) {
								case 0://case = 0时，也就是点击了不限,什么事情也不做
									Toast.makeText(ExamListActivity.this.getApplicationContext(),"你点击了不限",Toast.LENGTH_SHORT).show();
									Log.v("jenfee's", "你点击了不限");
									break;
								case 1://case = 1时，也就是点击了初中，继续创建子适配器，显示下级分类
									subAdapter = new SubAdapter(ExamListActivity.this.getApplicationContext(), subject[position]);
									lv2.setAdapter(subAdapter);
									subAdapter.notifyDataSetChanged();
									Log.v("jenfee's", "你点击了初中");
									break;

							}
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

	/**
	 * 从网络获取数据
	 */
	public void getwkcateList() {
		new Thread() {
			public void run() {
				String url = "http://www.shiyan360.cn/index.php/api/article_category_sub"; // 二级分类
				try {
					Map<String, String> rawParams = new HashMap<String, String>();
					rawParams.put("id", "139");
					PX_Cate_List = GetSortList.getpxcateList(url, rawParams);
				} catch (Exception e) {
				}
			}

		}.start();

	}

	// 缓存中获取数据
	public void getCacheCollectdata() {
		ACache mCache = ACache.get(ExamListActivity.this);
		String value = mCache.getAsString("ExamListActivity");
		Logger.json(value);
		if (value != null) {
			com.alibaba.fastjson.JSONObject jsonobj = JSON.parseObject(value);
			com.alibaba.fastjson.JSONArray jsondata = jsonobj.getJSONArray("data");
			String array = JSON.toJSONString(jsondata);
			WenKuListEntities = JSON.parseArray(array, PeiXun.class);
			mHandler.sendEmptyMessage(0);
		} else {
//			getExperimentList();
			getPxList();
		}

	}

	/**
	 * 从网络获取培训文章列表
	 */
	public void getPxList(){
		String desc_type = String.valueOf(desctype);
		String category_id = wkid;
		String paged = String.valueOf(page);
		Logger.i("page" + paged);

		APIWrapper.getInstance().getArticleListpx(desc_type, category_id, paged)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<HttpResult<List<PeiXun>>>() {
					@Override
					public void onCompleted() {
						if (WenKuListEntities==null||WenKuListEntities.size()<=0){
							mHandler.sendEmptyMessage(4);
							Logger.i("mhander 4");
						}else {
							mHandler.sendEmptyMessage(0);
							Logger.i("mhander 0");
							savePeiXunDB();
						}
					}

					@Override
					public void onError(Throwable e) {

						mHandler.sendEmptyMessage(1);
						Logger.i("mhander 1");
					}

					@Override
					public void onNext(HttpResult<List<PeiXun>> listHttpResult) {

						WenKuListEntities = listHttpResult.getData();
//						ACache mCache = ACache.get(ChuangKeList.this);
//						mCache.put("ChuangKeList", listHttpResult.getData().toString());

					}
				});
	}

	public void savePeiXunDB() {
		List<PeiXun> list = new ArrayList<>();
		for (int i = 0; i < WenKuListEntities.size(); i++) {
			PeiXun chuangKelist = new PeiXun();
			chuangKelist.setId(WenKuListEntities.get(i).getId());
			chuangKelist.setTitle(WenKuListEntities.get(i).getTitle());
			chuangKelist.setImg_url(WenKuListEntities.get(i).getImg_url());
			chuangKelist.setImg_url_s(WenKuListEntities.get(i).getImg_url_s());
			chuangKelist.setVideo_url(WenKuListEntities.get(i).getVideo_url());
			chuangKelist.setPid(WenKuListEntities.get(i).getPid());
			list.add(chuangKelist);
		}
		db.savePeiXunLists(list);
	}

	public void readPeiXunDB() {
		if (!db.loadAllPeiXun().isEmpty()) {
			WenKuListEntities=db.loadAllPeiXunByOrder();
			mHandler.sendEmptyMessage(0);
		}
		else{
			getPxList();
		}
	}

	/**
	 * 执行从网络服务器中获取试卷列表
	 * 并把试卷列表保存在examPaperList中,(wrote by Jenfee)
	 * page 下拉刷新的页数
	 */
	private void getExaminPaperList(int page, int cid) {
		/**
		 * 按分类来筛选(高中物理:371,高中化学:373,高中生物:375,通用技术:406,
		 *              初中物理:386,初中化学:387,初中生物:388,初中科学:404,信息技术:393)
		 */
		APIWrapper.getInstance().examinPaperList(page, cid)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<HttpResult<List<ExamPaper>>>() {
					@Override
					public void onCompleted() {
						if (examPaperList == null || examPaperList.size() <= 0) {
							mHandler.sendEmptyMessage(4);
						}else {
							mHandler.sendEmptyMessage(5);
						}
					}
					@Override
					public void onError(Throwable e) {

					}
					@Override
					public void onNext(HttpResult<List<ExamPaper>> listHttpResult) {
						examPaperList = listHttpResult.getData();
						peiXunAdapter = new PeiXunAdapter(ExamListActivity.this, examPaperList);
						actualListView.setAdapter(peiXunAdapter);
					}
				});
	}

	private void showPopupWindow2(View anchor, int flag) {
		View contentView = LayoutInflater.from(ExamListActivity.this).inflate(R.layout.windows_popupwindow, null);
		final PopupWindow popupWindow = new PopupWindow(contentView);

		lv1 = (ListView) contentView.findViewById(R.id.lv1);
		lv2 = (ListView) contentView.findViewById(R.id.lv2);
		lv1_layout = (LinearLayout) contentView.findViewById(R.id.lv1_layout);
		switch (flag) {
			case 1:
				myadapter = new MyAdapter(ExamListActivity.this, initArrayData(R.array.sub_peixun));
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
							//当点击ListView顶部的分类,此Activity中只有一个分类
							case 1:
								lv1_layout.getLayoutParams().width = 0; // 全部分类 高中物理
								switch (position) {
									//当点击一级列表的第一项
									case 0:
										if (examPaperList != null) {
											examPaperList.clear();
										}
										pullToRefreshListView.setAdapter(peiXunAdapter);
										getExaminPaperList(0,0);
										peiXunAdapter.notifyDataSetChanged();
										String name = (String) parent.getAdapter().getItem(position);
										setHeadText(idx, name);
										popupWindow.dismiss();
										break;
									//点击一级列表的第二项
									case 1:
										subAdapter = new SubAdapter(ExamListActivity.this.getApplicationContext(), subject[position]);
										lv2.setAdapter(subAdapter);
										subAdapter.notifyDataSetChanged();
										lv2.setOnItemClickListener(new OnItemClickListener() {
											@Override
											public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
												switch (position) {
													case 0:
														//Toast.makeText(ExamListActivity.this,"初中物理",Toast.LENGTH_SHORT).show();
														if (examPaperList != null) {
															examPaperList.clear();
														}
														pullToRefreshListView.setAdapter(peiXunAdapter);
														getExaminPaperList(0,386);
														peiXunAdapter.notifyDataSetChanged();
														popupWindow.dismiss();
														break;
													case 1:
														//Toast.makeText(ExamListActivity.this,"初中化学",Toast.LENGTH_SHORT).show();
														if (examPaperList != null) {
															examPaperList.clear();
														}
														pullToRefreshListView.setAdapter(peiXunAdapter);
														getExaminPaperList(0, 387);
														peiXunAdapter.notifyDataSetChanged();
														popupWindow.dismiss();
														break;
													case 2:
														//Toast.makeText(ExamListActivity.this,"初中生物",Toast.LENGTH_SHORT).show();
														if (examPaperList != null) {
															examPaperList.clear();
														}
														pullToRefreshListView.setAdapter(peiXunAdapter);
														getExaminPaperList(0, 388);
														peiXunAdapter.notifyDataSetChanged();
														popupWindow.dismiss();
														break;
													case 3:
														//Toast.makeText(ExamListActivity.this,"初中科学",Toast.LENGTH_SHORT).show();
														if (examPaperList != null) {
															examPaperList.clear();
														}
														pullToRefreshListView.setAdapter(peiXunAdapter);
														getExaminPaperList(0, 404);
														peiXunAdapter.notifyDataSetChanged();
														popupWindow.dismiss();
														break;
													case 4:
														//Toast.makeText(ExamListActivity.this,String.valueOf(position),Toast.LENGTH_SHORT).show();
														if (examPaperList != null) {
															examPaperList.clear();
														}
														pullToRefreshListView.setAdapter(peiXunAdapter);
														getExaminPaperList(0, 393);
														peiXunAdapter.notifyDataSetChanged();
														popupWindow.dismiss();
														break;
												}
											}
										});
										break;
									//点击一级列表的第三项
									case 2:
										subAdapter = new SubAdapter(ExamListActivity.this.getApplicationContext(), subject[position]);
										lv2.setAdapter(subAdapter);
										subAdapter.notifyDataSetChanged();
										lv2.setOnItemClickListener(new OnItemClickListener() {
											@Override
											public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
												switch (position) {

													/**
													 * 按分类来筛选(高中物理:371,高中化学:373,高中生物:375,通用技术:406,
													 *              初中物理:386,初中化学:387,初中生物:388,初中科学:404,信息技术:393)
													 */

													case 0:
														//Toast.makeText(ExamListActivity.this, "高中物理", Toast.LENGTH_SHORT).show();
														if (examPaperList != null) {
															examPaperList.clear();
														}
														pullToRefreshListView.setAdapter(peiXunAdapter);
														getExaminPaperList(0, 371);
														peiXunAdapter.notifyDataSetChanged();
														popupWindow.dismiss();
														break;
													case 1:
														//Toast.makeText(ExamListActivity.this, "高中化学", Toast.LENGTH_SHORT).show();
														if (examPaperList != null) {
															examPaperList.clear();
														}
														pullToRefreshListView.setAdapter(peiXunAdapter);
														getExaminPaperList(0, 373);
														peiXunAdapter.notifyDataSetChanged();
														popupWindow.dismiss();
														break;
													case 2:
														//Toast.makeText(ExamListActivity.this, "高中生物", Toast.LENGTH_SHORT).show();
														if (examPaperList != null) {
															examPaperList.clear();
														}
														pullToRefreshListView.setAdapter(peiXunAdapter);
														getExaminPaperList(0, 375);
														peiXunAdapter.notifyDataSetChanged();
														popupWindow.dismiss();
														break;
													case 3:
														//Toast.makeText(ExamListActivity.this, "通用技术", Toast.LENGTH_SHORT).show();
														if (examPaperList != null) {
															examPaperList.clear();
														}
														pullToRefreshListView.setAdapter(peiXunAdapter);
														getExaminPaperList(0, 406);
														peiXunAdapter.notifyDataSetChanged();
														popupWindow.dismiss();
														break;
												}
											}
										});
										break;
								}
								break;
						}

					}
				}
			}
		});
		popupWindow.setOnDismissListener(this);
		popupWindow.setWidth(ViewGroup.LayoutParams.FILL_PARENT);
		popupWindow.setHeight(ViewGroup.LayoutParams.FILL_PARENT);
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
		popupWindow.showAsDropDown(anchor);
	}
}
