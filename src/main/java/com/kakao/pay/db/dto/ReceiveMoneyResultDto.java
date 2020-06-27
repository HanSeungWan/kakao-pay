package com.kakao.pay.db.dto;

public class ReceiveMoneyResultDto extends ResultDto{

    public String getAmt() {
        return amt;
    }

    public void setAmt(String amt) {
        this.amt = amt;
    }

    private String amt;

}
