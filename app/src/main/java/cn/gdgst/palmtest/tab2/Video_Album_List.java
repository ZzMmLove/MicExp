package cn.gdgst.palmtest.tab2;

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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import cn.gdgst.palmtest.R;
import cn.gdgst.palmtest.rewrite.ProgressWheel;
import cn.gdgst.palmtest.utils.HttpUtil;

import com.orhanobut.logger.Logger;
import cn.gdgst.palmtest.Entitys.Video_Album_List_Entity;
import cn.gdgst.palmtest.utils.NetworkCheck;
import cn.gdgst.palmtest.utils.NetworkCheckDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Video_Album_List extends Activity implements  OnClickListener{
	private PullToRefreshGridView  Vid_Ab_Listview;

	private List<Video_Album_List_Entity> Video_Album_List = new ArrayList<Video_Album_List_Entity>();
	private List<Video_Album_List_Entity> vidalbum_ListEntities = new ArrayList<Video_Album_List_Entity>();
	private Video_Album_List_Entity vidalbum_ListEntity;
	private SharedPreferences sp;
	private Album_GridAdapter adapter;
	private ImageView iv_back;
	private ProgressWheel progress_bar;
	private TextView tv_loading;
	private String album_id="0";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_album_list);
		findview();
		initListView();

		Video_Album_List.clear();
		getExperimentList();

	}
	private void initListView() {
		// TODO Auto-generated method stub

		Intent intent2=getIntent();
		album_id=intent2.getStringExtra("album_id");



		adapter=new Album_GridAdapter(this, Video_Album_List);
//		Vid_Ab_Listview.setMode(Mode.BOTH); //两端刷新
		GridView actualGridView = Vid_Ab_Listview.getRefreshableView();
		// Set a listener to be invoked when the list should be refreshed.
		Vid_Ab_Listview.setOnRefreshListener(new OnRefreshListener2<GridView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub

				Video_Album_List.clear();
				getExperimentList();
				adapter.notifyDataSetChanged();


			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				if (vidalbum_ListEntities.size() < 20) {
					// TODO Auto-generated method stub
					Vid_Ab_Listview.postDelayed(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							adapter.notifyDataSetChanged();
							Vid_Ab_Listview.onRefreshComplete();
							Toast.makeText(Video_Album_List.this, "没有更多数据了", Toast.LENGTH_SHORT).show();
						}
					}, 500);

				} else {
					Vid_Ab_Listview.getLoadingLayoutProxy(false, true).setLastUpdatedLabel("");
					Vid_Ab_Listview.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载");
					Vid_Ab_Listview.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
					Vid_Ab_Listview.getLoadingLayoutProxy(false, true).setReleaseLabel("放开以加载");
					getExperimentList();
					adapter.notifyDataSetChanged();
				}
			}
		});
		actualGridView.setAdapter(adapter);
		Vid_Ab_Listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				String url =Video_Album_List.get(position).getVideo_url();
				String[] urls = new String[] { url };

				Intent myIntent3 = new Intent();
				myIntent3.putExtra("video_path", url);
				myIntent3.putExtra("video_name",Video_Album_List.get(position).getName());
				myIntent3.setClass(Video_Album_List.this, Vid_Play_Activity.class);
				startActivity(myIntent3);

			}
		});
	}
	private void findview() {
		// TODO Auto-generated method stub
		Vid_Ab_Listview= (PullToRefreshGridView) findViewById(R.id.vidalbum_display);
//		Vid_Ab_Listview.setEmptyView(findViewById(R.id.empty));
		iv_back=(ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		progress_bar=(ProgressWheel) findViewById(R.id.progress_bar);
		progress_bar.setBarColor(Color.parseColor("#63c5fe"));
		progress_bar.spin();

		tv_loading = (TextView) findViewById(R.id.tv_loading);
	}

	private void getExperimentList() {
		// TODO Auto-generated method stub
		new Thread() {
			public void run() {
				String urlStr = "http://www.shiyan360.cn/index.php/api/video_album";

				NetworkCheck check = new NetworkCheck(Video_Album_List.this);
				boolean isalivable = check.Network();
				if (isalivable) {
					// 封装请求参数
					Map<String, String> rawParams = new HashMap<String, String>();
					rawParams.put("album_id", album_id);
					try {
						mHandler.sendEmptyMessage(3);
						Thread.sleep(2000);
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
//							com.alibaba.fastjson.JSONArray jsondata = jsonobj.getJSONArray("data");
							com.alibaba.fastjson.JSONObject js = jsonobj.getJSONObject("data");
							com.alibaba.fastjson.JSONArray jsonarr =js.getJSONArray("video_list");
							String array=JSON.toJSONString(jsonarr);
							vidalbum_ListEntities = JSON.parseArray(array, Video_Album_List_Entity.class);

							if (!vidalbum_ListEntities.isEmpty()) {
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
					NetworkCheckDialog.dialog(Video_Album_List.this);
				}

			}

		}.start();

	}
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0:

					for (int i = 0; i < vidalbum_ListEntities.size(); i++) {
						vidalbum_ListEntity = new Video_Album_List_Entity();
						vidalbum_ListEntity.setImg_url_s(vidalbum_ListEntities.get(i).getImg_url_s().trim());
						vidalbum_ListEntity.setName(vidalbum_ListEntities.get(i).getName());
						vidalbum_ListEntity.setVideo_url(vidalbum_ListEntities.get(i).getVideo_url());
						Video_Album_List.add(vidalbum_ListEntity);

					}
					Vid_Ab_Listview.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							progress_bar.stopSpinning();
							tv_loading.setVisibility(View.GONE);
							adapter.notifyDataSetChanged();
							Vid_Ab_Listview.onRefreshComplete();
						}
					});

					break;
				case 1:
					Vid_Ab_Listview.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							progress_bar.stopSpinning();
							tv_loading.setVisibility(View.GONE);
							adapter.notifyDataSetChanged();
							Vid_Ab_Listview.onRefreshComplete();
							Vid_Ab_Listview.setEmptyView(findViewById(R.id.empty));
							Toast.makeText(Video_Album_List.this, "获取列表失败,请先进行登录", Toast.LENGTH_SHORT).show();
						}
					});


					break;
				case 2:
					getExperimentList();
					break;
				case 3:
					tv_loading.setVisibility(View.VISIBLE);
					progress_bar.spin();
					break;
				case 4:
					Vid_Ab_Listview.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							progress_bar.stopSpinning();
							tv_loading.setVisibility(View.GONE);
							adapter.notifyDataSetChanged();
							Vid_Ab_Listview.onRefreshComplete();
							Vid_Ab_Listview.setEmptyView(findViewById(R.id.empty));
							Toast.makeText(Video_Album_List.this, "暂无相关内容！", Toast.LENGTH_SHORT).show();
						}
					});


					break;
				default:
					break;
			}

		}

	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.iv_back:
				this.finish();
				break;

		}
	}}
