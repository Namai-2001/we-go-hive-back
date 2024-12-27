package com.dev.restLms.sechan.teacherSubjectRegister.controller;

// import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
// import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dev.restLms.entity.CourseOwnSubject;
import com.dev.restLms.entity.FileInfo;
import com.dev.restLms.entity.Subject;
import com.dev.restLms.sechan.teacherSubjectRegister.repository.TSR_COS_Repository;
import com.dev.restLms.sechan.teacherSubjectRegister.repository.TSR_File_Repository;
import com.dev.restLms.sechan.teacherSubjectRegister.repository.TSR_S_Repository;

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

    private static final String ROOT_DIR = "src/main/resources/static/";
    private static final String UPLOAD_DIR = "SubjectImage/";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB (바이트 단위)

    // 파일 저장 메서드
    private Map<String, Object> saveFile(MultipartFile file, String subjectName) throws Exception {
        // 원본 파일명에서 확장자 추출
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";

        if (originalFilename != null) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")); // 확장자 추출
        }

        // 고유한 파일명 생성 (UUID + 확장자)
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        // 저장 경로 : root 디렉터리 / SubjectImage / subjectName
        Path path = Paths.get(ROOT_DIR + UPLOAD_DIR + subjectName + '/' + uniqueFileName);

        // 경로가 존재하지 않으면 생성
        Files.createDirectories(path.getParent());

        // 파일 저장
        byte[] bytes = file.getBytes();
        Files.write(path, bytes);

        Map<String, Object> result = new HashMap<>();
        result.put("path", path);
        result.put("uniqueFileName", uniqueFileName);
        return result;
    }

    @PostMapping("/apply")
    @Operation(summary = "과목 신청", description = "강사가 과목을 신청하고 과목 정보와 파일 정보를 저장")
    public ResponseEntity<?> applySubject(
            @RequestPart("file") MultipartFile file,
            @RequestPart("subjectData") Map<String, String> requestData) throws Exception {

        // 파일과 데이터 유효성 검증
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 제공되지 않았습니다.");
        }
        if (requestData == null || requestData.isEmpty()) {
            return ResponseEntity.badRequest().body("과목 데이터가 제공되지 않았습니다.");
        }

        // 파일 크기 검증
        if (file.getSize() > MAX_FILE_SIZE) {
            return ResponseEntity.badRequest().body("파일 크기가 제한을 초과했습니다.");
        }

        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();
        // 유저 세션아이디 보안 컨텍스트에서 가져오기
        String teacherSessionId = auth.getPrincipal().toString();

        // 과목 데이터 추출
        String subjectName = requestData.get("subjectName");
        String subjectDesc = requestData.get("subjectDesc");
        String subjectCategory = requestData.get("subjectCategory");
        String subjectPromotion = requestData.get("subjectPromotion");
        String officerSessionId = "12g8h9i0j-1k2l-m3n4-o5p6-q7r8s9t0u1v"; // 기본값 설정
        String courseId = "individual-subjects";

        // 파일 저장
        Map<String, Object> result = saveFile(file, subjectName);
        Path path = (Path) result.get("path");
        String uniqueFileName = (String) result.get("uniqueFileName");

        // 고유한 파일 번호 생성
        String fileNo = UUID.randomUUID().toString();
        FileInfo fileInfo = FileInfo.builder()
                .fileNo(fileNo)
                .fileSize(Long.toString(file.getSize()))
                .filePath(path.getParent().toString())
                .orgFileNm(file.getOriginalFilename())
                .encFileNm(uniqueFileName)
                .uploadDt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")))
                .uploaderSessionId(teacherSessionId)
                .build();

        fileRepo.save(fileInfo);

        // 과목 정보 저장
        Subject subject = new Subject();
        subject.setSubjectName(subjectName);
        subject.setSubjectDesc(subjectDesc);
        subject.setSubjectCategory(subjectCategory);
        subject.setSubjectImageLink(fileNo); // 파일 번호 저장
        subject.setSubjectPromotion(subjectPromotion);
        subject.setTeacherSessionId(teacherSessionId);

        Subject savedSubject = tsr_s_repository.save(subject);

        // 과목 승인 관리 저장
        CourseOwnSubject courseOwnSubject = new CourseOwnSubject();
        courseOwnSubject.setSubjectId(savedSubject.getSubjectId());
        courseOwnSubject.setCourseId(courseId);
        courseOwnSubject.setSubjectApproval("F");
        courseOwnSubject.setOfficerSessionId(officerSessionId);

        tsr_cos_repository.save(courseOwnSubject);

        // 응답 반환
        Map<String, Object> response = new HashMap<>();
        response.put("message", "과목 신청이 완료되었습니다.");
        response.put("subjectId", savedSubject.getSubjectId());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/mySubject")
    @Operation(summary = "신청한 과목 조회", description = "강사가 자신이 신청한 과목을 조회")
    public ResponseEntity<?> getMySubjects() {

        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();
        // 유저 세션아이디 보안 컨텍스트에서 가져오기
        String teacherSessionId = auth.getPrincipal().toString();
        // 강사가 신청한 과목 목록 조회
        List<Subject> subjects = tsr_s_repository.findByTeacherSessionId(teacherSessionId);

        if (subjects.isEmpty()) {
            return ResponseEntity.ok("신청한 과목이 없습니다.");
        }

        // 과목 데이터를 반환할 리스트 생성
        List<Map<String, Object>> responseList = new ArrayList<>();

        for (Subject subject : subjects) {
            Map<String, Object> subjectData = new HashMap<>();
            subjectData.put("subjectId", subject.getSubjectId());
            subjectData.put("subjectName", subject.getSubjectName());
            subjectData.put("subjectDesc", subject.getSubjectDesc());
            subjectData.put("subjectCategory", subject.getSubjectCategory());
            subjectData.put("subjectPromotion", subject.getSubjectPromotion());

            // 파일 정보 조회
            Optional<FileInfo> fileInfoOpt = fileRepo.findById(subject.getSubjectImageLink());
            String subjectImageUrl = fileInfoOpt.map(fileInfo -> {
                // 파일 경로 구성
                return ROOT_DIR + UPLOAD_DIR + fileInfo.getFilePath() + fileInfo.getEncFileNm();
            }).orElse("이미지 없음"); // 기본값 설정

            subjectData.put("subjectImageLink", subjectImageUrl);

            // 과목 승인 여부 추가
            Optional<CourseOwnSubject> courseOwnSubject = tsr_cos_repository.findBySubjectId(subject.getSubjectId());
            courseOwnSubject.ifPresent(cos -> subjectData.put("subjectApproval", cos.getSubjectApproval()));

            responseList.add(subjectData);
        }

        return ResponseEntity.ok(responseList);
    }

    @PostMapping("/deleteSubject")
    @Operation(summary = "신청 과목 삭제", description = "강사가 신청한 과목을 삭제")
    public ResponseEntity<?> deleteSubject(@RequestBody Map<String, String> requestData) {

        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                .getContext().getAuthentication();
        String teacherSessionId = auth.getPrincipal().toString();

        String subjectId = requestData.get("subjectId");

        Optional<Subject> subjectOpt = tsr_s_repository.findById(subjectId);

        if (subjectOpt.isPresent()) {
            Subject subject = subjectOpt.get();

            if (!subject.getTeacherSessionId().equals(teacherSessionId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
            }

            // 관련 승인 정보 삭제
            tsr_cos_repository.deleteBySubjectId(subjectId);

            // 과목 삭제
            tsr_s_repository.deleteById(subjectId);

            return ResponseEntity.ok("과목이 성공적으로 삭제되었습니다.");
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 과목을 찾을 수 없습니다.");
    }
}
