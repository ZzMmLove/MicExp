package cn.gdgst.palmtest.tab2;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.gdgst.entity.Video;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.orhanobut.logger.Logger;
import cn.gdgst.palmtest.service.CollectService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.gdgst.palmtest.R;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class Video_List_Adapter extends BaseAdapter {
	private List<Video> Video_List;
	private Context context;
	private ArrayList<Integer> attentionArr = new ArrayList<Integer>();

	private int COLLECTTAG = 0;// 取消nomal 1为收藏true
	private Handler handler = new Handler();
	 /**
     * 初始化sharedPreferences
     */
    private SharedPreferences sp = null;
    
	public Video_List_Adapter(Context context, List<Video> video_List) {
		this.context = context;
		this.Video_List = video_List;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Video_List.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return Video_List.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(final int position, View view, ViewGroup viewgroup) {
		viewholder vd = new viewholder();
		final Video video_item = Video_List.get(position);

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
		//利用Universal-Image-Loader图片加载框架加载网络图片
		ImageLoader.getInstance().displayImage(video_item.getImg_url(), vd.vid_img);
		vd.vid_tv.setText(video_item.getName());

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
								String model = "play";
								Map<String, String> rawParams = new HashMap<String, String>();
								rawParams.put("accessToken", accessToken);
								rawParams.put("model", model);
								rawParams.put("id", Video_List.get(position).getId().toString());
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
					    					
					    					Logger.i( ""+video_item.getId());
					    	                
					                    }
					                });
								}else if (getaddInfo==2) {
									handler.post(new Runnable() {
					                    @Override
					                    public void run() {
					                    	Toast.makeText(context, "已经收藏过了", Toast.LENGTH_SHORT).show();
					                    }
					                });
								}
								else {
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
								String model = "play";
								Map<String, String> rawParams = new HashMap<String, String>();
								rawParams.put("accessToken", accessToken);
								rawParams.put("model", model);
								rawParams.put("id", Video_List.get(position).getId().toString());
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
					    					
					    					Logger.i( ""+video_item.getId());
					    					
					    					
					    					sp = context.getSharedPreferences("addInfo", Context.MODE_PRIVATE);
					    	                Editor editor = sp.edit();// 获取编辑器
					    	                editor.putString("videoid", video_item.getId().toString());
					    	                editor.commit();// 提交修改
					    	                
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
				oks.setTitle(video_item.getName());
				// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
				oks.setTitleUrl(video_item.getVideo_url());
				// text是分享文本，所有平台都需要这个字段
				oks.setText(video_item.getName() + video_item.getVideo_url());
				// 设置分享照片的url地址，如果没有可以不设置
				oks.setImageUrl(video_item.getImg_url());
				// url仅在微信（包括好友和朋友圈）中使用
				oks.setUrl(video_item.getVideo_url());
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
