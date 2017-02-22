package cn.gdgst.palmtest.tab2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cn.gdgst.palmtest.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import cn.gdgst.palmtest.Entitys.Video_Album_List_Entity;

import java.util.List;

public class Album_GridAdapter extends BaseAdapter {
	private List<Video_Album_List_Entity> Video_Album_List;
	private Context context;

	public Album_GridAdapter(Context context, List<Video_Album_List_Entity> video_Album_List) {
		this.context = context;
		this.Video_Album_List = video_Album_List;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Video_Album_List.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return Video_Album_List.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewgroup) {
		// TODO Auto-generated method stub
//		int width=viewgroup.getWidth()/2;
//		AbsListView.LayoutParams lp =new AbsListView.LayoutParams(width, width);
		viewholder vd=new viewholder();
		Video_Album_List_Entity videoitem=Video_Album_List.get(position);
		if (view==null) {
			view=LayoutInflater.from(context).inflate(R.layout.album_grid_item, null);
			vd=new viewholder();
			vd.iv_video=(ImageView) view.findViewById(R.id.iv_video);
			vd.tx_video=(TextView) view.findViewById(R.id.tx_video);
//			vd.tx_Episode=(TextView) view.findViewById(R.id.tx_Episode);
			view.setTag(vd);// 给View添加一个格外的数据
		}
		else {
			vd=(viewholder) view.getTag();// 把数据取出来
		}



		ImageLoader.getInstance().displayImage(videoitem.getImg_url_s(), vd.iv_video);
		vd.tx_video.setText(videoitem.getName());
//		int Episode=position+1;
//		vd.tx_Episode.setText("第"+Episode+"集"); //  exp_tv设置文本

//		view.setLayoutParams(lp);

		return view;
	}
	//视图控件内部类
	public class viewholder{
		private ImageView iv_video;
		private TextView tx_video;
//			private TextView tx_Episode;
	}
}
