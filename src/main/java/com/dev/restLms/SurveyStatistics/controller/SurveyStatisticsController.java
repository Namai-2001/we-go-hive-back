package com.dev.restLms.SurveyStatistics.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.SurveyStatistics.persistence.SurveyStatisticsCourseRepository;
import com.dev.restLms.SurveyStatistics.persistence.SurveyStatisticsPermissionGroupRepository;
import com.dev.restLms.SurveyStatistics.persistence.SurveyStatisticsUserOwnPermssionGroupRepository;
import com.dev.restLms.SurveyStatistics.projection.SurveyStatisticsPermissionGroup;
import com.dev.restLms.entity.UserOwnPermissionGroup;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/surveyStatistics")
@Tag(name = "SurveyStatisticsController", description = "책임자 만족도 통계")
public class SurveyStatisticsController {

    @Autowired
    private SurveyStatisticsCourseRepository surveyStatisticsCourseRepository;

    @Autowired
    private SurveyStatisticsUserOwnPermssionGroupRepository surveyStatisticsUserOwnPermssionGroupRepository;

    @Autowired
    private SurveyStatisticsPermissionGroupRepository surveyStatisticsPermissionGroupRepository;

    @GetMapping("/main")
    public ResponseEntity<?> getPermssionCheck() {

        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String sessionId = auth.getPrincipal().toString();

        Optional<UserOwnPermissionGroup> permissionCheck = surveyStatisticsUserOwnPermssionGroupRepository.findBySessionId(sessionId);

        Optional<SurveyStatisticsPermissionGroup> permissionNameCheck = surveyStatisticsPermissionGroupRepository.findByPermissionGroupUuid(permissionCheck.get().getPermissionGroupUuid2());

        String permissionName = permissionNameCheck.get().getPermissionName();

        if(permissionCheck.isPresent()){
            if(permissionName.equals("OFFICER")){
                return ResponseEntity.ok().body("로그인 되었습니다.");
            }
        }else{
            return ResponseEntity.status(HttpStatus.CONFLICT).body("로그인 후 사용 가능");
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body("책임자가 아닙니다.");
    }
    

    @GetMapping
    public ResponseEntity<?> getOfficerCourse(){

        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String sessionId = auth.getPrincipal().toString();

        Optional<UserOwnPermissionGroup> permissionCheck = surveyStatisticsUserOwnPermssionGroupRepository.findBySessionId(sessionId);


        return null;
    }
    
}
