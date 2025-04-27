package com.yunbao.phonelive.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.yunbao.phonelive.R;
import com.yunbao.phonelive.adapter.ChipOptionAdapter;
import com.yunbao.phonelive.game.GameBean;
import com.yunbao.phonelive.utils.DialogUitl;
import com.yunbao.phonelive.utils.SpUtil;
import com.yunbao.phonelive.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class LiveGameChipDialogFragment extends AbsDialogFragment implements View.OnClickListener {

    private ImageView chipClose;
    private Button sure;
    private RecyclerView recyclerView;
    private List<GameBean> list;
    private ChipOptionAdapter adapter;
    private CallBack callBack;

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    @Override
    protected int getLayoutId() { return R.layout.dialog_live_game_chips; }

    @Override
    protected int getDialogStyle() { return R.style.dialog2; }

    @Override
    protected boolean canCancel() { return true; }

    @Override
    protected void setWindowAttributes(Window window) {
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        window.setAttributes(params);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        chipClose = mRootView.findViewById(R.id.game_chip_close);
        sure = mRootView.findViewById(R.id.game_chip_sure);
        recyclerView = mRootView.findViewById(R.id.recycler);

        chipClose.setOnClickListener(this);
        sure.setOnClickListener(this);
        list = new ArrayList<>();

        String str = SpUtil.getInstance().getStringValue("iskTv");
        if (str != null && !str.equals("")) {
            boolean isCus = SpUtil.getInstance().getBooleanValue("iskCus") ;
            int num = Integer.parseInt(str);
            list.add(new GameBean(2, R.mipmap.icon_game_chip_two, !isCus && num == 2));
            list.add(new GameBean(5, R.mipmap.icon_game_chip_five, !isCus && num == 5));
            list.add(new GameBean(10, R.mipmap.icon_game_chip_ten, !isCus && num == 10));
            list.add(new GameBean(50, R.mipmap.icon_game_chip_fifty, !isCus && num == 50));
            list.add(new GameBean(100, R.mipmap.icon_game_chip_hundred, !isCus && num == 100));
            list.add(new GameBean(200, R.mipmap.icon_game_chip_hundred_two, !isCus && num == 200));
            list.add(new GameBean(500, R.mipmap.icon_game_chip_hundred_five, !isCus && num == 500));

            list.add(new GameBean(true));
        }else{
            list.add(new GameBean(2, R.mipmap.icon_game_chip_two, false));
            list.add(new GameBean(5, R.mipmap.icon_game_chip_five, false));
            list.add(new GameBean(10, R.mipmap.icon_game_chip_ten, false));
            list.add(new GameBean(50, R.mipmap.icon_game_chip_fifty, false));
            list.add(new GameBean(100, R.mipmap.icon_game_chip_hundred, false));
            list.add(new GameBean(200, R.mipmap.icon_game_chip_hundred_two, false));
            list.add(new GameBean(500, R.mipmap.icon_game_chip_hundred_five, false));
            list.add(new GameBean(true));

            SpUtil.getInstance().setBooleanValue("iskCus", false);
            SpUtil.getInstance().setStringValue("iskTv", "2");
            SpUtil.getInstance().setStringValue("iskIv", String.valueOf(R.mipmap.icon_game_chip_two));
        }

        recyclerView.setLayoutManager(new GridLayoutManager(mContext, 4, GridLayoutManager.VERTICAL, false));
        adapter = new ChipOptionAdapter(mContext, getLayoutInflater(), list, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUitl.showSimpleInputDialog(mContext, "自定义筹码","筹码为1到10000之间"
                        , DialogUitl.INPUT_TYPE_NUMBER, 5, new DialogUitl.SimpleCallback() {
                    @Override
                    public void onConfirmClick(Dialog dialog, String content) {
                        int inputValue = 0 ;
                        try {
                            inputValue = Integer.valueOf(content) ;
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }

                        if (inputValue <= 0) {
                            ToastUtil.show("筹码值必须大于0");
                            return ;
                        }

                        if (inputValue > 10000) {
                            ToastUtil.show("筹码值不能大于10000") ;
                        } else {
                            list.get(list.size() - 1).setCustomValue(inputValue) ;
                            adapter.notifyDataSetChanged() ;

                            dialog.dismiss();
                        }
                    }
                });

            }
        });
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.game_chip_close:
                dismiss();
                break;
            case R.id.game_chip_sure:

                submitChange() ;

                break;
        }
    }

    private void submitChange(){
        GameBean checkItem = null ;

        for(GameBean item : list){
            if (item.isChecked()){
                checkItem = item ;
                break;
            }
        }

        if(checkItem == null){
            ToastUtil.show("请选择筹码");
            return;
        }

        boolean isCus = checkItem.isCustom() ;
        if(isCus && checkItem.getCustomValue() <= 0){
            ToastUtil.show("请设置正确的筹码");
            return;
        }

        int value = isCus ? checkItem.getCustomValue() : checkItem.getValue() ;

        SpUtil.getInstance().setBooleanValue("iskCus",isCus);
        SpUtil.getInstance().setStringValue("iskTv",String.valueOf(value));
        SpUtil.getInstance().setStringValue("iskIv",String.valueOf(checkItem.getImageId()));
        callBack.onClick(checkItem);
        dismiss();
    }

    interface CallBack{
        void onClick(GameBean bean);
    }
}
