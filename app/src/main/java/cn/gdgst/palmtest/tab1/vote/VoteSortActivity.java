package cn.gdgst.palmtest.tab1.vote;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import cn.gdgst.palmtest.API.APIWrapper;
import cn.gdgst.palmtest.Entitys.UserVote;
import cn.gdgst.palmtest.R;
import cn.gdgst.palmtest.bean.HttpResult;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 查看排名
 */
public class VoteSortActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "VoteSortActivity";
    public static final double SMALL_WIN_H_SCALE = 0.67;
    public static final double SMALL_WIN_W_SCALE = 0.72;

    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwrf;
    private ProgressBar mProgressBar;
    private VoteSortAdapter mAdapter;
    private List<UserVote> mDatas;
    private int page = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_sort);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("排行榜");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP, ActionBar.DISPLAY_HOME_AS_UP);
        actionBar.setHomeAsUpIndicator(R.mipmap.ic_edittext_delete);
        initData();
        initView();
    }

    private void initData() {
        getData(page);
    }


    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recclerview_sort);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_sort);
        mSwrf = (SwipeRefreshLayout) findViewById(R.id.swrf);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mDatas = new ArrayList<>();
        mAdapter = new VoteSortAdapter(mDatas, this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new GridDividerDecoration(this));
        mRecyclerView.addOnScrollListener(new MyRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                Log.e(TAG, currentPage+"");
                getData(currentPage);
            }
        });

        mSwrf.setOnRefreshListener(this);

        mRecyclerView.setAdapter(mAdapter);
    }
    private void getData(int page) {
        APIWrapper.getInstance().getSortResult(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io())
                .subscribe(new Subscriber<HttpResult<List<UserVote>>>() {
                    @Override
                    public void onCompleted() {
                        mProgressBar.setVisibility(View.GONE);
                        mSwrf.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(HttpResult<List<UserVote>> listHttpResult) {
                        mDatas = listHttpResult.getData();
                        mAdapter.addData(mDatas);
                    }
                });
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        overridePendingTransition(R.anim.activity_dialog_close_enter, R.anim.activity_dialog_close_exit);
        resizeActivity();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.activity_dialog_close_enter, R.anim.activity_dialog_close_exit);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void resizeActivity(){
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        setFinishOnTouchOutside(true);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        layoutParams.copyFrom(getWindow().getAttributes());
        layoutParams.gravity = Gravity.CENTER;
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        //layoutParams.x = 200;
        layoutParams.width = (int) (displayMetrics.widthPixels * SMALL_WIN_W_SCALE);
        layoutParams.height = (int) (displayMetrics.heightPixels * SMALL_WIN_H_SCALE);

        layoutParams.dimAmount = 0.7f;
        layoutParams.alpha = 1.0f;
        getWindow().setAttributes(layoutParams);

        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        View view = getWindow().getDecorView();
        if(view != null) {
            view.setBackgroundResource(R.drawable.dialog_activity_bg);
        }
    }

    @Override
    public void onRefresh() {
        mAdapter.deleteData();
        getData(page);
    }
}
