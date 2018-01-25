package com.micexp.sdk;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.gdgst.palmtest.API.APIWrapper;
import cn.gdgst.palmtest.BuildConfig;
import cn.gdgst.palmtest.Entitys.UpdateInfo;
import cn.gdgst.palmtest.base.AppConstant;
import cn.gdgst.palmtest.base.BaseApplication;
import cn.gdgst.palmtest.bean.CollectionData;
import cn.gdgst.palmtest.bean.HttpResult;
import cn.gdgst.palmtest.service.CollectService;
import cn.gdgst.palmtest.service.UpdateInfoService;
import cn.gdgst.palmtest.utils.FileUtils;
import cn.gdgst.palmtest.utils.HttpUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    private static final String TAG = "ApplicationTest";
    public ApplicationTest() {
        super(Application.class);
    }

  public void testUpdateInfo() throws Exception {
        UpdateInfoService updateInfoService = new UpdateInfoService(getContext());
        Map<String , String> rawParams = new HashMap<String, String>();
        UpdateInfo updateInfo   = new UpdateInfo();
        rawParams.put("version_code", "2..2.0");
       // updateInfo = updateInfoService.getUpDateInfo(rawParams);
        Log.d("ApplicationTest", "更新返回的结果"+updateInfo.toString());
    }

   public void testUpdteInfo02() throws Exception {
        UpdateInfoService updateInfoService = new UpdateInfoService(getContext());
       // String version_code = String.valueOf(BuildConfig.VERSION_CODE);
       // updateInfoService.getUpDateInfoByRetrofit("2.2.0");
    }

    public void testGetaddInfo(){
        /*CollectService collectService = new CollectService(getContext());
        Map<String, String> rawparam = new HashMap<>();
        SharedPreferences sp = getContext().getSharedPreferences("userInfo", getContext().MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", "");
        rawparam.put("accessToken", accessToken);
        rawparam.put("model", "play");
        rawparam.put("id", "450");
        int result = collectService.getaddInfo(rawparam);
        Log.i("ApplicationText", "结果是否等于 == 2 ：" +result);*/

        SharedPreferences sp = getContext().getSharedPreferences(AppConstant.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", " ");

       APIWrapper.getInstance().getAddCollection(accessToken, "392", "play")
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(new Subscriber<HttpResult<List<CollectionData>>>() {
                   @Override
                   public void onCompleted() {
                        Log.i("Test","............");
                   }

                   @Override
                   public void onError(Throwable e) {

                   }

                   @Override
                   public void onNext(HttpResult<List<CollectionData>> listHttpResult) {
                       int errorCode = listHttpResult.getError_code();
                       Log.i("Test", "===errorCode==="+ errorCode);

                   }
               });
    }

    public void testHttpUtils(){
        String url = "http://www.shiyan360.cn/index.php/api/user_collect_add";
        SharedPreferences sp = getContext().getSharedPreferences(AppConstant.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", " ");
        Map<String, String> rawparam = new HashMap<>();
        rawparam.put("accessToken", accessToken);
        rawparam.put("model", "play");
        rawparam.put("id", "4028");
        String jsonResult = HttpUtil.postRequest(url, rawparam);
        JSONObject jsonObject = JSON.parseObject(jsonResult);
        String message = jsonObject.getString("message");
        int error_code = jsonObject.getInteger("error_code");
        String success = jsonObject.getString("success");
        Log.i("Test", "Message = "+message+",error_code="+error_code+", success = "+success);
    }

    public void testVedioToStream() throws IOException {
        //InputStream in = getContext().getClass().getClassLoader().getResourceAsStream("assets/" + "vedio/" + "record-1280x720-1488361960393.mp4");
        File file = new File(Environment.getExternalStorageDirectory(), "record-1280x720-1488361960393.mp4");
        ByteArrayInputStream bis = FileUtils.getByteArrayInputStream(file);
        Log.i("Test", "===文件===" + bis.available());
        Log.i("Test", "===文件===" + bis.toString());

    }

    public void testUserInfo(){
        SharedPreferences sp = getContext().getSharedPreferences(AppConstant.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
        String accessToken = sp.getString("accessToken", " ");
        Log.i("Test", "电话号码=="+accessToken);

        OkHttpClient client = new OkHttpClient();
        FormBody body = new FormBody.Builder()
                .add("accessToken", accessToken)
                .add("model", "play")
                .add("id", "4012")
                .build();
        Request request = new Request.Builder()
                .post(body)
                .url("http://www.shiyan360.cn/index.php/api/user_collect_add")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.i("Test", "===body==="+response.body().string());
            }
        });

    }

    public void testResetPass(){
        APIWrapper.getInstance().getSendCode("18877826701", "9389", "2")
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
                        Log.i("Test", httpResult.getError_code()+"");
                    }
                });
    }


}