package com.dev.restLms.hyeon.course.repository;

import org.springframework.data.repository.CrudRepository;

import com.dev.restLms.entity.User;

public interface UserRepository extends CrudRepository<User, String> {
    User findBysessionId(String sessionId);
}