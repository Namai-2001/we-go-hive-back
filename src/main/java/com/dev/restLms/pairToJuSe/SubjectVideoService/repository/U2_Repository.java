package com.dev.restLms.pairToJuSe.SubjectVideoService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.User;

public interface U2_Repository extends JpaRepository<User, String> {
    Optional<User> findBySessionId(String sessionId);
}
