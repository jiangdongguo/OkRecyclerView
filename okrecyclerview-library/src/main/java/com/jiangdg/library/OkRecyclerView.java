package com.jiangdg.library;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
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
    private static final int STYLE_DEFAULT = 0;
    private static final int STYLE_QQZONE = 1;
    private static final int STYLE_UCBROWSER = 2;
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
    private boolean isRefreshing;
    // 自定义属性变量
    private float mHeaderLayoutWidth;
    private int mHeaderProgressStyle;
    private int mHeaderTextColor;
    private float mHeaderTextSize;
    private float mFooterLayoutWidth;
    private int mFooterProgressStyle;
    private int mFooterTextColor;
    private float mFooterTextSize;
    private int mHeaderLayoutBgColor;
    private int mFooterLayoutBgColor;
    private String mHeaderText;
    private String mFooterText;

    private ProgressBar footerProgress;
    private ProgressBar headerProgress;
    private TextView footerText;
    private TextView mTvHeaderText;
    // 是否需要调整Span标志
    private boolean isAjustSize;
    // 是否滑动到顶部,默认为true
    private boolean isTopItem = true;
    private float startY;
    private float endY;
    private float moveY;
    private int viewHeight;
    private String mReadyToRefreshTip;
    private String mReleaseToRefreshTip;
    private String mRefreshingTip;
    private String mLoadPrepareTip;
    private String mLoadingTip;

    public OkRecyclerView(Context context) {
        super(context);
        intView(context);
    }

    public OkRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttriButeSet(context, attrs);
        intView(context);
    }

    public OkRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttriButeSet(context, attrs);
        intView(context);
    }

    private void initAttriButeSet(Context context, AttributeSet attrs) {
        Resources resources = context.getResources();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.OkRecyclerView);
        // 自定义Header View属性
        mHeaderLayoutWidth = ta.getDimension(R.styleable.OkRecyclerView_headerLayoutWidth,
                resources.getDimension(R.dimen.headerLayoutWidth));
        mHeaderLayoutBgColor = ta.getColor(R.styleable.OkRecyclerView_headerLayoutBgColor,
                resources.getColor(R.color.headerLayoutBgColor));
        mHeaderProgressStyle = ta.getInt(R.styleable.OkRecyclerView_headerProgressStyle, STYLE_DEFAULT);
        mHeaderText = ta.getString(R.styleable.OkRecyclerView_headerText);
        mHeaderTextColor = ta.getColor(R.styleable.OkRecyclerView_headerTextColor,
                resources.getColor(R.color.headerTextColor));
        mHeaderTextSize = ta.getDimension(R.styleable.OkRecyclerView_headerTextSize,
                resources.getDimension(R.dimen.headerTextSize));
        mReadyToRefreshTip = ta.getString(R.styleable.OkRecyclerView_pullToRefreshTip);
        mReleaseToRefreshTip = ta.getString(R.styleable.OkRecyclerView_releaseToRefreshTip);
        mRefreshingTip = ta.getString(R.styleable.OkRecyclerView_refreshingTip);
        // 自定义Footer View属性
        mFooterLayoutWidth = ta.getDimension(R.styleable.OkRecyclerView_footerLayoutWidth,
                resources.getDimension(R.dimen.footerLayoutWidth));
        mFooterLayoutBgColor = ta.getColor(R.styleable.OkRecyclerView_footerLayoutBgColor,
                resources.getColor(R.color.footerLayoutBgColor));
        mFooterProgressStyle = ta.getInt(R.styleable.OkRecyclerView_footerProgressStyle, STYLE_DEFAULT);
        mFooterText = ta.getString(R.styleable.OkRecyclerView_footerText);
        mFooterTextColor = ta.getColor(R.styleable.OkRecyclerView_footerTextColor,
                resources.getColor(R.color.footerTextColor));
        mFooterTextSize = ta.getDimension(R.styleable.OkRecyclerView_footerTextSize,
                resources.getDimension(R.dimen.footerTextSize));
        mLoadPrepareTip = ta.getString(R.styleable.OkRecyclerView_loadPrepareTip);
        mLoadingTip = ta.getString(R.styleable.OkRecyclerView_loadingTip);
        ta.recycle();
    }

    private void intView(Context context) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams childParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        childParams.leftMargin = 15;
        // 头部View
        LinearLayout headerLayout = new LinearLayout(context);
        headerLayout.setPadding(0, (int) mHeaderLayoutWidth, 0, (int) mHeaderLayoutWidth);
        headerLayout.setGravity(Gravity.CENTER);
        headerLayout.setLayoutParams(layoutParams);
        headerLayout.setOrientation(LinearLayout.HORIZONTAL);
        headerProgress = new ProgressBar(context, null, android.R.attr.progressBarStyleSmall);
        headerProgress.setLayoutParams(childParams);
        headerProgress.setVisibility(GONE);
        headerLayout.addView(headerProgress);
        mTvHeaderText = new TextView(context);
        mTvHeaderText.setTextSize(mHeaderTextSize);
        mTvHeaderText.setLayoutParams(childParams);
        mTvHeaderText.setText(TextUtils.isEmpty(mHeaderText) ? "下拉刷新" : mHeaderText);
        headerLayout.addView(mTvHeaderText);
        addHeaderView(headerLayout);
        // 底部View
        LinearLayout footerLayout = new LinearLayout(context);
        footerLayout.setPadding(0, (int) mFooterLayoutWidth, 0, (int) mFooterLayoutWidth);
        footerLayout.setGravity(Gravity.CENTER);
        footerLayout.setLayoutParams(layoutParams);
        footerLayout.setOrientation(LinearLayout.HORIZONTAL);
        footerProgress = new ProgressBar(context, null, android.R.attr.progressBarStyleSmall);
        footerProgress.setLayoutParams(childParams);
        footerProgress.setTag("footerProgress");
        footerProgress.setVisibility(GONE);
        footerLayout.addView(footerProgress);
        footerText = new TextView(context);
        footerText.setLayoutParams(childParams);
        footerText.setTextSize(mFooterTextSize);
        footerText.setText(TextUtils.isEmpty(mFooterText) ? "查看更多" : mFooterText);
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
                totalItems = getLayoutManager().getItemCount();
                if (getLayoutManager() instanceof LinearLayoutManager) {
                    firstVisiblePos = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
                    lastVisiblePos = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
                } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
                    int[] firstInto = null;
                    int[] lastInto = null;
                    firstInto = ((StaggeredGridLayoutManager) getLayoutManager()).findFirstVisibleItemPositions(firstInto);
                    lastInto = ((StaggeredGridLayoutManager) getLayoutManager()).findLastVisibleItemPositions(lastInto);
                    firstVisiblePos = firstInto[0];
                    lastVisiblePos = lastInto[0];
                } else if (getLayoutManager() instanceof GridLayoutManager) {
                    firstVisiblePos = ((GridLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
                    lastVisiblePos = ((GridLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
                }
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.i("ddddddddddddddd", "firstVisiblePos=" + firstVisiblePos + "；lastVisiblePos=" + lastVisiblePos +
                        "；mAdapter.getItemCount()=" + mAdapter.getItemCount());
                // 当滑动到数据item最后一项，显示Footer View
                if (lastVisiblePos == mAdapter.getItemCount() - 1 && !isLoadingMore) {
                    footerProgress.setVisibility(GONE);
                    footerText.setText(TextUtils.isEmpty(mLoadPrepareTip) ? "查看更多" : mLoadPrepareTip);
                    LinearLayout mFooterLayout = (LinearLayout) mFooterViews.get(0);
                    ViewGroup.LayoutParams layoutParams = mFooterLayout.getLayoutParams();
                    layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    mFooterLayout.setLayoutParams(layoutParams);
                    if(mFooterLayout.getVisibility() == GONE) {
                        mFooterLayout.setVisibility(VISIBLE);
                    }

                    if(newState == RecyclerView.SCROLL_STATE_IDLE) {
                        footerProgress.setVisibility(VISIBLE);
                        footerText.setText(TextUtils.isEmpty(mLoadingTip) ? "正在加载更多..." : mLoadingTip);
                        smoothScrollToPosition(totalItems);
                        if (loadMoreListener != null) {
                            loadMoreListener.onLoadMore();
                        }
                        isLoadingMore = true;
                    }
                }
                // 标记可见item是否为第一条
                if (firstVisiblePos == 0) {
                    isTopItem = true;
                } else {
                    isTopItem = false;
                }
            }
        });
        /**
         *  监听触摸事件，实现下拉刷新数据
         */
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isTopItem) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            touchDown(event);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            touchMove(event);
                            break;
                        case MotionEvent.ACTION_UP:
                            touchUp();
                            break;
                    }
                }
                return false;
            }
        });
    }

    private void touchDown(MotionEvent event) {
        startY = event.getY();
    }

    private void touchMove(MotionEvent event) {
        endY = event.getY();
        moveY = endY - startY;
        if (moveY > 0 && !isRefreshing) {
            scrollToPosition(0);
            // 计算向下滑动距离，moveY
            if (moveY >= 400) {
                moveY = 100 + moveY / 4;
            } else {
                moveY = moveY / 2;
            }
            viewHeight = mHeaderViews.get(0).getHeight();
            if (viewHeight <= 0)
                viewHeight = 130;
            moveY = moveY - viewHeight;
            // 使header随moveY的值从顶部渐渐出现
            if (mHeaderViews.get(0).getVisibility() == GONE)
                mHeaderViews.get(0).setVisibility(VISIBLE);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) mHeaderViews.get(0).getLayoutParams();
            params.width = RecyclerView.LayoutParams.MATCH_PARENT;
            params.height = RecyclerView.LayoutParams.WRAP_CONTENT;
            params.setMargins(0, (int) moveY, 0, 0);
            mHeaderViews.get(0).setLayoutParams(params);
            // 根据moveY的值，更新Header View内容
            if (moveY > 80) {
                String releaseTip = getResources().getString(R.string.release_to_refresh);
                mTvHeaderText.setText(TextUtils.isEmpty(mReleaseToRefreshTip) ? releaseTip : mReleaseToRefreshTip);
            } else {
                String pullToTip = getResources().getString(R.string.pull_to_refresh);
                mTvHeaderText.setText(TextUtils.isEmpty(mReadyToRefreshTip) ? pullToTip : mReadyToRefreshTip);
            }
            if (headerProgress.getVisibility() == View.VISIBLE) {
                headerProgress.setVisibility(View.GONE);
            }
        } else {
            if (mHeaderViews.get(0).getVisibility() != GONE && !isRefreshing) {
                mHeaderViews.get(0).setVisibility(GONE);
            }
        }
    }

    private void touchUp() {
        if (!isRefreshing && (endY - startY) != 0) {
            RecyclerView.LayoutParams params1 = (RecyclerView.LayoutParams) mHeaderViews.get(0).getLayoutParams();
            params1.width = RecyclerView.LayoutParams.MATCH_PARENT;
            params1.height = RecyclerView.LayoutParams.WRAP_CONTENT;

            if (moveY >= 80) {
                if (headerProgress.getVisibility() == View.GONE) {
                    headerProgress.setVisibility(VISIBLE);
                }
                String refreshingTip = getResources().getString(R.string.refreshing);
                mTvHeaderText.setText(TextUtils.isEmpty(mRefreshingTip) ? refreshingTip : mRefreshingTip);
                params1.setMargins(0, 0, 0, 0);
                isRefreshing = true;
                //刷新数据
                if (refreshListener != null) {
                    refreshListener.onPullToRefresh();
                }
            } else {
                if (viewHeight <= 0)
                    viewHeight = 130;
                params1.setMargins(0, -viewHeight, 0, 0);
                mHeaderViews.get(0).setVisibility(GONE);
            }
            mHeaderViews.get(0).setLayoutParams(params1);
        }
    }

    @Override
    public void setAdapter(Adapter adapter) {
        // 重写Adapter,判断是否添加了头部或者底部到list
        // 根据情况选择不同适配器
        if (mHeaderViews.size() > 0 || mFooterViews.size() > 0) {
            mAdapter = new HeaderRecyclerViewAdapter(mHeaderViews, mFooterViews, adapter);
            if (isAjustSize) {
                ((HeaderRecyclerViewAdapter) mAdapter).adjustSpanSize(getLayoutManager());
            }
        } else {
            mAdapter = adapter;
        }
        super.setAdapter(mAdapter);
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        // 如果是StaggerGridLayoutManager
        // 或GridLayoutManager，需要调整头部View和底部View占1行
        if (layout instanceof StaggeredGridLayoutManager
                || layout instanceof GridLayoutManager) {
            isAjustSize = true;
        }
    }

    private void addHeaderView(View view) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        view.setLayoutParams(params);
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

    private void addFooterView(View view) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);

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
        if (loadMoreListener == null) {
            setEventListener();
        }
        this.refreshListener = refreshListener;
    }

    // 向外提供注册监听上拉加载事件接口
    public void setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        if (refreshListener == null) {
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
        mFooterViews.get(0).setVisibility(GONE);
        isLoadingMore = false;

    }

    // 下拉刷新完成
    public void pullToRefreshComplete() {
        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) mHeaderViews.get(0).getLayoutParams();
        params.width = RecyclerView.LayoutParams.MATCH_PARENT;
        params.height = RecyclerView.LayoutParams.WRAP_CONTENT;
        params.setMargins(0, -mHeaderViews.get(0).getHeight(), 0, 0);
        mHeaderViews.get(0).setLayoutParams(params);
        mHeaderViews.get(0).setVisibility(GONE);
        isRefreshing = false;
    }
}
