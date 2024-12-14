package com.dev.restLms.hyeon.course.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.entity.Subject;
import com.dev.restLms.hyeon.course.repository.SubjectRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/subjects")
@Tag(name = "Subject API", description = "과목 API")
public class SubjectController {

    @Autowired
    private SubjectRepository subjectRepository;


    // 특정 과목 조회 (ID로)
    @GetMapping("/{subjectId}")
    @Operation(summary = "특정 과목 조회", description = "주어진 SUBJECT_ID로 과목을 조회합니다.")
    public ResponseEntity<Subject> getSubjectById(@PathVariable("subjectId") String subjectId) {
        Optional<Subject> subject = subjectRepository.findById(subjectId);
        return subject.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

}
