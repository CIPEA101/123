package com.yunbao.phonelive.activity;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.yunbao.phonelive.R;
import com.yunbao.phonelive.adapter.LastAwardAdapter;
import com.yunbao.phonelive.game.AwardBean;
import com.yunbao.phonelive.game.TzInfoBean;
import com.yunbao.phonelive.http.HttpCallback;
import com.yunbao.phonelive.http.HttpConsts;
import com.yunbao.phonelive.http.HttpUtil;
import com.yunbao.phonelive.utils.DateFormatUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TzDetailActivity extends AbsActivity {

    private TextView mName;
    private TextView mZj;
    private TextView mWf;
    private TextView mTime;
    private TextView mTotal;
    private TextView mTzje;
    private RecyclerView recycler1;
    private TextView mDh;
    private String gameId;
    private String id;
    private TextView mTitleView;
    private TextView tvCode;
    private TextView odds;
    private TextView tvJe;
    private TextView tvZt;
    private TextView tvZjje;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_tz_detail;
    }

    @Override
    protected void main() {
        mTitleView = findViewById(R.id.titleView);
        mName = findViewById(R.id.tv_game_name);
        mZj = findViewById(R.id.tv_zj);
        mWf = findViewById(R.id.tv_wf);
        mTime = findViewById(R.id.tv_time);
        mTotal = findViewById(R.id.tv_total);
        mTzje = findViewById(R.id.tv_zjje);
        recycler1 = findViewById(R.id.recycler1);
        mDh = findViewById(R.id.tv_dh);
        tvCode = findViewById(R.id.tv_code);
        odds = findViewById(R.id.tv_odds);
        tvJe = findViewById(R.id.tv_je);
        tvZt = findViewById(R.id.tv_status);
        tvZjje = findViewById(R.id.tv_zjje_sj);

        recycler1.setHasFixedSize(true);
        recycler1.setLayoutManager(new GridLayoutManager(mContext, 1, GridLayoutManager.HORIZONTAL, false));

        gameId = getIntent().getStringExtra("gameId");
        id = getIntent().getStringExtra("id");

        mTitleView.setText("投注详情");
        loadData();
    }

    private void loadData() {
        HttpUtil.getTzRecordDetail(gameId, id, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                List<TzInfoBean> list = JSON.parseArray(Arrays.toString(info), TzInfoBean.class);
                if (list != null && list.size() > 0) {
                    TzInfoBean item = list.get(0);
                    String gameName = "";
                    String gameQs = "";
                    String strWf = "";
                    switch (item.getGame_id()) {
                        case "1":
                            switch (item.getType()) {
                                case "1":
                                    strWf = "总和";
                                    break;
                                case "2":
                                    strWf = "三军";
                                    break;
                                case "3":
                                    strWf = "短牌";
                                    break;
                            }
                            gameQs = item.getYfks_id();
                            gameName = HttpConsts.GAME_NAME_YFKS;
                            break;
                        case "2":
                            switch (item.getType()) {
                                case "1":
                                    strWf = "总和";
                                    break;
                                case "2":
                                    strWf = "第一球两面";
                                    break;
                                case "3":
                                    strWf = "全五中一";
                                    break;
                            }

                            gameQs = item.getYf115_id();
                            gameName = HttpConsts.GAME_NAME_YF115;
                            break;
                        case "3":
                            switch (item.getType()) {
                                case "1":
                                    strWf = "冠军单码";
                                    break;
                                case "2":
                                    strWf = "冠亚和";
                                    break;
                                case "3":
                                    strWf = "冠军两面";
                                    break;
                            }
                            gameQs = item.getYfsc_id();
                            gameName = HttpConsts.GAME_NAME_YFSC;
                            break;
                        case "4":
                            switch (item.getType()) {
                                case "1":
                                    strWf = "第一球两面";
                                    break;
                                case "2":
                                    strWf = "第一球VS第五球";
                                    break;
                                case "3":
                                    strWf = "全5中1";
                                    break;
                            }
                            gameQs = item.getYfssc_id();
                            gameName = HttpConsts.GAME_NAME_YFSSC;
                            break;
                        case "5":
                            switch (item.getType()) {
                                case "1":
                                    strWf = "特码两面";
                                    break;
                                case "2":
                                    strWf = "特码生肖";
                                    break;
                                case "3":
                                    strWf = "特码色波";
                                    break;
                            }
                            gameQs = item.getYflhc_id();
                            gameName = HttpConsts.GAME_NAME_YFLHC;
                            break;
                        case "6":
                            switch (item.getType()) {
                                case "1":
                                    strWf = "总和";
                                    break;
                                case "2":
                                    strWf = "第一球两面";
                                    break;
                                case "3":
                                    strWf = "第八球两面";
                                    break;
                            }
                            gameQs = item.getYfklsf_id();
                            gameName = HttpConsts.GAME_NAME_YFKLSF;
                            break;
                        case "7":
                            switch (item.getType()) {
                                case "1":
                                    strWf = "总和";
                                    break;
                                case "2":
                                    strWf = "第一球两面";
                                    break;
                                case "3":
                                    strWf = "第八球两面";
                                    break;
                            }
                            gameQs = item.getYfxync_id();
                            gameName = HttpConsts.GAME_NAME_YFXYNC;
                            break;
                    }
                    mName.setText(gameName + " 第" + DateFormatUtil.dateTimeToYmd(Long.parseLong(item.getDatetime())) + gameQs + "期");
                    mZj.setText(item.getIs_ok().equals("1") ? "中奖" : "未中奖");
                    mWf.setText(strWf);
                    mTime.setText(DateFormatUtil.dateTimeToString(Long.parseLong(item.getDatetime())));
                    mTotal.setText(String.valueOf(Integer.parseInt(item.getTotalcoin())*Integer.parseInt(item.getMagnification())));
                    mTzje.setText(String.valueOf(Integer.parseInt(item.getTotalcoin())*Integer.parseInt(item.getMagnification())*Float.parseFloat(item.getOdds())));
                    mDh.setText(item.getId());
                    tvCode.setText(item.getCode());
                    odds.setText(item.getOdds());
                    tvJe.setText(String.valueOf(Integer.parseInt(item.getTotalcoin())*Integer.parseInt(item.getMagnification())));
                    tvZt.setText(item.getIs_ok().equals("1") ? "中奖" : "未中奖");
                    tvZjje.setText(String.valueOf(Integer.parseInt(item.getTotalcoin())*Integer.parseInt(item.getMagnification())*Float.parseFloat(item.getOdds())));
                    String result = item.getResult();
                    String[] strArr = result.split(",");
                    List<AwardBean> listAb = new ArrayList<>();
                    int type =0;
                    int imgeUrl;
                    switch (gameId){
                        case "1":
                            imgeUrl = 0;
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
                                listAb.add(new AwardBean(imgeUrl));
                            }
                            type = 1;
                            break;
                        case "2":
                        case "4":
                        case "6":
                            for (String str : strArr) {
                                listAb.add(new AwardBean(str));
                            }
                            type = 2;
                            break;
                        case "3":
                            for (String str : strArr){
                                listAb.add(new AwardBean(str));
                            }
                            type = 4;
                            break;
                        case "5":
                            for (int i = 0; i < strArr.length; i++) {
                                if (i == strArr.length - 1) {
                                    listAb.add(new AwardBean("",R.mipmap.ic_game_point_add_black));
//                                    listAb.add(new AwardBean("+",R.drawable.bg_game_point_add));
//                                    listAb.add(new AwardBean("",R.mipmap.ic_game_point_add));
                                }
                                String number = strArr[i] ;
                                listAb.add(new AwardBean(number,AwardBean.getItemBackgroundByName(number))) ;
                            }
                            type = 3;
                            break;
                        case "7":
                            imgeUrl = 0;
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
                                listAb.add(new AwardBean(imgeUrl));
                            }
                            break;
                    }
                    LastAwardAdapter adapter = new LastAwardAdapter(mContext, getLayoutInflater(), listAb, type);
                    recycler1.setAdapter(adapter);
                }
            }
        });
    }
}
