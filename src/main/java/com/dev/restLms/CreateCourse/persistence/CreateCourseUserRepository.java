package com.dev.restLms.CreateCourse.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.CreateCourse.projection.CreateCourseUser;
import com.dev.restLms.entity.User;
import java.util.Optional;


public interface CreateCourseUserRepository extends JpaRepository<User, String> {
    Optional<CreateCourseUser> findBySessionId(String sessionId);
}
