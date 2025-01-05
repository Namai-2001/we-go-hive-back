package com.dev.restLms.juhwi.OAuth2.Naver.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.dev.restLms.Auth.controller.AuthController;
import com.dev.restLms.Auth.dto.LoginRequest;
import com.dev.restLms.entity.LoginEmails;
import com.dev.restLms.entity.User;
import com.dev.restLms.juhwi.OAuth2.Naver.repository.Naver_LEe_Repository;
import com.dev.restLms.juhwi.OAuth2.Naver.repository.Naver_Ue_Repository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RestController
@Tag(name = "네이버 컨트롤러", description = "네이버 OAuth 로그인")
@RequestMapping("/naver")
public class NaverController {

    public final String NAVER_OAUTH_BASE_URL = "https://nid.naver.com";
    public final String NAVER_OAUTH_TOKEN = "/oauth2.0/token";
    public final String NAVER_HEADER_CONTENT_TYPE = "text/html;charset=utf-8";

    @Value("${NAVER_CLIENT_ID:WBKWxAxoDOSiEal4xwod}")
    String NAVER_CLIENT_ID;
    @Value("${NAVER_CLIENT_SECRET:rli1o7lzhV}")
    String NAVER_CLIENT_SECRET;

    // @GetMapping("/getToken")
    // @Operation(summary = "네이버 OAuth2 토큰 가져오기", description = "1. 네이버 OAuth 토큰
    // 가져오기")
    public ResponseEntity<NaverUserResponse> getToken(
            @Parameter(name = "code", description = "사용자 인증 인가 code") @RequestParam String code,
            @RequestParam String redirectUri) {
        WebClient webClient = WebClient.create(NAVER_OAUTH_BASE_URL);
        NaverUserResponse response = webClient.post()
                .uri(NAVER_OAUTH_TOKEN + "?" +
                        "grant_type=" + "authorization_code" +
                        "&client_id=" + NAVER_CLIENT_ID +
                        "&client_secret=" + NAVER_CLIENT_SECRET +
                        "&redirect_uri=" + redirectUri +
                        "&code=" + code +
                        "&state=" + "FALSE")
                .header("Content-Type", NAVER_HEADER_CONTENT_TYPE)
                .retrieve()
                .bodyToMono(NaverUserResponse.class) // JSON 응답을 DTO로 변환
                .block(); // 동기 방식으로 결과 받기
        log.info("토큰 발급: {}", response);
        return ResponseEntity.ok(response);
    }

    public record NaverUserResponse(String access_token, String refresh_token, String token_type, String expires_in,
            String error, String error_description) {
    }

    public final String NAVER_OPEN_API_URL = "https://openapi.naver.com";
    public final String NAVER_NIDME_API_URI = "/v1/nid/me";

    // @GetMapping("/getNidMe")
    // @Operation(summary = "네이버 내 정보 가져오기", description = "네이버에서 AccessToken 으로 정보
    // 가져오기")
    public ResponseEntity<?> getNidMe(
            @Parameter(name = "AccessToken", description = "로그인 대상의 AccessToken") @RequestParam String accessToken) {
        String token = accessToken;
        WebClient webClient = WebClient.create(NAVER_OPEN_API_URL);
        NaverNidMeResponse response = webClient.post()
                .uri(NAVER_NIDME_API_URI)
                .header("Content-Type", NAVER_HEADER_CONTENT_TYPE)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(NaverNidMeResponse.class) // JSON 응답을 DTO로 변환
                .block(); // 동기 방식으로 결과 받기
        log.info("정보 가져오기: {}", response);
        return ResponseEntity.ok(response);
    }

    public record NaverNidMeResponse(String resultcode, String message, V1NidMeResponse response) {
    }

    public record V1NidMeResponse(String id, String email, String mobile, String name) {
    }

    // 로그인 구현
    @Autowired
    Naver_Ue_Repository naver_Ue_Repository;
    @Autowired
    Naver_LEe_Repository naver_LEe_Repository;
    @Autowired
    AuthController authController;

    // @GetMapping("/login")
    // @Operation(summary = "네이버 로그인", description = "네이버 로그인")
    public ResponseEntity<?> login(
            @Parameter(name = "AccessToken", description = "로그인 대상의 AccessToken") @RequestParam NaverNidMeResponse response) {
        String email = "";
        if (response != null && response.response.email != null) {
            email = response.response.email;
        } else {
            return ResponseEntity.ok("등록된 이메일이 없습니다.");
        }
        List<LoginEmails> sessionds = naver_LEe_Repository.findByEmail(email);

        String sessionId = "";
        Optional<User> userSelect = null;
        for (LoginEmails loginEmails : sessionds) {
            sessionId = loginEmails.getSessionId();
            userSelect = naver_Ue_Repository.findById(sessionId);
            if (userSelect.isPresent()) {
                break;
            }
        }
        if (userSelect == null) {
            return ResponseEntity.ok("등록된 이메일이 없습니다.");
        }
        User user = userSelect.get();
        LoginRequest req = LoginRequest.builder().userId(user.getUserId()).userPw(user.getUserPw()).build();
        ResponseEntity<?> responseEntity = authController.authenticateUser(req);
        return responseEntity;
    }

    // @GetMapping("/logout")
    // @Operation(summary = "네이버 연동 해제", description = "네이버 연동 해제")
    public ResponseEntity<?> logout(@Parameter(description = "access_token") @RequestParam String accessToken) {
        String token = accessToken;
        WebClient webClient = WebClient.create(NAVER_OAUTH_BASE_URL);
        NaverNidMeResponse response = webClient.get()
                .uri(NAVER_OAUTH_TOKEN + "?"
                        + "grant_type=delete"
                        + "&client_id=" + NAVER_CLIENT_ID
                        + "&client_secret=" + NAVER_CLIENT_SECRET
                        + "&access_token=" + token
                        + "&service_provider=NAVER")
                .header("Content-Type", NAVER_HEADER_CONTENT_TYPE)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(NaverNidMeResponse.class) // JSON 응답을 DTO로 변환
                .block(); // 동기 방식으로 결과 받기
        log.info("로그아웃 반환: {}", response);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/authorize")
    @Operation(summary = "프론트 로그인 검증 호출", description = "네이버로 간편 로그인")
    public ResponseEntity<?> authorize(
            @Parameter(name = "code", description = "연결 해제 대상 사용자의 인가 코드") @RequestParam String code,
            @Parameter(name = "redirectri", description = "redirect 대상 uri") @RequestParam String redirectUri) {

        // 토큰 가져오기
        ResponseEntity<NaverUserResponse> getTokenRes = getToken(code, redirectUri);
        NaverUserResponse tokenBody = getTokenRes.getBody();
        if (tokenBody == null) {
            return ResponseEntity.status(500).body("토큰 발급 중 오류가 발생했습니다.");
        }

        // 내 정보 조회하기
        ResponseEntity<?> getNidMeRes = getNidMe(tokenBody.access_token);
        if (getNidMeRes.getBody() == null) {
            return ResponseEntity.status(500).body("API /v1/nid/me 정보 조회 중 오류가 발생했습니다.");

        }

        // 로그인 내용 가져오기
        ResponseEntity<?> loginRes = login((NaverNidMeResponse) getNidMeRes.getBody());
        if (loginRes.getBody() == null) {
            return ResponseEntity.status(500).body("로그인 중 오류가 발생했습니다.");
        }

        // 연결 끊기
        // ResponseEntity<?> logoutRes = logout(tokenBody.access_token);
        logout(tokenBody.access_token);

        // 결과
        ResponseEntity<?> response = loginRes;
        return response;
    }
}
