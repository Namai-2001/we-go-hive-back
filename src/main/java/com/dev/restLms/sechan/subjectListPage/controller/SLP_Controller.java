package com.dev.restLms.sechan.subjectListPage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.entity.FileInfo;
import com.dev.restLms.entity.OfferedSubjects;
import com.dev.restLms.entity.Subject;
import com.dev.restLms.entity.User;
import com.dev.restLms.sechan.subjectListPage.projection.S_Projection;
import com.dev.restLms.sechan.subjectListPage.projection.SLP_U_Projection;
import com.dev.restLms.sechan.subjectListPage.repository.SLP_F_Repository;
import com.dev.restLms.sechan.subjectListPage.repository.SLP_OS_Repository;
import com.dev.restLms.sechan.subjectListPage.repository.SLP_S_Repository;
import com.dev.restLms.sechan.subjectListPage.repository.SLP_U_Repository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@Tag(name = "과목 조회", description = "개설된 전체 과목 조회")
public class SLP_Controller {

    @Autowired
    private SLP_OS_Repository slp_os_repository;

    @Autowired
    private SLP_S_Repository slp_s_repository;

    @Autowired
    private SLP_F_Repository slp_f_repository;

    @GetMapping("/subjectInfo")
    @Operation(summary = "전체 개설 과목 조회", description = "개설이 완료된 전체 과목을 조회합니다.")
    public List<Map<String, String>> getAllSubjects() {

        // OfferredSubjects에서 모든 개설 과목 조회
        List<OfferedSubjects> offeredSubjectsList = slp_os_repository.findAll();

        // OfferredSubjects에서 subjectId 목록 추출
        List<String> subjectIds = new ArrayList<>();
        for (OfferedSubjects os : offeredSubjectsList) {
            subjectIds.add(os.getSubjectId());
        }

        // Subject 데이터 조회 (S_Projection 활용)
        List<S_Projection> projections = slp_s_repository.findBySubjectIdIn(subjectIds);

        // 결과 생성
        List<Map<String, String>> result = new ArrayList<>();
        for (S_Projection projection : projections) {
            Map<String, String> subjectInfo = new HashMap<>();
            subjectInfo.put("subjectId", projection.getSubjectId());
            subjectInfo.put("subjectName", projection.getSubjectName());
            subjectInfo.put("subjectDesc", projection.getSubjectDesc());
            subjectInfo.put("subjectImage", projection.getSubjectImageLink());
            result.add(subjectInfo);
        }

        return result;
    }

    @Autowired
    private SLP_U_Repository slp_u_repository;

    @GetMapping("/getTeacherName")
    @Operation(summary = "개설 과목마다 강사 이름 조회", description = "개설된 과목의 강사 이름을 볼 수 있음")
    public List<Map<String, String>> getTeachersForAllSubjects() {

        // OfferredSubjects에서 모든 개설 과목 조회
        List<OfferedSubjects> offeredSubjectsList = slp_os_repository.findAll();

        // OfferredSubjects에서 teacherSessionId 목록 추출
        List<String> teacherSessionIds = new ArrayList<>();
        for (OfferedSubjects os : offeredSubjectsList) {
            teacherSessionIds.add(os.getTeacherSessionId());
        }

        // User 데이터 조회 (SLP_U_Projection 활용)
        List<SLP_U_Projection> teacherProjections = slp_u_repository.findBySessionIdIn(teacherSessionIds);

        // 결과 생성
        List<Map<String, String>> result = new ArrayList<>();
        for (int i = 0; i < offeredSubjectsList.size(); i++) {
            Map<String, String> teacherDetails = new HashMap<>();
            teacherDetails.put("subjectId", offeredSubjectsList.get(i).getSubjectId());
            teacherDetails.put("teacherName", teacherProjections.get(i).getUserName());
            result.add(teacherDetails);
        }

        return result;
    }

    @GetMapping("/getAllSubjectInfo")
    @Operation(summary = "전체 개설 과목 및 강사 조회", description = "개설된 모든 과목의 상세 정보와 강사 이름을 조회합니다")
    public Map<String, Object> getAllSubjectInfo(
            @RequestParam(defaultValue = "") String searchParam,
            @RequestParam(defaultValue = "ALL") String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size) {

        Pageable pageable = PageRequest.of(page, size);

        // 결과 리스트 초기화
        List<Map<String, String>> result = new ArrayList<>();

        // 검색 조건 처리
        List<String> subjectIds = new ArrayList<>();
        List<String> teacherSessionIds = new ArrayList<>();

        // 검색어가 비어있지 않은 경우
        if (!searchParam.isBlank()) {
            if (type.equalsIgnoreCase("ALL") || type.equalsIgnoreCase("SUBJECTNAME")) {
                // SubjectName 필드에서 검색어에 해당하는 과목 ID 조회
                List<Subject> searchSubjects = slp_s_repository.findBySubjectNameContaining(searchParam);
                for (Subject subject : searchSubjects) {
                    subjectIds.add(subject.getSubjectId());
                }
            }

            if (type.equalsIgnoreCase("ALL") || type.equalsIgnoreCase("TEACHERNAME")) {
                // UserName 필드에서 검색어에 해당하는 강사 ID 조회
                List<User> searchUsers = slp_u_repository.findByUserNameContaining(searchParam);
                for (User user : searchUsers) {
                    teacherSessionIds.add(user.getSessionId());
                }
            }
        }

        Page<OfferedSubjects> offeredSubjectsPage;

        // 검색 조건에 따른 필터링
        if (type.equalsIgnoreCase("ALL")) {
            if (subjectIds.isEmpty() && teacherSessionIds.isEmpty()) {
                // 검색어가 없을 경우, courseId가 "individual-subjects"인 모든 OfferedSubjects 조회
                offeredSubjectsPage = slp_os_repository.findByCourseId("individual-subjects", pageable);
            } else {
                // 과목 ID와 강사 ID 모두 포함된 검색 조건
                offeredSubjectsPage = slp_os_repository.findByCourseIdAndSubjectIdInOrTeacherSessionIdIn(
                        "individual-subjects", subjectIds, teacherSessionIds, pageable);
            }
        } else if (type.equalsIgnoreCase("SUBJECTNAME")) {
            offeredSubjectsPage = slp_os_repository.findByCourseIdAndSubjectIdIn(
                    "individual-subjects", subjectIds, pageable);
        } else if (type.equalsIgnoreCase("TEACHERNAME")) {
            offeredSubjectsPage = slp_os_repository.findByCourseIdAndTeacherSessionIdIn(
                    "individual-subjects", teacherSessionIds, pageable);
        } else {
            // 기본 값
            offeredSubjectsPage = slp_os_repository.findByCourseId("individual-subjects", pageable);
        }

        // OfferedSubjects 데이터 매핑
        for (OfferedSubjects os : offeredSubjectsPage) {
            Map<String, String> details = new HashMap<>();
            details.put("offeredSubjectsId", os.getOfferedSubjectsId());

            // 과목 정보 조회
            Subject subject = slp_s_repository.findById(os.getSubjectId()).orElse(null);
            if (subject != null) {
                details.put("subjectName", subject.getSubjectName());
                details.put("subjectDesc", subject.getSubjectDesc());
                details.put("subjectImage", subject.getSubjectImageLink());
            } else {
                details.put("subjectName", "과목 이름 없음");
                details.put("subjectDesc", "과목 설명 없음");
                details.put("subjectImage", "이미지 없음");
            }

            // 강사 정보 조회
            User teacher = slp_u_repository.findById(os.getTeacherSessionId()).orElse(null);
            if (teacher != null) {
                details.put("teacherName", teacher.getUserName());
            } else {
                details.put("teacherName", "강사 정보 없음");
            }

            // 결과 추가
            result.add(details);
        }

        // 페이지 정보와 결과 리스트를 함께 반환
        Map<String, Object> response = new HashMap<>();
        response.put("currentPage", offeredSubjectsPage.getNumber());
        response.put("totalItems", offeredSubjectsPage.getTotalElements());
        response.put("totalPages", offeredSubjectsPage.getTotalPages());
        response.put("subjects", result);

        return response;
    }

    // 이미지 반환
    @GetMapping("/slp/images/{fileNo:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileNo) {
        try {
            Optional<FileInfo> fileInfoOptional = slp_f_repository.findByFileNo(fileNo);
            if (!fileInfoOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            FileInfo fileInfo = fileInfoOptional.get();
            Path filePath = Paths.get(fileInfo.getFilePath() + fileInfo.getEncFileNm());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG) // 이미지 형식에 맞게 설정
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

}
