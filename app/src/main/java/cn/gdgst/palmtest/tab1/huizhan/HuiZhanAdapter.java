package cn.gdgst.palmtest.tab1.huizhan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import cn.gdgst.entity.HuiZhan;
import cn.gdgst.palmtest.R;

import java.util.List;

public class HuiZhanAdapter extends BaseAdapter {
	private List<HuiZhan> ZiXunList;
	private Context context;
	//定义当前listview是否在滑动状态

	public HuiZhanAdapter(Context context, List<HuiZhan> zixunList) {
		this.context = context;
		this.ZiXunList = zixunList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return ZiXunList.size();

	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return ZiXunList.get(position);
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
		HuiZhan expitem=ZiXunList.get(position);
		if (view==null) {
			view=LayoutInflater.from(context).inflate(R.layout.wenku_list_item, null);
			vd=new viewholder();

			vd.tv_wenku=(TextView) view.findViewById(R.id.tv_wenku);
			view.setTag(vd);// 给View添加一个格外的数据
		}
		else {
			vd=(viewholder) view.getTag();// 把数据取出来
		}

		vd.tv_wenku.setText(expitem.getTitle()); //  exp_tv设置文本


		return view;
	}
	//视图控件内部类
	public class viewholder{
		private TextView tv_wenku;
	}

}