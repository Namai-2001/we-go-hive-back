package com.dev.restLms.CreateCourse.controller;

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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dev.restLms.CreateCourse.dto.checkSubjectDto;
import com.dev.restLms.CreateCourse.dto.courseDto;
import com.dev.restLms.CreateCourse.persistence.CreateCourseBardRepository;
import com.dev.restLms.CreateCourse.persistence.CreateCourseFileinfoRepositoy;
import com.dev.restLms.CreateCourse.persistence.CreateCourseOfferedSubjectsRepository;
import com.dev.restLms.CreateCourse.persistence.CreateCourseOwnSubjuctRepository;
import com.dev.restLms.CreateCourse.persistence.CreateCourseRepository;
import com.dev.restLms.CreateCourse.persistence.CreateCourseSubjectRepository;
import com.dev.restLms.CreateCourse.persistence.CreateCourseUserRepository;
import com.dev.restLms.CreateCourse.projection.CreateCourse;
import com.dev.restLms.CreateCourse.projection.CreateCourseOfferedSubjects;
import com.dev.restLms.CreateCourse.projection.CreateCourseOwnSubjuct;
import com.dev.restLms.CreateCourse.projection.CreateCourseSubject;
import com.dev.restLms.CreateCourse.projection.CreateCourseUser;
import com.dev.restLms.entity.Board;
import com.dev.restLms.entity.Course;
import com.dev.restLms.entity.CourseOwnSubject;
import com.dev.restLms.entity.FileInfo;
import com.dev.restLms.entity.OfferedSubjects;
import com.dev.restLms.entity.Subject;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;




@RestController
@RequestMapping("/createCourse")
@Tag(name = "CreateCourseController", description = "관리자 과정 생성 페이지")
public class CreateCourseController {

    @Autowired
    private CreateCourseSubjectRepository createCourseSubjectRepository;
    
    @Autowired
    private CreateCourseUserRepository createCourseUserRepository;

    @Autowired
    private CreateCourseFileinfoRepositoy createCourseFileinfoRepositoy;

    @Autowired
    private CreateCourseRepository createCourseRepository;

    @Autowired
    private CreateCourseOwnSubjuctRepository createCourseOwnSubjuctRepository;

    @Autowired
    private CreateCourseOfferedSubjectsRepository createCourseOfferedSubjectsRepository;

    @Autowired
    private CreateCourseBardRepository createCourseBardRepository;

    private static final String ROOT_DIR = "src/main/resources/static/";
    private static final String UPLOAD_DIR = "Course/";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB (바이트 단위)

    private Map<String, Object> saveFile(MultipartFile file, courseDto course) throws Exception{

        // 원본 파일명에서 확장자 추출 
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";

        if(originalFilename != null){
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

    @PostMapping("/createCourse")
    @Operation(summary = "과정 생성", description = "과정을 생성합니다.")
    public ResponseEntity<?> createCourse(
        @RequestPart("file") MultipartFile file,
        @RequestPart("createCourse") courseDto createCourse,
        @RequestPart("checkSubjectDto") List<checkSubjectDto> checkSubjectDtos
        ) {

            try {

                UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String sessionId = auth.getPrincipal().toString();

                String courseImgFileNo = null;

                if(file != null){

                    // 파일 크기 확인 
                    if(file.getSize() > MAX_FILE_SIZE){
                        return ResponseEntity.status(HttpStatus.CONFLICT).body("파일 크기 초과");
                    }

                    // 파일 정보 저장 
                    Map<String, Object> result = saveFile(file, createCourse);
                    Path path = (Path) result.get("path");
                    String uniqueFileName = (String) result.get("uniqueFileName");

                    // 파일의 마지막 경로 (파일명 + 확장자 전까지 저장)
                    String filePath = path.toString().substring(0, path.toString().lastIndexOf("\\")+1);
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
                        createCourseFileinfoRepositoy.save(fileInfo);
                        
                        courseImgFileNo = fileNo;


                }else{
                    courseImgFileNo = null;
                }

                // 수료 기간 계산
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                LocalDateTime start = LocalDateTime.parse(createCourse.getCourseStartDate(), formatter);
                LocalDateTime end = LocalDateTime.parse(createCourse.getCourseEndDate(), formatter);
                Long boundary = ChronoUnit.DAYS.between(start, end);

                Course course = Course.builder()
                        .sessionId(sessionId)
                        .courseTitle(createCourse.getCourseTitle())
                        .courseBoundary(Long.toString(boundary))
                        .courseCompleted(Integer.toString(checkSubjectDtos.size()))
                        .courseCapacity(createCourse.getCourseCapacity())
                        .courseProgressStatus("0")
                        .courseStartDate(createCourse.getCourseStartDate())
                        .courseEndDate(createCourse.getCourseEndDate())
                        .enrollStartDate(createCourse.getEnrollStartDate())
                        .enrollEndDate(createCourse.getEnrollEndDate())
                        .courseImg(courseImgFileNo)
                        .build();

                createCourseRepository.save(course);
                
                for(checkSubjectDto checkSubjectDto : checkSubjectDtos ){

                    Subject subject;

                    if(checkSubjectDto.getTeacherSessionId().isEmpty() || checkSubjectDto.getTeacherSessionId()==null || checkSubjectDto.getTeacherSessionId().equals("Not")){

                         subject = Subject.builder()
                        .subjectName(checkSubjectDto.getSubjectName())
                        .subjectDesc(checkSubjectDto.getSubjectDesc())
                        .subjectCategory(checkSubjectDto.getSubjectCategory())
                        .build();

                        createCourseSubjectRepository.save(subject);

                    }else{

                         subject = Subject.builder()
                        .subjectName(checkSubjectDto.getSubjectName())
                        .subjectDesc(checkSubjectDto.getSubjectDesc())
                        .subjectCategory(checkSubjectDto.getSubjectCategory())
                        .teacherSessionId(checkSubjectDto.getTeacherSessionId())
                        .build();

                        createCourseSubjectRepository.save(subject);

                    }



                    CourseOwnSubject courseOwnSubject = CourseOwnSubject.builder()
                    .subjectId(subject.getSubjectId())
                    .courseId(course.getCourseId())
                    .officerSessionId(sessionId)
                    .subjectApproval("T")
                    .build();

                    createCourseOwnSubjuctRepository.save(courseOwnSubject);

                    if(checkSubjectDto.getTeacherSessionId().isEmpty() || checkSubjectDto.getTeacherSessionId() == null || checkSubjectDto.getTeacherSessionId().equals("Not")){

                        OfferedSubjects offeredSubjects = OfferedSubjects.builder()
                        .courseId(course.getCourseId())
                        .subjectId(subject.getSubjectId())
                        .officerSessionId(sessionId)
                        .teacherSessionId(null)
                        .build();

                        createCourseOfferedSubjectsRepository.save(offeredSubjects);
                        
                    }else{

                        OfferedSubjects offeredSubjects = OfferedSubjects.builder()
                        .courseId(course.getCourseId())
                        .subjectId(subject.getSubjectId())
                        .officerSessionId(sessionId)
                        .teacherSessionId(checkSubjectDto.getTeacherSessionId())
                        .build();
    
                        createCourseOfferedSubjectsRepository.save(offeredSubjects);

                        Board board = Board.builder()
                        .boardCategory(checkSubjectDto.getSubjectName()+"Q&A 게시판")
                        .teacherSessionId(checkSubjectDto.getTeacherSessionId())
                        .offeredSubjectsId(offeredSubjects.getOfferedSubjectsId())
                        .build();

                        createCourseBardRepository.save(board);

                    }
                }
                return ResponseEntity.ok().body(createCourse.getCourseTitle()+" 과정 생성 완료");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " + e.getMessage());
            }
    }

    // @GetMapping("/subjectList")
    // @Operation(summary = "과목 목록", description = "과목 목록을 불러옵니다.")
    // public ResponseEntity<?> getSubjects(
    //     @RequestParam(defaultValue = "0") int page,
    //     @RequestParam(defaultValue = "5") int size
    //     ) {

    //         try {
                
    //             List<Map<String, Object>>resultList = new ArrayList<>();
    
    //             Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC,"subjectName"));
    //             Page<Subject> findSubjects = createCourseSubjectRepository.findAll(pageable);
    
    //             for(Subject findSubject : findSubjects){
    
    //                 HashMap<String, Object> subjectMap = new HashMap<>();
    //                 subjectMap.put("subjectId", findSubject.getSubjectId());
    //                 subjectMap.put("subjectName", findSubject.getSubjectName());

    //                 if(findSubject.getTeacherSessionId()==null){

    //                     subjectMap.put("teacherName", "미정");

    //                 }else{

    //                     Optional<CreateCourseUser> findTeacherName = createCourseUserRepository.findBySessionId(findSubject.getTeacherSessionId());

    //                     if(findTeacherName.isPresent()){
    //                         subjectMap.put("teacherName", findTeacherName.get().getUserName());
    //                     }

    //                 }
    
    //                 resultList.add(subjectMap);
    
    //             }
    
    //             Map<String, Object> response = new HashMap<>();
    
    //             response.put("content", resultList);
    //             response.put("currentPage", findSubjects.getNumber());
    //             response.put("totalItems", findSubjects.getTotalElements());
    //             response.put("totalPages", findSubjects.getTotalPages());
    
    //         return ResponseEntity.ok().body(response);

    //         } catch (Exception e) {
    //             return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " + e.getMessage());
    //         }

    // }

    @PostMapping("/searchSubject")
    @Operation(summary = "과목 검색", description = "검색한 과목을 불러옵니다.")
    public ResponseEntity<?> searchSubject(
        @RequestParam String subjectName,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size
        ) {

            try {

                List<Map<String, Object>>resultList = new ArrayList<>();

                List<CreateCourseOwnSubjuct> findSubjectIds = createCourseOwnSubjuctRepository.findByCourseIdAndSubjectApproval("individual-subjects", "T");

                List<String> filterSubjectIds = new ArrayList<>();
                for(CreateCourseOwnSubjuct findSubjectId : findSubjectIds ){
                    filterSubjectIds.add(findSubjectId.getSubjectId());
                }

                Pageable pageable = PageRequest.of(page, size,  Sort.by(Sort.Direction.ASC,"subjectName"));
                Page<CreateCourseSubject> subjectIds = createCourseSubjectRepository.findBySubjectIdInAndSubjectNameContaining(filterSubjectIds, subjectName, pageable);

                for(CreateCourseSubject subjectId : subjectIds){

                    HashMap<String, Object> subjectMap = new HashMap<>();
                    subjectMap.put("subjectId", subjectId.getSubjectId());
                    subjectMap.put("subjectName", subjectId.getSubjectName());
                    subjectMap.put("subjectDesc", subjectId.getSubjectDesc());
                    subjectMap.put("subjectCategory", subjectId.getSubjectCategory());
                    
                    if(subjectId.getTeacherSessionId()==null){
                        subjectMap.put("teacherName", "미정");
                        subjectMap.put("teacherSessionId", "");
                    }else{

                        Optional<CreateCourseUser> findTeacherName = createCourseUserRepository.findBySessionId(subjectId.getTeacherSessionId());

                        if(findTeacherName.isPresent()){
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

    @GetMapping("/history")
    @Operation(summary = "해당 과목의 강사에 대한 인적사항 정보")
    public ResponseEntity<?> getTeacherHistory(
        @RequestParam String teacherSessionId
        ) {

            try {

                List<Map<String, Object>> teacherInfoList = new ArrayList<>();
                List<Map<String, Object>> teacherCourseList = new ArrayList<>();
                List<Map<String, Object>> teacherSubjectList = new ArrayList<>();

                if(!teacherSessionId.isEmpty()){
                    
                    Optional<CreateCourseUser> findTeacherInfo = createCourseUserRepository.findBySessionId(teacherSessionId);
                    
                    if(findTeacherInfo.isPresent()){
                        
                        Map<String, Object> teacherInfo = new HashMap<>();
                        teacherInfo.put("teacherName", findTeacherInfo.get().getUserName());
                        teacherInfo.put("teacherBirth", findTeacherInfo.get().getUserBirth());
                        teacherInfo.put("teacherEmail", findTeacherInfo.get().getUserEmail());
                        teacherInfo.put("teacherPhoneNumber", findTeacherInfo.get().getPhoneNumber());
                        if(findTeacherInfo.get().getFileNo()==null || findTeacherInfo.get().getFileNo().isEmpty() || findTeacherInfo.get().getFileNo().equals("0")  ){
                            teacherInfo.put("teacherFileNo", "16235caa-b7c2-4fb4-98f0-d16cc18c2315");
                        }else{
                            teacherInfo.put("teacherFileNo", findTeacherInfo.get().getFileNo());
                        }
                        
                        teacherInfoList.add(teacherInfo);

                        List<CreateCourseOfferedSubjects> findTeacherHistorys = createCourseOfferedSubjectsRepository.findByTeacherSessionId(teacherSessionId);

                        for(CreateCourseOfferedSubjects findTeacherHistory :findTeacherHistorys){

                            if(!findTeacherHistory.getCourseId().equals("individual-subjects")){

                                Optional<CreateCourse> findCourseTitle = createCourseRepository.findByCourseId(findTeacherHistory.getCourseId());
                                Optional<CreateCourseSubject> findSubjectName = createCourseSubjectRepository.findBySubjectId(findTeacherHistory.getSubjectId());
    
                                if(findCourseTitle.isPresent() && findSubjectName.isPresent()){
    
                                    Map<String, Object> courseMap = new HashMap<>();
                                    courseMap.put("courseTitle", findCourseTitle.get().getCourseTitle());
                                    courseMap.put("subjectName", findSubjectName.get().getSubjectName());
    
                                    teacherCourseList.add(courseMap);
    
                                }

                            }else{

                                Optional<CreateCourseOwnSubjuct> findSubjectCheck = createCourseOwnSubjuctRepository.findBySubjectIdAndSubjectApproval(findTeacherHistory.getSubjectId(), "T");

                                if(findSubjectCheck.isPresent()){

                                    Optional<CreateCourseSubject> findSubjectName = createCourseSubjectRepository.findBySubjectId(findTeacherHistory.getSubjectId());
    
                                    if(findSubjectName.isPresent()){
    
                                        Map<String, Object> subjectMap = new HashMap<>();
                                        subjectMap.put("subjectName", findSubjectName.get().getSubjectName());
    
                                        teacherSubjectList.add(subjectMap);
    
                                    }
                                    
                                }
                                

                            }

                        }

                        Map<String, Object> response = new HashMap<>();
                        response.put("teacherInfo", teacherInfo);
                        response.put("teacherCourseHistory", teacherCourseList);
                        response.put("teacherIndividualHistory", teacherSubjectList);

                        return ResponseEntity.ok().body(response);

                    }else{
                        return ResponseEntity.status(HttpStatus.CONFLICT).body("배정된 강사가 없는 과목입니다.");
                    }

                }

                return ResponseEntity.status(HttpStatus.CONFLICT).body("배정된 강사가 없는 과목입니다.");
                
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " + e.getMessage());
            }
    }

    // 이미지 반환 
    @GetMapping("/images/{fileNo:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileNo) {
        try {
            Optional<FileInfo> fileInfoOptional = createCourseFileinfoRepositoy.findByFileNo(fileNo);
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
    

    // @PostMapping("/createSubject")
    // @Operation(summary = "과목 생성", description = "과목을 생성합니다.")
    // public ResponseEntity<?> createSubject(
    //     @RequestBody subjectDto dto
    //     ) {

    //         try {

    //             UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
    //                             .getContext().getAuthentication();
    //             // 유저 세션아이디 보안 컨텍스트에서 가져오기
    //             String sessionId = auth.getPrincipal().toString();

    //             if(dto.getSubjectName() != null && dto.getSubjectDesc() != null && dto.getSubjectCategory()!=null){

    //                 Subject subject = Subject.builder()
    //                 .subjectName(dto.getSubjectName())
    //                 .subjectDesc(dto.getSubjectDesc())
    //                 .subjectCategory(dto.getSubjectCategory())
    //                 .build();

    //                 createCourseSubjectRepository.save(subject);

    //                 CourseOwnSubject courseOwnSubject = CourseOwnSubject.builder()
    //                 .subjectId(subject.getSubjectId())
    //                 .courseId("individual-subjects")
    //                 .officerSessionId(sessionId)
    //                 .subjectApproval("T")
    //                 .build();

    //                 createCourseOwnSubjuctRepository.save(courseOwnSubject);

    //                 return ResponseEntity.ok().body(dto.getSubjectName()+"과목 생성 완료");

    //             }else{
    //                 return ResponseEntity.status(HttpStatus.CONFLICT).body("모두 입력 후 생성해주세요.");
    //             }

                
    //         } catch (Exception e) {
    //             return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " + e.getMessage());
    //         }
    // }
    
    
    
    
}
