package cn.gdgst.palmtest.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import cn.gdgst.palmtest.API.APIWrapper;
import cn.gdgst.palmtest.Entitys.UserEntity;
import cn.gdgst.palmtest.base.AppConstant;
import cn.gdgst.palmtest.bean.HttpResult;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 用户登录工具
 * Created by JenfeeMa on 2017/3/1.
 */

public class UserLoginUtil {
    private Context context;

    public UserLoginUtil(Context context) {
        this.context = context;
    }

    public void autoLogin(String user_name, String user_pass) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppConstant.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
        Observable<HttpResult<UserEntity>> observable = APIWrapper.getInstance().login(user_name, user_pass);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HttpResult<UserEntity>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(HttpResult<UserEntity> userEntityHttpResult) {
                        boolean success = userEntityHttpResult.getSuccess();
                        if (success) {
                            UserEntity userEntity = userEntityHttpResult.getData();
                            SharedPreferences sharedPreferences_save = context.getSharedPreferences(AppConstant.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences_save.edit();
                            editor.putString("id", userEntity.getId());
                            editor.putString("nickname", userEntity.getNickname());
                            editor.putString("name", userEntity.getName());
                            editor.putString("avatar", userEntity.getAvatar());
                            editor.putInt("sex", userEntity.getSex());
                            editor.putString("type", userEntity.getType());
                            editor.putString("school", userEntity.getSchool());
                            editor.putInt("status", userEntity.getStatus());
                            editor.putString("accessToken", userEntity.getAccessToken());
                            editor.putString("banji", userEntity.getBanji());
                            editor.putString("teacher", userEntity.getTeacher());
                            editor.commit();
                            Log.d("UserLoginUtil", "用户数据信息保存完毕:"+userEntity.toString());
                        }
                    }
                });
    }
}
