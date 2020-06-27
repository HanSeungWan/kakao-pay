package com.kakao.pay.util;

import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.UUID;

public class Util {

    /**
     * token 값을 생성
     * */
    public static String getToken() {

        return UUID.randomUUID().toString().substring(0, 3);
    }

    public static long getDiffDate(Date curDate, Date targetDate) {

        long diffTime = curDate.getTime() - targetDate.getTime();

        long diffDate = (diffTime / (24*60*60*1000));

        long adbDiffDate = Math.abs(diffDate);

        return adbDiffDate;
    }

    public static long getDiffTime(Date curDate, Date targetDate) {

        long diffMinute = (curDate.getTime() - targetDate.getTime()) / 60000;

        return diffMinute;
    }

    public static String getPlainUserId(String userId) {
        if (!StringUtils.isEmpty(userId)) {
            if (userId.contains("%5B")) {
                return userId.replace("%5B", "");
            } else if (userId.contains("%2") && userId.contains("%5D")) {
                String tempUserId = userId.replace("%2", "");
                return tempUserId.replace("%5D", "");
            } else if (userId.contains("%2")) {
                return userId.replace("%2", "");
            }
        }

        return "";
    }
}
