package cn.gdgst.palmtest.tab4;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;

import cn.gdgst.palmtest.R;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.mob.MobSDK;
import cn.gdgst.palmtest.utils.NetworkCheck;
import cn.gdgst.palmtest.utils.NetworkCheckDialog;

import cn.gdgst.palmtest.utils.ToastUtil;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class ResetPasswordActivity extends AppCompatActivity implements OnClickListener{
	// 手机号输入框    验证码输入框
	private EditText user_name_edit,code_edit;
	// 获取验证码按钮  注册按钮  重置按钮
	private Button btnCode;
	private Button btnNext;
	private int i = 30;
	private ProgressBar mProBar;
	private EventHandler eventHandler;
	private  String userName;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_password);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("忘记密码");
		initView();
		initSMSSDK();
	}

	Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
				case -9:
					btnCode.setText("重新发送(" + i + ")");
					break;
				case -8:
					btnCode.setText("获取验证码");
					btnCode.setBackgroundColor(Color.parseColor("#70d0f8"));
					btnCode.setClickable(true);
					i = 30;
					break;
				case 0:
					if (msg.what == -9) {
						btnCode.setText("重新发送(" + i + ")");
					} else if (msg.what == -8) {
						btnCode.setText("获取验证码");
						btnCode.setBackgroundColor(Color.parseColor("#70d0f8"));
						btnCode.setClickable(true);
						i = 30;
					}
                    /* else {
						int event = msg.arg1;
						int result = msg.arg2;
						Object data = msg.obj;


						Logger.e("event=" + event);
						if (result == SMSSDK.RESULT_COMPLETE) {
							if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
								Toast.makeText(getApplicationContext(), "验证码已经发送", Toast.LENGTH_SHORT).show();
							}else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE){
								Toast.makeText(getApplicationContext(), "验证通过", Toast.LENGTH_SHORT).show();
								mProBar.setVisibility(View.GONE);
								userName = user_name_edit.getText().toString().trim();
								Intent intent = new Intent(ResetPasswordActivity.this, ResetPass.class);
								intent.putExtra("user_name",userName );
								startActivity(intent);
								finish();
							}
						} else if (result == SMSSDK.RESULT_ERROR){
							mProBar.setVisibility(View.GONE);
							String jsonString = ((Throwable) data).getMessage();
							JSONObject jsonObject = JSON.parseObject(jsonString);
							String status = jsonObject.getString("status");
							Log.e("TAG", ">>>>>"+status);
							if (status.equals("468")){
								Toast.makeText(getApplicationContext(), "验证码错误", Toast.LENGTH_SHORT).show();
							}else if (status.equals("467")){
								Toast.makeText(getApplicationContext(), "校验验证码请求频繁", Toast.LENGTH_SHORT).show();
							}
						}
						Logger.i("" + msg);
					}*/
					break;
			}
		}
	};


	/**
	 * 初始化短信验证
	 */
	private void initSMSSDK() {
//		SMSSDK.initSDK(this, "11aee97e4d4ec", "8be3985767da866344b8d4ffa8bd173e");
        MobSDK.init(this, "11aee97e4d4ec", "8be3985767da866344b8d4ffa8bd173e");
		eventHandler = new EventHandler(){
			@Override
			public void afterEvent(int event, int result, final Object data) {
		/*		Message msg = new Message();
				msg.arg1 = event;
				msg.arg2 = result;
				msg.obj = data;
				handler.sendMessage(msg);*/


                if (result == SMSSDK.RESULT_COMPLETE){
                    //完成回调
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE){
                        //提交验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "验证通过", Toast.LENGTH_SHORT).show();
                                mProBar.setVisibility(View.GONE);
                                userName = user_name_edit.getText().toString().trim();
                                Intent intent = new Intent(ResetPasswordActivity.this, ResetPass.class);
                                intent.putExtra("user_name",userName );
                                startActivity(intent);
                                finish();
                            }
                        });
                    }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        //获取验证码成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "验证码已经发送", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                        //返回支持发送验证码的国家列表
                    }
                }else if (result == SMSSDK.RESULT_ERROR){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProBar.setVisibility(View.GONE);
                            String jsonString = ((Throwable) data).getMessage();
                            JSONObject jsonObject = null;
                            String status = "";
                            try{
                                jsonObject = JSON.parseObject(jsonString);
                                status = jsonObject.getString("status");
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                            Log.e("TAG", ">>>>>"+status);
                            if (status.equals("468")){
                                Toast.makeText(getApplicationContext(), "验证码错误", Toast.LENGTH_SHORT).show();
                            }else if (status.equals("467")){
                                Toast.makeText(getApplicationContext(), "校验验证码请求频繁", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }

			}
		};
		SMSSDK.registerEventHandler(eventHandler);
	}

	/**
	 * 初始化视图空件
	 */
	private void initView() {
		code_edit = (EditText) findViewById(R.id.reset_password_edt_code);
		user_name_edit = (EditText) findViewById(R.id.reset_password_username);
		btnCode = (Button) findViewById(R.id.btn_getcode);
		btnNext = (Button) findViewById(R.id.btn_next);

		btnNext.setOnClickListener(this);
		btnCode.setOnClickListener(this);
		mProBar = this.createProgressBar();
	}

	/**
	 * 按钮单击事件
	 * @param v
	 */
	@Override
	public void onClick(View v) {
		Log.e("TAG", "======1"+userName);
		userName = user_name_edit.getText().toString().trim();
		Log.e("TAG", "======2"+userName);
		switch(v.getId()){
			case R.id.btn_getcode:
				getCode(userName);
				break;
			case R.id.btn_next:
				String smsCode =code_edit.getText().toString().trim();

				if(TextUtils.isEmpty(smsCode) || smsCode == ""){
					ToastUtil.show("验证码不能为空！");
				}else {
					Log.i("RegisterActivity", "==userName=="+userName+"==smsCode=="+smsCode);
					NetworkCheck check = new NetworkCheck(this);
					if (check.Network()){
						checkCode(userName, smsCode);
					}else {
						NetworkCheckDialog.dialog(this);
					}
				}
				break;
		}
	}

	/**
	 * 根据请求结果验证完就下一步设置密码
	 */
	private void checkCode(String userName, String smsCode){
		SMSSDK.submitVerificationCode("86", userName, smsCode);//将验证码提交至SMSSDK服务器
		mProBar.setVisibility(View.VISIBLE);
		createProgressBar();
	}

	/**
	 * 获取验证码
	 * @param userName
	 */
	private void getCode(String userName) {
		// 1. 通过规则判断手机号
		if (!judgePhoneNums(userName)) {
			Toast.makeText(this, "手机号码输入有误！",Toast.LENGTH_SHORT).show();
			return;
		}
		// 2. 通过sdk发送短信验证
		SMSSDK.getVerificationCode("86", userName);

		// 3. 把按钮变成不可点击，并且显示倒计时（正在获取）
		btnCode.setClickable(false);
		btnCode.setText("重新发送(" + i + ")");
		btnCode.setBackgroundColor(Color.GRAY);
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (; i > 0; i--) {
					handler.sendEmptyMessage(-9);
					if (i <= 0) break;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				handler.sendEmptyMessage(-8);
			}
		}).start();
	}


	/**
	 * 判断手机号码是否合理
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
	 * 自定义进度条
	 * @return
	 */
	private ProgressBar createProgressBar() {
		FrameLayout layout = (FrameLayout) findViewById(android.R.id.content);
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		layoutParams.gravity = Gravity.CENTER;
		ProgressBar mProBar = new ProgressBar(this);
		mProBar.setLayoutParams(layoutParams);
		mProBar.setVisibility(View.GONE);
		layout.addView(mProBar);
		return mProBar;
	}

	/**
	 * 资源回收，防止OOM
	 */
	@Override
	protected void onDestroy() {
		SMSSDK.unregisterEventHandler(eventHandler);
		super.onDestroy();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		mProBar.setVisibility(View.GONE);
	}
}
