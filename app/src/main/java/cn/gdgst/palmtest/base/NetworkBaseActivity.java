package cn.gdgst.palmtest.base;

import android.os.Bundle;
import android.os.Handler;

import cn.gdgst.palmtest.imagecache.AsyncBaseRequest;
import cn.gdgst.palmtest.imagecache.DefaultThreadPool;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述：应用中需要访问网络的Activity基类
 * @author android_ls
 */
public abstract class NetworkBaseActivity extends BaseActivity {

    /**
     * 当前activity所持有的所有网络请求
     */
    protected List<AsyncBaseRequest> mAsyncRequests;

    /**
     * 当前activity所持有的Handler
     */
    protected Handler mHandler;

    /**
     * 当前activity所持有的线程池对象
     */
    protected DefaultThreadPool mDefaultThreadPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preliminary();
        super.onCreate(savedInstanceState);
    }

    /**
     * 向用户展示信息前的准备工作在这个方法里处理
     */
    protected void preliminary() {
        mHandler = new Handler();
        mAsyncRequests = new ArrayList<AsyncBaseRequest>();
        mDefaultThreadPool = DefaultThreadPool.getInstance();
    }
    public List<AsyncBaseRequest> getAsyncRequests() {
        return mAsyncRequests;
    }

    public Handler getHandler() {
        return mHandler;
    }

    public DefaultThreadPool getDefaultThreadPool() {
        return mDefaultThreadPool;
    }

    @Override
    protected void onPause() {
        /**
         * 在activity暂停的时候，同时设置终止标识，终止异步线程
         */
        cancelAllRequest();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        /**
         * 在activity销毁的时候，同时设置终止标识，终止异步线程
         */
        cancelAllRequest();
        super.onDestroy();
    }

    /**
     * 取消当前Activity相关的网络请求
     */
    private void cancelAllRequest() {
        if (mAsyncRequests != null && mAsyncRequests.size() > 0) {
            try {
                int size = mAsyncRequests.size();
                for (int i = 0; i < size; i++) {
                    AsyncBaseRequest request = mAsyncRequests.get(i);
                    Thread thread = new Thread(request);
                    if (thread.isAlive() || !Thread.interrupted()) {
                        request.setInterrupted(true);
                    }

                    HttpURLConnection conn = request.getRequestConn();
                    if (conn != null) {
                        // conn.disconnect();
                        System.err.println("onDestroy disconnect URL: " + conn.getURL());
                    }

                    mAsyncRequests.remove(request);
                    size = mAsyncRequests.size();
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}
