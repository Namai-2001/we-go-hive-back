package com.dev.restLms.test;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;



public interface UserRepository extends JpaRepository<User, String>{
    List<User> findBySessionId(String sessionId);
}