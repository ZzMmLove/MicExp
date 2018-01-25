package cn.gdgst.palmtest.tab1.chuangke;

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

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import cn.gdgst.entity.ChuangKe;
import cn.gdgst.palmtest.R;
import com.orhanobut.logger.Logger;
import cn.gdgst.palmtest.API.APIWrapper;
import cn.gdgst.palmtest.DB.DbService;
import cn.gdgst.palmtest.Entitys.CK_Cate_Entity;
import cn.gdgst.palmtest.bean.HttpResult;
import cn.gdgst.palmtest.rewrite.ProgressWheel;
import cn.gdgst.palmtest.servers.GetSortList;
import cn.gdgst.palmtest.service.HistoryService;
import cn.gdgst.palmtest.tab2.Vid_Play_Activity;
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

public class ChuangKeList extends Activity implements OnDismissListener, OnClickListener {
    private PullToRefreshListView MSListview;
    private List<ChuangKe> ChuangKeList = new ArrayList<ChuangKe>();
    private List<ChuangKe> ChuangKeListEntities = new ArrayList<ChuangKe>();
    private ChuangKe ChuangKeListEntity;
    private SharedPreferences sp;
    private String accessToken;
    private ChuangKeAdapter adapter;

    private TextView tv_grade, tv_sorting_latest, tv_loading;
    private LinearLayout ll_grade, ll_sorting_latest, lv1_layout;
    private ImageView icon1, icon3, iv_back;
    private ListView lv1;
    private MyAdapter myadapter;
    private int idx;
    private List<CK_Cate_Entity> CK_Cate_List;

    private ProgressWheel progress_bar;
    private int desctype = 0;
    private String ckid = null; // 专辑ID
    private int page = 1;

    //greendao
    private DbService db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chuangkelist);
        db = DbService.getInstance(this);
        findview();
        initListView();

        ChuangKeList.clear();
        // getExperimentList();
        getckcateList();
//		   getCacheCollectdata();
//        getCkList();

        sp = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        accessToken = sp.getString("accessToken", "");


        readChuangKeDB();
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
        tv_loading = (TextView) findViewById(R.id.tv_loading);
        progress_bar = (ProgressWheel) findViewById(R.id.progress_bar);
        progress_bar.setBarColor(Color.parseColor("#63c5fe"));
        progress_bar.spin();
    }

    private void initListView() {
        // TODO Auto-generated method stub

        adapter = new ChuangKeAdapter(this, ChuangKeList);
        MSListview.setMode(Mode.BOTH);

        ListView actualListView = MSListview.getRefreshableView();
        MSListview.setOnRefreshListener(new OnRefreshListener2<ListView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase refreshView) {
                // TODO Auto-generated method stub
                page = 1;
//				getExperimentList();
                getckcateList();
                getCkList();
                ChuangKeList.clear();
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
                            Toast.makeText(ChuangKeList.this, "没有更多数据了", Toast.LENGTH_SHORT).show();
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
                    getCkList();
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
                String model = "chuangke";
                try {
                    HistoryService historyService = new HistoryService(ChuangKeList.this);
                    historyService.addHistory(accessToken, videoid, model);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String url = ChuangKeList.get(position - 1).getVideo_url();
                Intent myIntent3 = new Intent();
                myIntent3.putExtra("video_path", url);
                myIntent3.putExtra("video_name", ChuangKeList.get(position - 1).getName());
                myIntent3.setClass(ChuangKeList.this, Vid_Play_Activity.class);
                startActivity(myIntent3);

            }
        }

        );
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        NetworkCheck check = new NetworkCheck(this);
            switch (v.getId()) {
                case R.id.ll_grade:
                    idx = 1;
                    icon1.setImageResource(R.mipmap.icon_up);
                    if (check.Network()){

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

//    private void getExperimentList() {
//        // TODO Auto-generated method stub
//        new Thread() {
//            public void run() {
//                String urlStr = "http://www.shiyan360.cn/index.php/api/chuangke_list";
//                String desc_type = String.valueOf(desctype);
//                String category_id = ckid;
//                String paged = String.valueOf(page);
//
//                NetworkCheck check = new NetworkCheck(ChuangKeList.this);
//                boolean isalivable = check.Network();
//                if (isalivable) {
//                    // 封装请求参数
//                    Map<String, String> rawParams = new HashMap<String, String>();
//                    rawParams.put("desc_type", desc_type);
//                    rawParams.put("category_id", category_id);
//                    rawParams.put("page", paged);
//                    try {
//                        mHandler.sendEmptyMessage(3);
//                        Thread.sleep(2000);
//                        // 设置请求参数项
//                        // 发送请求返回json
//                        String json = HttpUtil.postRequest(urlStr, rawParams);
//                        Logger.json(json);
//                        // 解析json数据
//                        com.alibaba.fastjson.JSONObject jsonobj = JSON.parseObject(json);
//                        Boolean response = (Boolean) jsonobj.get("success");
//
//                        // 判断是否请求成功
//                        if (response) {
//                            // 解析截取“data”中的内容
//                            com.alibaba.fastjson.JSONArray jsondata = jsonobj.getJSONArray("data");
//                            String array = JSON.toJSONString(jsondata);
//                            ChuangKeListEntities = JSON.parseArray(array, ChuangKe.class);
//
//                            if (!ChuangKeListEntities.isEmpty()) {
//
//                                mHandler.sendEmptyMessage(0);
//                                ACache mCache = ACache.get(ChuangKeList.this);
//                                mCache.put("ChuangKeList", json);
//                            }
//                            // if (!experiment_ListEntities.isEmpty()) {
//                            // mHandler.sendEmptyMessage(0);
//                            // } else {
//                            // mHandler.sendEmptyMessage(4);
//                            //
//                            // }
//
//                        } else {
//                            mHandler.sendEmptyMessage(1);
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        mHandler.sendEmptyMessage(4);
//                    }
//
//                } else {
//                    NetworkCheckDialog.dialog(ChuangKeList.this);
//                    Toast.makeText(ChuangKeList.this, "当前网络不可用，请检查网络链接", Toast.LENGTH_SHORT).show();
//                }
//
//            }
//
//        }.start();
//
//    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:

                    for (int i = 0; i < ChuangKeListEntities.size(); i++) {
                        ChuangKeListEntity = new ChuangKe();
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
                            tv_loading.setVisibility(View.GONE);
                            progress_bar.stopSpinning();
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
                            Toast.makeText(ChuangKeList.this, "获取列表失败,请先进行登录", Toast.LENGTH_SHORT).show();
                        }
                    });

                    break;
                case 2:
//                    getExperimentList();
                    getCkList();
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
                            Toast.makeText(ChuangKeList.this, "暂无相关内容！", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case 5:
                    Logger.i("addhistory" + "add成功");
                    break;
                case 6:
                    Logger.i("addhistory" + "add失败");
                    break;
                case 7:
                    Logger.i("collect" + "收藏成功");

                    Toast.makeText(ChuangKeList.this, "收藏成功", Toast.LENGTH_SHORT).show();
                    break;
                case 8:

                    Toast.makeText(ChuangKeList.this, "取消收藏", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }

        }

    };

    /**
     * @return void 返回类型
     * @Title: showPopupWindow
     * @Description: PopupWindow
     * @author yimei
     */

    public void showPopupWindow(View anchor, int flag) {

        View contentView = LayoutInflater.from(ChuangKeList.this).inflate(R.layout.windows_popupwindow, null);
        final PopupWindow popupWindow = new PopupWindow(contentView);

        lv1 = (ListView) contentView.findViewById(R.id.lv1);
        lv1_layout = (LinearLayout) contentView.findViewById(R.id.lv1_layout);
        switch (flag) {
            case 1:
                myadapter = new MyAdapter(ChuangKeList.this, initArrayData(R.array.sub_chuangke));
                break;
            case 3:
                myadapter = new MyAdapter(ChuangKeList.this, initArrayData(R.array.sub_sorting_latest));
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
                                ckid = null;
                                ChuangKeList.clear();
                                MSListview.setAdapter(adapter);
//                                getExperimentList();
                                getCkList();
                                adapter.notifyDataSetChanged();
                            } else {
                                ckid = CK_Cate_List.get(position - 1).getId();
                                Logger.i("ckid:" + ckid);
                                ChuangKeList.clear();
                                MSListview.setAdapter(adapter);
//                                getExperimentList();
                                getCkList();
                                adapter.notifyDataSetChanged();
                            }
                            String name = (String) parent.getAdapter().getItem(position);
                            setHeadText(idx, name);
                            popupWindow.dismiss();

                            break;

                        case 3:
                            lv1_layout.getLayoutParams().width = LayoutParams.MATCH_PARENT; // 顺序
                            // 按最新
                            switch (position) {
                                case 0:
                                    desctype = 0;
                                    ChuangKeList.clear();
                                    MSListview.setAdapter(adapter);
//                                    getExperimentList();
                                    getCkList();
                                    adapter.notifyDataSetChanged();
                                    break;
                                case 1:
                                    desctype = 2; // 按访问记录 2
                                    ChuangKeList.clear();
                                    MSListview.setAdapter(adapter);
//                                    getExperimentList();
                                    getCkList();
                                    adapter.notifyDataSetChanged();

                                    break;
                                case 2:
                                    desctype = 3; // 按最多评论 3
                                    ChuangKeList.clear();
                                    MSListview.setAdapter(adapter);
//                                    getExperimentList();
                                    getCkList();
                                    adapter.notifyDataSetChanged();
                                    break;

                            }
                            // 当没有下级时直接将信息设置textview中
                            String name3 = (String) parent.getAdapter().getItem(position);
                            setHeadText(idx, name3);
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
     * @return void 返回类型
     * @Title: setHeadText
     * @Description: 点击之后设置在上边的TextView里
     * @author yimei
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
                String url = "http://www.shiyan360.cn/index.php/api/chuangke_category_list"; // 年级分类
                try {
                    Map<String, String> rawParams = new HashMap<String, String>();
                    CK_Cate_List = GetSortList.getckcateList(url, rawParams);

                } catch (Exception e) {
                    // TODO: handle exception
                }
            }

        }.start();

    }

    // 缓存中获取数据
    public void getCacheCollectdata() {
        ACache mCache = ACache.get(ChuangKeList.this);
        String value = mCache.getAsString("ChuangKeList");
        Logger.json(value);
        if (value != null) {
            com.alibaba.fastjson.JSONObject jsonobj = JSON.parseObject(value);
            com.alibaba.fastjson.JSONArray jsondata = jsonobj.getJSONArray("data");
            String array = JSON.toJSONString(jsondata);
            ChuangKeListEntities = JSON.parseArray(array, ChuangKe.class);
            mHandler.sendEmptyMessage(0);
        } else {
//			getExperimentList();
            getCkList();
        }
    }

    public void getCkList() {
        String desc_type = String.valueOf(desctype);
        String category_id = ckid;
        String paged = String.valueOf(page);
        APIWrapper.getInstance().getChuangke_list(desc_type, category_id, paged)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HttpResult<List<ChuangKe>>>() {
                    @Override
                    public void onCompleted() {
                        if (ChuangKeListEntities == null || ChuangKeListEntities.size() <= 0) {
                            mHandler.sendEmptyMessage(4);
                            Logger.i("mhander 4");
                        } else {
                            mHandler.sendEmptyMessage(0);
                            saveChuangKeDB();

                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                        mHandler.sendEmptyMessage(1);
                        Logger.i("mhander 1");
                    }

                    @Override
                    public void onNext(HttpResult<List<ChuangKe>> listHttpResult) {
                        ChuangKeListEntities = listHttpResult.getData();
//						ACache mCache = ACache.get(ChuangKeList.this);
//						mCache.put("ChuangKeList", listHttpResult.getData().toString());
                    }
                });
    }

    public void saveChuangKeDB() {

        List<ChuangKe> list = new ArrayList<>();
        for (int i = 0; i < ChuangKeListEntities.size(); i++) {
            ChuangKe chuangKelist = new ChuangKe();
            chuangKelist.setId(ChuangKeListEntities.get(i).getId());
            chuangKelist.setName(ChuangKeListEntities.get(i).getName());
            chuangKelist.setImg_url(ChuangKeListEntities.get(i).getImg_url());
            chuangKelist.setImg_url_s(ChuangKeListEntities.get(i).getImg_url_s());
            chuangKelist.setVideo_url(ChuangKeListEntities.get(i).getVideo_url());
            chuangKelist.setCateid(ChuangKeListEntities.get(i).getCateid());
            list.add(chuangKelist);
        }
        db.saveChuangKeLists(list);

    }

    public void readChuangKeDB() {
        if (!db.loadAllChuangKe().isEmpty()) {
            ChuangKeListEntities = db.loadAllChuangKeByOrder();
            mHandler.sendEmptyMessage(0);
        } else {
            getCkList();
        }
    }


}
