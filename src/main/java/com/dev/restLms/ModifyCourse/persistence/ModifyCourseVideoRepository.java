package com.dev.restLms.ModifyCourse.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Video;

public interface ModifyCourseVideoRepository extends JpaRepository<Video, String> {
    
}
