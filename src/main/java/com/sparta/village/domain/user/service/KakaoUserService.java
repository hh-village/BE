package com.sparta.village.domain.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.village.domain.user.dto.UserInfoDto;
import com.sparta.village.domain.user.entity.User;
import com.sparta.village.domain.user.entity.UserRoleEnum;
import com.sparta.village.domain.user.repository.UserRepository;
import com.sparta.village.global.exception.CustomException;
import com.sparta.village.global.exception.ErrorCode;
import com.sparta.village.global.exception.ResponseMessage;
import com.sparta.village.global.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoUserService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Value("${kakao.clientId}")
    String clientId;

    @Value("${redirect_url}")
    String redirectUrl;

    @Transactional
    public ResponseEntity<ResponseMessage> kakaoLogin(String code, HttpServletResponse response) throws JsonProcessingException {
        String accessToken = requestAccessToken(code);
        UserInfoDto userInfo = fetchKakaoUserInfo(accessToken);
        String nickname = registerOrUpdateKakaoUser(userInfo);

        String jwtToken = jwtUtil.createToken(nickname, userInfo.getKakaoId());
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtToken);

        return ResponseMessage.SuccessResponse("로그인 성공되었습니다.", "");
    }

    private String requestAccessToken(String code) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<?> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        String url = UriComponentsBuilder.fromHttpUrl("https://kauth.kakao.com/oauth/token")
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUrl)
                .queryParam("code", code)
                .toUriString();

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        return jsonNode.get("access_token").asText();
    }

    private UserInfoDto fetchKakaoUserInfo(String accessToken) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<?> request = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://kapi.kakao.com/v2/user/me";

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.getBody());
        Long id = jsonNode.get("id").asLong();

        log.info("카카오 사용자 정보: " + id);
        return new UserInfoDto(id);
    }

    private String registerOrUpdateKakaoUser(UserInfoDto kakaoUserInfo) {
        User user = userRepository.findByKakaoId(kakaoUserInfo.getKakaoId()).orElse(null);
        String nickname = "";
        if (user == null) {
            nickname = UUID.randomUUID().toString().substring(0, 8);
            userRepository.save(new User(kakaoUserInfo.getKakaoId(), nickname, "https://s3-village-image.s3.ap-northeast-2.amazonaws.com/profile1.png", UserRoleEnum.USER));
        } else {
            nickname = user.getNickname();
        }
        return nickname;
    }

//    @Transactional
//    public ResponseEntity<ResponseMessage> testLogin(String nickname, HttpServletResponse response) {
//        User user = userRepository.findByNickname(nickname).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
//        String jwtToken = jwtUtil.createToken(nickname, user.getKakaoId());
//        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, jwtToken);
//
//        return ResponseMessage.SuccessResponse("로그인 성공되었습니다.", user.getNickname());
//    }
}