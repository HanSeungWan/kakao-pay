package com.kakao.pay;

import com.kakao.pay.db.dto.MoneyResultDto;
import com.kakao.pay.db.dto.ReceiveMoneyResultDto;
import com.kakao.pay.db.dto.SprinkleMoneyDto;
import com.kakao.pay.db.dto.SprinkleMoneyResultDto;
import com.kakao.pay.db.repo.SprinkleMoneyRepo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class test {
    private TestApi testApi = new TestApi(new RestTemplateBuilder());

    @Autowired
    private SprinkleMoneyRepo sprinkleMoneyRepo;

    @Test
    public void 전체_시나리오_테스트() {

        // 테스트 데이터 생성
        String reqUserId = "000000001";
        String revUserId = "";
        String testUserId = "";
        String roomId    = "000000014";
        String totalAmt  = "100000";
        List< String> sprinkleUserList = new LinkedList<>();
        boolean testResult = false;

        sprinkleUserList.add("000000002");
        sprinkleUserList.add("000000003");
        sprinkleUserList.add("000000004");
        sprinkleUserList.add("000000005");
        sprinkleUserList.add("000000006");

        // 뿌리기_테스트_진행
        SprinkleMoneyResultDto result = testApi.sprinkleMoney(reqUserId, roomId, totalAmt, sprinkleUserList);
        String token = result.getToken();

        if ("0000".equals(result.getResultCode())) {
            testResult = true;
        } else {
            testResult = false;
        }

        assertTrue(result.getResultCode() + "(" + result.getResultMsg() + ")", testResult);

        // -- 받기 테스트
        // 1 - 1 뿌리기 당 한 사용자는 한번만 받을 수 있습니다.
        revUserId = "000000002";
        ReceiveMoneyResultDto resultReceiveMoney = testApi.receiveMoney(revUserId, roomId, token);
        if ("0000".equals(resultReceiveMoney.getResultCode())) {
            testResult = true;
        } else {
            testResult = false;
        }

        assertTrue(resultReceiveMoney.getResultCode() + "(" + resultReceiveMoney.getResultMsg() + ")", testResult);

        // 이미 받은 뿌리기
        resultReceiveMoney = testApi.receiveMoney(revUserId, roomId, token);
        if ("8003".equals(resultReceiveMoney.getResultCode())) {
            testResult = true;
        } else {
            testResult = false;
        }

        assertTrue(resultReceiveMoney.getResultCode() + "(" + resultReceiveMoney.getResultMsg() + ")", testResult);


        // 1 - 2 ○ 자신이 뿌리기한 건은 자신이 받을 수 없습니다.
        revUserId = reqUserId;
        resultReceiveMoney = testApi.receiveMoney(revUserId, roomId, token);
        if ("8001".equals(resultReceiveMoney.getResultCode())) {
            testResult = true;
        } else {
            testResult = false;
        }

        assertTrue(resultReceiveMoney.getResultCode() + "(" + resultReceiveMoney.getResultMsg() + ")", testResult);

        // 1 - 3 ○ 뿌린기가 호출된 대화방과 동일한 대화방에 속한 사용자만이 받을 수있습니다.
        revUserId = "000000009";
        resultReceiveMoney = testApi.receiveMoney(revUserId, roomId, token);
        if ("8004".equals(resultReceiveMoney.getResultCode())) {
            testResult = true;
        } else {
            testResult = false;
        }

        assertTrue(resultReceiveMoney.getResultCode() + "(" + resultReceiveMoney.getResultMsg() + ")", testResult);

        // 1 - 4 ○ 뿌린 건은 10분간만 유효합니다. 뿌린지 10분이 지난 요청에 대해서는 받기실패 응답이 내려가야 합니다
        // 10분 지나게 테스트 데이터 업데이트
//        SprinkleMoneyDto info = sprinkleMoneyRepo.findByTokenAndRoomId(token, roomId);
//        info.setSprinkleTime(getTestTime(info.getSprinkleTime(), 10));
//        sprinkleMoneyRepo.save(info);

//        resultReceiveMoney = testApi.receiveMoney(revUserId, roomId, token);
//        if ("8002".equals(resultReceiveMoney.getResultCode())) {
//            testResult = true;
//        } else {
//            testResult = false;
//        }
//
//        assertTrue(resultReceiveMoney.getResultCode() + "(" + resultReceiveMoney.getResultMsg() + ")", testResult);

        // -- 조회 테스트
        // 2 - 1  뿌린 시각, 뿌린 금액, 받기 완료된 금액, 받기 완료된 정보 ([받은 금액, 받은사용자 아이디] 리스트)
        reqUserId = "000000001";
        MoneyResultDto resultGetMoney = testApi.getMoney(reqUserId, roomId, token);
        if ("0000".equals(resultGetMoney.getResultCode())) {
            testResult = true;
        } else {
            testResult = false;
        }

        assertTrue(resultGetMoney.getResultCode() + "(" + resultGetMoney.getResultMsg() + ")", testResult);

        // 2 - 2  ○ 뿌린 사람 자신만 조회를 할 수 있습니다. 다른사람의 뿌리기건이나
        reqUserId = "000000002";
        resultGetMoney = testApi.getMoney(reqUserId, roomId, token);
        if ("9002".equals(resultGetMoney.getResultCode())) {
            testResult = true;
        } else {
            testResult = false;
        }

        assertTrue(resultGetMoney.getResultCode() + "(" + resultGetMoney.getResultMsg() + ")", testResult);

        // 2 - 3  유효하지 않은 token에 대해서는 조회 실패 응답이 내려가야 합니다.
        String testToken = "-1";
        resultGetMoney = testApi.getMoney(reqUserId, roomId, testToken);
        if ("9000".equals(resultGetMoney.getResultCode())) {
            testResult = true;
        } else {
            testResult = false;
        }

        assertTrue(resultGetMoney.getResultCode() + "(" + resultGetMoney.getResultMsg() + ")", testResult);

//        // 2 - 4  ○ 뿌린 건에 대한 조회는 7일 동안 할 수 있습니다.
//        info = sprinkleMoneyRepo.findByTokenAndRoomId(token, roomId);
//        info.setSprinkleTime(getTestDate(info.getSprinkleTime(), 7));
//        sprinkleMoneyRepo.save(info);

//        reqUserId = "000000001";
//        resultGetMoney = testApi.getMoney(reqUserId, roomId, token);
//        if ("9002".equals(resultGetMoney.getResultCode())) {
//            testResult = true;
//        } else {
//            testResult = false;
//        }
//
//        assertTrue(resultGetMoney.getResultCode() + "(" + resultGetMoney.getResultMsg() + ")", testResult);

    }

    @Test
    public void 뿌리고_받기_금액_검증_테스트() {
        // 테스트 데이터 생성
        String reqUserId = "000000001";
        String revUserId = "";
        String testUserId = "";
        String roomId    = "000000015";
        String totalAmt  = "100000";
        List< String> sprinkleUserList = new LinkedList<>();
        boolean testResult = false;

        sprinkleUserList.add("000000002");
        sprinkleUserList.add("000000003");
        sprinkleUserList.add("000000004");
        sprinkleUserList.add("000000005");
        sprinkleUserList.add("000000006");

        // 뿌리기_테스트_진행
        SprinkleMoneyResultDto result = testApi.sprinkleMoney(reqUserId, roomId, totalAmt, sprinkleUserList);
        String token = result.getToken();

        if ("0000".equals(result.getResultCode())) {
            testResult = true;
        } else {
            testResult = false;
        }

        assertTrue(result.getResultCode() + "(" + result.getResultMsg() + ")", testResult);

        // -- 조회 테스트
        reqUserId = "000000001";
        MoneyResultDto resultGetMoney = testApi.getMoney(reqUserId, roomId, token);
        if ("0000".equals(resultGetMoney.getResultCode())) {
            testResult = true;
        } else {
            testResult = false;
        }

        assertTrue(resultGetMoney.getResultCode() + "(" + resultGetMoney.getResultMsg() + ")", testResult);

        // 아직 받은 유저가 없음
        if (resultGetMoney.getReceiveAmt() == 0) {
            testResult = true;
        } else {
            testResult = false;
        }

        assertTrue(resultGetMoney.getReceiveAmt() + "", testResult);

        // 총금액 정확성 테스트
        if (totalAmt.equals(Long.toString(resultGetMoney.getSprinkleAmt()))) {
            testResult = true;
        } else {
            testResult = false;
        }

        assertTrue(resultGetMoney.getSprinkleAmt() + "", testResult);

        // -- 받기 테스트
        revUserId = "000000002";
        ReceiveMoneyResultDto resultReceiveMoney = testApi.receiveMoney(revUserId, roomId, token);
        if ("0000".equals(resultReceiveMoney.getResultCode())) {
            testResult = true;
        } else {
            testResult = false;
        }

        assertTrue(resultReceiveMoney.getResultCode() + "(" + resultReceiveMoney.getResultMsg() + ")", testResult);

        // -- 조회 테스트
        reqUserId = "000000001";
        resultGetMoney = testApi.getMoney(reqUserId, roomId, token);
        if ("0000".equals(resultGetMoney.getResultCode())) {
            testResult = true;
        } else {
            testResult = false;
        }

        assertTrue(resultGetMoney.getResultCode() + "(" + resultGetMoney.getResultMsg() + ")", testResult);

        // revUserId 유저가 받은 금액과 받은 총액 검사
        if (resultReceiveMoney.getAmt().equals(Long.toString(resultGetMoney.getReceiveAmt()))) {
            testResult = true;
        } else {
            testResult = false;
        }

        assertTrue(resultGetMoney.getReceiveAmt() + "", testResult);

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
