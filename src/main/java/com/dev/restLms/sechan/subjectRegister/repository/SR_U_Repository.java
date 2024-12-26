package com.dev.restLms.sechan.subjectRegister.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.User;
import com.dev.restLms.sechan.subjectRegister.projection.SR_U_Projection;

public interface SR_U_Repository extends JpaRepository<User, String> {
    List<SR_U_Projection> findBySessionIdIn(List<String> sessionIds);
}
