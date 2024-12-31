package com.dev.restLms.juhwi.OAuth2.Kakao.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.reactive.function.client.WebClient;

// @RestController
// @RequestMapping("/Oauth2/kakao")
// public class KakaoContoller {

//    // 여기부터 만드는 거
//    @GetMapping("/validate")
//    public String validate(@RequestParam String accessToken) {

//       // 카카오에 요청 보내서 email 값 가져오기
//       WebClient webClient = WebClient.create("https://kapi.kakao.com/v2/user/me");
//       String response = webClient.get()
//             .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
//             .header("Authorization", "Bearer " + accessToken).retrieve().bodyToMono(String.class).block();

//       return null;
//    }
// }
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.dev.restLms.Auth.controller.AuthController;
import com.dev.restLms.Auth.dto.LoginRequest;
import com.dev.restLms.entity.LoginEmails;
import com.dev.restLms.entity.User;
import com.dev.restLms.juhwi.OAuth2.Kakao.dto.KakaoTokenRequestDTO;
import com.dev.restLms.juhwi.OAuth2.Kakao.dto.KakaoTokenResponseDTO;
import com.dev.restLms.juhwi.OAuth2.Kakao.dto.KakaoUserResponse;
import com.dev.restLms.juhwi.OAuth2.Kakao.repository.Kakao_LEe_Repository;
import com.dev.restLms.juhwi.OAuth2.Kakao.repository.Kakao_Ue_Repository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Tag(name = "카카오 컨트롤러", description = "카카오 로그인")
@RequestMapping("/kakao")
public class KakaoController {

    @Autowired
    Kakao_LEe_Repository kakao_LEe_Repository;

    @Autowired
    AuthController authController;

    @Autowired
    Kakao_Ue_Repository kakao_Ue_Repository;

    @SuppressWarnings("null")
    // 엑세스 토큰을 통해 사용자의 이메일 값을 가져오는 기능
    @GetMapping("/validate")
    public ResponseEntity<?> validate(@Parameter(name = "Access Token 으로 이메일 값 가져오기", description = "이메일 값 반환") @RequestParam String accessToken) {
        // WebClient 생성 및 요청
        WebClient webClient = WebClient.create("https://kapi.kakao.com");

        KakaoUserResponse response = webClient.get()
                .uri("/v2/user/me")
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KakaoUserResponse.class) // JSON 응답을 DTO로 변환
                .block(); // 동기 방식으로 결과 받기

        // 이메일 추출
        String email = "";
        if (response != null && response.getKakao_account() != null) {
            email = response.getKakao_account().getEmail();
        } else {
            return ResponseEntity.ok("등록된 이메일이 없습니다.");
        }
        List<LoginEmails> sessionds = kakao_LEe_Repository.findByEmail(email);

        String sessionId = "";
        Optional<User> userSelect = null;
        for (LoginEmails loginEmails : sessionds) {
            sessionId = loginEmails.getSessionId();
            userSelect = kakao_Ue_Repository.findById(sessionId);
            if (userSelect.isPresent()) {
                break;
            }
        }
        if(userSelect == null) {
            return ResponseEntity.ok("등록된 이메일이 없습니다.");
        }
        User user = userSelect.get();
        LoginRequest req = LoginRequest.builder().userId(user.getUserId()).userPw(user.getUserPw()).build();
        ResponseEntity<?> responseEntity = authController.authenticateUser(req);
        return responseEntity;
    }

    @GetMapping("/authorize")
    @Operation(summary = "카카오 OAuth2 로그인", description = "카카오 서버로부터 인증 코드를 받아 사용자의 이메일을 가져오고 토큰을 반환합니다.")
    public ResponseEntity<?> authorize(@RequestParam String code, @RequestParam String redirectUri) {

        WebClient webClient = WebClient.create("https://kauth.kakao.com");

        KakaoTokenRequestDTO request = KakaoTokenRequestDTO.builder()
                .grant_type("authorization_code")
                .client_id("ed5a183c1e62742faca95ebaa05bc039")
                .code(code)
                .redirect_uri(redirectUri).build();

        KakaoTokenResponseDTO response = webClient.post()
                .uri("/oauth/token")
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .body(BodyInserters.fromFormData("grant_type", request.getGrant_type())
                        .with("client_id", request.getClient_id())
                        .with("code", request.getCode())
                        .with("redirect_uri", request.getRedirect_uri()))
                .retrieve()
                .bodyToMono(KakaoTokenResponseDTO.class) // JSON 응답을 DTO로 변환
                .block(); // 동기 방식으로 결과 받기

        // 바로 연결 해제
        ResponseEntity<?> loginInfo = validate(response.getAccess_token());
        unLink(response.getAccess_token());
        return loginInfo;

    }
    // 카카오 앱과 연결 해제
    @GetMapping("/unlink")
    @Operation(summary = "카카오 OAuth2 연결 해제", description = "카카오 서버와의 연결을 해제합니다.")
    public void unLink(@Parameter(name = "AccessToken", description = "연결 해제 대상 사용자의 AccessToken") @RequestParam String accessToken) {
        WebClient webClient = WebClient.create("https://kapi.kakao.com");
        KakaoUserResponse response = webClient.post()
                .uri("/v1/user/unlink")
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(KakaoUserResponse.class) // JSON 응답을 DTO로 변환
                .block(); // 동기 방식으로 결과 받기
        log.info("response: {}", response);
    }


    // 원시 구현
    /*
     * 1. code 값 파싱
     * 2. 파싱 code 담아서 요청
     * 3. 요청 request와 response 담아서 반환
     */
    // @GetMapping("/authorize")
    // public ResponseEntity<?> authorize(@RequestParam String code) {

    // WebClient webClient = WebClient.create("https://kauth.kakao.com");

    // KakaoTokenRequestDTO request = KakaoTokenRequestDTO.builder()
    // .grant_type("authorization_code")
    // .client_id("ed5a183c1e62742faca95ebaa05bc039")
    // .code(code)
    // .redirect_uri("http://localhost:8080/authorize").build();
    // // return ResponseEntity.ok(request);

    // KakaoTokenResponseDTO response = webClient.post()
    // .uri("/oauth/token")
    // .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
    // .body(BodyInserters.fromFormData("grant_type", request.getGrant_type())
    // .with("client_id", request.getClient_id())
    // .with("code", request.getCode())
    // .with("redirect_uri", request.getRedirect_uri()))
    // .retrieve()
    // .bodyToMono(KakaoTokenResponseDTO.class) // JSON 응답을 DTO로 변환
    // .block(); // 동기 방식으로 결과 받기

    // record LoginEmails(KakaoTokenResponseDTO response, KakaoTokenRequestDTO
    // request){}
    // LoginEmails loginEmails = new LoginEmails(response, request);
    // return ResponseEntity.ok(loginEmails);
    // }

}