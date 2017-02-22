package cn.gdgst.palmtest.tab1;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import cn.gdgst.palmtest.R;

import java.util.ArrayList;
/**
 * 图片轮播
 * @author admin
 *
 */
public class SlideImageLayout {
	// 包含图片的ArrayList
	private ArrayList<ImageView> imageList = null;
	private Activity activity = null;
	private ImageView[] imageViews = null; 
	private ImageView imageView = null;
	// 表示当前滑动图片的索引
	private int pageIndex = 0;
	private int imageWidth = 0;
	private int imageHeight = 0;
	public SlideImageLayout(Activity activity) {
		// TODO Auto-generated constructor stub
		this.activity = activity;
		imageWidth = activity.getResources().getDisplayMetrics().widthPixels;
		imageHeight = imageWidth / 2;
		imageList = new ArrayList<ImageView>();
		
	}
	
	/**
	 * 生成滑动图片区域布局
	 * @param
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public View getSlideImageLayout(String imgUrl,int position,int defId){
		LinearLayout imageLinerLayout = new LinearLayout(activity);
		LinearLayout.LayoutParams imageLinerLayoutParames = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		ImageView iv = new ImageView(activity);
		iv.setScaleType(ScaleType.FIT_XY);
		iv.setTag(position);
		//ImageLoaderUtil.loadImageAsync("SlideImageLayout",iv, imgUrl, null,activity.getResources().getDrawable(defId), imageWidth);
		iv.setImageDrawable(activity.getResources().getDrawable(defId));
		iv.setOnClickListener(new ImageOnClickListener());
		imageLinerLayout.addView(iv,imageLinerLayoutParames);
		imageList.add(iv);
		return imageLinerLayout;
	}
	
	/**
	 * 获取LinearLayout
	 * @param view
	 * @param width
	 * @param height
	 * @return
	 */
	public View getLinearLayout(View view,int width,int height){
		LinearLayout linerLayout = new LinearLayout(activity);
		LinearLayout.LayoutParams linerLayoutParames = new LinearLayout.LayoutParams(width, height,1);
		linerLayout.setPadding(10, 0, 10, 0);
		linerLayout.addView(view, linerLayoutParames);
		
		return linerLayout;
	}
	
	/**
	 * 设置圆点个数
	 * @param size
	 */
	public void setCircleImageLayout(int size){
		imageViews = new ImageView[size];
	}
	
	/**
	 * 生成圆点图片区域布局对象
	 * @param index
	 * @return
	 */
	public ImageView getCircleImageLayout(int index){
		imageView = new ImageView(activity);  
		imageView.setLayoutParams(new LayoutParams(10,10));
        imageView.setScaleType(ScaleType.FIT_XY);
        
        imageViews[index] = imageView;
         
        if (index == 0) {  
            imageViews[index].setBackgroundResource(R.mipmap.page_indicator_focused);
        } else {  
            imageViews[index].setBackgroundResource(R.mipmap.page_indicator_unfocused);
        }  
         
        return imageViews[index];
	}
	
	/**
	 * 设置当前滑动图片的索引
	 * @param index
	 */
	public void setPageIndex(int index){
		pageIndex = index;
	}
    private class ImageOnClickListener implements OnClickListener{
    	@Override
    	public void onClick(View v) {
    		
    	}
    }
}
