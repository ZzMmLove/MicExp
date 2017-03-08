package cn.gdgst.palmtest.main;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.orhanobut.logger.Logger;

import cn.gdgst.palmtest.BuildConfig;
import cn.gdgst.palmtest.R;
import cn.gdgst.palmtest.Entitys.UpdateInfo;
import cn.gdgst.palmtest.Entitys.UserEntity;
import cn.gdgst.palmtest.base.AppConstant;
import cn.gdgst.palmtest.service.UpdateInfoService;
import cn.gdgst.palmtest.tab4.AboutActivity;
import cn.gdgst.palmtest.tab4.FeedBackActivity;
import cn.gdgst.palmtest.tab4.LoginActivity;
import cn.gdgst.palmtest.tab4.UserInfoActivity;
import cn.gdgst.palmtest.tab4.history.HistoryList;
import cn.gdgst.palmtest.tab4.shoucang.GetCollectListActivity;
import cn.gdgst.palmtest.utils.HttpUtil;
import cn.gdgst.palmtest.utils.NetworkCheck;
import cn.gdgst.palmtest.utils.NetworkCheckDialog;

import org.afinal.simplecache.ACache;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Tab4Activity extends Activity implements OnClickListener {
    private final int AUTO_LOGIN_FALSE = 0;
    private RelativeLayout rl_account_manager, rl_switch_about, rl_feedback, rl_switch_collect, rl_switch_cache, rl_shared, rl_update, rl_history;
    private TextView tv_cache;
    /**
     * 初始化sharedPreferences
     */
    private SharedPreferences sp = null;
    // 登录url
    private String urlStr = "http://www.shiyan360.cn/index.php/api/user_login";
    private String accessToken;
    // 更新版本要用到的一些信息
    private UpdateInfo info;
    private ProgressDialog pBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        setContentView(R.layout.activity_tab4);
        rl_account_manager = (RelativeLayout) findViewById(R.id.rl_account_manager);
        rl_switch_about = (RelativeLayout) findViewById(R.id.rl_switch_about);
        rl_feedback = (RelativeLayout) findViewById(R.id.rl_feedback);
        rl_switch_collect = (RelativeLayout) findViewById(R.id.rl_switch_collect);
        rl_switch_cache = (RelativeLayout) findViewById(R.id.rl_switch_cache);
        rl_shared = (RelativeLayout) findViewById(R.id.rl_shared);
        rl_history = (RelativeLayout) findViewById(R.id.rl_history);

        tv_cache = (TextView) findViewById(R.id.tv_cache);
        rl_update = (RelativeLayout) findViewById(R.id.rl_update);
        rl_account_manager.setOnClickListener(this);
        rl_switch_about.setOnClickListener(this);
        rl_feedback.setOnClickListener(this);
        rl_switch_collect.setOnClickListener(this);
        rl_switch_cache.setOnClickListener(this);
        rl_shared.setOnClickListener(this);
        rl_update.setOnClickListener(this);
        rl_history.setOnClickListener(this);
    }

    Handler tab4ActivityHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case AUTO_LOGIN_FALSE:
                    String obj = (String) msg.obj;
                    Log.d("Tab4Activity", "Handler中的message值:"+obj);
                    Toast.makeText(Tab4Activity.this,obj, Toast.LENGTH_SHORT).show();
                    Intent intent_Login_Activity = new Intent(Tab4Activity.this, LoginActivity.class);
                    startActivity(intent_Login_Activity);
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /**
             * 账号管理
             */
            case R.id.rl_account_manager:
                sp = getSharedPreferences(AppConstant.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
                String accessToken = sp.getString("accessToken", null);
                if (accessToken == null || accessToken.equals("")) {
                    Intent intent = new Intent();
                    intent.setClass(Tab4Activity.this, LoginActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(Tab4Activity.this, UserInfoActivity.class);
                    startActivity(intent);
                }
                /*boolean autologin = sp.getBoolean("autoLogin", false);
                Logger.v("autologin:" + autologin);
                if (autologin == true) {
                    //开启线程去自动登录
                    Thread autologinThread = new Thread(new autoLoginThread());
                    autologinThread.start();
                } else {
                    Intent intent = new Intent();
                    intent.setClass(Tab4Activity.this, LoginActivity.class);
                    startActivity(intent);
                }*/
                break;
            case R.id.rl_switch_about:
                Intent intent1 = new Intent();
                intent1.setClass(Tab4Activity.this, AboutActivity.class);
                startActivity(intent1);

                break;
            case R.id.rl_feedback:
                Intent intent2 = new Intent();
                intent2.setClass(Tab4Activity.this, FeedBackActivity.class);
                startActivity(intent2);

                break;
            case R.id.rl_switch_collect:
                Intent intent3 = new Intent();
                intent3.setClass(Tab4Activity.this, GetCollectListActivity.class);
                startActivity(intent3);
                break;
            case R.id.rl_switch_cache:
                showDialog();

                break;
            case R.id.rl_shared:
                showQrcode();

                break;
            case R.id.rl_history:
				Intent intent4 = new Intent();
				intent4.setClass(Tab4Activity.this, HistoryList.class);
				startActivity(intent4);

                //视频录制
//                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
//                int sizeLimit = 1024;
//                intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, sizeLimit);
//                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60);
//                startActivityForResult(intent, 3333);


                break;
            case R.id.rl_update:
                Toast.makeText(Tab4Activity.this, "正在检查更新", Toast.LENGTH_SHORT).show();
                new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(500);
                            UpdateInfoService updateInfoService = new UpdateInfoService(
                                    Tab4Activity.this);

                            SharedPreferences sp = getSharedPreferences(AppConstant.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
                            String version_code = BuildConfig.VERSION_NAME;
                            Map<String, String> rawParams = new HashMap<String, String>();
                            rawParams.put("version_code", version_code);
                            info = updateInfoService.getUpDateInfo(rawParams);
                            handler.sendEmptyMessage(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }.start();
                break;
            default:
                break;
        }

    }

    // autoLoginThread线程类
    class autoLoginThread implements Runnable {

        @Override
        public void run() {
            autoLogin();
        }
    }

    private void autoLogin() {
        final String auto_user_name = sp.getString("username", "");
        final String auto_user_password = sp.getString("password", "");
        if (auto_user_name != "" && auto_user_name != null && auto_user_password != "" && auto_user_password != null) {
            NetworkCheck check = new NetworkCheck(Tab4Activity.this);
            boolean isalivable = check.Network();
            if (isalivable) {
                // 封装请求参数
                Map<String, String> rawParams = new HashMap<String, String>();
                rawParams.put("user_name", auto_user_name);
                rawParams.put("user_pass", auto_user_password);
                try {
                    // 设置请求参数项， 发送请求返回json
                    String json = HttpUtil.postRequest(urlStr, rawParams);
                    Log.d("Tab4Activity", json);

                    // 解析json数据
                    org.json.JSONObject jsonObject_my = new org.json.JSONObject(json);
                    com.alibaba.fastjson.JSONObject jsonobj = JSON.parseObject(json);
                    Boolean response = (Boolean) jsonobj.get("success");
                    String message = (String) jsonobj.get("message");


                    if (response) {
                        // 解析截取“data”中的accessToken

                        org.json.JSONObject object_data = jsonObject_my.getJSONObject("data");
                        //org.json.JSONObject object_data = (org.json.JSONObject) jsonobj.get("data");
                        UserEntity user = new UserEntity();
                        Log.d("Tab4Activity", "response和message的结果:"+response+":"+message+"object_data的结果:"+object_data.toString());
                        //用户在服务器中的id
                        String id = object_data.getString("id");
                        user.setId(id);
                        //用户的昵称
                        String nickname = object_data.getString("nickname");
                        user.setNickname(nickname);
                        //用户的真实姓名
                        String name = object_data.getString("name");
                        user.setName(name);
                        //用户在服务器的头像的文件名路径
                        String avatar = object_data.getString("avatar");
                        user.setAvatar(avatar);
                        //用户的性别
                        int sex = object_data.getInt("sex");
                        user.setSex(sex);
                        //用户的身份
                        int status = object_data.getInt("status");
                        user.setStatus(status);
                        //用户的访问令牌
                        String accessToken = object_data.getString("accessToken");
                        user.setAccessToken(accessToken);

                        Log.d("Tab4Activity", "手动解析登录结果:"+user.toString());

                        /*org.json.JSONObject jsonObject = (org.json.JSONObject) jsonobj.get("data");
                        String array = JSON.toJSONString(jsonObject);
                        UserEntity userarray = JSON.parseObject(array, UserEntity.class);*/

                        // 保存用户登录信息
                        sp = getSharedPreferences(AppConstant.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
                        Editor editor = sp.edit();// 获取编辑器
                        editor.putString("id" ,id);//保存用户的id
                        editor.putString("nickname", nickname);//保存用户的昵称
                        editor.putString("name", name);//保存用户的真实姓名
                        editor.putString("avatar", avatar);//保存用户的头像路径
                        editor.putInt("sex", sex);//保存用户的性别
                        editor.putInt("status", status);//保存用户的身份
                        editor.putString("accessToken", accessToken);//保存用户的访问令牌
                        editor.commit();// 提交修改

                        Bundle bundle = new Bundle();
                        bundle.putString("username", auto_user_name);
                        bundle.putSerializable("user_info", user);
                        Intent intent = new Intent(Tab4Activity.this, UserInfoActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    } else if(response == false) {
                        Log.d("Tab4Activity", "(2)登录时返回的response值:"+String.valueOf(response)+";message值:"+message);
                        String response_message = (String) jsonobj.get("message");
                        Message message1 = new Message();
                        message1.what = AUTO_LOGIN_FALSE;
                        message1.obj = response_message;
                        tab4ActivityHandler.sendMessage(message1);
                    }

                } catch (Exception e) {
                }
            } else {
                NetworkCheckDialog.dialog(Tab4Activity.this);
            }

        } else {
            Toast.makeText(Tab4Activity.this, "自动登录失败，请手动登录", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 这是兼容的 AlertDialog
     * 这里使用了 android.support.v7.app.AlertDialog.Builder
     * 可以直接在头部写 import android.support.v7.app.AlertDialog
     * 那么下面就可以写成 AlertDialog.Builder
     */
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Tab4Activity.this);
        builder.setInverseBackgroundForced(false);
        builder.setTitle("清除缓存");
        builder.setMessage("确定清除缓存数据？");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                ACache mCache = ACache.get(Tab4Activity.this);
                mCache.clear();
                Toast.makeText(Tab4Activity.this, "清理完成", Toast.LENGTH_SHORT).show();
            }
        });
        builder.create().show();
    }

    private void showQrcode() {
        LinearLayout layout = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);  //image的布局方式
//		lp.setMargins(10, 10, 10, 10);
        layout.setPadding(60, 60, 60, 20);
        ImageView iv = new ImageView(Tab4Activity.this);
        iv.setImageResource(R.mipmap.xhdpi_qrcode);
        layout.setGravity(Gravity.CENTER);
        iv.setLayoutParams(lp);
        layout.addView(iv);

        AlertDialog.Builder builder = new AlertDialog.Builder(Tab4Activity.this);
        builder.setTitle("扫描二维码，下载实验掌上通");
        builder.setPositiveButton("其他方式分享", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_SEND); // 启动分享发送的属性
                intent.setType("text/plain"); // 分享发送的数据类型
//				String msg = "我正在使用《实验掌上通》，挺不错的应用。你也来试试吧！下载地址："+info.getUpgrade_url();
                String msg = "我正在使用《实验掌上通》，挺不错的应用。你也来试试吧！下载地址：http://a.app.qq.com/o/simple.jsp?pkgname=cn.gdgst.palmtest";
                intent.putExtra(Intent.EXTRA_TEXT, msg); // 分享的内容
                startActivity(Intent.createChooser(intent, "选择分享"));// 目标应用选择对话框的标题
            }
        });
        builder.setView(layout);
        builder.create().show();
    }

    // 广告移动处理
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    //更新
                    if (isNeedUpdate()) {
                        showUpdateDialog();
                    } else {
                        Toast.makeText(Tab4Activity.this, "已经是最新版本啦", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    //更新版本
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//					builder.setTitle("请升级APP至版本" + info.getVersion());
        builder.setCancelable(false);
        builder.setTitle("检测到有新的版本");
        builder.setMessage(info.getUpgrade_remark());
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

//							open();

                if (Environment.getExternalStorageState().equals(
                        Environment.MEDIA_MOUNTED)) {
                    downFile(info.getUpgrade_url());
                } else {
                    Toast.makeText(Tab4Activity.this, "SD卡不可用，请插入SD卡",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }

        });
        builder.create().show();
    }

    private boolean isNeedUpdate() {

        int v = info.getIs_upgrade(); // 最新版本的版本号
//					Toast.makeText(Tab1Activity.this, v, Toast.LENGTH_SHORT).show();
        if (v == 0) {
            return false;
        } else {
            return true;
        }
    }

    // 获取当前版本的版本号
    private String getVersion() {
        try {
            PackageManager packageManager = getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    getPackageName(), 0);
            return packageInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return "版本号未知";
        }
    }

    void downFile(final String url) {
        pBar = new ProgressDialog(Tab4Activity.this);    //进度条，在下载的时候实时更新进度，提高用户友好度
        pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pBar.setTitle("正在下载");
        pBar.show();
        pBar.setProgress(0);
        new Thread() {
            public void run() {
                HttpClient client = new DefaultHttpClient();
                HttpGet get = new HttpGet(url);
                HttpResponse response;
                try {
                    response = client.execute(get);
                    HttpEntity entity = response.getEntity();
                    int length = (int) entity.getContentLength();   //获取文件大小
//			                                        pBar.setMax(length);                            //设置进度条的总长度
                    InputStream is = entity.getContent();
                    FileOutputStream fileOutputStream = null;
                    if (is != null) {
                        File file = new File(
                                Environment.getExternalStorageDirectory(),
                                "MicExp.apk");
                        fileOutputStream = new FileOutputStream(file);
                        //这个是缓冲区，即一次读取10个比特，我弄的小了点，因为在本地，所以数值太大一下就下载完了,
                        //看不出progressbar的效果。
                        byte[] buf = new byte[1024 * 100];
                        int ch = -1;
                        int process = 0;
                        while ((ch = is.read(buf)) != -1) {
                            fileOutputStream.write(buf, 0, ch);
                            process += ch;
                            pBar.setProgress(process / (1024 * 100));       //这里就是关键的实时更新进度了！
                        }

                    }
                    fileOutputStream.flush();
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                    down();
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

    void down() {
        handler.post(new Runnable() {
            public void run() {
                pBar.cancel();
                update();
            }
        });
    }

    void update() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory(), "MicExp.apk")),
                "application/vnd.android.package-archive");
        startActivity(intent);

    }

}