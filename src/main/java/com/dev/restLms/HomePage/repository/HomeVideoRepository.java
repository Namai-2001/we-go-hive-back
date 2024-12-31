package com.dev.restLms.HomePage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Video;

public interface HomeVideoRepository extends JpaRepository<Video, String>{
  
}
