package com.dev.restLms.ModifyCourse.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dev.restLms.ModifyCourse.dto.checkSubjectDto;
import com.dev.restLms.ModifyCourse.dto.courseDto;
import com.dev.restLms.ModifyCourse.persistence.ModifyCourseAssignmentRepository;
import com.dev.restLms.ModifyCourse.persistence.ModifyCourseBoardPostRepository;
import com.dev.restLms.ModifyCourse.persistence.ModifyCourseBoardRepository;
import com.dev.restLms.ModifyCourse.persistence.ModifyCourseCommentRepository;
import com.dev.restLms.ModifyCourse.persistence.ModifyCourseCourseRepository;
import com.dev.restLms.ModifyCourse.persistence.ModifyCourseFileinfoRepository;
import com.dev.restLms.ModifyCourse.persistence.ModifyCourseOfferedSubjectsRepository;
import com.dev.restLms.ModifyCourse.persistence.ModifyCourseOwnSubjectRepository;
import com.dev.restLms.ModifyCourse.persistence.ModifyCourseSubjectOwnVideoRepository;
import com.dev.restLms.ModifyCourse.persistence.ModifyCourseSubjectResitory;
import com.dev.restLms.ModifyCourse.persistence.ModifyCourseUserRepository;
import com.dev.restLms.ModifyCourse.persistence.ModifyCourseVideoRepository;
import com.dev.restLms.ModifyCourse.projection.ModifyCourseAssignment;
import com.dev.restLms.ModifyCourse.projection.ModifyCourseBoard;
import com.dev.restLms.ModifyCourse.projection.ModifyCourseBoardPost;
import com.dev.restLms.ModifyCourse.projection.ModifyCourseComment;
import com.dev.restLms.ModifyCourse.projection.ModifyCourseCourse;
import com.dev.restLms.ModifyCourse.projection.ModifyCourseOfferedSubjects;
import com.dev.restLms.ModifyCourse.projection.ModifyCourseOwnSubject;
import com.dev.restLms.ModifyCourse.projection.ModifyCourseSubject;
import com.dev.restLms.ModifyCourse.projection.ModifyCourseSubjectOwnVideo;
import com.dev.restLms.ModifyCourse.projection.ModifyCourseUser;
import com.dev.restLms.entity.Board;
import com.dev.restLms.entity.Course;
import com.dev.restLms.entity.CourseOwnSubject;
import com.dev.restLms.entity.FileInfo;
import com.dev.restLms.entity.OfferedSubjects;
import com.dev.restLms.entity.Subject;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/modifyCourse")
@Tag(name = "ModifyCourseController", description = "관리자 과정 수정 페이지")
public class ModifyCourseController {

    @Autowired
    private ModifyCourseCourseRepository modifyCourseCourseRepository;

    @Autowired
    private ModifyCourseOwnSubjectRepository modifyCourseOwnSubjectRepository;

    @Autowired
    private ModifyCourseSubjectResitory modifyCourseSubjectResitory;

    @Autowired
    private ModifyCourseFileinfoRepository modifyCourseFileinfoRepository;

    @Autowired
    private ModifyCourseUserRepository modifyCourseUserRepository;

    @Autowired
    private ModifyCourseOfferedSubjectsRepository modifyCourseOfferedSubjectsRepository;

    @Autowired
    private ModifyCourseSubjectOwnVideoRepository modifyCourseSubjectOwnVideoRepository;

    @Autowired
    private ModifyCourseVideoRepository modifyCourseVideoRepository;

    @Autowired
    private ModifyCourseAssignmentRepository modifyCourseAssignmentRepository;

    @Autowired
    private ModifyCourseBoardRepository modifyCourseBoardRepository;

    @Autowired
    private ModifyCourseBoardPostRepository modifyCourseBoardPostRepository;

    @Autowired
    private ModifyCourseCommentRepository modifyCourseCommentRepository;

    private static final String ROOT_DIR = "src/main/resources/static/";
    private static final String UPLOAD_DIR = "Course/";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB (바이트 단위)

    // @GetMapping("/Courses")
    // @Operation(summary = "책임자의 과정 목록", description = "책임자의 과정 목록을 불러옵니다.")
    // public ResponseEntity<?> getCourses(
    // @RequestParam(defaultValue = "0") int page,
    // @RequestParam(defaultValue = "7") int size
    // ) {

    // try {

    // UsernamePasswordAuthenticationToken auth =
    // (UsernamePasswordAuthenticationToken) SecurityContextHolder
    // .getContext().getAuthentication();
    // // 유저 세션아이디 보안 컨텍스트에서 가져오기
    // String sessionId = auth.getPrincipal().toString();

    // List<Map<String, Object>> resultList = new ArrayList<>();

    // Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC,
    // "courseStartDate").and(Sort.by(Sort.Direction.ASC, "courseTitle")));
    // Page<ModifyCourseCourse> findCourses =
    // modifyCourseCourseRepository.findBySessionId(sessionId, pageable);

    // for(ModifyCourseCourse findCourse : findCourses){

    // HashMap<String, Object> courseMap = new HashMap<>();
    // courseMap.put("courseTitle", findCourse.getCourseTitle());
    // courseMap.put("courseStartDate", findCourse.getCourseStartDate());
    // courseMap.put("courseId", findCourse.getCourseId());

    // resultList.add(courseMap);

    // }

    // Map<String, Object> response = new HashMap<>();
    // response.put("content", resultList);
    // response.put("currentPage", findCourses.getNumber());
    // response.put("totalItems", findCourses.getTotalElements());
    // response.put("totalPages", findCourses.getTotalPages());

    // return ResponseEntity.ok().body(response);

    // } catch (Exception e) {
    // return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : "
    // +e.getMessage());
    // }
    // }

    @PostMapping("/sreachCourse")
    @Operation(summary = "책임자의 과정 검색", description = "검색한 책임자의 과정 목록을 불러옵니다.")
    public ResponseEntity<?> sreachCourse(
            @RequestParam String courseTitle,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "7") int size) {

        try {

            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                    .getContext().getAuthentication();
            // 유저 세션아이디 보안 컨텍스트에서 가져오기
            String sessionId = auth.getPrincipal().toString();

            List<Map<String, Object>> resultList = new ArrayList<>();

            Pageable pageable = PageRequest.of(page, size,
                    Sort.by(Sort.Direction.DESC, "courseStartDate").and(Sort.by(Sort.Direction.ASC, "courseTitle")));
            Page<ModifyCourseCourse> findCourses = modifyCourseCourseRepository
                    .findBySessionIdAndCourseTitleContaining(sessionId, courseTitle, pageable);

            for (ModifyCourseCourse findCourse : findCourses) {

                HashMap<String, Object> courseMap = new HashMap<>();
                courseMap.put("courseTitle", findCourse.getCourseTitle());
                courseMap.put("enrollStartDate", findCourse.getEnrollStartDate());
                courseMap.put("courseId", findCourse.getCourseId());

                resultList.add(courseMap);

            }

            Map<String, Object> response = new HashMap<>();
            response.put("content", resultList);
            response.put("currentPage", findCourses.getNumber());
            response.put("totalItems", findCourses.getTotalElements());
            response.put("totalPages", findCourses.getTotalPages());

            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " + e.getMessage());
        }
    }

    // 이미지 반환
    @GetMapping("/images/{fileNo:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileNo) {
        try {
            Optional<FileInfo> fileInfoOptional = modifyCourseFileinfoRepository.findByFileNo(fileNo);
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

    @GetMapping("/files/{fileNo:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String fileNo) {
        try {
            Optional<FileInfo> fileInfoOptional = modifyCourseFileinfoRepository.findByFileNo(fileNo);
            if (!fileInfoOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            FileInfo fileInfo = fileInfoOptional.get();
            Path filePath = Paths.get(fileInfo.getFilePath() + fileInfo.getEncFileNm());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                // 파일의 MIME 타입을 동적으로 설정합니다.
                MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM; // 일반 파일로 설정
                // 파일의 확장자에 따라 MIME 타입을 설정할 수 있습니다.
                String fileExtension = fileInfo.getEncFileNm().substring(fileInfo.getEncFileNm().lastIndexOf(".") + 1);
                switch (fileExtension.toLowerCase()) {
                    case "pdf":
                        mediaType = MediaType.APPLICATION_PDF;
                        break;
                    case "txt":
                        mediaType = MediaType.TEXT_PLAIN;
                        break;
                    // 추가적인 파일 형식에 대한 조건을 추가할 수 있음
                    default:
                        mediaType = MediaType.APPLICATION_OCTET_STREAM; // 기본
                }

                return ResponseEntity.ok()
                        .contentType(mediaType)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileInfo.getEncFileNm() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @GetMapping("/courseView")
    @Operation(summary = "과정 정보", description = "기존의 과정을 수정합니다.")
    public ResponseEntity<?> courseView(
            @RequestParam String courseId) {

        try {

            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                    .getContext().getAuthentication();
            // 유저 세션아이디 보안 컨텍스트에서 가져오기
            String sessionId = auth.getPrincipal().toString();

            List<Map<String, Object>> courseList = new ArrayList<>();
            List<Map<String, Object>> subjectList = new ArrayList<>();

            Optional<ModifyCourseCourse> findCourse = modifyCourseCourseRepository.findByCourseId(courseId);

            Long nowDate = Long.parseLong(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

            if (Long.parseLong(findCourse.get().getEnrollStartDate()) < nowDate) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("과정을 수정할 수 있는 기간이 지났습니다.");
            }

            if (findCourse.isPresent()) {

                Map<String, Object> courseMap = new HashMap<>();
                courseMap.put("courseId", findCourse.get().getCourseId());
                courseMap.put("courseTitle", findCourse.get().getCourseTitle());
                courseMap.put("courseCapacity", findCourse.get().getCourseCapacity());
                courseMap.put("courseStartDate", findCourse.get().getCourseStartDate());
                courseMap.put("courseEndDate", findCourse.get().getCourseEndDate());
                courseMap.put("enrollStartDate", findCourse.get().getEnrollStartDate());
                courseMap.put("enrollEndDate", findCourse.get().getEnrollEndDate());
                courseMap.put("courseImg", findCourse.get().getCourseImg());

                courseList.add(courseMap);

                List<ModifyCourseOwnSubject> findSubjectIds = modifyCourseOwnSubjectRepository
                        .findByCourseIdAndOfficerSessionId(courseId, sessionId);

                for (ModifyCourseOwnSubject findSubjectId : findSubjectIds) {

                    Optional<ModifyCourseSubject> findSubject = modifyCourseSubjectResitory
                            .findBySubjectId(findSubjectId.getSubjectId());
                    if (findSubject.isPresent()) {

                        Map<String, Object> subjectMap = new HashMap<>();
                        subjectMap.put("subjectId", findSubject.get().getSubjectId());
                        subjectMap.put("subjectName", findSubject.get().getSubjectName());
                        subjectMap.put("subjectDesc", findSubject.get().getSubjectDesc());
                        subjectMap.put("subjectCategory", findSubject.get().getSubjectCategory());
                        
                        String teacherSessionId = findSubject.get().getTeacherSessionId();

                        if (teacherSessionId == null || teacherSessionId.isEmpty()) {
                            subjectMap.put("teacherName", "미정");
                        } else {
                            Optional<ModifyCourseUser> findTeacherName = modifyCourseUserRepository
                                    .findBySessionId(teacherSessionId);
                            if (findTeacherName.isPresent()) {
                                String teacherName = findTeacherName.get().getUserName();
                                subjectMap.put("teacherName", teacherName);
                            } else {
                                subjectMap.put("teacherName", "미정");
                            }
                        }

                        subjectList.add(subjectMap);

                    }

                }

                Map<String, Object> response = new HashMap<>();
                response.put("course", courseList);
                response.put("subjectList", subjectList);

                return ResponseEntity.ok().body(response);

            } else {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("과정을 찾을 수 없습니다.");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " + e.getMessage());
        }
    }

    @PostMapping("/searchSubject")
    @Operation(summary = "과목 검색", description = "검색한 과목을 불러옵니다.")
    public ResponseEntity<?> searchSubject(
            @RequestParam String subjectName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        try {

            List<Map<String, Object>> resultList = new ArrayList<>();

            List<ModifyCourseOwnSubject> findSubjectIds = modifyCourseOwnSubjectRepository
                    .findByCourseIdAndSubjectApproval("individual-subjects", "T");

            List<String> filterSubjectIds = new ArrayList<>();
            for (ModifyCourseOwnSubject findSubjectId : findSubjectIds) {
                filterSubjectIds.add(findSubjectId.getSubjectId());
            }

            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "subjectName"));
            Page<ModifyCourseSubject> subjectIds = modifyCourseSubjectResitory
                    .findBySubjectIdInAndSubjectNameContaining(filterSubjectIds, subjectName, pageable);

            for (ModifyCourseSubject subjectId : subjectIds) {

                HashMap<String, Object> subjectMap = new HashMap<>();
                subjectMap.put("subjectId", subjectId.getSubjectId());
                subjectMap.put("subjectName", subjectId.getSubjectName());

                if (subjectId.getTeacherSessionId() == null) {
                    subjectMap.put("teacherName", "미정");
                    subjectMap.put("teacherSessionId", "");
                } else {

                    Optional<ModifyCourseUser> findTeacherName = modifyCourseUserRepository
                            .findBySessionId(subjectId.getTeacherSessionId());

                    if (findTeacherName.isPresent()) {
                        subjectMap.put("teacherName", findTeacherName.get().getUserName());
                        subjectMap.put("teacherSessionId", subjectId.getTeacherSessionId());
                    }

                }

                resultList.add(subjectMap);

            }

            Map<String, Object> response = new HashMap<>();

            response.put("content", resultList);
            response.put("currentPage", subjectIds.getNumber());
            response.put("totalItems", subjectIds.getTotalElements());
            response.put("totalPages", subjectIds.getTotalPages());

            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " + e.getMessage());
        }

    }

    private Map<String, Object> saveFile(MultipartFile file, courseDto course) throws Exception {

        // 원본 파일명에서 확장자 추출
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";

        if (originalFilename != null) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 고유 파일명 생성
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        // 저장 경로
        Path path = Paths.get(ROOT_DIR + UPLOAD_DIR + uniqueFileName);

        // 파일이 존재하지 않으면 생성
        Files.createDirectories(path.getParent());

        // 파일 저장
        byte[] bytes = file.getBytes();
        Files.write(path, bytes);

        Map<String, Object> result = new HashMap<>();
        result.put("path", path);
        result.put("uniqueFileName", uniqueFileName);

        return result;

    }

    @PostMapping("/updateCourse")
    @Operation(summary = "과정 수정", description = "기존의 과정을 수정합니다.")
    public ResponseEntity<?> updateCourse(
            @RequestParam String courseId,
            @RequestPart(value = "file") MultipartFile file,
            @RequestPart("updateCourse") courseDto updateCourse,
            @RequestPart("checkSubjectDto") List<checkSubjectDto> checkSubjectDtos) {

        try {

            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                    .getContext().getAuthentication();
            // 유저 세션아이디 보안 컨텍스트에서 가져오기
            String sessionId = auth.getPrincipal().toString();

            // Optional<ModifyCourseCourse> existCourse =
            // modifyCourseCourseRepository.findByCourseId(courseId);
            Optional<Course> findCourse = modifyCourseCourseRepository.findById(courseId);

            if (!findCourse.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("해당 과정이 존재하지 않습니다.");
            }

            // 과정을 수정할 수 없는 조건
            Long nowDate = Long.parseLong(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
            if (Long.parseLong(findCourse.get().getEnrollStartDate()) < nowDate) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("과정 수정 가능 기간이 아닙니다.");
            }

            // ----------------------------------------------------- 기존의 과정에 대한 과목들에 대한 정보
            // 삭제

            List<ModifyCourseOwnSubject> findSubjectIds = modifyCourseOwnSubjectRepository
                    .findByCourseIdAndOfficerSessionId(courseId, sessionId);

            for (ModifyCourseOwnSubject findSubjectId : findSubjectIds) {

                Optional<ModifyCourseOfferedSubjects> findOfferedSubjectId = modifyCourseOfferedSubjectsRepository
                        .findByCourseIdAndOfficerSessionIdAndSubjectId(courseId, sessionId,
                                findSubjectId.getSubjectId());

                if (findOfferedSubjectId.isPresent()) {

                    List<ModifyCourseSubjectOwnVideo> findSubjectOwnVideos = modifyCourseSubjectOwnVideoRepository
                            .findBySovOfferedSubjectsId(findOfferedSubjectId.get().getOfferedSubjectsId());

                    if (!findSubjectOwnVideos.isEmpty()) {

                        for (ModifyCourseSubjectOwnVideo findSubjectOwnVideo : findSubjectOwnVideos) {

                            modifyCourseSubjectOwnVideoRepository.deleteById(findSubjectOwnVideo.getEpisodeId());

                            modifyCourseVideoRepository.deleteById(findSubjectOwnVideo.getSovVideoId());

                        }

                    }

                    List<ModifyCourseAssignment> findAssignmentIds = modifyCourseAssignmentRepository
                            .findByOfferedSubjectsId(findOfferedSubjectId.get().getOfferedSubjectsId());

                    if (!findAssignmentIds.isEmpty()) {

                        for (ModifyCourseAssignment findAssignmentId : findAssignmentIds) {

                            modifyCourseAssignmentRepository.deleteById(findAssignmentId.getAssignmentId());

                        }

                    }

                    Optional<ModifyCourseBoard> findBoardId = modifyCourseBoardRepository
                            .findByOfferedSubjectsId(findOfferedSubjectId.get().getOfferedSubjectsId());

                    if (findBoardId.isPresent()) {

                        List<ModifyCourseBoardPost> findPostIds = modifyCourseBoardPostRepository
                                .findByBoardId(findBoardId.get().getBoardId());

                        if (!findPostIds.isEmpty()) {

                            for (ModifyCourseBoardPost findPostId : findPostIds) {

                                List<ModifyCourseComment> findCommentIds = modifyCourseCommentRepository
                                        .findByPostId(findPostId.getPostId());

                                if (!findCommentIds.isEmpty()) {

                                    for (ModifyCourseComment findCommentId : findCommentIds) {

                                        modifyCourseCommentRepository.deleteById(findCommentId.getCommentId());

                                    }

                                }

                                if (!findPostId.getFileNo().isEmpty()) {

                                    Optional<FileInfo> findFilePath = modifyCourseFileinfoRepository
                                            .findByFileNo(findPostId.getFileNo());

                                    if (findFilePath.isPresent()) {

                                        FileInfo fileInfo = findFilePath.get();
                                        Path filePath = Paths.get(fileInfo.getFilePath() + fileInfo.getEncFileNm());

                                        try {
                                            Files.deleteIfExists(filePath);
                                        } catch (Exception e) {
                                            return ResponseEntity.status(HttpStatus.CONFLICT)
                                                    .body("파일 삭제 실패: " + e.getMessage());
                                        }

                                        modifyCourseFileinfoRepository.deleteById(findPostId.getFileNo());

                                    }

                                }

                                modifyCourseBoardPostRepository.deleteById(findPostId.getPostId());

                            }

                        }

                        modifyCourseBoardRepository.deleteById(findBoardId.get().getBoardId());

                    }

                    modifyCourseOfferedSubjectsRepository.deleteById(findOfferedSubjectId.get().getOfferedSubjectsId());

                    modifyCourseOwnSubjectRepository.deleteById(findSubjectId.getIncreaseId());

                    modifyCourseSubjectResitory.deleteById(findSubjectId.getSubjectId());

                }

            }

            // modifyCourseCourseRepository.deleteById(courseId);

            // ---------------------------------------------------------- 수정 전 과정에 대한 과목 정보
            
            // 수료 기간 계산
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            LocalDateTime start = LocalDateTime.parse(updateCourse.getCourseStartDate(), formatter);
            LocalDateTime end = LocalDateTime.parse(updateCourse.getCourseEndDate(), formatter);
            Long boundary = ChronoUnit.DAYS.between(start, end);

            Course findCourseInfo = findCourse.get();
            findCourseInfo.setCourseTitle(updateCourse.getCourseTitle());
            findCourseInfo.setCourseBoundary(Long.toString(boundary));
            findCourseInfo.setCourseCompleted(Integer.toString(checkSubjectDtos.size()));
            findCourseInfo.setCourseCapacity(updateCourse.getCourseCapacity());
            findCourseInfo.setCourseProgressStatus("0");
            findCourseInfo.setCourseStartDate(updateCourse.getCourseStartDate());
            findCourseInfo.setCourseEndDate(updateCourse.getCourseEndDate());
            findCourseInfo.setEnrollStartDate(updateCourse.getEnrollStartDate());
            findCourseInfo.setEnrollEndDate(updateCourse.getEnrollEndDate());

            modifyCourseCourseRepository.save(findCourseInfo);

            if(file != null && !"no-file".equals(file.getOriginalFilename())){

                Optional<FileInfo> findFileInfo = modifyCourseFileinfoRepository
                        .findByFileNo(findCourse.get().getCourseImg());
                if (findFileInfo.isPresent()) {
                    FileInfo fileInfo = findFileInfo.get();
                    Path filePath = Paths.get(fileInfo.getFilePath() + fileInfo.getEncFileNm());
    
                    try {
                        Files.deleteIfExists(filePath);
                    } catch (Exception e) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body("파일 삭제 실패: " + e.getMessage());
                    }
    
                }
    
                if (file != null) {
    
                    // 파일 크기 확인
                    if (file.getSize() > MAX_FILE_SIZE) {
                        return ResponseEntity.status(HttpStatus.CONFLICT).body("파일 크기 초과");
                    }
    
                    // 파일 정보 저장
                    Map<String, Object> result = saveFile(file, updateCourse);
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
                            .uploaderSessionId(sessionId)
                            .build();
                    modifyCourseFileinfoRepository.save(fileInfo);
    
                    findCourseInfo.setCourseImg(fileNo);
    
                } 
                
            }else{
                findCourseInfo.setCourseImg(findCourseInfo.getCourseImg());
            }

            for (checkSubjectDto checkSubjectDto : checkSubjectDtos) {

                Subject subject = Subject.builder()
                        .subjectName(checkSubjectDto.getSubjectName())
                        .subjectDesc(checkSubjectDto.getSubjectDesc())
                        .subjectCategory(checkSubjectDto.getSubjectCategory())
                        .build();

                modifyCourseSubjectResitory.save(subject);

                CourseOwnSubject courseOwnSubject = CourseOwnSubject.builder()
                        .subjectId(subject.getSubjectId())
                        .courseId(findCourseInfo.getCourseId())
                        .officerSessionId(sessionId)
                        .subjectApproval("T")
                        .build();

                modifyCourseOwnSubjectRepository.save(courseOwnSubject);

                if (checkSubjectDto.getTeacherSessionId().isEmpty() || checkSubjectDto.getTeacherSessionId() == null
                        || checkSubjectDto.getTeacherSessionId().equals("Not")) {

                    OfferedSubjects offeredSubjects = OfferedSubjects.builder()
                            .courseId(findCourseInfo.getCourseId())
                            .subjectId(subject.getSubjectId())
                            .officerSessionId(sessionId)
                            .teacherSessionId(null)
                            .build();

                    modifyCourseOfferedSubjectsRepository.save(offeredSubjects);

                } else {

                    OfferedSubjects offeredSubjects = OfferedSubjects.builder()
                            .courseId(findCourseInfo.getCourseId())
                            .subjectId(subject.getSubjectId())
                            .officerSessionId(sessionId)
                            .teacherSessionId(checkSubjectDto.getTeacherSessionId())
                            .build();

                    modifyCourseOfferedSubjectsRepository.save(offeredSubjects);

                    Board board = Board.builder()
                            .boardCategory(checkSubjectDto.getSubjectName() + "Q&A 게시판")
                            .teacherSessionId(checkSubjectDto.getTeacherSessionId())
                            .offeredSubjectsId(offeredSubjects.getOfferedSubjectsId())
                            .build();

                    modifyCourseBoardRepository.save(board);

                }

            }

            return ResponseEntity.ok().body(updateCourse.getCourseTitle() + "과정 수정 완료");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " + e.getMessage());
        }

    }

}
