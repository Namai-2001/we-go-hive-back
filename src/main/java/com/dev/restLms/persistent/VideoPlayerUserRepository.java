package com.dev.restLms.persistent;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.model.VideoPlayerUser;
import java.util.Optional;

// 강사의 세션 아이디를 이름으로 변환
public interface VideoPlayerUserRepository extends JpaRepository<VideoPlayerUser, String>{
  Optional<VideoPlayerUser> findBySessionId(String sessionId);
}
