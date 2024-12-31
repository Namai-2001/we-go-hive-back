package com.dev.restLms.hyeon.officer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.entity.User;
import com.dev.restLms.hyeon.officer.projection.PursuitUser;

@Repository
public interface PursuitUserRepository extends JpaRepository<User, String> {
    Optional<PursuitUser> findBySessionId(String sessionId);
    List<PursuitUser> findBySessionIdIn(List<String> sessionId);
    // Boolean existsBySessionId(String sessionId);
}