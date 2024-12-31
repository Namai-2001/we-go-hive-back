package com.dev.restLms.OfficerMainPage.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.OfficerMainPage.projection.OfficerMainPagePermissionGroup;
import com.dev.restLms.entity.PermissionGroup;
import java.util.Optional;


public interface OfficerMainPagePermissionGroupRepotisoty extends JpaRepository<PermissionGroup, String>{
    Optional<OfficerMainPagePermissionGroup> findByPermissionGroupUuid(String permissionGroupUuid);
}
