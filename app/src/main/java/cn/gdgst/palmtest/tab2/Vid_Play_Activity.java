package cn.gdgst.palmtest.tab2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import cn.com.video.venvy.param.JjVideoView;
import cn.com.video.venvy.param.OnJjBufferCompleteListener;
import cn.com.video.venvy.param.OnJjBufferStartListener;
import cn.com.video.venvy.param.OnJjBufferingUpdateListener;
import cn.com.video.venvy.param.OnJjOpenStartListener;
import cn.com.video.venvy.param.OnJjOpenSuccessListener;
import cn.com.video.venvy.param.OnJjOutsideLinkClickListener;
import cn.com.video.venvy.param.VideoJjMediaContoller;
import cn.gdgst.palmtest.R;

public class Vid_Play_Activity extends Activity {
	private String path = null;
	private JjVideoView mVideoView;
	private View mLoadBufferView;
	private TextView mLoadBufferTextView;
	private View mLoadView;
	private TextView mLoadText;
	private File file_native_video_path;
	private boolean isNativeVideo;
	private File file1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 定义全屏参数
		int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		// 获得当前窗体对象
		Window window = Vid_Play_Activity.this.getWindow();
		// 设置当前窗体为全屏显示
		setContentView(R.layout.act_play);
		Intent intent = getIntent();
		path = intent.getStringExtra("video_path");

		//http://www.shiyan360.cn/Public/Uploads/Video/20170109/5872ed61d0d5a.mp4
		String fileName1 = path.substring(23);//   /Public/Uploads/Video/20170109/5872ed61d0d5a.mp4
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable()) {
			file1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/PalmTest"+fileName1);
			if (file1.exists()) {
				Log.d("Vid_Play_Activity", "测试本地化资源的视频的第一条视频路径输出:"+file1.getAbsolutePath());
				isNativeVideo = true;
			}
		}



		/*//从URL中获取视频的文件名
		String fileName = path.substring(path.lastIndexOf("/")+1);
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable()) {
			File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/PalmTest");
			if (file.exists()) {
				File[] file_ExternalStorageDir_list = file.listFiles();
				for (int i = 0; i < file_ExternalStorageDir_list.length; i++) {
					Log.d("jenfee", "打印:" + file_ExternalStorageDir_list[i].toString());
					if (file_ExternalStorageDir_list[i].getName().equals(fileName)) {
						file_native_video_path = new File(Environment.getExternalStorageDirectory()+"/PalmTest/"+fileName);
						Log.d("jenfee", "成功创建视频文件路径:" + file_native_video_path.toString());
					}
				}
			}
		}*/

		String mTitle = intent.getStringExtra("video_name");
		mVideoView = (JjVideoView) findViewById(R.id.JjvideoView);

		mLoadView = findViewById(R.id.sdk_ijk_progress_bar_layout);//加载视图
		mLoadText = (TextView) findViewById(R.id.sdk_ijk_progress_bar_text);//初始加载文字
		mLoadBufferView = findViewById(R.id.sdk_load_layout); //缓冲加载视图
		mLoadBufferTextView = (TextView) findViewById(R.id.sdk_sdk_ijk_load_buffer_text); //缓冲加载文字
		mVideoView.setMediaController(new VideoJjMediaContoller(this, true));
		mLoadBufferTextView.setTextColor(Color.WHITE); //缓冲加载文字设置为白色

		/***
		 * 用户自定义的外链 可获取外链点击时间
		 */
		mVideoView.setOnJjOutsideLinkClick(new OnJjOutsideLinkClickListener() {

			@Override
			public void onJjOutsideLinkClick(String arg0) {
			}
		});
		/***
		 * 设置缓冲
		 */
		mVideoView.setMediaBufferingView(mLoadBufferView);
		/***
		 * 视频开始加载数据
		 */
		mVideoView.setOnJjOpenStart(new OnJjOpenStartListener() {

			@Override
			public void onJjOpenStart(String arg0) {
				mLoadText.setText(arg0);
			}
		});
		/***
		 * 视频开始播放
		 */
		mVideoView.setOnJjOpenSuccess(new OnJjOpenSuccessListener() {

			@Override
			public void onJjOpenSuccess() {
				mLoadView.setVisibility(View.GONE);
			}
		});
		// 缓冲开始
		mVideoView.setOnJjBufferStart(new OnJjBufferStartListener() {

			@Override
			public void onJjBufferStartListener(int arg0) {
				Log.e("Video++", "====================缓冲值=====" + arg0);
			}
		});
		mVideoView.setOnJjBufferingUpdateListener(new OnJjBufferingUpdateListener() {

					@Override
					public void onJjBufferingUpdate(int arg1) {
						// TODO Auto-generated method stub
						if (mLoadBufferView.getVisibility() == View.VISIBLE) {
							mLoadBufferTextView.setText(String.valueOf(mVideoView.getBufferPercentage()) + "%");
							Log.e("Video++", "====================缓冲值2=====" + arg1);
						}
					}
				});
		// 缓冲完成
		mVideoView.setOnJjBufferComplete(new OnJjBufferCompleteListener() {

			@Override
			public void onJjBufferCompleteListener(int arg0) {
			}
		});
		/***
		 * 注意VideoView 要调用下面方法 配置你用户信息
		 */
		mVideoView.setVideoJjAppKey("Eyjve7J6g");
		mVideoView.setVideoJjPageName("cn.gdgst.palmtest");
		// mVideoView.setMediaCodecEnabled(true);// 是否开启 硬解 硬解对一些手机有限制
		// 判断是否源 0 代表 8大视频网站url 3代表自己服务器的视频源 2代表直播地址 1代表本地视频(手机上的视频源),4特殊需求
		mVideoView.setVideoJjType(1);
		/*if (file_native_video_path != null || !file_native_video_path.equals("")) {
			Log.e("jenfee", "从本地播放视频");
			Toast.makeText(this, "从本地播放视频", Toast.LENGTH_SHORT).show();
			mVideoView.setResourceVideo(file_native_video_path.getAbsolutePath());// 开启播放
		}*/

		if (isNativeVideo) {
			Log.e("jenfee", "从本地播放视频");
			Toast.makeText(this, "从本地播放视频", Toast.LENGTH_SHORT).show();
			mVideoView.setResourceVideo(file1.getAbsolutePath());// 开启播放
		}else {
			Log.e("jenfee", "从网络播放视频");
			Toast.makeText(this, "从网络播放视频", Toast.LENGTH_SHORT).show();
			mVideoView.setResourceVideo(path);// 开启播放
		}

		//mVideoView.setResourceVideo(path1);// 开启播放
		mVideoView.setVideoJjTitle(mTitle);// 设置视频标题
		mVideoView.stopPlayback(); // 停止视频播放，并释放资源
		mVideoView.setVideoJjSaveExitTime(true); // true 二次进入播放从上次退出位置开始播放 false
		// 不记录退出位置
		mVideoView.setMediaCodecEnabled(true); // 设置是否开启硬解
	}

}
