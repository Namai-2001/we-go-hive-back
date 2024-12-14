package com.dev.restLms.QuestionBoard.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.QuestionBoard.model.QuestionBoardPermissionGroup;
import java.util.Optional;


@Repository
public interface QuestionBoardPermissionGroupRepository extends JpaRepository<QuestionBoardPermissionGroup, String> {

    Optional<QuestionBoardPermissionGroup> findByPermissionGroupUuid(String permissionGroupUuid);
    
}
