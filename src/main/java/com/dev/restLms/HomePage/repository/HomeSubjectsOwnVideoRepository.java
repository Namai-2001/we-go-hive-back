package com.dev.restLms.HomePage.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.SubjectOwnVideo;

public interface HomeSubjectsOwnVideoRepository extends JpaRepository<SubjectOwnVideo, String> {
  
}
