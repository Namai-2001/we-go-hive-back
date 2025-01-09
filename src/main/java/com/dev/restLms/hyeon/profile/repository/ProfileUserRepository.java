package com.dev.restLms.hyeon.profile.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.entity.User;
import com.dev.restLms.hyeon.profile.projection.ProfileUser;

@Repository
public interface ProfileUserRepository extends JpaRepository<User, String> {
    Optional<ProfileUser> findBySessionId(String sessionId);
    List<ProfileUser> findBySessionIdIn(List<String> sessionId);
}