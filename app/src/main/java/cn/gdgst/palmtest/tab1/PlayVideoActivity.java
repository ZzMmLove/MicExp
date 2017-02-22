package cn.gdgst.palmtest.tab1;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.lidroid.xutils.view.annotation.ViewInject;
import cn.gdgst.palmtest.R;
import cn.gdgst.palmtest.base.BaseActivity;
import cn.gdgst.palmtest.rewrite.XListView;

import java.util.ArrayList;
import java.util.List;
public class PlayVideoActivity extends BaseActivity {
	@ViewInject(R.id.message_display)
	XListView message_display;
	List<Object> datas;
	@ViewInject(R.id.title)
	TextView title;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void setLayout() {
		// TODO Auto-generated method stub
		super.setLayout();
		setContentView(R.layout.tab1_main);
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		super.init();
		title.setText("实验资讯");
		initListView();
	}

	private void initListView() {
		// TODO Auto-generated method stub
		datas = new ArrayList<Object>();
		for (int i = 0; i < 8; i++) {
			Course c=new Course();
			c.setImg_id(R.mipmap.course1+i);
			c.setTv_complete("5");
			c.setTv_title("初中物理");
			c.setTv_total("10"+i);
			c.setTv_xuexi("4"+i);
			datas.add(c);
		}
		PlayAdapter yueAdapter = new PlayAdapter(datas,PlayVideoActivity.this);
		message_display.setAdapter(yueAdapter);
		message_display.setPullRefreshEnable(true);
		message_display.setPullLoadEnable(true);
	/*	message_display.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent i = new Intent();
				i.setClass(PlayActivity.this, PlayListActivity.class);
				startActivity(i);
			}
		});*/
	}

	@Override
	protected void loadData() {
		// TODO Auto-generated method stub
		super.loadData();
	}

	class PlayAdapter extends BaseAdapter {
		List<Object> list = new ArrayList<Object>();
		Context context;

		public PlayAdapter(List<Object> list, Context context) {
			this.list.addAll(list);
			this.context = context;
		}

		class HomeViewHolder {
			ImageView imageApoint;
			NetworkImageView imageView, imageViewInner;
			TextView tv_title, tv_distance, tv_sailcount, tv_price, tv_content,
					tv_listtext;
			Button goto_course;
		}

		public void remove() {
			list.clear();
			this.notifyDataSetChanged();
		}

		public void add(List<Object> lis) {
			this.list.addAll(lis);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			HomeViewHolder holder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(
						R.layout.tab1_listview_item, parent, false);
				holder = new HomeViewHolder();
				holder.imageView = (NetworkImageView) convertView.findViewById(R.id.home_like_item_image); //主图

				holder.imageViewInner = (NetworkImageView) convertView.findViewById(R.id.home_like_item_img_inner);

				holder.imageApoint = (ImageView) convertView.findViewById(R.id.home_like_item_img_apoint); //免预约

				holder.tv_title = (TextView) convertView.findViewById(R.id.home_like_item_title);//初中物理标题

				holder.tv_distance = (TextView) convertView.findViewById(R.id.home_like_item_long);  //课程数

				holder.goto_course = (Button) convertView.findViewById(R.id.goto_course);//进入课程

				holder.tv_price = (TextView) convertView.findViewById(R.id.home_like_item_price);//学习中

				holder.tv_listtext = (TextView) convertView.findViewById(R.id.home_like_item_text);

				holder.tv_content = (TextView) convertView.findViewById(R.id.home_like_item_content); //已完成

				convertView.setTag(holder);
			} else {
				holder = (HomeViewHolder) convertView.getTag();
			}

			Course c=(Course) list.get(position);
			holder.tv_title.setText(c.getTv_title());//初中物理标题

			holder.imageView.setBackgroundResource(c.getImg_id());//主图

			holder.tv_distance.setText("课程数："+c.getTv_total()+"节"); //课程数

			holder.imageApoint.setImageResource(R.mipmap.groupon_ic_free_appoint); //免预约

			holder.tv_price.setText("学习中："+c.getTv_xuexi()+"节");  //学习中

			holder.tv_content.setText("已完成："+c.getTv_complete()+"节");  //已完成

			//进入课程
			holder.goto_course.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent i = new Intent();
					i.setClass(PlayVideoActivity.this, PlayListActivity.class);
					startActivity(i);
				}
			});
			return convertView;
		}
	}
}
