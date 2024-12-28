package com.dev.restLms.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.dev.restLms.Auth.filter.JwtAuthenticationFilter;


// 시큐리티 설정 없애기

/* 트러블 슈팅
 * 1. 아이디 비밀번호를 환경설정에 넣었음
 */
@Configuration
public class SecurityConfig {
    @Autowired
    JwtAuthenticationFilter jwtAuthenticationFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 설정 추가
                .authorizeHttpRequests(
                        auth -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui/index.html",
                                            "/api/login", "/api/security/getcontext", "/Home/RandSubjectVid", 
                                            "/freeBulletinBoard", "/announcement", "/announcement/mainBanner", "/announcementPost/images/**", "/getAllSubjectInfo", "/subjectInfoDetail/{offeredSubjectsId}", "/teacher/subject/images/{fileNo:.+}", "/teacher/video-management/images/{fileNo:.+}")
                        .permitAll() // Swagger 관련 경로 허용
                        .anyRequest().authenticated()
                )
                .httpBasic(basic -> {
                });
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://10.10.24.67:3000")); // React 앱의
                                                                                                            // URL 허용
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE")); // 허용할 HTTP 메서드
        configuration.setAllowedHeaders(Arrays.asList("*")); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 자격 증명 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 설정 적용
        return source;
    }

}