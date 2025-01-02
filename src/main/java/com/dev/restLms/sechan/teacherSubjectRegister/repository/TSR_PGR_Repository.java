package com.dev.restLms.sechan.teacherSubjectRegister.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.PermissionGroup;

public interface TSR_PGR_Repository extends JpaRepository<PermissionGroup, String> {
    Optional<PermissionGroup> findByPermissionName(String permissionName);
}
