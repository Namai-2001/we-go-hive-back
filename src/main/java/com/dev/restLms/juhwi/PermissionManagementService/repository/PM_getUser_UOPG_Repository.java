package com.dev.restLms.juhwi.PermissionManagementService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.UserOwnPermissionGroup;
import java.util.List;


public interface PM_getUser_UOPG_Repository extends JpaRepository<UserOwnPermissionGroup, String> {
    List<UserOwnPermissionGroup> findBySessionId(String sessionId);
}