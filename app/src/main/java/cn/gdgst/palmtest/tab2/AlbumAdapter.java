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

/**使用自定义数据适配器绑定数据到专辑列表上
 * @author Don
 *
 */
public class AlbumAdapter extends BaseAdapter {
	private List<Video_Album_List_Entity> Video_Album_List;
	private Context context;


	public AlbumAdapter(Context context, List<Video_Album_List_Entity> video_Album_List) {
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
		viewholder vd = new viewholder();
		Video_Album_List_Entity albumitem = Video_Album_List.get(position);
		if (view == null) {
			view=LayoutInflater.from(context).inflate(R.layout.video_list_item, null);
			vd=new viewholder();
			vd.vid_img=(ImageView) view.findViewById(R.id.vid_img);
			vd.vid_tv=(TextView) view.findViewById(R.id.vid_tv);
			view.setTag(vd);// 给View添加一个格外的数据
		}
		else{
			vd=(viewholder) view.getTag();// 把数据取出来
		}
		// TODO Auto-generated method stub
		ImageLoader.getInstance().displayImage(albumitem.getImg_url(), vd.vid_img);
		vd.vid_tv.setText(albumitem.getName());

		return view;
	}

	// 视图控件内部类
	public class viewholder {
		private ImageView vid_img;
		private TextView vid_tv;
	}
}
