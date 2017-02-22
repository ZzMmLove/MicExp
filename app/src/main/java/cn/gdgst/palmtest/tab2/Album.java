package cn.gdgst.palmtest.tab2;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
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
import cn.gdgst.palmtest.R;
import cn.gdgst.palmtest.Entitys.GradeEntity;
import cn.gdgst.palmtest.Entitys.Sub;
import cn.gdgst.palmtest.rewrite.ProgressWheel;
import cn.gdgst.palmtest.servers.GetSortList;
import cn.gdgst.palmtest.tab3.MyAdapter;
import cn.gdgst.palmtest.utils.HttpUtil;
import cn.gdgst.palmtest.utils.NetworkCheck;
import cn.gdgst.palmtest.utils.NetworkCheckDialog;

import com.orhanobut.logger.Logger;

import cn.gdgst.palmtest.Entitys.Video_Album_List_Entity;
import cn.gdgst.palmtest.tab3.SubAdapter;

import org.afinal.simplecache.ACache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Album extends Fragment implements OnClickListener, OnDismissListener {
	private PullToRefreshListView AlbumListview;
	private List<Video_Album_List_Entity> Video_Album_List = new ArrayList<Video_Album_List_Entity>();
	private List<Video_Album_List_Entity> album_List_Entities = new ArrayList<Video_Album_List_Entity>();
	private Video_Album_List_Entity album_List_Entity;
	private SharedPreferences sp;
	private AlbumAdapter albumAdapter;

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

	private int desctype = 0;
	private String gradeid = "0";
	private String categoryid = "0";
	private String album_id = "0";
	private int page = 1;
	private ProgressWheel progress_bar;
	private List<GradeEntity> GradeList;
	private List<Sub> Subsort;
	private List<String> highList = new ArrayList<String>();
	private List<String> juniList = new ArrayList<String>();
	private List<String> primList = new ArrayList<String>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_album, container, false);
		findview();
		initListView();
		cities = new String[][] { null, this.getResources().getStringArray(R.array.sub_highcate),
				this.getResources().getStringArray(R.array.sub_junicate),
				this.getResources().getStringArray(R.array.sub_primcate) };
		return view;
	}

	private void initListView() {
		// TODO Auto-generated method stub

		albumAdapter = new AlbumAdapter(getActivity(), Video_Album_List);
		ListView actualListView = AlbumListview.getRefreshableView();
		AlbumListview.setMode(Mode.BOTH); // 两端刷新

		// Set a listener to be invoked when the list should be refreshed.
		AlbumListview.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				Video_Album_List.clear();
				getAlbumList();
				getSubList();
				getGraList();
				albumAdapter.notifyDataSetChanged();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub

				if (album_List_Entities.size() < 20) {
					// TODO Auto-generated method stub
					AlbumListview.postDelayed(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							albumAdapter.notifyDataSetChanged();
							AlbumListview.onRefreshComplete();
							Toast.makeText(getActivity(), "没有更多数据了", Toast.LENGTH_SHORT).show();
						}
					}, 500);

				} else {
					AlbumListview.getLoadingLayoutProxy(false, true).setLastUpdatedLabel("");
					AlbumListview.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载");
					AlbumListview.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
					AlbumListview.getLoadingLayoutProxy(false, true).setReleaseLabel("放开以加载");
					page = page + 1;
					Logger.i( "page" + page);
					getAlbumList();
					getSubList();
					getGraList();
					albumAdapter.notifyDataSetChanged();
				}
			}
		});
		actualListView.setAdapter(albumAdapter);
		AlbumListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				album_id = album_List_Entities.get(position - 1).getId();
				Intent intent2 = new Intent();
				intent2.putExtra("album_id", album_id);
				intent2.setClass(getActivity(), Video_Album_List.class);
				startActivity(intent2);

			}
		});

		getCacheCollectdata();
		albumAdapter.notifyDataSetChanged();
		getSubList();
		getGraList();


	}

	private void findview() {
		// TODO Auto-generated method stub
		AlbumListview = (PullToRefreshListView) view.findViewById(R.id.album_display);
		// AlbumListview.setEmptyView(view.findViewById(R.id.empty));
		// 文本
		tv_grade = (TextView) view.findViewById(R.id.tv_grade);
		tv_category = (TextView) view.findViewById(R.id.tv_category);
		tv_sorting_latest = (TextView) view.findViewById(R.id.tv_sorting_latest);
		// 布局
		ll_grade = (LinearLayout) view.findViewById(R.id.ll_grade);
		ll_category = (LinearLayout) view.findViewById(R.id.ll_category);
		ll_sorting_latest = (LinearLayout) view.findViewById(R.id.ll_sorting_latest);

		ll_grade.setOnClickListener((OnClickListener) this);
		ll_category.setOnClickListener((OnClickListener) this);
		ll_sorting_latest.setOnClickListener((OnClickListener) this);
		// 图片
		icon1 = (ImageView) view.findViewById(R.id.icon1);
		icon2 = (ImageView) view.findViewById(R.id.icon2);
		icon3 = (ImageView) view.findViewById(R.id.icon3);

		progress_bar=(ProgressWheel) view.findViewById(R.id.progress_bar);
		progress_bar.setBarColor(Color.parseColor("#63c5fe"));
		progress_bar.spin();
		tv_loading = (TextView) view.findViewById(R.id.tv_loading);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.ll_grade:
				idx = 1;
				icon1.setImageResource(R.mipmap.icon_up);
				showPopupWindow(view.findViewById(R.id.ll_layout), 1);
				break;
			case R.id.ll_category:
				idx = 2;
				icon2.setImageResource(R.mipmap.icon_up);
				showPopupWindow(view.findViewById(R.id.ll_layout), 2);
				break;
			case R.id.ll_sorting_latest:
				idx = 3;
				icon3.setImageResource(R.mipmap.icon_up);
				showPopupWindow(view.findViewById(R.id.ll_layout), 3);
				break;
		}
	}

	private void showPopupWindow(View anchor, int flag) {
		// TODO Auto-generated method stub

		View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.windows_popupwindow, null);
		final PopupWindow popupWindow = new PopupWindow(contentView);

		lv1 = (ListView) contentView.findViewById(R.id.lv1);
		lv2 = (ListView) contentView.findViewById(R.id.lv2);
		lv1_layout = (LinearLayout) contentView.findViewById(R.id.lv1_layout);
		switch (flag) {
			case 1:
				myadapter = new MyAdapter(getActivity(), initArrayData(R.array.sub_grade2));
				break;
			case 2:
				myadapter = new MyAdapter(getActivity(), initArrayData(R.array.sub_category));
				break;
			case 3:
				myadapter = new MyAdapter(getActivity(), initArrayData(R.array.sub_sorting_latest));
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
									Video_Album_List.clear();
									AlbumListview.setAdapter(albumAdapter);
									getAlbumList();
									albumAdapter.notifyDataSetChanged();
								} else {
									gradeid = GradeList.get(position - 1).getId();
									Video_Album_List.clear();
									AlbumListview.setAdapter(albumAdapter);
									getAlbumList();
									albumAdapter.notifyDataSetChanged();
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
										Video_Album_List.clear();
										AlbumListview.setAdapter(albumAdapter);
										getAlbumList();
										albumAdapter.notifyDataSetChanged();

										subAdapter = null;
										// 当没有下级时直接将信息设置textview中
										String name2 = (String) parent.getAdapter().getItem(position);
										setHeadText(idx, name2);
										popupWindow.dismiss();
										break;
									case 1:
										subAdapter = new SubAdapter(getActivity().getApplicationContext(), cities[position]);
										lv2.setAdapter(subAdapter);
										subAdapter.notifyDataSetChanged();
										lv2.setOnItemClickListener(new OnItemClickListener() {

											@Override
											public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
												if (position == 0) {
													categoryid = "368";
													Video_Album_List.clear();
													AlbumListview.setAdapter(albumAdapter);
													getAlbumList();
													albumAdapter.notifyDataSetChanged();
												} else {
													categoryid = highList.get(position - 1);
													Video_Album_List.clear();
													AlbumListview.setAdapter(albumAdapter);
													getAlbumList();
													albumAdapter.notifyDataSetChanged();
												}

												String name = (String) parent.getAdapter().getItem(position);
												setHeadText(idx, name);
												popupWindow.dismiss();
												subAdapter = null;
											}
										});
										break;
									case 2:
										subAdapter = new SubAdapter(getActivity().getApplicationContext(), cities[position]);
										lv2.setAdapter(subAdapter);
										subAdapter.notifyDataSetChanged();
										lv2.setOnItemClickListener(new OnItemClickListener() {

											@Override
											public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
												if (position == 0) {
													categoryid = "369";
													Video_Album_List.clear();
													AlbumListview.setAdapter(albumAdapter);
													getAlbumList();
													albumAdapter.notifyDataSetChanged();
												} else {
													categoryid = juniList.get(position - 1);
													Video_Album_List.clear();
													AlbumListview.setAdapter(albumAdapter);
													getAlbumList();
													albumAdapter.notifyDataSetChanged();
												}

												String name = (String) parent.getAdapter().getItem(position);
												setHeadText(idx, name);
												popupWindow.dismiss();
												subAdapter = null;
											}
										});
										break;
									case 3:
										subAdapter = new SubAdapter(getActivity().getApplicationContext(), cities[position]);
										lv2.setAdapter(subAdapter);
										subAdapter.notifyDataSetChanged();
										lv2.setOnItemClickListener(new OnItemClickListener() {

											@Override
											public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
												if (position == 0) {
													categoryid = "370";
													Video_Album_List.clear();
													AlbumListview.setAdapter(albumAdapter);
													getAlbumList();
													albumAdapter.notifyDataSetChanged();
												} else {
													categoryid = primList.get(position - 1);
													Video_Album_List.clear();
													AlbumListview.setAdapter(albumAdapter);
													getAlbumList();
													albumAdapter.notifyDataSetChanged();
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
										Video_Album_List.clear();
										AlbumListview.setAdapter(albumAdapter);
										getAlbumList();
										albumAdapter.notifyDataSetChanged();
										break;
									case 1:
										desctype = 2; // 按访问记录 2
										Video_Album_List.clear();
										AlbumListview.setAdapter(albumAdapter);
										getAlbumList();
										albumAdapter.notifyDataSetChanged();

										break;
									case 2:
										desctype = 3; // 按最多评论 3
										Video_Album_List.clear();
										AlbumListview.setAdapter(albumAdapter);
										getAlbumList();
										albumAdapter.notifyDataSetChanged();
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

	private void getAlbumList() {
		// TODO Auto-generated method stub
		new Thread() {
			public void run() {
				// 获取视频专辑列表URL
				String urlStr = "http://www.shiyan360.cn/index.php/api/video_album_list";
				String paged = String.valueOf(page);
				String desc_type = String.valueOf(desctype);
				String category_id = categoryid;

				NetworkCheck check = new NetworkCheck(getActivity());
				boolean isalivable = check.Network();
				if (isalivable) {
					// 封装请求参数
					Map<String, String> rawParams = new HashMap<String, String>();
					rawParams.put("page", paged);
					rawParams.put("gradeid", gradeid);
					rawParams.put("desc_type", desc_type);
					rawParams.put("category_id", category_id);

					try {
						mHandler.sendEmptyMessage(3);
						Thread.sleep(1100);
						// 设置请求参数项
						// 发送请求返回json
						String json = HttpUtil.postRequest(urlStr, rawParams);
						Logger.json(json);

						// 解析json数据
						com.alibaba.fastjson.JSONObject jsonobj = JSON.parseObject(json);
						Boolean response = (Boolean) jsonobj.get("success");

						// 判断是否请求成功
						if (response) {
							// 解析截取“data”中的内容
							com.alibaba.fastjson.JSONArray jsondata = jsonobj.getJSONArray("data");
							String array = JSON.toJSONString(jsondata);
							album_List_Entities = JSON.parseArray(array, Video_Album_List_Entity.class);
							if (!album_List_Entities.isEmpty()) {
								mHandler.sendEmptyMessage(0);

								ACache mCache = ACache.get(getActivity());
								mCache.put("AlbumList", json);
							}

						} else {
							mHandler.sendEmptyMessage(1);

						}
					} catch (Exception e) {
						e.printStackTrace();
						mHandler.sendEmptyMessage(4);
					}

				} else {
					NetworkCheckDialog.dialog(getActivity());
				}

			}
		}.start();
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0:
					for (int i = 0; i < album_List_Entities.size(); i++) {
						album_List_Entity = new Video_Album_List_Entity();
						album_List_Entity.setImg_url(album_List_Entities.get(i).getImg_url().trim());
						album_List_Entity.setName(album_List_Entities.get(i).getName());
						album_List_Entity.setId(album_List_Entities.get(i).getId());
						Video_Album_List.add(album_List_Entity);

					}
					AlbumListview.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							progress_bar.stopSpinning();
							tv_loading.setVisibility(View.GONE);
							albumAdapter.notifyDataSetChanged();
							AlbumListview.onRefreshComplete();
							// Toast.makeText(getActivity(), "获取列表成功",
							// Toast.LENGTH_SHORT).show();
						}
					});

					break;
				case 1:
					AlbumListview.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							progress_bar.stopSpinning();
							tv_loading.setVisibility(View.GONE);
							albumAdapter.notifyDataSetChanged();
							AlbumListview.onRefreshComplete();
							AlbumListview.setEmptyView(view.findViewById(R.id.empty));
							Toast.makeText(getActivity(), "获取列表失败,请先进行登录", Toast.LENGTH_SHORT).show();
						}
					});

					break;
				case 2:
					getAlbumList();
					break;
				case 3:
					tv_loading.setVisibility(View.VISIBLE);
					progress_bar.spin();
					break;
				case 4:
					AlbumListview.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							progress_bar.stopSpinning();
							tv_loading.setVisibility(View.GONE);
							albumAdapter.notifyDataSetChanged();
							AlbumListview.onRefreshComplete();
							AlbumListview.setEmptyView(view.findViewById(R.id.empty));
							Toast.makeText(getActivity(), "暂无相关内容！", Toast.LENGTH_SHORT).show();
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
		// TODO Auto-generated method stub
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
				String url = "http://www.shiyan360.cn/index.php/api/category_list";// 二级分类
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
	//缓存中获取数据
	public void getCacheCollectdata(){
		ACache mCache = ACache.get(getActivity());
		String value = mCache.getAsString("AlbumList");
		Logger.json(value);
		if (value!=null) {
			com.alibaba.fastjson.JSONObject jsonobj = JSON.parseObject(value);
			com.alibaba.fastjson.JSONArray jsondata = jsonobj.getJSONArray("data");
			String array = JSON.toJSONString(jsondata);
			album_List_Entities = JSON.parseArray(array, Video_Album_List_Entity.class);
			mHandler.sendEmptyMessage(0);
		}
		else {
			getAlbumList();
		}

	}
}