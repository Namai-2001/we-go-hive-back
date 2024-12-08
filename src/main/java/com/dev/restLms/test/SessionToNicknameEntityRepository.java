package com.dev.restLms.test;


import org.springframework.data.jpa.repository.JpaRepository;



public interface SessionToNicknameEntityRepository extends JpaRepository<SessionToNicknameEntity, String>{
    SessionToNicknameEntity findBySessionId(String sessionId);
}