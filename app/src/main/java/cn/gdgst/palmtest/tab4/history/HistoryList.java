package cn.gdgst.palmtest.tab4.history;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import cn.gdgst.palmtest.API.APIWrapper;
import cn.gdgst.palmtest.R;
import cn.gdgst.palmtest.Entitys.HistoryEntity;
import cn.gdgst.palmtest.bean.HttpResult;
import cn.gdgst.palmtest.utils.HttpUtil;

import com.orhanobut.logger.Logger;

import cn.gdgst.palmtest.rewrite.ProgressWheel;
import cn.gdgst.palmtest.tab2.Vid_Play_Activity;
import cn.gdgst.palmtest.utils.NetworkCheck;
import cn.gdgst.palmtest.utils.NetworkCheckDialog;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryList extends Activity implements OnClickListener{
	private PullToRefreshListView MSListview;
	private List<HistoryEntity> historyList = new ArrayList<HistoryEntity>();
	private List<HistoryEntity> historyListEntities = new ArrayList<HistoryEntity>();
	private HistoryEntity historyListEntity;
	private SharedPreferences sp;
	private String accessToken;
	private HistoryAdapter adapter;
	private ProgressWheel progress_bar;
	private TextView tv_loading;
	private ImageView  iv_back,iv_delete;

	private int page = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		findview();
		initListView();

		if (accessToken.isEmpty()) {
			Toast.makeText(HistoryList.this, "获取列表失败,请先进行登录", Toast.LENGTH_SHORT).show();
//			progress_bar.setVisibility(View.GONE);
			progress_bar.stopSpinning();
			tv_loading.setVisibility(View.GONE);
			MSListview.setEmptyView(findViewById(R.id.empty));
		} else {
			historyList.clear();
			//getExperimentList();
			getExperimentListByRetrofit();
		}
	}

	private void findview() {
		// TODO Auto-generated method stub
		MSListview = (PullToRefreshListView) findViewById(R.id.exp_display);
		// MSListview.setEmptyView(findViewById(R.id.empty));
		// 图片
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		iv_delete= (ImageView) findViewById(R.id.iv_delete);
		iv_delete.setOnClickListener(this);
//		progress_bar = (ProgressBar) findViewById(R.id.progress_bar);
		progress_bar = (ProgressWheel) findViewById(R.id.progress_bar);
		progress_bar.setBarColor(Color.parseColor("#63c5fe"));
		progress_bar.spin();
		tv_loading = (TextView) findViewById(R.id.tv_loading);
	}

	private void initListView() {
		// TODO Auto-generated method stub
		sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		accessToken = sp.getString("accessToken", "");
		Log.v("HistorList", "accessToken=="+accessToken.toString());
		adapter = new HistoryAdapter(this, historyList);

		MSListview.setMode(Mode.BOTH);// 使列表可以同时下拉和上拉刷新
		ListView actualListView = MSListview.getRefreshableView();

		MSListview.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override // 下拉监听
			public void onPullDownToRefresh(PullToRefreshBase refreshView) {
				// TODO Auto-generated method stub
				page=1;
				historyList.clear();
				//getExperimentList();
				getExperimentListByRetrofit();
				adapter.notifyDataSetChanged();

			}

			@Override // 上拉监听
			public void onPullUpToRefresh(PullToRefreshBase refreshView) {

				if (historyListEntities.size() < 20) {
					// TODO Auto-generated method stub
					MSListview.postDelayed(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							adapter.notifyDataSetChanged();
							MSListview.onRefreshComplete();
							Toast.makeText(HistoryList.this, "没有更多数据了", Toast.LENGTH_SHORT).show();
						}
					}, 500);
				} else {
					MSListview.getLoadingLayoutProxy(false, true).setLastUpdatedLabel("");
					MSListview.getLoadingLayoutProxy(false, true).setPullLabel("上拉加载");
					MSListview.getLoadingLayoutProxy(false, true).setRefreshingLabel("正在加载...");
					MSListview.getLoadingLayoutProxy(false, true).setReleaseLabel("放开以加载");
					page = page + 1;
					Logger.i( "page" + page);
					//getExperimentList();
					getExperimentListByRetrofit();
					adapter.notifyDataSetChanged();
				}

			}
		});
		actualListView.setAdapter(adapter);

		MSListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				String url = historyList.get(position - 1).getVideo_url();
				Intent intent = new Intent();
				intent.putExtra("video_path", url);
				intent.putExtra("video_name", historyList.get(position - 1).getName());
				intent.setClass(HistoryList.this, Vid_Play_Activity.class);
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
			case R.id.iv_delete:
				showDialog();
				break;

		}
	}

	/**通过RxJava和Retrofit来加载内容*/
	private void getExperimentListByRetrofit(){
		final NetworkCheck check = new NetworkCheck(this);
		sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		accessToken = sp.getString("accessToken", "");
		String paged = String.valueOf(page);

		APIWrapper.getInstance().getHistoryList(accessToken, paged)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<HttpResult<List<HistoryEntity>>>() {
					@Override
					public void onCompleted() {

					}

					@Override
					public void onError(Throwable e) {
						if(!check.Network()){
							NetworkCheckDialog.dialog(HistoryList.this);
						}
						mHandler.sendEmptyMessage(1);

					}

					@Override
					public void onNext(HttpResult<List<HistoryEntity>> listHttpResult) {
						historyListEntities = listHttpResult.getData();

						Log.v("HistoryList", historyList.toString());

						if (!historyListEntities.isEmpty()){
							mHandler.sendEmptyMessage(0);
						}else {
							mHandler.sendEmptyMessage(1);
						}
					}
				});
	}

	private void getExperimentList() {
		// TODO Auto-generated method stub
		sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
		accessToken = sp.getString("accessToken", "");
		String paged = String.valueOf(page);
		new Thread() {
			public void run() {
				String paged = String.valueOf(page);
				String url = "http://shiyan360.cn/index.php/api/user_history";
				NetworkCheck check = new NetworkCheck(HistoryList.this);
				boolean isalivable = check.Network();
				if (isalivable) {
					// 封装请求参数
					Map<String, String> rawParams = new HashMap<String, String>();
					rawParams.put("accessToken", accessToken);
					rawParams.put("page", paged);
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
							historyListEntities = JSON.parseArray(array, HistoryEntity.class);
							if (!historyListEntities.isEmpty()) {
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
					NetworkCheckDialog.dialog(HistoryList.this);
				}

			}

		}.start();

	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0:

					for (int i = 0; i < historyListEntities.size(); i++) {
						historyListEntity = new HistoryEntity();
						historyListEntity.setName(historyListEntities.get(i).getName());
						historyListEntity.setImg_url(historyListEntities.get(i).getImg_url());
						historyListEntity.setVideo_url(historyListEntities.get(i).getVideo_url());
						historyList.add(historyListEntity);

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
							Toast.makeText(HistoryList.this, "暂无浏览记录,赶快去观看学习视频吧...", Toast.LENGTH_SHORT).show();
						}
					});

					break;
				case 2:
					//getExperimentList();
					getExperimentListByRetrofit();
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
							Toast.makeText(HistoryList.this, "暂无相关内容！", Toast.LENGTH_SHORT).show();
						}
					});

					break;
				case 5:
					Toast.makeText(HistoryList.this, "清理成功", Toast.LENGTH_SHORT).show();
					historyList.clear();
					adapter.notifyDataSetChanged();
					MSListview.setEmptyView(findViewById(R.id.empty));
					break;
				case 6:
					Toast.makeText(HistoryList.this, "清理失败", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
			}

		}

	};

	private void showDialog() {
		  /*
		  这里使用了 android.support.v7.app.AlertDialog.Builder
		  可以直接在头部写 import android.support.v7.app.AlertDialog
		  那么下面就可以写成 AlertDialog.Builder
		  */


		AlertDialog.Builder builder = new AlertDialog.Builder(HistoryList.this);
		builder.setInverseBackgroundForced(false);
		builder.setTitle("清除浏览记录");
		builder.setMessage("确定清除浏览记录？");
		builder.setNegativeButton("取消", null);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Thread deleteThread=new Thread(new deleteThread());
				deleteThread.start();
			}
		});
		builder.create().show();
	}

	class deleteThread implements Runnable{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			String url = "http://shiyan360.cn/index.php/api/user_history_clear";

			Map<String, String> rawParams = new HashMap<String, String>();
			rawParams.put("accessToken", accessToken);
			try {
				Thread.sleep(200);
				// 设置请求参数项
				// 发送请求返回json
				String json = HttpUtil.postRequest(url, rawParams);
				Logger.json(json);
				// 解析json数据
				com.alibaba.fastjson.JSONObject jsonobj = JSON.parseObject(json);
				Boolean response = (Boolean) jsonobj.get("success");

				// 判断是否请求成功
				if (response) {
					mHandler.sendEmptyMessage(5);
				}
				else {
					mHandler.sendEmptyMessage(6);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

	}



}
