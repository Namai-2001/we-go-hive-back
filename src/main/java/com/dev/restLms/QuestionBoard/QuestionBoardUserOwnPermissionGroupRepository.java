package com.dev.restLms.QuestionBoard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.dev.restLms.entity.UserOwnPermissionGroup;

import java.util.Optional;


@Repository
public interface QuestionBoardUserOwnPermissionGroupRepository extends JpaRepository<UserOwnPermissionGroup, String> {

    Optional<QuestionBoardUserOwnPermissionGroup> findBySessionId(String sessionId);
    
}
