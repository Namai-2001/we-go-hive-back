package com.dev.restLms.sechan.courseCompletePage.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.entity.UserOwnCourse;
import com.dev.restLms.sechan.courseCompletePage.projection.CCP_UOSV_Projection;
import com.dev.restLms.sechan.courseCompletePage.repository.CCP_UOC_Repository;
import com.dev.restLms.sechan.courseCompletePage.repository.CCP_UOSV_Repository;

@RestController
@RequestMapping("/courseComplete")
public class CCP_Controller {

    @Autowired
    private CCP_UOC_Repository ccp_uoc_repository;

    @Autowired
    private CCP_UOSV_Repository ccp_uosv_repository;

    @GetMapping("/check-approval/{courseId}/{sessionId}")
    public ResponseEntity<String> checkAndAutoUpdateCourseApproval(
            @PathVariable String courseId,
            @PathVariable String sessionId) {

        // 사용자 과정 조회
        UserOwnCourse userOwnCourse = ccp_uoc_repository
                .findByCourseIdAndSessionId(courseId, sessionId)
                .orElse(null);

        if (userOwnCourse == null) {
            return ResponseEntity.badRequest().body("해당 과정을 찾을 수 없습니다.");
        }

        // 과정에 포함된 개설 과목 ID 조회
        List<String> offeredSubjectsIds = new ArrayList<>();
        List<CCP_UOSV_Projection> allSubjectVideos = ccp_uosv_repository
                .findByUosvOfferedSubjectsIdAndUosvSessionId(courseId, sessionId);
        for (CCP_UOSV_Projection video : allSubjectVideos) {
            if (!offeredSubjectsIds.contains(video.getUosvOfferedSubjectsId())) {
                offeredSubjectsIds.add(video.getUosvOfferedSubjectsId());
            }
        }

        // 각 개설 과목의 모든 영상 progress 확인
        for (String offeredSubjectsId : offeredSubjectsIds) {
            List<CCP_UOSV_Projection> subjectVideos = ccp_uosv_repository
                    .findByUosvOfferedSubjectsIdAndUosvSessionId(offeredSubjectsId, sessionId);

            boolean allProgress100 = true;
            for (CCP_UOSV_Projection video : subjectVideos) {
                if (Integer.parseInt(video.getProgress()) < 100) {
                    allProgress100 = false;
                    break;
                }
            }

            if (!allProgress100) {
                return ResponseEntity.ok("해당 과정에 포함된 영상들의 진행률이 100% 미만입니다.");
            }
        }

        // 조건 충족 시 courseApproval 자동 업데이트
        if (!"T".equals(userOwnCourse.getCourseApproval())) {
            userOwnCourse.setCourseApproval("T");
            ccp_uoc_repository.save(userOwnCourse);
        }

        return ResponseEntity.ok("과정 상태가 확인되었습니다. 승인 상태: " + userOwnCourse.getCourseApproval());
    }
}
