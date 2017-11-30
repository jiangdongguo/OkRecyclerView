package com.jiangdg.library;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by jiangdongguo on 2017/11/29.
 */

public class HeaderRecyclerViewAdapter extends RecyclerView.Adapter {
    private static final int TYPE_HEADER_VIEW = 1;
    private static final int TYPE_FOOTER_VIEW = 2;
    private RecyclerView.Adapter mAdapter;
    // 存储头部View列表集合
    private ArrayList<View> mHeaderViews;
    // 存储底部View列表集合
    private ArrayList<View> mFooterViews;

    public HeaderRecyclerViewAdapter(ArrayList<View> headerViews, ArrayList<View> footerViews, RecyclerView.Adapter adapter) {
        mAdapter = adapter;
        if (headerViews == null) {
            mHeaderViews = new ArrayList<>();
        } else {
            mHeaderViews = headerViews;
        }
        if (footerViews == null) {
            mFooterViews = new ArrayList<>();
        } else {
            mFooterViews = footerViews;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == HeaderRecyclerViewAdapter.TYPE_HEADER_VIEW) {
            // 头部View
            return new MyViewHolder(mHeaderViews.get(0));
        }else if(viewType == HeaderRecyclerViewAdapter.TYPE_FOOTER_VIEW) {
            // 底部View
            return  new MyViewHolder(mFooterViews.get(0));
        }else {
            // 正常条目
            return mAdapter.onCreateViewHolder(parent,viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int numHeaders = getHeadersCount();
        // 头部，无需理会
        if (position < numHeaders) {
            return;
        }
        // 正常条目
        final int adjPosition = position - numHeaders;
        int adapterCount = 0;
        if (mAdapter != null) {
            adapterCount = mAdapter.getItemCount();
            if (adjPosition < adapterCount) {
                mAdapter.onBindViewHolder(holder,adjPosition);
                return;
            }
        }
    }

    @Override
    public int getItemCount() {
        // 添加头部和底部后
        // item的长度为头部列表size、底部列表size和数据size之和
        if (mAdapter != null) {
            return getFootersCount() + getHeadersCount() + mAdapter.getItemCount();
        } else {
            return getFootersCount() + getHeadersCount();
        }
    }

    public int getHeadersCount() {
        return mHeaderViews.size();
    }

    public int getFootersCount() {
        return mFooterViews.size();
    }

    @Override
    public int getItemViewType(int position) {
        // item类型为头部
        int numHeaders = getHeadersCount();
        if (position < numHeaders) {
            return HeaderRecyclerViewAdapter.TYPE_HEADER_VIEW;
        }
        // item类型为正常条目
        final int adjPosition = position - numHeaders;
        int adapterCount = 0;
        if (mAdapter != null) {
            adapterCount = mAdapter.getItemCount();
            if (adjPosition < adapterCount) {
                return mAdapter.getItemViewType(adjPosition);
            }
        }
        // item类型为底部
        return HeaderRecyclerViewAdapter.TYPE_FOOTER_VIEW;
    }

    public boolean isEmpty() {
        return mAdapter == null;
    }

    public boolean removeHeader(View v) {
        for (int i = 0; i < mHeaderViews.size(); i++) {
            if (mHeaderViews.get(i) == v) {
                mHeaderViews.remove(i);
                return true;
            }
        }
        return false;
    }

    public boolean removeFooter(View v) {
        for (int i = 0; i < mFooterViews.size(); i++) {
            if (mFooterViews.get(i) == v) {
                mFooterViews.remove(i);
                return true;
            }
        }

        return false;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
