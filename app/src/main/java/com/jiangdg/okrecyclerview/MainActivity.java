package com.jiangdg.okrecyclerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import com.jiangdg.library.OkRecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.okRecycerView)
    OkRecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 绑定View
        ButterKnife.bind(this);

        View headerView = LayoutInflater.from(this).inflate(R.layout.layout_header,null);
        mRecyclerView.addHeaderView(headerView);
        mRecyclerView.addFooterView(headerView);

        MyAdapter adapter = new MyAdapter();
        adapter.setAdapterData(new String[]{"张三","李四","王五","赵六","张三","李四","王五","赵六","张三",
                "李四","王五","赵六","张三","李四","王五","赵六"});
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
    }
}
