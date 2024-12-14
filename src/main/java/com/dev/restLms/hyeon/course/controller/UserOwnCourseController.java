package com.dev.restLms.hyeon.course.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.entity.UserOwnCourse;
import com.dev.restLms.hyeon.course.repository.UserOwnCourseRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/user-own-courses")
@Tag(name = "UserOwnCourse API", description = "사용자별 과정 목록 API")
public class UserOwnCourseController {

    @Autowired
    private UserOwnCourseRepository userOwnCourseRepository;

    @GetMapping("/{sessionId}")
    @Operation(summary = "특정 사용자 과정 조회", description = "주어진 SESSION_ID로 사용자의 과정을 조회합니다.")
    public ResponseEntity<UserOwnCourse> getUserOwnCourseById(
            @Parameter(description = "조회할 사용자의 SESSION_ID", required = true) 
            @PathVariable("sessionId") String sessionId) {
    	UserOwnCourse userOwnCourse = userOwnCourseRepository.findBysessionId(sessionId);
    	if(userOwnCourse == null) {
        	return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userOwnCourse);
    }

}
