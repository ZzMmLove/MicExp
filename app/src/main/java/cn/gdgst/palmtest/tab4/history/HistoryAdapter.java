package cn.gdgst.palmtest.tab4.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cn.gdgst.palmtest.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import cn.gdgst.palmtest.Entitys.HistoryEntity;

import java.util.List;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class HistoryAdapter extends BaseAdapter {
	private List<HistoryEntity> HistoryList;
	private Context context;
	// 定义当前listview是否在滑动状态

	public HistoryAdapter(Context context, List<HistoryEntity> HistoryList) {
		this.context = context;
		this.HistoryList = HistoryList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return HistoryList.size();

	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return HistoryList.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewgroup) {
		// TODO Auto-generated method stub
		viewholder vd = new viewholder();
		final HistoryEntity expitem = HistoryList.get(position);
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.video_list_item, null);
			vd = new viewholder();
			vd.vid_img = (ImageView) view.findViewById(R.id.vid_img);
			vd.iv_share = (ImageView) view.findViewById(R.id.iv_share);
			vd.vid_tv = (TextView) view.findViewById(R.id.vid_tv);
			view.setTag(vd);// 给View添加一个格外的数据
		} else {
			vd = (viewholder) view.getTag();// 把数据取出来
		}

		ImageLoader.getInstance().displayImage(expitem.getImg_url(), vd.vid_img);

		vd.vid_tv.setText(expitem.getName()); // exp_tv设置文本

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
		private ImageView vid_img, iv_share;
		private TextView vid_tv;
	}

}