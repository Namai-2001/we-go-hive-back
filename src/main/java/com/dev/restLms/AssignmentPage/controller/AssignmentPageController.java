package com.dev.restLms.AssignmentPage.controller;

import java.io.ObjectInputFilter.Status;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.AssignmentPage.projection.AssignmentPageUserOwnAssignmentEvaluationProjection;
import com.dev.restLms.AssignmentPage.projection.AssignmentPagesubjectNameProjection;
import com.dev.restLms.AssignmentPage.projection.AssignmentPageAssignmentProjection;
import com.dev.restLms.AssignmentPage.projection.AssignmentPageOfferedSubjectsProjection;
import com.dev.restLms.AssignmentPage.repository.AssignmentPageAssignmentRepository;
import com.dev.restLms.AssignmentPage.repository.AssignmentPageOfferedSubjectsRepository;
import com.dev.restLms.AssignmentPage.repository.AssignmentPageSubjectRepository;
import com.dev.restLms.AssignmentPage.repository.AssignmentPageUserOwnAssignmentEvaluationRepository;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@Tag(name = "Assignmnet API", description = "과제에 대한 API 목록")
@RequestMapping("/Assignment")
public class AssignmentPageController {
  @Autowired
  private AssignmentPageAssignmentRepository assignmentRepo;

  @Autowired
  private AssignmentPageUserOwnAssignmentEvaluationRepository userOwnAssignmentEvaluationRepo;

  @Autowired
  private AssignmentPageOfferedSubjectsRepository offeredSubjectsRepository;

  @Autowired
  private AssignmentPageSubjectRepository subjectRepo;
    
    @GetMapping("/getSpecificUserAssignmentTotal")
    public ResponseEntity<?> getSpecificUserAssignmentTotal(
        @RequestParam(value = "sessionId", required = true) String sessionId,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);
        List<Map<String, Object>> resultList = new ArrayList<>();

        Page<AssignmentPageUserOwnAssignmentEvaluationProjection> pagedUserAssignments =
            userOwnAssignmentEvaluationRepo.findByUoaeSessionId(sessionId, pageable);

        if (pagedUserAssignments.hasContent()) {
            for (AssignmentPageUserOwnAssignmentEvaluationProjection userOwnAssignmentEvaluation : pagedUserAssignments.getContent()) {
                Page<AssignmentPageAssignmentProjection> assignments =
                    assignmentRepo.findByAssignmentIdAndTeacherSessionId(
                        userOwnAssignmentEvaluation.getAssignmentId(),
                        userOwnAssignmentEvaluation.getTeacherSessionId(),
                        pageable
                    );

                if (assignments.hasContent()) {
                    for (AssignmentPageAssignmentProjection assignment : assignments.getContent()) {
                        Map<String, Object> resultMap = new HashMap<>();
                        resultMap.put("fileNo", userOwnAssignmentEvaluation.getFileNo());
                        resultMap.put("isSubmit", userOwnAssignmentEvaluation.getIsSubmit());
                        resultMap.put("score", userOwnAssignmentEvaluation.getScore());
                        resultMap.put("assignmentTitle", assignment.getAssignmentTitle());
                        resultMap.put("assignmentContent", assignment.getAssignmentContent());
                        resultMap.put("cutLine", assignment.getCutline());
                        resultMap.put("deadLine", assignment.getDeadline());

                        Optional<AssignmentPageOfferedSubjectsProjection> offeredSubjectData =
                            offeredSubjectsRepository.findByOfferedSubjectsId(assignment.getOfferedSubjectsId());

                        if (offeredSubjectData.isPresent()) {
                            AssignmentPageOfferedSubjectsProjection subjectId = offeredSubjectData.get();
                            Optional<AssignmentPagesubjectNameProjection> subjectData =
                                subjectRepo.findBySubjectId(subjectId.getSubjectId());

                            if (subjectData.isPresent()) {
                                AssignmentPagesubjectNameProjection subjectName = subjectData.get();
                                resultMap.put("subjectName", subjectName.getSubjectName());
                            }
                        }
                        resultList.add(resultMap);
                    }
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("data", resultList);
            response.put("currentPage", pagedUserAssignments.getNumber());
            response.put("totalPages", pagedUserAssignments.getTotalPages());
            response.put("totalItems", pagedUserAssignments.getTotalElements());

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
    @GetMapping("/getSpecificUserAssignmentComplete")
    public ResponseEntity<?> getSpecificUserAssignmentComplete(
        @RequestParam(value = "sessionId", required = true) String sessionId,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);
        List<Map<String, Object>> resultList = new ArrayList<>();

        // "isSubmit"이 "t"인 데이터만 가져오기
        Page<AssignmentPageUserOwnAssignmentEvaluationProjection> pagedUserAssignments =
            userOwnAssignmentEvaluationRepo.findByUoaeSessionIdAndIsSubmit(sessionId, "t", pageable);

        if (pagedUserAssignments.hasContent()) {
            for (AssignmentPageUserOwnAssignmentEvaluationProjection userOwnAssignmentEvaluation : pagedUserAssignments.getContent()) {
                Page<AssignmentPageAssignmentProjection> assignments =
                    assignmentRepo.findByAssignmentIdAndTeacherSessionId(
                        userOwnAssignmentEvaluation.getAssignmentId(),
                        userOwnAssignmentEvaluation.getTeacherSessionId(),
                        pageable
                    );

                if (assignments.hasContent()) {
                    for (AssignmentPageAssignmentProjection assignment : assignments.getContent()) {
                        Map<String, Object> resultMap = new HashMap<>();
                        resultMap.put("fileNo", userOwnAssignmentEvaluation.getFileNo());
                        resultMap.put("isSubmit", userOwnAssignmentEvaluation.getIsSubmit());
                        resultMap.put("score", userOwnAssignmentEvaluation.getScore());
                        resultMap.put("assignmentTitle", assignment.getAssignmentTitle());
                        resultMap.put("assignmentContent", assignment.getAssignmentContent());
                        resultMap.put("cutLine", assignment.getCutline());
                        resultMap.put("deadLine", assignment.getDeadline());

                        Optional<AssignmentPageOfferedSubjectsProjection> offeredSubjectData =
                            offeredSubjectsRepository.findByOfferedSubjectsId(assignment.getOfferedSubjectsId());

                        if (offeredSubjectData.isPresent()) {
                            AssignmentPageOfferedSubjectsProjection subjectId = offeredSubjectData.get();
                            Optional<AssignmentPagesubjectNameProjection> subjectData =
                                subjectRepo.findBySubjectId(subjectId.getSubjectId());

                            if (subjectData.isPresent()) {
                                AssignmentPagesubjectNameProjection subjectName = subjectData.get();
                                resultMap.put("subjectName", subjectName.getSubjectName());
                            }
                        }
                        resultList.add(resultMap);
                    }
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("data", resultList);
            response.put("currentPage", pagedUserAssignments.getNumber());
            response.put("totalPages", pagedUserAssignments.getTotalPages());
            response.put("totalItems", pagedUserAssignments.getTotalElements());

            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping("/getSpecificUserAssignmentIncomplete")
    public ResponseEntity<?> getSpecificUserAssignmentIncomplete(
        @RequestParam(value = "sessionId", required = true) String sessionId,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size);
        List<Map<String, Object>> resultList = new ArrayList<>();

        // "isSubmit"이 "t"인 데이터만 가져오기
        Page<AssignmentPageUserOwnAssignmentEvaluationProjection> pagedUserAssignments =
            userOwnAssignmentEvaluationRepo.findByUoaeSessionIdAndIsSubmit(sessionId, "f", pageable);

        if (pagedUserAssignments.hasContent()) {
            for (AssignmentPageUserOwnAssignmentEvaluationProjection userOwnAssignmentEvaluation : pagedUserAssignments.getContent()) {
                Page<AssignmentPageAssignmentProjection> assignments =
                    assignmentRepo.findByAssignmentIdAndTeacherSessionId(
                        userOwnAssignmentEvaluation.getAssignmentId(),
                        userOwnAssignmentEvaluation.getTeacherSessionId(),
                        pageable
                    );

                if (assignments.hasContent()) {
                    for (AssignmentPageAssignmentProjection assignment : assignments.getContent()) {
                        Map<String, Object> resultMap = new HashMap<>();
                        resultMap.put("fileNo", userOwnAssignmentEvaluation.getFileNo());
                        resultMap.put("isSubmit", userOwnAssignmentEvaluation.getIsSubmit());
                        resultMap.put("score", userOwnAssignmentEvaluation.getScore());
                        resultMap.put("assignmentTitle", assignment.getAssignmentTitle());
                        resultMap.put("assignmentContent", assignment.getAssignmentContent());
                        resultMap.put("cutLine", assignment.getCutline());
                        resultMap.put("deadLine", assignment.getDeadline());

                        Optional<AssignmentPageOfferedSubjectsProjection> offeredSubjectData =
                            offeredSubjectsRepository.findByOfferedSubjectsId(assignment.getOfferedSubjectsId());

                        if (offeredSubjectData.isPresent()) {
                            AssignmentPageOfferedSubjectsProjection subjectId = offeredSubjectData.get();
                            Optional<AssignmentPagesubjectNameProjection> subjectData =
                                subjectRepo.findBySubjectId(subjectId.getSubjectId());

                            if (subjectData.isPresent()) {
                                AssignmentPagesubjectNameProjection subjectName = subjectData.get();
                                resultMap.put("subjectName", subjectName.getSubjectName());
                            }
                        }
                        resultList.add(resultMap);
                    }
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("data", resultList);
            response.put("currentPage", pagedUserAssignments.getNumber());
            response.put("totalPages", pagedUserAssignments.getTotalPages());
            response.put("totalItems", pagedUserAssignments.getTotalElements());

            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}