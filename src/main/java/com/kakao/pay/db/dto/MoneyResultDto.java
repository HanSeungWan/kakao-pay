package com.kakao.pay.db.dto;

import org.json.simple.JSONObject;

import java.util.Date;
import java.util.List;

public class MoneyResultDto extends ResultDto {

    // 뿌린 시각, 뿌린 금액, 받기 완료된 금액, 받기 완료된 정보 ([받은 금액, 받은 사용자 아이디] 리스트)

    public List<JSONObject> getList() {
        return list;
    }

    public void setList(List<JSONObject> list) {
        this.list = list;
    }

    public Date getSprinkleTime() {
        return sprinkleTime;
    }

    public void setSprinkleTime(Date sprinkleTime) {
        this.sprinkleTime = sprinkleTime;
    }

    public long getSprinkleAmt() {
        return sprinkleAmt;
    }

    public void setSprinkleAmt(long sprinkleAmt) {
        this.sprinkleAmt = sprinkleAmt;
    }

    public long getReceiveAmt() {
        return receiveAmt;
    }

    public void setReceiveAmt(long receiveAmt) {
        this.receiveAmt = receiveAmt;
    }

    private List<JSONObject> list;
    private Date sprinkleTime;
    private long sprinkleAmt;
    private long receiveAmt;
}
