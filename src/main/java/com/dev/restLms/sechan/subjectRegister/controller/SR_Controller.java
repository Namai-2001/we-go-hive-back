package com.dev.restLms.sechan.subjectRegister.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.entity.Subject;
import com.dev.restLms.entity.UserOwnAssignment;
import com.dev.restLms.sechan.subjectRegister.projection.SR_S_Projection;
import com.dev.restLms.sechan.subjectRegister.projection.SR_U_Projection;
import com.dev.restLms.sechan.subjectRegister.repository.SR_OS_Repository;
import com.dev.restLms.sechan.subjectRegister.repository.SR_S_Repository;
import com.dev.restLms.sechan.subjectRegister.repository.SR_UOA_Repository;
import com.dev.restLms.sechan.subjectRegister.repository.SR_U_Repository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/teacher/students")
@Tag(name = "SubjectRegisterController", description = "강사의 과목별 수강 학생 조회 컨트롤러")
public class SR_Controller {

    @Autowired
    private SR_UOA_Repository sr_uoa_repository;

    @Autowired
    private SR_OS_Repository sr_os_repository;

    @Autowired
    private SR_S_Repository sr_s_repository;

    @Autowired
    private SR_U_Repository sr_u_repository;

    @GetMapping("/enrolled")
    @Operation(summary = "수강 학생 조회", description = "강사가 개설한 과목별 수강 학생을 조회")
    public ResponseEntity<?> getEnrolledStudents() {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();
        // 유저 세션아이디 보안 컨텍스트에서 가져오기
        String teacherSessionId = auth.getPrincipal().toString();
        // 강사가 개설한 과목 조회
        List<SR_S_Projection> subjects = sr_os_repository.findByTeacherSessionId(teacherSessionId);

        List<Map<String, Object>> responseList = new ArrayList<>();

        for (SR_S_Projection subjectProjection : subjects) {
            String subjectId = subjectProjection.getSubjectId();
            String offeredSubjectsId = subjectProjection.getOfferedSubjectsId();

            String subjectName = "";
            Subject subject = sr_s_repository.findBySubjectId(subjectId).orElse(null);
            if (subject != null) {
                subjectName = subject.getSubjectName();
            }

            // 해당 과목의 학생 ID 조회
            List<UserOwnAssignment> userAssignments = sr_uoa_repository.findByOfferedSubjectsId(offeredSubjectsId);
            List<String> userSessionIds = new ArrayList<>();
            for (UserOwnAssignment userAssignment : userAssignments) {
                userSessionIds.add(userAssignment.getUserSessionId());
            }

            // 학생 세션 ID로 사용자 정보 조회
            List<SR_U_Projection> students = sr_u_repository.findBySessionIdIn(userSessionIds);

            List<Map<String, String>> studentData = new ArrayList<>();
            for (SR_U_Projection student : students) {
                Map<String, String> studentInfo = new HashMap<>();
                studentInfo.put("userName", student.getUserName());
                studentInfo.put("userBirth", student.getUserBirth());
                studentInfo.put("userEmail", student.getUserEmail());
                studentData.add(studentInfo);
            }

            Map<String, Object> subjectData = new HashMap<>();
            subjectData.put("subjectName", subjectName);
            subjectData.put("offeredSubjectsId", offeredSubjectsId); 
            subjectData.put("students", studentData);

            responseList.add(subjectData);
        }

        return ResponseEntity.ok(responseList);
    }
}
