package com.dev.restLms.sechan.teacherSubjectRegister.controller;

// import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
// import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
// import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dev.restLms.entity.CourseOwnSubject;
import com.dev.restLms.entity.FileInfo;
import com.dev.restLms.entity.PermissionGroup;
import com.dev.restLms.entity.Subject;
import com.dev.restLms.entity.UserOwnPermissionGroup;
// import com.dev.restLms.hyeon.course.repository.SubjectRepository;
import com.dev.restLms.sechan.teacherSubjectRegister.repository.TSR_COS2_Repository;
import com.dev.restLms.sechan.teacherSubjectRegister.repository.TSR_COS_Repository;
import com.dev.restLms.sechan.teacherSubjectRegister.repository.TSR_File_Repository;
import com.dev.restLms.sechan.teacherSubjectRegister.repository.TSR_PGR_Repository;
import com.dev.restLms.sechan.teacherSubjectRegister.repository.TSR_S_Repository;
import com.dev.restLms.sechan.teacherSubjectRegister.repository.TSR_UOPGR_Repository;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/teacher/subject")
@Tag(name = "SubjectController", description = "강사의 과목 신청 컨트롤러")
public class TSR_Controller {

    @Autowired
    private TSR_S_Repository tsr_s_repository;

    @Autowired
    private TSR_COS_Repository tsr_cos_repository;

    @Autowired
    private TSR_File_Repository fileRepo;

    @Autowired
    private TSR_COS2_Repository tsr_cos2_repository;

    @Autowired
    private TSR_PGR_Repository tsr_pgr_repository;

    @Autowired
    private TSR_UOPGR_Repository tsr_uopgr_repository;

    private static final String ROOT_DIR = "src/main/resources/static/";
    private static final String UPLOAD_DIR = "SubjectImage/";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB (바이트 단위)

    // public TSR_Controller(TSR_File_Repository fileRepo, TSR_S_Repository
    // tsr_s_repository,
    // TSR_COS_Repository tsr_cos_repository) {
    // this.fileRepo = fileRepo;
    // this.tsr_s_repository = tsr_s_repository;
    // this.tsr_cos_repository = tsr_cos_repository;
    // }

    // 파일 저장 메서드
    private Map<String, Object> saveFile(MultipartFile file, String subjectName) throws Exception {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";

        if (originalFilename != null) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 고유 파일명 생성성
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        // 저장 경로로
        Path path = Paths.get(ROOT_DIR + UPLOAD_DIR + subjectName + "/" + uniqueFileName);

        // 파일이 존재하지 않으면 생성성
        Files.createDirectories(path.getParent());

        // 파일저장
        byte[] bytes = file.getBytes();
        Files.write(path, bytes);

        Map<String, Object> result = new HashMap<>();
        result.put("path", path);
        result.put("uniqueFileName", uniqueFileName);

        return result;
    }

    // 과목 신청
    @PostMapping("/apply")
    public ResponseEntity<?> applySubject(
            @RequestPart("file") MultipartFile file,
            @RequestPart("subjectData") Map<String, String> requestData) throws Exception {

        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 제공되지 않았습니다.");
        }
        if (requestData == null || requestData.isEmpty()) {
            return ResponseEntity.badRequest().body("과목 데이터가 제공되지 않았습니다.");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            return ResponseEntity.badRequest().body("파일 크기가 제한을 초과했습니다.");
        }

        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();
        String teacherSessionId = auth.getPrincipal().toString();

        String subjectName = requestData.get("subjectName");
        String subjectDesc = requestData.get("subjectDesc");
        String subjectCategory = requestData.get("subjectCategory");
        String subjectPromotion = requestData.get("subjectPromotion");
        String courseId = "individual-subjects";

        // 권한명이 INDIV_OFFICER인 PermissionGroup의 UUID 조회
        Optional<PermissionGroup> permissionGroupOptional = tsr_pgr_repository
                .findByPermissionName("INDIV_OFFICER");

        if (permissionGroupOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("권한명이 INDIV_OFFICER인 그룹을 찾을 수 없습니다.");
        }

        String permissionGroupUuid = permissionGroupOptional.get().getPermissionGroupUuid();

        // UserOwnPermissionGroup에서 권한 그룹 UUID를 가진 사용자의 sessionId 조회
        Optional<UserOwnPermissionGroup> userOwnPermissionGroupOptional = tsr_uopgr_repository
                .findByPermissionGroupUuid2(permissionGroupUuid);

        if (userOwnPermissionGroupOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("권한명이 INDIV_OFFICER인 사용자를 찾을 수 없습니다.");
        }

        String officerSessionId = userOwnPermissionGroupOptional.get().getSessionId();

        Map<String, Object> result = saveFile(file, subjectName);
        Path path = (Path) result.get("path");
        String uniqueFileName = (String) result.get("uniqueFileName");

        // 파일의 마지막 경로 (파일명 + 확장자 전까지 저장)
        String filePath = path.toString().substring(0, path.toString().lastIndexOf("\\") + 1);
        // 고유한 파일 번호 생성
        String fileNo = UUID.randomUUID().toString();
        FileInfo fileInfo = FileInfo.builder()
                .fileNo(fileNo)
                .fileSize(Long.toString(file.getSize()))
                .filePath(filePath)
                .orgFileNm(file.getOriginalFilename())
                .encFileNm(uniqueFileName)
                .uploadDt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")))
                .uploaderSessionId(teacherSessionId)
                .build();

        fileRepo.save(fileInfo);

        Subject subject = new Subject();
        subject.setSubjectName(subjectName);
        subject.setSubjectDesc(subjectDesc);
        subject.setSubjectCategory(subjectCategory);
        subject.setSubjectImageLink(fileNo);
        subject.setSubjectPromotion(subjectPromotion);
        subject.setTeacherSessionId(teacherSessionId);

        Subject savedSubject = tsr_s_repository.save(subject);

        CourseOwnSubject courseOwnSubject = new CourseOwnSubject();
        courseOwnSubject.setSubjectId(savedSubject.getSubjectId());
        courseOwnSubject.setCourseId(courseId);
        courseOwnSubject.setSubjectApproval("F");
        courseOwnSubject.setOfficerSessionId(officerSessionId); // 동적으로 가져온 officerSessionId 설정

        tsr_cos_repository.save(courseOwnSubject);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "과목 신청이 완료되었습니다.");
        response.put("subjectId", savedSubject.getSubjectId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/mySubject")
    public ResponseEntity<?> getMySubjects() {
        // 현재 인증된 사용자 정보 가져오기
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();
        String teacherSessionId = auth.getPrincipal().toString();

        // 강사의 세션 ID로 과목 가져오기
        List<Subject> subjects = tsr_s_repository.findByTeacherSessionId(teacherSessionId);

        if (subjects.isEmpty()) {
            return ResponseEntity.ok("신청한 과목이 없습니다.");
        }

        // 결과 필터링 및 응답 생성
        List<Map<String, Object>> responseList = subjects.stream().map(subject -> {
            Map<String, Object> subjectData = new HashMap<>();
            subjectData.put("subjectId", subject.getSubjectId());
            subjectData.put("subjectName", subject.getSubjectName());
            subjectData.put("subjectDesc", subject.getSubjectDesc());
            subjectData.put("subjectCategory", subject.getSubjectCategory());
            subjectData.put("subjectPromotion", subject.getSubjectPromotion());
            subjectData.put("subjectImageLink", subject.getSubjectImageLink());

            // 리포지토리 메서드로 조건에 맞는 CourseOwnSubject 검색
            Optional<CourseOwnSubject> courseOwnSubject = tsr_cos_repository
                    .findBySubjectIdAndCourseId(
                            subject.getSubjectId(), "individual-subjects");

            // 결과가 있는 경우 승인 상태 추가
            if (courseOwnSubject.isPresent()) {
                subjectData.put("subjectApproval", courseOwnSubject.get().getSubjectApproval());
            } else {
                subjectData.put("subjectApproval", "N/A"); // 조건을 만족하는 결과가 없는 경우
            }

            return subjectData;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

    // 이미지 반환
    @GetMapping("/images/{fileNo:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileNo) {
        try {
            Optional<FileInfo> fileInfoOptional = fileRepo.findByFileNo(fileNo);
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

    @PostMapping("/deleteSubject/{subjectId}")
    @Operation(summary = "신청 과목 삭제", description = "강사가 신청한 과목을 삭제")
    public ResponseEntity<?> deleteSubject(@PathVariable String subjectId) {

        // 보안 컨텍스트에서 강사의 세션 ID 가져오기
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();
        String teacherSessionId = auth.getPrincipal().toString();

        // 과목 확인
        Optional<Subject> subjectOpt = tsr_s_repository.findById(subjectId);

        if (subjectOpt.isPresent()) {
            Subject subject = subjectOpt.get();

            // 권한 확인
            if (!subject.getTeacherSessionId().equals(teacherSessionId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
            }

            // 관련 승인 정보 삭제
            List<CourseOwnSubject> relatedApprovals = tsr_cos2_repository.findBySubjectId(subjectId);
            for (CourseOwnSubject approval : relatedApprovals) {
                tsr_cos2_repository.delete(approval);
            }

            // 과목 삭제
            tsr_s_repository.deleteById(subjectId);

            return ResponseEntity.ok("과목이 성공적으로 삭제되었습니다.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 과목을 찾을 수 없습니다.");
    }
}
