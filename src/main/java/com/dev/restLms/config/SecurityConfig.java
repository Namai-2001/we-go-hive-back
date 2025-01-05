package com.dev.restLms.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
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
                                        "/freeBulletinBoard", "/announcement", "/announcement/mainBanner",
                                        "/announcementPost/images/**",
                                        "/createCourse/images/{fileNo:.+}",
                                        "/modifyCourse/images/{fileNo:.+}",
                                        "/course/allTitles", "/serachSubject/images/{fileNo:.+}",
                                        "/subjectApproval/images/{fileNo:.+}",
                                        "/kakao/*", "/google/*", "/naver/*",
                                        "/getAllSubjectInfo",
                                        "/subjectInfoDetail/{offeredSubjectsId}",
                                        "/teacher/subject/images/{fileNo:.+}",
                                        "/teacher/video-management/images/{fileNo:.+}",
                                        "/teacher/video-management/delete-video/{offeredSubjectsId}/{videoId}",
                                        "/Videoplayer/videoList/{episodeId}/{offeredSubjectsId}",
                                        "/slp/images/{fileNo:.+}",
                                        "/sid/images/{fileNo:.+}",
                                        "/op/images/{fileNo:.+}",
                                        "/lecture/images/{fileNo:.+}",
                                        "/courseComplete/download/certificate/**",
                                        "/course/titles",
                                        "/course/images/{fileNo:.+}",
                                        "/course/searchDueCourse",
                                        "/course/searchDeadlineCourse",
                                        "/course/searchReceivingCourse",
                                        "/freeBulletinBoardPost/images/{fileNo:.+}",
                                        "/freeBulletinBoardPost/download/{fileNo}",
                                        "/announcementPost/download/{fileNo}")
                                .permitAll() // Swagger 관련 경로 허용
                                .anyRequest().authenticated())
                .httpBasic(basic -> {
                });
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Value("${MY_AMPLIFY_URL:http://localhost:3000}")
    private String corsUrl;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(
                Arrays.asList(corsUrl)); // React
                                                                     // 앱의
        // URL 허용
        configuration.setAllowedMethods(Arrays.asList("GET", "POST")); // 허용할 HTTP 메서드
        configuration.setAllowedHeaders(Arrays.asList("*")); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 자격 증명 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 설정 적용
        return source;
    }

    @Bean
    public HttpFirewall allowUrlWithDoubleSlash() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        firewall.setAllowUrlEncodedDoubleSlash(true); // `//` 허용
        return firewall;
    }
}