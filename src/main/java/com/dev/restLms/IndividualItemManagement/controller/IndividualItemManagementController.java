package com.dev.restLms.IndividualItemManagement.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.IndividualItemManagement.persistence.IndividualItemManagementSurveyOwnResultRepository;
import com.dev.restLms.IndividualItemManagement.persistence.IndividualItemManagementSurveyQuestionRepository;
import com.dev.restLms.IndividualItemManagement.projection.IndividualItemManagementSurveyOwnResult;
import com.dev.restLms.IndividualItemManagement.projection.IndividualItemManagementSurveyQuestion;
import com.dev.restLms.entity.SurveyQuestion;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/individualItemManagement")
@Tag(name = "IndividualItemManagementController", description = "개별 과목 책임자 만족도 문항 관리")
public class IndividualItemManagementController {

    @Autowired
    private IndividualItemManagementSurveyOwnResultRepository individualItemManagementSurveyOwnResultRepository;

    @Autowired
    private IndividualItemManagementSurveyQuestionRepository individualItemManagementSurveyQuestionRepository;

    // @GetMapping("/Subjects")
    // @Operation(summary = "과목에 대한 만족도 문항 조회", description = "과목에 대한 만족도 문항을 조회합니다.")
    // public ResponseEntity<?> getSubejctQuestion(
    //     @RequestParam(defaultValue = "0") int page,
    //     @RequestParam(defaultValue = "8") int size
    // ) {

    //     try {
            
    //         Sort sort = Sort.by(Sort.Direction.DESC, "answerCategory").and(Sort.by(Sort.Direction.ASC, "questionData"));
    //         Pageable pageable = PageRequest.of(page, size, sort);
    //         Page<IndividualItemManagementSurveyQuestion> findCourseQuestions = individualItemManagementSurveyQuestionRepository.findBySurveyCategoryAndQuestionInactive("subject", "F", pageable);
    
    //         List<Map<String, Object>> resultList = new ArrayList<>();
    
    //         for(IndividualItemManagementSurveyQuestion findCourseQuestion : findCourseQuestions){

    //             HashMap<String, Object> courseQuestionMap = new HashMap<>();
    //             courseQuestionMap.put("questionData", findCourseQuestion.getQuestionData());
    //             courseQuestionMap.put("surveyQuestionId", findCourseQuestion.getSurveyQuestionId());
    //             courseQuestionMap.put("answerCategory", findCourseQuestion.getAnswerCategory());

    //             resultList.add(courseQuestionMap);
    
    //         }

    //         Map<String, Object> response = new HashMap<>();
    //         response.put("content", resultList);
    //         response.put("currentPage", findCourseQuestions.getNumber());
    //         response.put("totalItems", findCourseQuestions.getTotalElements());
    //         response.put("totalPages", findCourseQuestions.getTotalPages());

    //         return ResponseEntity.ok().body(response);

    //     } catch (Exception e) {
    //         return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " + e.getMessage());
    //     }
    // }

    @PostMapping("/searchSubjectQuestion")
    @Operation(summary = "과목에 대한 만족도 문항 검색", description = "과목에 대한 만족도 문항을 검색합니다.")
    public ResponseEntity<?> searchSubjectQuestion(
        @RequestParam String questionData,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "8") int size
    ) {

        try {
            
            Sort sort = Sort.by(Sort.Direction.DESC, "answerCategory").and(Sort.by(Sort.Direction.ASC, "questionData"));
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<IndividualItemManagementSurveyQuestion> findCourseQuestions = individualItemManagementSurveyQuestionRepository.findByQuestionDataContainingAndSurveyCategoryAndQuestionInactive(questionData, "subject", "F", pageable);
    
            List<Map<String, Object>> resultList = new ArrayList<>();
    
            for(IndividualItemManagementSurveyQuestion findCourseQuestion : findCourseQuestions){

                HashMap<String, Object> courseQuestionMap = new HashMap<>();
                courseQuestionMap.put("questionData", findCourseQuestion.getQuestionData());
                courseQuestionMap.put("surveyQuestionId", findCourseQuestion.getSurveyQuestionId());
                courseQuestionMap.put("answerCategory", findCourseQuestion.getAnswerCategory());

                resultList.add(courseQuestionMap);
    
            }

            Map<String, Object> response = new HashMap<>();
            response.put("content", resultList);
            response.put("currentPage", findCourseQuestions.getNumber());
            response.put("totalItems", findCourseQuestions.getTotalElements());
            response.put("totalPages", findCourseQuestions.getTotalPages());

            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " + e.getMessage());
        }
    }

    @PostMapping("/createSubjetQuestion")
    @Operation(summary = "과목에 대한 만족도 문항 생성", description = "과목에 대한 만족도 문항을 생성합니다.")
    public ResponseEntity<?> createSubjetQuestion(
        @RequestBody SurveyQuestion surveyQuestion
        ) {

            try {

                surveyQuestion.setSurveyQuestionId(null);
                surveyQuestion.setQuestionData(surveyQuestion.getQuestionData());
                surveyQuestion.setAnswerCategory(surveyQuestion.getAnswerCategory());
                surveyQuestion.setSurveyCategory("subject");
                surveyQuestion.setQuestionInactive("F");

                individualItemManagementSurveyQuestionRepository.save(surveyQuestion);

                return ResponseEntity.ok().body("생성 완료");
                
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " + e.getMessage());
            }
    }

    @PostMapping("/deleteQuestion")
    @Operation(summary = "만족도 문항 삭제 또는 비활성화", description = "선택한 만족도 문항을 삭제 또는 비활성화 합니다.")
    public ResponseEntity<?> deleteQuestion(
        @RequestParam String surveyQuestionId
        ) {

            try {

                List<IndividualItemManagementSurveyOwnResult> findQuestion = individualItemManagementSurveyOwnResultRepository.findBySurveyQuestionId(surveyQuestionId);

                if(findQuestion.isEmpty()){

                    individualItemManagementSurveyQuestionRepository.deleteById(surveyQuestionId);

                    if(!individualItemManagementSurveyQuestionRepository.existsById(surveyQuestionId)) {

                        return ResponseEntity.ok().body("삭제 완료");

                    }else{
                        return ResponseEntity.status(HttpStatus.CONFLICT).body("삭제 실패");
                    }
                    

                }else{
                    SurveyQuestion surveyQuestion = individualItemManagementSurveyQuestionRepository.findById(surveyQuestionId).orElseThrow(() -> new RuntimeException("문항을 찾을 수 없습니다."));

                    surveyQuestion.setQuestionInactive("T");
                    individualItemManagementSurveyQuestionRepository.save(surveyQuestion);
                    return ResponseEntity.ok().body("비활성화 완료");
                }
                
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " +e.getMessage());
            }
    }
    
}
