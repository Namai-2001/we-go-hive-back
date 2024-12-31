package com.dev.restLms.ModifyCourse.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.ModifyCourse.projection.ModifyCourseUser;
import com.dev.restLms.entity.User;
import java.util.Optional;


public interface ModifyCourseUserRepository extends JpaRepository<User, String> {
    Optional<ModifyCourseUser> findBySessionId(String sessionId);
}
