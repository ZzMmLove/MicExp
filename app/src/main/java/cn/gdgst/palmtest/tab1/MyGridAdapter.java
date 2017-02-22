package cn.gdgst.palmtest.tab1;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import cn.gdgst.palmtest.R;

public class MyGridAdapter extends BaseAdapter {

	private String mTitleArr[] = null;
	private int mImgIdArr[] = null;
	private LayoutInflater inflater = null;


	/**
	 * 构造方法
	 *
	 * @param context
	 *            // 上下文对象
	 * @param titleArr
	 *            // Title数组
	 * @param imgId
	 *            // imageView资源数组
	 */
	public MyGridAdapter(Context context, String[] titleArr, int[] imgId) {
		super();
		this.mTitleArr = titleArr;
		this.mImgIdArr = imgId;
		inflater = LayoutInflater.from(context);
	}

	/**
	 * 获取Item总数
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mTitleArr.length;
	}

	/**
	 * 获取Item
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	/**
	 * 获取ItemID
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.grid_item, null);
			holder = new ViewHolder();
			holder.titleName = (TextView) convertView.findViewById(R.id.gridview_text);
			holder.image = (ImageView) convertView.findViewById(R.id.gridview_img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 设置内容
		holder.titleName.setText(mTitleArr[position]);
		holder.image.setBackgroundResource(mImgIdArr[position]);
		Log.v("showmark","各个item的位置:"+String.valueOf(position));
		return convertView;
	}

	/**
	 * 工具类
	 */
	private class ViewHolder {
		TextView titleName = null;
		ImageView image = null;
	}

}
