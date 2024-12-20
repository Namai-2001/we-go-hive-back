package com.dev.restLms.sechan.subjectInfoDetailPage.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.entity.OfferedSubjects;
import com.dev.restLms.entity.UserOwnAssignment;
import com.dev.restLms.sechan.subjectInfoDetailPage.projection.SID_CourseCheck_Projection;
import com.dev.restLms.sechan.subjectInfoDetailPage.projection.SID_S_Projection;
import com.dev.restLms.sechan.subjectInfoDetailPage.projection.SID_U_Projection;
import com.dev.restLms.sechan.subjectInfoDetailPage.repository.SID_OS_Repository;
import com.dev.restLms.sechan.subjectInfoDetailPage.repository.SID_S_Repository;
import com.dev.restLms.sechan.subjectInfoDetailPage.repository.SID_UOA_Repository;
import com.dev.restLms.sechan.subjectInfoDetailPage.repository.SID_UOC_Repository;
import com.dev.restLms.sechan.subjectInfoDetailPage.repository.SID_U_Repository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "과목 상세 페이지", description = "개설된 특정 과목의 세부 정보를 조회")
public class SID_Controller {

    @Autowired
    private SID_OS_Repository sid_os_repository;

    @Autowired
    private SID_S_Repository sid_s_repository;

    @Autowired
    private SID_U_Repository sid_u_repository;

    @GetMapping("/subjectInfoDetail/{offeredSubjectsId}")
    @Operation(summary = "특정 개설 과목 조회", description = "개설이 완료된 특정 과목의 세부 정보를 조회합니다.")
    public Map<String, String> getSubjectDetails(@PathVariable String offeredSubjectsId) {
        // 1. 개설 과목(OfferedSubjects) 정보 조회
        Optional<OfferedSubjects> offeredSubjectOpt = sid_os_repository.findById(offeredSubjectsId);
        if (offeredSubjectOpt.isEmpty()) {
            throw new RuntimeException("해당 개설 과목 정보를 찾을 수 없습니다.");
        }

        OfferedSubjects offeredSubject = offeredSubjectOpt.get();

        // 2. 과목 상세 정보(SID_S_Projection) 조회
        SID_S_Projection subjectProjection = sid_s_repository.findFirstBySubjectId(offeredSubject.getSubjectId());
        if (subjectProjection == null) {
            throw new RuntimeException("해당 과목 정보를 찾을 수 없습니다.");
        }

        // 3. 강사 이름(SID_U_Projection) 조회
        SID_U_Projection teacherProjection = sid_u_repository.findBySessionId(offeredSubject.getTeacherSessionId());
        if (teacherProjection == null) {
            throw new RuntimeException("강사 정보를 찾을 수 없습니다.");
        }

        // 4. 데이터 맵핑
        Map<String, String> subjectDetails = new HashMap<>();
        subjectDetails.put("offeredSubjectsId", offeredSubject.getOfferedSubjectsId());
        subjectDetails.put("subjectName", subjectProjection.getSubjectName());
        subjectDetails.put("subjectPromotion", subjectProjection.getSubjectPromotion());
        subjectDetails.put("subjectImageLink", subjectProjection.getSubjectImageLink());
        subjectDetails.put("teacherName", teacherProjection.getUserName()); // 강사 이름 추가

        return subjectDetails;
    }

    @Autowired
    private SID_UOC_Repository sid_uoc_repository;

    @Autowired
    private SID_UOA_Repository sid_uoa_repository;

    @PostMapping("/subjectenroll/{userSessionId}/{offeredSubjectsId}")
    @Operation(summary = "수강 신청", description = "사용자가 특정 개설 과목을 수강 신청합니다.")
    public Map<String, String> applySubject(
            @PathVariable String userSessionId,
            @PathVariable String offeredSubjectsId) {

        Map<String, String> response = new HashMap<>();

        // 1. 개설 과목(OfferedSubjects) 정보 확인
        Optional<OfferedSubjects> offeredSubjectOpt = sid_os_repository.findById(offeredSubjectsId);
        if (offeredSubjectOpt.isEmpty()) {
            response.put("status", "fail");
            response.put("message", "해당 개설 과목이 존재하지 않습니다.");
            return response;
        }

        OfferedSubjects offeredSubject = offeredSubjectOpt.get();

        // 2. 사용자가 이미 등록한 과목 확인
        if (sid_uoa_repository.findByUserSessionIdAndOfferedSubjectsId(userSessionId, offeredSubjectsId).isPresent()) {
            response.put("status", "fail");
            response.put("message", "이미 신청한 과목입니다.");
            return response;
        }

        // 3. 사용자가 해당 과정에 등록했는지 확인
        List<SID_CourseCheck_Projection> userCourses = sid_uoc_repository.findBySessionId(userSessionId);
        for (SID_CourseCheck_Projection course : userCourses) {
            if (course.getCourseId().equals(offeredSubject.getCourseId())) {
                response.put("status", "fail");
                response.put("message", "이미 해당 과정에 등록되어 있는 상태입니다.");
                return response;
            }
        }

        // 4. 수강 신청 처리
        UserOwnAssignment newAssignment = UserOwnAssignment.builder()
                .userSessionId(userSessionId)
                .offeredSubjectsId(offeredSubjectsId)
                .build();

        sid_uoa_repository.save(newAssignment);
        response.put("status", "success");
        response.put("message", "수강 신청이 완료되었습니다.");
        return response;
    }
}
