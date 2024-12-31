package com.dev.restLms.deleteCourse.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Video;

public interface DeleteCourseVideoRepository extends JpaRepository<Video, String> {
    
}
