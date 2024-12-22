package com.dev.restLms.juhwi.LoginSystem.LoginService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.UserOwnPermissionGroup;
import java.util.List;
import java.util.Optional;

public interface LoginUserOwnPermissionGroupRepository extends JpaRepository<UserOwnPermissionGroup, String>{
    // 유저의 모든 권한을 가져오기 때문에 페이지 네이션 x
    Optional<List<UserOwnPermissionGroup>> findBySessionId(String sessionId);
}