package com.yunbao.phonelive.event;

public class GameResultEvent {
    private boolean isOnlyTime ;
    private String gameType ;
    private String newPeriod ;
    private int time ;

    public GameResultEvent(boolean isOnlyTime,String gameType, String newPeriod,int time) {
        this.isOnlyTime = isOnlyTime;
        this.gameType = gameType;
        this.newPeriod = newPeriod;
        this.time = time;
    }

    public boolean isOnlyTime() {
        return isOnlyTime;
    }

    public String getGameType() {
        return gameType;
    }

    public int getTime() {
        return time ;
    }

    public String getNewPeriod() {
        return newPeriod;
    }
}
