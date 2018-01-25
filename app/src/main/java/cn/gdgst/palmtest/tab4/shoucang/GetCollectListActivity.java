package cn.gdgst.palmtest.tab4.shoucang;

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
import cn.gdgst.palmtest.R;
import cn.gdgst.palmtest.base.AppConstant;
import cn.gdgst.palmtest.tab2.Vid_Play_Activity;
import cn.gdgst.palmtest.tab3.MyAdapter;
import cn.gdgst.palmtest.utils.HttpUtil;

import com.orhanobut.logger.Logger;
import cn.gdgst.palmtest.Entitys.CollectEntity;
import cn.gdgst.palmtest.rewrite.ProgressWheel;
import cn.gdgst.palmtest.utils.NetworkCheck;
import cn.gdgst.palmtest.utils.NetworkCheckDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetCollectListActivity extends AppCompatActivity implements OnDismissListener, OnClickListener {
	private PullToRefreshListView MSListview;
	private List<CollectEntity> CollectList = new ArrayList<CollectEntity>();
	private List<CollectEntity> CollectListEntities = new ArrayList<CollectEntity>();
	private CollectEntity CollectListEntity;
	private SharedPreferences sp;
	private String accessToken;
	private ShouCangAdapter adapter;
	//	private ProgressBar progress_bar;
	private ProgressWheel progress_bar;
	private TextView tv_grade, tv_loading;
	private LinearLayout ll_grade, lv1_layout;
	private ImageView icon1;
	private ListView lv1;
	private MyAdapter myadapter;
	private int idx;

	private int page = 1;
	private String model = null;
	//private TextView tv_title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exam_list);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle("我的收藏");
		findview();
		initListView();

		if (accessToken.isEmpty()) {
			Toast.makeText(GetCollectListActivity.this, "获取列表失败,请先进行登录", Toast.LENGTH_SHORT).show();
			progress_bar.setVisibility(View.GONE);
			progress_bar.stopSpinning();
			tv_loading.setVisibility(View.GONE);
			MSListview.setEmptyView(findViewById(R.id.empty));
		} else {
			CollectList.clear();
			getExperimentList();

		}



	}

	private void findview() {
		// TODO Auto-generated method stub
		MSListview = (PullToRefreshListView) findViewById(R.id.exp_display);
		// MSListview.setEmptyView(findViewById(R.id.empty));
		// 文本
		tv_grade = (TextView) findViewById(R.id.tv_grade);
		tv_grade.setText("收藏分类");
		// 布局
		ll_grade = (LinearLayout) findViewById(R.id.ll_grade);

		ll_grade.setOnClickListener(this);
		// 图片
		icon1 = (ImageView) findViewById(R.id.icon1);
		//iv_back = (ImageView) findViewById(R.id.iv_back);
		//iv_back.setOnClickListener(this);
//		progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
		progress_bar=(ProgressWheel) findViewById(R.id.progress_bar);
		progress_bar.setBarColor(Color.parseColor("#63c5fe"));
		progress_bar.spin();
		tv_loading = (TextView) findViewById(R.id.tv_loading);
		//tv_title = (TextView) findViewById(R.id.tv_title);
		//tv_title.setText("我的收藏");
	}

	private void initListView() {
		// TODO Auto-generated method stub
		sp = getSharedPreferences(AppConstant.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
		accessToken = sp.getString("accessToken", "");
		adapter = new ShouCangAdapter(this, CollectList);

		MSListview.setMode(Mode.BOTH);// 使列表可以同时下拉和上拉刷新
		ListView actualListView = MSListview.getRefreshableView();

		MSListview.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override // 下拉监听
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				page=1;
				CollectList.clear();
				getExperimentList();
				adapter.notifyDataSetChanged();

			}

			@Override // 上拉监听
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {

				if (CollectListEntities.size() < 20) {
					// TODO Auto-generated method stub
					MSListview.postDelayed(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							adapter.notifyDataSetChanged();
							MSListview.onRefreshComplete();
							Toast.makeText(GetCollectListActivity.this, "没有更多数据了", Toast.LENGTH_SHORT).show();
						}
					}, 500);

				} else {
					MSListview.getLoadingLayoutProxy(false, true).setLastUpdatedLabel("");
					MSListview.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载");
					MSListview.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
					MSListview.getLoadingLayoutProxy(false, true).setReleaseLabel("放开以加载");
					page = page + 1;
					Logger.i("page" + page);
					getExperimentList();
					adapter.notifyDataSetChanged();
				}

			}
		});
		actualListView.setAdapter(adapter);

		MSListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				String url = CollectList.get(position - 1).getVideo_url();
				Intent intent = new Intent();
				intent.putExtra("video_path", url);
				intent.putExtra("video_name", CollectList.get(position - 1).getName());
				intent.setClass(GetCollectListActivity.this, Vid_Play_Activity.class);
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

	private void getExperimentList() {
		// TODO Auto-generated method stub
		sp = getSharedPreferences(AppConstant.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
		accessToken = sp.getString("accessToken", "");
		new Thread() {
			public void run() {
				String paged = String.valueOf(page);
				String url = "http://shiyan360.cn/index.php/api/user_collect";
				NetworkCheck check = new NetworkCheck(GetCollectListActivity.this);
				boolean isalivable = check.Network();
				if (isalivable) {
					// 封装请求参数
					Map<String, String> rawParams = new HashMap<>();
					rawParams.put("accessToken", accessToken);
					rawParams.put("page", paged);
					rawParams.put("model", model);
					try {
						mHandler.sendEmptyMessage(3);
						Thread.sleep(2000);
						// 设置请求参数项
						// 发送请求返回json
						String json = HttpUtil.postRequest(url, rawParams);
						Logger.json(json);
						// 解析json数据
						com.alibaba.fastjson.JSONObject jsonobj = JSON.parseObject(json);
						Boolean response = (Boolean) jsonobj.get("success");

						// 判断是否请求成功
						if (response) {
							// 解析截取“data”中的内容
							com.alibaba.fastjson.JSONArray jsondata = jsonobj.getJSONArray("data");
							String array = JSON.toJSONString(jsondata);
							CollectListEntities = JSON.parseArray(array, CollectEntity.class);
							if (!CollectListEntities.isEmpty()) {
								mHandler.sendEmptyMessage(0);
							}
						} else {
							mHandler.sendEmptyMessage(1);
						}
					} catch (Exception e) {
						e.printStackTrace();
						mHandler.sendEmptyMessage(4);
					}
				} else {
					NetworkCheckDialog.dialog(GetCollectListActivity.this);
				}
			}

		}.start();

	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0:

					for (int i = 0; i < CollectListEntities.size(); i++) {
						CollectListEntity = new CollectEntity();
						CollectListEntity.setName(CollectListEntities.get(i).getName());
						CollectListEntity.setImg_url(CollectListEntities.get(i).getImg_url());
						CollectListEntity.setVideo_url(CollectListEntities.get(i).getVideo_url());
						CollectList.add(CollectListEntity);

					}
					MSListview.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
//						progress_bar.setVisibility(View.GONE);
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
//						progress_bar.setVisibility(View.GONE);
							progress_bar.stopSpinning();
							tv_loading.setVisibility(View.GONE);
							adapter.notifyDataSetChanged();
							MSListview.onRefreshComplete();
							MSListview.setEmptyView(findViewById(R.id.empty));
							Toast.makeText(GetCollectListActivity.this, "获取列表失败,请先进行登录", Toast.LENGTH_SHORT).show();
						}
					});

					break;
				case 2:
					getExperimentList();
					break;
				case 3:
					tv_loading.setVisibility(View.VISIBLE);
//				progress_bar.setVisibility(View.VISIBLE);
					progress_bar.spin();

					break;
				case 4:
					MSListview.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
//						progress_bar.setVisibility(View.GONE);
							progress_bar.stopSpinning();
							tv_loading.setVisibility(View.GONE);
							adapter.notifyDataSetChanged();
							MSListview.onRefreshComplete();
							MSListview.setEmptyView(findViewById(R.id.empty));
							Toast.makeText(GetCollectListActivity.this, "暂无相关内容！", Toast.LENGTH_SHORT).show();
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

		View contentView = LayoutInflater.from(GetCollectListActivity.this).inflate(R.layout.windows_popupwindow, null);
		final PopupWindow popupWindow = new PopupWindow(contentView);

		lv1 = (ListView) contentView.findViewById(R.id.lv1);
		lv1_layout = (LinearLayout) contentView.findViewById(R.id.lv1_layout);
		switch (flag) {
			case 1:
				myadapter = new MyAdapter(GetCollectListActivity.this, initArrayData(R.array.sub_shoucang));
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
							switch (position) {
								case 0:
									model = "play";// 同步视频
									CollectList.clear();
									MSListview.setAdapter(adapter);
									getExperimentList();
									adapter.notifyDataSetChanged();
									break;
								case 1:
									model = "video";// 视频专辑
									CollectList.clear();
									MSListview.setAdapter(adapter);
									getExperimentList();
									adapter.notifyDataSetChanged();
									break;
								case 2:
									model = "mingshi";// 名师
									CollectList.clear();
									MSListview.setAdapter(adapter);
									getExperimentList();
									adapter.notifyDataSetChanged();
									break;
								case 3:
									model = "kaoshi";// 考试
									CollectList.clear();
									MSListview.setAdapter(adapter);
									getExperimentList();
									adapter.notifyDataSetChanged();
									break;
								case 4:
									model = "shiyan";// 实验
									CollectList.clear();
									MSListview.setAdapter(adapter);
									getExperimentList();
									adapter.notifyDataSetChanged();
									break;
								case 5:
									model = "chuangke";// 创客
									CollectList.clear();
									MSListview.setAdapter(adapter);
									getExperimentList();
									adapter.notifyDataSetChanged();
									break;
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




}
