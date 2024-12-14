package com.dev.restLms.ProcessList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.entity.UserOwnPermissionGroup;

import java.util.Optional;



@Repository
public interface ProcessListUserOwnPermissionGroupRepository extends JpaRepository<UserOwnPermissionGroup, String> {
    Optional<ProcessListUserOwnPermissionGroup> findByPermissionGroupUuid2(String permissionGroupUuid2);

    Optional<ProcessListUserOwnPermissionGroup> findBySessionId(String sessionId);

}
