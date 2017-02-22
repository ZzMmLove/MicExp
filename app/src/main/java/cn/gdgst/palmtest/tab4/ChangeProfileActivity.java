package cn.gdgst.palmtest.tab4;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

import com.bigkoo.svprogresshud.SVProgressHUD;

import java.util.HashMap;
import java.util.Map;

import cn.gdgst.palmtest.R;
import cn.gdgst.palmtest.base.AppConstant;
import cn.gdgst.palmtest.service.UpdateUserInfo;

public class ChangeProfileActivity extends AppCompatActivity implements OnClickListener {
	private final int UPDATE_USER_INFO_SUCCESS = 0;
	private final int UPDATE_USER_INFO_FALSE = 1;
	private Button btnSub;
	private EditText ed_text;
	private String old_pass, accessToken;
	private String responseMsg = "";
	private Intent intent;
	private DealUpdateUserInfoHandler dealUpdateUserInfoHandler;
	private SVProgressHUD svProgressHUD;
	private String showActionBarTitle;
	/**
	 * 初始化sharedPreferences
	 */
	private SharedPreferences sp = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_changeprofile);
		svProgressHUD = new SVProgressHUD(this);
		dealUpdateUserInfoHandler = new DealUpdateUserInfoHandler();
		intent=getIntent();
		showActionBarTitle = intent.getStringExtra("tv_title");
		ActionBar actionBar = getSupportActionBar();
		actionBar.setTitle(showActionBarTitle);
		InitView();
	}

	private void InitView() {
		btnSub = (Button) findViewById(R.id.new_btn_submit);
		ed_text = (EditText) findViewById(R.id.ed_text);
		btnSub.setOnClickListener(this);
		sp = getSharedPreferences(AppConstant.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
		ed_text.setHint(intent.getStringExtra("hint"));
	}

	@Override
	public void onClick(View v) {
		String edtext = ed_text.getText().toString().trim();
		switch (v.getId()) {
			case R.id.new_btn_submit:
				if (!TextUtils.isEmpty(edtext)) {
					svProgressHUD.showWithStatus("请稍后...");
					Thread CPThread =new Thread(new ChangeProfileThread());
					CPThread.start();
				}  else {
					Toast.makeText(this, "输入框不能为空哟~", Toast.LENGTH_SHORT).show();
				}
				break;
		}

	}

	class ChangeProfileThread implements Runnable{

		@Override
		public void run() {
			try {
				Thread.sleep(100);
				UpdateUserInfo updateUserInfo = new UpdateUserInfo(ChangeProfileActivity.this);
				sp = getSharedPreferences(AppConstant.SHARED_PREFERENCES_USER, Context.MODE_PRIVATE);
				String accessToken = sp.getString("accessToken", "");
				Map<String, String> rawParams = new HashMap<String, String>();
				rawParams.put("accessToken", accessToken);
				switch (showActionBarTitle) {
					case "修改昵称":
						rawParams.put("nickname", ed_text.getText().toString().trim());
						break;
					case "修改用户名":
						rawParams.put("name", ed_text.getText().toString().trim());
						break;
					case "修改学校":
						rawParams.put("school", ed_text.getText().toString().trim());
						break;
				}
				Boolean getupdateUserInfo = updateUserInfo.getUpdateUserInfo(rawParams);
				if (getupdateUserInfo) {
					dealUpdateUserInfoHandler.sendEmptyMessage(UPDATE_USER_INFO_SUCCESS);
				}else if (getupdateUserInfo == false){
					Message message = new Message();
					message.obj = updateUserInfo.getMessage();
					message.what = UPDATE_USER_INFO_FALSE;
					dealUpdateUserInfoHandler.sendMessage(message);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private class DealUpdateUserInfoHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case UPDATE_USER_INFO_SUCCESS:
					if (svProgressHUD.isShowing()) {
						svProgressHUD.dismiss();
					}
					Toast.makeText(ChangeProfileActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
					SharedPreferences sp = getSharedPreferences(AppConstant.SHARED_PREFERENCES_USER, MODE_PRIVATE);
					SharedPreferences.Editor ed = sp.edit();
					switch (showActionBarTitle) {
						case "修改昵称":
							ed.putString("nickname", ed_text.getText().toString().trim());
							break;
						case "修改用户名":
							ed.putString("name", ed_text.getText().toString().trim());
							break;
						case "修改学校":
							ed.putString("school", ed_text.getText().toString().trim());
							break;
					}
					ed.commit();
					ChangeProfileActivity.this.finish();
					break;
				case UPDATE_USER_INFO_FALSE:
					if (svProgressHUD.isShowing()) {
						svProgressHUD.dismiss();
					}
					String message = (String) msg.obj;
					Toast.makeText(ChangeProfileActivity.this, message, Toast.LENGTH_SHORT).show();
					break;
			}
		}
	}
}
