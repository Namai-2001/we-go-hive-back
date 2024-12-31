package com.dev.restLms.hyeon.course.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.entity.Course;
import com.dev.restLms.entity.CourseOwnSubject;
import com.dev.restLms.entity.OfferedSubjects;
import com.dev.restLms.entity.Subject;
import com.dev.restLms.entity.SubjectOwnVideo;
import com.dev.restLms.entity.User;
import com.dev.restLms.entity.UserOwnAssignment;
import com.dev.restLms.entity.UserOwnCourse;
import com.dev.restLms.entity.UserOwnSubjectVideo;
import com.dev.restLms.entity.Video;
import com.dev.restLms.hyeon.course.repository.CourseOwnSubjectRepository;
import com.dev.restLms.hyeon.course.repository.CourseRepository;
import com.dev.restLms.hyeon.course.repository.OfferedSubjectRepository;
import com.dev.restLms.hyeon.course.repository.SubjectOwnVideoRepository;
import com.dev.restLms.hyeon.course.repository.SubjectRepository;
import com.dev.restLms.hyeon.course.repository.UserOwnAssignmentRepository;
import com.dev.restLms.hyeon.course.repository.UserOwnCourseRepository;
import com.dev.restLms.hyeon.course.repository.UserOwnSubjectVideoRepository;
import com.dev.restLms.hyeon.course.repository.UserRepository;
import com.dev.restLms.hyeon.course.repository.VideoRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/myclass")
@Tag(name = "나의강의실 과정 및 과목 정보 조회 API", description = "사용자의 과정 및 과목 조회 API")
public class UserCourseController {

    @Autowired
    private OfferedSubjectRepository offeredSubjectRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserOwnCourseRepository userOwnCourseRepository;

    @Autowired
    private UserOwnAssignmentRepository userOwnAssignmentRepository;

    @Autowired
    private CourseOwnSubjectRepository courseOwnSubjectRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private SubjectOwnVideoRepository subjectOwnVideoRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserOwnSubjectVideoRepository userOwnSubjectVideoRepository;

    @Autowired
    private UserRepository userRepository;

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
    public ResponseEntity<Map<String, Object>> getUserByCourseSessionId() {

        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
            .getContext().getAuthentication();

        final String userSessionId = auth.getPrincipal().toString();
        // 유저 정보 및 과정 조회
        List<UserOwnCourse> userOwnCourses = userOwnCourseRepository.findBysessionId(userSessionId);

        List<Course> courses = new ArrayList<>();
        if (userOwnCourses != null) {
            for (UserOwnCourse userOwnCourse : userOwnCourses) {
                Course course = courseRepository.findByCourseId(userOwnCourse.getCourseId());
                if (course != null) {
                    courses.add(course);
                }
            }
        } else {
            return ResponseEntity.notFound().build();
        }

        // 현재 날짜 가져오기 (yyyyMMddHHmmss 형식)
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String formattedDate = LocalDateTime.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        // 이전에 수료한 과정과 현재 수강 중인 과정을 구분
        List<Course> currentCourses = new ArrayList<>();
        List<Course> previousCourses = new ArrayList<>();

        String elapsedDays = "";// 과정 시작일로부터 경과일
        for (Course course : courses) {
            String courseBoundary = calculateCourseDuration(course.getCourseStartDate(), course.getCourseBoundary());
            String formattedStartDate = convertTo8DigitDate(course.getCourseStartDate());
            String formattedEndDate = convertTo8DigitDate(course.getCourseEndDate());

            
            if (isCoursePast(currentDate, course.getCourseBoundary())) {
                course.setCourseBoundary(courseBoundary);// 문자열 날짜 --> xx일
                course.setCourseStartDate(formattedStartDate);// 문자열 날짜 --> 년월일
                course.setCourseEndDate(formattedEndDate);// 문자열 날짜 --> 년월일
                // 수료일이 과거면 "이전에 수료한 과정"
                previousCourses.add(course);
            } else {
                elapsedDays = calculateCourseDuration(course.getCourseStartDate(), formattedDate);// 경과일 계산 (수강 중인 경우)

                course.setCourseBoundary(courseBoundary);// 문자열 날짜 --> xx일
                course.setCourseStartDate(formattedStartDate);// 문자열 날짜 --> 년월일
                course.setCourseEndDate(formattedEndDate);// 문자열 날짜 --> 년월일
                // 수료일이 현재보다 이후면 "현재 수강 중인 과정"
                currentCourses.add(course);
            }
        }

        // 결과를 맵에 담아 반환
        Map<String, Object> response = new HashMap<>();
        response.put("currentCourses", currentCourses);
        response.put("previousCourses", previousCourses);
        response.put("elapsedDays", elapsedDays);

        return ResponseEntity.ok(response);
    }

    // --------------------------------------------------------------------------------------------------------------------------
    // 사용자의 과목 목록 조회
    @GetMapping("/personalAssignment")
    public ResponseEntity<?> getUserByAssignmentSessionId() {

        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
        .getContext().getAuthentication();

        final String userSessionId = auth.getPrincipal().toString();
                
        // 사용자가 수강 중인 과목 조회
        List<UserOwnAssignment> userOwnAssignments = userOwnAssignmentRepository.findAllByuserSessionId(userSessionId);
        if (userOwnAssignments.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // 과정별 과목과 개별 과목을 저장할 맵
        Map<String, List<Map<String, Object>>> courseOwnSubjects = new HashMap<>();
        List<Map<String, Object>> noneCourseSubjects = new ArrayList<>();

        // 사용자의 과목 목록 순회
        for (UserOwnAssignment userOwnAssignment : userOwnAssignments) {
            // 개설과목 조회
            OfferedSubjects offeredSubject = offeredSubjectRepository.findByofferedSubjectsId(userOwnAssignment.getOfferedSubjectsId());
            if (offeredSubject == null) continue;

            // 강사명 조회
            User instructor = userRepository.findBysessionId(offeredSubject.getTeacherSessionId());
            String instructorName = instructor != null ? instructor.getUserName() : "강사 정보 없음";

            // 과목별 세부 정보 준비
            Map<String, Object> subjectDetail = new HashMap<>();
            subjectDetail.put("instructorName", instructorName);

            // 사용자별 과목 영상 목록 조회
            List<UserOwnSubjectVideo> userVideos = userOwnSubjectVideoRepository.findByUosvSessionIdAndUosvOfferedSubjectsId(
                userOwnAssignment.getUserSessionId(), offeredSubject.getOfferedSubjectsId());

            // 사용자별 과목 영상 목록에 해당하는 회차 정보만 추가
            List<Map<String, Object>> episodeDetails = new ArrayList<>();
            for (UserOwnSubjectVideo userVideo : userVideos) {
                SubjectOwnVideo subjectVideo = subjectOwnVideoRepository.findByEpisodeIdAndSovOfferedSubjectsId(
                        userVideo.getUosvEpisodeId(), offeredSubject.getOfferedSubjectsId());

                if (subjectVideo != null) {
                    // 영상 정보 조회
                    Video video = videoRepository.findByVideoId(subjectVideo.getSovVideoId());
                    String videoTitle = video != null ? video.getVideoTitle() : "영상 정보 없음";

                    Map<String, Object> episodeDetail = new HashMap<>();
                    episodeDetail.put("episodeId", subjectVideo.getEpisodeId());
                    episodeDetail.put("videoSortIndex", subjectVideo.getVideoSortIndex());
                    episodeDetail.put("sovVideoId", subjectVideo.getSovVideoId());
                    episodeDetail.put("videoTitle", videoTitle); // 영상 제목 추가
                    episodeDetail.put("progress", userVideo.getProgress());
                    episodeDetail.put("final", userVideo.getUosvFinal());

                    episodeDetails.add(episodeDetail);
                }
            }
            subjectDetail.put("episodes", episodeDetails);

            // 과정 여부에 따른 분기 처리
            if (offeredSubject.getCourseId() != null && !offeredSubject.getCourseId().isEmpty()) {
                // 과정별 과목 처리
                List<CourseOwnSubject> courseSubjects = courseOwnSubjectRepository.findByCourseId(offeredSubject.getCourseId());
                List<Map<String, Object>> courseSubjectDetails = new ArrayList<>();

                // 과정명 조회
                Course course = courseRepository.findByCourseId(offeredSubject.getCourseId());
                String courseName = course != null ? course.getCourseTitle() : "과정 정보 없음";

                for (CourseOwnSubject courseOwnSubject : courseSubjects) {
                    Subject subject = subjectRepository.findBysubjectId(courseOwnSubject.getSubjectId());
                    if (subject != null) {
                        Map<String, Object> courseSubjectDetail = new HashMap<>(subjectDetail);
                        courseSubjectDetail.put("subject", subject);
                        courseSubjectDetails.add(courseSubjectDetail);
                    }
                }

                if (!courseSubjectDetails.isEmpty()) {
                    courseOwnSubjects.put(courseName, courseSubjectDetails);
                }
            } else {
                // 개별 과목 처리
                Subject subject = subjectRepository.findBysubjectId(offeredSubject.getSubjectId());
                if (subject != null) {
                    Map<String, Object> individualSubjectDetail = new HashMap<>(subjectDetail);
                    individualSubjectDetail.put("subject", subject);
                    individualSubjectDetail.put("offeredSubjectCode", offeredSubject.getOfferedSubjectsId());
                    noneCourseSubjects.add(individualSubjectDetail);
                }
            }
        }

        // 응답 데이터 생성
        Map<String, Object> response = new HashMap<>();
        response.put("userOwnAssignments", userOwnAssignments);
        response.put("courseOwnSubjects", courseOwnSubjects);
        response.put("noneCourseSubjects", noneCourseSubjects);

        return ResponseEntity.ok(response);
    }
}
