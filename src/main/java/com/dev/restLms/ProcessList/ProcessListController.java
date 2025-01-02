// ProcessListController.java
package com.dev.restLms.ProcessList;

import com.dev.restLms.entity.Course;
import com.dev.restLms.entity.FileInfo;
import com.dev.restLms.entity.UserOwnAssignment;
import com.dev.restLms.entity.UserOwnAssignmentEvaluation;
import com.dev.restLms.entity.UserOwnCourse;
import com.dev.restLms.entity.UserOwnSubjectVideo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

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
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/course")
@Tag(name = "ProcessListController", description = "과정 목록 및 해당 과정의 수강자 수, 과정 책임자")
public class ProcessListController {

    @Autowired
    private ProcessListCourseRepository processListCourseRepository;

    @Autowired
    private ProcessListUserOwnCourseRepository processListUserOwnCourseRepository;

    @Autowired
    private ProcessListUserRepository processListUserRepository;

    @Autowired
    private ProcessListCourseOwnSubjectRepository processListCourseOwnSubjectRepository;

    @Autowired
    private ProcessListOfferedSubjectsRepository processListOfferedSubjectsRepository;

    @Autowired
    private ProcessListSubjectOwnVideoRepository processListSubjectOwnVideoRepository;

    @Autowired
    private ProcessListUserOwnAssignmentRepository processListUserOwnAssignmentRepository;

    @Autowired
    private ProcessListUserOwnSubjectVideoRepository processListUserOwnSubjectVideoRepository;

    @Autowired
    private ProcessListUserOwnPermissionGroupRepository processListUserOwnPermissionGroupRepository;

    @Autowired
    private ProcessListPermissionGroupRepository processListPermissionGroupRepository;

    @Autowired
    private ProcessListAssignmentRepository processListAssignmentRepository;

    @Autowired
    private ProcessListUserOwnAssignmentEvaluationRepository processListUserOwnAssignmentEvaluationRepository;

    @Autowired
    private ProcessListFileinfoReposutory processListFileinfoReposutory;

    // 이미지 반환
    @GetMapping("/images/{fileNo:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String fileNo) {
        try {
            Optional<FileInfo> fileInfoOptional = processListFileinfoReposutory.findByFileNo(fileNo);
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

    @GetMapping("/allTitles")
    @Operation(summary = "모든 과정 조회", description = "전체 과정 목록을 반환합니다.")
    public ResponseEntity<?> getAllCoursesWithOfficer(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // 페이징 요청
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "courseTitle"));
        String excludedCourseId = "individual-subjects";
        Page<Course> coursePage = processListCourseRepository.findByCourseIdNot(excludedCourseId, pageable);

        // 결과를 저장할 리스트
        List<Map<String, Object>> resultList = new ArrayList<>();

        for (Course course : coursePage) {

            if (!course.getCourseId().equals("individual-subjects")) {

                // 수강자 수 조회
                List<ProcessListUserOwnCourse> userCount = processListUserOwnCourseRepository
                        .findByCourseId(course.getCourseId());
                int studentCount = userCount.size();

                // 과정 책임자 정보 조회
                Optional<ProcessListUser> processListUsers = processListUserRepository
                        .findBySessionId(course.getSessionId());

                // 각 과정 정보와 수강자 수를 HashMap에 추가
                HashMap<String, Object> courseMap = new HashMap<>();
                courseMap.put("courseId", course.getCourseId());
                courseMap.put("courseTitle", course.getCourseTitle());
                courseMap.put("courseCapacity", course.getCourseCapacity());
                courseMap.put("enrollStartDate", course.getEnrollStartDate());
                courseMap.put("enrollEndDate", course.getEnrollEndDate());
                courseMap.put("studentCount", studentCount);
                courseMap.put("courseImg", course.getCourseImg());

                // 책임자 정보 가져오기
                courseMap.put("courseOfficerSessionId", processListUsers.get().getSessionId());
                courseMap.put("courseOfficerUserName", processListUsers.get().getUserName());

                // 결과를 리스트에 추가
                resultList.add(courseMap);

            }

        }

        Map<String, Object> response = new HashMap<>();
        response.put("courses", resultList);
        response.put("currentPage", coursePage.getNumber());
        response.put("totalItems", coursePage.getTotalElements());
        response.put("totalPages", coursePage.getTotalPages());

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/searchDueCourse")
    @Operation(summary = "수강 신청 예정 과정 검색")
    public ResponseEntity<?> searchDueCourse(
            @RequestParam String courseTitle,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String excludedCourseId = "individual-subjects";
        List<Course> courseLists = processListCourseRepository
                .findByCourseIdNotAndCourseTitleContaining(excludedCourseId, courseTitle,
                        Sort.by(Sort.Direction.ASC, "courseTitle"));

        // 결과를 저장할 리스트
        List<Map<String, Object>> resultList = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime nowDate = LocalDateTime.now();

        for (Course course : courseLists) {

            LocalDateTime courseDate = LocalDateTime.parse(course.getEnrollStartDate(), formatter);
            if (courseDate.isAfter(nowDate)) {

                // 수강자 수 조회
                List<ProcessListUserOwnCourse> userCount = processListUserOwnCourseRepository
                        .findByCourseId(course.getCourseId());
                int studentCount = userCount.size();

                // 과정 책임자 정보 조회
                Optional<ProcessListUser> processListUsers = processListUserRepository
                        .findBySessionId(course.getSessionId());

                // 각 과정 정보와 수강자 수를 HashMap에 추가
                HashMap<String, Object> courseMap = new HashMap<>();
                courseMap.put("courseId", course.getCourseId());
                courseMap.put("courseTitle", course.getCourseTitle());
                courseMap.put("courseCapacity", course.getCourseCapacity());
                courseMap.put("enrollStartDate", course.getEnrollStartDate());
                courseMap.put("enrollEndDate", course.getEnrollEndDate());
                courseMap.put("studentCount", studentCount);
                courseMap.put("courseImg", course.getCourseImg());

                // 책임자 정보 가져오기
                courseMap.put("courseOfficerSessionId", processListUsers.get().getSessionId());
                courseMap.put("courseOfficerUserName", processListUsers.get().getUserName());

                // 결과를 리스트에 추가
                resultList.add(courseMap);

            }

        }

        // 페이징 처리
        int totalItems = resultList.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        int start = page * size;
        int end = Math.min(start + size, totalItems);

        List<Map<String, Object>> pagedResultList = resultList.subList(start, end);

        Map<String, Object> response = new HashMap<>();
        response.put("officerCourse", pagedResultList);
        response.put("currentPage", page);
        response.put("totalItems", totalItems);
        response.put("totalPages", totalPages);

        return ResponseEntity.ok().body(response);

    }

    @PostMapping("/searchDeadlineCourse")
    @Operation(summary = "수강 신청 마감 과정 검색")
    public ResponseEntity<?> searchDeadlineCourse(
            @RequestParam String courseTitle,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String excludedCourseId = "individual-subjects";
        List<Course> courseLists = processListCourseRepository
                .findByCourseIdNotAndCourseTitleContaining(excludedCourseId, courseTitle,
                        Sort.by(Sort.Direction.ASC, "courseTitle"));

        // 결과를 저장할 리스트
        List<Map<String, Object>> resultList = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime nowDate = LocalDateTime.now();

        for (Course course : courseLists) {

            LocalDateTime courseDate = LocalDateTime.parse(course.getEnrollEndDate(), formatter);
            if (courseDate.isBefore(nowDate)) {

                // 수강자 수 조회
                List<ProcessListUserOwnCourse> userCount = processListUserOwnCourseRepository
                        .findByCourseId(course.getCourseId());
                int studentCount = userCount.size();

                // 과정 책임자 정보 조회
                Optional<ProcessListUser> processListUsers = processListUserRepository
                        .findBySessionId(course.getSessionId());

                // 각 과정 정보와 수강자 수를 HashMap에 추가
                HashMap<String, Object> courseMap = new HashMap<>();
                courseMap.put("courseId", course.getCourseId());
                courseMap.put("courseTitle", course.getCourseTitle());
                courseMap.put("courseCapacity", course.getCourseCapacity());
                courseMap.put("enrollStartDate", course.getEnrollStartDate());
                courseMap.put("enrollEndDate", course.getEnrollEndDate());
                courseMap.put("studentCount", studentCount);
                courseMap.put("courseImg", course.getCourseImg());

                // 책임자 정보 가져오기
                courseMap.put("courseOfficerSessionId", processListUsers.get().getSessionId());
                courseMap.put("courseOfficerUserName", processListUsers.get().getUserName());

                // 결과를 리스트에 추가
                resultList.add(courseMap);

            }

        }

        // 페이징 처리
        int totalItems = resultList.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        int start = page * size;
        int end = Math.min(start + size, totalItems);

        List<Map<String, Object>> pagedResultList = resultList.subList(start, end);

        Map<String, Object> response = new HashMap<>();
        response.put("officerCourse", pagedResultList);
        response.put("currentPage", page);
        response.put("totalItems", totalItems);
        response.put("totalPages", totalPages);

        return ResponseEntity.ok().body(response);

    }

    @PostMapping("/searchReceivingCourse")
    @Operation(summary = "수강 신청 중인 과정 검색")
    public ResponseEntity<?> searchReceivingCourse(
            @RequestParam String courseTitle,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        String excludedCourseId = "individual-subjects";
        List<Course> courseLists = processListCourseRepository
                .findByCourseIdNotAndCourseTitleContaining(excludedCourseId, courseTitle,
                        Sort.by(Sort.Direction.ASC, "courseTitle"));

        // 결과를 저장할 리스트
        List<Map<String, Object>> resultList = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        LocalDateTime nowDate = LocalDateTime.now();

        for (Course course : courseLists) {

            LocalDateTime enrollStartDate = LocalDateTime.parse(course.getEnrollStartDate(), formatter);
            LocalDateTime enrollEndDate = LocalDateTime.parse(course.getEnrollEndDate(), formatter);
            if (enrollStartDate.isBefore(nowDate) && enrollEndDate.isAfter(nowDate)) {

                // 수강자 수 조회
                List<ProcessListUserOwnCourse> userCount = processListUserOwnCourseRepository
                        .findByCourseId(course.getCourseId());
                int studentCount = userCount.size();

                // 과정 책임자 정보 조회
                Optional<ProcessListUser> processListUsers = processListUserRepository
                        .findBySessionId(course.getSessionId());

                // 각 과정 정보와 수강자 수를 HashMap에 추가
                HashMap<String, Object> courseMap = new HashMap<>();
                courseMap.put("courseId", course.getCourseId());
                courseMap.put("courseTitle", course.getCourseTitle());
                courseMap.put("courseCapacity", course.getCourseCapacity());
                courseMap.put("enrollStartDate", course.getEnrollStartDate());
                courseMap.put("enrollEndDate", course.getEnrollEndDate());
                courseMap.put("studentCount", studentCount);
                courseMap.put("courseImg", course.getCourseImg());

                // 책임자 정보 가져오기
                courseMap.put("courseOfficerSessionId", processListUsers.get().getSessionId());
                courseMap.put("courseOfficerUserName", processListUsers.get().getUserName());

                // 결과를 리스트에 추가
                resultList.add(courseMap);

            }

        }

        // 페이징 처리
        int totalItems = resultList.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);
        int start = page * size;
        int end = Math.min(start + size, totalItems);

        List<Map<String, Object>> pagedResultList = resultList.subList(start, end);

        Map<String, Object> response = new HashMap<>();
        response.put("officerCourse", pagedResultList);
        response.put("currentPage", page);
        response.put("totalItems", totalItems);
        response.put("totalPages", totalPages);

        return ResponseEntity.ok().body(response);

    }

    @PostMapping("/searchCourse")
    @Operation(summary = "모든 과정 검색")
    public ResponseEntity<?> searchCourse(
            @RequestParam String courseTitle,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // 페이징 요청
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "courseTitle"));
        String excludedCourseId = "individual-subjects";
        Page<Course> coursePage = processListCourseRepository
                .findByCourseIdNotAndCourseTitleContaining(excludedCourseId, courseTitle, pageable);

        // 결과를 저장할 리스트
        List<Map<String, Object>> resultList = new ArrayList<>();

        for (Course course : coursePage) {

            // 수강자 수 조회
            List<ProcessListUserOwnCourse> userCount = processListUserOwnCourseRepository
                    .findByCourseId(course.getCourseId());
            int studentCount = userCount.size();

            // 과정 책임자 정보 조회
            Optional<ProcessListUser> processListUsers = processListUserRepository
                    .findBySessionId(course.getSessionId());

            // 각 과정 정보와 수강자 수를 HashMap에 추가
            HashMap<String, Object> courseMap = new HashMap<>();
            courseMap.put("courseId", course.getCourseId());
            courseMap.put("courseTitle", course.getCourseTitle());
            courseMap.put("courseCapacity", course.getCourseCapacity());
            courseMap.put("enrollStartDate", course.getEnrollStartDate());
            courseMap.put("enrollEndDate", course.getEnrollEndDate());
            courseMap.put("studentCount", studentCount);
            courseMap.put("courseImg", course.getCourseImg());

            // 책임자 정보 가져오기
            courseMap.put("courseOfficerSessionId", processListUsers.get().getSessionId());
            courseMap.put("courseOfficerUserName", processListUsers.get().getUserName());

            // 결과를 리스트에 추가
            resultList.add(courseMap);

        }

        // return resultList;
        Map<String, Object> response = new HashMap<>();
        response.put("courses", resultList);
        response.put("currentPage", coursePage.getNumber());
        response.put("totalItems", coursePage.getTotalElements());
        response.put("totalPages", coursePage.getTotalPages());

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerCourse(
        @RequestParam String courseId
    ){

        try {

            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                    .getContext().getAuthentication();
            // 유저 세션아이디 보안 컨텍스트에서 가져오기
            String sessionId = auth.getPrincipal().toString();

            Optional<ProcessListUserOwnPermissionGroup> findUserPermission = processListUserOwnPermissionGroupRepository.findBySessionId(sessionId);

            if(findUserPermission.isPresent()){

                Optional<ProcessListPermissionGroup> findPermissionName = processListPermissionGroupRepository.findByPermissionGroupUuid(findUserPermission.get().getPermissionGroupUuid2());

                if(findPermissionName.isPresent() && findPermissionName.get().getPermissionName().equals("STUDENT")){

                    Optional<ProcessListCourse> findDate = processListCourseRepository.findBycourseId(courseId);

                    if(findDate.isPresent()){

                        Long nowDate = Long.parseLong(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
                        if(Long.parseLong(findDate.get().getEnrollStartDate())>nowDate && Long.parseLong(findDate.get().getEnrollEndDate()) < nowDate){
                            return ResponseEntity.status(HttpStatus.CONFLICT).body("수강신청 기간이 아닙니다");
                        }

                        List<ProcessListUserOwnCourse> findUsers = processListUserOwnCourseRepository.findByCourseId(courseId);
                        if(findUsers.size() >= Integer.parseInt(findDate.get().getCourseCapacity())){
                            return ResponseEntity.status(HttpStatus.CONFLICT).body("수강 신청 인원이 초과되었습니다.");
                        }

                        List<ProcessListUserOwnCourse> findUserCourses = processListUserOwnCourseRepository.findBySessionId(sessionId);
                        boolean userCoursesCheck = false;
                        for(ProcessListUserOwnCourse findUserCourse : findUserCourses) {
                            if(findUserCourse.getCourseApproval().equals("T")){
                                userCoursesCheck = true;
                            }else if(findUserCourse.getCourseApproval().equals("F")){
                                userCoursesCheck = false;
                                break;
                            }
                        }

                        if(userCoursesCheck){
                            UserOwnCourse userOwnCourse = UserOwnCourse.builder()
                            .sessionId(sessionId)
                            .courseId(courseId)
                            .officerSessionId(findDate.get().getCourseId())
                            .courseApproval("F")
                            .build();
                            processListUserOwnCourseRepository.save(userOwnCourse);
                            
                            List<ProcessListCourseOwnSubject> findCourseSubjects = processListCourseOwnSubjectRepository.findByCourseIdAndOfficerSessionId(courseId, findDate.get().getSessionId());
                            for(ProcessListCourseOwnSubject findCourseSubject : findCourseSubjects){

                                Optional<ProcessListOfferedSubjects> findOfferedSubjectsId = processListOfferedSubjectsRepository.findBySubjectIdAndOfficerSessionIdAndCourseId(findCourseSubject.getSubjectId(), findDate.get().getSessionId(), courseId);

                                if(findOfferedSubjectsId.isPresent()){

                                    UserOwnAssignment userOwnAssignment = UserOwnAssignment.builder()
                                    .userSessionId(sessionId)
                                    .offeredSubjectsId(findOfferedSubjectsId.get().getOfferedSubjectsId())
                                    .subjectAcceptCategory("F")
                                    .build();
                                    processListUserOwnAssignmentRepository.save(userOwnAssignment);

                                    List<ProcessListSubjectOwnVideo> findSubjectVideos = processListSubjectOwnVideoRepository.findBySovOfferedSubjectsId(findOfferedSubjectsId.get().getOfferedSubjectsId());

                                    if(!findSubjectVideos.isEmpty()){

                                        for(ProcessListSubjectOwnVideo findSubjectVideo : findSubjectVideos){

                                            UserOwnSubjectVideo userOwnSubjectVideo = UserOwnSubjectVideo.builder()
                                            .uosvSessionId(sessionId)
                                            .uosvEpisodeId(findSubjectVideo.getEpisodeId())
                                            .uosvOfferedSubjectsId(findOfferedSubjectsId.get().getOfferedSubjectsId())
                                            .progress("0")
                                            .uosvFinal("0")
                                            .build();
                                            processListUserOwnSubjectVideoRepository.save(userOwnSubjectVideo);

                                        }

                                    }

                                    List<ProcessListAssignment> findAssignments = processListAssignmentRepository.findByOfferedSubjectsId(findOfferedSubjectsId.get().getOfferedSubjectsId());

                                    if(!findAssignments.isEmpty()){

                                        for(ProcessListAssignment findAssignment : findAssignments){

                                            UserOwnAssignmentEvaluation userOwnAssignmentEvaluation = UserOwnAssignmentEvaluation.builder()
                                            .uoaeSessionId(sessionId)
                                            .assignmentId(findAssignment.getAssignmentId())
                                            .teacherSessionId(findAssignment.getTeacherSessionId())
                                            .build();
                                            processListUserOwnAssignmentEvaluationRepository.save(userOwnAssignmentEvaluation);

                                        }

                                    }

                                }
                                
                            }

                            return ResponseEntity.ok().body(findDate.get().getCourseTitle()+" 과정 수강 신청 완료");

                        }
                        
                    }

                }else{
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("수강신청 권한이 없습니다.");
                }

            }
            return ResponseEntity.status(HttpStatus.CONFLICT).body("권한이 없습니다.");
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " + e.getMessage());
        }
    }
    

    // @PostMapping("/registerCourse")
    // @Operation(summary = "사용자가 과정 등록", description = "사용자가 과정을 등록합니다.")
    // public ResponseEntity<?> userPutCourse(
    //         @RequestParam String courseId,
    //         @RequestParam String officerSessionId) {

    //     try {

    //         UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
    //                 .getContext().getAuthentication();
    //         // 유저 세션아이디 보안 컨텍스트에서 가져오기
    //         String sessionId = auth.getPrincipal().toString();

    //         // 사용자 권한 그룹에서 사용자 세션 아이디 확인
    //         Optional<ProcessListUserOwnPermissionGroup> userPermissionGroup = processListUserOwnPermissionGroupRepository
    //                 .findBySessionId(sessionId);

    //         // 사용자의 권한이 있는지 확인
    //         if (userPermissionGroup.isPresent()) {

    //             // 권한 아이디 저장
    //             String permissionGroupUuid = userPermissionGroup.get().getPermissionGroupUuid2();

    //             // 권한 그룹에서의 권한 아이디 확인
    //             Optional<ProcessListPermissionGroup> userPermissionName = processListPermissionGroupRepository
    //                     .findByPermissionGroupUuid(permissionGroupUuid);

    //             // 권한 그룹에 권한 아이디가 있는지 확인
    //             if (userPermissionName.isPresent()) {

    //                 // 권한 아이디의 권한 명 저장
    //                 String permissionName = userPermissionName.get().getPermissionName();

    //                 // 사용자의 권한 명이 STUDENT인지 확인
    //                 if (permissionName.equals("STUDENT")) {

    //                     // 해당 과정의 수강신청 날짜가 현재날짜보다 지났거나 아직 되지 않았을 때 수강신청을 할 수 없음
    //                     Optional<ProcessListCourse> findCourse = processListCourseRepository.findBycourseId(courseId);
    //                     Long nowDate = Long
    //                             .parseLong(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
    //                     if (Long.parseLong(findCourse.get().getEnrollEndDate()) < nowDate
    //                             || Long.parseLong(findCourse.get().getEnrollStartDate()) > nowDate) {
    //                         return ResponseEntity.status(HttpStatus.CONFLICT).body("수강신청 기간이 아닙니다.");
    //                     }

    //                     List<ProcessListUserOwnCourse> findUsers = processListUserOwnCourseRepository
    //                             .findByCourseId(courseId);
    //                     if (findUsers.size() >= Integer.parseInt(findCourse.get().getCourseCapacity())) {
    //                         return ResponseEntity.status(HttpStatus.CONFLICT).body("수강 신청 인원이 전부 다 찼습니다.");
    //                     }

    //                     // 사용자의 과정 목록 확인
    //                     List<ProcessListUserOwnCourse> userOwnCourses = processListUserOwnCourseRepository
    //                             .findBySessionId(sessionId);

    //                     // 이미 과정을 듣고 있으면 거짓 없으면 참
    //                     boolean userCourses = true;
    //                     for (ProcessListUserOwnCourse userOwnCourse : userOwnCourses) {
    //                         if (userOwnCourse.getCourseApproval().equals("F")) {
    //                             userCourses = false;
    //                             break;
    //                         } else {
    //                             userCourses = true;
    //                         }
    //                     }

    //                     // 듣고 있는 과정이 없을 때 실행
    //                     if (userCourses) {
    //                         UserOwnCourse postUserOwnCourse = UserOwnCourse.builder()
    //                                 .sessionId(sessionId)
    //                                 .courseId(courseId)
    //                                 .officerSessionId(officerSessionId)
    //                                 .courseApproval("F")
    //                                 .build();
    //                         processListUserOwnCourseRepository.save(postUserOwnCourse);

    //                         // 해당 과정의 과목 목록 확인
    //                         List<ProcessListCourseOwnSubject> courseOwnSubjects = processListCourseOwnSubjectRepository
    //                                 .findByCourseIdAndOfficerSessionId(courseId, findCourse.get().getSessionId());

    //                         for (ProcessListCourseOwnSubject courseOwnSubject : courseOwnSubjects) {

    //                             // 해당 과목이 개설된 과목인지 확인
    //                             Optional<ProcessListOfferedSubjects> offeredSubject = processListOfferedSubjectsRepository
    //                                     .findBySubjectIdAndOfficerSessionId(courseOwnSubject.getSubjectId(),
    //                                             findCourse.get().getSessionId());

    //                             if (offeredSubject.isPresent()) {

    //                                 UserOwnAssignment postUserOwnAssignment = UserOwnAssignment.builder()
    //                                         .userSessionId(sessionId)
    //                                         .offeredSubjectsId(offeredSubject.get().getOfferedSubjectsId())
    //                                         .subjectAcceptCategory("F")
    //                                         .build();
    //                                 processListUserOwnAssignmentRepository.save(postUserOwnAssignment);

    //                                 // 해당 과목의 영상 목록 확인
    //                                 List<ProcessListSubjectOwnVideo> subjectOwnVideos = processListSubjectOwnVideoRepository
    //                                         .findBySovOfferedSubjectsId(offeredSubject.get().getOfferedSubjectsId());

    //                                 if (!subjectOwnVideos.isEmpty()) {

    //                                     for (ProcessListSubjectOwnVideo subjectOwnVideo : subjectOwnVideos) {
    //                                         UserOwnSubjectVideo postUserSubjectOwnVideo = UserOwnSubjectVideo.builder()
    //                                                 .uosvSessionId(sessionId)
    //                                                 .uosvEpisodeId(subjectOwnVideo.getEpisodeId())
    //                                                 .uosvOfferedSubjectsId(subjectOwnVideo.getSovOfferedSubjectsId())
    //                                                 .progress("0")
    //                                                 .uosvFinal("0")
    //                                                 .build();
    //                                         processListUserOwnSubjectVideoRepository.save(postUserSubjectOwnVideo);
    //                                     }

    //                                 }

    //                                 // 해당 과목의 과제가 할당되어 있는지 확인
    //                                 List<ProcessListAssignment> findAssignments = processListAssignmentRepository
    //                                         .findByOfferedSubjectsId(offeredSubject.get().getOfferedSubjectsId());

    //                                 if (!findAssignments.isEmpty()) {

    //                                     for (ProcessListAssignment findAssignment : findAssignments) {

    //                                         UserOwnAssignmentEvaluation userOwnAssignmentEvaluation = UserOwnAssignmentEvaluation
    //                                                 .builder()
    //                                                 .uoaeSessionId(sessionId)
    //                                                 .assignmentId(findAssignment.getAssignmentId())
    //                                                 .teacherSessionId(findAssignment.getTeacherSessionId())
    //                                                 .isSubmit("F")
    //                                                 .build();

    //                                         processListUserOwnAssignmentEvaluationRepository
    //                                                 .save(userOwnAssignmentEvaluation);

    //                                     }

    //                                 }

    //                             } else {
    //                                 return ResponseEntity.status(HttpStatus.CONFLICT).body("개설 과목이 아닙니다");
    //                             }
    //                         }
    //                         return ResponseEntity.ok().body("수강신청 완료");
    //                     }
    //                     return ResponseEntity.status(HttpStatus.CONFLICT).body("다른 과정을 듣고 있습니다.");
    //                 }
    //                 return ResponseEntity.status(HttpStatus.CONFLICT).body("수강신청 권한이 없습니다.");
    //             }
    //         }
    //         return ResponseEntity.status(HttpStatus.CONFLICT).body("권한이 없습니다.");
    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " + e.getMessage());
    //     }
    // }
}
