package com.jiangdg.okrecyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/** 列表数据适配器
 *
 * Created by jiangdongguo on 2017/11/29.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.RecyclerViewHolder> {

    private String[] datas;

    public void setAdapterData(String[] datas) {
        this.datas = datas;
    }
    
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.layout_list_item,parent,false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {
        holder.mTvContent.setText(datas[position]);
    }

    @Override
    public int getItemCount() {
        return datas.length;
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.textView)
        TextView mTvContent;

        public RecyclerViewHolder(View itemView) {
            super(itemView);
            // 绑定item view
            ButterKnife.bind(this,itemView);
        }
    }
}
