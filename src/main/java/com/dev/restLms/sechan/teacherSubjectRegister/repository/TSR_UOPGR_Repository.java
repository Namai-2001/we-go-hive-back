package com.dev.restLms.sechan.teacherSubjectRegister.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.UserOwnPermissionGroup;

public interface TSR_UOPGR_Repository extends JpaRepository<UserOwnPermissionGroup, String> {
    Optional<UserOwnPermissionGroup> findByPermissionGroupUuid2(String permissionGroupUuid2);
}
