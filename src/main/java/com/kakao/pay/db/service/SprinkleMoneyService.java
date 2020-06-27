package com.kakao.pay.db.service;

import com.kakao.pay.db.dto.MoneyResultDto;
import com.kakao.pay.db.dto.ReceiveMoneyResultDto;
import com.kakao.pay.db.dto.SprinkleMoneyDto;
import com.kakao.pay.db.dto.SprinkleMoneyResultDto;
import com.kakao.pay.db.repo.SprinkleMoneyRepo;
import com.kakao.pay.util.Util;
import com.sun.tools.corba.se.idl.StringGen;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Service
public class SprinkleMoneyService {

    @Autowired
    private SprinkleMoneyRepo sprinkleMoneyRepo;

    /**
     * 뿌리기가 발생하면 DB에 저장
     * */
    public void insertSprinkleMoney(SprinkleMoneyDto sprinkleMoneyDto) {
        sprinkleMoneyRepo.insert(sprinkleMoneyDto);
    }

    /**
     * Token 정보로 뿌리기 정보 조회
     * */
    public SprinkleMoneyDto findByToken(String token) {
        return sprinkleMoneyRepo.findByToken(token);
    }

    /**
     * Token, 방정보로 뿌리기 정보 조회
     * */
    public SprinkleMoneyDto findByTokenRoomId(String token, String roomId) {
        return sprinkleMoneyRepo.findByTokenAndRoomId(token, roomId);
    }

    /**
     * Token save 하여 object update
     * */
    public SprinkleMoneyDto save(SprinkleMoneyDto sprinkleMoneyDto) {
        return sprinkleMoneyRepo.save(sprinkleMoneyDto);
    }

//  다음 조건을 만족하는 뿌리기 API를 만들어 주세요.
//  ○ 뿌릴 금액, 뿌릴 인원을 요청값으로 받습니다.
//  ○ 뿌리기 요청건에 대한 고유 token을 발급하고 응답값으로 내려줍니다.
//  ○ 뿌릴 금액을 인원수에 맞게 분배하여 저장합니다. (분배 로직은 자유롭게 구현해 주세요.)
//  ○ token은 3자리 문자열로 구성되며 예측이 불가능해야 합니다.
    public SprinkleMoneyResultDto sprinkleMoney(String reqUserId, String roomId, String totalAmt, List<String> sprinkleUserList) {
        int totalPerson = sprinkleUserList.size();
        long sprinkleAmt;
        List<JSONObject> sprinkleInfoList = new LinkedList();
        JSONObject sprinkleInfoObj = new JSONObject();
        SprinkleMoneyResultDto result = new SprinkleMoneyResultDto();
        long remindAmt = Long.parseLong(totalAmt);
        int size = sprinkleUserList.size();
        int count = 0;

        // 1. 토큰만들기
        String token = Util.getToken();

        // 2. 요청 user 들에게 돈을 분배
        for (String userId : sprinkleUserList) {
            // 3. 분배하기
            sprinkleInfoObj.put("userId", Util.getPlainUserId(userId));
            if (count == size - 1) {
                // 마지막에는 남은 금액 모두 분배
                sprinkleInfoObj.put("amt", Long.toString(remindAmt));
            } else {
                // 금액 분배
                sprinkleAmt = getSprinkleAmt(remindAmt);
                remindAmt -= sprinkleAmt;
                sprinkleInfoObj.put("amt", Long.toString(sprinkleAmt));
            }
            sprinkleInfoObj.put("sprinkleYn", "N");

            sprinkleInfoList.add(sprinkleInfoObj);

            sprinkleInfoObj = new JSONObject();
            ++count;
        }

        // 3. db에 분배 내용 저장
        SprinkleMoneyDto dto = new SprinkleMoneyDto();
        // 현재 시간
        dto.setSprinkleTime(new Date());
        // 뿌린 userId
        dto.setSprinkleUseId(reqUserId);
        // 체팅방 번호
        dto.setRoomId(roomId);
        // 총 뿌린 금액
        dto.setTotalAmt(Long.parseLong(totalAmt));
        // 총 뿌린 받은 사람 수
        dto.setTotalPerson(totalPerson);
        // token
        dto.setToken(token);
        // 전체 금액
        dto.setTotalAmt(Long.parseLong(totalAmt));
        // 뿌림 받을 사용자 정보
        dto.setSprinkleInfoList(sprinkleInfoList);
        // 뿌리기 받기 완료된 금액
        dto.setCmlSprinkleAmt(0L);


        // TODO : 성공/실패 구분해서 리턴
        this.insertSprinkleMoney(dto);

        // 4. 결과값 리턴
        result.setToken(token);
        result.setResultCode("0000");
        result.setResultMsg("성공");

        return result;
    }

//  다음 조건을 만족하는 받기 API를 만들어 주세요.
//  ○ 뿌리기 시 발급된 token을 요청값으로 받습니다.
//  ○ token에 해당하는 뿌리기 건 중 아직 누구에게도 할당되지 않은 분배건 하나를 API를 호출한 사용자에게 할당하고, 그 금액을 응답값으로 내려줍니다.
//  ○ 뿌리기 당 한 사용자는 한번만 받을 수 있습니다.
//  ○ 자신이 뿌리기한 건은 자신이 받을 수 없습니다.
//  ○ 뿌린기가 호출된 대화방과 동일한 대화방에 속한 사용자만이 받을 수 있습니다.
//  ○ 뿌린 건은 10분간만 유효합니다. 뿌린지 10분이 지난 요청에 대해서는 받기 실패 응답이 내려가야 합니다.
    public ReceiveMoneyResultDto receiveMoney(String reqUserId, String roomId, String token) {
        String userId  = "";
        String sprinkleYn = "";
        String sprinkleAmt = "";
        String resultCode = "";
        String resultMsg = "";
        ReceiveMoneyResultDto result = new ReceiveMoneyResultDto();

        // 1. token 값으로 조회
        SprinkleMoneyDto info = this.findByTokenRoomId(token, roomId);

        // 2. 뿌리기가 호출된 대화방과 동일한 대화방에 속한 사용자만이 받을 수 있다.
        if (info == null) {
            resultCode  = "8000";
            resultMsg   = "뿌리기가 호출된 대화방과 동일한 대화방에서만 받을 수 있습니다.";

            result.setResultCode(resultCode);
            result.setResultMsg(resultMsg);

            return result;
        }

        // 3. 자신이 뿌리기 한건은 자신이 받을수 없음
        if (reqUserId.equals(info.getSprinkleUseId())) {
            resultCode  = "8001";
            resultMsg   = "자신이 뿌리기 한건은 받을 수 없습니다.";

            result.setResultCode(resultCode);
            result.setResultMsg(resultMsg);

            return result;
        }

        // 4. 뿌린 건은 10분간만 유효합니다. 뿌린지 10분이 지난 요청에 대해서는 받기 실패 응답이 내려가야 합니다.
        if (Util.getDiffTime(new Date(), info.getSprinkleTime()) > 10) {
            resultCode  = "8002";
            resultMsg   = "뿌린 건은 10분간만 유효합니다.";

            result.setResultCode(resultCode);
            result.setResultMsg(resultMsg);

            return result;
        }

        // 5. 뿌리기 받을 사용자 검사
        List<JSONObject> infoList = info.getSprinkleInfoList();
        for (JSONObject sprinkleInfo : infoList) {
            userId = (String) sprinkleInfo.get("userId");
            sprinkleYn = (String) sprinkleInfo.get("sprinkleYn");
            sprinkleAmt = (String) sprinkleInfo.get("amt");

            if (reqUserId.equals(userId)) {
                if ("N".equals(sprinkleYn)) {
                    // 아직 받지 않음
                    resultCode = "0000";
                    resultMsg = "성공";

                    result.setResultCode(resultCode);
                    result.setResultMsg(resultMsg);
                    result.setAmt(sprinkleAmt);

                    // 분배된 금액을 받음으로 update
                    sprinkleInfo.put("sprinkleYn", "Y");
                    info.setSprinkleInfoList(infoList);

                    // 분배되었으면 분배 완료 금액 update
                    info.setCmlSprinkleAmt(info.getCmlSprinkleAmt() + Long.parseLong(sprinkleAmt));

                    // save 하여 데이터 update
                    this.sprinkleMoneyRepo.save(info);

                    return result;
                } else {
                    // 받음
                    resultCode = "8003";
                    resultMsg = "이미 받은 뿌리기입니다.";

                    result.setResultCode(resultCode);
                    result.setResultMsg(resultMsg);

                    return result;
                }
            }
        }

        resultCode = "8004";
        resultMsg = "해당 뿌리기를 받을 대상이 아닙니다.";

        result.setResultCode(resultCode);
        result.setResultMsg(resultMsg);

        return result;
    }

//  다음 조건을 만족하는 조회 API를 만들어 주세요.
//  ○ 뿌리기 시 발급된 token을 요청값으로 받습니다.
//  ○ token에 해당하는 뿌리기 건의 현재 상태를 응답값으로 내려줍니다. 현재 상태는 다음의 정보를 포함합니다.
//  ○ 뿌린 시각, 뿌린 금액, 받기 완료된 금액, 받기 완료된 정보 ([받은 금액, 받은 사용자 아이디] 리스트)
//  ○ 뿌린 사람 자신만 조회를 할 수 있습니다. 다른사람의 뿌리기건이나 유효하지 않은 token에 대해서는 조회 실패 응답이 내려가야 합니다.
//  ○ 뿌린 건에 대한 조회는 7일 동안 할 수 있습니다.
    public MoneyResultDto getMoney(String reqUserId, String roomId, String token) {
        String resultCode = "";
        String resultMsg = "";
        String resultAmt = "";
        List resultList = new LinkedList();
        MoneyResultDto result = new MoneyResultDto();

        // 1. token 값으로 조회
        SprinkleMoneyDto info = this.findByTokenRoomId(token, roomId);

        // 1-1. token 으로 조회 값이 없으면 실패 응답
        if (info == null) {
            resultCode  = "9000";
            resultMsg   = "유효하지 않은 토큰입니다.";

            result.setResultCode(resultCode);
            result.setResultMsg(resultMsg);

            return result;
        }

        // 2. 요청자 ID 와 뿌리기 요청 ID 검사
        // reqUserId null 검사
        if (StringUtils.isEmpty(reqUserId)) {
            resultCode  = "9001";
            resultMsg   = "요청자ID가 없습니다.";

            result.setResultCode(resultCode);
            result.setResultMsg(resultMsg);

            return result;
        }

        // 뿌린 사람만 조회가 가능
        if (reqUserId.equals(info.getSprinkleUseId())) {
            // 3. 뿌리기 요청 후 7일 이전 건만 확인 가능
            if (Util.getDiffDate(new Date(), info.getSprinkleTime()) <= 7) {
                // 뿌리기 요청 후 7일 이전 건만 조회가 가능.
                resultCode  = "0000";
                resultMsg   = "성공";
                resultList  = info.getSprinkleInfoList();

                result.setResultCode(resultCode);
                result.setResultMsg(resultMsg);
                result.setSprinkleTime(info.getSprinkleTime());
                result.setSprinkleAmt(info.getTotalAmt());
                result.setReceiveAmt(info.getCmlSprinkleAmt());
                result.setList(resultList);

                // 조회 결과가 없음
                if (resultList == null || resultList.isEmpty()) {
                    resultCode  = "9004";
                    resultMsg   = "조회 결과가 없습니다.";

                    result.setResultCode(resultCode);
                    result.setResultMsg(resultMsg);
                }

                return result;

            } else {
                // 뿌리기 요청 후 7일 이후건은 조회 불가
                resultCode  = "9003";
                resultMsg   = "뿌린뒤 7일 이전 데이터만 조회가 가능 합니다.";

                result.setResultCode(resultCode);
                result.setResultMsg(resultMsg);

                return result;
            }
        } else {
            // 요청자ID와 뿌린 사람이 다름.
            resultCode  = "9002";
            resultMsg   = "요청자ID와 뿌린사람이 다름니다.";

            result.setResultCode(resultCode);
            result.setResultMsg(resultMsg);

            return result;
        }
    }

    /**
     * 랜덤으로 금액 분배
     * */
    private long getSprinkleAmt(long amt) {
        return (long)(Math.random()*amt);
    }

    public boolean testDate(String reqUserId, String roomId, String token) {
        SprinkleMoneyDto info = sprinkleMoneyRepo.findByTokenAndRoomId(token, roomId);
        info.setSprinkleTime(getTestDate(info.getSprinkleTime(), 8));
        sprinkleMoneyRepo.save(info);

        return true;
    }

    public boolean testTime(String reqUserId, String roomId, String token) {
        SprinkleMoneyDto info = sprinkleMoneyRepo.findByTokenAndRoomId(token, roomId);
        info.setSprinkleTime(getTestTime(info.getSprinkleTime(), 12));
        sprinkleMoneyRepo.save(info);

        return false;
    }


    private Date getTestTime(Date curDate, int min) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(curDate);
        cal.add(Calendar.MINUTE, min);
        return new Date(cal.getTimeInMillis());
    }

    private Date getTestDate(Date curDate, int date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(curDate);
        cal.add(Calendar.DATE, date);
        return new Date(cal.getTimeInMillis());
    }
}
