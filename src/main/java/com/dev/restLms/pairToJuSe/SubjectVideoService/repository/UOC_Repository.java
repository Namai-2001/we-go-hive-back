package com.dev.restLms.pairToJuSe.SubjectVideoService.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.UserOwnCourse;
public interface UOC_Repository extends JpaRepository<UserOwnCourse, String>{
    List<UserOwnCourse> findBySessionId(String sessionId);
}
