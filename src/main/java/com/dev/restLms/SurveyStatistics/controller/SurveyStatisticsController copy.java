// package com.dev.restLms.SurveyStatistics.controller;

// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;
// import java.util.Set;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.Sort;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.dev.restLms.SurveyStatistics.persistence.SurveyStatisticsCourseRepository;
// import com.dev.restLms.SurveyStatistics.persistence.SurveyStatisticsPermissionGroupRepository;
// import com.dev.restLms.SurveyStatistics.persistence.SurveyStatisticsSurveyExecutionRepository;
// import com.dev.restLms.SurveyStatistics.persistence.SurveyStatisticsSurveyOwnResultRepository;
// import com.dev.restLms.SurveyStatistics.persistence.SurveyStatisticsSurveyQuestionRepository;
// import com.dev.restLms.SurveyStatistics.persistence.SurveyStatisticsUserOwnPermssionGroupRepository;
// import com.dev.restLms.SurveyStatistics.projection.SurveyStatisticsCourse;
// import com.dev.restLms.SurveyStatistics.projection.SurveyStatisticsPermissionGroup;
// import com.dev.restLms.SurveyStatistics.projection.SurveyStatisticsSurveyExecution;
// import com.dev.restLms.SurveyStatistics.projection.SurveyStatisticsSurveyOwnResult;
// import com.dev.restLms.SurveyStatistics.projection.SurveyStatisticsSurveyQuestion;
// import com.dev.restLms.entity.Course;
// import com.dev.restLms.entity.UserOwnPermissionGroup;

// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.PostMapping;



// @RestController
// @RequestMapping("/surveyStatistics")
// @Tag(name = "SurveyStatisticsController", description = "책임자 만족도 통계")
// public class SurveyStatisticsController {

//     @Autowired
//     private SurveyStatisticsCourseRepository surveyStatisticsCourseRepository;

//     @Autowired
//     private SurveyStatisticsUserOwnPermssionGroupRepository surveyStatisticsUserOwnPermssionGroupRepository;

//     @Autowired
//     private SurveyStatisticsPermissionGroupRepository surveyStatisticsPermissionGroupRepository;

//     @Autowired
//     private SurveyStatisticsSurveyExecutionRepository surveyStatisticsSurveyExecutionRepository;

//     @Autowired
//     private SurveyStatisticsSurveyOwnResultRepository surveyStatisticsSurveyOwnResultRepository;

//     @Autowired
//     private SurveyStatisticsSurveyQuestionRepository surveyStatisticsSurveyQuestionRepository;

//     @GetMapping("/Courses")
//     @Operation(summary = "책임자의 과정 목록", description = "책임자가 맡은 과정 목록을 불러옵니다.")
//     public ResponseEntity<?> getOfficerCourse(
//         @RequestParam(defaultValue = "0") int page,
//         @RequestParam(defaultValue = "8") int size
//     ){

//         UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
//                                 .getContext().getAuthentication();
//                 // 유저 세션아이디 보안 컨텍스트에서 가져오기
//                 String sessionId = auth.getPrincipal().toString();

//         // 사용자 확인 
//         Optional<UserOwnPermissionGroup> permissionCheck = surveyStatisticsUserOwnPermssionGroupRepository.findBySessionId(sessionId);

//         // 사용자 권한 확인 
//         Optional<SurveyStatisticsPermissionGroup> permissionNameCheck = surveyStatisticsPermissionGroupRepository.findByPermissionGroupUuid(permissionCheck.get().getPermissionGroupUuid2());

//         String permissionName = permissionNameCheck.get().getPermissionName();

//         List<Map<String, Object>> resultList = new ArrayList<>();
        
//         if(permissionName.equals("OFFICER")){
            
//             List<SurveyStatisticsCourse> findOfficerCourses = surveyStatisticsCourseRepository.findBySessionId(sessionId, Sort.by(Sort.Direction.DESC, "courseEndDate"));

//             for(SurveyStatisticsCourse findOfficerCourse : findOfficerCourses){

//                 Optional<SurveyStatisticsSurveyExecution> findSurveyExecutionId = surveyStatisticsSurveyExecutionRepository.findBySessionIdAndCourseId(sessionId, findOfficerCourse.getCourseId());

//                 if(findSurveyExecutionId.isPresent()){

//                     HashMap<String, Object> courseMap = new HashMap<>();
//                     courseMap.put("courseTitle", findOfficerCourse.getCourseTitle());
//                     courseMap.put("courseId", findOfficerCourse.getCourseId());
//                     courseMap.put("executionId", findSurveyExecutionId.get().getSurveyExecutionId());
//                     resultList.add(courseMap);

//                 }

//             }

//             // 페이징 처리
//             int totalItems = resultList.size();
//             int totalPages = (int) Math.ceil((double) totalItems / size);
//             int start = page * size;
//             int end = Math.min(start + size, totalItems);

//             List<Map<String, Object>> pagedResultList = resultList.subList(start, end);

//             Map<String, Object> response = new HashMap<>();
//             response.put("officerCourse", pagedResultList);
//             response.put("currentPage", page);
//             response.put("totalItems", totalItems);
//             response.put("totalPages", totalPages);

//             return ResponseEntity.ok().body(response);

//         }

//         return ResponseEntity.status(HttpStatus.CONFLICT).body("권한이 없습니다.");
//     }

//     @PostMapping()
//     @Operation(summary = "책임자의 과정 목록에서의 과정 검색", description = "책임자가 검색한 과정을 불러옵니다.")
//     public ResponseEntity<?> postSearchCourse(
//         @RequestParam String courseTitle,
//         @RequestParam(defaultValue = "0") int page,
//         @RequestParam(defaultValue = "8") int size
//         ) {

//         UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
//                                 .getContext().getAuthentication();
//         String sessionId = auth.getPrincipal().toString();

//         // Containing 메서드를 사용하여 부분 문자열 검색
//         List<Course> findCourses = surveyStatisticsCourseRepository.findByCourseTitleContainingAndSessionId(courseTitle, sessionId, Sort.by(Sort.Direction.DESC, "courseEndDate"));

//         List<Map<String, Object>> resultList = new ArrayList<>();

//         for(Course findCourse : findCourses){

//             // 사용자 세션 아이디와 , 과정 코드를 통해 만족도 조사 실시 번호가 있는지 조회 
//             Optional<SurveyStatisticsSurveyExecution> findSurveyExecutionId = surveyStatisticsSurveyExecutionRepository.findBySessionIdAndCourseId(sessionId, findCourse.getCourseId());

//             if(findSurveyExecutionId.isPresent()){

//                 HashMap<String, Object> courseMap = new HashMap<>();
//                 courseMap.put("courseTitle", findCourse.getCourseTitle());
//                 courseMap.put("courseId", findCourse.getCourseId());
//                 courseMap.put("executionId", findSurveyExecutionId.get().getSurveyExecutionId());

//                 resultList.add(courseMap);

//             }
            
//         }

//         // 페이징 처리
//         int totalItems = resultList.size();
//         int totalPages = (int) Math.ceil((double) totalItems / size);
//         int start = page * size;
//         int end = Math.min(start + size, totalItems);

//         List<Map<String, Object>> pagedResultList = resultList.subList(start, end);

//         Map<String, Object> response = new HashMap<>();
//         response.put("officerCourse", pagedResultList);
//         response.put("currentPage", page);
//         response.put("totalItems", totalItems);
//         response.put("totalPages", totalPages);

//         return ResponseEntity.ok().body(response);

//     }

//     @GetMapping("/Subjects")
//     public ResponseEntity<?> getOfficerSubjects(@RequestParam String param) {
//         return null;
//     }
    

//     @GetMapping("/courseStatistics")
//     @Operation(summary = "해당 과정의 만족도 통계 문항 조회", description = "해당 과정의 만족도 통계 문항 값을 불러옵니다.")
//     public ResponseEntity<?> getcourseStatistics(
//         @RequestParam String surveyExecutionId
//         ) {
            
//         // 결과를 저장할 리스트
//         List<Map<String, Object>> resultList = new ArrayList<>();

//         // 만족도 조사 결과 테이블에서 만족도조사 실시번호와 책임자 세션 아이디들 통해 문항 번호 확인
//         List<SurveyStatisticsSurveyOwnResult> findResults = surveyStatisticsSurveyOwnResultRepository.findBySurveyExecutionId(surveyExecutionId);

//         // Set을 사용하여 중복된 surveyQuestionId를 제거합니다.
//         Set<String> uniqueSurveyQuestionIds = new HashSet<>();

//         for (SurveyStatisticsSurveyOwnResult findResult : findResults) {
//             uniqueSurveyQuestionIds.add(findResult.getSurveyQuestionId());
//         }
//         // 중복된 값이 제거된 후의 리스트 
//         List<String> surveyQuestionIds = new ArrayList<>(uniqueSurveyQuestionIds);

//         for(String surveyQuestionId : surveyQuestionIds){

//             Optional<SurveyStatisticsSurveyQuestion> findQuestionData = surveyStatisticsSurveyQuestionRepository.findBySurveyQuestionId(surveyQuestionId);

//             if(findQuestionData.isPresent()){

//                 HashMap<String, Object> questionMap = new HashMap<>();
//                 questionMap.put("surveyData", findQuestionData.get().getQuestionData());
//                 questionMap.put("surveyQuestionId", surveyQuestionId);
//                 questionMap.put("surveyExecutionId", surveyExecutionId);
//                 resultList.add(questionMap);

//             }

//         }

//         // a는 resultList의 첫번째 요소, b는 resultList의 두번째 요소
//         // compareTo 메서드를 사용해서 문자열을 비교하면서 sort메소드를 통해 내림차순으로 정렬
//         resultList.sort((a, b) -> a.get("surveyQuestionId").toString().compareTo(b.get("surveyQuestionId").toString()));

//         return ResponseEntity.ok().body(resultList);
//     }
    
    
    
// }
