package com.kakao.pay;

import com.kakao.pay.db.dto.MoneyResultDto;
import com.kakao.pay.db.dto.ReceiveMoneyResultDto;
import com.kakao.pay.db.dto.SprinkleMoneyResultDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
public class TestApi {

    private final RestTemplate restTemplate;

    private String moneyApiUrl;

    private String sprinkleMoneyApiUrl;

    private String receiveMoneyUrl;

    public TestApi(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public SprinkleMoneyResultDto sprinkleMoney(String reqUserId, String roomId, String totalAmt, List<String> sprinkleUserList) {

        sprinkleMoneyApiUrl = "http://localhost:8080/api/sprinkleMoney";

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(sprinkleMoneyApiUrl)
                .queryParam("totalAmt", totalAmt)
                .queryParam("sprinkleUserList", sprinkleUserList);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.set("X-USER-ID", reqUserId);
        httpHeaders.set("X-ROOM-ID", roomId);

        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);
        return restTemplate.exchange(builder.toUriString(), HttpMethod.POST, httpEntity, SprinkleMoneyResultDto.class).getBody();
    }

    public ReceiveMoneyResultDto receiveMoney(String reqUserId, String roomId, String token) {

        receiveMoneyUrl = "http://localhost:8080/api/receiveMoney";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(receiveMoneyUrl)
                .queryParam("token", token);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.set("X-USER-ID", reqUserId);
        httpHeaders.set("X-ROOM-ID", roomId);

        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);
        return restTemplate.exchange(builder.toUriString(), HttpMethod.POST, httpEntity, ReceiveMoneyResultDto.class).getBody();
    }

    public MoneyResultDto getMoney(String reqUserId, String roomId, String token) {

        moneyApiUrl = "http://localhost:8080/api/Money";

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(moneyApiUrl)
                .queryParam("token", token);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        httpHeaders.set("X-USER-ID", reqUserId);
        httpHeaders.set("X-ROOM-ID", roomId);

        HttpEntity<?> httpEntity = new HttpEntity<>(httpHeaders);
        return restTemplate.exchange(builder.toUriString(), HttpMethod.GET, httpEntity, MoneyResultDto.class).getBody();
    }
}
