package com.dev.restLms.sechan.SurveyMain.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

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
import java.time.format.DateTimeFormatter;
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
    public static String formatDate(String dateString) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일");

        LocalDate date = LocalDate.parse(dateString, inputFormatter);
        return date.format(outputFormatter);
    }

    @GetMapping("survey/courses/{sessionId}")
    @Operation(summary = "과정 만족도 조사 상태 조회", description = "특정 sessionId를 기준으로 과정에 대한 만족도 조사 상태를 반환")
    public List<Map<String, Object>> getCourseSurveyStatus(@RequestParam String sessionId) {
        List<Map<String, Object>> courseResponse = new ArrayList<>();

        List<SM_UOC_Projection> userCourses = sm_uoc_repository.findBySessionId(sessionId);
        List<String> courseIds = new ArrayList<>();
        for (SM_UOC_Projection userCourse : userCourses) {
            courseIds.add(userCourse.getCourseId());
        }

        List<SM_C_Projection> courses = sm_c_repository.findByCourseIdIn(courseIds);
        String today = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

        for (SM_C_Projection course : courses) {
            Optional<SurveyExecution> surveyExecutionOpt = sm_se_repository.findByCourseId(course.getCourseId());

            if (surveyExecutionOpt.isPresent()) {
                SurveyExecution surveyExecution = surveyExecutionOpt.get();

                String courseApproval = "F";
                for (SM_UOC_Projection userCourse : userCourses) {
                    if (userCourse.getCourseId().equals(course.getCourseId())) {
                        courseApproval = userCourse.getCourseApproval();
                        break;
                    }
                }

                String courseEndDate = course.getCourseEndDate();
                String surveyStatus = ("T".equals(courseApproval)
                        && (courseEndDate == null || courseEndDate.compareTo(today) >= 0)) ? "T" : "F";

                Map<String, Object> courseData = new HashMap<>();
                courseData.put("courseId", course.getCourseId());
                courseData.put("courseTitle", course.getCourseTitle());
                courseData.put("courseEndDate", formatDate(courseEndDate));
                courseData.put("courseApproval", courseApproval);
                courseData.put("surveyStatus", surveyStatus);
                courseData.put("surveyExecutionId", surveyExecution.getSurveyExecutionId());

                courseResponse.add(courseData);
            }
        }
        return courseResponse;
    }

    // ------------ 과목 조회 ------------
    @GetMapping("survey/subjects/{sessionId}")
    @Operation(summary = "과목 만족도 조사 상태 조회", description = "특정 sessionId를 기준으로 과목에 대한 만족도 조사 상태를 반환")
    public List<Map<String, Object>> getSubjectSurveyStatus(@RequestParam String sessionId) {
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

                Map<String, Object> subjectData = new HashMap<>();
                subjectData.put("offeredSubjectsId", offeredSubjectsId);
                subjectData.put("subjectName", subjectName);
                subjectData.put("surveyAvailable", allCompleted ? "T" : "F");
                subjectData.put("surveyExecutionId", surveyExecution.getSurveyExecutionId());

                subjectResponse.add(subjectData);
            }
        }
        return subjectResponse;
    }

    @GetMapping("/survey/status/{sessionId}")
    @Operation(summary = "과정 및 과목 만족도 조사 상태 조회", description = "특정 sessionId를 기준으로 과정과 과목의 만족도 조사 상태를 반환")
    public Map<String, List<Map<String, Object>>> getSurveyStatus(@RequestParam String sessionId) {
        Map<String, List<Map<String, Object>>> response = new HashMap<>();

        // ------------ 과정 데이터 조회 ------------
        List<Map<String, Object>> courseResponse = new ArrayList<>();
        List<SM_UOC_Projection> userCourses = sm_uoc_repository.findBySessionId(sessionId);

        List<String> courseIds = new ArrayList<>();
        for (SM_UOC_Projection userCourse : userCourses) {
            courseIds.add(userCourse.getCourseId());
        }

        List<SM_C_Projection> courses = sm_c_repository.findByCourseIdIn(courseIds);
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        for (SM_C_Projection course : courses) {
            Map<String, Object> courseData = new HashMap<>();
            Optional<SurveyExecution> surveyExecutionOpt = sm_se_repository
                    .findByCourseId(course.getCourseId());

            if (surveyExecutionOpt.isPresent()) {
                SurveyExecution surveyExecution = surveyExecutionOpt.get();

                String courseApproval = "F";
                for (SM_UOC_Projection userCourse : userCourses) {
                    if (userCourse.getCourseId().equals(course.getCourseId())) {
                        courseApproval = userCourse.getCourseApproval();
                        break;
                    }
                }

                String courseEndDate = course.getCourseEndDate(); 
                String surveyStatus = ("T".equals(courseApproval)
                        && courseEndDate != null
                        && courseEndDate.compareTo(today) >= 0) ? "T" : "F";

                String formattedCourseEndDate = courseEndDate != null ? formatDate(courseEndDate) : null;

                courseData.put("courseId", course.getCourseId());
                courseData.put("courseTitle", course.getCourseTitle());
                courseData.put("courseEndDate", formattedCourseEndDate); // 변환된 날짜 사용
                courseData.put("courseApproval", courseApproval);
                courseData.put("surveyStatus", surveyStatus);
                courseData.put("surveyExecutionId", surveyExecution.getSurveyExecutionId());

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

                Map<String, Object> subjectData = new HashMap<>();
                subjectData.put("offeredSubjectsId", offeredSubjectsId);
                subjectData.put("subjectName", subjectName);
                subjectData.put("surveyAvailable", allCompleted ? "T" : "F");
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
            @RequestParam String sessionId,
            @RequestParam String surveyExecutionId,
            @RequestParam(required = false) String courseId,
            @RequestParam(required = false) String offeredSubjectsId) {

        // 과정인지 과목인지 판단
        if (courseId != null) {
            return sm_sq_repository.findBySurveyCategory("course");
        } else if (offeredSubjectsId != null) {
            return sm_sq_repository.findBySurveyCategory("subject");
        } else {
            throw new IllegalArgumentException("courseId 또는 offeredSubjectsId 중 하나를 입력");
        }
    }

    @Operation(summary = "만족도 조사 답변 제출", description = "5지선다 및 서술형 답변을 저장합니다.")
    @PostMapping("survey/submit")
    public ResponseEntity<String> submitSurveyAnswers(@RequestBody List<SM_Survey_DTO> answers) {
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
            result.setSessionId(answerDTO.getSessionId());
            result.setSurveyQuestionId(answerDTO.getSurveyQuestionId());
            result.setSurveyAnswerId(answer.getSurveyAnswerId());

            sm_sor_repository.save(result);
        }
        return ResponseEntity.ok("제출!");
    }
}
