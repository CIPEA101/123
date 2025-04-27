package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yunbao.phonelive.Constants;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.activity.LiveAudienceActivity;
import com.yunbao.phonelive.bean.LiveChatBean;
import com.yunbao.phonelive.dialog.LiveGameTzInfoDialogFragment;
import com.yunbao.phonelive.http.HttpConsts;
import com.yunbao.phonelive.interfaces.OnItemClickListener;
import com.yunbao.phonelive.utils.ClickUtil;
import com.yunbao.phonelive.utils.TextRender;
import com.yunbao.phonelive.views.DrawableTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/10/10.
 */

public class LiveChatAdapter extends RecyclerView.Adapter {

    private List<LiveChatBean> mList;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private OnItemClickListener<LiveChatBean> mOnItemClickListener;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private Context context;
    private String gameIdstr;
    private String idstr;

    public LiveChatAdapter(Context context) {
        this.context = context;
        mList = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null) {
                    LiveChatBean bean = (LiveChatBean) tag;
                    if (bean.getType() != LiveChatBean.SYSTEM && mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(bean, 0);
                    }
                }
            }
        };
    }

    public void setOnItemClickListener(OnItemClickListener<LiveChatBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == LiveChatBean.RED_PACK) {
            return new RedPackVh(mInflater.inflate(R.layout.item_live_chat_red_pack, parent, false));
        } else if (viewType == LiveChatBean.TZ_FOLLOW) {
            return new TzFollowVh(mInflater.inflate(R.layout.item_live_chat_tz_follow, parent, false));
        } else if(viewType == LiveChatBean.ZJ_LIST){
            return new TzListVh(mInflater.inflate(R.layout.item_live_chat_dj_list, parent, false));
        } else{
            return new Vh(mInflater.inflate(R.layout.item_live_chat, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        if (vh instanceof Vh) {
            ((Vh) vh).setData(mList.get(position));
        } else if (vh instanceof RedPackVh) {
            ((RedPackVh) vh).setData(mList.get(position));
        } else if (vh instanceof TzFollowVh) {
            ((TzFollowVh) vh).setData(mList.get(position));
        } else if (vh instanceof TzListVh) {
            ((TzListVh) vh).setData(mList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
    }

    class RedPackVh extends RecyclerView.ViewHolder {

        TextView mTextView;

        public RedPackVh(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
        }

        void setData(LiveChatBean bean) {
            mTextView.setText(bean.getContent());
        }
    }

    class TzListVh extends RecyclerView.ViewHolder {

        DrawableTextView mTextView;

        public TzListVh(View itemView) {
            super(itemView);
            mTextView = (DrawableTextView) itemView;
        }

        void setData(LiveChatBean bean) {
            String content = bean.getContent() ;
            mTextView.setText(null == content ? "" : content) ;
        }
    }

    class TzFollowVh extends RecyclerView.ViewHolder {

        DrawableTextView mTextView;
        TextView ids;
        TextView gameId;

        public TzFollowVh(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.tv_tz_follow);
            ids = itemView.findViewById(R.id.ids);
            gameId = itemView.findViewById(R.id.game_id);
            mTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(ClickUtil.canClick()){
                        gameIdstr = gameId.getText().toString();
                        idstr = ids.getText().toString();
                        LiveGameTzInfoDialogFragment tzFragment = new LiveGameTzInfoDialogFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.LIVE_GAME_ID, gameIdstr);
                        bundle.putString(Constants.LIVE_GAME_NAME_TZ_IDS, idstr);
                        tzFragment.setArguments(bundle);
                        tzFragment.show(((LiveAudienceActivity) context).getSupportFragmentManager(), HttpConsts.LIVE_GAME_TZ_INFO_DIALOG_FRAGMENT);
                    }
                }
            });
        }

        void setData(LiveChatBean bean) {
            SpannableStringBuilder builder = TextRender.createPrefixSystem(bean);
            mTextView.setText(builder);
            ids.setText(bean.getGameItemIds());
            gameId.setText(bean.getGameId());
        }
    }

    class Vh extends RecyclerView.ViewHolder {

        TextView mTextView;

        public Vh(View itemView) {
            super(itemView);
            mTextView = (TextView) itemView;
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(LiveChatBean bean) {
            Log.e(LiveChatAdapter.class.getSimpleName(), bean.getType() + "");

            itemView.setTag(bean);
            //mTextView.setText(bean.getContent());
            if (bean.getType() == LiveChatBean.SYSTEM) {
                mTextView.setTextColor(0xffffdd00);
                mTextView.setText(bean.getContent());
            } else {
                if (bean.getType() == LiveChatBean.ENTER_ROOM || bean.getType() == LiveChatBean.LIGHT) {
                    mTextView.setTextColor(0xffc8c8c8);
                } else {
                    mTextView.setTextColor(0xffffffff);
                }
                TextRender.render(mTextView, bean);
            }
        }
    }

    public void insertItem(LiveChatBean bean) {
        if (bean == null) {
            return;
        }
        int size = mList.size();
        mList.add(bean);
        notifyItemInserted(size);
        int lastItemPosition = mLayoutManager.findLastCompletelyVisibleItemPosition();
        if (lastItemPosition != size - 1) {
            mRecyclerView.smoothScrollToPosition(size);
        } else {
            mRecyclerView.scrollToPosition(size);
        }
    }

    public void scrollToBottom() {
        if (mList.size() > 0) {
            mRecyclerView.smoothScrollToPosition(mList.size() - 1);
        }
    }

    public void clear() {
        if (mList != null) {
            mList.clear();
        }
        notifyDataSetChanged();
    }
}
