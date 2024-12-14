package com.dev.restLms.hyeon.course.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.entity.Course;
import com.dev.restLms.hyeon.course.repository.CourseRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/courses")
@Tag(name = "Course API", description = "과정 관련 API")
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @GetMapping("/{courseId}")
    @Operation(summary = "특정 과정 조회", description = "주어진 COURSE_ID로 과정을 조회합니다.")
    public ResponseEntity<Course> getCourseById(
            @Parameter(description = "조회할 과정의 COURSE_ID", required = true)
            @PathVariable("courseId") String courseId) {
        return courseRepository.findById(courseId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
