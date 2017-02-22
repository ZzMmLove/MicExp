package cn.gdgst.palmtest.tab4;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import cn.gdgst.palmtest.R;
import com.orhanobut.logger.Logger;
import cn.gdgst.palmtest.utils.Encrypt;
import cn.gdgst.palmtest.utils.HttpUtil;
import cn.gdgst.palmtest.utils.NetworkCheck;
import cn.gdgst.palmtest.utils.NetworkCheckDialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class ResetPass extends Activity implements OnClickListener {
	private ImageView register_back;

	private Button btnSub;
	private EditText password_edit;
	private String username, smscode;
	private String responseMsg = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.resetpass);
		InitView();

	}

	private void InitView() {
		// TODO Auto-generated method stub
		register_back = (ImageView) findViewById(R.id.iv_back);
		btnSub = (Button) findViewById(R.id.new_btn_submit);
		password_edit = (EditText) findViewById(R.id.new_password);

		register_back.setOnClickListener(this);
		btnSub.setOnClickListener(this);

		Bundle bundle = new Bundle();
		bundle = this.getIntent().getExtras();
		username = bundle.getString("user_name");
		smscode = bundle.getString("smscode");
		Logger.v( "传递取得的:" + username);
		Logger.v( "传递取得的:" + smscode);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.iv_back:
				this.finish();
				break;

			case R.id.new_btn_submit:
				if (!TextUtils.isEmpty(password_edit.getText().toString())) {
					// SMSSDK.submitVerificationCode("86", phoneNums,
					// code_edit.getText().toString());//将验证码提交至SMSSDK服务器
					Thread RegThread = new Thread(new RegisterThread());
					RegThread.start();
				} else {
					Toast.makeText(this, "密码不能为空", 1).show();
				}
				break;

			default:
				break;
		}
	}

	// RegisterThread主线程类
	class RegisterThread implements Runnable {
		@Override
		public void run() {

			String password = Encrypt.md5(password_edit.getText().toString());
			//
			boolean registerValidate = registerServer(password);
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
	private boolean registerServer(String password) {
		boolean loginValidate = false;
		// 使用apache HTTP客户端实现
		String urlStr = "http://www.shiyan360.cn/index.php/api/find_pass";
		// String urlStr = "http://testphp7.114dg.cn/index.php/api/send_code";
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
					;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			NetworkCheckDialog.dialog(ResetPass.this);
		}
		return loginValidate;
	}

	/**
	 *
	 */
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 3:
					Intent intent = new Intent();
					intent.setClass(ResetPass.this, LoginActivity.class);
					startActivity(intent);
					finish();
					Toast.makeText(ResetPass.this, "重置密码成功，请重新登录", 1).show();
					Logger.v(responseMsg+"注册成功");
					break;
				case 1:
					Toast.makeText(ResetPass.this, "重置密码失败", 1).show();
					break;
				default:
					break;
			}
		}
	};

}