package com.dev.restLms.juhwi.UserManagerService.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.UserOwnPermissionGroup;
import com.dev.restLms.juhwi.UserManagerService.projecttion.UM_searchUser_UOPGe_Projection;



public interface UM_searchUser_UOPGe_Repository extends JpaRepository<UserOwnPermissionGroup, String>{
    Page<UM_searchUser_UOPGe_Projection> findByPermissionGroupUuid2(String permissionGroupUuid2, Pageable pageable);
    Page<UM_searchUser_UOPGe_Projection> findBySessionId(String sessionId, Pageable pageable);
}
