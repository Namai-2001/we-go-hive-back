package com.dev.restLms.IndividualSurveyStatistics.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.IndividualSurveyStatistics.persistence.IndividualSurveyStatisticsCourseOwnSubjectRepository;
import com.dev.restLms.IndividualSurveyStatistics.persistence.IndividualSurveyStatisticsOfferedSubjectsRepository;
import com.dev.restLms.IndividualSurveyStatistics.persistence.IndividualSurveyStatisticsSubjectRepository;
import com.dev.restLms.IndividualSurveyStatistics.persistence.IndividualSurveyStatisticsSurveyExecutionRepository;
import com.dev.restLms.IndividualSurveyStatistics.persistence.IndividualSurveyStatisticsSurveyOwnAnswerRepository;
import com.dev.restLms.IndividualSurveyStatistics.persistence.IndividualSurveyStatisticsSurveyOwnResultRepository;
import com.dev.restLms.IndividualSurveyStatistics.persistence.IndividualSurveyStatisticsSurveyQuestionRepository;
import com.dev.restLms.IndividualSurveyStatistics.projection.IndividualSurveyStatisticsCourseOwnSubject;
import com.dev.restLms.IndividualSurveyStatistics.projection.IndividualSurveyStatisticsOfferedSubjects;
import com.dev.restLms.IndividualSurveyStatistics.projection.IndividualSurveyStatisticsSubject;
import com.dev.restLms.IndividualSurveyStatistics.projection.IndividualSurveyStatisticsSurveyOwnAnswer;
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

    @Autowired
    private IndividualSurveyStatisticsSurveyOwnAnswerRepository individualSurveyStatisticsSurveyOwnAnswerRepository;

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

                int num1 = 0;
                int num2 = 0;
                int num3 = 0;
                int num4 = 0;
                int num5 = 0;
                int answerSize = 0;

                List<SurveyExecution> findSurveyExecutions = individualSurveyStatisticsSurveyExecutionRepository.findByOfferedSubjectsIdAndSessionId(offeredSubjectsId, sessionId);

                if(answerCategory.equals("T")){

                    for(SurveyExecution findSurveyExecution : findSurveyExecutions){

                        List<IndividualSurveyStatisticsSurveyOwnResult> findAnswerIds = individualSurveyStatisticsSurveyOwnResultRepository.findBySurveyExecutionIdAndSurveyQuestionId(findSurveyExecution.getSurveyExecutionId(), surveyQuestionId);
                        
                        for(IndividualSurveyStatisticsSurveyOwnResult findAnswerId : findAnswerIds){

                            answerSize = findAnswerIds.size();

                            Optional<IndividualSurveyStatisticsSurveyOwnAnswer> findAnswer = individualSurveyStatisticsSurveyOwnAnswerRepository.findBySurveyAnswerIdAndSurveyQuestionId(findAnswerId.getSurveyAnswerId(), surveyQuestionId);

                            if(findAnswer.isPresent() && findAnswer.get().getScore() != null){
                                String answerNumber = findAnswer.get().getScore();
        
                                if(answerNumber.equals("1")){
                                    num1 += 1;
                                }else if(answerNumber.equals("2")){
                                    num2 += 1;
                                }else if(answerNumber.equals("3")){
                                    num3 += 1;
                                }else if(answerNumber.equals("4")){
                                    num4 += 1;
                                }else if(answerNumber.equals("5")){
                                    num5 += 1;
                                }

                            }

                        }

                    }

                    HashMap<String, Object> answerMap = new HashMap<>();

                    if(answerSize > 0){
                        answerMap.put("VeryNo", (int)Math.round((double)num1*100/answerSize));
                        answerMap.put("No",   (int)Math.round((double)num2*100/answerSize));
                        answerMap.put("Normal",  (int)Math.round((double)num3*100/answerSize));
                        answerMap.put("Yes",  (int)Math.round((double)num4*100/answerSize));
                        answerMap.put("VeryYes",  (int)Math.round((double)num5*100/answerSize));
                    }else {
                        // 응답이 없을 경우 0%로 설정
                        answerMap.put("VeryNo", 0);
                        answerMap.put("No", 0);
                        answerMap.put("Normal", 0);
                        answerMap.put("Yes", 0);
                        answerMap.put("VeryYes", 0);
                    }
                    return ResponseEntity.ok().body(answerMap);

                }else if(answerCategory.equals("F")){

                    for(SurveyExecution findSurveyExecution : findSurveyExecutions){

                        List<IndividualSurveyStatisticsSurveyOwnResult> findFormAnswerIds = individualSurveyStatisticsSurveyOwnResultRepository.findBySurveyExecutionIdAndSurveyQuestionId(findSurveyExecution.getSurveyExecutionId(), surveyQuestionId);

                        for(IndividualSurveyStatisticsSurveyOwnResult findFormAnswerId : findFormAnswerIds){

                            Optional<IndividualSurveyStatisticsSurveyOwnAnswer> findFormAnswer = individualSurveyStatisticsSurveyOwnAnswerRepository.findBySurveyAnswerIdAndSurveyQuestionId(findFormAnswerId.getSurveyAnswerId(), surveyQuestionId);

                            if(findFormAnswer.isPresent() && findFormAnswer.get().getAnswerData() != null){

                                HashMap<String, Object> formAnswerMap = new HashMap<>();
                                formAnswerMap.put("answerData", findFormAnswer.get().getAnswerData());
                                resultList.add(formAnswerMap);

                            }
                            
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

                }

                return ResponseEntity.status(HttpStatus.CONFLICT).body("문항의 답변이 없습니다.");

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " + e.getMessage());
            }

    }
    
    
    
}
