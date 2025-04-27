package com.yunbao.phonelive.bean;

import java.util.List;

public class NiurenInfoBean {
    private NiurenTop fortune ;
    private NiurenTop financial_gas ;
    private NiurenTop probability ;
    private List<NiurenList> lists ;

    public NiurenTop getFortune() {
        return fortune;
    }

    public void setFortune(NiurenTop fortune) {
        this.fortune = fortune;
    }

    public NiurenTop getFinancial_gas() {
        return financial_gas;
    }

    public void setFinancial_gas(NiurenTop financial_gas) {
        this.financial_gas = financial_gas;
    }

    public NiurenTop getProbability() {
        return probability;
    }

    public void setProbability(NiurenTop probability) {
        this.probability = probability;
    }

    public List<NiurenList> getLists() {
        return lists;
    }

    public void setLists(List<NiurenList> lists) {
        this.lists = lists;
    }
}
