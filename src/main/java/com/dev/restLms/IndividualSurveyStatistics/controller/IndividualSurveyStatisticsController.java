package com.dev.restLms.IndividualSurveyStatistics.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.IndividualSurveyStatistics.persistence.IndividualSurveyStatisticsCourseOwnSubjectRepository;
import com.dev.restLms.IndividualSurveyStatistics.persistence.IndividualSurveyStatisticsOfferedSubjectsRepository;
import com.dev.restLms.IndividualSurveyStatistics.persistence.IndividualSurveyStatisticsSubjectRepository;
import com.dev.restLms.IndividualSurveyStatistics.persistence.IndividualSurveyStatisticsSurveyExecutionRepository;
import com.dev.restLms.IndividualSurveyStatistics.persistence.IndividualSurveyStatisticsSurveyOwnResultRepository;
import com.dev.restLms.IndividualSurveyStatistics.persistence.IndividualSurveyStatisticsSurveyQuestionRepository;
import com.dev.restLms.IndividualSurveyStatistics.projection.IndividualSurveyStatisticsCourseOwnSubject;
import com.dev.restLms.IndividualSurveyStatistics.projection.IndividualSurveyStatisticsOfferedSubjects;
import com.dev.restLms.IndividualSurveyStatistics.projection.IndividualSurveyStatisticsSubject;
import com.dev.restLms.IndividualSurveyStatistics.projection.IndividualSurveyStatisticsSurveyOwnResult;
import com.dev.restLms.IndividualSurveyStatistics.projection.IndividualSurveyStatisticsSurveyQuestion;
import com.dev.restLms.entity.SurveyExecution;

import io.swagger.v3.oas.annotations.Operation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@RequestMapping("/individualSurveyStatistics")
public class IndividualSurveyStatisticsController {

    @Autowired
    private IndividualSurveyStatisticsSubjectRepository individualSurveyStatisticsSubjectRepository;

    @Autowired
    private IndividualSurveyStatisticsCourseOwnSubjectRepository individualSurveyStatisticsCourseOwnSubjectRepository;

    @Autowired
    private IndividualSurveyStatisticsOfferedSubjectsRepository individualSurveyStatisticsOfferedSubjectsRepository;

    @Autowired
    private IndividualSurveyStatisticsSurveyExecutionRepository individualSurveyStatisticsSurveyExecutionRepository;

    @Autowired
    private IndividualSurveyStatisticsSurveyOwnResultRepository individualSurveyStatisticsSurveyOwnResultRepository;

    @Autowired
    private IndividualSurveyStatisticsSurveyQuestionRepository individualSurveyStatisticsSurveyQuestionRepository;

    @PostMapping("/serachSubject")
    @Operation(summary = "과목 검색")
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

                List<IndividualSurveyStatisticsSubject> findSubjects = individualSurveyStatisticsSubjectRepository.findBySubjectNameContaining(subjectName, Sort.by(Sort.Direction.ASC,"subjectName"));
                
                for(IndividualSurveyStatisticsSubject findSubject :findSubjects){

                    // Optional<IndividualSurveyStatisticsCourseOwnSubject> subjectCheck = individualSurveyStatisticsCourseOwnSubjectRepository.findByCourseIdAndSubjectIdAndOfficerSessionIdAndSubjectApproval("individual-subjects", findSubject.getSubjectId(), sessionId, "T");

                    Optional<IndividualSurveyStatisticsCourseOwnSubject> subjectCheck = individualSurveyStatisticsCourseOwnSubjectRepository.findBySubjectId(findSubject.getSubjectId());

                    String courseId = subjectCheck.get().getCourseId();
                    String officerSessionId = subjectCheck.get().getOfficerSessionId();
                    String subjectApproval = subjectCheck.get().getSubjectApproval();

                    if(subjectCheck.isPresent() && courseId.equals("individual-subjects") && officerSessionId.equals(sessionId) && subjectApproval.equals("T")){

                        Optional<IndividualSurveyStatisticsOfferedSubjects> findOfferedSubjectId = individualSurveyStatisticsOfferedSubjectsRepository.findBySubjectIdAndCourseIdAndOfficerSessionId(findSubject.getSubjectId(), courseId, officerSessionId);

                        if(findOfferedSubjectId.isPresent()){

                            List<SurveyExecution> surveyCheck = individualSurveyStatisticsSurveyExecutionRepository.findByOfferedSubjectsIdAndSessionId(findOfferedSubjectId.get().getOfferedSubjectsId(), sessionId);

                            if(!surveyCheck.isEmpty()){

                                Map<String, Object> subjectMap = new HashMap<>();
                                subjectMap.put("subjectName", findSubject.getSubjectName());
                                subjectMap.put("subjectId", findSubject.getSubjectId());
                                subjectMap.put("offeredSubjectsId", findOfferedSubjectId.get().getOfferedSubjectsId());

                                resultList.add(subjectMap);

                            }

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
            return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " + e.getMessage());
            }
        
    }

    @GetMapping("/subjectStatistics")
    @Operation(summary = "문항 조회")
    public ResponseEntity<?> subjectStatistics(
        @RequestParam String offeredSubjectsId
    ) {
        try {
            UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                    .getContext().getAuthentication();
            String sessionId = auth.getPrincipal().toString();

            List<Map<String, Object>> resultList = new ArrayList<>();
            Map<String, Map<String, Object>> uniqueSurveyQuestionsMap = new HashMap<>();

            List<SurveyExecution> findSurveyExecutions = individualSurveyStatisticsSurveyExecutionRepository.findByOfferedSubjectsIdAndSessionId(offeredSubjectsId, sessionId);
            
            for (SurveyExecution findSurveyExecution : findSurveyExecutions) {
                List<IndividualSurveyStatisticsSurveyOwnResult> findResults = individualSurveyStatisticsSurveyOwnResultRepository.findBySurveyExecutionId(findSurveyExecution.getSurveyExecutionId());

                Set<String> uniqueSurveyQuestionIds = new HashSet<>();

                for (IndividualSurveyStatisticsSurveyOwnResult findResult : findResults) {
                    uniqueSurveyQuestionIds.add(findResult.getSurveyQuestionId());
                }

                List<String> surveyQuestionIds = new ArrayList<>(uniqueSurveyQuestionIds);

                for (String surveyQuestionId : surveyQuestionIds) {
                    Optional<IndividualSurveyStatisticsSurveyQuestion> findQuestionData = individualSurveyStatisticsSurveyQuestionRepository.findBySurveyQuestionId(surveyQuestionId);

                    if (findQuestionData.isPresent()) {
                        Map<String, Object> questionMap = new HashMap<>();
                        questionMap.put("surveyQuestionData", findQuestionData.get().getQuestionData());
                        questionMap.put("surveyQuestionId", surveyQuestionId);
                        questionMap.put("answerCategory", findQuestionData.get().getAnswerCategory());
                        questionMap.put("offeredSubjectsId", offeredSubjectsId);

                        // surveyData를 키로 사용하여 중복을 방지
                        String surveyDataKey = findQuestionData.get().getQuestionData();
                        uniqueSurveyQuestionsMap.putIfAbsent(surveyDataKey, questionMap);
                    }
                }
            }

            // 중복을 제거한 결과를 resultList에 추가
            resultList.addAll(uniqueSurveyQuestionsMap.values());

            // answerCategory를 기준으로 정렬
            resultList.sort((a, b) -> a.get("answerCategory").toString().compareTo(b.get("answerCategory").toString()));

            return ResponseEntity.ok().body(resultList);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " + e.getMessage());
        }
    }


    @GetMapping("/answerStatistics")
    public ResponseEntity<?> answerStatistics(
        @RequestParam String offeredSubjectsId,
        @RequestParam String surveyQuestionId,
        @RequestParam String answerCategory,
        @RequestParam (required = false, defaultValue = "0") int page,
        @RequestParam (required = false, defaultValue = "5") int size
        ) {

            try {
                
                UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                    .getContext().getAuthentication();
                String sessionId = auth.getPrincipal().toString();

                List<Map<String, Object>> resultList = new ArrayList<>();

                List<SurveyExecution> findSurveyExecutions = individualSurveyStatisticsSurveyExecutionRepository.findByOfferedSubjectsIdAndSessionId(offeredSubjectsId, sessionId);

                for(SurveyExecution findSurveyExecution : findSurveyExecutions){



                }

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " + e.getMessage());
            }

        return null;
    }
    
    
    
}
