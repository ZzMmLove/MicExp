package cn.gdgst.palmtest.tab4;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import cn.gdgst.palmtest.API.APIWrapper;
import cn.gdgst.palmtest.R;
import cn.gdgst.palmtest.base.AppConstant;
import cn.gdgst.palmtest.bean.HttpResult;
import cn.gdgst.palmtest.utils.HttpUtil;

import com.orhanobut.logger.Logger;
import cn.gdgst.palmtest.utils.Encrypt;
import cn.gdgst.palmtest.utils.NetworkCheck;
import cn.gdgst.palmtest.utils.NetworkCheckDialog;
import cn.gdgst.palmtest.utils.ToastUtil;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.util.HashMap;
import java.util.Map;

public class ChangePassActivity extends Activity implements OnClickListener {
	private ImageView register_back;

	private Button btnSub;
	private EditText oldpassword_edit, newpassword_edit;
	private String accessToken;
	private String responseMsg = "";

	/**
	 * 初始化sharedPreferences
	 */
	private SharedPreferences sharedPreferencesUserInfo = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_changepass);
		InitView();

	}

	private void InitView() {
		// TODO Auto-generated method stub
		register_back = (ImageView) findViewById(R.id.iv_back);
		btnSub = (Button) findViewById(R.id.new_btn_submit);
		oldpassword_edit = (EditText) findViewById(R.id.old_password);
		newpassword_edit = (EditText) findViewById(R.id.new_password);

		register_back.setOnClickListener(this);
		btnSub.setOnClickListener(this);

		sharedPreferencesUserInfo = getSharedPreferences(AppConstant.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
		accessToken = sharedPreferencesUserInfo.getString("accessToken", "");
		Logger.i( "accessToken:" + accessToken);

	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
			case R.id.iv_back:
				this.finish();
				break;

			case R.id.new_btn_submit:
				String oldPass = oldpassword_edit.getText().toString().trim();
				String newPass = newpassword_edit.getText().toString().trim();
				String oldpassword = Encrypt.md5(oldPass);
				String newpassword = Encrypt.md5(newPass);
				Log.i("ChangePassActivity", "====oldpassword==="+oldpassword);
				Log.i("ChangePassActivity", "====password==="+oldPass);
				if (TextUtils.isEmpty(oldPass) || "".equals(oldPass)) {
					oldpassword_edit.setError("旧密码不能为空");
					return;
				}
				if(TextUtils.isEmpty(newPass) || "".equals(newPass)){
					newpassword_edit.setError("新密码不能为空");
					return;
				}
				if (!oldpassword.equals(sharedPreferencesUserInfo.getString("passwordMd5",""))){
					ToastUtil.show("旧密码不正确");
					return;
				}
					// SMSSDK.submitVerificationCode("86", phoneNums,
					// code_edit.getText().toString());//将验证码提交至SMSSDK服务器
					//Thread RegThread = new Thread(new RegisterThread());
					//RegThread.start();
				APIWrapper.getInstance().getChangePass(accessToken, oldpassword, newpassword)
						.observeOn(AndroidSchedulers.mainThread())
						.subscribeOn(Schedulers.io())
						.subscribe(new Subscriber<HttpResult>() {
							@Override
							public void onCompleted() {
							}

							@Override
							public void onError(Throwable e) {
							}

							@Override
							public void onNext(HttpResult httpResult) {
								Log.i("ChangePassActivity", "===success==="+httpResult.getSuccess());

								if (httpResult.getError_code() == 0){
									Intent intent = new Intent();
									intent.setClass(ChangePassActivity.this, LoginActivity.class);
									startActivity(intent);
									finish();
								}
							}

						});
				break;
			default:
				break;
		}
	}

	// RegisterThread主线程类
	class RegisterThread implements Runnable {
		@Override
		public void run() {

			String oldpassword = Encrypt.md5(oldpassword_edit.getText().toString());
			String newpassword = Encrypt.md5(newpassword_edit.getText().toString());
			//
			boolean registerValidate = registerServer(oldpassword, newpassword);
			// System.out.println("----------------------------bool is
			// :"+registerValidate+"----------response:"+responseMsg);

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
	private boolean registerServer(String oldpassword, String newpassword) {
		boolean loginValidate = false;
		// 使用apache HTTP客户端实现
		String urlStr = "http://shiyan360.cn/index.php/api/change_pass";
		// String urlStr = "http://testphp7.114dg.cn/index.php/api/send_code";

		oldpassword = Encrypt.md5(oldpassword_edit.getText().toString()).trim();
		newpassword = Encrypt.md5(newpassword_edit.getText().toString()).trim();
		NetworkCheck check = new NetworkCheck(ChangePassActivity.this);
		boolean isalivable = check.Network();
		if (isalivable) {

			// 封装请求参数
			Map<String, String> rawParams = new HashMap<String, String>();
			rawParams.put("accessToken", accessToken);
			rawParams.put("old_pass", oldpassword);
			rawParams.put("new_pass", newpassword);

			Logger.i( "请求提交的accessToken" + accessToken);
			Logger.i( "请求提交的oldpass:" + oldpassword);
			Logger.i( "请求提交的newpass:" + newpassword);
			try {
				// 设置请求参数项
				// 发送请求返回json
				String json = HttpUtil.postRequest(urlStr, rawParams);
				Logger.json(json);

				// 解析json数据
				com.alibaba.fastjson.JSONObject jsonobj = JSON.parseObject(json);
				Boolean response = (Boolean) jsonobj.get("success");

				// com.alibaba.fastjson.JSONObject responsee = (JSONObject)
				// jsonobj.get("success");
				// String data = responsee.toJSONString();
				// com.alibaba.fastjson.JSONObject response =
				// JSON.parseObject(data);

				// 判断是否请求成功
				if (response) {
					loginValidate = true;
					// responseMsg = response.toString();\
					responseMsg = response.toString();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			NetworkCheckDialog.dialog(ChangePassActivity.this);
		}
		return loginValidate;
	}


	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 3:
					Intent intent = new Intent();
					intent.setClass(ChangePassActivity.this, LoginActivity.class);
					startActivity(intent);
					finish();
					Toast.makeText(ChangePassActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
					Logger.v(responseMsg+ "修改成功");

					sharedPreferencesUserInfo = getSharedPreferences(AppConstant.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
					Editor editor = sharedPreferencesUserInfo.edit();// 获取编辑器
					editor.putString("password", "");
					// 设置自动登陆为否
					editor.putBoolean("autoLogin", false);
					editor.commit();// 提交修改
					break;
				case 1:
					Toast.makeText(ChangePassActivity.this, "修改失败，请重新尝试", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
			}
		}
	};

}
