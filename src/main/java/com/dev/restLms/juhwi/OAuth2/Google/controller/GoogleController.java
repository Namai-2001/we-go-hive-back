package com.dev.restLms.juhwi.OAuth2.Google.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.dev.restLms.Auth.controller.AuthController;
import com.dev.restLms.Auth.dto.LoginRequest;
import com.dev.restLms.entity.LoginEmails;
import com.dev.restLms.entity.User;
import com.dev.restLms.juhwi.OAuth2.Google.repository.Google_LEe_Repository;
import com.dev.restLms.juhwi.OAuth2.Google.repository.Google_Ue_Repository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RestController
@Tag(name = "구글 컨트롤러", description = "구글글 OAuth 로그인")
@RequestMapping("/google")
public class GoogleController {
    @Value("${GOOGLE_CLIENT_ID:595841165127-58do495p0vm72kd1t74sobqakjb35gcr.apps.googleusercontent.com}")
    String GOOGLE_CLIENT_ID;
    @Value("${GOOGLE_CLIENT_SECRET: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}")
    String GOOGLE_CLIENT_SECRET;

    @GetMapping("/getToken")
    @Operation(summary = "구글 OAuth2 토큰 가져오기", description = "구글 OAuth 토큰을 발급받습니다.")
    public ResponseEntity<GoogleUserResponse> getToken(
            @Parameter(name = "code", description = "사용자 인증 인가 code") @RequestParam String code,
            @RequestParam String redirectUri) {

        WebClient webClient = WebClient.create("https://oauth2.googleapis.com");

        GoogleUserResponse response = webClient.post()
                .uri("/token")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(BodyInserters.fromFormData("client_id", GOOGLE_CLIENT_ID)
                        .with("client_secret", GOOGLE_CLIENT_SECRET)
                        .with("code", code)
                        .with("grant_type", "authorization_code")
                        .with("redirect_uri", redirectUri))
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class).map(body -> {
                            log.error("토큰 발급 중 오류가 발생했습니다. {}", body);
                            return null;
                        }))
                .bodyToMono(GoogleUserResponse.class)
                .block();

        log.info("토큰 발급: {}", response);
        return ResponseEntity.ok(response);
    }

    public record GoogleUserResponse(String access_token, String expires_in, String scope, String token_type,
            String id_token) {
    }

    // 원시 구현
    // 구글은 id_token으로 바로 가져오기 가능
    // 즉 parse 하는 부분으로 변경
    // @GetMapping("/getNidMe")
    // @Operation(summary = "구글 이메일 가져오기", description = "구글에서 email 정보 가져오기")
    // public ResponseEntity<String> getNidMe(
    // @Parameter(name = "AccessToken", description = "로그인 대상의 AccessToken")
    // @RequestParam GoogleUserResponse parseResponse) {
    // String res = new
    // String(Base64.getUrlDecoder().decode(parseResponse.id_token.split("\\.")[1]));
    // // byte 배열을 String으로 변환

    // // Jackson ObjectMapper를 사용하여 JSON 문자열을 Map으로 변환
    // ObjectMapper objectMapper = new ObjectMapper();
    // Map<String, Object> map;
    // try {
    // map = objectMapper.readValue(res, new TypeReference<Map<String, Object>>() {
    // });
    // } catch (JsonProcessingException e) {
    // log.error("JSON 처리 중 오류가 발생했습니다.", e);
    // return ResponseEntity.ok("JSON 처리 중 오류가 발생했습니다.");
    // }

    // // Map 출력
    // log.info("email 출력 : " + map.get("email"));
    // log.info("내 정보 조회: {}", res);
    // return ResponseEntity.ok(res);
    // }

    @GetMapping("/getNidMe")
    @Operation(summary = "구글 이메일 가져오기", description = "구글에서 email 정보 가져오기")
    public ResponseEntity<String> getNidMe(
            @Parameter(name = "AccessToken", description = "로그인 대상의 AccessToken") @RequestParam GoogleUserResponse parseResponse) {
        String res = new String(Base64.getUrlDecoder().decode(parseResponse.id_token.split("\\.")[1]));

        // Jackson ObjectMapper를 사용하여 JSON 문자열을 Map으로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map;
        try {
            map = objectMapper.readValue(res, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            log.error("JSON 처리 중 오류가 발생했습니다.", e);
            return ResponseEntity.ok("JSON 처리 중 오류가 발생했습니다.");
        }

        // 안전하게 email 값 처리
        String email = Optional.ofNullable(map.get("email"))
                .map(Object::toString) // email 값이 null이 아니면 문자열로 변환
                .orElse("이메일 없음"); // 값이 없으면 기본값을 반환

        log.info("email 출력 : " + email);
        log.info("내 정보 조회: {}", res);
        return ResponseEntity.ok(email); // email을 반환
    }

    // 버전 1
    // @GetMapping("/getNidMe")
    // @Operation(summary = "구글 이메일 가져오기", description = "구글에서 email 정보 가져오기")
    // public ResponseEntity<String> getNidMe(
    // @Parameter(name = "AccessToken", description = "로그인 대상의 AccessToken")
    // @RequestParam GoogleUserResponse parseResponse) {

    // // 1. JWT에서 헤더 추출 (kid 값)
    // String idToken = parseResponse.id_token;
    // String kid =
    // Jwts.parserBuilder().build().parseClaimsJws(idToken).getHeader().getKeyId();

    // // 2. 공개키 목록에서 해당 kid에 맞는 공개키 추출
    // String publicKey = getPublicKey(kid); // getPublicKey는 앞서 작성한 메서드

    // // 3. 공개키로 서명 검증
    // Claims claims = Jwts.parserBuilder()
    // .setSigningKey(publicKey.getBytes()) // 공개키로 서명 검증
    // .build()
    // .parseClaimsJws(idToken)
    // .getBody();

    // // 4. 이메일 정보 추출
    // String email = claims.get("email", String.class);

    // log.info("내 정보 조회: {}", email);
    // return ResponseEntity.ok(email);
    // }

    // 버전 2
    // @GetMapping("/getNidMe")
    // @Operation(summary = "구글 이메일 가져오기", description = "구글에서 email 정보 가져오기")
    // public ResponseEntity<String> getNidMe(
    // @Parameter(name = "AccessToken", description = "로그인 대상의 AccessToken")
    // @RequestParam GoogleUserResponse parseResponse) {
    // // 1. JWT에서 헤더 추출 (kid 값)
    // String idToken = parseResponse.id_token;
    // String kid =
    // Jwts.parserBuilder().build().parseClaimsJws(idToken).getHeader().getKeyId();
    // // 2. 공개키 목록에서 해당 kid에 맞는 공개키 추출
    // String publicKeyString = getPublicKey(kid); // getPublicKey는 앞서 작성한 메서드
    // // 3. 공개키 문자열을 RSAPublicKey로 변환 (base64로 디코딩 후 PublicKey 객체로 변환)
    // RSAPublicKey publicKey = (RSAPublicKey)
    // Keys.hmacShaKeyFor(Base64.getDecoder().decode(publicKeyString));
    // // 4. 공개키로 서명 검증
    // Claims claims = Jwts.parserBuilder()
    // .setSigningKey(publicKey) // 공개키로 서명 검증
    // .build()
    // .parseClaimsJws(idToken)
    // .getBody();
    // // 5. 이메일 정보 추출
    // String email = claims.get("email", String.class);
    // log.info("내 정보 조회: {}", email);
    // return ResponseEntity.ok(email);
    // }

    // 버전 3
    // @GetMapping("/getNidMe")
    // @Operation(summary = "구글 이메일 가져오기", description = "구글에서 email 정보 가져오기")
    // public ResponseEntity<String> getNidMe(
    // @Parameter(name = "AccessToken", description = "로그인 대상의 AccessToken")
    // @RequestParam GoogleUserResponse parseResponse) {
    // // 1. JWT에서 헤더 추출 (kid 값)
    // String idToken = parseResponse.id_token;
    // String kid =
    // Jwts.parserBuilder().build().parseClaimsJws(idToken).getHeader().getKeyId();
    // // 2. 공개키 목록에서 해당 kid에 맞는 공개키 추출
    // String publicKeyString = getPublicKey(kid); // getPublicKey는 앞서 작성한 메서드
    // log.info("키값: {}", publicKeyString);
    // // 3. 공개키 문자열을 RSAPublicKey로 변환 (Base64로 디코딩 후 KeyFactory로 변환)
    // try {
    // String publicKeyPEM = publicKeyString.replace("-----BEGIN PUBLIC KEY-----",
    // "")
    // .replace("-----END PUBLIC KEY-----", "").trim();
    // byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
    // KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    // RSAPublicKey publicKey = (RSAPublicKey) keyFactory.generatePublic(new
    // X509EncodedKeySpec(encoded));
    // // 4. 공개키로 서명 검증
    // Claims claims = Jwts.parserBuilder()
    // .setSigningKey(publicKey) // 공개키로 서명 검증
    // .build()
    // .parseClaimsJws(idToken)
    // .getBody();
    // // 5. 이메일 정보 추출
    // String email = claims.get("email", String.class);
    // log.info("내 정보 조회: {}", email);
    // return ResponseEntity.ok(email);
    // } catch (Exception e) {
    // log.error("공개키 처리 중 오류 발생: ", e);
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("공개키 검증
    // 오류");
    // }
    // }
    // public record GooglePublicKeyResponse(String kty, String use, String alg,
    // String e, String kid, String n) {
    // }
    // public String getPublicKey(String kid) {
    // // 공개키 목록을 가져와서 해당 kid에 맞는 공개키를 선택
    // WebClient webClient =
    // WebClient.create("https://www.googleapis.com/oauth2/v3/certs");
    // List<GooglePublicKeyResponse> keys = webClient.get()
    // .retrieve()
    // .bodyToMono(new ParameterizedTypeReference<List<GooglePublicKeyResponse>>() {
    // })
    // .block();
    // log.info("키 response" + keys.toString());
    // // kid와 일치하는 공개키 찾기
    // for (GooglePublicKeyResponse key : keys) {
    // if (key.kid().equals(kid)) {
    // log.info("키값: {}", key.n());
    // return key.n();
    // }
    // }
    // throw new IllegalArgumentException("유효한 공개키를 찾을 수 없습니다.");
    // }

    // 구글은
    @Autowired
    Google_LEe_Repository google_LEe_Repository;
    @Autowired
    Google_Ue_Repository google_Ue_Repository;
    @Autowired
    AuthController authController;

    @GetMapping("/login")
    @Operation(summary = "구글 로그인", description = "구글 로그인")
    public ResponseEntity<?> login(
            @Parameter(name = "email", description = "로그인 대상의 email") @RequestParam String email) {

        List<LoginEmails> sessionds = google_LEe_Repository.findByEmail(email);

        String sessionId = "";
        Optional<User> userSelect = null;
        for (LoginEmails loginEmails : sessionds) {
            sessionId = loginEmails.getSessionId();
            userSelect = google_Ue_Repository.findById(sessionId);
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

    // 구글은 바로 idToken으로 가져오고 끝, refreshToken 은 scope로
    // @GetMapping("/logout")
    // @Operation(summary = "네이버 연동 해제", description = "네이버 연동 해제")
    // public ResponseEntity<?> logout(@Parameter(description = "access_token")
    // @RequestParam String accessToken) {
    // String token = accessToken;
    // WebClient webClient = WebClient.create(NAVER_OAUTH_BASE_URL);
    // NaverNidMeResponse response = webClient.get()
    // .uri(NAVER_OAUTH_TOKEN + "?"
    // + "grant_type=delete"
    // + "&client_id=" + GOOGLE_CLIENT_ID
    // + "&client_secret=" + GOOGLE_CLIENT_SECRET
    // + "&access_token=" + token
    // + "&service_provider=NAVER")
    // .header("Content-Type", NAVER_HEADER_CONTENT_TYPE)
    // .header("Authorization", "Bearer " + token)
    // .retrieve()
    // .bodyToMono(NaverNidMeResponse.class) // JSON 응답을 DTO로 변환
    // .block(); // 동기 방식으로 결과 받기
    // log.info("로그아웃 반환: {}", response);
    // return ResponseEntity.ok(response);
    // }

    @GetMapping("/authorize")
    @Operation(summary = "프론트 로그인 검증 호출", description = "구글로 간편 로그인")
    public ResponseEntity<?> authorize(
            @Parameter(name = "code", description = "사용자의 인가 코드") @RequestParam String code,
            @Parameter(name = "redirectri", description = "redirect 대상 uri") @RequestParam String redirectUri) {

        // 토큰 가져오기
        ResponseEntity<GoogleUserResponse> getTokenRes = getToken(code, redirectUri);
        GoogleUserResponse tokenBody = getTokenRes.getBody();
        if (tokenBody == null) {
            return ResponseEntity.status(500).body("토큰 발급 중 오류가 발생했습니다.");
        }

        // 내 정보 조회하기
        ResponseEntity<String> getNidMeRes = getNidMe(tokenBody);
        if (getNidMeRes.getBody() == null) {
            return ResponseEntity.status(500).body("API /v1/nid/me 정보 조회 중 오류가 발생했습니다.");
        }

        // 로그인 내용 가져오기
        ResponseEntity<?> loginRes = login(getNidMeRes.getBody());
        if (loginRes.getBody() == null) {
            return ResponseEntity.status(500).body("로그인 중 오류가 발생했습니다.");
        }

        // 결과
        ResponseEntity<?> response = loginRes;
        return response;
    }
}
