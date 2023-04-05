package com.sparta.village.domain.naver.service;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class NaverMapService {
    //네이버 지도 API 클라이언트 ID
    @Value("${naver.map.client-id}")
    private String clientId;

    //네이버 지도 API 클라이언트 시크릿
    @Value("${naver.map.client-secret}")
    private String clientSecret;

    //REST 통신을 위한 Rest Template 객체
    private final RestTemplate restTemplate;

    //NaverMapService 생성자에서 Rest Template 객체를 초기화
    public NaverMapService() {
        this.restTemplate = new RestTemplate();
    }

    public String geocode(String address) {
        //Http 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-NCP-APIGW-API-KEY-ID", clientId);
        headers.set("X-NCP-APIGW-API-KEY", clientSecret);
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        //네이버 지도 API 호출 URL 생성
        String url = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query="+address;
        //Rest Template를 사용해 네이버 지도 API에 요청을 보내고 응답을 받음
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        //응답을 반환
        return response.getBody();
    }
}
