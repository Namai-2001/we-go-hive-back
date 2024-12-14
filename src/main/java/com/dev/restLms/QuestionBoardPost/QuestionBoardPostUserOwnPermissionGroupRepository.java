package com.dev.restLms.QuestionBoardPost;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.entity.UserOwnPermissionGroup;

import java.util.Optional;


@Repository
public interface QuestionBoardPostUserOwnPermissionGroupRepository extends JpaRepository<UserOwnPermissionGroup, String> {
    Optional<QuestionBoardPostUserOwnPermissionGroup> findBySessionId(String sessionId);
}
