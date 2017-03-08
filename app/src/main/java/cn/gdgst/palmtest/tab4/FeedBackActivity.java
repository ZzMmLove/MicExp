package cn.gdgst.palmtest.tab4;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import cn.gdgst.palmtest.R;
import cn.gdgst.palmtest.utils.HttpUtil;
import cn.gdgst.palmtest.utils.NetworkCheck;
import cn.gdgst.palmtest.utils.NetworkCheckDialog;

import com.orhanobut.logger.Logger;

import java.util.HashMap;
import java.util.Map;

public class FeedBackActivity extends AppCompatActivity implements OnClickListener {
	private Button sub_feedback;
	private EditText et_name, et_phone, et_email, et_qq, et_content;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("意见反馈");
		sub_feedback = (Button) findViewById(R.id.sub_feedback);
		sub_feedback.setOnClickListener(this);

		et_name = (EditText) findViewById(R.id.et_name);
		et_phone = (EditText) findViewById(R.id.et_phone);
		et_email = (EditText) findViewById(R.id.et_email);
		et_qq = (EditText) findViewById(R.id.et_qq);
		et_content = (EditText) findViewById(R.id.et_content);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.sub_feedback:
				String content = et_content.getText().toString().trim();
				String name = et_name.getText().toString().trim();
				String phone = et_phone.getText().toString().trim();
				String qq = et_qq.getText().toString().trim();
				String email = et_email.getText().toString().trim();
				Logger.i("phone"+ phone);
				Logger.i("qq"+ qq);

				if (content.isEmpty()) {

					Toast.makeText(FeedBackActivity.this, "请输入内容", Toast.LENGTH_SHORT).show();

				} else if (!phone.equals("") || !qq.equals("") || !email.equals("")) {
					Thread submitThread = new Thread(new submitThread());
					submitThread.start();

				} else {
					Toast.makeText(FeedBackActivity.this, "为了让我们联系到您，请至少填一种联系方式吧~", Toast.LENGTH_SHORT).show();
				}

				break;

			default:
				break;
		}
	}

	class submitThread implements Runnable {

		@Override
		public void run() {
			String content = et_content.getText().toString();
			String name = et_name.getText().toString();
			String tel = et_phone.getText().toString();
			String email = et_email.getText().toString();
			String qq = et_qq.getText().toString();
			String url = "http://www.shiyan360.cn/index.php/api/guestbook";
			Map<String, String> rawParams = new HashMap<String, String>();
			rawParams.put("content", content);
			rawParams.put("name", name);
			rawParams.put("tel", tel);
			rawParams.put("email", email);
			rawParams.put("qq", qq);
			NetworkCheck check = new NetworkCheck(FeedBackActivity.this);
			boolean isalivable = check.Network();
			if (isalivable) {
				try {
					String json = HttpUtil.postRequest(url, rawParams);
					Logger.json(json);
					// 解析json数据
					com.alibaba.fastjson.JSONObject jsonobj = JSON.parseObject(json);
					Boolean response = (Boolean) jsonobj.get("success");
					if (response) {
						mHandler.sendEmptyMessage(0);
					} else {
						mHandler.sendEmptyMessage(1);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
			} else {
				NetworkCheckDialog.dialog(FeedBackActivity.this);
			}

		}

	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 0:
					Toast.makeText(FeedBackActivity.this, "提交成功，谢谢您的建议", Toast.LENGTH_SHORT).show();
					break;

				case 1:
					Toast.makeText(FeedBackActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
					break;
			}

		}
	};

}
