package cn.gdgst.palmtest.tab4;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import cn.gdgst.palmtest.R;
import cn.gdgst.palmtest.utils.HttpUtil;

import com.orhanobut.logger.Logger;
import cn.gdgst.palmtest.utils.Encrypt;
import cn.gdgst.palmtest.utils.NetworkCheck;
import cn.gdgst.palmtest.utils.NetworkCheckDialog;
import cn.gdgst.palmtest.utils.RegExUtils;
import cn.gdgst.palmtest.utils.ToastUtil;

import java.util.HashMap;
import java.util.Map;

public class CreateUserActivity extends AppCompatActivity implements OnClickListener, TextWatcher {
	private Button btnSub;
	private EditText password_edit;
	private EditText user_email;
	private ImageView ic_correct;
	private String username, smscode;
	private String responseMsg = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_creteuser);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("设置密码");
		InitView();

	}

	private void InitView() {
		btnSub = (Button) findViewById(R.id.new_btn_submit);
		user_email = (EditText) findViewById(R.id.new_email);
		password_edit = (EditText) findViewById(R.id.new_password);
		ic_correct = (ImageView) findViewById(R.id.iv_correct);
		btnSub.setOnClickListener(this);
		user_email.addTextChangedListener(this);

		username =getIntent().getStringExtra("user_name");
		//smscode = bundle.getString("smscode");
		Logger.v( "传递取得的:" + username);
		//Logger.v( "传递取得的:" + smscode);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.new_btn_submit:
				if (!TextUtils.isEmpty(password_edit.getText().toString())) {
					Thread RegThread = new Thread(new RegisterThread());
					RegThread.start();
				} else {
					Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
				}
				break;

			default:
				break;
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(Editable s) {
		if (RegExUtils.isEmail(s.toString())){
			ic_correct.setVisibility(View.VISIBLE);
		}else {
			ic_correct.setVisibility(View.INVISIBLE);
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
		String urlStr = "http://shiyan360.cn/index.php/api/user_signup";
		// String urlStr = "http://testphp7.114dg.cn/index.php/api/send_code";
		Log.e("TAG","==="+username);
		smscode = user_email.getText().toString().trim();
		NetworkCheck check = new NetworkCheck(CreateUserActivity.this);
		boolean isalivable = check.Network();
		if (isalivable) {
			// 封装请求参数
			Map<String, String> rawParams = new HashMap<>();
			rawParams.put("user_name", username);
			rawParams.put("user_email", smscode);
			rawParams.put("user_pass", password);
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
					// responseMsg = response.toString();
					responseMsg = response.toString();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			NetworkCheckDialog.dialog(CreateUserActivity.this);
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
					intent.setClass(CreateUserActivity.this, LoginActivity.class);
					startActivity(intent);
					finish();
					Toast.makeText(CreateUserActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
					Logger.v(responseMsg+ "注册成功");
					break;
				case 1:
					Toast.makeText(CreateUserActivity.this, "注册失败，请核对信息",  Toast.LENGTH_SHORT).show();
					break;
				case 2:
					ToastUtil.show("注册失败，请检查网络");
				default:
					break;
			}
		}
	};

}
