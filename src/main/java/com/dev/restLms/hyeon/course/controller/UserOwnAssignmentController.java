package com.dev.restLms.hyeon.course.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.entity.UserOwnAssignment;
import com.dev.restLms.hyeon.course.repository.UserOwnAssignmentRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/user-own-assignments")
@Tag(name = "UserOwnAssignment API", description = "사용자별 과목 목록 API")
public class UserOwnAssignmentController {

    @Autowired
    private UserOwnAssignmentRepository userOwnAssignmentRepository;


    @GetMapping("/{userSessionId}")
    @Operation(summary = "특정 사용자 과목 목록 조회", description = "주어진 USER_SESSION_ID로 사용자의 과목 목록을 조회합니다.")
    public ResponseEntity<List<UserOwnAssignment>> getUserOwnAssignmentById(
            @Parameter(description = "조회할 사용자의 USER_SESSION_ID", required = true) 
            @PathVariable("userSessionId") String userSessionId) {
        List<UserOwnAssignment> userOwnAssignment = userOwnAssignmentRepository.findAllByuserSessionId(userSessionId);
    	if(userOwnAssignment == null) {
        	return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userOwnAssignment);
    }

}
