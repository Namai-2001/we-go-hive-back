package com.dev.restLms.sechan.SurveyMain.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.entity.OfferedSubjects;
import com.dev.restLms.entity.SurveyExecution;
import com.dev.restLms.entity.SurveyOwnAnswer;
import com.dev.restLms.entity.SurveyOwnResult;
import com.dev.restLms.sechan.SurveyMain.dto.SM_Survey_DTO;
import com.dev.restLms.sechan.SurveyMain.projection.SM_C_Projection;
import com.dev.restLms.sechan.SurveyMain.projection.SM_SQ_Projection;
import com.dev.restLms.sechan.SurveyMain.projection.SM_S_Projection;
import com.dev.restLms.sechan.SurveyMain.projection.SM_UOC_Projection;
import com.dev.restLms.sechan.SurveyMain.projection.SM_UOSV_Projection;
import com.dev.restLms.sechan.SurveyMain.repository.SM_C_Repository;
import com.dev.restLms.sechan.SurveyMain.repository.SM_OS_Repository;
import com.dev.restLms.sechan.SurveyMain.repository.SM_SE_Repository;
import com.dev.restLms.sechan.SurveyMain.repository.SM_SOA_Repository;
import com.dev.restLms.sechan.SurveyMain.repository.SM_SOR_Repository;
import com.dev.restLms.sechan.SurveyMain.repository.SM_SQ_Repository;
import com.dev.restLms.sechan.SurveyMain.repository.SM_S_Repository;
import com.dev.restLms.sechan.SurveyMain.repository.SM_UOC_Repository;
import com.dev.restLms.sechan.SurveyMain.repository.SM_UOSV_Repository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDate;
// import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@Tag(name = "만족도조사 조회", description = "사용자의 만족도 조사 가능 여부")
public class SM_Controller {

    @Autowired
    private SM_UOC_Repository sm_uoc_repository;

    @Autowired
    private SM_C_Repository sm_c_repository;

    @Autowired
    private SM_UOSV_Repository sm_uosv_repository;

    @Autowired
    private SM_OS_Repository sm_os_repository;

    @Autowired
    private SM_S_Repository sm_s_repository;

    @Autowired
    private SM_SE_Repository sm_se_repository;

    @Autowired
    private SM_SQ_Repository sm_sq_repository;

    @Autowired
    private SM_SOA_Repository sm_soa_repository;

    @Autowired
    private SM_SOR_Repository sm_sor_repository;

    // 날짜 형식 변환 함수
    // public static String convertTo8DigitDate(String dateString) {
    // DateTimeFormatter inputFormatter =
    // DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    // DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy년 M월
    // d일");

    // LocalDateTime dateTime = LocalDateTime.parse(dateString, inputFormatter);

    // return dateTime.toLocalDate().format(outputFormatter);
    // }

    // 날짜 형식 변환 함수
    public static String convertTo8DigitDate(String dateString) {
        try {
            // 입력값 유효성 검증
            if (dateString == null || !dateString.matches("\\d{14}")) {
                throw new IllegalArgumentException("Invalid date format: " + dateString);
            }

            String dateOnly = dateString.substring(0, 8); // 날짜 부분만 추출
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일");

            LocalDate date = LocalDate.parse(dateOnly, inputFormatter);

            return date.format(outputFormatter);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Failed to parse date: " + dateString, e);
        }
    }

    @GetMapping("survey/courses")
    @Operation(summary = "과정 만족도 조사 상태 조회", description = "특정 sessionId를 기준으로 과정에 대한 만족도 조사 상태를 반환")
    public List<Map<String, Object>> getCourseSurveyStatus() {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();
        // 유저 세션아이디 보안 컨텍스트에서 가져오기
        String sessionId = auth.getPrincipal().toString();
        List<Map<String, Object>> courseResponse = new ArrayList<>();

        // 사용자 과정 조회
        List<SM_UOC_Projection> userCourses = sm_uoc_repository.findBySessionId(sessionId);
        List<String> courseIds = new ArrayList<>();
        for (SM_UOC_Projection userCourse : userCourses) {
            courseIds.add(userCourse.getCourseId());
        }

        // 과정 정보 조회
        List<SM_C_Projection> courses = sm_c_repository.findByCourseIdIn(courseIds);
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        for (SM_C_Projection course : courses) {
            Map<String, Object> courseData = new HashMap<>();
            List<SurveyExecution> surveyExecutions = sm_se_repository.findByCourseId(course.getCourseId());

            if (!surveyExecutions.isEmpty()) {
                for (SurveyExecution surveyExecution : surveyExecutions) {
                    // 설문 완료 여부 확인
                    boolean isSurveyCompleted = sm_sor_repository.existsBySurveyExecutionIdAndSessionId(
                            surveyExecution.getSurveyExecutionId(), sessionId);

                    String courseApproval = "F";
                    for (SM_UOC_Projection userCourse : userCourses) {
                        if (userCourse.getCourseId().equals(course.getCourseId())) {
                            courseApproval = userCourse.getCourseApproval();
                            break;
                        }
                    }

                    String courseStartDate = course.getCourseStartDate();
                    String courseEndDate = course.getCourseEndDate();

                    // 중간 날짜 계산
                    LocalDate midDate = null;
                    if (courseStartDate != null && courseEndDate != null) {
                        LocalDate startDate = LocalDate.parse(courseStartDate.substring(0, 8),
                                DateTimeFormatter.ofPattern("yyyyMMdd"));
                        LocalDate endDate = LocalDate.parse(courseEndDate.substring(0, 8),
                                DateTimeFormatter.ofPattern("yyyyMMdd"));
                        midDate = startDate.plusDays((ChronoUnit.DAYS.between(startDate, endDate)) / 2);
                    }

                    // 설문 가능 여부 결정
                    String surveyStatus = isSurveyCompleted
                            ? "F" // 설문이 완료된 경우
                            : ("F".equals(courseApproval) && midDate != null
                                    && today.compareTo(midDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))) >= 0)
                                            ? "T" // 과정 승인 "F"이며, 중간 날짜 이후 설문 가능
                                            : "F"; // 기본 설문 불가능

                    String formattedCourseEndDate = courseEndDate != null ? convertTo8DigitDate(courseEndDate) : null;
                    String formattedCourseStartDate = courseStartDate != null ? convertTo8DigitDate(courseStartDate)
                            : null;

                    courseData.put("courseId", course.getCourseId());
                    courseData.put("courseTitle", course.getCourseTitle());
                    courseData.put("courseStartDate", formattedCourseStartDate);
                    courseData.put("courseMidDate", midDate);
                    courseData.put("courseEndDate", formattedCourseEndDate);
                    courseData.put("courseApproval", courseApproval);
                    courseData.put("surveyStatus", surveyStatus);
                    courseData.put("surveyExecutionId", surveyExecution.getSurveyExecutionId());
                }
                courseResponse.add(courseData);
            }
        }
        return courseResponse;
    }

    // ------------ 과목 조회 ------------
    @GetMapping("survey/subjects")
    @Operation(summary = "과목 만족도 조사 상태 조회", description = "특정 sessionId를 기준으로 과목에 대한 만족도 조사 상태를 반환")
    public List<Map<String, Object>> getSubjectSurveyStatus() {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();
        // 유저 세션아이디 보안 컨텍스트에서 가져오기
        String sessionId = auth.getPrincipal().toString();
        List<Map<String, Object>> subjectResponse = new ArrayList<>();
        List<SM_UOSV_Projection> userVideos = sm_uosv_repository.findByUosvSessionId(sessionId);
        Map<String, List<SM_UOSV_Projection>> groupedVideos = new HashMap<>();

        for (SM_UOSV_Projection video : userVideos) {
            groupedVideos.computeIfAbsent(video.getUosvOfferedSubjectsId(), k -> new ArrayList<>()).add(video);
        }

        for (Map.Entry<String, List<SM_UOSV_Projection>> entry : groupedVideos.entrySet()) {
            String offeredSubjectsId = entry.getKey();
            List<SM_UOSV_Projection> videos = entry.getValue();

            Optional<SurveyExecution> surveyExecutionOpt = sm_se_repository.findByOfferedSubjectsId(offeredSubjectsId);

            if (surveyExecutionOpt.isPresent()) {
                SurveyExecution surveyExecution = surveyExecutionOpt.get();

                // 설문 완료 여부 확인
                boolean isSurveyCompleted = sm_sor_repository.existsBySurveyExecutionIdAndSessionId(
                        surveyExecution.getSurveyExecutionId(), sessionId);

                boolean allCompleted = videos.stream()
                        .allMatch(video -> Integer.parseInt(video.getProgress()) >= 100);

                Optional<OfferedSubjects> offeredSubjectOpt = sm_os_repository.findById(offeredSubjectsId);
                String subjectName = "Unknown Subject";

                if (offeredSubjectOpt.isPresent()) {
                    OfferedSubjects offeredSubject = offeredSubjectOpt.get();

                    SM_S_Projection subject = sm_s_repository.findBySubjectId(offeredSubject.getSubjectId());
                    if (subject != null) {
                        subjectName = subject.getSubjectName();
                    }
                }

                String surveyAvailable = isSurveyCompleted ? "F" : (allCompleted ? "T" : "F");

                Map<String, Object> subjectData = new HashMap<>();
                subjectData.put("offeredSubjectsId", offeredSubjectsId);
                subjectData.put("subjectName", subjectName);
                subjectData.put("surveyAvailable", surveyAvailable);
                subjectData.put("isSurveyCompleted", isSurveyCompleted ? "T" : "F");
                subjectData.put("surveyExecutionId", surveyExecution.getSurveyExecutionId());

                subjectResponse.add(subjectData);
            }
        }
        return subjectResponse;
    }

    @GetMapping("/survey/status")
    @Operation(summary = "과정 및 과목 만족도 조사 상태 조회", description = "특정 sessionId를 기준으로 과정과 과목의 만족도 조사 상태를 반환")
    public Map<String, List<Map<String, Object>>> getSurveyStatus() {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();
        // 유저 세션아이디 보안 컨텍스트에서 가져오기
        String sessionId = auth.getPrincipal().toString();
        Map<String, List<Map<String, Object>>> response = new HashMap<>();

        // ------------ 과정 데이터 조회 ------------
        List<Map<String, Object>> courseResponse = new ArrayList<>();

        // 사용자 과정 조회
        List<SM_UOC_Projection> userCourses = sm_uoc_repository.findBySessionId(sessionId);
        List<String> courseIds = new ArrayList<>();
        for (SM_UOC_Projection userCourse : userCourses) {
            courseIds.add(userCourse.getCourseId());
        }

        // 과정 정보 조회
        List<SM_C_Projection> courses = sm_c_repository.findByCourseIdIn(courseIds);
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        for (SM_C_Projection course : courses) {
            Map<String, Object> courseData = new HashMap<>();
            List<SurveyExecution> surveyExecutions = sm_se_repository.findByCourseId(course.getCourseId());

            if (!surveyExecutions.isEmpty()) {
                for (SurveyExecution surveyExecution : surveyExecutions) {
                    // 설문 완료 여부 확인
                    boolean isSurveyCompleted = sm_sor_repository.existsBySurveyExecutionIdAndSessionId(
                            surveyExecution.getSurveyExecutionId(), sessionId);

                    String courseApproval = "F";
                    for (SM_UOC_Projection userCourse : userCourses) {
                        if (userCourse.getCourseId().equals(course.getCourseId())) {
                            courseApproval = userCourse.getCourseApproval();
                            break;
                        }
                    }

                    String courseStartDate = course.getCourseStartDate();
                    String courseEndDate = course.getCourseEndDate();

                    // 중간 날짜 계산
                    LocalDate midDate = null;
                    if (courseStartDate != null && courseEndDate != null) {
                        LocalDate startDate = LocalDate.parse(courseStartDate.substring(0, 8),
                                DateTimeFormatter.ofPattern("yyyyMMdd"));
                        LocalDate endDate = LocalDate.parse(courseEndDate.substring(0, 8),
                                DateTimeFormatter.ofPattern("yyyyMMdd"));
                        midDate = startDate.plusDays((ChronoUnit.DAYS.between(startDate, endDate)) / 2);
                    }

                    // 설문 가능 여부 결정
                    String surveyStatus = isSurveyCompleted
                            ? "F" // 설문이 완료된 경우
                            : ("F".equals(courseApproval) && midDate != null
                                    && today.compareTo(midDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))) >= 0)
                                            ? "T" // 과정 승인 "F"이며, 중간 날짜 이후 설문 가능
                                            : "F"; // 기본 설문 불가능

                    String formattedCourseEndDate = courseEndDate != null ? convertTo8DigitDate(courseEndDate) : null;
                    String formattedCourseStartDate = courseStartDate != null ? convertTo8DigitDate(courseStartDate)
                            : null;

                    courseData.put("courseId", course.getCourseId());
                    courseData.put("courseTitle", course.getCourseTitle());
                    courseData.put("courseStartDate", formattedCourseStartDate);
                    courseData.put("courseMidDate", midDate);
                    courseData.put("courseEndDate", formattedCourseEndDate);
                    courseData.put("courseApproval", courseApproval);
                    courseData.put("surveyStatus", surveyStatus);
                    courseData.put("surveyExecutionId", surveyExecution.getSurveyExecutionId());
                }
                courseResponse.add(courseData);
            }
        }

        // ------------ 과목 데이터 조회 ------------
        List<Map<String, Object>> subjectResponse = new ArrayList<>();
        List<SM_UOSV_Projection> userVideos = sm_uosv_repository.findByUosvSessionId(sessionId);
        Map<String, List<SM_UOSV_Projection>> groupedVideos = new HashMap<>();

        for (SM_UOSV_Projection video : userVideos) {
            groupedVideos.computeIfAbsent(video.getUosvOfferedSubjectsId(), k -> new ArrayList<>()).add(video);
        }

        for (Map.Entry<String, List<SM_UOSV_Projection>> entry : groupedVideos.entrySet()) {
            String offeredSubjectsId = entry.getKey();
            List<SM_UOSV_Projection> videos = entry.getValue();

            // SurveyExecution 확인
            Optional<SurveyExecution> surveyExecutionOpt = sm_se_repository.findByOfferedSubjectsId(offeredSubjectsId);

            if (surveyExecutionOpt.isPresent()) {
                SurveyExecution surveyExecution = surveyExecutionOpt.get();

                // 설문 완료 여부 확인
                boolean isSurveyCompleted = sm_sor_repository.existsBySurveyExecutionIdAndSessionId(
                        surveyExecution.getSurveyExecutionId(), sessionId);

                boolean allCompleted = videos.stream()
                        .allMatch(video -> Integer.parseInt(video.getProgress()) >= 100);

                // OfferedSubjects 조회
                Optional<OfferedSubjects> offeredSubjectOpt = sm_os_repository.findById(offeredSubjectsId);

                String subjectName = "Unknown Subject"; // 기본값 설정
                if (offeredSubjectOpt.isPresent()) {
                    OfferedSubjects offeredSubject = offeredSubjectOpt.get();

                    // Subject 이름 조회
                    SM_S_Projection subject = sm_s_repository.findBySubjectId(offeredSubject.getSubjectId());
                    if (subject != null) {
                        subjectName = subject.getSubjectName();
                    }
                }

                String surveyAvailable = isSurveyCompleted ? "F" : (allCompleted ? "T" : "F");

                Map<String, Object> subjectData = new HashMap<>();
                subjectData.put("offeredSubjectsId", offeredSubjectsId);
                subjectData.put("subjectName", subjectName);
                subjectData.put("surveyAvailable", surveyAvailable);
                subjectData.put("isSurveyCompleted", isSurveyCompleted ? "T" : "F");
                subjectData.put("surveyExecutionId", surveyExecution.getSurveyExecutionId());

                subjectResponse.add(subjectData);
            }
        }

        response.put("courses", courseResponse);
        response.put("subjects", subjectResponse);

        return response;
    }

    @GetMapping("/survey/questions")
    @Operation(summary = "만족도 조사 질문 조회", description = "과정 또는 과목에 대한 만족도 조사 질문을 반환")
    public List<SM_SQ_Projection> getSurveyQuestions(
            @RequestParam String surveyExecutionId,
            @RequestParam(required = false) String courseId,
            @RequestParam(required = false) String offeredSubjectsId) {

        // 과정인지 과목인지 판단(만약 둘다 들어오면 과목으로 우선처리리)
        if (offeredSubjectsId != null) {
            return sm_sq_repository.findBySurveyCategory("subject");
        } else if (courseId != null) {
            return sm_sq_repository.findBySurveyCategory("course");
        } else {
            throw new IllegalArgumentException("courseId 또는 offeredSubjectsId 중 하나를 입력");
        }
    }

    @Operation(summary = "만족도 조사 답변 제출", description = "5지선다 및 서술형 답변을 저장합니다.")
    @PostMapping("survey/submit")
    public ResponseEntity<String> submitSurveyAnswers(@RequestBody List<SM_Survey_DTO> answers) {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();
        // 유저 세션아이디 보안 컨텍스트에서 가져오기
        String sessionId = auth.getPrincipal().toString();
        for (SM_Survey_DTO answerDTO : answers) {
            // SurveyOwnAnswer 저장
            SurveyOwnAnswer answer = new SurveyOwnAnswer();
            answer.setSurveyQuestionId(answerDTO.getSurveyQuestionId());

            // 5지선다 점수 처리
            if (answerDTO.getScore() != null) {
                answer.setScore(answerDTO.getScore());
            }

            // 서술형 답변 처리
            if (answerDTO.getAnswerData() != null) {
                answer.setAnswerData(answerDTO.getAnswerData());
            }

            sm_soa_repository.save(answer);

            // SurveyOwnResult 저장
            SurveyOwnResult result = new SurveyOwnResult();
            result.setSurveyExecutionId(answerDTO.getSurveyExecutionId());
            result.setSessionId(sessionId);
            result.setSurveyQuestionId(answerDTO.getSurveyQuestionId());
            result.setSurveyAnswerId(answer.getSurveyAnswerId());

            sm_sor_repository.save(result);
        }
        return ResponseEntity.ok("제출!");
    }
}
