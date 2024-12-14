package com.dev.restLms.pairToJuSe.SubjectVideoService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.User;
import com.dev.restLms.pairToJuSe.SubjectVideoService.projection.Ch_U_Projection;


public interface Ch_U_Repository extends JpaRepository<User, String>{
    Ch_U_Projection findBySessionId(String sessionId);
}
