
package cn.gdgst.palmtest.tab4;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


import cn.gdgst.palmtest.API.APIWrapper;
import cn.gdgst.palmtest.Entitys.UserEntity;
import cn.gdgst.palmtest.R;

import cn.gdgst.palmtest.base.AppConstant;

import cn.gdgst.palmtest.bean.HttpResult;
import cn.gdgst.palmtest.utils.Encrypt;

import cn.gdgst.palmtest.utils.HttpUtil;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements OnClickListener {
    private TextView schoolLogin, tv_forget_pwd;

    private EditText user_name_edit, password_edit;
    private Button BtnMenulogin;
    private String responseMsg = "";
    private String accessToken;
    private UserEntity user;

    private ProgressBar mProBar = null;
    /**
     * 初始化sharedPreferences
     */
    private SharedPreferences sp = null;

    // 登录url
    private String urlStr = "http://shiyan360.cn/index.php/api/user_login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("登录");
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_login, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * 注册跳转菜单选项
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_login_register:
                Intent intent_register = new Intent(this, RegisterActivity.class);
                startActivity(intent_register);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        tv_forget_pwd = (TextView) findViewById(R.id.tv_forget_pwd);
        user_name_edit = (EditText) findViewById(R.id.login_username);
        password_edit = (EditText) findViewById(R.id.login_password);
        BtnMenulogin = (Button) findViewById(R.id.login);
        schoolLogin = (TextView) findViewById(R.id.otherlogin);
        mProBar = this.createProgressBar();
        tv_forget_pwd.setOnClickListener(this);
        schoolLogin.setOnClickListener(this);
        BtnMenulogin.setOnClickListener(this);
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

                    Log.i("LoginActivity", passwordValid);

                    autoLogin(usernameTest, passwordValid);
                    /*Thread loginThread = new Thread(new LoginThread());
                    loginThread.start();*/
                }

                break;
            case R.id.tv_forget_pwd:
                Intent intent2 = new Intent(this, ResetPasswordActivity.class);
                startActivity(intent2);
                break;
            case R.id.otherlogin:
                startActivity(new Intent(this, SchoolLoginActivity.class));

            default:
                break;
        }
    }

    // LoginThread线程类
    class LoginThread implements Runnable {

        @Override
        public void run() {

            String username = user_name_edit.getText().toString();
            String password = password_edit.getText().toString();

            // URL合法，但是这一步并不验证密码是否正确
            boolean loginValidate = loginServer(username, password);
            Message msg = handler.obtainMessage();
            if (loginValidate) {
                if (responseMsg.equals("true")) {
                    msg.what = 0;
                    handler.sendMessage(msg);
                }
            } else {
                msg.what = 1;
                handler.sendMessage(msg);
            }
        }
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(getApplicationContext(), "登录成功！", Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    bundle.putString("username", user_name_edit.getText().toString());
                    bundle.putSerializable("user_info", user);
                    Intent intent = new Intent(LoginActivity.this, UserInfoActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                    break;
                case 1:
                    mProBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "登录失败,请检查用户名密码是否正确", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    // 登陆线程,现在没有用这种请求方式
    private boolean loginServer(String username, String password) {
        boolean loginValidate = false;
        // 使用apache HTTP客户端实现

        username = user_name_edit.getText().toString().trim();
        password = Encrypt.md5(password_edit.getText().toString()).trim();

        // 封装请求参数
        Map<String, String> rawParams = new HashMap<String, String>();
        rawParams.put("user_name", username);
        rawParams.put("user_pass", password);
        Logger.i("请求登录的user_name:" + username);
        Logger.i("请求登录的user_pass:" + password_edit.getText().toString());
        Logger.i("请求登录的加密user_pass:" + password);


        try {
            // 设置请求参数项
            // 发送请求返回json
            String json = HttpUtil.postRequest(urlStr, rawParams);
            Log.d("LoginActivity", "登录返回的JSON结果:"+json);
            Logger.json(json);
            // 解析json数据
            com.alibaba.fastjson.JSONObject jsonobj = JSON.parseObject(json);
            Boolean response = (Boolean) jsonobj.get("success");

            // 解析截取“data”中的accessToken
            com.alibaba.fastjson.JSONObject jsondata = (JSONObject) jsonobj.get("data");
            String array = JSON.toJSONString(jsondata);
            user = JSON.parseObject(array, UserEntity.class);
            Log.d("LoginActivity", "用户的详细信息："+user.toString());

           accessToken = user.getAccessToken();

            Logger.v("accessToken:" + accessToken);
            Logger.i("LoginActivity" + user.getType());

            if (response) {
                loginValidate = true;
                responseMsg = response.toString();

                // 保存用户登录信息
                sp = getSharedPreferences(AppConstant.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
                Editor editor = sp.edit();// 获取编辑器
                editor.putString("id", user.getId());
                editor.putString("j", username);
                editor.putString("password", password);
                editor.putString("avatar", user.getAvatar());
                editor.putString("nickname", user.getNickname());
                editor.putString("name", user.getName());
                editor.putInt("sex", user.getSex());
                editor.putString("type", user.getType());//用户身份
                editor.putString("school", user.getSchool());
                editor.putString("banji", user.getBanji());
                editor.putString("teacher", user.getTeacher());
                editor.putString("accessToken", accessToken);
                // 设置自动登陆
                editor.putBoolean("autoLogin", true);

                editor.commit();// 提交修改
                Logger.v("sp保存editor：" + editor);
                Logger.v("sp保存：" + "用户名：" + username + "密码：" + password + "token:" + accessToken);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return loginValidate;
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
     * 验证手机格式
     */
    public static boolean isMobileNO(String mobileNums) {
        /*
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
         * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
         * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
         */
        String telRegex = "[1][358]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobileNums))
            return false;
        else
            return mobileNums.matches(telRegex);
    }

    /**
     * 圆型进度显示条
     * progressbar
     */
    private ProgressBar createProgressBar() {
        FrameLayout layout = (FrameLayout) findViewById(android.R.id.content);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
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
        Observable<HttpResult<UserEntity>> observable = APIWrapper.getInstance().login(username, password, "");
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
                            SharedPreferences.Editor editor = sharedPreferences_Save.edit();
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
                            startActivity(new Intent(LoginActivity.this, UserInfoActivity.class));
                            //关闭当前页面释放空间
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}