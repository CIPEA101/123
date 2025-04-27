package com.yunbao.phonelive.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.adapter.LastAwardAdapter;
import com.yunbao.phonelive.game.AwardBean;
import com.yunbao.phonelive.http.HttpConsts;
import com.yunbao.phonelive.utils.DpUtil;
import com.yunbao.phonelive.utils.ScreenDimenUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 直播游戏结果
 */
public class LiveGameResultViewHolder extends AbsViewHolder implements View.OnClickListener{
    private ImageView mGameTypeIv ;
    private TextView mGameNameTv ;
    private RecyclerView mResultRv ;

    private static final int WAIT_TIME = 6 ;

    public LiveGameResultViewHolder(Context activity, ViewGroup parentView) {
        super(activity, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_game_result ;
    }

    @Override
    public void init() {
        mGameTypeIv = (ImageView) findViewById(R.id.view_live_game_result_iv);
        mGameNameTv = (TextView) findViewById(R.id.view_live_game_result_name_tv);
        mResultRv = (RecyclerView) findViewById(R.id.view_live_game_result_rv);
        View closeIv = findViewById(R.id.view_live_game_result_close_iv);
        closeIv.setOnClickListener(this) ;

        addToParent() ;
        mContentView.setVisibility(View.GONE) ;
    }

    @Override
    public void onClick(View view) {
        if(R.id.view_live_game_result_close_iv == view.getId()){//关闭
            if(mContentView != null && mContentView.getVisibility() != View.GONE){
                mContentView.setVisibility(View.GONE) ;
            }
        }
    }

    public void startAnim(){
        mContentView.setVisibility(View.VISIBLE);
        mContentView.startAnimation(AnimationUtils.loadAnimation(mContext,R.anim.right_left_anim)) ;

        ValueAnimator animator = ValueAnimator.ofInt(0,WAIT_TIME) ;
        animator.setDuration(WAIT_TIME * 1000) ;
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (int) valueAnimator.getAnimatedValue();
                if(WAIT_TIME == value){
                    if(mContentView != null && mContentView.getVisibility() != View.GONE){
                        mContentView.setVisibility(View.GONE) ;
                    }
                }
            }
        });
        animator.start() ;
    }

    /**
     * 填充内容
     * @param gameType 游戏类型
     * @param result 结果
     * @param period 期数
     */
    public void updateDisplay(String gameType,String[] result,String period){
        List<AwardBean> list = new ArrayList<>();
        int type = 1 ;

        if("1".equals(gameType)){//快三
            type = 1 ;

            int imgeUrl = 0;
            for (String str : result) {
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
        }else if("2".equals(gameType) || "4".equals(gameType) || "6".equals(gameType)){//11选5  时时彩  快乐十分
            type = 2 ;

            for (String str : result) {
                list.add(new AwardBean(str));
            }
        }else if("3".equals(gameType)){//赛车
            type = 4 ;

            for (String str : result){
                list.add(new AwardBean(str));
            }
        }else if("5".equals(gameType)){//六合彩
            type = 3 ;

            for (int i = 0; i < result.length; i++) {
                if (i == result.length - 1) {
                    list.add(new AwardBean("",R.mipmap.ic_game_point_add_white));
//                    list.add(new AwardBean("+",R.drawable.bg_game_point_add));
//                    list.add(new AwardBean("",R.mipmap.ic_game_point_add));
                }
                String number = result[i] ;
                list.add(new AwardBean(number,AwardBean.getItemBackgroundByName(number))) ;
            }
        }else if("7".equals(gameType)){//时时彩
            type = 1 ;

            int imgeUrl = 0;
            for (String str : result) {
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
                } else if(str.equals("7")){
                    imgeUrl = R.mipmap.cqxync07;
                }else if (str.equals("8")) {
                    imgeUrl = R.mipmap.cqxync08;
                } else if (str.equals("9")) {
                    imgeUrl = R.mipmap.cqxync09;
                } else if (str.equals("10")) {
                    imgeUrl = R.mipmap.cqxync10;
                } else if (str.equals("11")) {
                    imgeUrl = R.mipmap.cqxync11;
                } else if (str.equals("12")) {
                    imgeUrl = R.mipmap.cqxync12;
                } else if(str.equals("13")){
                    imgeUrl = R.mipmap.cqxync13;
                }else if (str.equals("14")) {
                    imgeUrl = R.mipmap.cqxync14;
                } else if (str.equals("15")) {
                    imgeUrl = R.mipmap.cqxync15;
                } else if (str.equals("16")) {
                    imgeUrl = R.mipmap.cqxync16;
                } else if (str.equals("17")) {
                    imgeUrl = R.mipmap.cqxync17;
                } else if (str.equals("18")) {
                    imgeUrl = R.mipmap.cqxync18;
                } else if(str.equals("19")){
                    imgeUrl = R.mipmap.cqxync19;
                }else if (str.equals("20")){
                    imgeUrl = R.mipmap.cqxync20;
                }
                list.add(new AwardBean(imgeUrl));
            }
        }

        mResultRv.setHasFixedSize(true);
        mResultRv.setLayoutManager(new GridLayoutManager(mContext, 1, GridLayoutManager.HORIZONTAL, false));

        int itemSize = list.size() ;
        int cornerSize = DpUtil.dp2px(20) ;
        int parentWidth = ScreenDimenUtil.getInstance().getScreenWdith()- DpUtil.dp2px(110) ;
        int dvSize = DpUtil.dp2px(3) ;
        if((cornerSize + dvSize) * itemSize > parentWidth){
            cornerSize = (parentWidth - (dvSize * itemSize)) / itemSize ;
        }

        LastAwardAdapter adapter = new LastAwardAdapter(mContext, list, type,cornerSize) ;
        mResultRv.setAdapter(adapter);

        //1快三 2十一选五 3赛车 4时时彩 5六合彩 6快乐十分 7幸运农场
        int imageId = HttpConsts.getGameIconByType(gameType) ;
        StringBuffer gameTipsBuf = new StringBuffer() ;
        gameTipsBuf
                .append(HttpConsts.getGameNameByType(gameType))
                .append(period)
                .append("期开奖") ;
        mGameNameTv.setText(gameTipsBuf.toString()) ;
        mGameTypeIv.setBackgroundResource(imageId) ;

        startAnim() ;
    }

}
