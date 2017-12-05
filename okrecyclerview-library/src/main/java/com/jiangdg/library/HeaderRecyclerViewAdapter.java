package com.jiangdg.library;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * 自定义RecyclerView
 * <p>
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
    private boolean isStragger;

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

    public void adjustSpanSize(final RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof GridLayoutManager) {
            // 调整GridLayoutManager Span大小
            ((GridLayoutManager) layoutManager).setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int headerViewSize = mHeaderViews.size();
                    int adjustPos = position - headerViewSize;
                    if(position < headerViewSize || adjustPos >= mAdapter.getItemCount()) {
                        return ((GridLayoutManager) layoutManager).getSpanCount();
                    }
                    return 1;
                }
            });
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            // StaggeredGridLayoutManager Span大小,这里只做个标记
            isStragger = true;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == HeaderRecyclerViewAdapter.TYPE_HEADER_VIEW) {
            // 如果是Stagger布局,设置头部View的Span占一行
            View headerView = mHeaderViews.get(0);
            if (isStragger) {
                StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        0);
                params.setFullSpan(true);
                headerView.setLayoutParams(params);
            }
            return new MyViewHolder(headerView);
        } else if (viewType == HeaderRecyclerViewAdapter.TYPE_FOOTER_VIEW) {
            // 如果是Stagger布局，设置底部View的Span占一行
            View footerView = mFooterViews.get(0);
            if (isStragger) {
                StaggeredGridLayoutManager.LayoutParams params = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setFullSpan(true);
                footerView.setLayoutParams(params);
            }
            return new MyViewHolder(footerView);
        } else {
            // 正常条目
            return mAdapter.onCreateViewHolder(parent, viewType);
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
                mAdapter.onBindViewHolder(holder, adjPosition);
                return;
            }
        }
    }

    @Override
    public long getItemId(int position) {
        int numHeaders = getHeadersCount();
        if (mAdapter != null && position >= numHeaders) {
            int adjPosition = position - numHeaders;
            int adapterCount = mAdapter.getItemCount();
            if (adjPosition < adapterCount) {
                return mAdapter.getItemId(adjPosition);
            }
        }
        return -1;
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

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
