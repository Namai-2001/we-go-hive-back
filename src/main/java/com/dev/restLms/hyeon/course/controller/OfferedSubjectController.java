package com.dev.restLms.hyeon.course.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.entity.OfferedSubjects;
import com.dev.restLms.hyeon.course.repository.OfferedSubjectRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/offered-subjects")
@Tag(name = "OfferedSubject API", description = "개설 과목 API")
public class OfferedSubjectController {

    @Autowired
    private OfferedSubjectRepository offeredSubjectRepository;

    // 특정 개설 과목 조회 (ID로)
    @GetMapping("/{offeredSubjectsId}")
    @Operation(summary = "특정 개설 과목 조회", description = "주어진 OFFERED_SUBJECTS_ID로 개설 과목을 조회합니다.")
    public ResponseEntity<OfferedSubjects> getOfferedSubjectById(@PathVariable("offeredSubjectsId") String offeredSubjectsId) {
        Optional<OfferedSubjects> offeredSubject = offeredSubjectRepository.findById(offeredSubjectsId);
        return offeredSubject.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

}
