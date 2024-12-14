package com.dev.restLms.juhwi.messagepage.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.User;
import com.dev.restLms.juhwi.messagepage.projection.MessagepageParseNicknameProjection;

public interface MessagepageParseNicknameRepository extends JpaRepository<User, String>{
    // 1. 페이지 네이션 구현 의도 주입
    // 2. 프로젝션 구현 의도 주입
    Page<MessagepageParseNicknameProjection> findByNicknameContaining(String nickname, Pageable pageable);
}