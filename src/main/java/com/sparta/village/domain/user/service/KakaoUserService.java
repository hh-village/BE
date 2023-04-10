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
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;
import java.util.Random;
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
    // 3. 필요시에 회원가입
    private String registerOrUpdateKakaoUser(UserInfoDto kakaoUserInfo) {
        String nickname = "";
        if (!userRepository.existsByKakaoId(kakaoUserInfo.getKakaoId())) {
            // 8-10자리의 랜덤 닉네임을 생성하여 사용자 정보에 추가하고, 데이터베이스에 저장합니다.
            nickname = UUID.randomUUID().toString().substring(0, 8);
            userRepository.save(new User(kakaoUserInfo.getKakaoId(), nickname, "", UserRoleEnum.USER));
        } else {
            // 카카오 ID에 해당하는 사용자 정보를 데이터베이스에서 검색하여 닉네임을 가져옵니다.
            nickname = userRepository.findByKakaoId(kakaoUserInfo.getKakaoId()).getNickname();
        }
        // 등록 또는 업데이트된 사용자의 닉네임을 반환합니다.
        return nickname;
    }
    public User getUserByUserId(String userId) {
        return userRepository.findById(Long.parseLong(userId)).orElse(null);
    }

}