package com.dev.restLms.juhwi.MessageService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.User;
import com.dev.restLms.juhwi.MessageService.projection.MSG_UserNickname_Projection;

public interface MSG_UserNickname_Repository extends JpaRepository<User, String>{
    Optional<MSG_UserNickname_Projection> findBySessionId(String sessionId);
}