package com.dev.restLms.sechan.teacherAssignment.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import org.springframework.web.bind.annotation.*;

import com.dev.restLms.entity.Assignment;
import com.dev.restLms.entity.OfferedSubjects;
import com.dev.restLms.entity.Subject;
import com.dev.restLms.sechan.teacherAssignment.projection.TA_A_Projection;
import com.dev.restLms.sechan.teacherAssignment.repository.TA_A_Repository;
import com.dev.restLms.sechan.teacherAssignment.repository.TA_OS_Repository;
import com.dev.restLms.sechan.teacherAssignment.repository.TA_S_Repository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/teacher/assignments")
@Tag(name = "TeacherAssignmentController", description = "강사 과제 관리 컨트롤러")
public class TA_Controller {

    @Autowired
    private TA_OS_Repository ta_os_repository;

    @Autowired
    private TA_A_Repository ta_a_repository;

    @Autowired
    private TA_S_Repository ta_s_repository;

    // 날짜 형식 변환 함수
    public static String convertTo8DigitDate(String dateString) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일");

        LocalDateTime dateTime = LocalDateTime.parse(dateString, inputFormatter);

        return dateTime.toLocalDate().format(outputFormatter);
    }

    // 과제 조회
    @GetMapping("/subjectAssignments")
    @Operation(summary = "과제 관리", description = "해당 과목에 포함된 과제를 조회")
    public ResponseEntity<?> getSubjectAssignments() {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();
        // 유저 세션아이디 보안 컨텍스트에서 가져오기
        String teacherSessionId = auth.getPrincipal().toString();
        List<OfferedSubjects> subjects = ta_os_repository.findByTeacherSessionId(teacherSessionId);

        if (!subjects.isEmpty()) {
            List<Map<String, Object>> responseList = new ArrayList<>();

            for (OfferedSubjects subject : subjects) {
                Optional<Subject> subjectOpt = ta_s_repository.findBySubjectId(subject.getSubjectId());
                String subjectName = subjectOpt.map(Subject::getSubjectName).orElse("Unknown");

                List<Assignment> assignments = ta_a_repository.findByOfferedSubjectsId(subject.getOfferedSubjectsId());
                List<Map<String, Object>> assignmentProjections = new ArrayList<>();

                for (Assignment assignment : assignments) {
                    Optional<TA_A_Projection> assignmentProjection = ta_a_repository
                            .findByAssignmentId(assignment.getAssignmentId());
                    assignmentProjection.ifPresent(projection -> {
                        Map<String, Object> assignmentData = new HashMap<>();
                        assignmentData.put("assignmentId", projection.getAssignmentId());
                        assignmentData.put("assignmentTitle", projection.getAssignmentTitle());
                        assignmentData.put("deadline", projection.getDeadline());
                        assignmentData.put("noticeNo", projection.getNoticeNo());
                        assignmentData.put("cutline", projection.getCutline());
                        assignmentData.put("assignmentContent", projection.getAssignmentContent());
                        assignmentProjections.add(assignmentData);
                    });
                }

                Map<String, Object> subjectData = new HashMap<>();
                subjectData.put("offeredSubjectsId", subject.getOfferedSubjectsId());
                subjectData.put("subjectName", subjectName);
                subjectData.put("assignments", assignmentProjections);

                responseList.add(subjectData);
            }

            return ResponseEntity.ok(responseList);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("표시할 항목이 존재하지 않습니다.");
    }

    // 과제 수정
    @PostMapping("/updateAssignment/{assignmentId}")
    @Operation(summary = "과제 수정", description = "과제의 세부 항목을 수정")
    public ResponseEntity<?> updateAssignment(
            @PathVariable String assignmentId,
            @RequestBody Map<String, Object> updatedData) {

        Optional<Assignment> assignmentOpt = ta_a_repository.findById(assignmentId);

        if (assignmentOpt.isPresent()) {
            Assignment assignment = assignmentOpt.get();

            // 업데이트할 데이터 설정
            if (updatedData.containsKey("assignmentTitle")) {
                assignment.setAssignmentTitle((String) updatedData.get("assignmentTitle"));
            }
            if (updatedData.containsKey("deadline")) {
                assignment.setDeadline((String) updatedData.get("deadline"));
            }
            if (updatedData.containsKey("noticeNo")) {
                assignment.setNoticeNo((String) updatedData.get("noticeNo"));
            }
            if (updatedData.containsKey("cutline")) {
                assignment.setCutline((String) updatedData.get("cutline"));
            }
            if (updatedData.containsKey("assignmentContent")) {
                assignment.setAssignmentContent((String) updatedData.get("assignmentContent"));
            }

            // 저장
            ta_a_repository.save(assignment);

            return ResponseEntity.ok("과제가 성공적으로 업데이트되었습니다.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 과제를 찾을 수 없습니다.");
    }

    // 과제 삭제
    @PostMapping("/deleteAssignment/{assignmentId}")
    @Operation(summary = "과제 삭제", description = "특정 과제를 삭제")
    public ResponseEntity<?> deleteAssignment(@PathVariable String assignmentId) {
        Optional<Assignment> assignmentOpt = ta_a_repository.findById(assignmentId);

        if (assignmentOpt.isPresent()) {
            ta_a_repository.deleteById(assignmentId);
            return ResponseEntity.ok("과제가 성공적으로 삭제되었습니다.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 과제를 찾을 수 없습니다.");
    }

    @GetMapping("/createAssignmentPage/{offeredSubjectsId}/{subjectName}")
    @Operation(summary = "과제 등록 페이지", description = "과목 ID와 이름을 받아 과제 등록 페이지")
    public ResponseEntity<?> createAssignmentPage(
            @PathVariable("offeredSubjectsId") String offeredSubjectsId,
            @PathVariable("subjectName") String subjectName) {

        Map<String, Object> response = new HashMap<>();
        response.put("offeredSubjectsId", offeredSubjectsId);
        response.put("subjectName", subjectName);

        return ResponseEntity.ok(response);
    }

    // 과제 등록
    @PostMapping("/createAssignment")
    @Operation(summary = "과제 등록", description = "선택한 과목에 새로운 과제를 등록")
    public ResponseEntity<?> createAssignment(@RequestBody Map<String, Object> assignmentData) {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();
        String teacherSessionId = auth.getPrincipal().toString();

        String offeredSubjectsId = (String) assignmentData.get("offeredSubjectsId");
        String assignmentTitle = (String) assignmentData.get("assignmentTitle");
        String deadlineRaw = (String) assignmentData.get("deadline");
        String noticeNoRaw = (String) assignmentData.get("noticeNo");
        String cutline = (String) assignmentData.get("cutline");
        String assignmentContent = (String) assignmentData.get("assignmentContent");

        // 날짜 변환
        String deadline = convertTo8DigitDate(deadlineRaw);
        String noticeNo = convertTo8DigitDate(noticeNoRaw);

        // 과제 생성
        Assignment assignment = new Assignment();
        assignment.setOfferedSubjectsId(offeredSubjectsId);
        assignment.setAssignmentTitle(assignmentTitle);
        assignment.setDeadline(deadline);
        assignment.setNoticeNo(noticeNo);
        assignment.setCutline(cutline);
        assignment.setAssignmentContent(assignmentContent);
        assignment.setTeacherSessionId(teacherSessionId); 

        // 저장
        ta_a_repository.save(assignment);

        return ResponseEntity.ok("과제가 성공적으로 등록되었습니다.");
    }
}