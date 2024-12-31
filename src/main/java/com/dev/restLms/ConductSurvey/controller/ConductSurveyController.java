package com.dev.restLms.ConductSurvey.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.ConductSurvey.dto.checkQuestionDto;
import com.dev.restLms.ConductSurvey.persistence.ConductSurveyCourseOwnSubjectRepository;
import com.dev.restLms.ConductSurvey.persistence.ConductSurveyCourseRepository;
import com.dev.restLms.ConductSurvey.persistence.ConductSurveyExecutionRepository;
import com.dev.restLms.ConductSurvey.persistence.ConductSurveyOfferedSubjectsRepository;
import com.dev.restLms.ConductSurvey.persistence.ConductSurveyOwnAnswerRepository;
import com.dev.restLms.ConductSurvey.persistence.ConductSurveyOwnResultRepository;
import com.dev.restLms.ConductSurvey.persistence.ConductSurveyQuestionRepository;
import com.dev.restLms.ConductSurvey.persistence.ConductSurveySubjectRepository;
import com.dev.restLms.ConductSurvey.persistence.ConductSurveyUserOwnCourseRepository;
import com.dev.restLms.ConductSurvey.projection.ConductSurveyCourse;
import com.dev.restLms.ConductSurvey.projection.ConductSurveyCourseOwnSubject;
import com.dev.restLms.ConductSurvey.projection.ConductSurveyExecution;
import com.dev.restLms.ConductSurvey.projection.ConductSurveyOfferedSubjects;
import com.dev.restLms.ConductSurvey.projection.ConductSurveyQuestion;
import com.dev.restLms.ConductSurvey.projection.ConductSurveySubject;
import com.dev.restLms.ConductSurvey.projection.ConductSurveyUserOwnCourse;
import com.dev.restLms.entity.SurveyExecution;
import com.dev.restLms.entity.SurveyOwnAnswer;
import com.dev.restLms.entity.SurveyOwnResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/conductSurvey")
@Tag(name = "ConductSurveyController", description = "책임자 만족도 조사 실시")
public class ConductSurveyController {
    
    @Autowired
    private ConductSurveyCourseRepository conductSurveyCourseRepository;

    @Autowired
    private ConductSurveyExecutionRepository conductSurveyExecutionRepository;

    @Autowired
    private ConductSurveyCourseOwnSubjectRepository conductSurveyCourseOwnSubjectRepository;

    @Autowired
    private ConductSurveyOfferedSubjectsRepository conductSurveyOfferedSubjectsRepository;

    @Autowired
    private ConductSurveySubjectRepository conductSurveySubjectRepository;

    @Autowired
    private ConductSurveyQuestionRepository conductSurveyQuestionRepository;

    @Autowired
    private ConductSurveyUserOwnCourseRepository conductSurveyUserOwnCourseRepository;

    @Autowired
    private ConductSurveyOwnAnswerRepository conductSurveyOwnAnswerRepository;

    @Autowired
    private ConductSurveyOwnResultRepository conductSurveyOwnResultRepository;

    @GetMapping("/Courses")
    @Operation(summary = "책임자의 과정 목록", description = "책임자가 맡은 과정 목록을 불러옵니다.")
    public ResponseEntity<?> getOfficerCourse(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "8") int size
        ) {

            try {

                UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String sessionId = auth.getPrincipal().toString();

                List<Map<String, Object>> resultList = new ArrayList<>();

                // 과정 시작 일자를 오름차순으로 책임자의 과정 목록 확인 
                List<ConductSurveyCourse> findOfficerCourses = conductSurveyCourseRepository.findBySessionId(sessionId, Sort.by(Sort.Direction.ASC, "courseStartDate"));

                Long nowDate = Long.parseLong(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

                for(ConductSurveyCourse findOfficerCourse : findOfficerCourses) {
                    
                    Long courseStartDate = Long.parseLong(findOfficerCourse.getCourseStartDate());

                    // 만족도 조사 실시는 과정이 시작한 뒤에만 할 수 있도록 if문 설정 
                    if(nowDate >= courseStartDate){

                        // 해당 과정이 만족도 조사를 실시 했는지 확인 
                        Optional<ConductSurveyExecution> officerCourseCheck =  conductSurveyExecutionRepository.findByCourseIdAndSessionIdAndOfferedSubjectsId(findOfficerCourse.getCourseId(), sessionId, null);
    
                        if(officerCourseCheck.isEmpty()){
    
                            HashMap<String, Object> courseMap = new HashMap<>();
                            courseMap.put("courseTitle", findOfficerCourse.getCourseTitle());
                            courseMap.put("courseId", findOfficerCourse.getCourseId());
                            courseMap.put("courseSurveyCheck", "none");
    
                            resultList.add(courseMap);
    
                        }else{

                            HashMap<String, Object> courseMap = new HashMap<>();
                            courseMap.put("courseTitle", findOfficerCourse.getCourseTitle());
                            courseMap.put("courseId", findOfficerCourse.getCourseId());
                            courseMap.put("courseSurveyCheck", "done");
    
                            resultList.add(courseMap);

                        }
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
                
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : "+ e.getMessage());
            }
    }

    @PostMapping("/searchCourses")
    @Operation(summary = "책임자의 과정 목록에서의 과정 검색", description = "책임자가 검색한 과정을 불러옵니다.")
    public ResponseEntity<?> postSearchCourse(
        @RequestParam String courseTitle,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "8") int size
        ) {

            try {

                UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                String sessionId = auth.getPrincipal().toString();

                List<ConductSurveyCourse> finsCourses = conductSurveyCourseRepository.findByCourseTitleContainingAndSessionId(courseTitle, sessionId, Sort.by(Sort.Direction.ASC, "courseStartDate"));

                List<Map<String, Object>> resultList = new ArrayList<>();

                Long nowDate = Long.parseLong(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")));

                for(ConductSurveyCourse findCourse : finsCourses){

                    Long courseStartDate = Long.parseLong(findCourse.getCourseStartDate());

                    if(nowDate >= courseStartDate){

                        // 해당 과정이 만족도 조사를 실시 했는지 확인 
                        Optional<ConductSurveyExecution> officerCourseCheck =  conductSurveyExecutionRepository.findByCourseIdAndSessionIdAndOfferedSubjectsId(findCourse.getCourseId(), sessionId, null);
        
                        if(officerCourseCheck.isEmpty()){

                            HashMap<String, Object> courseMap = new HashMap<>();
                            courseMap.put("courseTitle", findCourse.getCourseTitle());
                            courseMap.put("courseId", findCourse.getCourseId());
                            courseMap.put("courseSurveyCheck", "none");

                            resultList.add(courseMap);

                        }else{

                            HashMap<String, Object> courseMap = new HashMap<>();
                            courseMap.put("courseTitle", findCourse.getCourseTitle());
                            courseMap.put("courseId", findCourse.getCourseId());
                            courseMap.put("courseSurveyCheck", "done");

                            resultList.add(courseMap);

                        }
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
                
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : "+e.getMessage());
            }

    }

    @GetMapping("/getCourseQuestion")
    @Operation(summary = "과정에 대한 만족도 조사 문항", description = "과정에 대한 만족도 조사 문항을 불러옵니다.")
    public ResponseEntity<?> getCourseQuestion(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size
        ) {

            try {

                Sort sort = Sort.by(Sort.Direction.ASC, "answerCategory").and(Sort.by(Sort.Direction.ASC, "questionData"));
                Pageable pageable = PageRequest.of(page, size, sort);
                Page<ConductSurveyQuestion> findCourseQuestions = conductSurveyQuestionRepository.findBySurveyCategoryAndQuestionInactive("course", "F", pageable);

                List<Map<String, Object>> resultList = new ArrayList<>();

                for(ConductSurveyQuestion findCourseQuestion : findCourseQuestions){

                    Map<String, Object> courseQuestionMap = new HashMap<>();
                    courseQuestionMap.put("answerData", findCourseQuestion.getQuestionData());
                    courseQuestionMap.put("surveyQuestionId", findCourseQuestion.getSurveyQuestionId());
                    if(findCourseQuestion.getAnswerCategory().equals("T")){
                        courseQuestionMap.put("answerCategory", "5지 선다");
                    }else if(findCourseQuestion.getAnswerCategory().equals("F")){
                        courseQuestionMap.put("answerCategory", "서술형");
                    }

                    resultList.add(courseQuestionMap);

                }

                Map<String, Object> response = new HashMap<>();
                response.put("content", resultList);
                response.put("currentPage", findCourseQuestions.getNumber());
                response.put("totalItems", findCourseQuestions.getTotalElements());
                response.put("totalPages", findCourseQuestions.getTotalPages());
                
                return ResponseEntity.ok().body(response);

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " +e.getMessage());
            }
    }

    @PostMapping("/searchCourseQuestion")
    @Operation(summary = "과정에 대한 만족도 조사 문항 검색", description = "검색한 만족도 조사 문항을 불러옵니다.")
    public ResponseEntity<?> searchCourseQuestion(
        @RequestParam String questionData,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size
        ) {

            try {

                Sort sort = Sort.by(Sort.Direction.ASC, "answerCategory").and(Sort.by(Sort.Direction.ASC, "questionData"));
                Pageable pageable = PageRequest.of(page, size, sort);
                Page<ConductSurveyQuestion> findCourseQuestions = conductSurveyQuestionRepository.findByQuestionDataContainingAndSurveyCategoryAndQuestionInactive(questionData, "course", "F", pageable);
                List<Map<String, Object>> resultList = new ArrayList<>();

                for(ConductSurveyQuestion findCourseQuestion : findCourseQuestions){

                    Map<String, Object> courseQuestionMap = new HashMap<>();
                    courseQuestionMap.put("answerData", findCourseQuestion.getQuestionData());
                    courseQuestionMap.put("surveyQuestionId", findCourseQuestion.getSurveyQuestionId());
                    if(findCourseQuestion.getAnswerCategory().equals("T")){
                        courseQuestionMap.put("answerCategory", "5지 선다");
                    }else if(findCourseQuestion.getAnswerCategory().equals("F")){
                        courseQuestionMap.put("answerCategory", "서술형");
                    }

                    resultList.add(courseQuestionMap);

                }

                Map<String, Object> response = new HashMap<>();
                response.put("content", resultList);
                response.put("currentPage", findCourseQuestions.getNumber());
                response.put("totalItems", findCourseQuestions.getTotalElements());
                response.put("totalPages", findCourseQuestions.getTotalPages());
                
                return ResponseEntity.ok().body(response);
                
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : "+e.getMessage());
            }
    }

    @PostMapping("/postCourseQuestion")
    @Operation(summary = "과정에 대한 만족도 조사 실시", description = "과정에 대해 선택한 문항으로 만족도 조사를 실시합니다.")
    public ResponseEntity<?> postCourseQuestion(
        @RequestParam String courseId,
        @RequestBody List<checkQuestionDto>  courseQuestions
        ) {

            try {

                UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                String sessionId = auth.getPrincipal().toString();

                // 해당 과정을 듣는 사용자들 확인 
                List<ConductSurveyUserOwnCourse> findCourseUsers = conductSurveyUserOwnCourseRepository.findByCourseIdAndOfficerSessionId(courseId, sessionId);

                if(findCourseUsers.isEmpty()){
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("해당 과정의 수강생이 없습니다.");
                }

                SurveyExecution postSurveyExecution = SurveyExecution.builder()
                    .courseId(courseId)
                    .sessionId(sessionId)
                    .build();
                conductSurveyExecutionRepository.save(postSurveyExecution);

                for(ConductSurveyUserOwnCourse findCourseUser : findCourseUsers){

                    for(checkQuestionDto courseQuestion : courseQuestions){

                        SurveyOwnAnswer postSurveyOwnAnswer = SurveyOwnAnswer.builder()
                            .surveyQuestionId(courseQuestion.getSurveyQuestionId())
                            .build();
                        conductSurveyOwnAnswerRepository.save(postSurveyOwnAnswer);

                        SurveyOwnResult postSurveyOwnResult = SurveyOwnResult.builder()
                            .surveyExecutionId(postSurveyExecution.getSurveyExecutionId())
                            .sessionId(findCourseUser.getSessionId())
                            .surveyQuestionId(courseQuestion.getSurveyQuestionId())
                            .surveyAnswerId(postSurveyOwnAnswer.getSurveyAnswerId())
                            .build();
                        conductSurveyOwnResultRepository.save(postSurveyOwnResult);
    
                    }
                }

                int userSize = findCourseUsers.size();
                Optional<ConductSurveyCourse> findCourseTitle = conductSurveyCourseRepository.findByCourseId(courseId);
                String courseTitle = null;
                if(findCourseTitle.isPresent()){
                    courseTitle = findCourseTitle.get().getCourseTitle();
                }

                return ResponseEntity.ok().body(courseTitle+"과정에 대한 "+ userSize+"명의 과정 만족도 조사 실행");
                
            } catch (Exception e) {
               return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " + e.getMessage());
            }
    }
    
    
    // --------------------------------------------------------------------------------------------------

    @GetMapping("/Subjects")
    @Operation(summary = "책임자의 과정의 과목 목록", description = "책임자가 맡은 과정의 과목 목록을 불러옵니다")
    public ResponseEntity<?> getOfficerSubjects(
        @RequestParam String courseId
        ) {

            try {

                UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String sessionId = auth.getPrincipal().toString();

                List<Map<String, Object>> resultList = new ArrayList<>();

                // 해당 과정의 과목 목록 확인 
                List<ConductSurveyCourseOwnSubject> findCourseOwnSubjects = conductSurveyCourseOwnSubjectRepository.findByCourseIdAndOfficerSessionId(courseId, sessionId);

                for(ConductSurveyCourseOwnSubject findCourseOwnSubject : findCourseOwnSubjects){

                    // 해당 과목의 개설과목 코드 확인 
                    Optional<ConductSurveyOfferedSubjects> findOfferedSubejctId = conductSurveyOfferedSubjectsRepository.findByCourseIdAndSubjectIdAndOfficerSessionId(courseId, findCourseOwnSubject.getSubjectId(), sessionId);

                    if(findOfferedSubejctId.isPresent()){

                        // 해당 과정의 과목을 만족도 조사 실시했는지 확인 
                        Optional<ConductSurveyExecution> findSurveyExecution = conductSurveyExecutionRepository.findByCourseIdAndSessionIdAndOfferedSubjectsId(courseId, sessionId, findOfferedSubejctId.get().getOfferedSubjectsId());
    
                        if(findSurveyExecution.isEmpty()){
    
                            // 해당 과목의 이름을 확인 
                            Optional<ConductSurveySubject> findSubjectName = conductSurveySubjectRepository.findBySubjectId(findCourseOwnSubject.getSubjectId());
    
                            HashMap<String, Object> subjectMap = new HashMap<>();
                            subjectMap.put("subjectName", findSubjectName.get().getSubjectName());
                            subjectMap.put("subjectId", findCourseOwnSubject.getSubjectId());
                            subjectMap.put("courseId", courseId);
                            subjectMap.put("subjectSurveyCheck", "none");
    
                            resultList.add(subjectMap);
    
                        }else{
    
                            // 해당 과목의 이름을 확인 
                            Optional<ConductSurveySubject> findSubjectName = conductSurveySubjectRepository.findBySubjectId(findCourseOwnSubject.getSubjectId());
    
                            HashMap<String, Object> subjectMap = new HashMap<>();
                            subjectMap.put("subjectName", findSubjectName.get().getSubjectName());
                            subjectMap.put("subjectId", findCourseOwnSubject.getSubjectId());
                            subjectMap.put("courseId", courseId);
                            subjectMap.put("subjectSurveyCheck", "done");
    
                            resultList.add(subjectMap);
    
                        }
                        
                    }

                }

                // compareTo 메서드를 사용해서 문자열을 비교하면서 sort메소드를 통해 정렬
                resultList.sort((a, b) -> a.get("subjectName").toString().compareTo(b.get("subjectName").toString()));
                
                return ResponseEntity.ok().body(resultList);

            } catch (Exception e) {
               return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : "+e.getMessage());
            }
    }

    @GetMapping("/getSubjectQuestion")
    @Operation(summary = "과목에 대한 만족도 조사 문항", description = "과목에 대한 만족도 조사 문항을 불러옵니다.")
    public ResponseEntity<?> getSubjectQuestion(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size
        ) {

            try {

                Sort sort = Sort.by(Sort.Direction.ASC, "answerCategory").and(Sort.by(Sort.Direction.ASC, "questionData"));
                Pageable pageable = PageRequest.of(page, size, sort);
                Page<ConductSurveyQuestion> findCourseQuestions = conductSurveyQuestionRepository.findBySurveyCategoryAndQuestionInactive("subject", "F", pageable);

                List<Map<String, Object>> resultList = new ArrayList<>();

                for(ConductSurveyQuestion findCourseQuestion : findCourseQuestions){

                    Map<String, Object> courseQuestionMap = new HashMap<>();
                    courseQuestionMap.put("answerData", findCourseQuestion.getQuestionData());
                    courseQuestionMap.put("surveyQuestionId", findCourseQuestion.getSurveyQuestionId());
                    if(findCourseQuestion.getAnswerCategory().equals("T")){
                        courseQuestionMap.put("answerCategory", "5지 선다");
                    }else if(findCourseQuestion.getAnswerCategory().equals( "F")){
                        courseQuestionMap.put("answerCategory", "서술형");
                    }

                    resultList.add(courseQuestionMap);

                }

                Map<String, Object> response = new HashMap<>();
                response.put("content", resultList);
                response.put("currentPage", findCourseQuestions.getNumber());
                response.put("totalItems", findCourseQuestions.getTotalElements());
                response.put("totalPages", findCourseQuestions.getTotalPages());
                
                return ResponseEntity.ok().body(response);

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " +e.getMessage());
            }
    }

    @PostMapping("/searchSubjectQuestion")
    @Operation(summary = "과목에 대한 만족도 조사 문항 검색", description = "검색한 만족도 조사 문항을 불러옵니다.")
    public ResponseEntity<?> searchSubjectQuestion(
        @RequestParam String questionData,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size
        ) {

            try {

                Sort sort = Sort.by(Sort.Direction.ASC, "answerCategory").and(Sort.by(Sort.Direction.ASC, "questionData"));
                Pageable pageable = PageRequest.of(page, size, sort);
                Page<ConductSurveyQuestion> findCourseQuestions = conductSurveyQuestionRepository.findByQuestionDataContainingAndSurveyCategoryAndQuestionInactive(questionData, "subject", "F", pageable);
                List<Map<String, Object>> resultList = new ArrayList<>();

                for(ConductSurveyQuestion findCourseQuestion : findCourseQuestions){

                    Map<String, Object> courseQuestionMap = new HashMap<>();
                    courseQuestionMap.put("answerData", findCourseQuestion.getQuestionData());
                    courseQuestionMap.put("surveyQuestionId", findCourseQuestion.getSurveyQuestionId());
                    if(findCourseQuestion.getAnswerCategory().equals("T")){
                        courseQuestionMap.put("answerCategory", "5지 선다");
                    }else if(findCourseQuestion.getAnswerCategory().equals("F")){
                        courseQuestionMap.put("answerCategory", "서술형");
                    }

                    resultList.add(courseQuestionMap);

                }

                Map<String, Object> response = new HashMap<>();
                response.put("content", resultList);
                response.put("currentPage", findCourseQuestions.getNumber());
                response.put("totalItems", findCourseQuestions.getTotalElements());
                response.put("totalPages", findCourseQuestions.getTotalPages());
                
                return ResponseEntity.ok().body(response);
                
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : "+e.getMessage());
            }
    }

    @PostMapping("/postSubjectQuestion")
    @Operation(summary = "해당 과정의 과목에 대한 만족도 조사 실시", description = "해당 과정의 과목에 대해 선택한 문항으로 만족도 조사를 실시합니다.")
    public ResponseEntity<?> postSubjectQuestion(
        @RequestParam String courseId,
        @RequestParam String subjectId,
        @RequestBody List<checkQuestionDto>  courseQuestions
        ) {

            try {

                UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                String sessionId = auth.getPrincipal().toString();

                // 해당 과정을 듣는 사용자들 확인 
                List<ConductSurveyUserOwnCourse> findCourseUsers = conductSurveyUserOwnCourseRepository.findByCourseIdAndOfficerSessionId(courseId, sessionId);

                if(findCourseUsers.isEmpty()){
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("해당 과정의 수강생이 없습니다.");
                }

                Optional<ConductSurveyOfferedSubjects> findOfferedSubjectId = conductSurveyOfferedSubjectsRepository.findByCourseIdAndSubjectIdAndOfficerSessionId(courseId, subjectId, sessionId);

                if(findOfferedSubjectId.isPresent()){

                    SurveyExecution postSurveyExecution = SurveyExecution.builder()
                        .courseId(courseId)
                        .offeredSubjectsId(findOfferedSubjectId.get().getOfferedSubjectsId())
                        .sessionId(sessionId)
                        .build();
                    conductSurveyExecutionRepository.save(postSurveyExecution);
    
                    for(ConductSurveyUserOwnCourse findCourseUser : findCourseUsers){
    
                        for(checkQuestionDto courseQuestion : courseQuestions){
    
                            SurveyOwnAnswer postSurveyOwnAnswer = SurveyOwnAnswer.builder()
                                .surveyQuestionId(courseQuestion.getSurveyQuestionId())
                                .build();
                            conductSurveyOwnAnswerRepository.save(postSurveyOwnAnswer);
    
                            SurveyOwnResult postSurveyOwnResult = SurveyOwnResult.builder()
                                .surveyExecutionId(postSurveyExecution.getSurveyExecutionId())
                                .sessionId(findCourseUser.getSessionId())
                                .surveyQuestionId(courseQuestion.getSurveyQuestionId())
                                .surveyAnswerId(postSurveyOwnAnswer.getSurveyAnswerId())
                                .build();
                            conductSurveyOwnResultRepository.save(postSurveyOwnResult);
        
                        }
                    }
                }


                int userSize = findCourseUsers.size();

                Optional<ConductSurveyCourse> findCourseTitle = conductSurveyCourseRepository.findByCourseId(courseId);
                String courseTitle = null;
                if(findCourseTitle.isPresent()){
                    courseTitle = findCourseTitle.get().getCourseTitle();
                }

                Optional<ConductSurveySubject> findSubjectName = conductSurveySubjectRepository.findBySubjectId(subjectId);
                String subjectName = null;
                if(findSubjectName.isPresent()){
                    subjectName = findSubjectName.get().getSubjectName();
                }

                return ResponseEntity.ok().body(courseTitle+"과정의 " + subjectName +"과목에 대한 "+ userSize+"명의 과정 만족도 조사 실행");
                
            } catch (Exception e) {
               return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " + e.getMessage());
            }
    }

    
    
}
