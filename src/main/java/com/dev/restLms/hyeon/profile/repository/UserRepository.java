package com.dev.restLms.hyeon.profile.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findBySessionId(String sessionId);
}