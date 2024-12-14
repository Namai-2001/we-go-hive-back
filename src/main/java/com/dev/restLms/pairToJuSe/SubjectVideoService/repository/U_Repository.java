package com.dev.restLms.pairToJuSe.SubjectVideoService.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.User;
import com.dev.restLms.pairToJuSe.SubjectVideoService.projection.U_Projection;

public interface U_Repository extends JpaRepository<User, String> {
    List<U_Projection> findBySessionIdIn(List<String> sessionIds);
}
