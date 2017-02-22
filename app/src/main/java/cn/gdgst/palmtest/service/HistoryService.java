package cn.gdgst.palmtest.service;

import android.content.Context;

import com.orhanobut.logger.Logger;
import cn.gdgst.palmtest.API.APIWrapper;
import cn.gdgst.palmtest.bean.HttpResult;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Don on 2016/7/29.
 */
public class HistoryService {
    private Boolean addTag=false;
    private  Boolean deleteTag=false;
    public HistoryService(Context context) throws Exception {
    }
    public Boolean  addHistory(String accessToken,String id,String model){
                APIWrapper.getInstance().getAddHistoryList(accessToken, id, model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HttpResult>() {
                    @Override
                    public void onCompleted() {

                    }
                    @Override
                    public void onError(Throwable e) {

                    }
                    @Override
                    public void onNext(HttpResult httpResult) {

                        addTag=httpResult.getSuccess();
                        Logger.i("addTag:"+addTag);
                    }

    });
        return addTag;
    }

    //删除浏览记录
    public Boolean  deleteHistory(String accessToken,String id,String model){
        APIWrapper.getInstance().getAddHistoryList(accessToken, id, model)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HttpResult>() {
                    @Override
                    public void onCompleted() {

                    }
                    @Override
                    public void onError(Throwable e) {

                    }
                    @Override
                    public void onNext(HttpResult httpResult) {

                        addTag=httpResult.getSuccess();
                        Logger.i("addTag:"+addTag);
                    }

                });
        return addTag;
    }
}
