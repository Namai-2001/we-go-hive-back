package com.dev.restLms.sechan.SurveyMain.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
// import com.dev.restLms.entity.SurveyOwnResult;
import com.dev.restLms.entity.SurveyQuestion;
import com.dev.restLms.sechan.SurveyMain.dto.SM_Survey_DTO;
import com.dev.restLms.sechan.SurveyMain.projection.SM_C_Projection;
import com.dev.restLms.sechan.SurveyMain.projection.SM_SQ_Projection;
// import com.dev.restLms.sechan.SurveyMain.projection.SM_SQ_Projection;
import com.dev.restLms.sechan.SurveyMain.projection.SM_S_Projection;
import com.dev.restLms.sechan.SurveyMain.projection.SM_UOC_Projection;
import com.dev.restLms.sechan.SurveyMain.projection.SM_UOSV_Projection;
import com.dev.restLms.sechan.SurveyMain.repository.SM_C_Repository;
import com.dev.restLms.sechan.SurveyMain.repository.SM_OS_Repository;
import com.dev.restLms.sechan.SurveyMain.repository.SM_SE_Repository;
import com.dev.restLms.sechan.SurveyMain.repository.SM_SOA_Repository;
import com.dev.restLms.sechan.SurveyMain.repository.SM_SOR_Repository;
import com.dev.restLms.sechan.SurveyMain.repository.SM_SQ2_Repository;
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

    @Autowired
    private SM_SQ2_Repository sm_sq2_repository;

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
                    boolean isSurveyCompleted = true;
                    List<SurveyOwnResult> results = sm_sor_repository.findBySurveyExecutionIdAndSessionId(
                            surveyExecution.getSurveyExecutionId(), sessionId);

                    if (!results.isEmpty()) {
                        for (SurveyOwnResult result : results) {
                            Optional<SurveyOwnAnswer> answerOpt = sm_soa_repository
                                    .findById(result.getSurveyAnswerId());
                            if (answerOpt.isEmpty() ||
                                    (answerOpt.get().getAnswerData() == null && answerOpt.get().getScore() == null)) {
                                isSurveyCompleted = false;
                                break;
                            }
                        }
                    } else {
                        isSurveyCompleted = false;
                    }

                    String courseApproval = "F";
                    for (SM_UOC_Projection userCourse : userCourses) {
                        if (userCourse.getCourseId().equals(course.getCourseId())) {
                            courseApproval = userCourse.getCourseApproval();
                            break;
                        }
                    }

                    String courseStartDate = course.getCourseStartDate();
                    String courseEndDate = course.getCourseEndDate();

                    LocalDate midDate = null;
                    if (courseStartDate != null && courseEndDate != null) {
                        LocalDate startDate = LocalDate.parse(courseStartDate.substring(0, 8),
                                DateTimeFormatter.ofPattern("yyyyMMdd"));
                        LocalDate endDate = LocalDate.parse(courseEndDate.substring(0, 8),
                                DateTimeFormatter.ofPattern("yyyyMMdd"));
                        midDate = startDate.plusDays((ChronoUnit.DAYS.between(startDate, endDate)) / 2);
                    }

                    String surveyStatus = isSurveyCompleted
                            ? "F"
                            : ("F".equals(courseApproval) && midDate != null
                                    && today.compareTo(midDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))) >= 0)
                                            ? "T"
                                            : "F";

                    courseData.put("courseId", course.getCourseId());
                    courseData.put("courseTitle", course.getCourseTitle());
                    courseData.put("courseStartDate", convertTo8DigitDate(courseStartDate));
                    courseData.put("courseMidDate", midDate);
                    courseData.put("courseEndDate", convertTo8DigitDate(courseEndDate));
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
            if (!groupedVideos.containsKey(video.getUosvOfferedSubjectsId())) {
                groupedVideos.put(video.getUosvOfferedSubjectsId(), new ArrayList<>());
            }
            groupedVideos.get(video.getUosvOfferedSubjectsId()).add(video);
        }

        for (Map.Entry<String, List<SM_UOSV_Projection>> entry : groupedVideos.entrySet()) {
            String offeredSubjectsId = entry.getKey();
            List<SM_UOSV_Projection> videos = entry.getValue();

            Optional<SurveyExecution> surveyExecutionOpt = sm_se_repository.findByOfferedSubjectsId(offeredSubjectsId);

            if (surveyExecutionOpt.isPresent()) {
                SurveyExecution surveyExecution = surveyExecutionOpt.get();
                boolean isSurveyCompleted = true;

                List<SurveyOwnResult> results = sm_sor_repository.findBySurveyExecutionIdAndSessionId(
                        surveyExecution.getSurveyExecutionId(), sessionId);

                if (!results.isEmpty()) {
                    for (SurveyOwnResult result : results) {
                        Optional<SurveyOwnAnswer> answerOpt = sm_soa_repository.findById(result.getSurveyAnswerId());
                        if (answerOpt.isEmpty() ||
                                (answerOpt.get().getAnswerData() == null && answerOpt.get().getScore() == null)) {
                            isSurveyCompleted = false;
                            break;
                        }
                    }
                } else {
                    isSurveyCompleted = false;
                }

                boolean allCompleted = true;
                for (SM_UOSV_Projection video : videos) {
                    if (Integer.parseInt(video.getProgress()) < 100) {
                        allCompleted = false;
                        break;
                    }
                }

                Optional<OfferedSubjects> offeredSubjectOpt = sm_os_repository.findById(offeredSubjectsId);

                String subjectName = "Unknown Subject";
                if (offeredSubjectOpt.isPresent()) {
                    OfferedSubjects offeredSubject = offeredSubjectOpt.get();

                    SM_S_Projection subject = sm_s_repository.findBySubjectId(
                            offeredSubject.getSubjectId());
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
                    boolean isSurveyCompleted = true;
                    List<SurveyOwnResult> results = sm_sor_repository.findBySurveyExecutionIdAndSessionId(
                            surveyExecution.getSurveyExecutionId(), sessionId);

                    if (!results.isEmpty()) {
                        for (SurveyOwnResult result : results) {
                            Optional<SurveyOwnAnswer> answerOpt = sm_soa_repository
                                    .findById(result.getSurveyAnswerId());
                            if (answerOpt.isEmpty() ||
                                    (answerOpt.get().getAnswerData() == null && answerOpt.get().getScore() == null)) {
                                isSurveyCompleted = false;
                                break;
                            }
                        }
                    } else {
                        isSurveyCompleted = false;
                    }

                    String courseApproval = "F";
                    for (SM_UOC_Projection userCourse : userCourses) {
                        if (userCourse.getCourseId().equals(course.getCourseId())) {
                            courseApproval = userCourse.getCourseApproval();
                            break;
                        }
                    }

                    String courseStartDate = course.getCourseStartDate();
                    String courseEndDate = course.getCourseEndDate();

                    LocalDate midDate = null;
                    if (courseStartDate != null && courseEndDate != null) {
                        LocalDate startDate = LocalDate.parse(courseStartDate.substring(0, 8),
                                DateTimeFormatter.ofPattern("yyyyMMdd"));
                        LocalDate endDate = LocalDate.parse(courseEndDate.substring(0, 8),
                                DateTimeFormatter.ofPattern("yyyyMMdd"));
                        midDate = startDate.plusDays((ChronoUnit.DAYS.between(startDate, endDate)) / 2);
                    }

                    String surveyStatus = isSurveyCompleted
                            ? "F"
                            : ("F".equals(courseApproval) && midDate != null
                                    && today.compareTo(midDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"))) >= 0)
                                            ? "T"
                                            : "F";

                    courseData.put("courseId", course.getCourseId());
                    courseData.put("courseTitle", course.getCourseTitle());
                    courseData.put("courseStartDate", convertTo8DigitDate(courseStartDate));
                    courseData.put("courseMidDate", midDate);
                    courseData.put("courseEndDate", convertTo8DigitDate(courseEndDate));
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
            if (!groupedVideos.containsKey(video.getUosvOfferedSubjectsId())) {
                groupedVideos.put(video.getUosvOfferedSubjectsId(), new ArrayList<>());
            }
            groupedVideos.get(video.getUosvOfferedSubjectsId()).add(video);
        }

        for (Map.Entry<String, List<SM_UOSV_Projection>> entry : groupedVideos.entrySet()) {
            String offeredSubjectsId = entry.getKey();
            List<SM_UOSV_Projection> videos = entry.getValue();

            Optional<SurveyExecution> surveyExecutionOpt = sm_se_repository.findByOfferedSubjectsId(offeredSubjectsId);

            if (surveyExecutionOpt.isPresent()) {
                SurveyExecution surveyExecution = surveyExecutionOpt.get();
                boolean isSurveyCompleted = true;

                List<SurveyOwnResult> results = sm_sor_repository.findBySurveyExecutionIdAndSessionId(
                        surveyExecution.getSurveyExecutionId(), sessionId);

                if (!results.isEmpty()) {
                    for (SurveyOwnResult result : results) {
                        Optional<SurveyOwnAnswer> answerOpt = sm_soa_repository.findById(result.getSurveyAnswerId());
                        if (answerOpt.isEmpty() ||
                                (answerOpt.get().getAnswerData() == null && answerOpt.get().getScore() == null)) {
                            isSurveyCompleted = false;
                            break;
                        }
                    }
                } else {
                    isSurveyCompleted = false;
                }

                boolean allCompleted = true;
                for (SM_UOSV_Projection video : videos) {
                    if (Integer.parseInt(video.getProgress()) < 100) {
                        allCompleted = false;
                        break;
                    }
                }

                Optional<OfferedSubjects> offeredSubjectOpt = sm_os_repository.findById(offeredSubjectsId);

                String subjectName = "Unknown Subject";
                if (offeredSubjectOpt.isPresent()) {
                    OfferedSubjects offeredSubject = offeredSubjectOpt.get();

                    SM_S_Projection subject = sm_s_repository.findBySubjectId(
                            offeredSubject.getSubjectId());
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
    @Operation(summary = "만족도 조사 질문 조회", description = "SurveyExecution ID를 기준으로 질문을 반환하고 설문 유형을 판별합니다.")
    public ResponseEntity<?> getSurveyQuestions(
            @RequestParam String surveyExecutionId,
            @RequestParam(required = false) String courseId,
            @RequestParam(required = false) String offeredSubjectsId) {
        try {
            
            // ------------------------------ 수정 부분
            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
            .getContext().getAuthentication();
            // 유저 세션아이디 보안 컨텍스트에서 가져오기
            String sessionId = auth.getPrincipal().toString();
            // ------------------------------ 수정 부분

            System.out.println("GET /survey/questions called");
            System.out.println("surveyExecutionId: " + surveyExecutionId);
            System.out.println("courseId: " + courseId);
            System.out.println("offeredSubjectsId: " + offeredSubjectsId);

            // SurveyExecution 조회
            Optional<SurveyExecution> executionOpt = sm_se_repository.findById(surveyExecutionId);
            if (executionOpt.isEmpty()) {
                System.out.println("SurveyExecution not found for ID: " + surveyExecutionId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("유효하지 않은 SurveyExecution ID입니다.");
            }

            SurveyExecution execution = executionOpt.get();
            System.out.println("SurveyExecution found: " + execution);

            // 설문 유형 판별
            String surveyType;
            if (offeredSubjectsId != null) {
                surveyType = "subject"; // 과목 설문조사
            } else if (courseId != null) {
                surveyType = "course"; // 과정 설문조사
            } else {
                if (execution.getOfferedSubjectsId() != null) {
                    surveyType = "subject";
                } else if (execution.getCourseId() != null) {
                    surveyType = "course";
                } else {
                    System.out.println("SurveyExecution에 과정 또는 과목 정보가 없습니다.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("SurveyExecution에 과정 또는 과목 정보가 없습니다.");
                }
            }
            System.out.println("Survey type determined: " + surveyType);

            // ------------------------------ 수정 부분 (Repository - SM_SQ, SM_SOR 수정)
            // SM_SQ 수정 - Optional<SM_SQ_Projection> findBySurveyQuestionId(String surveyQuestionId);
            // SM_SOR 수정 - List<SurveyOwnResult> findBySurveyExecutionIdAndSessionId(String surveyExecutionId, String sessionId);
            List<SurveyOwnResult> findQuestionIds = sm_sor_repository.findBySurveyExecutionIdAndSessionId(surveyExecutionId, sessionId);
            
            List<Map<String, Object>> questionData = new ArrayList<>();
            
            for(SurveyOwnResult findQuestionId : findQuestionIds){

                Optional<SM_SQ_Projection> findQuestion = sm_sq_repository.findBySurveyQuestionId(findQuestionId.getSurveyQuestionId());

                if(findQuestion.isPresent()){

                    Map<String, Object> questionMap = new HashMap<>();
                    questionMap.put("surveyQeustionId", findQuestionId.getSurveyQuestionId());
                    questionMap.put("questionData", findQuestion.get().getQuestionData());
                    questionMap.put("answerCategory", findQuestion.get().getAnswerCategory());
                    questionMap.put("surveyAnswerId", findQuestionId.getSurveyAnswerId());
                    
                    questionData.add(questionMap);
                    
                }
                
            }
            // ------------------------------ 수정 부분

            // // 질문 조회
            // List<SurveyQuestion> questions = sm_sq2_repository.findBySurveyCategory(surveyType);
            // if (questions.isEmpty()) {
            //     System.out.println("No questions found for survey type: " + surveyType);
            //     return ResponseEntity.status(HttpStatus.NOT_FOUND).body("등록된 질문이 없습니다.");
            // }
            // System.out.println("Questions retrieved: " + questions.size());

            // // SurveyAnswerId 조회 및 매핑
            // List<Map<String, Object>> questionData = new ArrayList<>();
            // for (SurveyQuestion question : questions) {
            //     System.out.println("Processing question: " + question.getSurveyQuestionId());
            //     Optional<SurveyOwnAnswer> answerOpt = sm_soa_repository
            //             .findBySurveyQuestionId(question.getSurveyQuestionId());

            //     Map<String, Object> questionMap = new HashMap<>();
            //     questionMap.put("surveyQuestionId", question.getSurveyQuestionId());
            //     questionMap.put("questionData", question.getQuestionData());
            //     questionMap.put("answerCategory", question.getAnswerCategory());
            //     questionMap.put("surveyAnswerId", answerOpt.map(SurveyOwnAnswer::getSurveyAnswerId).orElse(null));

            //     System.out.println("Mapped question data: " + questionMap);
            //     questionData.add(questionMap);
            // }

            // 결과 반환
            Map<String, Object> response = new HashMap<>();
            response.put("surveyType", surveyType);
            response.put("questions", questionData);

            System.out.println("Final response: " + response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Exception occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("오류 발생: " + e.getMessage());
        }
    }

    @PostMapping("/survey/submit")
    public ResponseEntity<?> submitSurveyAnswers(@RequestBody List<SM_Survey_DTO> answers) {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();
        String sessionId = auth.getPrincipal().toString();

        try {
            if (answers.isEmpty()) {
                return ResponseEntity.badRequest().body("답변 리스트가 비어 있습니다.");
            }

            String surveyExecutionId = answers.get(0).getSurveyExecutionId();
            Optional<SurveyExecution> executionOpt = sm_se_repository.findById(surveyExecutionId);

            if (executionOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("유효하지 않은 SurveyExecution ID: " + surveyExecutionId);
            }

            for (SM_Survey_DTO answerDTO : answers) {
                System.out.println("Processing answer: " + answerDTO);

                // SurveyQuestion 유효성 확인
                Optional<SurveyQuestion> questionOpt = sm_sq_repository.findById(answerDTO.getSurveyQuestionId());
                if (questionOpt.isEmpty()) {
                    return ResponseEntity.badRequest()
                            .body("유효하지 않은 SurveyQuestion ID: " + answerDTO.getSurveyQuestionId());
                }

                // `score` 또는 `answerData`가 하나라도 있어야 함
                if (answerDTO.getScore() == null && answerDTO.getAnswerData() == null) {
                    return ResponseEntity.badRequest()
                            .body("점수 또는 답변 데이터가 비어 있습니다: " + answerDTO.getSurveyQuestionId());
                }

                // SurveyOwnAnswer 존재 확인
                Optional<SurveyOwnAnswer> existingAnswerOpt = sm_soa_repository
                        .findBySurveyAnswerId(answerDTO.getSurveyAnswerId());
                        System.out.println("Processing answer 12 12 : " + answerDTO);
                if (existingAnswerOpt.isPresent()) {
                    
                    // 기존 레코드 업데이트
                    SurveyOwnAnswer existingAnswer = existingAnswerOpt.get();
                    existingAnswer.setAnswerData(answerDTO.getAnswerData());
                    existingAnswer.setScore(answerDTO.getScore());
                    sm_soa_repository.save(existingAnswer);
                } else {
                    return ResponseEntity.badRequest()
                            .body("SurveyAnswerId에 해당하는 답변이 존재하지 않습니다: " + answerDTO.getSurveyAnswerId());
                }

            }

            // 설문 완료 상태 확인
            List<SurveyOwnResult> results = sm_sor_repository.findBySurveyExecutionIdAndSessionId(surveyExecutionId,
                    sessionId);

            boolean isSurveyCompleted = true;
            for (SurveyOwnResult result : results) {
                Optional<SurveyOwnAnswer> answerOpt = sm_soa_repository.findById(result.getSurveyAnswerId());
                if (answerOpt.isEmpty()
                        || (answerOpt.get().getAnswerData() == null && answerOpt.get().getScore() == null)) {
                    isSurveyCompleted = false;
                    break;
                }
            }

            String message = isSurveyCompleted ? "설문이 완료되었습니다." : "설문이 저장되었으나 모든 질문에 대한 답변이 완료되지 않았습니다.";
            return ResponseEntity.ok(message);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("설문 저장 중 오류 발생: " + e.getMessage());
        }
    }
}
