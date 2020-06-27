package com.kakao.pay.controller;

import com.kakao.pay.db.dto.MoneyResultDto;
import com.kakao.pay.db.dto.ReceiveMoneyResultDto;
import com.kakao.pay.db.dto.SprinkleMoneyResultDto;
import com.kakao.pay.db.service.SprinkleMoneyService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SprinkleController {

    @Autowired
    private SprinkleMoneyService sprinkleMoneyService;

    @RequestMapping(value = "/api/sprinkleMoney", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public SprinkleMoneyResultDto sprinkleMoney(@RequestHeader(value="X-USER-ID") String reqUserId, @RequestHeader(value="X-ROOM-ID") String roomId, @RequestParam("totalAmt") String totalAmt, @RequestParam("sprinkleUserList") List<String> sprinkleUserList) {
        return sprinkleMoneyService.sprinkleMoney(reqUserId, roomId, totalAmt, sprinkleUserList);
    }

    @RequestMapping(value = "/api/receiveMoney", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public ReceiveMoneyResultDto receiveMoney(@RequestHeader(value="X-USER-ID") String reqUserId, @RequestHeader(value="X-ROOM-ID") String roomId, @RequestParam("token") String token) {
        return sprinkleMoneyService.receiveMoney(reqUserId,roomId,token);
    }

    @RequestMapping(value = "/api/Money", method = RequestMethod.GET)
    @ResponseStatus(value = HttpStatus.OK)
    public MoneyResultDto getMoney(@RequestHeader(value="X-USER-ID") String reqUserId, @RequestHeader(value="X-ROOM-ID") String roomId, @RequestParam("token") String token) {
        return sprinkleMoneyService.getMoney(reqUserId,roomId,token);
    }

    @RequestMapping(value = "/api/testDate", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public boolean test(@RequestHeader(value="X-USER-ID") String reqUserId, @RequestHeader(value="X-ROOM-ID") String roomId, @RequestParam("token") String token) {
         return sprinkleMoneyService.testDate(reqUserId,roomId,token);
    }

    @RequestMapping(value = "/api/testTime", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public boolean testTime(@RequestHeader(value="X-USER-ID") String reqUserId, @RequestHeader(value="X-ROOM-ID") String roomId, @RequestParam("token") String token) {
        return sprinkleMoneyService.testTime(reqUserId,roomId,token);
    }
}
