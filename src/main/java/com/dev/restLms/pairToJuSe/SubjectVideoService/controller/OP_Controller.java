package com.dev.restLms.pairToJuSe.SubjectVideoService.controller;

import java.util.ArrayList;
import java.util.HashMap;
// import java.util.HashMap;
import java.util.List;
// import java.util.Map;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.entity.CourseOwnSubject;
import com.dev.restLms.entity.OfferedSubjects;
import com.dev.restLms.entity.Subject;
import com.dev.restLms.entity.User;
import com.dev.restLms.entity.UserOwnCourse;
import com.dev.restLms.pairToJuSe.SubjectVideoService.projection.C_Projection;
import com.dev.restLms.pairToJuSe.SubjectVideoService.projection.S_Projection;
import com.dev.restLms.pairToJuSe.SubjectVideoService.projection.U_Projection;
import com.dev.restLms.pairToJuSe.SubjectVideoService.repository.COS_Repository;
// import com.dev.restLms.pairToJuSe.SubjectVideoService.projection.S_Projection;
// import com.dev.restLms.pairToJuSe.SubjectVideoService.repository.COS_Repository;
import com.dev.restLms.pairToJuSe.SubjectVideoService.repository.C_Repository;
import com.dev.restLms.pairToJuSe.SubjectVideoService.repository.OS2_Repository;
// import com.dev.restLms.pairToJuSe.SubjectVideoService.repository.OS_Repository;
import com.dev.restLms.pairToJuSe.SubjectVideoService.repository.S_Repository;
// import com.dev.restLms.pairToJuSe.SubjectVideoService.repository.UOA_Repository;
import com.dev.restLms.pairToJuSe.SubjectVideoService.repository.U_Repository;
import com.dev.restLms.pairToJuSe.SubjectVideoService.repository.UOC_Repository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
// import org.springframework.web.bind.annotation.RequestParam;

@RestController
@Tag(name = "나의 강의실 과목 목록 페이지", description = "내가 신청한 과목 목록을 보여주는 페이지")
public class OP_Controller {
    @Autowired
    private C_Repository c_repository;

    // @Autowired
    // private OS_Repository os_repository;

    // @Autowired
    // private UOA_Repository uoa_repository;

    // @Autowireds
    // private S_Repository s_repository;

    @GetMapping("/courses/{courseId}")
    @Operation(summary = "과정 이름 조회", description = "주어진 과정 ID에 따라 과정 이름 조회")
    public List<C_Projection> getCourseTitle(
            @Parameter(description = "과정 ID", required = true) @PathVariable String courseId) {
        return c_repository.findByCourseId(courseId);
    }

    @Autowired
    private COS_Repository cos_repository;

    @Autowired
    private S_Repository subject_repository;

    @GetMapping("/getSubjectList/{courseId}")
    @Operation(summary = "과정별 과목 조회", description = "과정 ID를 기반으로 모든 과목 정보를 조회합니다.")
    public List<S_Projection> getSubjectList(
            @Parameter(description = "과정 ID", required = true) @PathVariable String courseId) {

        List<CourseOwnSubject> courseSubjects = cos_repository.findByCourseId(courseId);
        List<S_Projection> subjectDetails = new ArrayList<>();

        for (CourseOwnSubject courseSubject : courseSubjects) {
            Subject subject = subject_repository.findById(courseSubject.getSubjectId()).orElse(null);
            if (subject != null) {
                S_Projection projection = new S_Projection() {
                    @Override
                    public String getSubjectName() {
                        return subject.getSubjectName();
                    }

                    @Override
                    public String getSubjectDesc() {
                        return subject.getSubjectDesc();
                    }

                    @Override
                    public String getSubjectCategory() {
                        return subject.getSubjectCategory();
                    }

                    @Override
                    public String getSubjectImageLink() {
                        return subject.getSubjectImageLink();
                    }
                };
                subjectDetails.add(projection);
            }
        }

        return subjectDetails;
    }

    @Autowired
    private OS2_Repository os2_repository;

    @Autowired
    private U_Repository u_repository;

    @GetMapping("/getTeacherName/{courseId}")
    @Operation(summary = "과정의 과목마다 강사 이름 조회", description = "과정 ID에 따라 강사 이름 출력")
    public List<String> getTeacherName(
            @Parameter(description = "과정 ID", required = true) @PathVariable String courseId) {
        List<OfferedSubjects> offeredSubjects = os2_repository.findByCourseId(courseId);

        List<String> teacherSessionIds = new ArrayList<>();
        for (OfferedSubjects subject : offeredSubjects) {
            teacherSessionIds.add(subject.getTeacherSessionId());
        }

        List<U_Projection> teacherProjections = u_repository.findBySessionIdIn(teacherSessionIds);

        List<String> teacherNames = new ArrayList<>();
        for (U_Projection projection : teacherProjections) {
            teacherNames.add(projection.getUserName());
        }

        return teacherNames;
    }

    @GetMapping("/courses/{courseId}/info")
    @Operation(summary = "과정 및 과목 상세 조회", description = "과정 ID를 기반으로 각 과목의 상세 정보를 반환합니다.")
    public List<Map<String, String>> getCourseAndSubjectInfo(
            @Parameter(description = "과정 ID", required = true) @PathVariable String courseId) {

        // 과정 이름 조회
        List<C_Projection> courseTitleProjections = c_repository.findByCourseId(courseId);
        String courseTitle = courseTitleProjections.isEmpty() ? "Unknown Course"
                : courseTitleProjections.get(0).getCourseTitle();

        // 개설 과목 목록 조회
        List<OfferedSubjects> offeredSubjects = os2_repository.findByCourseId(courseId);

        // 최종 출력 리스트
        List<Map<String, String>> result = new ArrayList<>();

        for (OfferedSubjects os : offeredSubjects) {
            Map<String, String> subjectDetails = new HashMap<>();

            // 과목 정보 조회
            Subject subject = subject_repository.findById(os.getSubjectId()).orElse(null);
            if (subject != null) {
                subjectDetails.put("courseTitle", courseTitle);
                subjectDetails.put("subjectName", subject.getSubjectName());
                subjectDetails.put("subjectDesc", subject.getSubjectDesc());
                subjectDetails.put("subjectImage", subject.getSubjectImageLink());
                subjectDetails.put("subjectCategory", subject.getSubjectCategory());
            }

            // 강사 이름 조회
            User teacher = u_repository.findById(os.getTeacherSessionId()).orElse(null);
            subjectDetails.put("teacherName", teacher != null ? teacher.getUserName() : "강사 정보 없음");

            result.add(subjectDetails);
        }

        return result;
    }

    @Autowired
    private UOC_Repository uoc_repository;

    @GetMapping("/{sessionId}/coursesinfo")
    @Operation(summary = "사용자의 과정 및 과목 상세 조회", description = "사용자 ID(sessionId)를 기반으로 각 과정의 과목 상세 정보를 반환합니다.")
    public List<Map<String, String>> getUserCoursesAndSubjectsInfo(
            @Parameter(description = "사용자 ID", required = true) @PathVariable String sessionId) {

        // 사용자별 과정 목록 조회
        List<UserOwnCourse> userCourses = uoc_repository.findBySessionId(sessionId);

        // 최종 결과 리스트
        List<Map<String, String>> result = new ArrayList<>();

        for (UserOwnCourse userCourse : userCourses) {
            if(!userCourse.getCourseApproval().equals("T")){
                String courseId = userCourse.getCourseId();

            // 과정 이름 조회
            List<C_Projection> courseTitleProjections = c_repository.findByCourseId(courseId);
            String courseTitle = courseTitleProjections.isEmpty() ? "Unknown Course"
                    : courseTitleProjections.get(0).getCourseTitle();

            // 개설 과목 목록 조회
            List<OfferedSubjects> offeredSubjects = os2_repository.findByCourseId(courseId);

            for (OfferedSubjects os : offeredSubjects) {
                Map<String, String> subjectDetails = new HashMap<>();

                // 과목 정보 조회
                Subject subject = subject_repository.findById(os.getSubjectId()).orElse(null);
                if (subject != null) {
                    subjectDetails.put("courseTitle", courseTitle);
                    subjectDetails.put("subjectName", subject.getSubjectName());
                    subjectDetails.put("subjectDesc", subject.getSubjectDesc());
                    subjectDetails.put("subjectImage", subject.getSubjectImageLink());
                    subjectDetails.put("subjectCategory", subject.getSubjectCategory());
                }

                // 강사 이름 조회
                User teacher = u_repository.findById(os.getTeacherSessionId()).orElse(null);
                subjectDetails.put("teacherName", teacher != null ? teacher.getUserName() : "강사 정보 없음");

                // 개설 과목 ID 추가
                subjectDetails.put("offeredSubjectsId", os.getOfferedSubjectsId());

                result.add(subjectDetails);
            }
            }
        }
        return result;
    }
}
