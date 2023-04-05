package com.sparta.village.domain.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.village.domain.user.dto.UserInfoDto;
import com.sparta.village.domain.user.entity.User;
import com.sparta.village.domain.user.entity.UserRoleEnum;
import com.sparta.village.domain.user.repository.UserRepository;
import com.sparta.village.global.exception.ResponseMessage;
import com.sparta.village.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoUserService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Value("${kakao.clientId}")
    String client_id;

    @Value("${redirect_url}")
    String redirect_url;



    public ResponseEntity<ResponseMessage> kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getToken(code);

        // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
        UserInfoDto userInfo = getKakaoUserInfo(accessToken);

        // 3. 필요시에 회원가입
        String nickname = registerKakaoUserIfNeeded(userInfo);

        // 4. JWT 토큰 반환
        String createToken =  jwtUtil.createToken(nickname, userInfo.getKakaoId());
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, createToken);

        return ResponseMessage.SuccessResponse("로그인 성공되었습니다.", "");
    }
    // 1. "인가 코드"로 "액세스 토큰" 요청
    private String getToken(String code) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", client_id);
        body.add("redirect_uri", redirect_url);
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        return jsonNode.get("access_token").asText();
    }
    // 2. 토큰으로 카카오 API 호출 : "액세스 토큰"으로 "카카오 사용자 정보" 가져오기
    private UserInfoDto getKakaoUserInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoUserInfoRequest,
                String.class
        );
        // 응답을 받는 코드
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long id = jsonNode.get("id").asLong();


        log.info("카카오 사용자 정보: " + id);
        return new UserInfoDto(id);
    }
    // 3. 필요시에 회원가입
    private String registerKakaoUserIfNeeded(UserInfoDto kakaoUserInfo) {
        String nickname = "";
        if(!userRepository.existsByKakaoId(kakaoUserInfo.getKakaoId())){
            nickname = UUID.randomUUID().toString();
            userRepository.save(new User(kakaoUserInfo.getKakaoId(), nickname,"", UserRoleEnum.USER));
        } else {
            nickname = userRepository.findByKakaoId(kakaoUserInfo.getKakaoId()).getNickname();
        }
        return nickname;
    }

    public String getNicknameByUserId(String userId) {
        User user = userRepository.findById(Long.parseLong(userId)).orElse(null);
        if (user == null) {
            return null;
        }
        return String.valueOf(user.getNickname());
    }
}