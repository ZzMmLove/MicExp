package cn.gdgst.palmtest.tab1.examsystem;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import cn.gdgst.entity.ExamTopic;
import cn.gdgst.palmtest.R;

/**
 * Created by Jenfee on 2016/12/30.
 * All right reserved
 * email 1017033168@qq.com
 */

public class AnswerAdapter extends BaseAdapter {
    Context context;
    private ExamTopic examTopic;

    public AnswerAdapter(Context context, ExamTopic examTopic) {
        this.context = context;
        this.examTopic = examTopic;
    }

    @Override
    public int getCount() {
        return examTopic.getMap_Answer().size();
    }

    @Override
    public Object getItem(int position) {
        return examTopic.getMap_Answer().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_answer_list, null);
            holder = new ViewHolder();
            holder.tvText = (TextView) convertView.findViewById(R.id.tv_answer);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String string_answer = examTopic.getMap_Answer().get(position);
        holder.tvText.setText(Html.fromHtml(string_answer));
        return convertView;
    }

    static class ViewHolder {

        TextView tvText;

        ViewHolder() {
        }
    }
}
