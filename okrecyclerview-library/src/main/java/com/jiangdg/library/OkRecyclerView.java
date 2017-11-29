package com.jiangdg.library;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/** 加强版RecyclerView
 *  1. 支持上拉加载更多，下拉刷新(仿UC浏览器，待实现)；
 *  2. 间隔线(仿支付宝，待实现)
 *  3. 动画(待实现)
 *  4. 滑动删除(仿QQ，待实现)
 *
 * Created by jiangdongguo on 2017/11/29.
 */
public class OkRecyclerView extends RecyclerView {
    // 缓存头部view列表集合
    private ArrayList<View> mHeaderViews = new ArrayList<>();
    // 缓存底部view列表集合
    private ArrayList<View> mFooterViews = new ArrayList<>();
    private Adapter mAdapter;

    public OkRecyclerView(Context context) {
        super(context);
    }

    public OkRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public OkRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        // 重写Adapter,判断是否添加了头部或者底部到list
        // 根据情况选择不同适配器
        if (mHeaderViews.size() > 0|| mFooterViews.size() > 0) {
            mAdapter = new HeaderRecyclerViewAdapter(mHeaderViews, mFooterViews, adapter);
        } else {
            mAdapter = adapter;
        }
        super.setAdapter(mAdapter);
    }

    public void addHeaderView(View view) {
        mHeaderViews.add(view);
        // 添加头部View，判断HeaderRecyclerViewAdapter是否被创建
        // 如果没有，需要创建它
        if (mAdapter != null) {
            if (!(mAdapter instanceof HeaderRecyclerViewAdapter)) {
                mAdapter = new HeaderRecyclerViewAdapter(mHeaderViews, mFooterViews, mAdapter);
            }
        }
    }

    public void addFooterView(View view) {
        mFooterViews.add(view);
        // 添加底部View，判断HeaderRecyclerViewAdapter是否被创建
        // 如果没有，需要创建它
        if (mAdapter != null) {
            if (!(mAdapter instanceof HeaderRecyclerViewAdapter)) {
                mAdapter = new HeaderRecyclerViewAdapter(mHeaderViews, mFooterViews, mAdapter);
            }
        }
    }
}
