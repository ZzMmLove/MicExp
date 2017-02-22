package cn.gdgst.palmtest.tab4;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import cn.gdgst.palmtest.R;
import com.orhanobut.logger.Logger;
import cn.gdgst.palmtest.utils.HttpUtil;
import cn.gdgst.palmtest.utils.NetworkCheck;
import cn.gdgst.palmtest.utils.NetworkCheckDialog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.FrameLayout.LayoutParams;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class SendCodeActivity extends AppCompatActivity implements OnClickListener{
	//后退图片按钮
	//private ImageView register_back;
	// 手机号输入框    验证码输入框
	private EditText user_name_edit,code_edit;
	// 获取验证码按钮  注册按钮  重置按钮
	private Button btn_next,getcode;
	//
	int i = 30;
	private String responseMsg = "";
	private int error_codeMsg;

	private ProgressBar mProBar = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sendcode);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("注册");
		InitView();
	}
	private void InitView() {
		// TODO Auto-generated method stub
		//控件
		//register_back=(ImageView) findViewById(R.id.iv_back);
		user_name_edit=(EditText) findViewById(R.id.new_username);
		code_edit = (EditText) findViewById(R.id.edt_code);
		btn_next=(Button) findViewById(R.id.btn_next);
		getcode = (Button) findViewById(R.id.btn_getcode);
		//register_back.setOnClickListener(this);
		btn_next.setOnClickListener(this);
		getcode.setOnClickListener(this);

		mProBar = this.createProgressBar();
//			 启动短信验证sdk
//			SMSSDK.initSDK(this, "您的appkey", "您的appsecret");
		SMSSDK.initSDK(this, "11aee97e4d4ec", "8be3985767da866344b8d4ffa8bd173e");
		EventHandler eventHandler = new EventHandler(){
			@Override
			public void afterEvent(int event, int result, Object data) {
				Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler.sendMessage(msg);
			}
		};
		//注册回调监听接口
		SMSSDK.registerEventHandler(eventHandler);
	}



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		String phoneNums = user_name_edit.getText().toString();
		switch (v.getId()) {
			/*case R.id.iv_back:
				this.finish();
				break;*/
			case R.id.btn_getcode:
				// 1. 通过规则判断手机号
				if (!judgePhoneNums(phoneNums)) {
					return;
				} // 2. 通过sdk发送短信验证
				SMSSDK.getVerificationCode("86", phoneNums);

				// 3. 把按钮变成不可点击，并且显示倒计时（正在获取）
				getcode.setClickable(false);
				getcode.setText("重新发送(" + i + ")");
				getcode.setBackgroundColor(Color.GRAY);
				new Thread(new Runnable() {
					@Override
					public void run() {
						for (; i > 0; i--) {
							handler.sendEmptyMessage(-9);
							if (i <= 0) {
								break;
							}
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						handler.sendEmptyMessage(-8);
					}
				}).start();
				break;

			case R.id.btn_next:
				if(!TextUtils.isEmpty(code_edit.getText().toString())){
//					SMSSDK.submitVerificationCode("86", phoneNums, code_edit.getText().toString());//将验证码提交至SMSSDK服务器
					mProBar.setVisibility(View.VISIBLE);
//					createProgressBar();
					Thread SendcodeThread = new Thread(new SendcodeThread());
					SendcodeThread.start();
				}
				else {
					Toast.makeText(this, "验证码不能为空", Toast.LENGTH_SHORT).show();
				}

				break;
			default:
				break;
		}
	}



	/**
	 *
	 */
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case -9:
					getcode.setText("重新发送(" + i + ")");
					break;
				case -8:
					getcode.setText("获取验证码");
					getcode.setBackgroundColor(Color.parseColor("#70d0f8"));
					getcode.setClickable(true);
					i = 30;
					break;
				case 0:
					if (msg.what == -9) {
						getcode.setText("重新发送(" + i + ")");
					} else if (msg.what == -8) {
						getcode.setText("获取验证码");
						getcode.setBackgroundColor(Color.parseColor("#70d0f8"));
						getcode.setClickable(true);
						i = 30;
					} else {
						int event = msg.arg1;
						int result = msg.arg2;
						Object data = msg.obj;
						Logger.e("event=" + event);
						if (result == SMSSDK.RESULT_COMPLETE) {
							if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
								Toast.makeText(getApplicationContext(), "验证码已经发送",
										Toast.LENGTH_SHORT).show();
							}
						}else {
							((Throwable) data).printStackTrace();
							Toast.makeText(SendCodeActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
						}
						Logger.i( ""+msg);
					}
					break;
				case 3:
					Bundle bundle = new Bundle();
					bundle.putString("user_name", user_name_edit.getText()
							.toString());
					bundle.putString("smscode", code_edit.getText().toString());
					Logger.v( "传递的user_name:"+user_name_edit.getText().toString());
					Logger.v( "传递的code:"+code_edit.getText().toString());
					Intent intent = new Intent();
					intent.putExtras(bundle);
					intent.setClass(SendCodeActivity.this, CreateUserActivity.class);
					startActivity(intent);
					finish();
					// 返回intent
					setResult(RESULT_OK, intent);
					Toast.makeText(SendCodeActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
					break;
				case 2:
					mProBar.setVisibility(View.GONE);
					Toast.makeText(SendCodeActivity.this, "此手机已注册", Toast.LENGTH_SHORT).show();
					break;
				case 1:
					mProBar.setVisibility(View.GONE);
					Toast.makeText(SendCodeActivity.this, "验证码错误", Toast.LENGTH_SHORT).show();
					break;
				case 4:
					mProBar.setVisibility(View.GONE);
					Toast.makeText(SendCodeActivity.this, "操作频繁，请稍后再试", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
			}
		}
	};



	/**
	 * 判断手机号码是否合理
	 *
	 * @param phoneNums
	 */
	private boolean judgePhoneNums(String phoneNums) {
		if (isMatchLength(phoneNums, 11)
				&& isMobileNO(phoneNums)) {
			return true;
		}
		Toast.makeText(this, "手机号码输入有误！",Toast.LENGTH_SHORT).show();
		return false;
	}
	/**
	 * 判断一个字符串的位数
	 * @param str
	 * @param length
	 * @return
	 */
	public static boolean isMatchLength(String str, int length) {
		if (str.isEmpty()) {
			return false;
		} else {
			return str.length() == length ? true : false;
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
	 * progressbar
	 * @return
	 */
	private ProgressBar createProgressBar() {
		FrameLayout layout = (FrameLayout) findViewById(android.R.id.content);
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		layoutParams.gravity = Gravity.CENTER;
		ProgressBar mProBar = new ProgressBar(this);
		mProBar.setLayoutParams(layoutParams);
		mProBar.setVisibility(View.GONE);
		layout.addView(mProBar);
		return mProBar;
	}

	// SendCodeThread主线程类
	class SendcodeThread implements Runnable {
		@Override
		public void run() {
			String username = user_name_edit.getText().toString();
			String smscode = code_edit.getText().toString();
			//
			boolean registerValidate = registerServer(username,smscode);
			// System.out.println("----------------------------bool is :"+registerValidate+"----------response:"+responseMsg);

			Message msg = handler.obtainMessage();
			if (registerValidate) {
				if (responseMsg.equals("true")) {
					msg.what = 3;
					handler.sendMessage(msg);
				}

			} else {
				if (error_codeMsg==468) {
					//验证码错误
					msg.what = 1;
					handler.sendMessage(msg);
				}
				else if(error_codeMsg==2){
					//手机已经注册
					msg.what = 2;
					handler.sendMessage(msg);
				}
				else if(error_codeMsg==467){
					//操作频繁
					msg.what = 4;
					handler.sendMessage(msg);
				}

			}
		}

	}

	// 注册服务 方式二
	private boolean registerServer(String username,String smscode) {
		boolean loginValidate = false;
		// 使用apache HTTP客户端实现
//					 String urlStr = "http://testphp7.114dg.cn/index.php/api/user_signup";
		String urlStr = "http://www.shiyan360.cn/index.php/api/send_code";
		username = user_name_edit.getText().toString().trim();
		smscode = code_edit.getText().toString().trim();
		NetworkCheck check = new NetworkCheck(SendCodeActivity.this);
		boolean isalivable = check.Network();
		String code_type=String.valueOf(1);
		if (isalivable) {

			// 封装请求参数
			Map<String, String> rawParams = new HashMap<String, String>();
			rawParams.put("user_name", username);
			rawParams.put("sms_code", smscode);
			rawParams.put("code_type",code_type);

			Logger.i( username);
			Logger.i( smscode);
			Logger.i( code_type);

			try {
				// 设置请求参数项
				// 发送请求返回json
				String json = HttpUtil.postRequest(urlStr, rawParams);
				Logger.json(json);


				// 解析json数据
				com.alibaba.fastjson.JSONObject jsonobj = JSON
						.parseObject(json);
				Boolean response = (Boolean) jsonobj.get("success");

				int error_code=(int) jsonobj.get("error_code");

				Logger.i( "error_code:"+error_code);

				// com.alibaba.fastjson.JSONObject responsee = (JSONObject)
				// jsonobj.get("success");
				// String data = responsee.toJSONString();
				// com.alibaba.fastjson.JSONObject response =
				// JSON.parseObject(data);

				// 判断是否请求成功 大情况：success为true
				if (response) {
					loginValidate = true;
					// responseMsg = response.toString();\
					responseMsg = response.toString();

				}//大情况：success为false
				else {
					loginValidate = false;
					error_codeMsg=error_code;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			NetworkCheckDialog.dialog(SendCodeActivity.this);
		}
		return loginValidate;
	}





	@Override
	protected void onDestroy() {
		SMSSDK.unregisterAllEventHandler();
		super.onDestroy();
	}
}
