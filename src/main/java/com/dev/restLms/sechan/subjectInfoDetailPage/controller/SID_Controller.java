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

import com.dev.restLms.entity.CourseOwnSubject;
import com.dev.restLms.entity.OfferedSubjects;
import com.dev.restLms.entity.UserOwnAssignment;
import com.dev.restLms.sechan.subjectInfoDetailPage.projection.SID_S_Projection;
import com.dev.restLms.sechan.subjectInfoDetailPage.projection.SID_U_Projection;
import com.dev.restLms.sechan.subjectInfoDetailPage.repository.SID_COS_Repository;
import com.dev.restLms.sechan.subjectInfoDetailPage.repository.SID_OS2_Repository;
import com.dev.restLms.sechan.subjectInfoDetailPage.repository.SID_OS_Repository;
import com.dev.restLms.sechan.subjectInfoDetailPage.repository.SID_S_Repository;
import com.dev.restLms.sechan.subjectInfoDetailPage.repository.SID_UOA_Repository;
import com.dev.restLms.sechan.subjectInfoDetailPage.repository.SID_U_Repository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "과목 상세페이지", description = "해당 과목의 상세 페이지와 수강 신청 페이지")
public class SID_Controller {

    @Autowired
    SID_OS_Repository sid_os_repository;

    @Autowired
    SID_S_Repository sid_s_repository;

    @Autowired
    SID_U_Repository sid_u_repository;

    @GetMapping("/subjectInfoDetail/{subjectId}")
    @Operation(summary = "특정 개설 과목 조회", description = "개설이 완료된 특정 과목의 세부 정보를 조회합니다")
    public Map<String, String> getSubjectDetails(@PathVariable String subjectId) {
        // OfferredSubjects에서 데이터 조회
        Optional<OfferedSubjects> offeredSubjectOpt = sid_os_repository.findBySubjectId(subjectId);

        if (offeredSubjectOpt.isEmpty()) {
            throw new RuntimeException("개설된 과목 정보를 찾을 수 없습니다");
        }

        OfferedSubjects offeredSubject = offeredSubjectOpt.get();

        // Subject 데이터 조회
        SID_S_Projection subjectProjection = sid_s_repository.findFirstBySubjectId(subjectId);

        if (subjectProjection == null) {
            throw new RuntimeException("과목 정보를 찾을 수 없습니다");
        }

        // 강사 데이터 조회
        SID_U_Projection teacherProjection = sid_u_repository.findBySessionId(offeredSubject.getTeacherSessionId());

        if (teacherProjection == null) {
            throw new RuntimeException("강사 정보를 찾을 수 없습니다");
        }

        // 결과 맵핑
        Map<String, String> subjectDetails = new HashMap<>();
        subjectDetails.put("subjectName", subjectProjection.getSubjectName());
        subjectDetails.put("subjectPromotion", subjectProjection.getSubjectPromotion());
        subjectDetails.put("subjectImageLink", subjectProjection.getSubjectImageLink());
        subjectDetails.put("teacherName", teacherProjection.getUserName());

        return subjectDetails;
    }

    @Autowired
    private SID_OS2_Repository sid_os2_repository;

    @Autowired
    private SID_UOA_Repository uoa_os_repository;

    @Autowired
    private SID_COS_Repository sid_cos_repository;

    @PostMapping("/subjectenroll/{userSessionId}/{subjectId}")
    public Map<String, String> applySubject(
        @PathVariable String userSessionId,
        @PathVariable String subjectId
    ) {
        Map<String, String> response = new HashMap<>();
        //1. 해당 과목의 개설 정보 조회
        OfferedSubjects offeredSubject = sid_os2_repository.findBySubjectId(subjectId);

        if(offeredSubject == null) {
            response.put("status", "fail");
            response.put("message", "해당 과목이 존재하지 않습니다");
            return response;
        }

        //2. 과정 정보 확인
        if(offeredSubject.getCourseId() == null) {
            //과정 등록이 안된 경우 수강 신청 처리
            if (!uoa_os_repository.existsByUserSessionIdAndOfferedSubjectsId(userSessionId, offeredSubject.getOfferedSubjectsId())) {
                UserOwnAssignment newEnroll = new UserOwnAssignment();
                newEnroll.setUserSessionId(userSessionId);
                newEnroll.setOfferedSubjectsId(offeredSubject.getOfferedSubjectsId());
                newEnroll.setSubjectAcceptCategory("T");

                uoa_os_repository.save(newEnroll);
                response.put("status", "success");
                response.put("message", "수강 신청이 완료되었습니다");
                return response;
            } else {
                response.put("status", "fail");
                response.put("message", "이미 신청된 과목입니다");
                return response;
            }
        }

        //3. 과정 등록된 경우, 동일한 과정 내 신청 여부 확인
        // List<OfferredSubjects> sameCourseSubjects = sid_os2_repository.findByCourseId(offeredSubject.getCourseId());
        
        // for (OfferredSubjects courseSubjects : sameCourseSubjects) {
        //     if(uoa_os_repository.existsByUserSessionIdAndOffredSubjectsId(userSessionId, courseSubjects.getOffredSubjectsId())) {
        //         response.put("status", "fail");
        //         response.put("messsage", "이미 동일한 과정의 과목이 신청되었습니다");
        //         return response;
        //     }
        // }


        List<CourseOwnSubject> courseSubjects = sid_cos_repository.findByCourseId(offeredSubject.getCourseId());
        for (CourseOwnSubject courseSubject : courseSubjects) {
            if (courseSubject.getSubjectId().equals(subjectId)) {
                response.put("status", "fail");
                response.put("message", "이미 동일한 과정에 포함된 과목입니다");
                return response;
            }
        }

        //4. 신청 가능
        UserOwnAssignment newEnroll = new UserOwnAssignment();
        newEnroll.setUserSessionId(userSessionId);
        newEnroll.setOfferedSubjectsId(offeredSubject.getOfferedSubjectsId());
        newEnroll.setSubjectAcceptCategory("T");

        uoa_os_repository.save(newEnroll);
        response.put("status", "success");
        response.put("message", "수강 신청이 완료되었습니다");
        
        return response;
    }
}

