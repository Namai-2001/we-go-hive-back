// // ProcessListController.java
// package com.dev.restLms.ProcessList.controller;

// import com.dev.restLms.ProcessList.model.ProcessListCourse;
// import com.dev.restLms.ProcessList.model.ProcessListCourseOwnSubject;
// import com.dev.restLms.ProcessList.model.ProcessListOfferedSubjects;
// import com.dev.restLms.ProcessList.model.ProcessListPermissionGroup;
// import com.dev.restLms.ProcessList.model.ProcessListSubjectOwnVideo;
// import com.dev.restLms.ProcessList.model.ProcessListUser;
// import com.dev.restLms.ProcessList.model.ProcessListUserOwnAssignment;
// import com.dev.restLms.ProcessList.model.ProcessListUserOwnCourse;
// import com.dev.restLms.ProcessList.model.ProcessListUserOwnPermissionGroup;
// import com.dev.restLms.ProcessList.model.ProcessListUserOwnSubjectVideo;
// import com.dev.restLms.ProcessList.persistence.ProcessListCourseOwnSubjectRepository;
// import com.dev.restLms.ProcessList.persistence.ProcessListCourseRepository;
// import com.dev.restLms.ProcessList.persistence.ProcessListOfferedSubjectsRepository;
// import com.dev.restLms.ProcessList.persistence.ProcessListPermissionGroupRepository;
// import com.dev.restLms.ProcessList.persistence.ProcessListSubjectOwnVideoRepository;
// import com.dev.restLms.ProcessList.persistence.ProcessListUserOwnAssignmentRepository;
// import com.dev.restLms.ProcessList.persistence.ProcessListUserOwnCourseRepository;
// import com.dev.restLms.ProcessList.persistence.ProcessListUserOwnPermissionGroupRepository;
// import com.dev.restLms.ProcessList.persistence.ProcessListUserOwnSubjectVideoRepository;
// import com.dev.restLms.ProcessList.persistence.ProcessListUserRepository;

// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.tags.Tag;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;


// @RestController
// @RequestMapping("/course")
// @Tag(name = "ProcessListController", description = "과정 목록 및 해당 과정의 수강자 수, 과정 책임자")
// public class ProcessListController {

//     @Autowired
//     private ProcessListCourseRepository processListCourseRepository;

//     @Autowired
//     private ProcessListUserOwnCourseRepository processListUserOwnCourseRepository;

//     @Autowired
//     private ProcessListUserRepository processListUserRepository;

//     @Autowired
//     private ProcessListCourseOwnSubjectRepository processListCourseOwnSubjectRepository;

//     @Autowired
//     private ProcessListOfferedSubjectsRepository processListOfferedSubjectsRepository;

//     @Autowired
//     private ProcessListSubjectOwnVideoRepository processListSubjectOwnVideoRepository;

//     @Autowired
//     private ProcessListUserOwnAssignmentRepository processListUserOwnAssignmentRepository;

//     @Autowired
//     private ProcessListUserOwnSubjectVideoRepository processListUserOwnSubjectVideoRepository;

//     @Autowired
//     private ProcessListUserOwnPermissionGroupRepository processListUserOwnPermissionGroupRepository;

//     @Autowired
//     private ProcessListPermissionGroupRepository processListPermissionGroupRepository;

//     @GetMapping("/allTitles")
//     @Operation(summary = "모든 과정 조회", description = "전체 과정 목록을 반환합니다.")
//     public List<Map<String, Object>> getAllCoursesWithOfficer(
//             @RequestParam(defaultValue = "0") int page,
//             @RequestParam(defaultValue = "10") int size) {
        
//         // 페이징 요청
//         Pageable pageable = PageRequest.of(page, size);
//         Page<ProcessListCourse> coursePage = processListCourseRepository.findAll(pageable);
        
//         // 결과를 저장할 리스트
//         List<Map<String, Object>> resultList = new ArrayList<>();
        
//         for (ProcessListCourse course : coursePage) {
//             // 수강자 수 조회
//             List<ProcessListUserOwnCourse> userCount = processListUserOwnCourseRepository.findByCourseId(course.getCourseId());
//             int studentCount = userCount.size();

//             // 과정 책임자 정보 조회
//             List<ProcessListUser> processListUsers = processListUserRepository.findBySessionId(course.getSessionId());

//             // 각 과정 정보와 수강자 수를 HashMap에 추가
//             HashMap<String, Object> courseMap = new HashMap<>();
//             courseMap.put("courseId", course.getCourseId());
//             courseMap.put("courseTitle", course.getCourseTitle());
//             courseMap.put("courseCapacity", course.getCourseCapacity());
//             courseMap.put("enrollStartDate", course.getEnrollStartDate());
//             courseMap.put("enrollEndDate", course.getEnrollEndDate());
//             courseMap.put("studentCount", studentCount);
//             courseMap.put("courseImg", course.getCourseImg());

//             // 책임자 정보 가져오기
//             ProcessListUser officer = processListUsers.get(0);
//             courseMap.put("courseOfficerSessionId", officer.getSessionId());
//             courseMap.put("courseOfficerUserName", officer.getUserName());

//             // 결과를 리스트에 추가
//             resultList.add(courseMap);
//         }

//         return resultList;
//     }

//     @PostMapping("/registerCourse")
//     @Operation(summary = "사용자가 과정 등록", description = "사용자가 과정을 등록합니다.")
//     public ResponseEntity<String> userPutCourse(
//         @RequestParam String sessionId,
//         @RequestParam String courseId,
//         @RequestParam String officerSessionId
//     ) {

//         // 사용자 권한 그룹에서 사용자 세션 아이디 확인
//         Optional<ProcessListUserOwnPermissionGroup> userPermissionGroup = processListUserOwnPermissionGroupRepository.findBySessionId(sessionId);

//         // 사용자의 권한이 있는지 확인
//         if(userPermissionGroup.isPresent()){
//             // 권한 아이디 저장
//             String permissionGroupUuid2 = userPermissionGroup.get().getPermissionGroupUuid2();

//             // 권한 그룹에서의 권한 아이디 확인
//             Optional<ProcessListPermissionGroup> permissionGroup = processListPermissionGroupRepository.findByPermissionGroupUuid(permissionGroupUuid2);

//             // 권한 그룹에 권한 아이디가 있는지 확인
//             if(permissionGroup.isPresent()){

//                 // 권한 아이디에 대한 권한 명 저장
//                 String permissionGroupName = permissionGroup.get().getPermissionName();

//                 // 사용자의 세션아이디에 대한 권한 명이 STUDENT인지 확인
//                 if(permissionGroupName.equals("STUDENT")){

//                     // 사용자의 과정 목록 확인
//                     List<ProcessListUserOwnCourse> listUserOwnCourses = processListUserOwnCourseRepository.findBySessionId(sessionId);

//                     // 사용자가 현재 과정을 듣고 있는지 확인
//                     boolean userHaveCourse = false;

//                     if(listUserOwnCourses.isEmpty()){
//                         userHaveCourse = true;
//                     }else{
//                         for(ProcessListUserOwnCourse listUserOwnCourse : listUserOwnCourses){
//                             if("F".equals(listUserOwnCourse.getCourseApproval())){
//                                 userHaveCourse = false;
//                                 break;
//                             }else{
//                                 userHaveCourse = true;
//                             }
//                             continue;
//                         }
//                     }

//                     if (userHaveCourse) {
                        
//                         // UserOwnCourse 사용자 세션아이디, 과정 코드, 관리자 세션 아이디 저장
//                         ProcessListUserOwnCourse userOwnCourse = ProcessListUserOwnCourse.builder()
//                         .sessionId(sessionId)
//                         .courseId(courseId)
//                         .officerSessionId(officerSessionId)
//                         .build();
//                         processListUserOwnCourseRepository.save(userOwnCourse);

//                         // UserOwnAssigment 및 UserOwnSubjectVideo에 해당 과정의 과목 목록 정보 확인
//                         List<ProcessListCourseOwnSubject> courseOwnSubjects = processListCourseOwnSubjectRepository.findByCourseIdAndOfficerSessionId(courseId, officerSessionId);

//                             for(ProcessListCourseOwnSubject courseOwnSubject : courseOwnSubjects){

//                                 // 개설과목 코드 확인
//                                 List<ProcessListOfferedSubjects> offeredSubjects = processListOfferedSubjectsRepository.findByCourseIdAndOfficerSessionIdAndSubjectId(courseOwnSubject.getCourseId(), courseOwnSubject.getOfficerSessionId(), courseOwnSubject.getSubjectId());

//                                 for(ProcessListOfferedSubjects offeredSubject : offeredSubjects){

//                                     // UserOwnAssigment 사용자 세션아이디, 개설과목코드 저장
//                                     ProcessListUserOwnAssignment userOwnAssignment = ProcessListUserOwnAssignment.builder()
//                                     .userSessionId(sessionId)
//                                     .offeredSubjectsId(offeredSubject.getOfferedSubjectsId())
//                                     .build();
//                                     processListUserOwnAssignmentRepository.save(userOwnAssignment);

//                                     // 개설과목 영상 확인
//                                     List<ProcessListSubjectOwnVideo> subjectOwnVideos = processListSubjectOwnVideoRepository.findBySovOfferedSubjectsId(offeredSubject.getOfferedSubjectsId());

//                                     for(ProcessListSubjectOwnVideo subjectOwnVideo : subjectOwnVideos){

//                                         // UserOwnSubjectVideo 사용자 세션아이디, 에피소드아이디, 개설과목코드
//                                         ProcessListUserOwnSubjectVideo userOwnSubjectVideo = ProcessListUserOwnSubjectVideo.builder()
//                                         .uosvSessionId(sessionId)
//                                         .uosvEpisodeId(subjectOwnVideo.getEpisodeId())
//                                         .uosvOfferedSubjectsId(subjectOwnVideo.getSovOfferedSubjectsId())
//                                         .build();
//                                         processListUserOwnSubjectVideoRepository.save(userOwnSubjectVideo);

//                                     }
//                                 }
//                             }
//                             return ResponseEntity.ok().body("과정 신청이 완료되었습니다.");
//                     }else{
//                         return ResponseEntity.status(HttpStatus.CONFLICT).body("다른 과정을 듣고 있습니다.");
//                     }
//                 }
//             }
//         }
//         return ResponseEntity.status(HttpStatus.CONFLICT).body("수강 신청에 대한 권한이 없습니다");
//     }
// }
