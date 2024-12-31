package com.dev.restLms.juhwi.PermissionManagementService.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.User;


public interface PM_getUser_Ue_Repository extends JpaRepository<User, String>{
    Page<User> findByUserEmailContaining(String userEmail, Pageable pageable);
}