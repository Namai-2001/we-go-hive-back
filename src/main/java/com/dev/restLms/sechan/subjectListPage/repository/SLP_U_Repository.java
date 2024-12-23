package com.dev.restLms.sechan.subjectListPage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.User;
import com.dev.restLms.sechan.subjectListPage.projection.SLP_U_Projection;

public interface SLP_U_Repository extends JpaRepository<User, String> {
    List<SLP_U_Projection> findBySessionIdIn(List<String> sessionIds);
    List<User> findByUserNameContaining(String userName);
} 
    

