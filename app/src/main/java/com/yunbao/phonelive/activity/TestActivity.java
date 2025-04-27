package com.yunbao.phonelive.activity;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.adapter.MainHomeTestAdapter;

/**
 * Created by cxf on 2018/10/19.
 */

public class TestActivity extends AbsActivity {

    private RecyclerView mRecyclerView;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    protected void main() {
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new MainHomeTestAdapter(mContext));
    }


}
