package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.game.GameBean;

import java.util.ArrayList;
import java.util.List;

public class GamePagerAdapter extends PagerAdapter {
    private List<RecyclerView> mViewList;

    private static final int GIFT_COUNT = 8;//每页8个礼物
    private Context mContext;

    public GamePagerAdapter(Context context, List<GameBean> gameList) {
        mContext = context;
        mViewList = new ArrayList<>();
        int fromIndex = 0;
        int size = gameList.size();
        int pageCount = size / GIFT_COUNT;
        if (size % GIFT_COUNT > 0) {
            pageCount++;
        }
        LayoutInflater inflater = LayoutInflater.from(context);
        for (int i = 0; i < pageCount; i++) {
            RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.view_gift_page, null, false);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false));
            int endIndex = fromIndex + GIFT_COUNT;
            if (endIndex > size) {
                endIndex = size;
            }
            List<GameBean> list = new ArrayList<>();
            for (int j = fromIndex; j < endIndex; j++) {
                GameBean bean = gameList.get(j);
                bean.setPage(i);
                list.add(bean);
            }

            GameAdapter adapter = new GameAdapter(mContext,inflater, list);
            recyclerView.setAdapter(adapter);
            mViewList.add(recyclerView);
            fromIndex = endIndex;
        }
    }

    @Override
    public int getCount() {
        return mViewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mViewList.get(position);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViewList.get(position));
    }

    public void release() {
        if (mViewList != null) {
            for (RecyclerView recyclerView : mViewList) {
                GameAdapter adapter = (GameAdapter) recyclerView.getAdapter();
                if (adapter != null) {
                    adapter.release();
                }
            }
        }
    }
}