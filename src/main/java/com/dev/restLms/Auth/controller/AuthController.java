package com.dev.restLms.Auth.controller;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.entity.User;
import com.dev.restLms.entity.UserOwnPermissionGroup;
import com.dev.restLms.Auth.dto.LoginRequest;
import com.dev.restLms.Auth.repository.LoginUserOwnPermissionGroupRepository;
import com.dev.restLms.Auth.repository.LoginUserOwnPermissionGroupRepository2;
import com.dev.restLms.Auth.repository.LoginUserPermissionRepository;
import com.dev.restLms.Auth.repository.LoginUserRepository;
import com.dev.restLms.Auth.service.JwtTokenProvider;
import com.dev.restLms.Auth.projection.permissionProjection;
import com.dev.restLms.Auth.projection.permissionUuidProjection;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "AuthController", description = "로그인 관련 API")
@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private LoginUserRepository loginUserRepository;

    @Autowired
    LoginUserOwnPermissionGroupRepository loginUserOwnPermissionGroupRepository;

    @Autowired
    LoginUserOwnPermissionGroupRepository2 loginUserOwnPermissionGroupRepository2;

    @Autowired
    LoginUserPermissionRepository loginUserPermissionRepository;

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "로그인을 수행하는 엔드포인트 입니다.")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Optional<User> authId = loginUserRepository.findByUserId(loginRequest.getUserId());
        Optional<User> loginUser = loginUserRepository.findByUserIdAndUserPw(loginRequest.getUserId(),
                loginRequest.getUserPw());
                
        if (!authId.isPresent()) {
            return ResponseEntity.badRequest().body("가입된 회원이 아닙니다.");
        }
        if (!loginUser.isPresent()) {
            return ResponseEntity.badRequest().body("비밀번호가 틀렸습니다.");
        }
        
        // 반환되는 유저의 정보를 담는 HashMap
        Optional<permissionUuidProjection> permissionGroupsSelect = loginUserOwnPermissionGroupRepository2.findBySessionId(loginUser.get().getSessionId());
        if(permissionGroupsSelect.isPresent()){
            String getPermissionUuid = permissionGroupsSelect.get().getPermissionGroupUuid2();
            Optional<permissionProjection> permissionName = loginUserPermissionRepository.findByPermissionGroupUuid(getPermissionUuid);
            User authorizedUser = loginUser.get();
        
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    // 실제 검증은 세션아이디로 이루어져서 설정
                    loginUser.get().getSessionId(),
                    loginUser.get().getUserPw());
    
            // 인증정보 등록
            SecurityContextHolder.getContext().setAuthentication(authentication); // 필요 없어 보임
            String jwt = tokenProvider.generateToken(authentication); // 실제 JWT 발급하는 부분, 관리부분 결여
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("nickName", authorizedUser.getNickname());
            userInfo.put("permissionName", permissionName.get().getPermissionName());
            userInfo.put("token", jwt);

            // 로그인이 완료된 사용자를 반환
            return ResponseEntity.ok(userInfo);
        }
        return ResponseEntity.badRequest().body("권한이 없습니다.");
    }

    @GetMapping("/security/getcontext")
    @Operation(summary = "인증 토큰 사용 메뉴얼", description = "사용자 관점의 인증 세션 조회하는 엔드포인트 입니다.")
    public ResponseEntity<UserRecord> getContext() {
        // 보안 컨텍스트로부터 데이터 가져오기
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();

        // 불변 객체가 아니여서 조회가 불가능, 읽기 전용만 가능
        // 즉 읽기 쓰기가 가능한 String 객체에는 할당이 불가능, 또한 로그의 해석도 난해하여 아무 값이 노출되지 않음
        // log.info("선언 없이 사용 : 토큰 저장 세션 아이디 ", auth.getPrincipal());
        // log.info("선언 없이 사용 : 토큰 저장 비밀번호 " , auth.getCredentials());
        // for (Object object : auth.getAuthorities().toArray()) {
        // log.info("선언 없이 사용 : 토큰 저장 정보로 조회한 권한 코드 " , object);
        // }

        // 변수 매핑 주의점
        // 변수로 캐스팅하는 과정에 null 값이 들어가면 에러가 발생할 수 있음 => null 대신 기본 값으로 처리했음
        // final로 불변객체성을 부여하여 조회 가능하도록 변경

        final String userSessionId = auth.getPrincipal().toString(); // 유저 세션아이디 보안 컨텍스트에서 가져오기
        final String userPw = auth.getCredentials().toString(); // 유저 비밀번호 보안 컨텍스트에서 가져오기
        final Object[] grantStrings = auth.getAuthorities().toArray(); // 권한들 보안 컨텍스트에서 가져오기

        // 출력
        log.info("토큰 저장 세션 아이디 >> " + userSessionId);
        log.info("토큰 저장 비밀번호 >> " + userPw);
        for (Object object : grantStrings) {
            log.info("토큰 저장 정보로 조회한 권한 코드 >> " + object);
        }

        // User record 생성
        UserRecord user = new UserRecord(userSessionId, userPw, grantStrings);

        return ResponseEntity.ok(user);
    }



    @GetMapping("/security/getcontext/test")
    @Operation(summary = "인증 토큰 검증 엔드포인트", description = "JWT를 해석해서 이름만 반환하는 엔드포인트 입니다. 만료는 확인하지 않습니다.")
    public ResponseEntity<?> getContextTest(
            @Parameter(description = "검증할 JWT 헤더", required = true) @RequestHeader(required = true) String token) {

        if (!tokenProvider.validateToken(token)) {
            return ResponseEntity.badRequest().body("토큰이 유효하지 않습니다.");
        }

        final String userSessionId = tokenProvider.getUserIdFromJWT(token);

        // 변경 로직
        Optional<List<UserOwnPermissionGroup>> permissionGroupsSelect = loginUserOwnPermissionGroupRepository
                .findBySessionId(userSessionId);

        List<SimpleGrantedAuthority> grantStrings;
        if (permissionGroupsSelect.isPresent()) {
            List<UserOwnPermissionGroup> permissionGroups = permissionGroupsSelect.get();
            grantStrings = permissionGroups.stream()
                    .map(pg -> new SimpleGrantedAuthority(pg.getPermissionGroupUuid2()))
                    .collect(Collectors.toList());
        }
        // 권한 그룹이 존재하지 않는 경우
        else {
            grantStrings = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        }
        UserRecord user = new UserRecord(userSessionId, "비밀번호는 현재 저장하지 않습니다.", grantStrings.toArray());

        return ResponseEntity.ok(user);
    }

    // User record 정의
    public record UserRecord(String userSessionId, String userPw, Object[] grantStrings) {
    }
}
