package com.dev.restLms.hyeon.course.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.entity.Course;
import com.dev.restLms.entity.UserOwnCourse;
import com.dev.restLms.entity.UserOwnPermissionGroup;
import com.dev.restLms.hyeon.course.DTO.CourseDTO;
import com.dev.restLms.hyeon.course.repository.CourseRepository;
import com.dev.restLms.hyeon.course.repository.StudentPermisionGroupRepository;
import com.dev.restLms.hyeon.course.repository.StudentUopgRepository;
import com.dev.restLms.hyeon.course.repository.UserOwnCourseRepository;
import com.dev.restLms.hyeon.officer.projection.PursuitPermissionGroup;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/myclass")
@Tag(name = "나의강의실 과정 및 과목 정보 조회 API", description = "사용자의 과정 및 과목 조회 API")
public class UserCourseController {
    
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserOwnCourseRepository userOwnCourseRepository;

    @Autowired
    private StudentUopgRepository userOwnPermissionGroupRepository;

    @Autowired
    private StudentPermisionGroupRepository permisionGroupRepository;

    // 수료일을 기준으로 이전에 수강한 과정과 현재 수강중인 과정을 구분
    public static boolean isCoursePast(String currentDateStr, String courseBoundaryStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        // 현재 날짜와 courseBoundary를 LocalDateTime으로 변환
        LocalDateTime currentDate = LocalDateTime.parse(currentDateStr, formatter);
        LocalDateTime courseBoundaryDate = LocalDateTime.parse(courseBoundaryStr, formatter);

        // 현재 날짜가 수료일 이후면 true
        return currentDate.isAfter(courseBoundaryDate);
    }

    // 수료기간 계산 함수
    public static String calculateCourseDuration(String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime start = LocalDateTime.parse(startDate, formatter);
        LocalDateTime end = LocalDateTime.parse(endDate, formatter);

        long durationInDays = ChronoUnit.DAYS.between(start, end);
        return durationInDays + "일";
    }

    // 날짜 형식 변환 함수
    public static String convertTo8DigitDate(String dateString) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일");

        LocalDateTime dateTime = LocalDateTime.parse(dateString, inputFormatter);

        return dateTime.toLocalDate().format(outputFormatter);
    }

// --------------------------------------------------------------------------------------------------------------------------
    // 사용자의 과정 목록 조회
    @GetMapping("/personalCourse")
    @Operation(summary = "사용자의 과정 목록 조회", description = "주어진 SESSION_ID로 사용자의 과정 목록 조회합니다.")
    public ResponseEntity<?> getUserSessionIdByCourse() {
        try {
            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();
            final String userSessionId = auth.getPrincipal().toString();

            Optional<UserOwnPermissionGroup> uopsOpt = userOwnPermissionGroupRepository.findBySessionId(userSessionId);
            if (uopsOpt.isEmpty()) {
                return ResponseEntity.status(403).body("접근 권한이 없습니다.");
            }
            PursuitPermissionGroup ppg = permisionGroupRepository
                .findByPermissionGroupUuid(uopsOpt.get().getPermissionGroupUuid2());

            if (ppg.getPermissionName().equals("STUDENT")) {

                // 사용자가 수강한 모든 목록 조회, 이전에 수강한 과정 포함
                List<UserOwnCourse> userOwnCourses = userOwnCourseRepository.findBysessionId(userSessionId);
                if (userOwnCourses == null || userOwnCourses.isEmpty()) {
                    return ResponseEntity.status(404).body("수강한 과정이 없습니다.");
                }
                
                
                // 이전에 수료한 과정과 현재 수강 중인 과정을 구분
                List<CourseDTO> previousCourses = new ArrayList<>();
                List<CourseDTO> currentCourses = new ArrayList<>();
                String elapsedDays = "";// 과정 시작일로부터 경과일

                // 현재 날짜 가져오기 (yyyyMMddHHmmss 형식)
                String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                String formattedDate = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

                for (UserOwnCourse uoc : userOwnCourses) {
                    Course course = courseRepository.findByCourseId(uoc.getCourseId());
                    if (course == null) continue;

                    String courseBoundary = calculateCourseDuration(course.getCourseStartDate(), course.getCourseEndDate());
                    String formattedStartDate = convertTo8DigitDate(course.getCourseStartDate());
                    String formattedEndDate = convertTo8DigitDate(course.getCourseEndDate());

                    if (isCoursePast(currentDate, course.getCourseEndDate())) {
                        
                        CourseDTO c = CourseDTO.builder()
                            .courseId(course.getCourseId())
                            .sessionId(course.getSessionId())
                            .courseTitle(course.getCourseTitle())
                            .courseBoundary(courseBoundary)// 문자열 날짜 --> xx일
                            .courseCompleted(course.getCourseCompleted())
                            .courseCapacity(course.getCourseCapacity()+"명")
                            .courseStartDate(formattedStartDate)// 문자열 날짜 --> 년월일
                            .courseEndDate(formattedEndDate)// 문자열 날짜 --> 년월일
                            .build();
                        
                        // 수료일이 과거면 "이전에 수료한 과정"
                        previousCourses.add(c);

                    } else if ("F".equals(uoc.getCourseApproval()) && !isCoursePast(currentDate, course.getCourseEndDate())) {

                        elapsedDays = calculateCourseDuration(course.getCourseStartDate(), formattedDate);
                        
                        CourseDTO c = CourseDTO.builder()
                            .courseId(course.getCourseId())
                            .sessionId(course.getSessionId())
                            .courseTitle(course.getCourseTitle())
                            .courseBoundary(courseBoundary)// 문자열 날짜 --> xx일
                            .courseCompleted(course.getCourseCompleted())
                            .courseCapacity(course.getCourseCapacity()+"명")
                            .courseStartDate(formattedStartDate)// 문자열 날짜 --> 년월일
                            .courseEndDate(formattedEndDate)// 문자열 날짜 --> 년월일
                            .build();

                        currentCourses.add(c);
                    }
                }
                
                // 결과를 맵에 담아 반환
                Map<String, Object> response = new HashMap<>();
                response.put("currentCourses", currentCourses);
                response.put("previousCourses", previousCourses);
                response.put("elapsedDays", elapsedDays);

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(403).body("접근 권한이 없습니다.");
            }
        } catch (Exception e) {
        return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
      }
    }

}
