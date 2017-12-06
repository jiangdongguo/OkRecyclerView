package com.jiangdg.okrecyclerview;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.Toast;

import com.jiangdg.library.MyItemDecoration;
import com.jiangdg.library.OkRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.okRecycerView)
    OkRecyclerView mRecyclerView;
    private Handler mHanlder = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x00:
                    mRecyclerView.loadMoreComplete();
                    showToast("加载完毕~");
                    break;
                case 0x01:
                    mRecyclerView.pullToRefreshComplete();
                    showToast("刷新成功~");
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 绑定View
        ButterKnife.bind(this);

        MyAdapter adapter = new MyAdapter();
        adapter.setAdapterData(new String[]{"aaa","张三","李四","王五","赵六","张三","张三","李四","王五","赵六",
                "张三","张三","李四","王五","赵六","张三","ddd"});
//        GridLayoutManager gridManager = new GridLayoutManager(this,3);
//        mRecyclerView.setLayoutManager(gridManager);
//        StaggeredGridLayoutManager staggerManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
//        mRecyclerView.setLayoutManager(staggerManager);
        LinearLayoutManager linearManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearManager);
        mRecyclerView.addItemDecoration(new MyItemDecoration(this,10));
        mRecyclerView.setOnLoadMoreListener(new OkRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                // 模拟一个请求数据线程
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mHanlder.sendEmptyMessage(0x00);
                    }
                }).start();
            }
        });
        mRecyclerView.setOnPullToRefreshListener(new OkRecyclerView.OnPullToRefreshListener() {
            @Override
            public void onPullToRefresh() {
                // 模拟一个请求数据线程
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        mHanlder.sendEmptyMessage(0x01);
                    }
                }).start();
            }
        });
        mRecyclerView.setAdapter(adapter);
    }

    private void showToast(String msg) {
        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
    }
}
