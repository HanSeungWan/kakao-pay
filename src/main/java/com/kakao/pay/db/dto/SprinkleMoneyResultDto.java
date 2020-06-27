package com.kakao.pay.db.dto;

public class SprinkleMoneyResultDto extends ResultDto {

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String token;
}
