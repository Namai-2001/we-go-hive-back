package com.dev.restLms.sechan.teacherAssignment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.User;

public interface TA_U_Repository extends JpaRepository<User, String>{
    List<User> findBySessionIdIn(List<String> sessionIds);
}
