package cn.gdgst.palmtest.tab3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cn.gdgst.entity.Experiment;
import cn.gdgst.palmtest.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**仿真实验的适配器
 * @author Don
 *
 */
public class ExperimentAdapter extends BaseAdapter {

	private List<Experiment> ExperimentList;
	private Context context;
	//定义当前listview是否在滑动状态



	public ExperimentAdapter(Context context, List<Experiment> experimentList) {
		this.context = context;
		this.ExperimentList = experimentList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return ExperimentList.size();

	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return ExperimentList.get(position);
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
		Experiment expitem=ExperimentList.get(position);

		if (view==null) {
			view=LayoutInflater.from(context).inflate(R.layout.exp_listview_item, null);
			vd=new viewholder();
			vd.exp_img=(ImageView) view.findViewById(R.id.exp_img);
			vd.exp_tv=(TextView) view.findViewById(R.id.exp_tv);
			vd.getExp_tv_time = (TextView) view.findViewById(R.id.exp_tv_time);
			view.setTag(vd);// 给View添加一个格外的数据
		}
		else {
			vd=(viewholder) view.getTag();// 把数据取出来
		}



		ImageLoader.getInstance().displayImage(expitem.getImg_url(), vd.exp_img);

		vd.exp_tv.setText(expitem.getName()); //  exp_tv设置文本

		vd.getExp_tv_time.setText("更新时间："+ expitem.getTime());


		return view;
	}
	//视图控件内部类
	public class viewholder{
		private ImageView exp_img;
		private TextView exp_tv;
		private TextView getExp_tv_time;
	}

}
