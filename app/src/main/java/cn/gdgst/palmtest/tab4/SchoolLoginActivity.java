
package cn.gdgst.palmtest.tab4;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.angmarch.views.NiceSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import cn.gdgst.palmtest.API.APIWrapper;
import cn.gdgst.palmtest.Entitys.AllSchool;
import cn.gdgst.palmtest.Entitys.UserEntity;
import cn.gdgst.palmtest.R;
import cn.gdgst.palmtest.base.AppConstant;
import cn.gdgst.palmtest.bean.HttpResult;
import cn.gdgst.palmtest.utils.Encrypt;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SchoolLoginActivity extends AppCompatActivity implements OnClickListener {
    private TextView tv_forget_pwd;

    private EditText user_name_edit, password_edit;
    private Button BtnMenulogin;
    private UserEntity user;

    private ProgressBar mProBar = null;
    private NiceSpinner niceSpinner;
    private List<AllSchool> allSchools = new ArrayList<>();
    private String table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schoollogin);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setTitle("师生登录");
        initView();
        getData();
    }

    private void getData() {

        APIWrapper.getInstance().getAllSchool()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HttpResult<List<AllSchool>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(HttpResult<List<AllSchool>> listHttpResult) {
                        allSchools = listHttpResult.getData();
                        List<String> dataset = new ArrayList<>();
                        dataset.add("请输入您所在的学校");
                        for (int i = 0; i < allSchools.size(); i++){
                            dataset.add(allSchools.get(i).getName());
                        }
                        niceSpinner.attachDataSource(dataset);
                    }
                });
    }

    /**
     * 初始化控件
     */
    private void initView() {
        tv_forget_pwd = (TextView) findViewById(R.id.tv_forget_pwd);
        user_name_edit = (EditText) findViewById(R.id.login_username);
        password_edit = (EditText) findViewById(R.id.login_password);
        BtnMenulogin = (Button) findViewById(R.id.login);
        tv_forget_pwd.setOnClickListener(this);
        BtnMenulogin.setOnClickListener(this);
        mProBar = this.createProgressBar();

        niceSpinner = (NiceSpinner) findViewById(R.id.nice_spinner);

        niceSpinner.addOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0)
                table =  allSchools.get(position -1).getPinyin();
            }
        });

    }

    @Override
    public void onClick(View v) {
        String username = user_name_edit.getText().toString();
        switch (v.getId()) {
            case R.id.login:
                // 1. 通过规则判断手机号
                if (!judgePhoneNums(username)) {
                    return;
                } else if (password_edit.getText().toString().trim().equals("")) {
                    Toast.makeText(this, "密码不能为空", Toast.LENGTH_LONG).show();
                } else {
                    mProBar.setVisibility(View.VISIBLE);
                    String usernameTest = user_name_edit.getText().toString();
                    String passwordTest = password_edit.getText().toString();
                    String passwordValid = Encrypt.md5(passwordTest);

//                    Log.i("LoginActivity", passwordValid);

                    autoLogin(usernameTest, passwordValid);
                    /*Thread loginThread = new Thread(new LoginThread());
                    loginThread.start();*/
                }

                break;
            case R.id.tv_forget_pwd:
                Intent intent2 = new Intent();
                intent2.setClass(this, ResetPasswordActivity.class);
                startActivity(intent2);
                break;

            default:
                break;
        }
    }


    /**
     * 判断手机号码是否合理
     *
     * @param phoneNums
     */
    private boolean judgePhoneNums(String phoneNums) {
        if (isMatchLength(phoneNums, 11) ) {    //&& isMobileNO(phoneNums):注释这个为了能让VIP卡登陆
            return true;
        }
        Toast.makeText(this, "账号码输入有误！", Toast.LENGTH_SHORT).show();
        return false;
    }

    /**
     * 判断一个字符串的位数
     *
     * @param str
     * @param length
     * @return
     */
    public static boolean isMatchLength(String str, int length) {
        if (str.isEmpty()) {
            return false;
        } else {
            return str.length() <= length && str.length() >= 6 ? true : false;   //放宽登陆账号的范围，为了可以让VIP卡账号也可以登录
        }
    }


    /**
     * 圆型进度显示条
     * progressbar
     */
    private ProgressBar createProgressBar() {
        FrameLayout layout = (FrameLayout) findViewById(android.R.id.content);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        ProgressBar mProBar = new ProgressBar(this);
        mProBar.setLayoutParams(layoutParams);
        mProBar.setVisibility(View.GONE);
        layout.addView(mProBar);
        return mProBar;
    }

    /**
     * 登录方法
     * @param username  用户的手机号
     * @param password  用户的密码
     */
    private void autoLogin(String username, final String password) {
        mProBar.setVisibility(View.VISIBLE);
        Observable<HttpResult<UserEntity>> observable = APIWrapper.getInstance().login(username, password, table);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<HttpResult<UserEntity>>() {
                    @Override
                    public void onCompleted() {
                        mProBar.setVisibility(View.GONE);
                        Log.i("LoginActivity","登录返回来的数据："+user.toString());

                    }

                    @Override
                    public void onError(Throwable e) {
                        mProBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(HttpResult<UserEntity> userEntityHttpResult) {
                        //UserEntity userEntity1 = userEntityHttpResult.getData();
                        String message = userEntityHttpResult.getMessage();
                        boolean success = userEntityHttpResult.getSuccess();
                        if (success) {
                           // UserEntity userEntity = userEntityHttpResult.getData();
                            user = userEntityHttpResult.getData();
                            SharedPreferences sharedPreferences_Save = getSharedPreferences(AppConstant.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
                            Editor editor = sharedPreferences_Save.edit();
                            editor.putString("id", user.getId());
                            editor.putString("passwordMd5", password);
                            editor.putString("nickname", user.getNickname());
                            editor.putString("name", user.getName());
                            editor.putString("avatar", user.getAvatar());
                            editor.putInt("sex", user.getSex());
                            editor.putString("type", user.getType());
                            editor.putString("school", user.getSchool());
                            editor.putInt("status", user.getStatus());
                            editor.putString("accessToken", user.getAccessToken());
                            editor.putString("teacher", user.getTeacher());
                            editor.putString("banji", user.getBanji());
                            editor.commit();//注意此处,一定要提交
                            startActivity(new Intent(SchoolLoginActivity.this, UserInfoActivity.class));
                            //关闭当前页面释放空间
                            finish();
                        } else {
                            Toast.makeText(SchoolLoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

}