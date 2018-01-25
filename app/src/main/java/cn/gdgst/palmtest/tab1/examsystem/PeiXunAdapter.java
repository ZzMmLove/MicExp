package cn.gdgst.palmtest.tab1.examsystem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import cn.gdgst.entity.ExamPaper;
import cn.gdgst.palmtest.R;
import cn.gdgst.palmtest.utils.TimeUtil;

import java.util.List;

public class PeiXunAdapter extends BaseAdapter {
	//private List<PeiXun> ZiXunList;
	private Context context;
	//定义当前listview是否在滑动状态

	private List<ExamPaper> examPaperList;

	public PeiXunAdapter(Context context, List<ExamPaper> examPaperList) {
		this.context = context;
		this.examPaperList = examPaperList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if (examPaperList == null || examPaperList.size() <=0 ) {
			return 0;
		}
		return examPaperList.size();

	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return examPaperList.get(position);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub

		return arg0;
	}

	@Override
	public View getView(int position, View view, ViewGroup viewgroup) {
		// TODO Auto-generated method stub
		Viewholder vd;
		if (view == null) {
			view = LayoutInflater.from(context).inflate(R.layout.wenku_list_item, null);
			vd = new Viewholder();
			vd.tv_wenku = (TextView) view.findViewById(R.id.tv_wenku);
			vd.tv_updatetime = (TextView) view.findViewById(R.id.tv_updatetime);
			view.setTag(vd);// 给View添加一个格外的数据
		} else {
			vd=(Viewholder) view.getTag();// 把数据取出来
		}
		if (examPaperList !=null || examPaperList.size() > 0) {
			ExamPaper examPaperItem = examPaperList.get(position);
			vd.tv_wenku.setText(examPaperItem.getPaper()); //  exp_tv设置文本
			vd.tv_updatetime.setText("更新日期:"+ TimeUtil.getChatTime(examPaperItem.getAddtime())); //设置更新时间,需要转换格式
			return view;
		}
		return null;
	}
	//视图控件内部类
	public class Viewholder{
		private TextView tv_wenku;
		private TextView tv_updatetime;
	}

}