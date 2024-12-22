package com.dev.restLms.QuestionBoardPost;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.User;
import java.util.Optional;


public interface QuestionBoardPostUserRepository extends JpaRepository<User, String> {
    Optional<QuestionBoardPostUser> findBySessionId(String sessionId);
}
