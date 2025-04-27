package com.yunbao.phonelive.views;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.adapter.GameTzOptionAdapter;
import com.yunbao.phonelive.game.OptionBean;
import com.yunbao.phonelive.interfaces.OnRvClickListener;

import java.util.List;

public class ViewGameTzHolder extends AbsViewHolder {
    private RecyclerView xxRecy;

    private GameTzOptionAdapter mAdapter ;

    public ViewGameTzHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_game_tz_holder ;
    }
    @Override
    public void init() {
        xxRecy = (RecyclerView) findViewById(R.id.view_game_tz_holder_rv);
    }

    public void initBaseinfo(List<OptionBean> optionList, OnRvClickListener clickListener){

        int size = optionList.size() ;

//        if (size <= 4) {
//            xxRecy.setLayoutManager(new GridLayoutManager(mContext, size));
//        } else {
//            xxRecy.setLayoutManager(new GridLayoutManager(mContext, (size+1)/2));
//        }

        if (size <= 6) {
            xxRecy.setLayoutManager(new GridLayoutManager(mContext, size));
        } else {
            xxRecy.setLayoutManager(new GridLayoutManager(mContext, 6));
        }

        mAdapter = new GameTzOptionAdapter(mContext,optionList,clickListener) ;
        xxRecy.setAdapter(mAdapter);
    }


    public void notifyAdapterInfo(){
        mAdapter.notifyDataSetChanged() ;
    }


}
