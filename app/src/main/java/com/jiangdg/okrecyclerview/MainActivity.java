package com.jiangdg.okrecyclerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.jiangdg.library.OkRecyclerView;

public class MainActivity extends AppCompatActivity {
    private OkRecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (OkRecyclerView)findViewById(R.id.okRecycerView);
        MyAdapter adapter = new MyAdapter();
        adapter.setAdapterData(new String[]{"张三","李四","王五","赵六"});
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
    }
}
