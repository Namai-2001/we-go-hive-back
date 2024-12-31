package com.dev.restLms.juhwi.PermissionManagementService.controller;

import org.springframework.web.bind.annotation.*;

import com.dev.restLms.entity.PermissionGroup;
import com.dev.restLms.entity.User;
import com.dev.restLms.entity.UserOwnPermissionGroup;
import com.dev.restLms.juhwi.PermissionManagementService.projection.PM_getUserWithPermission_PGe_Projection;
import com.dev.restLms.juhwi.PermissionManagementService.projection.PM_getUserWithPermission_UOPGe_projection;
import com.dev.restLms.juhwi.PermissionManagementService.projection.PM_setPermission_UOPGe_Projection;
import com.dev.restLms.juhwi.PermissionManagementService.repository.PM_PGe_Repository;
import com.dev.restLms.juhwi.PermissionManagementService.repository.PM_getUserWithPermission_PGe_Repository;
import com.dev.restLms.juhwi.PermissionManagementService.repository.PM_getUserWithPermission_UOPGe_Repository;
import com.dev.restLms.juhwi.PermissionManagementService.repository.PM_getUserWithPermission_Ue_Repository;
import com.dev.restLms.juhwi.PermissionManagementService.repository.PM_getUser_UOPG_Repository;
import com.dev.restLms.juhwi.PermissionManagementService.repository.PM_getUser_Ue_Repository;
import com.dev.restLms.juhwi.PermissionManagementService.repository.PM_setPermission_PGe_Repository;
import com.dev.restLms.juhwi.PermissionManagementService.repository.PM_setPermission_UOPGe_Repository;
import com.dev.restLms.juhwi.PermissionManagementService.repository.PM_setPermission_Ue_Repository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/permissionManager")
@Tag(name = "권한 관리 페이지", description = "권한 관리 API")
public class PM_Controller {

    @Autowired
    PM_PGe_Repository um_pge_repository;

    @Autowired
    PM_setPermission_UOPGe_Repository um_uopge_set_permission_repository;

    @Autowired
    PM_setPermission_PGe_Repository um_set_permission_pge_repository;

    @Autowired
    PM_setPermission_Ue_Repository um_set_permission_ue_repository;

    @PostMapping("/setPermission")
    @Operation(summary = "권한 설정", description = "사용자의 권한을 설정합니다.")
    public ResponseEntity<String> setPermission(
            @Parameter(description = "대상 아이디") @RequestParam String targetSessionId,
            @Parameter(description = "권한 이름") @RequestParam String setPermissionName) {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();

        // 사용자 검증
        final String userSessionId = auth.getPrincipal().toString();
        final Object[] grantStrings = auth.getAuthorities().toArray();
        log.info("토큰 저장 세션 아이디 >> " + userSessionId);

        // 접근 제어 로직
        boolean isChangeAble = false; // 권한 설정 성공 여부
        for (Object object : grantStrings) {
            log.info("토큰 저장 정보로 조회한 권한 코드 >> " + object);
            // 사용자 권한 나타내기
            String currentUserPermission = um_pge_repository.findById(object.toString())
                    .get()
                    .getPermissionName();
            if (currentUserPermission.equals("SITE_OFFICER")) {
                log.info("권한 존재 : " + currentUserPermission);
                isChangeAble = true;
            } else if (currentUserPermission.equals("STUDENT")) {
                log.info("권한 없음 : " + currentUserPermission);
                isChangeAble = false;
            }
        }

        if (isChangeAble) {
            // 사용자 권한 그룹 UOPG 에 사용자 권한이 있는가 확인
            Optional<List<PM_setPermission_UOPGe_Projection>> selectUser = um_uopge_set_permission_repository
                    .findBySessionId(targetSessionId);

            // 권한 찾기
            List<PermissionGroup> permissionGroups = um_set_permission_pge_repository
                    .findByPermissionName(setPermissionName);
            String permissionChoice = "false";
            for (PermissionGroup eachPermission : permissionGroups) {
                permissionChoice = eachPermission.getPermissionGroupUuid();
            }
            if (permissionChoice.equals("false")) {
                return new ResponseEntity<>("{\"responseText\": \"입력한 권한은 존재하지 않는 권한입니다.\"}", HttpStatus.BAD_REQUEST);
            }

            Optional<User> targetUser = um_set_permission_ue_repository.findById(targetSessionId);
            if (targetUser.isEmpty()) {
                return new ResponseEntity<>("{\"responseText\": \"해당 사용자가 존재하지 않습니다.\"}", HttpStatus.BAD_REQUEST);
            }

            if (selectUser.isPresent()) {
                // 이미 권한이 존재하는 경우
                log.info("권한을 업데이트 합니다.");
                List<PM_setPermission_UOPGe_Projection> originUserList = selectUser.get();
                for (PM_setPermission_UOPGe_Projection each : originUserList) {
                    UserOwnPermissionGroup pasteUser = UserOwnPermissionGroup.builder()
                            .sessionId(each.getSessionId())
                            .increaseId(each.getIncreaseId())
                            .permissionGroupUuid2(permissionChoice)
                            .build();
                    um_uopge_set_permission_repository.save(pasteUser);
                }
                return new ResponseEntity<>("{\"responseText\": \"권한을 업데이트 했습니다.\"}", HttpStatus.OK);
            } else {
                log.info("권한을 생성합니다.");
                UserOwnPermissionGroup pasteUser = UserOwnPermissionGroup.builder()
                        .sessionId(targetSessionId)
                        .permissionGroupUuid2(permissionChoice)
                        .build();
                um_uopge_set_permission_repository.save(pasteUser);
                return new ResponseEntity<>("{\"responseText\": \"권한을 생성 했습니다.\"}", HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>("{\"responseText\": \"전송 권한이 없습니다.\"}", HttpStatus.BAD_REQUEST);
        }
    }

    // 유저 조회를 위해 필요
    @Autowired
    PM_getUser_Ue_Repository um_get_user_ue_repository;

    // 유저 권한 조회를 위해 필요
    @Autowired
    PM_getUser_UOPG_Repository um_get_user_uopg_repository;

    @GetMapping("/getUser")
    @Operation(summary = "사용자 이메일로 조회", description = "사용자를 검색 데이터로 조회합니다.")
    public ResponseEntity<?> getUser(@RequestParam String searchParam,
            @RequestParam int page) {

        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();

        // 사용자 검증
        final String userSessionId = auth.getPrincipal().toString();
        final Object[] grantStrings = auth.getAuthorities().toArray();
        log.info("토큰 저장 세션 아이디 >> " + userSessionId);

        // 접근 제어 로직
        boolean isAccessAble = false; // 권한 설정 성공 여부
        for (Object object : grantStrings) {
            log.info("토큰 저장 정보로 조회한 권한 코드 >> " + object);
            // 사용자 권한 나타내기
            String currentUserPermission = um_pge_repository.findById(object.toString())
                    .get()
                    .getPermissionName();
            if (currentUserPermission.equals("SITE_OFFICER")) {
                log.info("권한 존재 : " + currentUserPermission);
                isAccessAble = true;
            } else if (currentUserPermission.equals("STUDENT")) {
                log.info("권한 없음 : " + currentUserPermission);
                isAccessAble = false;
            }
        }

        Pageable pageable = Pageable.ofSize(10).withPage(page);

        if (isAccessAble) {
            // 이메일로 검색
            Page<User> users = um_get_user_ue_repository.findByUserEmailContaining(searchParam, pageable);

            List<UserSearchResult> results = new ArrayList<>();
            for (User object : users) {
                // 권한아이디 권한으로 변경
                String permissionName = "false";
                List<UserOwnPermissionGroup> pgNames = um_get_user_uopg_repository
                        .findBySessionId(object.getSessionId());
                for (UserOwnPermissionGroup each : pgNames) {
                    if (um_pge_repository.findById(each.getPermissionGroupUuid2()).isPresent()) {
                        permissionName = um_pge_repository.findById(each.getPermissionGroupUuid2()).get()
                                .getPermissionName();
                    } else {
                        permissionName = "anonymous";
                    }
                }
                if (permissionName.equals("false")) {
                    permissionName = "권한 아이디로 권한 명 변환에 실패했습니다.";
                }
                results.add(new UserSearchResult(object.getSessionId(), object.getUserName(),
                        object.getUserEmail(),
                        permissionName));
            }
            Page<UserSearchResult> resultsPage = new PageImpl<>(results, pageable, users.getTotalElements());
            return new ResponseEntity<>(resultsPage, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("{\"responseText\": \"요청 권한이 없습니다.\"}",
                    HttpStatus.BAD_REQUEST);
        }
    }

    // 권한명 파싱을 위한 리포지토리 선언
    @Autowired
    PM_getUserWithPermission_PGe_Repository pm_get_user_with_permission_pge_repository;

    // 권한 그룹 조회를 위한 리포지토리 선언
    @Autowired
    PM_getUserWithPermission_UOPGe_Repository pm_get_user_with_permission_uopge_repository;

    @Autowired
    PM_getUserWithPermission_Ue_Repository pm_get_user_with_permission_ue_repository;

    // 이름으로 찾고, UUID를 가져와서 UUID로 UOPG에서 검색하여 조회한다.
    @GetMapping("/getUserWithPermission")
    @Operation(summary = "사용자 권한으로 조회", description = "권한명을 입력받아, 권한 그룹에서 UUID를 조회하고, UUID로 UOPG에서 검색하여 권한에 대한 사용자를 조회하는 기능.")
    public ResponseEntity<?> getUserWithPermission(@RequestParam String searchParam,
            @RequestParam int page) {

        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();

        // 사용자 검증
        final String userSessionId = auth.getPrincipal().toString();
        final Object[] grantStrings = auth.getAuthorities().toArray();
        log.info("토큰 저장 세션 아이디 >> " + userSessionId);

        // 접근 제어 로직
        boolean isAccessAble = false; // 권한 설정 성공 여부
        for (Object object : grantStrings) {
            log.info("토큰 저장 정보로 조회한 권한 코드 >> " + object);
            // 사용자 권한 나타내기
            String currentUserPermission = um_pge_repository.findById(object.toString())
                    .get()
                    .getPermissionName();
            if (currentUserPermission.equals("SITE_OFFICER")) {
                log.info("권한 존재 : " + currentUserPermission);
                isAccessAble = true;
            } else if (currentUserPermission.equals("STUDENT")) {
                log.info("권한 없음 : " + currentUserPermission);
                isAccessAble = false;
            }
        }

        Pageable pageable = Pageable.ofSize(10).withPage(page);

        if (isAccessAble) {
            // 권한명 파싱
            // List<PermissionGroup> permissionGroups = .(searchParam);
            Page<PM_getUserWithPermission_PGe_Projection> permissionNames = pm_get_user_with_permission_pge_repository
                    .findByPermissionName(searchParam, pageable);
            String permissionChoice = "false";
            for (PM_getUserWithPermission_PGe_Projection each : permissionNames) {
                permissionChoice = each.getPermissionGroupUuid();
            }
            if (permissionChoice.equals("false")) {
                return new ResponseEntity<>("{\"responseText\": \"입력한 권한은 존재하지 않는 권한입니다. 또는 데이터가 없습니다.\"}", HttpStatus.BAD_REQUEST);
            } else {
                log.info("권한명 파싱 성공 : " + permissionChoice);
                Page<PM_getUserWithPermission_UOPGe_projection> choiceUsers = pm_get_user_with_permission_uopge_repository
                        .findByPermissionGroupUuid2(permissionChoice, pageable);
                List<UserSearchResult> results = new ArrayList<>();
                for (PM_getUserWithPermission_UOPGe_projection each : choiceUsers) {
                    Optional<User> eachUser = pm_get_user_with_permission_ue_repository.findById(each.getSessionId());
                    if (eachUser.isPresent()) {
                        User user = eachUser.get();
                        results.add(new UserSearchResult(user.getSessionId(), user.getUserName(), user.getUserEmail(),
                                searchParam));
                    } else {
                        log.info("사용자 정보가 존재하지 않습니다.");
                    }
                }
                if (results.isEmpty()) {
                    return new ResponseEntity<>("{\"responseText\": \"해당 권한을 가진 사용자가 존재하지 않습니다.\"}", HttpStatus.OK);
                }
                Page<UserSearchResult> resultsPage = new PageImpl<>(results, pageable, choiceUsers.getTotalElements());
                return new ResponseEntity<>(resultsPage, HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>("{\"responseText\": \"요청 권한이 없습니다.\"}",
                    HttpStatus.BAD_REQUEST);
        }
    }

    // @GetMapping("/getUser")
    // @Operation(summary = "사용자 검색", description = "사용자를 검색 데이터로 검색합니다.")
    // public ResponseEntity<Page<UserSearchResult>> getUser(
    // @RequestParam String searchParam,
    // @RequestParam int page,
    // @RequestParam int size) {

    // Pageable pageable = PageRequest.of(page, size);
    // Page<User> users =
    // um_get_user_ue_repository.findByUserEmailContaining(searchParam, pageable);

    // Page<UserSearchResult> results = users.map(user -> {
    // String permissionName =
    // um_get_user_uopg_repository.findBySessionId(user.getSessionId())
    // .stream()
    // .map(UserOwnPermissionGroup::getPermissionGroupUuid2)
    // .map(um_pge_repository::findById)
    // .filter(Optional::isPresent)
    // .map(Optional::get)
    // .map(PermissionGroup::getPermissionName)
    // .findFirst()
    // .orElse("anonymous");

    // return new UserSearchResult(user.getSessionId(), user.getUserName(),
    // user.getUserEmail(), permissionName);
    // });

    // return ResponseEntity.ok(results);
    // }

    record UserSearchResult(String sessionId, String userName, String userEmail, String permissionName) {
    }

    @GetMapping("/getInit")
    @Operation(summary = "페이지 이니셜 데이터", description = "권한 목록을 제공합니다.")
    public ResponseEntity<?> getInit() {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();

        // 사용자 검증
        final String userSessionId = auth.getPrincipal().toString();
        final Object[] grantStrings = auth.getAuthorities().toArray();
        log.info("토큰 저장 세션 아이디 >> " + userSessionId);

        // 접근 제어 로직
        boolean isChangeAble = false; // 권한 설정 성공 여부
        for (Object object : grantStrings) {
            log.info("토큰 저장 정보로 조회한 권한 코드 >> " + object);
            // 사용자 권한 나타내기
            String currentUserPermission = um_pge_repository.findById(object.toString())
                    .get()
                    .getPermissionName();
            if (currentUserPermission.equals("SITE_OFFICER")) {
                log.info("권한 존재 : " + currentUserPermission);
                isChangeAble = true;
            } else if (currentUserPermission.equals("STUDENT")) {
                log.info("권한 없음 : " + currentUserPermission);
                isChangeAble = false;
            }
        }

        if (isChangeAble) {
            // 권한 검색 로직
            List<PermissionGroup> permissions = um_pge_repository.findAll();

            List<String> permissionNames = new ArrayList<>();
            permissions.forEach(permission -> {
                // log.info("권한 목록 : " + permission.getPermissionName());
                permissionNames.add(permission.getPermissionName());
            });
            return ResponseEntity.ok(permissionNames);
        } else {
            return new ResponseEntity<>("{\"responseText\": \"요청 권한이 없습니다.\"}",
                    HttpStatus.BAD_REQUEST);
        }
        // 예시 데이터
    }
}
