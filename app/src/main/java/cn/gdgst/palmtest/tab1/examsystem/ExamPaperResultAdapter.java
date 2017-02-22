package cn.gdgst.palmtest.tab1.examsystem;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.HashMap;
import java.util.List;

import cn.gdgst.entity.ExamTopic;
import cn.gdgst.palmtest.R;

/**
 * Created by JenfeeMa on 2016/12/30.
 * All right reserved
 * email 1017033168@qq.com
 */

public class ExamPaperResultAdapter extends BaseAdapter {
    private Context context;
    private List<ExamTopic> list_ExamTopc;
    private HashMap<Integer, String> hashmap_selected_result;
    private DisplayImageOptions displayImageOptions;

    public ExamPaperResultAdapter(Context context, List<ExamTopic> list_ExamTopc, HashMap<Integer, String> hashmap_selected_result) {
        this.context = context;
        this.list_ExamTopc = list_ExamTopc;
        this.hashmap_selected_result = hashmap_selected_result;
    }

    @Override
    public int getCount() {
        return list_ExamTopc.size();
    }

    @Override
    public Object getItem(int position) {
        return list_ExamTopc.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.exampaper_result_item, null);
            holder.textView_title = (TextView) convertView.findViewById(R.id.exampaper_result_item_title);
            holder.textView_selected = (TextView) convertView.findViewById(R.id.exampaper_result_item_selected);
            holder.textView_right = (TextView) convertView.findViewById(R.id.exampaper_result_item_right);
            holder.textView_optionA = (TextView) convertView.findViewById(R.id.exampaper_result_item_A);
            holder.textView_optionB = (TextView) convertView.findViewById(R.id.exampaper_result_item_B);
            holder.textView_optionC = (TextView) convertView.findViewById(R.id.exampaper_result_item_C);
            holder.textView_optionD = (TextView) convertView.findViewById(R.id.exampaper_result_item_D);
            holder.textView_analysis = (TextView) convertView.findViewById(R.id.exampaper_result_item_textView_analysis);
            holder.imageView = (ImageView) convertView.findViewById(R.id.exampaper_result_item_imageView);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        ExamTopic examTopic = list_ExamTopc.get(position);
        Log.v("ExamPaperResultAdapter","ExamPaperResultAdapter中的图片:"+"****************"+examTopic.getImg()+"************");
        String select_result = hashmap_selected_result.get(position+1);
        holder.textView_title.setText(String.valueOf(examTopic.getId())+". "+ Html.fromHtml(examTopic.getTitle()));
        holder.textView_selected.setText("已选择的答案:"+select_result);
        holder.textView_right.setText("正确答案:"+examTopic.getRight());
        holder.textView_optionA.setText(Html.fromHtml(examTopic.getOptionA()));
        holder.textView_optionB.setText(Html.fromHtml(examTopic.getOptionB()));
        holder.textView_optionC.setText(Html.fromHtml(examTopic.getOptionC()));
        holder.textView_optionD.setText(Html.fromHtml(examTopic.getOptionD()));
        holder.textView_analysis.setText(Html.fromHtml(examTopic.getAnalysis()));
        ImageView imageView = holder.imageView;
        final String tag = (String)imageView.getTag();
        final ExamTopic examTopic_uri = (ExamTopic) getItem(position);
        String uri = examTopic_uri.getImg();
        if (!uri.equals(tag)) {
            imageView.setImageDrawable(null);
        }
        imageView.setTag(uri);
        if (examTopic.getImg() != null && !examTopic.getImg().equals("")) {
            displayImageOptions = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .displayer(new RoundedBitmapDisplayer(20)).build();
            ImageLoader.getInstance().displayImage("http://www.shiyan360.cn"+examTopic.getImg(), holder.imageView);
            Log.v("ExamPaperResultAdapter", "测试ExamPaperResultAdapter适配器中的图片URL"+examTopic.getImg());
        }
        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
        TextView textView_title;
        TextView textView_selected;
        TextView textView_right;
        TextView textView_optionA;
        TextView textView_optionB;
        TextView textView_optionC;
        TextView textView_optionD;
        TextView textView_analysis;
    }
}
