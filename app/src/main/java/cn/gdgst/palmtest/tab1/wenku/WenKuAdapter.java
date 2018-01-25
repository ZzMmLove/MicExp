package cn.gdgst.palmtest.tab1.wenku;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import cn.gdgst.entity.WenKu;
import cn.gdgst.palmtest.R;

import java.util.List;

public class WenKuAdapter extends BaseAdapter {
	private List<WenKu> WenKuList;
	private Context context;
	//定义当前listview是否在滑动状态

	public WenKuAdapter(Context context, List<WenKu> wenkuList) {
		this.context = context;
		this.WenKuList = wenkuList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return WenKuList.size();

	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return WenKuList.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewgroup) {
		// TODO Auto-generated method stub
		viewholder vd=new viewholder();
		WenKu expitem=WenKuList.get(position);
		if (view==null) {
			view=LayoutInflater.from(context).inflate(R.layout.wenku_list_item, null);
			vd=new viewholder();

			vd.tv_wenku=(TextView) view.findViewById(R.id.tv_wenku);
			vd.tv_time = (TextView) view.findViewById(R.id.tv_updatetime);
			view.setTag(vd);// 给View添加一个格外的数据
		}
		else {
			vd=(viewholder) view.getTag();// 把数据取出来
		}



		vd.tv_wenku.setText(expitem.getTitle()); //  exp_tv设置文本
		vd.tv_time.setText("更新时间："+expitem.getTime());


		return view;
	}
	//视图控件内部类
	public class viewholder{
		private TextView tv_wenku;
		private TextView tv_time;
	}

}