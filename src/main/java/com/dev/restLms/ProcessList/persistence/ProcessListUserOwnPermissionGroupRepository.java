package com.dev.restLms.ProcessList.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.ProcessList.model.ProcessListUserOwnPermissionGroup;
import java.util.Optional;



@Repository
public interface ProcessListUserOwnPermissionGroupRepository extends JpaRepository<ProcessListUserOwnPermissionGroup, String> {
    Optional<ProcessListUserOwnPermissionGroup> findByPermissionGroupUuid2(String permissionGroupUuid2);

    Optional<ProcessListUserOwnPermissionGroup> findBySessionId(String sessionId);

}
