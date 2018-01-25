package cn.gdgst.palmtest.tab1.vote;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.gdgst.palmtest.API.APIWrapper;
import cn.gdgst.palmtest.Entitys.UserVote;
import cn.gdgst.palmtest.R;
import cn.gdgst.palmtest.bean.HttpResult;
import cn.gdgst.palmtest.utils.ToastUtil;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 投票系统主页
 */
public class VoteActivity extends AppCompatActivity implements MyRecyAdapter.Callbacks, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "VoteActivity";
    private RecyclerView mRecyclerView;
    private MyRecyAdapter mAdapter;
    private List<UserVote> userVoteList;
    private ProgressBar mProgressBar;
    private SwipeRefreshLayout mSwiRf;
    private EditText edtSearch;
    private Button btnSearch;
    private Button btnSort;
    private ImageView imageCancle;
    private int page = 1;
    private LruCache mLruCache;
    private ActionBar mActionBar;
    private String title;
    private String result;
    private long endTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        result = getIntent().getStringExtra("votedetail");
        if (result.equals("")){
            setContentView(R.layout.activity_test_a);
            mActionBar = getSupportActionBar();
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeButtonEnabled(true);
            mActionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
            mActionBar.setHomeAsUpIndicator(R.mipmap.ic_menu_back);
            return;
        }else {
            setContentView(R.layout.activity_vote);
        }
        try {
            JSONObject jsonObject = new JSONObject(result);
            title = jsonObject.getString("title");
//            endTime = 1516612488974L;
            endTime = jsonObject.getLong("endtime");
        } catch (JSONException e) {
        }
        mActionBar = getSupportActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setTitle(title);
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
        mActionBar.setHomeAsUpIndicator(R.mipmap.ic_menu_back);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        initData();
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        Log.e(TAG, System.currentTimeMillis()+"");
        mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        mSwiRf = (SwipeRefreshLayout) findViewById(R.id.mSwipeRefreshLayout);
        edtSearch = (EditText) findViewById(R.id.editinput);
        btnSearch = (Button) findViewById(R.id.btnsearch);
        btnSort = (Button) findViewById(R.id.btnsort);
        imageCancle = (ImageView) findViewById(R.id.imagecancle);
        btnSearch.setOnClickListener(this);
        btnSort.setOnClickListener(this);
        imageCancle.setOnClickListener(this);
        mProgressBar.setVisibility(View.VISIBLE);
        mSwiRf.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        mSwiRf.setOnRefreshListener(this);
        initAdapter();
    }

    private void initAdapter() {
        //        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        //设置布局管理器
        mRecyclerView.setLayoutManager(gridLayoutManager);
        //调价分割线
        mRecyclerView.addItemDecoration(new GridDividerDecoration(this));
        userVoteList = new ArrayList<>();
        mAdapter = new MyRecyAdapter(this, userVoteList, endTime);
        //让footView占满一行
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mAdapter.isPositionFooter(position) ? gridLayoutManager.getSpanCount(): 1;
            }
        });

        mAdapter.setCallback(this);
        mAdapter.setWithFooter(true);

        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 加载数据
     */
    private void initData() {
        getData(page);
    }

    @Override
    public void onClickLoadMore() {
        mAdapter.setWithFooter(false);
        page += 1;
       getData(page);
    }


    @Override
    public void onRefresh() {
        mAdapter.deleteData();
        getData(1);
        mSwiRf.setRefreshing(false);
        mAdapter.setWithFooter(true);
    }



    @Override
    public void onClick(View v) {
//        List<UserVote> cacheUserVote = null;
        switch (v.getId()){
            case R.id.btnsearch:
                sendSearch();
                break;
            case R.id.btnsort:
                Intent intent = new Intent(this, VoteSortActivity.class);
                startActivity(intent);
                break;
            case R.id.imagecancle:
                edtSearch.setText("");
                mSwiRf.setEnabled(true);
                mAdapter.deleteData();
                getData(page);
                /*cacheUserVote = new ArrayList<>();
                cacheUserVote = (List<UserVote>) mLruCache.get("mycache");
                if (!(cacheUserVote.size() <=0 || cacheUserVote == null))
                mAdapter.newData(cacheUserVote);*/
                break;
        }
    }

    /**
     * 回退结束当前页面
     * @return Boolean
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    /**
     * 网络获取数据  wangluohuoqushuju
     * @param page 页码
     */
    public void getData(int page){
        APIWrapper.getInstance().getUserVote(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Subscriber<HttpResult<List<UserVote>>>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        mProgressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCompleted() {
                        mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e instanceof HttpException){
                            ResponseBody responseBody = ((HttpException)e).response().errorBody();
                            try {
                                Log.e(TAG, "错误结果："+responseBody.string());
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onNext(HttpResult<List<UserVote>> listHttpResult) {
                        if (listHttpResult.getSuccess()){
                            userVoteList = listHttpResult.getData();
//                            mLruCache.put("mycache", userVoteList);
                            mAdapter.addData(userVoteList);
                        }else {
                            ToastUtil.show("加载失败……");
                        }
                    }
                });
    }

    /**
     * 点击搜索
     */
    private void sendSearch() {
        String keyWord = edtSearch.getText().toString();
        mSwiRf.setEnabled(false);
        APIWrapper.getInstance().getSearchUserVote(keyWord, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Subscriber<HttpResult<List<UserVote>>>() {

                    @Override
                    public void onStart() {
                        super.onStart();
                        mProgressBar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCompleted() {
                        mProgressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        try {
                            ToastUtil.show(((HttpException)e).response().errorBody().string());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }

                    @Override
                    public void onNext(HttpResult<List<UserVote>> listHttpResult) {
                        if (listHttpResult.getSuccess()){
                            userVoteList = listHttpResult.getData();
                            mAdapter.deleteData();
                            mAdapter.addData(userVoteList);
                        }else {
                            ToastUtil.show("没有内容……");
                        }
                    }
                });
    }

}
