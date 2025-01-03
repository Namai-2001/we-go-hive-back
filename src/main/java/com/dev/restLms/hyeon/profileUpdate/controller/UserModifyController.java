package com.dev.restLms.hyeon.profileUpdate.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/userProfile")
@Tag(name = "나의 정보 조회 API", description = "나의 정보 조회 API")
public class UserModifyController {
    @GetMapping("/personalCourse")
    @Operation(summary = "사용자의 과정 목록 조회", description = "주어진 SESSION_ID로 사용자의 과정 목록 조회합니다.")
    public ResponseEntity<?> getUserProfileInfo() {
        return null;
    }
    
}
