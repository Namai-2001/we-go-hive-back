package com.dev.restLms.announcementPost;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.User;
import java.util.Optional;



public interface AnnouncementPostUserRepository extends JpaRepository<User, String> {
    Optional<announcementPostUser> findBySessionId(String sessionId);
}
