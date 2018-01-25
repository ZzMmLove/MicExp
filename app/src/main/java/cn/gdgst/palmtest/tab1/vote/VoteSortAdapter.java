package cn.gdgst.palmtest.tab1.vote;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.gdgst.palmtest.Entitys.UserVote;
import cn.gdgst.palmtest.R;

/**
 *
 * Created by Administrator on 1/8 0008.
 */

public class VoteSortAdapter extends RecyclerView.Adapter<VoteSortAdapter.MyViewHolder> {

    private List<UserVote> mDatas;
//    private Context mContext;
    private LayoutInflater inflater;

    public VoteSortAdapter(List<UserVote> mDatas, Context mContext) {
        this.mDatas = mDatas;
//        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    public void addData(List<UserVote> data){
        if (mDatas != null){
            mDatas.addAll(data);
            notifyDataSetChanged();
        }
    }

    /**
     * 删除原有的数据
     */
    public void deleteData(){
        if (mDatas != null && !mDatas.isEmpty()){
            mDatas.clear();
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_vote_sort, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
       UserVote userVote = mDatas.get(position);
        holder.tvName.setText(userVote.getName());
        holder.tvCount.setText(userVote.getPiao()+"票");
        int sort = position + 1;
        holder.image.setText(String.valueOf(sort));

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvCount;
        TextView image;
        TextView tvName;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvCount = (TextView) itemView.findViewById(R.id.tv_count);
            image = (TextView) itemView.findViewById(R.id.image_huizhang);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }

}
