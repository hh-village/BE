package com.sparta.village.domain.naver.service;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;

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

    public String reverseGeocode(String coords) {
        //Http 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-NCP-APIGW-API-KEY-ID", clientId);
        headers.set("X-NCP-APIGW-API-KEY", clientSecret);
        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        //네이버 지도 API 호출 URL 생성
        String url = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/v2/gc?coords=" + coords + "&output=json&orders=roadaddr";
        //Rest Template를 사용해 네이버 지도 API에 요청을 보내고 응답을 받음
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        // JSON 객체로 변환
        JsonReader jsonReader = Json.createReader(new StringReader(response.getBody()));
        JsonObject jsonResponse = jsonReader.readObject();

        // results 배열에서 첫 번째 결과를 가져옴
        JsonArray resultsArray = jsonResponse.getJsonArray("results");
        JsonObject firstResult = resultsArray.getJsonObject(0);

        // 필요한 정보 추출
        JsonObject region = firstResult.getJsonObject("region");
        JsonObject area1 = region.getJsonObject("area1");
        String alias = area1.getString("alias");
        JsonObject area2 = region.getJsonObject("area2");
        String middleName = area2.getString("name");
        JsonObject land = firstResult.getJsonObject("land");
        String name = land.getString("name");
        String number1 = land.getString("number1");
        JsonObject addition0 = land.getJsonObject("addition0");
        String additionValue = addition0.getString("value");

        // 추출한 정보를 반환 형식에 맞게 조합
        String extractedInfo = alias + " " + middleName +" " + name + " " + number1 + " " + additionValue;

        // 추출한 정보 반환
        return extractedInfo;

        //응답을 반환
//        return response.getBody();
    }
}
