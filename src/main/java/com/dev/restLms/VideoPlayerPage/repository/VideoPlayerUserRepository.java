package com.dev.restLms.VideoPlayerPage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.VideoPlayerPage.projection.VideoPlayerUser;
import com.dev.restLms.entity.User;

import java.util.Optional;

// 강사의 세션 아이디를 이름으로 변환
@Repository
public interface VideoPlayerUserRepository extends JpaRepository<User, String>{
  Optional<VideoPlayerUser> findBySessionId(String sessionId);
}
