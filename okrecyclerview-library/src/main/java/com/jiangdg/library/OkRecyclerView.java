package com.jiangdg.library;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * 加强版RecyclerView
 * 1. 支持上拉加载更多，下拉刷新(仿UC浏览器，待实现)；
 * 2. 间隔线(仿支付宝，待实现)
 * 3. 动画(待实现)
 * 4. 滑动删除(仿QQ，待实现)
 * Created by jiangdongguo on 2017/11/29.
 */
public class OkRecyclerView extends RecyclerView {
    private final static int ID_HEADER_TEXTVIEW = 0;
    private final static int MARGIN_THIRTY_DP = 30;
    private final static int MARGIN_FIFTRRN_DP = 15;
    private final static int TEXT_SIZE_SIXTEEN_SP = 16;
    // 缓存头部view列表集合
    private ArrayList<View> mHeaderViews = new ArrayList<>();
    // 缓存底部view列表集合
    private ArrayList<View> mFooterViews = new ArrayList<>();
    private OnPullToRefreshListener refreshListener;
    private OnLoadMoreListener loadMoreListener;
    private Adapter mAdapter;
    // 总条目数
    private int totalItems;
    private int firstVisiblePos;
    private int lastVisiblePos;
    private boolean isLoadingMore;

    public OkRecyclerView(Context context) {
        super(context);
        intView(context);
    }

    public OkRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        intView(context);
    }

    public OkRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        intView(context);
    }

    private void intView(Context context) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams childParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        childParams.leftMargin = MARGIN_FIFTRRN_DP;
        // 头部View
        LinearLayout headerLayout = new LinearLayout(context);
        headerLayout.setPadding(0,MARGIN_THIRTY_DP,0,MARGIN_THIRTY_DP);
        headerLayout.setGravity(Gravity.CENTER);
        headerLayout.setLayoutParams(layoutParams);
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        ProgressBar headerProgress = new ProgressBar(context,null,android.R.attr.progressBarStyleSmall);
        headerProgress.setLayoutParams(childParams);
        headerLayout.addView(headerProgress);
        TextView headerText = new TextView(context);
        headerText.setTextSize(TEXT_SIZE_SIXTEEN_SP);
        headerText.setLayoutParams(childParams);
        headerText.setText("下拉刷新");
        headerText.setId(ID_HEADER_TEXTVIEW);
        headerLayout.addView(headerText);
        addHeaderView(headerLayout);
        // 底部View
        LinearLayout footerLayout = new LinearLayout(context);
        footerLayout.setPadding(0,MARGIN_THIRTY_DP,0,MARGIN_THIRTY_DP);
        footerLayout.setGravity(Gravity.CENTER);
        footerLayout.setLayoutParams(layoutParams);
        footerLayout.setOrientation(LinearLayout.HORIZONTAL);
        ProgressBar footerProgress = new ProgressBar(context,null,android.R.attr.progressBarStyleSmall);
        footerProgress.setLayoutParams(childParams);
        footerLayout.addView(footerProgress);
        TextView footerText = new TextView(context);
        footerText.setLayoutParams(childParams);
        footerText.setTextSize(TEXT_SIZE_SIXTEEN_SP);
        footerText.setText("正在加载更多");
        footerLayout.addView(footerText);
        addFooterView(footerLayout);
    }

    private void setEventListener() {
        /**
         * 监听RecyclerView滚动事件，实现上拉加载数据
         * 当滑动到底部时，显示底部view，并开始加载数据
         **/
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // 获取总条目数，可见列表中第一条和最后一条所在的位置
                totalItems = getLayoutManager().getItemCount();
                if(getLayoutManager() instanceof LinearLayoutManager) {
                    firstVisiblePos = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
                    lastVisiblePos = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
                } else {

                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                // 如果滑动到的设置给适配器的最后一条数据，并停止滑动
                // 显示footer，加载更多数据
                Log.i("ddddddddddddddd","lastVisiblePos="+lastVisiblePos+
                        "；mAdapter.getItemCount()="+mAdapter.getItemCount());

                if(lastVisiblePos == mAdapter.getItemCount()-2 && newState == RecyclerView.SCROLL_STATE_IDLE
                        && ! isLoadingMore) {
                    ViewGroup.LayoutParams layoutParams = mFooterViews.get(0).getLayoutParams();
                    layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    mFooterViews.get(0).setLayoutParams(layoutParams);
                    mFooterViews.get(0).setVisibility(VISIBLE);
                    smoothScrollToPosition(totalItems);
                    if(loadMoreListener != null) {
                        loadMoreListener.onLoadMore();
                    }
                    isLoadingMore = true;
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        /**
         *  监听触摸事件，实现下拉刷新数据
         */
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }

    @Override
    public void setAdapter(Adapter adapter) {
        // 重写Adapter,判断是否添加了头部或者底部到list
        // 根据情况选择不同适配器
        if (mHeaderViews.size() > 0 || mFooterViews.size() > 0) {
            mAdapter = new HeaderRecyclerViewAdapter(mHeaderViews, mFooterViews, adapter);
        } else {
            mAdapter = adapter;
        }
        super.setAdapter(mAdapter);
    }

    public void addHeaderView(View view) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.width = 0;
        layoutParams.height = 0;
        view.setLayoutParams(layoutParams);
        //默认隐藏头部
        view.setVisibility(View.GONE);

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
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.width = 0;
        layoutParams.height = 0;
        view.setLayoutParams(layoutParams);
        // 默认隐藏底部
        view.setVisibility(View.GONE);

        mFooterViews.add(view);
        // 添加底部View，判断HeaderRecyclerViewAdapter是否被创建
        // 如果没有，需要创建它
        if (mAdapter != null) {
            if (!(mAdapter instanceof HeaderRecyclerViewAdapter)) {
                mAdapter = new HeaderRecyclerViewAdapter(mHeaderViews, mFooterViews, mAdapter);
            }
        }
    }

    // 下拉刷新事件监听器
    public interface OnPullToRefreshListener {
        void onPullToRefresh();
    }

    // 上拉加载更多事件监听器
    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    // 向外提供注册监听下拉刷新事件接口
    public void setOnPullToRefreshListener(OnPullToRefreshListener refreshListener) {
        if(loadMoreListener == null) {
            setEventListener();
        }
        this.refreshListener = refreshListener;
    }

    // 向外提供注册监听上拉加载事件接口
    public void setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        if(refreshListener == null) {
            setEventListener();
        }
        this.loadMoreListener = loadMoreListener;
    }

    // 上拉加载更多完成
    public void loadMoreComplete() {
        ViewGroup.LayoutParams layoutParams = mFooterViews.get(0).getLayoutParams();
        layoutParams.width = 0;
        layoutParams.height = 0;
        mFooterViews.get(0).setLayoutParams(layoutParams);
        mFooterViews.get(0).setVisibility(INVISIBLE);
        isLoadingMore = false;
    }

    // 下拉刷新完成
    public void pullToRefreshComplete() {

    }
}
