package com.kakao.pay.db.dto;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Document(collection ="SprinkleMoney")
public class SprinkleMoneyDto {

    private static final long serialVersionUID = 1L;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSprinkleUseId() {
        return sprinkleUseId;
    }

    public void setSprinkleUseId(String sprinkleUseId) {
        this.sprinkleUseId = sprinkleUseId;
    }

    public List<JSONObject> getSprinkleInfoList() {
        return sprinkleInfoList;
    }

    public void setSprinkleInfoList(List<JSONObject> sprinkleInfoList) {
        this.sprinkleInfoList = sprinkleInfoList;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Date getSprinkleTime() {
        return sprinkleTime;
    }

    public void setSprinkleTime(Date sprinkleTime) {
        this.sprinkleTime = sprinkleTime;
    }

    public Long getCmlSprinkleAmt() {
        return cmlSprinkleAmt;
    }

    public void setCmlSprinkleAmt(Long cmlSprinkleAmt) {
        this.cmlSprinkleAmt = cmlSprinkleAmt;
    }

    public int getCmlSprinklePerson() {
        return cmlSprinklePerson;
    }

    public void setCmlSprinklePerson(int cmlSprinklePerson) {
        this.cmlSprinklePerson = cmlSprinklePerson;
    }

    public Long getTotalAmt() {
        return totalAmt;
    }

    public void setTotalAmt(Long totalAmt) {
        this.totalAmt = totalAmt;
    }

    public int getTotalPerson() {
        return totalPerson;
    }

    public void setTotalPerson(int totalPerson) {
        this.totalPerson = totalPerson;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String token;
    private String sprinkleUseId;
    private List<JSONObject> sprinkleInfoList;
    private String roomId;
    private Date sprinkleTime;
    private Long cmlSprinkleAmt;
    private int cmlSprinklePerson;
    private Long totalAmt;
    private int totalPerson;
    @Id
    private String id;
}
