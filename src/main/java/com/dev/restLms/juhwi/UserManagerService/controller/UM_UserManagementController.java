package com.dev.restLms.juhwi.UserManagerService.controller;

import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.dev.restLms.entity.User;
import com.dev.restLms.juhwi.UserManagerService.projecttion.UM_searchUser_PGe_Projection;
import com.dev.restLms.juhwi.UserManagerService.projecttion.UM_searchUser_UOPGe_Projection;
import com.dev.restLms.juhwi.UserManagerService.projecttion.UM_searchUser_Ue_Projection;
import com.dev.restLms.juhwi.UserManagerService.repository.UM_ACCESS_PGe_Repository;
import com.dev.restLms.juhwi.UserManagerService.repository.UM_DEAFULT_Ue_Repository;
import com.dev.restLms.juhwi.UserManagerService.repository.UM_getInit_Ue_Repository;
import com.dev.restLms.juhwi.UserManagerService.repository.UM_searchUser_PGe_Repository;
import com.dev.restLms.juhwi.UserManagerService.repository.UM_searchUser_UOPGe_Repository;
import com.dev.restLms.juhwi.UserManagerService.repository.UM_searchUser_Ue_Repository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/userManager")
@Tag(name = "사용자 관리 페이지", description = "사용자 관리 API")
public class UM_UserManagementController {

    // 권한 인증
    @Autowired
    UM_ACCESS_PGe_Repository um_pge_repository;

    // 사용자 정보 조회
    @Autowired
    UM_getInit_Ue_Repository um_getInit_U_Repository;

    private static final List<String> ALL_MONTHS = List.of("JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE",
            "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER");
    private static final List<String> ALL_DAYS = generateAllDays();
    private static final List<String> ALL_YEARS = generateAllYears();

    private static List<String> generateAllDays() {
        List<String> allDays = new ArrayList<>();
        for (int year = 2020; year <= ZonedDateTime.now().getYear(); year++) {
            for (String month : ALL_MONTHS) {
                int monthValue = ALL_MONTHS.indexOf(month) + 1;
                YearMonth yearMonth = YearMonth.of(year, monthValue);
                int daysInMonth = yearMonth.lengthOfMonth();
                for (int day = 1; day <= daysInMonth; day++) {
                    allDays.add(month + "-" + day);
                }
            }
        }
        return allDays;
    }

    private static List<String> generateAllYears() {
        List<String> allYears = new ArrayList<>();
        for (int year = 2020; year <= ZonedDateTime.now().getYear(); year++) {
            allYears.add(String.valueOf(year));
        }
        return allYears;
    }

    private Map<String, Integer> initializeCountMap(List<String> keys) {
        Map<String, Integer> countMap = new LinkedHashMap<>();
        for (String key : keys) {
            countMap.put(key, 0);
        }
        return countMap;
    }

    private void updateCountMap(Map<String, Integer> countMap, String key) {
        countMap.put(key, countMap.getOrDefault(key, 0) + 1);
    }

    private <T> List<T> mapToList(Map<String, T> map) {
        return new ArrayList<>(map.values());
    }

    private <T> List<String> mapToKeys(Map<String, T> map) {
        return new ArrayList<>(map.keySet());
    }

    private ZonedDateTime parseDate(String dateStr, DateTimeFormatter formatter) {
        String parseDate = dateStr.trim().replace("GMT", "KST");
        return ZonedDateTime.parse(parseDate, formatter);
    }

    private boolean checkUserPermission() {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();
        final String userSessionId = auth.getPrincipal().toString();
        final Object[] grantStrings = auth.getAuthorities().toArray();
        log.info("토큰 저장 세션 아이디 >> " + userSessionId);

        boolean isChangeAble = false;
        for (Object object : grantStrings) {
            log.info("토큰 저장 정보로 조회한 권한 코드 >> " + object);
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
        // 로그인 해제
        isChangeAble = true;
        return isChangeAble;
    }

    public record ChartData(List<String> monthKeys, List<Integer> monthValues,
            List<String> dayKeys, List<Integer> dayValues,
            List<String> yearKeys, List<Integer> yearValues) {
    }

    @GetMapping("/getInit")
    @Operation(summary = "페이지 이니셜 통계 데이터 API", description = "통계 데이터를 제공합니다.")
    public ResponseEntity<?> getInit() {
        try {
            if (checkUserPermission()) {
                List<User> users = um_getInit_U_Repository.findAll();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);

                // 첫번째 페이지 첫번째 통계
                // 마지막 비밀번호 변경일 기준 비활성화 여부로 조회
                Map<String, Integer> monthCountMap = initializeCountMap(ALL_MONTHS);
                Map<String, Integer> dayCountMap = initializeCountMap(ALL_DAYS);
                Map<String, Integer> yearCountMap = initializeCountMap(ALL_YEARS);

                for (User user : users) {
                    if ("T".equals(user.getUserInactivate())) {
                        ZonedDateTime date = parseDate(user.getPwChangeDate(), formatter);
                        updateCountMap(monthCountMap, date.getMonth().toString());
                        updateCountMap(dayCountMap, date.getMonth().toString() + "-" + date.getDayOfMonth());
                        updateCountMap(yearCountMap, String.valueOf(date.getYear()));
                    }
                }

                ChartData based_on_pwchange_response = new ChartData(
                        mapToKeys(monthCountMap), mapToList(monthCountMap),
                        mapToKeys(dayCountMap), mapToList(dayCountMap),
                        mapToKeys(yearCountMap), mapToList(yearCountMap));

                // 첫번째 페이지 두번째 통계
                // 마지막 접속일 기준 비활성화 여부로 조회
                Map<String, Integer> connectionMonthCountMap = initializeCountMap(ALL_MONTHS);
                Map<String, Integer> connectionDayCountMap = initializeCountMap(ALL_DAYS);
                Map<String, Integer> connectionYearCountMap = initializeCountMap(ALL_YEARS);

                for (User user : users) {

                    if ("T".equals(user.getUserInactivate())) {
                        ZonedDateTime date = parseDate(user.getCurrentConnection(), formatter);
                        updateCountMap(connectionMonthCountMap, date.getMonth().toString());
                        updateCountMap(connectionDayCountMap, date.getMonth().toString() + "-" + date.getDayOfMonth());
                        updateCountMap(connectionYearCountMap, String.valueOf(date.getYear()));
                    }
                }

                ChartData based_on_connection_response = new ChartData(
                        mapToKeys(connectionMonthCountMap), mapToList(connectionMonthCountMap),
                        mapToKeys(connectionDayCountMap), mapToList(connectionDayCountMap),
                        mapToKeys(connectionYearCountMap), mapToList(connectionYearCountMap));

                // 두번째 페이지 첫번째 통계
                // 마지막 접속일 기준 휴면 계정 여부로 조회
                Map<String, Integer> longTermDisconnectionMonthCountMap = initializeCountMap(ALL_MONTHS);
                Map<String, Integer> longTermDisconnectionDayCountMap = initializeCountMap(ALL_DAYS);
                Map<String, Integer> longTermDisconnectionYearCountMap = initializeCountMap(ALL_YEARS);

                for (User user : users) {

                    if ("T".equals(user.getLongTermDisconnection())) {
                        ZonedDateTime date = parseDate(user.getPwChangeDate(), formatter);
                        updateCountMap(longTermDisconnectionMonthCountMap, date.getMonth().toString());
                        updateCountMap(longTermDisconnectionDayCountMap,
                                date.getMonth().toString() + "-" + date.getDayOfMonth());
                        updateCountMap(longTermDisconnectionYearCountMap, String.valueOf(date.getYear()));
                    }
                }

                ChartData based_on_long_term_disconnection_response = new ChartData(
                        mapToKeys(longTermDisconnectionMonthCountMap), mapToList(longTermDisconnectionMonthCountMap),
                        mapToKeys(longTermDisconnectionDayCountMap), mapToList(longTermDisconnectionDayCountMap),
                        mapToKeys(longTermDisconnectionYearCountMap), mapToList(longTermDisconnectionYearCountMap));

                // 두번째 페이지 두번째 통계
                // 마지막 접속일 기준 휴면 계정 여부로 조회
                Map<String, Integer> userInactivateMonthCountMap = initializeCountMap(ALL_MONTHS);
                Map<String, Integer> userInactivateDayCountMap = initializeCountMap(ALL_DAYS);
                Map<String, Integer> userInactivateYearCountMap = initializeCountMap(ALL_YEARS);

                for (User user : users) {

                    if ("T".equals(user.getLongTermDisconnection())) {
                        ZonedDateTime date = parseDate(user.getCurrentConnection(), formatter);
                        updateCountMap(userInactivateMonthCountMap, date.getMonth().toString());
                        updateCountMap(userInactivateDayCountMap,
                                date.getMonth().toString() + "-" + date.getDayOfMonth());
                        updateCountMap(userInactivateYearCountMap, String.valueOf(date.getYear()));
                    }
                }

                ChartData based_on_user_inactivate_response = new ChartData(
                        mapToKeys(userInactivateMonthCountMap), mapToList(userInactivateMonthCountMap),
                        mapToKeys(userInactivateDayCountMap), mapToList(userInactivateDayCountMap),
                        mapToKeys(userInactivateYearCountMap), mapToList(userInactivateYearCountMap));

                // 세번째 페이지 첫번째 통계
                // 마지막 비밀번호 변경일 기준 탈퇴 요청 여부로 조회
                Map<String, Integer> unsubscribeMonthCountMap = initializeCountMap(ALL_MONTHS);
                Map<String, Integer> unsubscribeDayCountMap = initializeCountMap(ALL_DAYS);
                Map<String, Integer> unsubscribeYearCountMap = initializeCountMap(ALL_YEARS);

                for (User user : users) {
                    if ("T".equals(user.getUnsubscribe())) {
                        ZonedDateTime date = parseDate(user.getPwChangeDate(), formatter);
                        updateCountMap(unsubscribeMonthCountMap, date.getMonth().toString());
                        updateCountMap(unsubscribeDayCountMap, date.getMonth().toString() + "-" + date.getDayOfMonth());
                        updateCountMap(unsubscribeYearCountMap, String.valueOf(date.getYear()));
                    }
                }

                ChartData based_on_unsubscribe_response = new ChartData(
                        mapToKeys(unsubscribeMonthCountMap), mapToList(unsubscribeMonthCountMap),
                        mapToKeys(unsubscribeDayCountMap), mapToList(unsubscribeDayCountMap),
                        mapToKeys(unsubscribeYearCountMap), mapToList(unsubscribeYearCountMap));

                // 세번째 페이지 두번째 통계
                // 마지막 접속일 기준 휴면 계정 여부로 조회
                Map<String, Integer> inactiveUserMonthCountMap = initializeCountMap(ALL_MONTHS);
                Map<String, Integer> inactiveUserDayCountMap = initializeCountMap(ALL_DAYS);
                Map<String, Integer> inactiveUserYearCountMap = initializeCountMap(ALL_YEARS);

                for (User user : users) {

                    if ("T".equals(user.getUnsubscribe())) {
                        ZonedDateTime date = parseDate(user.getCurrentConnection(), formatter);
                        updateCountMap(inactiveUserMonthCountMap, date.getMonth().toString());
                        updateCountMap(inactiveUserDayCountMap,
                                date.getMonth().toString() + "-" + date.getDayOfMonth());
                        updateCountMap(inactiveUserYearCountMap, String.valueOf(date.getYear()));
                    }
                }

                ChartData based_on_inactive_user_response = new ChartData(
                        mapToKeys(inactiveUserMonthCountMap), mapToList(inactiveUserMonthCountMap),
                        mapToKeys(inactiveUserDayCountMap), mapToList(inactiveUserDayCountMap),
                        mapToKeys(inactiveUserYearCountMap), mapToList(inactiveUserYearCountMap));

                // 반환 데이터 생성하는 부분
                record FirstPage(ChartData basedOnPwChangeResponse,
                        ChartData basedOnConnectionResponse) {
                }
                FirstPage first_page = new FirstPage(based_on_pwchange_response, based_on_connection_response);

                record SecondPage(ChartData basedOnLongTermDisconnectionResponse,
                        ChartData basedOnUserInactivateResponse) {
                }
                SecondPage second_page = new SecondPage(based_on_long_term_disconnection_response,
                        based_on_user_inactivate_response);

                record ThirdPage(ChartData basedOnUnsubscribeResponse,
                        ChartData basedOnUnsubcribeResponseCurrent) {
                }
                ThirdPage third_page = new ThirdPage(based_on_unsubscribe_response, based_on_inactive_user_response);

                record getInitResponse(FirstPage firstPage, SecondPage secondPage, ThirdPage thirdPage) {
                }
                getInitResponse get_init_response = new getInitResponse(first_page, second_page, third_page);
                return ResponseEntity.ok(get_init_response);

            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("조회 권한이 없습니다.");
            }
        } catch (Exception e) {
            log.error("예외 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    @Autowired
    UM_searchUser_Ue_Repository um_searchUser_Ue_Repository;

    // 권한 파싱을 위한 리포지토리
    @Autowired
    UM_searchUser_UOPGe_Repository um_searchUser_UOPGe_Repository;

    // 권한 파싱을 위한 리포지토리
    @Autowired
    UM_searchUser_PGe_Repository um_searchUser_PGe_Repository;

    @GetMapping("/searchUser")
    @Operation(summary = "유저 검색 API, 세션아이디", description = "세션아이디로 검색해 제공합니다.")
    public ResponseEntity<?> searchUser(
            @Parameter(description = "검색어") @RequestParam String searchParam,
            @Parameter(description = "검색 타입") @RequestParam String searchType,
            @Parameter(description = "페이지") @RequestParam int page) {

        if (checkUserPermission()) {
            // 조회 권한이 있는 경우
            /*
             * 1. 세션 아이디
             * 2. 권한
             * 3. 최근 접속일
             * 4. 비밀번호 변경일
             * 5. 장기 미접속 여부
             * 6. 비활성화 여부
             * 7. 탈퇴 여부
             * 8. 이메일
             * 9. 이름
             */

            int size = 10;
            Pageable pageable = Pageable.ofSize(size).withPage(page);
            Page<UM_searchUser_Ue_Projection> searchResult = new PageImpl<>(new ArrayList<>());
            Page<User> users = new PageImpl<>(new ArrayList<>());
            switch (searchType) {
                case "sessionId":
                    searchResult = um_searchUser_Ue_Repository.findBySessionIdContaining(searchParam, pageable);
                    break;
                case "permissionName":
                    Page<UM_searchUser_PGe_Projection> selected_pge = um_searchUser_PGe_Repository
                            .findByPermissionName(searchParam, pageable);

                    if (selected_pge.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 검색 타입입니다.");
                    }

                    String pge_uuid = "";
                    for (UM_searchUser_PGe_Projection pg_each : selected_pge) {
                        pge_uuid = pg_each.getPermissionGroupUuid();
                    }

                    Page<UM_searchUser_UOPGe_Projection> selected_uopge = um_searchUser_UOPGe_Repository
                            .findByPermissionGroupUuid2(pge_uuid, pageable);
                    List<String> session_ids = new ArrayList<>();
                    for (UM_searchUser_UOPGe_Projection uopge_each : selected_uopge) {
                        session_ids.add(uopge_each.getSessionId());
                    }
                    searchResult = um_searchUser_Ue_Repository.findBySessionIdIn(session_ids, pageable);
                    break;
                case "currentConnection":
                    searchResult = um_searchUser_Ue_Repository.findByCurrentConnectionContaining(searchParam, pageable);
                    break;
                case "pwChangeDate":
                    searchResult = um_searchUser_Ue_Repository.findByPwChangeDateContaining(searchParam, pageable);
                    break;
                case "longTermDisconnection":
                    searchResult = um_searchUser_Ue_Repository.findByLongTermDisconnectionContaining(searchParam,
                            pageable);
                    break;
                case "userInactivate":
                    searchResult = um_searchUser_Ue_Repository.findByUserInactivateContaining(searchParam, pageable);
                    break;
                case "unsubscribe":
                    searchResult = um_searchUser_Ue_Repository.findByUnsubscribeContaining(searchParam, pageable);
                    break;
                case "userEmail":
                    searchResult = um_searchUser_Ue_Repository.findByUserEmailContaining(searchParam, pageable);
                    break;
                case "userName":
                    searchResult = um_searchUser_Ue_Repository.findByUserNameContaining(searchParam, pageable);
                    break;
                case "all":
                    users = um_searchUser_Ue_Repository.findAll(pageable);
                    break;
                default:
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 검색 타입입니다.");
            }
            record UserRecord(
                    String sessionId,
                    String userName,
                    String userEmail,
                    String pwChangeDate,
                    String currentConnection,
                    String userInactivate,
                    String longTermDisconnection,
                    String unsubscribe,
                    String permissionName) {
            }

            List<UserRecord> userRecords = new ArrayList<>();
            if (!users.isEmpty()) {
                for (User user : users) {
                    String sessionId = user.getSessionId();
                    Page<UM_searchUser_UOPGe_Projection> permissions = um_searchUser_UOPGe_Repository
                            .findBySessionId(sessionId, pageable);
                    String permissionUUID = "";
                    if (permissions.isEmpty()) {
                        permissionUUID = "anonymous";
                        // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("권한 변경 실패");
                        log.info(sessionId + "계정은 권한이 없는 계정입니다.");
                    }
                    List<UM_searchUser_UOPGe_Projection> permission = permissions.getContent();
                    for (UM_searchUser_UOPGe_Projection each : permission) {
                        permissionUUID = each.getPermissionGroupUuid2();
                    }
                    Page<UM_searchUser_PGe_Projection> permissionList = um_searchUser_PGe_Repository
                            .findByPermissionGroupUuid(permissionUUID, pageable);

                    String permissionName = "";
                    if (permissionList.isEmpty()) {
                        permissionName = "anonymous";
                        // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("권한 변경 실패 2");
                        log.info(permissionUUID + "는 찾을 수 없는 권한입니다.");
                    }

                    for (UM_searchUser_PGe_Projection userRecord : permissionList) {
                        permissionName = userRecord.getPermissionName();
                    }

                    UserRecord userRecord = new UserRecord(
                            user.getSessionId(),
                            user.getUserName(),
                            user.getUserEmail(),
                            user.getPwChangeDate(),
                            user.getCurrentConnection(),
                            user.getUserInactivate(),
                            user.getLongTermDisconnection(),
                            user.getUnsubscribe(),
                            permissionName);

                    userRecords.add(userRecord);
                }
                Page<UserRecord> resultPage = new PageImpl<>(userRecords, pageable, users.getTotalElements());
                return ResponseEntity.ok(resultPage);
            } else {
                for (UM_searchUser_Ue_Projection user : searchResult.getContent()) {
                    String sessionId = user.getSessionId();
                    Page<UM_searchUser_UOPGe_Projection> permissions = um_searchUser_UOPGe_Repository
                            .findBySessionId(sessionId, pageable);
                    String permissionUUID = "";
                    if (permissions.isEmpty()) {
                        permissionUUID = "anonymous";
                        // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("권한 변경 실패");
                        // log.info(sessionId + "계정은 권한이 없는 계정입니다.");
                    }
                    List<UM_searchUser_UOPGe_Projection> permission = permissions.getContent();
                    for (UM_searchUser_UOPGe_Projection each : permission) {
                        permissionUUID = each.getPermissionGroupUuid2();
                    }
                    Page<UM_searchUser_PGe_Projection> permissionList = um_searchUser_PGe_Repository
                            .findByPermissionGroupUuid(permissionUUID, pageable);

                    String permissionName = "";
                    if (permissionList.isEmpty()) {
                        permissionName = "anonymous";
                        // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("권한 변경 실패 2");
                        // log.info(permissionUUID + "는 찾을 수 없는 권한입니다.");
                    }

                    for (UM_searchUser_PGe_Projection userRecord : permissionList) {
                        permissionName = userRecord.getPermissionName();
                    }

                    UserRecord userRecord = new UserRecord(
                            user.getSessionId(),
                            user.getUserName(),
                            user.getUserEmail(),
                            user.getPwChangeDate(),
                            user.getCurrentConnection(),
                            user.getUserInactivate(),
                            user.getLongTermDisconnection(),
                            user.getUnsubscribe(),
                            permissionName);

                    userRecords.add(userRecord);
                }
                Page<UserRecord> resultPage = new PageImpl<>(userRecords, pageable, searchResult.getTotalElements());
                return ResponseEntity.ok(resultPage);
            }
            // 1. 세션아이디로 검색 containing, 페이징
            // 2. 검색한 데이터 반환 GMT를 KST로 변환해서
        } else {
            // 조회 권한이 없는 경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("{\"responseText: \"권한이 없습니다.\"}");
        }
    }

    @Autowired
    UM_DEAFULT_Ue_Repository um_default_Ue_Repository;

    @GetMapping("/updateInactiveBasedOnCurrentConnection")
    @Operation(summary = "비활성화 API, 최근 접속일 기준", description = "최근 접속일 기준으로 비활성화 처리")
    public ResponseEntity<?> updateInactiveBasedOnCurrentConnection(@RequestParam int days) {
        if (checkUserPermission()) {
            // 조회 권한이 있는 경우
            List<User> users = um_default_Ue_Repository.findAll();
            ZonedDateTime now = ZonedDateTime.now();
            for (User user : users) {
                ZonedDateTime lastConnection = parseDate(user.getCurrentConnection(),
                        DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH));
                if (lastConnection.isBefore(now.minusDays(days))) {
                    user.setUserInactivate("T");
                    um_default_Ue_Repository.save(user);
                }
            }
            return ResponseEntity.ok("비활성화 처리 완료");
        } else {
            // 조회 권한이 없는 경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }
    }

    @GetMapping("/updateInactiveBasedOnPwChangeDate")
    @Operation(summary = "비활성화 API, 최근 비밀번호 변경일", description = "비밀번호 변경일로 비활성화 처리")
    public ResponseEntity<?> updateInactiveBasedOnPwChangeDate(@RequestParam int days) {
        if (checkUserPermission()) {
            // 조회 권한이 있는 경우
            List<User> users = um_default_Ue_Repository.findAll();
            ZonedDateTime now = ZonedDateTime.now();
            for (User user : users) {
                ZonedDateTime pwChangeDate = parseDate(user.getPwChangeDate(),
                        DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH));
                if (pwChangeDate.isBefore(now.minusDays(days))) {
                    user.setUserInactivate("T");
                    um_default_Ue_Repository.save(user);
                }
            }
            return ResponseEntity.ok("비활성화 처리 완료");
        } else {
            // 조회 권한이 없는 경우
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }
    }

    @GetMapping("/updateInactiveBasedOnLongTermDisconnection")
    @Operation(summary = "비활성화 API, 장기 미접속자", description = "장기 미접속 여부로 비활성화 처리")
    public ResponseEntity<?> updateInactiveBasedOnLongTermDisconnection() {
        if (checkUserPermission()) {
            List<User> users = um_default_Ue_Repository.findAll();
            for (User user : users) {
                if ("T".equals(user.getLongTermDisconnection())) {
                    user.setUserInactivate("T");
                    um_default_Ue_Repository.save(user);
                }
            }
            return ResponseEntity.ok("비활성화 처리 완료");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }
    }

    @GetMapping("/updateLongTermDisconnectionBasedOnCurrentConnection")
    @Operation(summary = "미접속 API, 최근 접속 일자", description = "최근 접속 일자를 기준으로 미접속 처리")
    public ResponseEntity<?> updateLongTermDisconnectionBasedOnCurrentConnection(@RequestParam int days) {
        if (checkUserPermission()) {
            List<User> users = um_default_Ue_Repository.findAll();
            ZonedDateTime now = ZonedDateTime.now();
            for (User user : users) {
                ZonedDateTime lastConnection = parseDate(user.getCurrentConnection(),
                        DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH));
                if (lastConnection.isBefore(now.minusDays(days))) {
                    user.setLongTermDisconnection("T");
                    um_default_Ue_Repository.save(user);
                }
            }
            return ResponseEntity.ok("미접속 처리 완료");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }
    }

    @GetMapping("/updateLongTermDisconnectionBasedOnPwChangeDate")
    @Operation(summary = "미접속 API, 최근 비밀번호 변경 일자", description = "비밀번호 변경 일자를 기준으로 미접속 처리")
    public ResponseEntity<?> updateLongTermDisconnectionBasedOnPwChangeDate(@RequestParam int days) {
        if (checkUserPermission()) {
            List<User> users = um_default_Ue_Repository.findAll();
            ZonedDateTime now = ZonedDateTime.now();
            for (User user : users) {
                ZonedDateTime pwChangeDate = parseDate(user.getPwChangeDate(),
                        DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH));
                if (pwChangeDate.isBefore(now.minusDays(days))) {
                    user.setLongTermDisconnection("T");
                    um_default_Ue_Repository.save(user);
                }
            }
            return ResponseEntity.ok("미접속 처리 완료");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }
    }

    @GetMapping("/deleteUserBasedOnUnsubscribe")
    @Operation(summary = "사용자 삭제 API, 탈퇴 요청", description = "탈퇴 요청을 기준으로 사용자 삭제")
    public ResponseEntity<?> deleteUserBasedOnUnsubscribe() {
        if (checkUserPermission()) {
            List<User> users = um_default_Ue_Repository.findAll();
            for (User user : users) {
                if ("T".equals(user.getUnsubscribe())) {
                    um_default_Ue_Repository.delete(user);
                }
            }
            return ResponseEntity.ok("사용자 삭제 완료");
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한이 없습니다.");
        }
    }
}