// package com.dev.restLms.deleteCourse.controller;

// import java.time.LocalDateTime;
// import java.time.format.DateTimeFormatter;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.PageRequest;
// import org.springframework.data.domain.Pageable;
// import org.springframework.data.domain.Sort;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// import com.dev.restLms.deleteCourse.persistence.DeleteCourseAssignmentRepository;
// import com.dev.restLms.deleteCourse.persistence.DeleteCourseBoardPostRepository;
// import com.dev.restLms.deleteCourse.persistence.DeleteCourseBoardRepository;
// import com.dev.restLms.deleteCourse.persistence.DeleteCourseCommentRepository;
// import com.dev.restLms.deleteCourse.persistence.DeleteCourseFileinfoRepository;
// import com.dev.restLms.deleteCourse.persistence.DeleteCourseOfferedSubjectsRepository;
// import com.dev.restLms.deleteCourse.persistence.DeleteCourseOwnSubjectRepository;
// import com.dev.restLms.deleteCourse.persistence.DeleteCourseRepository;
// import com.dev.restLms.deleteCourse.persistence.DeleteCourseUserOwnAssignmentEvaluationRepository;
// import com.dev.restLms.deleteCourse.persistence.DeleteCourseUserOwnAssignmentRepository;
// import com.dev.restLms.deleteCourse.persistence.DeleteCourseUserOwnCourseRepository;
// import com.dev.restLms.deleteCourse.persistence.DeleteCourseUserOwnSubjectVideoRepository;
// import com.dev.restLms.deleteCourse.projection.DeleteCourse;
// import com.dev.restLms.deleteCourse.projection.DeleteCourseAssignment;
// import com.dev.restLms.deleteCourse.projection.DeleteCourseBoard;
// import com.dev.restLms.deleteCourse.projection.DeleteCourseBoardPost;
// import com.dev.restLms.deleteCourse.projection.DeleteCourseOfferedSubjects;
// import com.dev.restLms.deleteCourse.projection.DeleteCourseOwnSubject;

// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.GetMapping;




// @RestController
// @RequestMapping("/deleteCourse")
// @Tag(name = "DeleteCourseController", description = "관리자 과정 삭제 페이지")
// public class DeleteCourseController {

//     @Autowired
//     private DeleteCourseRepository deleteCourseRepository;

//     @Autowired
//     private DeleteCourseFileinfoRepository deleteCourseFileinfoRepository;

//     @Autowired
//     private DeleteCourseUserOwnCourseRepository deleteCourseUserOwnCourseRepository;

//     @Autowired
//     private DeleteCourseOwnSubjectRepository deleteCourseOwnSubjectRepository;

//     @Autowired
//     private DeleteCourseOfferedSubjectsRepository deleteCourseOfferedSubjectsRepository;

//     @Autowired
//     private DeleteCourseUserOwnSubjectVideoRepository deleteCourseUserOwnSubjectVideoRepository;

//     @Autowired
//     private DeleteCourseAssignmentRepository deleteCourseAssignmentRepository;

//     @Autowired
//     private DeleteCourseUserOwnAssignmentRepository deleteCourseUserOwnAssignmentRepository;

//     @Autowired
//     private DeleteCourseBoardRepository deleteCourseBoardRepository;

//     @Autowired
//     private DeleteCourseBoardPostRepository deleteCourseBoardPostRepository;

//     @Autowired
//     private DeleteCourseCommentRepository deleteCourseCommentRepository;

//     @Autowired
//     private DeleteCourseUserOwnAssignmentEvaluationRepository deleteCourseUserOwnAssignmentEvaluationRepository;

//     @GetMapping("/Courses")
//     @Operation(summary = "책임자의 과정 목록", description = "책임자의 과정 목록을 불러옵니다.")
//     public ResponseEntity<?> getCourses(
//         @RequestParam(defaultValue = "0") int page,
//         @RequestParam(defaultValue = "7") int size
//     ) {

//         try {

//             UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
//                                 .getContext().getAuthentication();
//             // 유저 세션아이디 보안 컨텍스트에서 가져오기
//             String sessionId = auth.getPrincipal().toString();

//             List<Map<String, Object>> resultList = new ArrayList<>();

//             Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "courseStartDate").and(Sort.by(Sort.Direction.ASC, "courseTitle")));
//             Page<DeleteCourse> findCourses = deleteCourseRepository.findBySessionId(sessionId, pageable);

//             for(DeleteCourse findCourse : findCourses){
                
//                 HashMap<String, Object> courseMap = new HashMap<>();
//                 courseMap.put("courseTitle", findCourse.getCourseTitle());
//                 courseMap.put("courseStartDate", findCourse.getCourseStartDate());
//                 courseMap.put("courseId", findCourse.getCourseId());

//                 resultList.add(courseMap);

//             }

//             Map<String, Object> response = new HashMap<>();
//             response.put("content", resultList);
//             response.put("currentPage", findCourses.getNumber());
//             response.put("totalItems", findCourses.getTotalElements());
//             response.put("totalPages", findCourses.getTotalPages());

//             return ResponseEntity.ok().body(response);
            
//         } catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " +e.getMessage());
//         }
//     }

//     @PostMapping("/searchCourse")
//     @Operation(summary = "책임자의 과정 검색", description = "검색한 책임자의 과정 목록을 불러옵니다.")
//     public ResponseEntity<?> sreachCourse(
//         @RequestParam String courseTitle,
//         @RequestParam(defaultValue = "0") int page,
//         @RequestParam(defaultValue = "7") int size
//         ) {

//             try {

//                 UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
//                                 .getContext().getAuthentication();
//                 // 유저 세션아이디 보안 컨텍스트에서 가져오기
//                 String sessionId = auth.getPrincipal().toString();

//                 List<Map<String, Object>> resultList = new ArrayList<>();

//                 Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "courseStartDate").and(Sort.by(Sort.Direction.ASC, "courseTitle")));
//                 Page<DeleteCourse> findCourses = deleteCourseRepository.findBySessionIdAndCourseTitleContaining(sessionId, courseTitle, pageable);

//                 for(DeleteCourse findCourse : findCourses){
                    
//                     HashMap<String, Object> courseMap = new HashMap<>();
//                     courseMap.put("courseTitle", findCourse.getCourseTitle());
//                     courseMap.put("courseStartDate", findCourse.getCourseStartDate());
//                     courseMap.put("courseId", findCourse.getCourseId());

//                     resultList.add(courseMap);

//                 }

//                 Map<String, Object> response = new HashMap<>();
//                 response.put("content", resultList);
//                 response.put("currentPage", findCourses.getNumber());
//                 response.put("totalItems", findCourses.getTotalElements());
//                 response.put("totalPages", findCourses.getTotalPages());

//                 return ResponseEntity.ok().body(response);
                
//             } catch (Exception e) {
//                 return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " +e.getMessage());
//             }
//     }

//     @PostMapping("/deleteCourse")
//     @Operation(summary = "책임자의 과정 삭제", description = "과정을 삭제합니다.")
//     public ResponseEntity<?> deleteCourse(
//         @RequestParam String courseId
//         ) {

//             try {

//                 UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
//                                 .getContext().getAuthentication();
//                 // 유저 세션아이디 보안 컨텍스트에서 가져오기
//                 String sessionId = auth.getPrincipal().toString();

//                 Optional<DeleteCourse> findCourse = deleteCourseRepository.findByCourseIdAndSessionId(courseId, sessionId);
                
//                 if(findCourse.isPresent()){
                    
//                     // 과정을 삭제할 수 없는 조건
//                     Long nowDate = Long.parseLong(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));
//                     if(Long.parseLong(findCourse.get().getCourseStartDate()) <= nowDate){
//                         return ResponseEntity.status(HttpStatus.CONFLICT).body("교육이 시작되어 삭제할 수 없습니다.");
//                     }
                    
//                     // 과정 신청 기간에 수료한 사람이 있다면 해당 사용자에 할당된 관련 정보를 삭제
//                     boolean findStudents = deleteCourseUserOwnCourseRepository.existsByCourseIdAndOfficerSessionId(courseId, sessionId);

//                     if(findStudents){

//                         List<DeleteCourseOwnSubject> findSubjectIds = deleteCourseOwnSubjectRepository.findByCourseIdAndOfficerSessionId(courseId, sessionId);
//                         for(DeleteCourseOwnSubject findSubjectId : findSubjectIds){

//                             Optional<DeleteCourseOfferedSubjects> findOfferedSubjectId = deleteCourseOfferedSubjectsRepository.findBySubjectIdAndCourseIdAndOfficerSessionId(findSubjectId.getSubjectId(), courseId, sessionId);

//                             if(findOfferedSubjectId.isPresent()){

//                                 if(deleteCourseUserOwnSubjectVideoRepository.existsByUosvOfferedSubjectsId(findOfferedSubjectId.get().getOfferedSubjectsId())){

//                                     // 사용자별 과목 영상 목록 삭제 
//                                     deleteCourseUserOwnSubjectVideoRepository.deleteByUosvOfferedSubjectsId(findOfferedSubjectId.get().getOfferedSubjectsId());

//                                 }

//                                 if(deleteCourseAssignmentRepository.existsByOfferedSubjectsId(findOfferedSubjectId.get().getOfferedSubjectsId())){

//                                     List<DeleteCourseAssignment> findAssignmentIds = deleteCourseAssignmentRepository.findByOfferedSubjectsId(findOfferedSubjectId.get().getOfferedSubjectsId());

//                                     for(DeleteCourseAssignment findAssignmentId : findAssignmentIds){

//                                         if(deleteCourseUserOwnAssignmentEvaluationRepository.existsByAssignmentId(findAssignmentId.getAssignmentId())){

//                                             // 사용자별 과제 목록 삭제 
//                                             deleteCourseUserOwnAssignmentEvaluationRepository.deleteByAssignmentId(findAssignmentId.getAssignmentId());

//                                         }

//                                         // 해당 과목의 과제 삭제 
//                                         deleteCourseAssignmentRepository.deleteById(findAssignmentId.getAssignmentId());

//                                     }
//                                 }

//                                 Optional<DeleteCourseBoard> findboardId = deleteCourseBoardRepository.findByOfferedSubjectsId(findOfferedSubjectId.get().getOfferedSubjectsId());

//                                 if(findboardId.isPresent()){

//                                     if(deleteCourseBoardPostRepository.existsByBoardId(findboardId.get().getBoardId())){

//                                         List<DeleteCourseBoardPost> findPostIds = deleteCourseBoardPostRepository.findByBoardId(findboardId.get().getBoardId());

//                                         for(DeleteCourseBoardPost findPostId : findPostIds){

//                                             if(deleteCourseCommentRepository.existsByPostId(findPostId.getPostId())){

//                                                 // 해당 게시판의 댓글들 삭제
//                                                 deleteCourseCommentRepository.deleteByPostId(findPostId.getPostId());

//                                             }

//                                             if(findPostId.getFileNo().isEmpty()){

//                                                 // 게시글 삭제
//                                                 deleteCourseBoardPostRepository.deleteById(findPostId.getPostId());

//                                             }else{

//                                                 // 게시글의 파일 삭제 
//                                                 deleteCourseFileinfoRepository.deleteById(findPostId.getFileNo());
//                                                 // 게시글 삭제 
//                                                 deleteCourseBoardPostRepository.deleteById(findPostId.getPostId());

//                                             }
//                                         }

//                                         // 게시판 삭제
//                                         deleteCourseBoardRepository.deleteById(findboardId.get().getBoardId());

//                                     }
//                                 }

//                                 if(deleteCourseUserOwnAssignmentRepository.existsByOfferedSubjectsId(findOfferedSubjectId.get().getOfferedSubjectsId())){

//                                     // 사용자별 과목 목록 삭제
//                                     deleteCourseUserOwnAssignmentRepository.deleteByOfferedSubjectsId(findOfferedSubjectId.get().getOfferedSubjectsId());

//                                 }
//                             }
//                         }

//                         // 사용자별 과정 목록 삭제
//                         deleteCourseUserOwnCourseRepository.deleteByCourseId(courseId);
                        
//                     }
//                     // 과정의 과목 목록 삭제
//                     deleteCourseOwnSubjectRepository.deleteByCourseId(courseId);

//                     // 과정 삭제
//                     deleteCourseRepository.deleteById(courseId);

//                     return ResponseEntity.ok().body(findCourse.get().getCourseTitle()+"과정 삭제 완료");

//                 }else{
//                     return ResponseEntity.status(HttpStatus.CONFLICT).body("삭제 할 수 없습니다.");
//                 }
                
//             } catch (Exception e) {
//                 return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " +e.getMessage());
//             }
        
//     }
    
    
    
    
    
// }
