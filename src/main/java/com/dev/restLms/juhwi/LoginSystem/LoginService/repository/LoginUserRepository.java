package com.dev.restLms.juhwi.LoginSystem.LoginService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.User;
import java.util.Optional;



public interface LoginUserRepository extends JpaRepository<User, String>{
    Optional<User> findByUserId(String userId);
    Optional<User> findByUserIdAndUserPw(String userId, String userPw);
}