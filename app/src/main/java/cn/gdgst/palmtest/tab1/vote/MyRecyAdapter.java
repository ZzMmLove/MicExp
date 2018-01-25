package cn.gdgst.palmtest.tab1.vote;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import cn.gdgst.palmtest.Entitys.UserVote;
import cn.gdgst.palmtest.R;
import cn.gdgst.palmtest.base.AppConstant;
import cn.gdgst.palmtest.utils.ToastUtil;

/**
 *
 * Created by Administrator on 1/3 0003.
 */

public class MyRecyAdapter extends RecyclerView.Adapter{
   /* private List<String> mDatas;
    private Context mContext;
    private LayoutInflater inflater;

    public MyRecyAdapter(List<String> mDatas, Context mContext) {
        this.mDatas = mDatas;
        this.mContext = mContext;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.vote_recy_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.textView.setText(mDatas.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, VoteDetailActivity.class);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView textView;
        ImageView image;
        Button btnVote;

        public MyViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView3);
            image = (ImageView) itemView.findViewById(R.id.image_url);
            btnVote = (Button) itemView.findViewById(R.id.vote);
        }
    }*/


    interface Callbacks {
        void onClickLoadMore();
    }

    private Callbacks mCallbacks;
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;

    private boolean mWithHeader = false;
    private boolean mWithFooter = false;
    private List<UserVote> mData;
    private Context mContext;
    private long endTime;

    public MyRecyAdapter(Context context, List<UserVote> data, long endTime) {
        this.mData = data;
        this.mContext = context;
        this.endTime = endTime;
    }

    public void addData(List<UserVote> addData){
        mData.addAll(addData);
        notifyDataSetChanged();
    }

    public void deleteData(){
        if (!mData.isEmpty()) {
            mData.clear();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = null;

        if (viewType == TYPE_FOOTER) {

            itemView = View.inflate(parent.getContext(), R.layout.row_loadmore, null);
            return new LoadMoreViewHolder(itemView);

        } else {
            itemView = View.inflate(parent.getContext(), R.layout.vote_recy_item, null);
            return new ElementsViewHolder(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof LoadMoreViewHolder) {

            LoadMoreViewHolder loadMoreViewHolder = (LoadMoreViewHolder) holder;

            loadMoreViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mCallbacks!=null)
                        mCallbacks.onClickLoadMore();
                }
            });

        } else {
            ElementsViewHolder elementsViewHolder = (ElementsViewHolder) holder;

            final UserVote elements = mData.get(position);
            elementsViewHolder.textView.setText(elements.getPiao()+" 票");
            elementsViewHolder.textTitle.setText(elements.getName());
            ImageLoader.getInstance().displayImage(AppConstant.SERVER_URL+elements.getImg_url_s(), elementsViewHolder.image);
            elementsViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  startActivity(elements);
                }
            });
            elementsViewHolder.btnVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   startActivity(elements);
                }
            });
        }

    }

    private void startActivity(UserVote elements){
        long curentTime = System.currentTimeMillis();
        if (endTime*1000 - curentTime < 0){
            showDailog();
        }else {
            Intent intent = new Intent(mContext, VoteDetailActivity.class);
            intent.putExtra("videourl", elements.getVideo_url());
            intent.putExtra("id", elements.getId());
            intent.putExtra("name", elements.getName());
            intent.putExtra("votecount", elements.getPiao());
            intent.putExtra("remark", elements.getRemark());
            mContext.startActivity(intent);
        }
    }

    private void showDailog() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_message, null);
        TextView confirm;    //确定按钮
        final TextView content;    //内容
        confirm = (TextView) view.findViewById(R.id.dialog_btn_comfirm);
        content = (TextView) view.findViewById(R.id.dialog_txt_content);
        content.setText("活动时间已截止，下期更精彩…");
        final Dialog dialog = new Dialog(mContext);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes();  //获取对话框当前的参数值
        p.width = (int) (displayWidth * 0.55);    //宽度设置为屏幕的0.5
        p.height = (int) (displayHeight * 0.28);    //宽度设置为屏幕的0.5
        dialog.setCanceledOnTouchOutside(true);// 设置点击屏幕Dialog不消失
        dialog.getWindow().setAttributes(p);    //设置生效
    }

    @Override
    public int getItemCount() {
        int itemCount = mData.size();
        if (mWithHeader)
            itemCount++;
        if (mWithFooter)
            itemCount++;
        return itemCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (mWithHeader && isPositionHeader(position))
            return TYPE_HEADER;
        if (mWithFooter && isPositionFooter(position))
            return TYPE_FOOTER;
        return TYPE_ITEM;
    }

    public boolean isPositionHeader(int position) {
        return position == 0 && mWithHeader;
    }

    public boolean isPositionFooter(int position) {
        return position == getItemCount() - 1 && mWithFooter;
    }

    public void setWithHeader(boolean value){
        mWithHeader = value;
    }

    public void setWithFooter(boolean value){
        mWithFooter = value;
    }

    public void setCallback(Callbacks callbacks){
        mCallbacks = callbacks;
    }

    public class ElementsViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        TextView textTitle;
        ImageView image;
        Button btnVote;

        public ElementsViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.textView3);
            image = (ImageView) itemView.findViewById(R.id.image_url);
            btnVote = (Button) itemView.findViewById(R.id.vote);
            textTitle = (TextView) itemView.findViewById(R.id.texttitle);
        }
    }

    public class LoadMoreViewHolder  extends RecyclerView.ViewHolder {

        public LoadMoreViewHolder(View itemView) {
            super(itemView);
        }
    }

}
