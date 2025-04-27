package com.yunbao.phonelive.bean;

import com.yunbao.phonelive.R;

import java.util.ArrayList;
import java.util.List;

public class GameTzChip {
    private String chipValue ;
    private int bgRes ;

    public GameTzChip(String chipValue, int bgRes) {
        this.chipValue = chipValue;
        this.bgRes = bgRes;
    }

    public String getChipValue() {
        return chipValue;
    }

    public int getBgRes() {
        return bgRes;
    }

    public static List<GameTzChip> getDefaultChipList(){
        List<GameTzChip> chipList = new ArrayList<>() ;
        chipList.add(new GameTzChip("2",R.mipmap.icon_game_chip_two)) ;
        chipList.add(new GameTzChip("5",R.mipmap.icon_game_chip_five)) ;
        chipList.add(new GameTzChip("10",R.mipmap.icon_game_chip_ten)) ;
        chipList.add(new GameTzChip("50",R.mipmap.icon_game_chip_fifty)) ;
        chipList.add(new GameTzChip("100",R.mipmap.icon_game_chip_hundred)) ;
        chipList.add(new GameTzChip("200",R.mipmap.icon_game_chip_hundred_two)) ;
        chipList.add(new GameTzChip("500",R.mipmap.icon_game_chip_hundred_five)) ;

        return chipList ;
    }
}
