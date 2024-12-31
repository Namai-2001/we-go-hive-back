package com.dev.restLms.IndividualConductSurvey.controller;

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
import com.dev.restLms.IndividualConductSurvey.persistence.IndividualConductSurveyCourseOwnSubjectRepository;
import com.dev.restLms.IndividualConductSurvey.persistence.IndividualConductSurveyExecutionRepository;
import com.dev.restLms.IndividualConductSurvey.persistence.IndividualConductSurveyOfferedSubjectsRepository;
import com.dev.restLms.IndividualConductSurvey.persistence.IndividualConductSurveyOwnAnswerRepository;
import com.dev.restLms.IndividualConductSurvey.persistence.IndividualConductSurveyOwnResultRepository;
import com.dev.restLms.IndividualConductSurvey.persistence.IndividualConductSurveyQuestionRepository;
import com.dev.restLms.IndividualConductSurvey.persistence.IndividualConductSurveySubjectRepository;
import com.dev.restLms.IndividualConductSurvey.persistence.IndividualConductSurveyUserOwnAssignmentRepository;
import com.dev.restLms.IndividualConductSurvey.projection.IndividualConductSurveyCourseOwnSubject;
import com.dev.restLms.IndividualConductSurvey.projection.IndividualConductSurveyOfferedSubjects;
import com.dev.restLms.IndividualConductSurvey.projection.IndividualConductSurveyQuestion;
import com.dev.restLms.IndividualConductSurvey.projection.IndividualConductSurveySubject;
import com.dev.restLms.IndividualConductSurvey.projection.IndividualConductSurveyUserOwnAssignment;
import com.dev.restLms.entity.SurveyExecution;
import com.dev.restLms.entity.SurveyOwnAnswer;
import com.dev.restLms.entity.SurveyOwnResult;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/individualConductSurvey")
@Tag(name = "IndividualConductSurveyController", description = "개별 과목 책임자 만족도 조사 실시")
public class IndividualConductSurveyController {

    @Autowired
    private IndividualConductSurveyCourseOwnSubjectRepository individualConductSurveyCourseOwnSubjectRepository;

    @Autowired
    private IndividualConductSurveyOfferedSubjectsRepository individualConductSurveyOfferedSubjectsRepository;

    @Autowired
    private IndividualConductSurveyExecutionRepository individualConductSurveyExecutionRepository;

    @Autowired
    private IndividualConductSurveySubjectRepository individualConductSurveySubjectRepository;

    @Autowired
    private IndividualConductSurveyQuestionRepository individualConductSurveyQuestionRepository;

    @Autowired
    private IndividualConductSurveyUserOwnAssignmentRepository individualConductSurveyUserOwnAssignmentRepository;

    @Autowired
    private IndividualConductSurveyOwnAnswerRepository individualConductSurveyOwnAnswerRepository;

    @Autowired
    private IndividualConductSurveyOwnResultRepository individualConductSurveyOwnResultRepository;

    // @GetMapping("/Subject")
    // @Operation(summary = "개별 과목 책임자의 과목 목록", description = "개별 과목 책임자의 과목 목록을 불러옵니다")
    // public ResponseEntity<?> getOfficerSubjects(
    //     @RequestParam(defaultValue = "0") int page,
    //     @RequestParam(defaultValue = "7") int size
    // ) {

    //     try {

    //         UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
    //                             .getContext().getAuthentication();
    //             // 유저 세션아이디 보안 컨텍스트에서 가져오기
    //             String sessionId = auth.getPrincipal().toString();
            
    //             List<Map<String, Object>> resultList = new ArrayList<>();

    //             List<IndividualConductSurveyCourseOwnSubject> findCourseOwnSubjects = individualConductSurveyCourseOwnSubjectRepository.findByCourseIdAndOfficerSessionIdAndSubjectApproval("individual-subjects", sessionId, "T");

    //             for(IndividualConductSurveyCourseOwnSubject findCourseOwnSubject : findCourseOwnSubjects){

    //                 Optional<IndividualConductSurveyOfferedSubjects> findOfferedSubejctId = individualConductSurveyOfferedSubjectsRepository.findByCourseIdAndSubjectIdAndOfficerSessionId("individual-subjects", findCourseOwnSubject.getSubjectId(), sessionId);

    //                 if(findOfferedSubejctId.isPresent()){

    //                     Optional<IndividualConductSurveyExecution> findSurveyExecution = individualConductSurveyExecutionRepository.findByOfferedSubjectsIdAndSessionId(findOfferedSubejctId.get().getOfferedSubjectsId(), sessionId);
    
    //                     if(findSurveyExecution.isEmpty()){
    
    //                         Optional<IndividualConductSurveySubject> findSubjectName = individualConductSurveySubjectRepository.findBySubjectId(findCourseOwnSubject.getSubjectId());
    
    //                         HashMap<String, Object> subjectMap = new HashMap<>();
    //                         subjectMap.put("subjectName", findSubjectName.get().getSubjectName());
    //                         subjectMap.put("subjectId", findCourseOwnSubject.getSubjectId());
    //                         subjectMap.put("courseId", "individual-subjects");
    //                         subjectMap.put("subjectSurveyCheck", "none");
    
    //                         resultList.add(subjectMap);
    
    //                     }else{
    
    //                         Optional<IndividualConductSurveySubject> findSubjectName = individualConductSurveySubjectRepository.findBySubjectId(findCourseOwnSubject.getSubjectId());
    
    //                         HashMap<String, Object> subjectMap = new HashMap<>();
    //                         subjectMap.put("subjectName", findSubjectName.get().getSubjectName());
    //                         subjectMap.put("subjectId", findCourseOwnSubject.getSubjectId());
    //                         subjectMap.put("courseId", "individual-subjects");
    //                         subjectMap.put("subjectSurveyCheck", "done");
    
    //                         resultList.add(subjectMap);
    
    //                     }

    //                 }

    //             }

    //             // compareTo 메서드를 사용해서 문자열을 비교하면서 sort메소드를 통해 정렬
    //             resultList.sort((a, b) -> a.get("subjectName").toString().compareTo(b.get("subjectName").toString()));
                
    //             return ResponseEntity.ok().body(resultList);


    //     } catch (Exception e) {
    //            return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : "+e.getMessage());
    //     }

    // }

    @PostMapping("/serachSubject")
    @Operation(summary = "개별 과목 검색")
    public ResponseEntity<?> serachSubject(
        @RequestParam String subjectName,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "7") int size
    ) {
        try {
            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                    .getContext().getAuthentication();
            String sessionId = auth.getPrincipal().toString();

            List<Map<String, Object>> resultList = new ArrayList<>();

            List<IndividualConductSurveySubject> findSubjectIds = individualConductSurveySubjectRepository
                    .findBySubjectNameContaining(subjectName, Sort.by(Sort.Direction.ASC, "subjectName"));

            for (IndividualConductSurveySubject findSubjectId : findSubjectIds) {
                Optional<IndividualConductSurveyCourseOwnSubject> subjectCheck = 
                    individualConductSurveyCourseOwnSubjectRepository
                    .findBySubjectIdAndOfficerSessionIdAndSubjectApproval(findSubjectId.getSubjectId(), sessionId, "T");

                if (subjectCheck.isPresent() && subjectCheck.get().getCourseId().equals("individual-subjects")) {

                    Map<String, Object> subjectMap = new HashMap<>();
                    subjectMap.put("subjectName", findSubjectId.getSubjectName());
                    subjectMap.put("subjectId", findSubjectId.getSubjectId());
                    subjectMap.put("courseId", "individual-subjects");
    
                    resultList.add(subjectMap);

                }
            }

            // 페이징 처리
            int totalItems = resultList.size();
            int start = Math.min(page * size, totalItems);
            int end = Math.min((page + 1) * size, totalItems);
            List<Map<String, Object>> pagedResult = resultList.subList(start, end);

            // 전체 페이지 수 계산
            int totalPages = (int) Math.ceil((double) totalItems / size);

            // 결과를 포함한 Map 생성
            Map<String, Object> response = new HashMap<>();
            response.put("currentPage", page);
            response.put("totalItems", totalItems);
            response.put("totalPages", totalPages);
            response.put("content", pagedResult);

            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " + e.getMessage());
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
                Page<IndividualConductSurveyQuestion> findCourseQuestions = individualConductSurveyQuestionRepository.findBySurveyCategoryAndQuestionInactive("subject", "F", pageable);

                List<Map<String, Object>> resultList = new ArrayList<>();

                for(IndividualConductSurveyQuestion findCourseQuestion : findCourseQuestions){

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
                Page<IndividualConductSurveyQuestion> findCourseQuestions = individualConductSurveyQuestionRepository.findByQuestionDataContainingAndSurveyCategoryAndQuestionInactive(questionData, "subject", "F", pageable);
                List<Map<String, Object>> resultList = new ArrayList<>();

                for(IndividualConductSurveyQuestion findCourseQuestion : findCourseQuestions){

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
    @Operation(summary = "과목 만족도 조사 실시")
    public ResponseEntity<?> postSubjectQuestion(
        @RequestParam String subjectId,
        
        @RequestBody List<checkQuestionDto>  subjectQuestions
        ) {

            try {

                UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                String sessionId = auth.getPrincipal().toString();

                Optional<IndividualConductSurveyOfferedSubjects> findOfferedSubjectId = individualConductSurveyOfferedSubjectsRepository.findByCourseIdAndSubjectIdAndOfficerSessionId("individual-subjects", subjectId, sessionId);

                if(findOfferedSubjectId.isPresent()){

                    List<IndividualConductSurveyUserOwnAssignment> findSubjectUsers = individualConductSurveyUserOwnAssignmentRepository.findByOfferedSubjectsId(findOfferedSubjectId.get().getOfferedSubjectsId());

                    if(findSubjectUsers.isEmpty()){
                        return ResponseEntity.status(HttpStatus.CONFLICT).body("해당 과목을 듣는 수강생이 없습니다.");
                    }

                    SurveyExecution postSurveyExecution = SurveyExecution.builder()
                    .offeredSubjectsId(findOfferedSubjectId.get().getOfferedSubjectsId())
                    .sessionId(sessionId)
                    .build();

                    individualConductSurveyExecutionRepository.save(postSurveyExecution);

                    for(IndividualConductSurveyUserOwnAssignment findSubejctUser : findSubjectUsers){

                        for(checkQuestionDto questionDto : subjectQuestions){

                            SurveyOwnAnswer postSurveyOwnAnswer = SurveyOwnAnswer.builder()
                            .surveyQuestionId(questionDto.getSurveyQuestionId())
                            .build();

                            individualConductSurveyOwnAnswerRepository.save(postSurveyOwnAnswer);

                            SurveyOwnResult postSurveyOwnResult = SurveyOwnResult.builder()
                            .surveyExecutionId(postSurveyExecution.getSurveyExecutionId())
                            .sessionId(findSubejctUser.getUserSessionId())
                            .surveyQuestionId(questionDto.getSurveyQuestionId())
                            .surveyAnswerId(postSurveyOwnAnswer.getSurveyAnswerId())
                            .build();

                            individualConductSurveyOwnResultRepository.save(postSurveyOwnResult);

                        }

                    }

                }

                List<IndividualConductSurveyUserOwnAssignment> findSubjectUsers = individualConductSurveyUserOwnAssignmentRepository.findByOfferedSubjectsId(findOfferedSubjectId.get().getOfferedSubjectsId());

                int userSize = findSubjectUsers.size();

                Optional<IndividualConductSurveySubject> findSubjectName = individualConductSurveySubjectRepository.findBySubjectId(subjectId);
                String subjectName = null;
                if(findSubjectName.isPresent()){
                    subjectName = findSubjectName.get().getSubjectName();
                }

                return ResponseEntity.ok().body(subjectName+"과목에 대한 " + userSize + "명의 과목 만족도 조사 실행");

                
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : "+e.getMessage());
            }
        
    }
    
    
    
}
