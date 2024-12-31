package com.dev.restLms.FreeBulletinBoardPost;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.User;
import java.util.Optional;


public interface FreeBulletinBoardPostUserRepository extends JpaRepository<User, String> {
    Optional<FreeBulletinBoardPostUser> findBySessionId(String sessionId);
}
