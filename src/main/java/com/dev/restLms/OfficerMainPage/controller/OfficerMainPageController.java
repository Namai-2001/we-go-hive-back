package com.dev.restLms.OfficerMainPage.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.OfficerMainPage.persistence.OfficerMainPagePermissionGroupRepotisoty;
import com.dev.restLms.OfficerMainPage.persistence.OfficerMainPageUserOwnPermissionGroupRepository;
import com.dev.restLms.OfficerMainPage.projection.OfficerMainPagePermissionGroup;
import com.dev.restLms.entity.UserOwnPermissionGroup;

import io.swagger.v3.oas.annotations.Operation;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/officerPage")
public class OfficerMainPageController {
    @Autowired
    private OfficerMainPageUserOwnPermissionGroupRepository officerMainPageUserOwnPermissionGroupRepository;

    @Autowired
    private OfficerMainPagePermissionGroupRepotisoty officerMainPagePermissionGroupRepotisoty;

    @GetMapping()
    @Operation(summary = "책임자 식별", description = "책임자가 맞는지 식별합니다.")
    public ResponseEntity<?> getPermissionCheck() {

        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder
                                .getContext().getAuthentication();
                // 유저 세션아이디 보안 컨텍스트에서 가져오기
                String sessionId = auth.getPrincipal().toString();

        Optional<UserOwnPermissionGroup> permissionCheck = officerMainPageUserOwnPermissionGroupRepository.findBySessionId(sessionId);

        Optional<OfficerMainPagePermissionGroup> permissionNameCheck = officerMainPagePermissionGroupRepotisoty.findByPermissionGroupUuid(permissionCheck.get().getPermissionGroupUuid2());

        String permissionName = permissionNameCheck.get().getPermissionName();

        if(permissionCheck.isPresent()){
            if(permissionName.equals("OFFICER")){
                return ResponseEntity.ok().body("책임자 권한이 식별되었습니다.");
            }else if(permissionName.equals("INDIV_OFFICER")){
                return ResponseEntity.ok().body("책임자 권한이 식별되었습니다.");
            }else{
                return ResponseEntity.status(HttpStatus.CONFLICT).body("책임자가 아닙니다.");
            }
        }

        return ResponseEntity.status(HttpStatus.CONFLICT).body("책임자가 아닙니다.");
    }
    

}
