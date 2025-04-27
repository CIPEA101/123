package com.yunbao.phonelive.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.TzRecordBean;
import com.yunbao.phonelive.game.AwardBean;
import com.yunbao.phonelive.http.HttpConsts;
import com.yunbao.phonelive.im.ImDateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LiveTzRecordAdapter extends RecyclerView.Adapter<LiveTzRecordAdapter.Vh> {

    private List<TzRecordBean> mList;
    private LayoutInflater mInflater;
    private ScaleAnimation mAnimation;
    private Context mContext;
    private AwardRecordAdapter adapter;
    private int type;

    public LiveTzRecordAdapter(Context context, LayoutInflater inflater, List<TzRecordBean> list, int type) {
        this.mContext = context;
        this.mInflater = inflater;
        this.mList = list;
        this.type = type;
        mAnimation = new ScaleAnimation(0.9f, 1.1f, 0.9f, 1.1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mAnimation.setDuration(400);
        mAnimation.setRepeatMode(Animation.REVERSE);
        mAnimation.setRepeatCount(-1);
    }

    public void addList(List<TzRecordBean> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void setList(List<TzRecordBean> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_tz_record, null, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) {
        vh.setData(mList.get(position));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position, @NonNull List<Object> payloads) {
        vh.setData(mList.get(position));
    }


    @Override
    public int getItemCount() {
        Log.e(LiveTzRecordAdapter.class.getSimpleName(),mList.size()+"");
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {

        TextView mTextView;
        TextView mTime;
        RecyclerView recyclerView;

        public Vh(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.iv_game_name);
            mTime = itemView.findViewById(R.id.iv_game_time);
            recyclerView = itemView.findViewById(R.id.recycler);

            recyclerView.setNestedScrollingEnabled(false);
            recyclerView.setFocusable(false);
            recyclerView.setLayoutManager(new GridLayoutManager(mContext, 1, GridLayoutManager.HORIZONTAL, false));
        }

        void setData(TzRecordBean bean) {
            Log.e(LiveTzRecordAdapter.class.getSimpleName(),bean.getResult());
            mTime.setText(ImDateUtil.dateToString(new Date()) + bean.getId() + "期");
            String[] strArr = bean.getResult().split(",");
            int optype = 0;
            List<AwardBean> list = null;
            switch (type) {
                case 1:
                    mTextView.setText(HttpConsts.GAME_NAME_YFKS);
                    if (strArr.length > 0) {
                        optype = 1;
                        list = new ArrayList<>();
                        int imgeUrl = 0;
                        for (String str : strArr) {
                            if (str.equals("1")) {
                                imgeUrl = R.mipmap.icon_game_dice_one;
                            } else if (str.equals("2")) {
                                imgeUrl = R.mipmap.icon_game_dice_two;
                            } else if (str.equals("3")) {
                                imgeUrl = R.mipmap.icon_game_dice_three;
                            } else if (str.equals("4")) {
                                imgeUrl = R.mipmap.icon_game_dice_four;
                            } else if (str.equals("5")) {
                                imgeUrl = R.mipmap.icon_game_dice_five;
                            } else if (str.equals("6")) {
                                imgeUrl = R.mipmap.icon_game_dice_six;
                            }
                            list.add(new AwardBean(imgeUrl));
                        }
                    }
                    break;
                case 2:
                    optype = 2;
                    list = new ArrayList<>();
                    for (String str : strArr) {
                        list.add(new AwardBean(str));
                    }
                    mTextView.setText(HttpConsts.GAME_NAME_YF115);
                    break;
                case 3:
                    optype = 4;
                    mTextView.setText(HttpConsts.GAME_NAME_YFSC);
                    list = new ArrayList<>();
                    for (String str : strArr) {
                        list.add(new AwardBean(str));
                    }
                    break;
                case 4:
                    optype = 2;
                    mTextView.setText(HttpConsts.GAME_NAME_YFSSC);
                    list = new ArrayList<>();
                    for (String str : strArr) {
                        list.add(new AwardBean(str));
                    }
                    break;
                case 5:
                    optype = 3;
                    mTextView.setText(HttpConsts.GAME_NAME_YFLHC);
                    list = new ArrayList<>();
                    for (int i = 0; i < strArr.length; i++) {
                        if (i == strArr.length - 1) {
                            list.add(new AwardBean("",R.mipmap.ic_game_point_add_black));
//                            list.add(new AwardBean("+",R.drawable.bg_game_point_add));
//                            list.add(new AwardBean("",R.mipmap.ic_game_point_add));
                        }
                        String number = strArr[i] ;
                        list.add(new AwardBean(number,AwardBean.getItemBackgroundByName(number))) ;
                    }
                    break;
                case 6:
                    optype = 2;
                    mTextView.setText(HttpConsts.GAME_NAME_YFKLSF);
                    list = new ArrayList<>();
                    for (String str : strArr) {
                        list.add(new AwardBean(str));
                    }
                    break;
                case 7:
                    optype = 1;
                    mTextView.setText(HttpConsts.GAME_NAME_YFXYNC);
                    list = new ArrayList<>();
                    int imgeUrl = 0;
                    for (String str : strArr) {
                        if (str.equals("1")) {
                            imgeUrl = R.mipmap.cqxync01;
                        } else if (str.equals("2")) {
                            imgeUrl = R.mipmap.cqxync02;
                        } else if (str.equals("3")) {
                            imgeUrl = R.mipmap.cqxync03;
                        } else if (str.equals("4")) {
                            imgeUrl = R.mipmap.cqxync04;
                        } else if (str.equals("5")) {
                            imgeUrl = R.mipmap.cqxync05;
                        } else if (str.equals("6")) {
                            imgeUrl = R.mipmap.cqxync06;
                        } else if (str.equals("7")) {
                            imgeUrl = R.mipmap.cqxync07;
                        } else if (str.equals("8")) {
                            imgeUrl = R.mipmap.cqxync08;
                        } else if (str.equals("9")) {
                            imgeUrl = R.mipmap.cqxync09;
                        } else if (str.equals("10")) {
                            imgeUrl = R.mipmap.cqxync10;
                        } else if (str.equals("11")) {
                            imgeUrl = R.mipmap.cqxync11;
                        } else if (str.equals("12")) {
                            imgeUrl = R.mipmap.cqxync12;
                        } else if (str.equals("13")) {
                            imgeUrl = R.mipmap.cqxync13;
                        } else if (str.equals("14")) {
                            imgeUrl = R.mipmap.cqxync14;
                        } else if (str.equals("15")) {
                            imgeUrl = R.mipmap.cqxync15;
                        } else if (str.equals("16")) {
                            imgeUrl = R.mipmap.cqxync16;
                        } else if (str.equals("17")) {
                            imgeUrl = R.mipmap.cqxync17;
                        } else if (str.equals("18")) {
                            imgeUrl = R.mipmap.cqxync18;
                        } else if (str.equals("19")) {
                            imgeUrl = R.mipmap.cqxync19;
                        } else if (str.equals("20")) {
                            imgeUrl = R.mipmap.cqxync20;
                        }
                        list.add(new AwardBean(imgeUrl));
                    }
                    break;
            }

            adapter = new AwardRecordAdapter(mContext, mInflater, list, optype);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }


    public void clear() {
        if (mList != null) {
            mList.clear();
        }
        notifyDataSetChanged();
    }
}

