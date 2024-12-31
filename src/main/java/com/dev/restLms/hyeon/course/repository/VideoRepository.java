package com.dev.restLms.hyeon.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.entity.Video;

@Repository
public interface VideoRepository extends JpaRepository<Video, String> {
    // 추가적인 쿼리 메소드가 필요한 경우 여기에 작성
   Video findByVideoId(String videoId);
}