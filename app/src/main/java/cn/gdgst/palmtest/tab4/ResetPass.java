package cn.gdgst.palmtest.tab4;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import cn.gdgst.palmtest.API.APIWrapper;
import cn.gdgst.palmtest.R;
import com.orhanobut.logger.Logger;

import cn.gdgst.palmtest.bean.HttpResult;
import cn.gdgst.palmtest.utils.Encrypt;
import cn.gdgst.palmtest.utils.HttpUtil;
import cn.gdgst.palmtest.utils.NetworkCheck;
import cn.gdgst.palmtest.utils.NetworkCheckDialog;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ResetPass extends AppCompatActivity implements OnClickListener {

	private Button btnSub;
	private EditText password_edit;
	private String username, smscode;
	private String responseMsg = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resetpass);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("重置密码");
		InitView();
	}

	private void InitView() {
		btnSub = (Button) findViewById(R.id.new_btn_submit);
		password_edit = (EditText) findViewById(R.id.new_password);

		btnSub.setOnClickListener(this);

		username = getIntent().getStringExtra("user_name");
		//smscode = bundle.getString("smscode");
		Logger.v( "传递取得的:" + username);
		Logger.v( "传递取得的:" + smscode);

	}

	@Override
	public void onClick(View v) {
		if (!TextUtils.isEmpty(password_edit.getText().toString().trim())) {
			/*Thread RegThread = new Thread(new RegisterThread());
				RegThread.start();*/
			NetworkCheck check = new NetworkCheck(this);
			boolean isAliable = check.Network();
			if (isAliable){
				String md5Pass = Encrypt.md5(password_edit.getText().toString().trim());
				APIWrapper.getInstance().getResetPass(username, smscode, md5Pass)
						.subscribeOn(Schedulers.io())
						.observeOn(AndroidSchedulers.mainThread())
						.subscribe(new Subscriber<HttpResult>() {
							@Override
							public void onCompleted() {

							}

							@Override
							public void onError(Throwable e) {
								handler.sendEmptyMessage(2);
							}

							@Override
							public void onNext(HttpResult httpResult) {
								if (httpResult.getSuccess()){
									handler.sendEmptyMessage(3);
								}else {
									handler.sendEmptyMessage(1);
								}
							}
						});
			}else {
				NetworkCheckDialog.dialog(this);
			}
		} else {
			Toast.makeText(this, "密码不能为空", Toast.LENGTH_LONG).show();
		}
	}

	// RegisterThread主线程类
	class RegisterThread implements Runnable {
		@Override
		public void run() {

			String password = Encrypt.md5(password_edit.getText().toString());
			boolean registerValidate = registerServer(password);

			Message msg = handler.obtainMessage();
			if (registerValidate) {
				if (responseMsg.equals("true")) {
					msg.what = 3;
					handler.sendMessage(msg);
				} else {
					msg.what = 1;
					handler.sendMessage(msg);
				}

			} else {
				msg.what = 2;
				handler.sendMessage(msg);
			}
		}

	}

	// 注册服务 方式二
	private boolean registerServer(String password) {
		boolean loginValidate = false;
		// 使用apache HTTP客户端实现
		String urlStr = "http://shiyan360.cn/index.php/api/find_pass";
		password = Encrypt.md5(password_edit.getText().toString()).trim();
		NetworkCheck check = new NetworkCheck(ResetPass.this);
		boolean isalivable = check.Network();
		if (isalivable) {
			// 封装请求参数
			Map<String, String> rawParams = new HashMap<String, String>();
			rawParams.put("user_name", username);
			rawParams.put("user_code", smscode);
			rawParams.put("user_pass", password);

			Logger.i( "请求提交的user_name:" + username);
			Logger.i( "请求提交的user_code:" + smscode.length());
			Logger.i( "请求提交的user_pass:" + password);
			try {
				// 设置请求参数项
				// 发送请求返回json
				String json = HttpUtil.postRequest(urlStr, rawParams);
				Logger.json(json);
				// 解析json数据
				com.alibaba.fastjson.JSONObject jsonobj = JSON.parseObject(json);
				Boolean response = (Boolean) jsonobj.get("success");
				// 判断是否请求成功
				if (response) {
					loginValidate = true;
					responseMsg = response.toString();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			NetworkCheckDialog.dialog(ResetPass.this);
		}
		return loginValidate;
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 3:
					Intent intent = new Intent();
					intent.setClass(ResetPass.this, LoginActivity.class);
					startActivity(intent);
					Toast.makeText(ResetPass.this, "重置密码成功，请重新登录", Toast.LENGTH_LONG).show();
					ResetPass.this.finish();
					Logger.v(responseMsg+"注册成功");
					break;
				case 1:
					Toast.makeText(ResetPass.this, "重置密码失败", Toast.LENGTH_LONG).show();
					break;
				default:
					break;
			}
		}
	};

}
