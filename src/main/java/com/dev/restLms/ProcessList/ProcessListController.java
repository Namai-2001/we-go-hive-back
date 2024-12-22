// ProcessListController.java
package com.dev.restLms.ProcessList;


import com.dev.restLms.entity.Course;
import com.dev.restLms.entity.UserOwnAssignment;
import com.dev.restLms.entity.UserOwnCourse;
import com.dev.restLms.entity.UserOwnSubjectVideo;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/allTitles")
    @Operation(summary = "모든 과정 조회", description = "전체 과정 목록을 반환합니다.")
    public ResponseEntity<?> getAllCoursesWithOfficer(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        // 페이징 요청
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<Course> coursePage = processListCourseRepository.findAll(pageable);
        
        // 결과를 저장할 리스트
        List<Map<String, Object>> resultList = new ArrayList<>();
        
        for (Course course : coursePage) {

            if(!course.getCourseTitle().equals("개별과목")){

                // 수강자 수 조회
                List<ProcessListUserOwnCourse> userCount = processListUserOwnCourseRepository.findByCourseId(course.getCourseId());
                int studentCount = userCount.size();

                // 과정 책임자 정보 조회
                Optional<ProcessListUser> processListUsers = processListUserRepository.findBySessionId(course.getSessionId());

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

                // 총 과정 수
                // courseMap.put("courseSize", processListCourseRepository.findAll().size());

                // 결과를 리스트에 추가
                resultList.add(courseMap);

            }

            
        }

        // return resultList;
        Map<String, Object> response = new HashMap<>();
        response.put("courses", resultList);
        response.put("currentPage", coursePage.getNumber());
        response.put("totalItems", coursePage.getTotalElements());
        response.put("totalPages", coursePage.getTotalPages());

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/registerCourse")
    @Operation(summary = "사용자가 과정 등록", description = "사용자가 과정을 등록합니다.")
    public ResponseEntity<String> userPutCourse(
        @RequestParam String courseId,
        @RequestParam String officerSessionId
    ) {

        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String sessionId = auth.getPrincipal().toString();

        // 사용자 권한 그룹에서 사용자 세션 아이디 확인
        Optional<ProcessListUserOwnPermissionGroup> userPermissionGroup = processListUserOwnPermissionGroupRepository.findBySessionId(sessionId);

        // 사용자의 권한이 있는지 확인
        if(userPermissionGroup.isPresent()){

            // 권한 아이디 저장
            String permissionGroupUuid = userPermissionGroup.get().getPermissionGroupUuid2();

            // 권한 그룹에서의 권한 아이디 확인
            Optional<ProcessListPermissionGroup> userPermissionName = processListPermissionGroupRepository.findByPermissionGroupUuid(permissionGroupUuid);

            // 권한 그룹에 권한 아이디가 있는지 확인
            if(userPermissionName.isPresent()){

                // 권한 아이디의 권한 명 저장
                String permissionName = userPermissionName.get().getPermissionName();

                // 사용자의 권한 명이 STUDENT인지 확인
                if(permissionName.equals("STUDENT")){

                    // 사용자의 과정 목록 확인
                    List<ProcessListUserOwnCourse> userOwnCourses = processListUserOwnCourseRepository.findBySessionId(sessionId);

                    // 이미 과정을 듣고 있으면 거짓 없으면 참
                    boolean userCourses = true;
                    for(ProcessListUserOwnCourse userOwnCourse : userOwnCourses){
                        if(userOwnCourse.getCourseApproval().equals("F")){
                            userCourses = false;
                            break;
                        }else{
                            userCourses = true;
                        }
                    }

                    // 듣고 있는 과정이 없을 때 실행행
                    if(userCourses){
                        UserOwnCourse postUserOwnCourse = UserOwnCourse.builder()
                        .sessionId(sessionId)
                        .courseId(courseId)
                        .officerSessionId(officerSessionId)
                        .build();
                        processListUserOwnCourseRepository.save(postUserOwnCourse);

                        // 해당 과정의 과목 목록 확인인
                        List<ProcessListCourseOwnSubject> courseOwnSubjects = processListCourseOwnSubjectRepository.findByCourseId(courseId);

                        for(ProcessListCourseOwnSubject courseOwnSubject : courseOwnSubjects){

                            // 해당 과목이 개설된 과목인지 확인인
                            Optional<ProcessListOfferedSubjects> offeredSubject = processListOfferedSubjectsRepository.findBySubjectId(courseOwnSubject.getSubjectId());

                            if(offeredSubject.isPresent()){

                                // 사용자가 듣고 있는 과목이 있는지 확인인
                                List<ProcessListUserOwnAssignment> userOwnAssignments = processListUserOwnAssignmentRepository.findByOfferedSubjectsIdAndUserSessionId(offeredSubject.get().getOfferedSubjectsId(), sessionId);

                                // 신청한 과목이 없거나 해당 과목 영상의 진행도가 모두 100 이상이면 참 아니면 거짓
                                boolean userSubject = false;
                                if(userOwnAssignments.isEmpty()){
                                    userSubject = true;
                                }else{
                                    List<ProcessListUserOwnSubjectVideo> userOwnSubjectVideos = processListUserOwnSubjectVideoRepository.findByUosvSessionIdAndUosvOfferedSubjectsId(sessionId, offeredSubject.get().getOfferedSubjectsId());

                                    for(ProcessListUserOwnSubjectVideo userOwnSubjectVideo : userOwnSubjectVideos){
                                        if(  Integer.parseInt(userOwnSubjectVideo.getProgress())  <100){
                                            userSubject = false;
                                            break;
                                        }else{
                                            userSubject = true;
                                        }
                                    }
                                }

                                if(userSubject){
                                    UserOwnAssignment postUserOwnAssignment = UserOwnAssignment.builder()
                                    .userSessionId(sessionId)
                                    .offeredSubjectsId(offeredSubject.get().getOfferedSubjectsId())
                                    .build();
                                    processListUserOwnAssignmentRepository.save(postUserOwnAssignment);

                                    // 해당 과목의 영상 목록 확인
                                    List<ProcessListSubjectOwnVideo> subjectOwnVideos = processListSubjectOwnVideoRepository.findBySovOfferedSubjectsId(offeredSubject.get().getOfferedSubjectsId());
                                    for(ProcessListSubjectOwnVideo subjectOwnVideo : subjectOwnVideos){
                                        UserOwnSubjectVideo postUserSubjectOwnVideo = UserOwnSubjectVideo.builder()
                                        .uosvSessionId(sessionId)
                                        .uosvEpisodeId(subjectOwnVideo.getEpisodeId())
                                        .uosvOfferedSubjectsId(subjectOwnVideo.getSovOfferedSubjectsId())
                                        .build();
                                        processListUserOwnSubjectVideoRepository.save(postUserSubjectOwnVideo);
                                    }
                                }
                            }else{
                                return ResponseEntity.status(HttpStatus.CONFLICT).body("개설 과목이 아닙니다");
                            }
                        }
                        return ResponseEntity.ok().body("수강신청 완료");
                    }
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("다른 과정을 듣고 있습니다.");
                }
                return ResponseEntity.status(HttpStatus.CONFLICT).body("수강신청 권한이 없습니다.");
            }
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body("권한이 없습니다.");
    }
}
