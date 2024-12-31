package com.dev.restLms.sechan.courseCompletePage.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.entity.Assignment;
import com.dev.restLms.entity.Course;
import com.dev.restLms.entity.OfferedSubjects;
import com.dev.restLms.entity.SubjectOwnVideo;
import com.dev.restLms.entity.SurveyExecution;
import com.dev.restLms.entity.SurveyOwnResult;
import com.dev.restLms.entity.UserOwnAssignmentEvaluation;
import com.dev.restLms.entity.UserOwnCourse;
import com.dev.restLms.entity.UserOwnSubjectVideo;
import com.dev.restLms.sechan.courseCompletePage.projection.CCP_UOSV_Projection;
import com.dev.restLms.sechan.courseCompletePage.repository.CCP_A_Repository;
import com.dev.restLms.sechan.courseCompletePage.repository.CCP_C_Repository;
import com.dev.restLms.sechan.courseCompletePage.repository.CCP_OS_Repository;
import com.dev.restLms.sechan.courseCompletePage.repository.CCP_SE_Repository;
import com.dev.restLms.sechan.courseCompletePage.repository.CCP_SOR_Repository;
import com.dev.restLms.sechan.courseCompletePage.repository.CCP_SOV_Repository;
import com.dev.restLms.sechan.courseCompletePage.repository.CCP_UOAE_Repository;
import com.dev.restLms.sechan.courseCompletePage.repository.CCP_UOC2_Repository;
import com.dev.restLms.sechan.courseCompletePage.repository.CCP_UOC_Repository;
import com.dev.restLms.sechan.courseCompletePage.repository.CCP_UOSV_Repository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/courseComplete")
@Tag(name = "사용자 수료 페이지", description = "과정 인정 구분이 'T'인 사용자는 수료증을 다운 받을 수 있다")
public class CCP_Controller {

    @Autowired
    private CCP_UOC_Repository ccp_uoc_repository;

    @Autowired
    private CCP_UOSV_Repository ccp_uosv_repository;

    @Autowired
    private CCP_A_Repository ccp_a_repository;

    @Autowired
    private CCP_UOAE_Repository ccp_uoae_repository;

    @Autowired
    private CCP_SOR_Repository ccp_sor_repository;

    @Autowired
    private CCP_SE_Repository ccp_se_repository;

    @Autowired
    private CCP_OS_Repository ccp_os_repository;

    @Autowired
    private CCP_SOV_Repository ccp_sov_repository;

    @Autowired
    private CCP_UOC2_Repository ccp_uoc2_repository;

    @Autowired
    private CCP_C_Repository ccp_c_repository;

    private UserOwnCourse validateCourseAndApproval(String courseId, String sessionId) {
        Optional<UserOwnCourse> userOwnCourseOpt = ccp_uoc_repository.findByCourseIdAndSessionId(courseId, sessionId);
        if (userOwnCourseOpt.isEmpty()) {
            return null;
        }
        UserOwnCourse userOwnCourse = userOwnCourseOpt.get();
        return "T".equals(userOwnCourse.getCourseApproval()) ? userOwnCourse : null;
    }

    @GetMapping("/user-courses")
    @Operation(summary = "사용자의 과정 확인", description = "사용자의 모든 과정을 반환합니다.")
    public ResponseEntity<List<Map<String, String>>> getUserCourses() {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();
        String userSessionId = auth.getPrincipal().toString();
        // 사용자의 수강 과정을 조회
        List<UserOwnCourse> userCourses = ccp_uoc2_repository.findBySessionId(userSessionId);
        if (userCourses.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<>()); // 빈 리스트 반환
        }

        // 필요한 정보를 담을 리스트 생성
        List<Map<String, String>> response = new ArrayList<>();

        // 각 수강 과정에 대해 courseId, courseTitle, courseApproval 데이터 매핑
        for (UserOwnCourse userCourse : userCourses) {
            Optional<Course> courseOpt = ccp_c_repository.findById(userCourse.getCourseId());
            if (courseOpt.isPresent()) {
                Map<String, String> courseInfo = new HashMap<>();
                courseInfo.put("courseId", userCourse.getCourseId());
                courseInfo.put("courseTitle", courseOpt.get().getCourseTitle());
                courseInfo.put("courseApproval", userCourse.getCourseApproval());
                response.add(courseInfo);
            }
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/check-approval/{courseId}")
    @Operation(summary = "사용자 과정 인정 구분 확인", description = "1. 과정 안에 모든 영상 100% 확인 2. 사용자 과제 점수 커트라인 확인 3. 과제의 만족도 조사 확인")
    public ResponseEntity<String> checkAndAutoUpdateCourseApproval(
            @PathVariable String courseId) {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();
        String userSessionId = auth.getPrincipal().toString();

        // 사용자 과정 조회
        Optional<UserOwnCourse> userOwnCourseOpt = ccp_uoc_repository.findByCourseIdAndSessionId(courseId,
                userSessionId);
        if (userOwnCourseOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("해당 과정을 찾을 수 없습니다.");
        }
        UserOwnCourse userOwnCourse = userOwnCourseOpt.get();

        // 과정에 포함된 개설 과목 조회
        List<OfferedSubjects> offeredSubjectsList = ccp_os_repository.findByCourseId(courseId);
        if (offeredSubjectsList.isEmpty()) {
            return ResponseEntity.badRequest().body("해당 과정에 개설된 과목이 없습니다.");
        }

        // 1. 모든 영상 progress 확인
        for (OfferedSubjects offeredSubject : offeredSubjectsList) {
            List<SubjectOwnVideo> subjectVideos = ccp_sov_repository
                    .findBySovOfferedSubjectsId(offeredSubject.getOfferedSubjectsId());
            for (SubjectOwnVideo subjectVideo : subjectVideos) {
                List<UserOwnSubjectVideo> userVideos = ccp_uosv_repository.findByUosvOfferedSubjectsIdAndUosvSessionId(
                        offeredSubject.getOfferedSubjectsId(), userSessionId);

                for (UserOwnSubjectVideo userVideo : userVideos) {
                    if (Integer.parseInt(userVideo.getProgress()) < 100) {
                        return ResponseEntity.ok("해당 과정의 일부 영상 진행률이 100% 미만입니다.");
                    }
                }
            }
        }

        // 2. 모든 과제 점수가 cutline을 초과해야 함
        for (OfferedSubjects offeredSubject : offeredSubjectsList) {
            List<Assignment> assignments = ccp_a_repository
                    .findByOfferedSubjectsId(offeredSubject.getOfferedSubjectsId());
            for (Assignment assignment : assignments) {
                List<UserOwnAssignmentEvaluation> evaluations = ccp_uoae_repository
                        .findByUoaeSessionIdAndAssignmentId(userSessionId, assignment.getAssignmentId());

                if (evaluations.isEmpty()) {
                    return ResponseEntity.ok("과제에 대한 제출 또는 평가가 없습니다.");
                }

                for (UserOwnAssignmentEvaluation evaluation : evaluations) {
                    int score = Integer.parseInt(evaluation.getScore());
                    int cutline = Integer.parseInt(assignment.getCutline());
                    if (score < cutline) {
                        return ResponseEntity.ok("과제 점수가 기준 점수를 초과하지 않았습니다.");
                    }
                }
            }
        }

        // 3. 과정에 대한 만족도 조사 실시 확인
        List<SurveyExecution> surveyExecutions = ccp_se_repository.findByCourseId(courseId);
        for (SurveyExecution surveyExecution : surveyExecutions) {
            List<SurveyOwnResult> surveyResults = ccp_sor_repository.findBySurveyExecutionIdAndSessionId(
                    surveyExecution.getSurveyExecutionId(), userSessionId);
            if (surveyResults.isEmpty()) {
                return ResponseEntity.ok("해당 과정에 대한 만족도 조사가 실시되지 않았습니다.");
            }
        }

        // 조건 충족 시 courseApproval 자동 업데이트
        if (!"T".equals(userOwnCourse.getCourseApproval())) {
            userOwnCourse.setCourseApproval("T");
            ccp_uoc_repository.save(userOwnCourse);
        }

        return ResponseEntity.ok("과정 상태가 확인되었습니다. 승인 상태: " + userOwnCourse.getCourseApproval());
    }

    @GetMapping("/download/certificate/{courseId}")
    @Operation(summary = "수료증 다운로드", description = "수료증 다운로드 (courseApproval이 'T'인 경우에만)")
    public ResponseEntity<?> downloadCertificate(@PathVariable String courseId) {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();
        String userSessionId = auth.getPrincipal().toString();

        // 사용자 과정 및 승인 상태 확인
        UserOwnCourse userOwnCourse = validateCourseAndApproval(courseId, userSessionId);
        if (userOwnCourse == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("수료 상태가 아닙니다. 수료증을 다운로드할 수 없습니다.");
        }

        // courseId에 해당하는 courseTitle 가져오기
        Optional<Course> courseOpt = ccp_c_repository.findById(courseId);
        if (courseOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 과정을 찾을 수 없습니다.");
        }
        String courseTitle = courseOpt.get().getCourseTitle();

        // 수료증 경로 생성
        String filePath = "src/main/resources/static/Certificates/" + courseTitle;
        Path directoryPath = Paths.get(filePath);

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(directoryPath)) {
            // 디렉토리 내 첫 번째 파일 찾기
            Path fileToDownload = null;
            for (Path path : directoryStream) {
                if (Files.isRegularFile(path)) {
                    fileToDownload = path;
                    break;
                }
            }

            if (fileToDownload == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("수료증 파일을 찾을 수 없습니다.");
            }

            // MIME 타입 설정
            String mimeType = Files.probeContentType(fileToDownload);
            if (mimeType == null) {
                String fileExtension = fileToDownload.getFileName().toString().split("\\.")[1];
                switch (fileExtension.toLowerCase()) {
                    case "jpg":
                    case "jpeg":
                        mimeType = "image/jpeg";
                        break;
                    case "png":
                        mimeType = "image/png";
                        break;
                    case "gif":
                        mimeType = "image/gif";
                        break;
                    default:
                        mimeType = "application/octet-stream";
                }
            }

            // 파일 이름 유지하여 다운로드
            String fileName = fileToDownload.getFileName().toString();
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
            String contentDisposition = "attachment; filename=\"" + encodedFileName + "\"";

            return ResponseEntity.ok()
                    .header("Content-Disposition", contentDisposition)
                    .header("Content-Type", mimeType)
                    .header("Cache-Control", "no-cache, no-store, must-revalidate")
                    .header("Pragma", "no-cache")
                    .header("Expires", "0")
                    .body(Files.readAllBytes(fileToDownload));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 다운로드 중 오류가 발생했습니다.");
        }
    }
}
