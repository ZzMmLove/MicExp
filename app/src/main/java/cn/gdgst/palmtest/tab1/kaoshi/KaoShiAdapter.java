package cn.gdgst.palmtest.tab1.kaoshi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.gdgst.entity.KaoShi;
import cn.gdgst.palmtest.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orhanobut.logger.Logger;
import cn.gdgst.palmtest.service.CollectService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class KaoShiAdapter extends BaseAdapter {
	private List<KaoShi> ChuangKeList;
	private Context context;
	// 定义当前listview是否在滑动状态
	private ArrayList<Integer> attentionArr = new ArrayList<Integer>();

	private int COLLECTTAG = 0;// 取消nomal 1为收藏true
	private Handler handler = new Handler();
	/**
	 * 初始化sharedPreferences
	 */
	private SharedPreferences sp = null;

	public KaoShiAdapter(Context context, List<KaoShi> chuangkeList) {
		this.context = context;
		this.ChuangKeList = chuangkeList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return ChuangKeList.size();

	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return ChuangKeList.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View view, ViewGroup viewgroup) {
		// TODO Auto-generated method stub
		viewholder vd = new viewholder();
		final KaoShi expitem = ChuangKeList.get(position);
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.video_list_item, null);
			vd = new viewholder();
			vd.vid_img = (ImageView) view.findViewById(R.id.vid_img);
			vd.iv_share = (ImageView) view.findViewById(R.id.iv_share);
			vd.vid_tv = (TextView) view.findViewById(R.id.vid_tv);
			vd.iv_collect = (ImageView) view.findViewById(R.id.iv_collect);
			view.setTag(vd);// 给View添加一个格外的数据
		} else {
			vd = (viewholder) view.getTag();// 把数据取出来
		}

		ImageLoader.getInstance().displayImage(expitem.getImg_url(), vd.vid_img);

		vd.vid_tv.setText(expitem.getName()); // exp_tv设置文本


		final ImageView iv_collect = vd.iv_collect;
		if (attentionArr.contains(position)) {
			if (COLLECTTAG == 1) {
				iv_collect.setImageResource(R.mipmap.detail_interaction_bar_favorite_pressed);
			} else {
				iv_collect.setImageResource(R.mipmap.detail_interaction_bar_favorite);
			}

		} else {
			iv_collect.setImageResource(R.mipmap.detail_interaction_bar_favorite);
		}
		vd.iv_collect.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (COLLECTTAG == 0) {

					new Thread() {
						public void run() {
							try {
								Thread.sleep(100);
								CollectService updateInfoService = new CollectService(context);
								sp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
								String accessToken = sp.getString("accessToken", "");
								String model = "kaoshi";
								Map<String, String> rawParams = new HashMap<String, String>();
								rawParams.put("accessToken", accessToken);
								rawParams.put("model", model);
								rawParams.put("id", ChuangKeList.get(position).getId().toString());
								int getaddInfo = updateInfoService.getaddInfo(rawParams);
								if (getaddInfo==0) {
									handler.post(new Runnable() {
										@Override
										public void run() {
											iv_collect.setImageResource(R.mipmap.detail_interaction_bar_favorite_pressed);
											COLLECTTAG = 1;
											attentionArr.add(position);// 在点击时将position加入其中
											Logger.i("collect"+ "收藏成功");
											Toast.makeText(context, "收藏成功", Toast.LENGTH_SHORT).show();

											Logger.i("spid"+expitem.getId());

										}
									});
								}else if (getaddInfo==2) {
									handler.post(new Runnable() {
										@Override
										public void run() {
											Toast.makeText(context, "已经收藏过了", Toast.LENGTH_SHORT).show();
										}
									});
								}else {
									handler.post(new Runnable() {
										@Override
										public void run() {
											Toast.makeText(context, "请先进行登录", Toast.LENGTH_SHORT).show();
										}
									});
								}


							} catch (Exception e) {
								e.printStackTrace();


							}
						}
					}.start();

				} else {
					new Thread() {
						public void run() {
							try {
								Thread.sleep(100);
								CollectService updateInfoService = new CollectService(context);
								sp = context.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
								String accessToken = sp.getString("accessToken", "");
								String model = "kaoshi";
								Map<String, String> rawParams = new HashMap<String, String>();
								rawParams.put("accessToken", accessToken);
								rawParams.put("model", model);
								rawParams.put("id", ChuangKeList.get(position).getId().toString());
								Boolean getaddInfo = updateInfoService.getDeleteInfo(rawParams);
								if (getaddInfo) {
									handler.post(new Runnable() {
										@Override
										public void run() {
											iv_collect.setImageResource(R.mipmap.detail_interaction_bar_favorite);
											attentionArr.add(position);// 在点击时将position加入其中
											COLLECTTAG = 0;

											Logger.i("collect"+ "取消收藏");
											Toast.makeText(context, "取消收藏", Toast.LENGTH_SHORT).show();

											Logger.i("spid"+expitem.getId());

										}
									});
								}else {
									handler.post(new Runnable() {
										@Override
										public void run() {
											Toast.makeText(context, "请先进行登录", Toast.LENGTH_SHORT).show();
										}
									});
								}

							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}.start();
				}

			}
		});


		vd.iv_share.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				ShareSDK.initSDK(context);

				OnekeyShare oks = new OnekeyShare();
				// 关闭sso授权
				oks.disableSSOWhenAuthorize();
				// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
				// oks.setNotification(R.drawable.ic_launcher,
				// getString(R.string.app_name));
				// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
				oks.setTitle(expitem.getName());
				// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
				oks.setTitleUrl(expitem.getVideo_url());
				// text是分享文本，所有平台都需要这个字段
				oks.setText(expitem.getName() + expitem.getVideo_url());
				// 设置分享照片的url地址，如果没有可以不设置
				oks.setImageUrl(expitem.getImg_url());
				// url仅在微信（包括好友和朋友圈）中使用
				oks.setUrl(expitem.getVideo_url());
				// comment是我对这条分享的评论，仅在人人网和QQ空间使用
				oks.setComment("我是测试评论文本");
				// site是分享此内容的网站名称，仅在QQ空间使用
				oks.setSite("实验掌上通");
				// siteUrl是分享此内容的网站地址，仅在QQ空间使用
				oks.setSiteUrl("http://www.shiyan360.cn/");
				// 启动分享GUI
				oks.show(context);
			}
		});

		return view;
	}

	// 视图控件内部类
	public class viewholder {
		private ImageView vid_img, iv_share,iv_collect;
		private TextView vid_tv;
	}


}
