package com.dev.restLms.juhwi.UserManagerService.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.PermissionGroup;
import com.dev.restLms.juhwi.UserManagerService.projecttion.UM_searchUser_PGe_Projection;


public interface UM_searchUser_PGe_Repository extends JpaRepository<PermissionGroup, String>{
    Page<UM_searchUser_PGe_Projection> findByPermissionName (String permissionName, Pageable pageable);
    Page<UM_searchUser_PGe_Projection> findByPermissionGroupUuid(String permissionGroupUuid, Pageable pageable);
}
