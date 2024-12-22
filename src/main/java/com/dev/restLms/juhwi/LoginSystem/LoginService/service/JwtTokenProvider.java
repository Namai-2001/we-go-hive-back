package com.dev.restLms.juhwi.LoginSystem.LoginService.service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {

    // 환경변수 가져오는데 시크릿 키
    String SECRET_KEY = "asasdasdasdasdasdasdasdasdasdasmySecretKeyForJwtGenerationShouldBeLongAndSecure!mySecretKeyForJwtGenerationShouldBeLongAndSecure!mySecretKeyForJwtGenerationShouldBeLongAndSecure!mySecretKeyForJwtGenerationShouldBeLongAndSecure!";
    Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    // 환경변수 가져오는데 토큰 만료 시간
    @Value("${app.jwtExpirationInMs}")
    private int jwtExpirationInMs = 86400000;

    // 토큰 생성
    public String generateToken(Authentication authentication) {
        // 유저 정보 가져오기
        String userPrincipal = (String) authentication.getPrincipal();
        // String userCredentials = (String) authentication.getCredentials();

        // 현재 시간
        Date now = new Date();

        // 만료 시간
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        // 토큰 생성
        return Jwts.builder()
                // 유저 이름을 서브젝트로 설정
                .setSubject(userPrincipal)
                // 발급 시간 설정

                .setIssuedAt(new Date())
                // 만료 시간 설정
                .setExpiration(expiryDate)
                // 시크릿 키로 서명
                .signWith(key, SignatureAlgorithm.HS512)
                // 토큰 생성
                .compact();
    }

    // 토큰에서 유저 이름 가져오기
    public String getUserIdFromJWT(String token) {
        // 토큰 검증
        if (validateToken(token)) {
            // 토큰 파싱
            Claims claims = Jwts.parserBuilder()
                    // 시크릿 키로 파싱
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            // 바디 가져오기
            // 서브젝트 반환
            return claims.getSubject();
        } else {
            log.info("유저 이름 가져오기 : 토큰이 유효하지 않습니다.");
            return "토큰이 유효하지 않습니다.";
        }
    }

    // 토큰 유효성 검사
    public boolean validateToken(String authToken) {
        try {
            // 토큰 파싱
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (MalformedJwtException | ExpiredJwtException | UnsupportedJwtException
                | IllegalArgumentException ex) {
            log.info("토큰이 유효하지 않습니다.");
        }
        return false;
    }
}