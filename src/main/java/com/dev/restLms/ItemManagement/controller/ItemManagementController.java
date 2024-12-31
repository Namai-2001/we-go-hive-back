package com.dev.restLms.ItemManagement.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.ItemManagement.persistence.ItemManagementSurveyOwnResultRepository;
import com.dev.restLms.ItemManagement.persistence.ItemManagementSurveyQuestionRepository;
import com.dev.restLms.ItemManagement.projection.ItemManagementSurveyOwnResult;
import com.dev.restLms.ItemManagement.projection.ItemManagementSurveyQuestion;
import com.dev.restLms.entity.SurveyQuestion;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/itemManagement")
@Tag(name = "ItemManagementController", description = "책임자 만족도 문항 관리")
public class ItemManagementController {

    @Autowired
    private ItemManagementSurveyQuestionRepository itemManagementSurveyQuestionRepository;

    @Autowired
    private ItemManagementSurveyOwnResultRepository itemManagementSurveyOwnReusltRepository;

    @GetMapping("/Courses")
    @Operation(summary = "과정에 대한 만족도 문항 조회", description = "과정에 대한 만족도 문항을 조회합니다.")
    public ResponseEntity<?> getCourseQuestion(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "8") int size
    ) {

        try {
            
            Sort sort = Sort.by(Sort.Direction.DESC, "answerCategory").and(Sort.by(Sort.Direction.ASC, "questionData"));
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<ItemManagementSurveyQuestion> findCourseQuestions = itemManagementSurveyQuestionRepository.findBySurveyCategoryAndQuestionInactive("course", "F", pageable);
    
            List<Map<String, Object>> resultList = new ArrayList<>();
    
            for(ItemManagementSurveyQuestion findCourseQuestion : findCourseQuestions){

                HashMap<String, Object> courseQuestionMap = new HashMap<>();
                courseQuestionMap.put("questionData", findCourseQuestion.getQuestionData());
                courseQuestionMap.put("surveyQuestionId", findCourseQuestion.getSurveyQuestionId());
                courseQuestionMap.put("answerCategory", findCourseQuestion.getAnswerCategory());
                // if(findCourseQuestion.getAnswerCategory().equals("T")){
                //     courseQuestionMap.put("answerCategory", "5지 선다");
                // }else if(findCourseQuestion.getAnswerCategory().equals("F")){
                //     courseQuestionMap.put("answerCategory", "서술형");
                // }

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

    @GetMapping("/Subjects")
    @Operation(summary = "과목에 대한 만족도 문항 조회", description = "과목에 대한 만족도 문항을 조회합니다.")
    public ResponseEntity<?> getSubejctQuestion(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "8") int size
    ) {

        try {
            
            Sort sort = Sort.by(Sort.Direction.DESC, "answerCategory").and(Sort.by(Sort.Direction.ASC, "questionData"));
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<ItemManagementSurveyQuestion> findCourseQuestions = itemManagementSurveyQuestionRepository.findBySurveyCategoryAndQuestionInactive("subject", "F", pageable);
    
            List<Map<String, Object>> resultList = new ArrayList<>();
    
            for(ItemManagementSurveyQuestion findCourseQuestion : findCourseQuestions){

                HashMap<String, Object> courseQuestionMap = new HashMap<>();
                courseQuestionMap.put("questionData", findCourseQuestion.getQuestionData());
                courseQuestionMap.put("surveyQuestionId", findCourseQuestion.getSurveyQuestionId());
                courseQuestionMap.put("answerCategory", findCourseQuestion.getAnswerCategory());
                // if(findCourseQuestion.getAnswerCategory().equals("T")){
                //     courseQuestionMap.put("answerCategory", "5지 선다");
                // }else if(findCourseQuestion.getAnswerCategory().equals("F")){
                //     courseQuestionMap.put("answerCategory", "서술형");
                // }

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

    @PostMapping("/searchCourseQuestion")
    @Operation(summary = "과정에 대한 만족도 문항 검색", description = "과정에 대한 만족도 문항을 검색합니다.")
    public ResponseEntity<?> searchCourseQuestion(
        @RequestParam String questionData,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "8") int size
    ) {

        try {
            
            Sort sort = Sort.by(Sort.Direction.DESC, "answerCategory").and(Sort.by(Sort.Direction.ASC, "questionData"));
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<ItemManagementSurveyQuestion> findCourseQuestions = itemManagementSurveyQuestionRepository.findByQuestionDataContainingAndSurveyCategoryAndQuestionInactive(questionData, "course", "F", pageable);
    
            List<Map<String, Object>> resultList = new ArrayList<>();
    
            for(ItemManagementSurveyQuestion findCourseQuestion : findCourseQuestions){

                HashMap<String, Object> courseQuestionMap = new HashMap<>();
                courseQuestionMap.put("questionData", findCourseQuestion.getQuestionData());
                courseQuestionMap.put("surveyQuestionId", findCourseQuestion.getSurveyQuestionId());
                courseQuestionMap.put("answerCategory", findCourseQuestion.getAnswerCategory());
                // if(findCourseQuestion.getAnswerCategory().equals("T")){
                //     courseQuestionMap.put("answerCategory", "5지 선다");
                // }else if(findCourseQuestion.getAnswerCategory().equals("F")){
                //     courseQuestionMap.put("answerCategory", "서술형");
                // }

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
            Page<ItemManagementSurveyQuestion> findCourseQuestions = itemManagementSurveyQuestionRepository.findByQuestionDataContainingAndSurveyCategoryAndQuestionInactive(questionData, "subject", "F", pageable);
    
            List<Map<String, Object>> resultList = new ArrayList<>();
    
            for(ItemManagementSurveyQuestion findCourseQuestion : findCourseQuestions){

                HashMap<String, Object> courseQuestionMap = new HashMap<>();
                courseQuestionMap.put("questionData", findCourseQuestion.getQuestionData());
                courseQuestionMap.put("surveyQuestionId", findCourseQuestion.getSurveyQuestionId());
                courseQuestionMap.put("answerCategory", findCourseQuestion.getAnswerCategory());
                // if(findCourseQuestion.getAnswerCategory().equals("T")){
                //     courseQuestionMap.put("answerCategory", "5지 선다");
                // }else if(findCourseQuestion.getAnswerCategory().equals("F")){
                //     courseQuestionMap.put("answerCategory", "서술형");
                // }

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

    @PostMapping("/createCourseQuestion")
    @Operation(summary = "과정에 대한 만족도 문항 생성", description = "과정에 대한 만족도 문항을 생성합니다.")
    public ResponseEntity<?> createCourseQuestion(
        @RequestBody SurveyQuestion surveyQuestion
        ) {

            try {

                surveyQuestion.setSurveyQuestionId(null);
                surveyQuestion.setQuestionData(surveyQuestion.getQuestionData());
                surveyQuestion.setAnswerCategory(surveyQuestion.getAnswerCategory());
                // if(surveyQuestion.getAnswerCategory().equals("5지 선다")){
                //     surveyQuestion.setAnswerCategory("T");
                // }else if(surveyQuestion.getAnswerCategory().equals("서술형")){
                //     surveyQuestion.setAnswerCategory("F");
                // }
                surveyQuestion.setSurveyCategory("course");
                surveyQuestion.setQuestionInactive("F");

                // SurveyQuestion saveCourseQuestion = itemManagementSurveyQuestionRepository.save(surveyQuestion);
                itemManagementSurveyQuestionRepository.save(surveyQuestion);

                // return ResponseEntity.ok().body(saveCourseQuestion);
                return ResponseEntity.ok().body("생성 완료");
                
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
                // if(surveyQuestion.getAnswerCategory().equals("5지 선다")){
                //     surveyQuestion.setAnswerCategory("T");
                // }else if(surveyQuestion.getAnswerCategory().equals("서술형")){
                //     surveyQuestion.setAnswerCategory("F");
                // }
                surveyQuestion.setSurveyCategory("subject");
                surveyQuestion.setQuestionInactive("F");

                // SurveyQuestion saveCourseQuestion = itemManagementSurveyQuestionRepository.save(surveyQuestion);
                itemManagementSurveyQuestionRepository.save(surveyQuestion);

                // return ResponseEntity.ok().body(saveCourseQuestion);
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

                List<ItemManagementSurveyOwnResult> findQuestion = itemManagementSurveyOwnReusltRepository.findBySurveyQuestionId(surveyQuestionId);

                if(findQuestion.isEmpty()){

                    itemManagementSurveyQuestionRepository.deleteById(surveyQuestionId);

                    if(!itemManagementSurveyQuestionRepository.existsById(surveyQuestionId)) {

                        return ResponseEntity.ok().body("삭제 완료");

                    }else{
                        return ResponseEntity.status(HttpStatus.CONFLICT).body("삭제 실패");
                    }
                    

                }else{
                    SurveyQuestion surveyQuestion = itemManagementSurveyQuestionRepository.findById(surveyQuestionId).orElseThrow(() -> new RuntimeException("문항을 찾을 수 없습니다."));

                    surveyQuestion.setQuestionInactive("T");
                    itemManagementSurveyQuestionRepository.save(surveyQuestion);
                    return ResponseEntity.ok().body("비활성화 완료");
                }
                
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("오류 발생 : " +e.getMessage());
            }
    }
    
    
}
