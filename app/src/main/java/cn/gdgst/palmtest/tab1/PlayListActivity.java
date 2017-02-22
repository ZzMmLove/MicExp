package cn.gdgst.palmtest.tab1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.lidroid.xutils.view.annotation.ViewInject;
import cn.gdgst.palmtest.R;
import cn.gdgst.palmtest.tab2.Vid_Play_Activity;
import cn.gdgst.palmtest.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class PlayListActivity extends BaseActivity {
	@ViewInject(R.id.main_grid)
	GridView gridView;
	List<Grid_Item> lists;
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
		setContentView(R.layout.tab1_playlist_main);
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub
		super.init();
		title.setText("实验资讯");
		initGridView();
	}

	private void initGridView() {
		lists = new ArrayList<Grid_Item>();
		lists.add(new Grid_Item(R.mipmap.course1, "初中物理(1)"));
		lists.add(new Grid_Item(R.mipmap.course1, "初中物理(2)"));
		lists.add(new Grid_Item(R.mipmap.course1, "初中物理(3)"));
		lists.add(new Grid_Item(R.mipmap.course1, "初中物理(4)"));
		lists.add(new Grid_Item(R.mipmap.course1, "初中化学(1)"));
		lists.add(new Grid_Item(R.mipmap.course1, "初中化学(2)"));
		lists.add(new Grid_Item(R.mipmap.course1, "初中生物(1)"));
		lists.add(new Grid_Item(R.mipmap.course1, "初中生物(2)"));
		lists.add(new Grid_Item(R.mipmap.course1, "初中生物(3)"));
		lists.add(new Grid_Item(R.mipmap.course1, "初中生物(4)"));
		lists.add(new Grid_Item(R.mipmap.course1, "高中物理(1)"));
		lists.add(new Grid_Item(R.mipmap.course1, "高中物理(2)"));
		lists.add(new Grid_Item(R.mipmap.course1, "高中物理(3)"));
		lists.add(new Grid_Item(R.mipmap.course1, "高中物理(4)"));
		lists.add(new Grid_Item(R.mipmap.course1, "高中物理(5)"));
		lists.add(new Grid_Item(R.mipmap.course1, "高中化学(1)"));
		lists.add(new Grid_Item(R.mipmap.course1, "高中化学(2)"));
		lists.add(new Grid_Item(R.mipmap.course1, "高中化学(3)"));
		lists.add(new Grid_Item(R.mipmap.course1, "高中化学(4)"));
		lists.add(new Grid_Item(R.mipmap.course1, "高中化学(5)"));
		lists.add(new Grid_Item(R.mipmap.course1, "高中生物(1)"));
		lists.add(new Grid_Item(R.mipmap.course1, "高中生物(2)"));
		lists.add(new Grid_Item(R.mipmap.course1, "高中生物(3)"));
		lists.add(new Grid_Item(R.mipmap.course1, "高中生物(4)"));
		PlayListGridAdaper adaper = new PlayListGridAdaper(PlayListActivity.this, lists);
		gridView.setAdapter(adaper);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
									int position, long arg3) {
				// 播放视频
				String url=getResources().getString(R.string.url);
//				String url="/mnt/sdcard/VID_20150119_154800.mp4";
				String[] urls = new String[] { url };
				Intent intent = new Intent(PlayListActivity.this,Vid_Play_Activity.class);
				intent.putExtra("video_path", url);
				intent.putExtra("video_name", "铝热反应");
				PlayListActivity.this.startActivity(intent);
			}
		});
	}

	@Override
	protected void loadData() {
		// TODO Auto-generated method stub
		super.loadData();
	}

}
