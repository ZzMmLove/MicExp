package cn.com.video.venvy.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import cn.com.video.venvy.R;
import cn.com.video.venvy.param.Gestures;
import cn.com.video.venvy.param.JjMediaContoller;
import cn.com.video.venvy.param.MediaContollerTouchListener;
import cn.com.video.venvy.param.MediaPlayerControl;

/**
 * Created by super on 2015/7/25. Video++��Ƶ������ �Զ���
 * �޸�venvy_video_user_media_controller_sdk.xml��д
 */
public class UsetMediaContoller extends JjMediaContoller {
	/**
	 * �������ӿ�
	 */
	private MediaPlayerControl mPlayer;
	/***
	 * ����Ƶ�ؼ�
	 */
	private ImageButton mLock;
	/**
	 * �϶����ȿؼ�
	 */
	private SeekBar mProgress;
	/**
	 * ��Ƶ��ʱ�� ��ǰʱ��
	 */
	private TextView mEndTime, mCurrentTime;
	/**
	 * ��Ƶ��ʱ��
	 */
	private long mDuration;
	/**
	 * �Ƿ���ʾ
	 */
	private boolean mShowing;
	/***
	 * �������Ƿ�������״̬
	 */
	private boolean mScreenLocked = false;
	/**
	 * �Ƿ����϶�״̬
	 */
	private boolean mDragging;
	/***
     *
     */
	private boolean mInstantSeeking = true;
	/***
	 * Ĭ��ʱ��
	 */
	private static final int DEFAULT_TIME_OUT = 3000;
	/***
	 * ���� ����һֱ��ʾ
	 */
	private static final int SHOW_TIME_CONTINUE = 0;
	/**
	 * ������Ĭ�ϵ���ʾʱ��
	 */
	private static final int DEFAULT_LONG_TIME_SHOW = 1200000;
	/**
	 * SeekBar�϶���Ĭ��ֵ
	 */
	private static final int DEFAULT_SEEKBAR_VALUE = 1000;
	private static final int TIME_TICK_INTERVAL = 1000;
	/**
	 * ���ú�����View
	 */
	private ImageButton mDirectionView;
	/***
	 * ��ͣ��ť
	 */
	private ImageButton mPauseButton;
	private View mMediaController;
	private View mControlsLayout;
	private View mSystemInfoLayout;
	private TextView mFileName;
	/***
	 * ����������
	 */
	private TextView mPopupInfoView;
	/**
	 * ����������View
	 */
	private TextView mQualityView;
	/**
	 * �رղ�����
	 */
	private ImageButton mBackButton;
	/***
	 * ���� ���Ȳ���
	 */
	private View mOperationVolLum;
	/***
	 * ��� ���� ����
	 */
	private View mPlanVolLum;
	/**
	 * ��� ���� ��ʾLogo
	 */
	private ImageView mPlanImage;
	/***
	 * ��� ���� ��ǰʱ��
	 */
	private long mPlanPos;
	/**
	 * ��� ���� ��ʾʱ��
	 */
	private TextView mPlanTimeView;
	/***
	 * ���ƿ��Ƶ�ͼƬ
	 */
	private ImageView mVolLumBg;
	/***
	 * ���ƿ��ƵĽ�����
	 */
	private TextView mVolLumTextView;
	private AudioManager mAM;
	private int mMaxVolume;
	private float mBrightness = 0.01f;
	private int mVolume = 0;
	private Handler mHandler;

	private Animation mAnimSlideInTop;
	private Animation mAnimSlideInBottom;
	private Animation mAnimSlideOutTop;
	private Animation mAnimSlideOutBottom;
	/**
	 * ���ƹ�����
	 */
	private Gestures mGestures;

	/***
	 * ���캯��
	 * 
	 * @param context
	 *            ������
	 */
	public UsetMediaContoller(Context context) {
		super(context);
		initResources();
	}

	/***
	 * ���캯��
	 * 
	 * @paramcontext
	 *            ������
	 */
	// public UsetMediaContoller(Context context, AttributeSet attrs) {
	// super(context, attrs);
	// lock(mScreenLocked);
	// }

	private void initResources() {
		mHandler = new MHandler(this);
		mAM = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
		mMaxVolume = mAM.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		mGestures = new Gestures(mContext);
		mGestures.setTouchListener(mTouchListener, true);
		mAnimSlideOutBottom = AnimationUtils.loadAnimation(mContext,
				R.anim.venvy_slide_out_bottom);
		mAnimSlideOutTop = AnimationUtils.loadAnimation(mContext,
				R.anim.venvy_slide_out_top);
		mAnimSlideInBottom = AnimationUtils.loadAnimation(mContext,
				R.anim.venvy_slide_in_bottom);
		mAnimSlideInTop = AnimationUtils.loadAnimation(mContext,
				R.anim.venvy_slide_in_top);
		mAnimSlideOutBottom
				.setAnimationListener(new Animation.AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						mMediaController.setVisibility(View.GONE);
						showButtons(false);
						mHandler.removeMessages(MSG_HIDE_SYSTEM_UI);
						mHandler.sendEmptyMessageDelayed(MSG_HIDE_SYSTEM_UI,
								DEFAULT_TIME_OUT);
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}
				});
		findViewItems(mRootView);
	}

	@Override
	public void setAnchorView(ViewGroup view) {
		super.setAnchorView(view);
	}

	/***
	 * find���ز��ֵĿؼ�
	 * 
	 * @param v
	 *            RootView
	 */
	private void findViewItems(View v) {
		/***
		 * �м䵯������ؼ�
		 */
		mPopupInfoView = (TextView) v
				.findViewById(R.id.sdk_ijk_show_popup_view);
		/**
		 * ���¿���������
		 */
		mMediaController = mRootView;
		/**
		 * ͷ������������
		 */
		mSystemInfoLayout = v.findViewById(R.id.sdk_media_controller_panel);
		/**
		 * ��Ƶ��ʱ��
		 */
		mEndTime = (TextView) v
				.findViewById(R.id.sdk_media_controller_time_total);
		/**
		 * ��Ƶ���Ź�ȥʱ��
		 */
		mCurrentTime = (TextView) v
				.findViewById(R.id.sdk_media_controller_time_current);
		/**
		 * ��Ƶ����
		 */
		mFileName = (TextView) v
				.findViewById(R.id.sdk_media_controller_video_name);
		/***
		 * �ײ�����������
		 */
		mControlsLayout = v.findViewById(R.id.sdk_media_controller_control);
		/**
		 * ���ذ�ť
		 */
		mBackButton = (ImageButton) v
				.findViewById(R.id.sdk_media_controller_back);
		mBackButton.setOnClickListener(mBackListener);
		/**
		 * ���� ���Ȳ���
		 */
		mOperationVolLum = v
				.findViewById(R.id.sdk_media_controller_operation_volume_brightness);
		/**
		 * ��� ���� ����
		 */
		mPlanVolLum = v.findViewById(R.id.sdk_media_controller_plan_layout);
		/**
		 * ��� ���� Logo
		 */
		mPlanImage = (ImageView) v
				.findViewById(R.id.sdk_sdk_media_controller_plan);
		/**
		 * ��� ���� ʱ��
		 */
		mPlanTimeView = (TextView) v
				.findViewById(R.id.sdk_sdk_media_controller_plan_time);
		/**
		 * ���� ������ʾ��ImageView
		 */
		mVolLumBg = (ImageView) v
				.findViewById(R.id.sdk_media_controller_operation_bg);
		/***
		 * ���� ������ʾ��TextView
		 */
		mVolLumTextView = (TextView) v
				.findViewById(R.id.sdk_media_controller_tv_volume_percentage);
		/***
		 * ���ؼ�
		 */
		mLock = (ImageButton) v
				.findViewById(R.id.sdk_media_controller_video_lock);
		mLock.setOnClickListener(mLockClickListener);
		/***
		 * ��ͣ ��ʼ ��ť
		 */
		mPauseButton = (ImageButton) v
				.findViewById(R.id.sdk_media_controller_play_pause);
		mPauseButton.setOnClickListener(mPauseListener);
		/**
		 * �϶��ؼ�
		 */
		mProgress = (SeekBar) v.findViewById(R.id.sdk_media_controller_seek);
		mProgress.setOnSeekBarChangeListener(mSeekListener);
		mProgress.setMax(DEFAULT_SEEKBAR_VALUE);
		/***
		 * ���ú�����
		 */
		mDirectionView = (ImageButton) v
				.findViewById(R.id.sdk_media_controller_direction);
		mDirectionView.setOnClickListener(mDirectionListener);
		/**
		 * ����������View
		 */
		mQualityView = (TextView) v
				.findViewById(R.id.sdk_media_controller_video_mass);
	}

	/**
	 * ���ÿ���������״̬����
	 * 
	 * @param toLock
	 */
	private void lock(boolean toLock) {
		if (toLock) {
			mLock.setImageResource(R.drawable.venvy_sdk_media_controller_lock_bg);
			mProgress.setEnabled(false);
			if (mScreenLocked != toLock)
				setOperationInfo(
						mContext.getString(R.string.sdk_video_screen_locked_string),
						1000);
		} else {
			mLock.setImageResource(R.drawable.venvy_sdk_media_controller_unlock_bg);
			mProgress.setEnabled(true);
			if (mScreenLocked != toLock)
				setOperationInfo(
						mContext.getString(R.string.sdk_video_screen_unlocked_string),
						1000);
		}
		mScreenLocked = toLock;
		mGestures.setTouchListener(mTouchListener, !mScreenLocked);
	}

	/***
	 * �ж���Ļ�Ƿ�����״̬
	 * 
	 * @return
	 */
	public boolean isLocked() {
		return mScreenLocked;
	}

	/***
	 * ���õ�������������
	 * 
	 * @param info
	 * @param time
	 */
	private void setOperationInfo(String info, long time) {
		mPopupInfoView.setText(info);
		mPopupInfoView.setVisibility(View.VISIBLE);
		mHandler.removeMessages(MSG_HIDE_OPERATION_INFO);
		mHandler.sendEmptyMessageDelayed(MSG_HIDE_OPERATION_INFO, time);
	}

	private static final int MSG_FADE_OUT = 1;
	private static final int MSG_SHOW_PROGRESS = 2;
	private static final int MSG_HIDE_SYSTEM_UI = 3;
	private static final int MSG_TIME_TICK = 4;
	private static final int MSG_HIDE_OPERATION_INFO = 5;
	private static final int MSG_HIDE_OPERATION_VOLLUM = 6;

	/**
	 * �Զ��� Handler ��Ϣ��
	 */
	private static class MHandler extends Handler {
		private WeakReference<UsetMediaContoller> mc;

		public MHandler(UsetMediaContoller mc) {
			this.mc = new WeakReference<>(mc);
		}

		@Override
		public void handleMessage(Message msg) {
			UsetMediaContoller c = mc.get();
			if (c == null)
				return;

			switch (msg.what) {
			case MSG_FADE_OUT:
				c.hide();
				break;
			case MSG_SHOW_PROGRESS:
				long pos = c.setProgress();
				if (!c.mDragging && c.mShowing) {
					msg = obtainMessage(MSG_SHOW_PROGRESS);
					sendMessageDelayed(msg, 1000 - (pos % 1000));
					c.updatePausePlay();
				}
				break;
			case MSG_HIDE_SYSTEM_UI:
				if (!c.mShowing)
					c.showSystemUi(false);
				break;
			case MSG_TIME_TICK:
				sendEmptyMessageDelayed(MSG_TIME_TICK, TIME_TICK_INTERVAL);
				break;
			case MSG_HIDE_OPERATION_INFO:
				c.mPopupInfoView.setVisibility(View.GONE);
				break;
			case MSG_HIDE_OPERATION_VOLLUM:
				c.mOperationVolLum.setVisibility(View.GONE);
				break;
			}
		}
	}

	/**
	 * ����SeekBar����
	 * 
	 * @return
	 */
	private long setProgress() {
		if (mPlayer == null || mDragging)
			return 0;

		long position = mPlayer.getCurrentPosition();
		long duration = mPlayer.getDuration();
		if (duration > 0) {
			long pos = 1000L * position / duration;
			mProgress.setProgress((int) pos);
		}
		int percent = mPlayer.getBufferPercentage();
		mProgress.setSecondaryProgress(percent * 10);

		mDuration = duration;

		mEndTime.setText(generateTime(mDuration));
		mCurrentTime.setText(generateTime(position));

		return position;
	}

	/***
	 * @param showButtons
	 */
	private void showButtons(boolean showButtons) {
		Window window = mContext.getWindow();
		WindowManager.LayoutParams layoutParams = window.getAttributes();
		float val = showButtons ? -1 : 0;
		try {
			Field buttonBrightness = layoutParams.getClass().getField(
					"buttonBrightness");
			buttonBrightness.set(layoutParams, val);
		} catch (Exception e) {
		}
		window.setAttributes(layoutParams);
	}

	/***
	 * ������ͣ ��ʼ���Ű�ť��ͣ
	 */
	private void updatePausePlay() {
		if (mPlayer.isPlaying())
			mPauseButton
					.setImageResource(R.drawable.venvy_sdk_media_controller_pause_bg);
		else
			mPauseButton
					.setImageResource(R.drawable.venvy_sdk_media_controller_play_bg);
	}

	/***
	 * �������� ���Ʋ���
	 * 
	 * @param scale
	 */
	private void setBrightnessScale(float scale) {
		mVolLumBg
				.setImageResource(R.drawable.venvy_sdk_media_controller_bright_big);
		int a = (int) (scale * 100);
		mVolLumTextView.setText(String.valueOf(a));
		mOperationVolLum.setVisibility(View.VISIBLE);
	}

	/***
	 * �������� ���Ʋ���
	 * 
	 * @param scale
	 *            ������
	 */
	private void setVolumeScale(float scale) {
		if (scale != 0) {
			mVolLumBg
					.setImageResource(R.drawable.venvy_sdk_media_controller_volume);
		} else {
			mVolLumBg
					.setImageResource(R.drawable.venvy_sdk_media_controller_silence);
		}
		mVolLumTextView.setText(String.valueOf((int) scale));
		mOperationVolLum.setVisibility(View.VISIBLE);
	}

	/***
	 * ��������
	 * 
	 * @param f
	 *            ����
	 */
	private void setBrightness(float f) {
		WindowManager.LayoutParams lp = mContext.getWindow().getAttributes();
		lp.screenBrightness = f;
		if (lp.screenBrightness > 1.0f)
			lp.screenBrightness = 1.0f;
		else if (lp.screenBrightness < 0.01f)
			lp.screenBrightness = 0.01f;
		mContext.getWindow().setAttributes(lp);
	}

	/***
	 * ��������
	 * 
	 * @param v
	 *            ����
	 */
	private void setVolume(int v) {
		if (v > mMaxVolume) {
			v = mMaxVolume;
		} else if (v < 0) {
			v = 0;
		}
		mAM.setStreamVolume(AudioManager.STREAM_MUSIC, v, 0);
		setVolumeScale((v * 100) / mMaxVolume);
	}

	/***
	 * SeekBar������
	 */
	private SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
		@Override
		public void onStartTrackingTouch(SeekBar bar) {
			mDragging = true;
			show(3600000);
			mHandler.removeMessages(MSG_SHOW_PROGRESS);
			if (mInstantSeeking) {
				mAM.setStreamMute(AudioManager.STREAM_MUSIC, true);
			}
		}

		@Override
		public void onProgressChanged(SeekBar bar, int progress,
				boolean fromuser) {
			if (!fromuser)
				return;

			long newPosition = (mDuration * progress) / 1000;
			String time = generateTime(newPosition);
			setOperationInfo(time, 1500);
			mCurrentTime.setText(time);
		}

		@Override
		public void onStopTrackingTouch(SeekBar bar) {
			if (!mInstantSeeking) {
				mPlayer.seekTo((mDuration * bar.getProgress()) / 1000);
			}
			show(DEFAULT_TIME_OUT);
			mAM.setStreamMute(AudioManager.STREAM_MUSIC, false);
			mDragging = false;
			if (mDuration != 0) {
				long curPos = (mDuration * bar.getProgress()) / 1000;
				long proPos = 1000L * curPos / mDuration;
				mProgress.setProgress((int) proPos);
				mPlayer.seekTo(curPos);
			}
		}
	};

	/**
	 * ���� ��� ���˽����� ���ƽ�������
	 * 
	 * @return
	 */
	private long setPlanProgress(long position) {
		if (mPlayer == null || mDragging)
			return 0;
		long duration = mPlayer.getDuration();
		if (duration > 0) {
			long pos = 1000L * position / duration;
			mProgress.setProgress((int) pos);
		}
		int percent = mPlayer.getBufferPercentage();
		mProgress.setSecondaryProgress(percent * 10);

		mDuration = duration;

		mEndTime.setText(generateTime(mDuration));
		mCurrentTime.setText(generateTime(position));
		mPlayer.seekTo(mPlanPos);
		return position;
	}

	// TODO

	/***
	 * ���ÿ������
	 */
	private void setForwardPlan() {
		mPlanImage
				.setImageResource(R.drawable.venvy_sdk_media_controller_forward_bg);
		long mDuration = mPlayer.getDuration();
		if (mPlanPos < mDuration - 16 * 1000) {
			mPlanPos += 3 * 1000;
		} else {
			mPlanPos = mDuration - 10 * 1000;
		}
		mPlanTimeView.setText(generateTime(mPlanPos) + "/"
				+ generateTime(mDuration));
		mPlanVolLum.setVisibility(VISIBLE);
	}

	/***
	 * ���ÿ��˷���
	 */
	private void setBackwardPlan() {
		mPlanImage
				.setImageResource(R.drawable.venvy_sdk_media_controller_backward_bg);
		long mDuration = mPlayer.getDuration();
		if (mPlanPos > 3 * 1000) {
			mPlanPos -= 3 * 1000;
		} else {
			mPlanPos = 3 * 1000;
		}
		mPlanTimeView.setText(generateTime(mPlanPos) + "/"
				+ generateTime(mDuration));
		mPlanVolLum.setVisibility(VISIBLE);
	}

	/**
	 * ת��ʱ����ʾ
	 * 
	 * @param time
	 *            ����
	 * @return
	 */
	public static String generateTime(long time) {
		int totalSeconds = (int) (time / 1000);
		int seconds = totalSeconds % 60;
		int minutes = (totalSeconds / 60) % 60;
		int hours = totalSeconds / 3600;

		return hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes,
				seconds) : String.format("%02d:%02d", minutes, seconds);
	}

	/***
	 * ���ú�����
	 */
	private OnClickListener mDirectionListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (!isLocked()) {
				setScreenlandscape(mContext);
			}
		}
	};

	/***
	 * ��������Ϊ���� ��
	 * 
	 * @param context
	 */
	public static void setScreenlandscape(Activity context) {
		Log.e("Video++", "===================================�Զ��������");
		if (context.getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
			context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);// ����Ϊ����

		} else {
			context.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// ����Ϊ����
		}
	}

	/***
	 * ������������
	 */
	private MediaContollerTouchListener mTouchListener = new MediaContollerTouchListener() {
		/***
		 * ���ƿ�ʼ
		 */
		@Override
		public void onGestureBegin() {
			mBrightness = mContext.getWindow().getAttributes().screenBrightness;
			mVolume = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);
			if (mBrightness <= 0.00f)
				mBrightness = 0.50f;
			if (mBrightness < 0.01f)
				mBrightness = 0.01f;
			if (mVolume < 0)
				mVolume = 0;
			/**
			 * ���ÿ�� ���˵�ǰʱ��
			 */
			mPlanPos = mPlayer.getCurrentPosition();
		}

		/***
		 * ���ƽ���
		 */
		@Override
		public void onGestureEnd() {
			if (mPlanVolLum.getVisibility() == VISIBLE) {
				if (mPlanPos != 0) {
					setPlanProgress(mPlanPos);
				}
			}
			mOperationVolLum.setVisibility(View.GONE);
			mPlanVolLum.setVisibility(GONE);
		}

		/***
		 * ��������
		 * 
		 * @param percent
		 */
		@Override
		public void onLeftSlide(float percent) {
			setBrightness(mBrightness + percent);
			setBrightnessScale(mContext.getWindow().getAttributes().screenBrightness);
		}

		/***
		 * ��������
		 * 
		 * @param percent
		 */
		@Override
		public void onRightSlide(float percent) {
			int v = (int) (percent * mMaxVolume) + mVolume;
			setVolume(v);
		}

		/**
		 * ����
		 * 
		 * @param percent
		 */
		@Override
		public void onLeftSpeedSlide(float percent) {
			setBackwardPlan();
		}

		/**
		 * ���
		 * 
		 * @param percent
		 */
		@Override
		public void onRightSpeedSlide(float percent) {
			setForwardPlan();
		}

		/***
		 * ����
		 */
		@Override
		public void onSingleTap() {
			if (mShowing)
				hide();
			else
				show();
		}

		/***
		 * ˫��
		 */
		@Override
		public void onDoubleTap() {
			if (mPlayer.isPlaying()) {
				show(SHOW_TIME_CONTINUE);
				mPlayer.pause();
			} else {
				mPlayer.start();
			}
		}

		@Override
		public void onScale(float scaleFactor, int state) {
			switch (state) {
			case Gestures.SCALE_STATE_BEGIN:
				break;
			case Gestures.SCALE_STATE_SCALEING:
				break;
			case Gestures.SCALE_STATE_END:
				break;
			}
		}

		@Override
		public void onLongPress() {
			doPauseResume();
		}
	};
	/***
	 * ������������ʱ��
	 */
	private View.OnClickListener mLockClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			hide();
			lock(!mScreenLocked);
			show();
		}
	};

	/***
	 * ���ò�������ʼ ��ͣ
	 */
	private void doPauseResume() {
		if (mPlayer.isPlaying())
			mPlayer.pause();
		else
			mPlayer.start();
		updatePausePlay();
	}

	/***
	 * ��Ƶ���Ű�ť����
	 */
	private View.OnClickListener mPauseListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!isLocked()) {
				if (!mCompletiond) {
					if (mPlayer.isPlaying())
						show(DEFAULT_LONG_TIME_SHOW);
					else
						show();
				} else {
					mControlsLayout.startAnimation(mAnimSlideOutTop);
					mSystemInfoLayout.startAnimation(mAnimSlideOutBottom);
					mShowing = false;
					mCompletiond = false;
				}
				doPauseResume();
			}
		}
	};
	private View.OnClickListener mBackListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (!isLocked()) {
				if (mPlayer.isPlaying())
					mContext.finish();
			}
		}
	};

	/***
	 * ��дTouch�¼�
	 * 
	 * @param event
	 * @return
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mHandler.removeMessages(MSG_HIDE_SYSTEM_UI);
		mHandler.sendEmptyMessageDelayed(MSG_HIDE_SYSTEM_UI, DEFAULT_TIME_OUT);
		return mGestures.onTouchEvent(event) || super.onTouchEvent(event);
	}

	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		show(DEFAULT_TIME_OUT);
		return false;
	}

	/***
	 * �¼��ַ�
	 * 
	 * @param event
	 * @return
	 */
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int keyCode = event.getKeyCode();

		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_MUTE:
			return super.dispatchKeyEvent(event);
		case KeyEvent.KEYCODE_VOLUME_UP:
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			mVolume = mAM.getStreamVolume(AudioManager.STREAM_MUSIC);
			int step = keyCode == KeyEvent.KEYCODE_VOLUME_UP ? 1 : -1;
			setVolume(mVolume + step);
			mHandler.removeMessages(MSG_HIDE_OPERATION_VOLLUM);
			mHandler.sendEmptyMessageDelayed(MSG_HIDE_OPERATION_VOLLUM, 500);
			return true;
		}

		if (isLocked()) {
			show();
			return true;
		}

		if (event.getRepeatCount() == 0
				&& (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
						|| keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_SPACE)) {
			doPauseResume();
			show(DEFAULT_TIME_OUT);
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP) {
			if (mPlayer.isPlaying()) {
				mPlayer.pause();
				updatePausePlay();
			}
			return true;
		} else {
			show(DEFAULT_TIME_OUT);
		}
		return super.dispatchKeyEvent(event);
	}

	// ////////////////////Ҫ��д�ķ���
	/***
	 * ������Ƶ����
	 */
	@Override
	public void setFileName(String name) {
		super.setFileName(name);
		mFileName.setText(name);
	}

	/***
	 * ���ؿ�����
	 */
	@Override
	public void hide() {
		super.hide();
		if (mShowing) {
			try {
				mHandler.removeMessages(MSG_TIME_TICK);
				mHandler.removeMessages(MSG_SHOW_PROGRESS);
				if (mPlayer.isPlaying()) {
					mControlsLayout.startAnimation(mAnimSlideOutTop);
					mSystemInfoLayout.startAnimation(mAnimSlideOutBottom);
					mShowing = false;
				} else {
					mShowing = true;
				}
			} catch (IllegalArgumentException ex) {
			}

		}
	}

	/***
	 * ������ʾʱ��Ĭ��ʱ��
	 */
	@Override
	public void show() {
		super.show();
	}

	/***
	 * �������¿�������ʾʱ��
	 * 
	 * @param timeout
	 */
	@Override
	public void show(int timeout) {
		super.show(timeout);
		if (timeout != 0) {
			mHandler.removeMessages(MSG_FADE_OUT);
			mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_FADE_OUT),
					timeout);
		}
		if (!mShowing) {
			showButtons(true);
			mHandler.removeMessages(MSG_HIDE_SYSTEM_UI);
			showSystemUi(true);
			mPauseButton.requestFocus();
			if (mPlayer.isPlaying()) {
				mControlsLayout.startAnimation(mAnimSlideInTop);
				mSystemInfoLayout.startAnimation(mAnimSlideInBottom);
				mShowing = true;
			}
			mMediaController.setVisibility(View.VISIBLE);
			updatePausePlay();
			mHandler.sendEmptyMessage(MSG_TIME_TICK);
			mHandler.sendEmptyMessage(MSG_SHOW_PROGRESS);

		}
	}

	/***
	 * ��ȡ��ƵԴ ������ mode==0 1080P mode==1 ���� mode==2 ���� mode==3 ���� mode==4 ����
	 * ���������ƿ��Զ���
	 */
	private TextView m1080;// Ҫ��ʾ��
	private TextView m720;
	private TextView m480P;
	private TextView m360P;
	// @Override
	// public void getVideoQuality(List<Integer> data) {
	// super.getVideoQuality(data);
	// // for (int x = 0; x < data.size(); x++) {
	// // int mode = data.get(x);
	// // switch (mode) {
	// // case 0:
	// // m1080.setText("1080p");
	// // break;
	// // case 1:
	// // m720.setText("720p");
	// // break;
	// // case 2:
	// // m480P.setText("480P");
	// // break;
	// // case 3:
	// // m360P.setText("360P");
	// // break;
	// // default:
	// // break;
	// // }
	// // }
	// }

	// �������л��������m1080
	private OnClickListener m1080pListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			mPlayer.setVideoQuality(0);
		}
	};

	/***
	 * ���ÿ�����
	 */
	@Override
	public void setMediaPlayer(MediaPlayerControl player) {
		super.setMediaPlayer(player);
		mPlayer = player;
		updatePausePlay();
	}

	/***
	 * �����������
	 */
	@SuppressLint("NewApi")
	@Override
	public void setVideoCofLand() {
		// TODO Auto-generated method stub
		super.setVideoCofLand();
		mContext.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	/***
	 * �����������
	 */
	@SuppressLint("NewApi")
	@Override
	public void setVideoCofPort() {
		// TODO Auto-generated method stub
		super.setVideoCofPort();
		mContext.getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	private boolean mCompletiond;// �ж��Ƿ���Ƶ���Ž���

	/***
	 * ��Ƶ��������Զ��ص�
	 */
	@Override
	public void onVideoCompletion() {
		super.onVideoCompletion();
		mCompletiond = true;
		show();
	}
}
