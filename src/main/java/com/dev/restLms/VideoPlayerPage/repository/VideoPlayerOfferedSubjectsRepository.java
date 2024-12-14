package com.dev.restLms.VideoPlayerPage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.entity.OfferedSubjects;

@Repository
public interface VideoPlayerOfferedSubjectsRepository extends JpaRepository<OfferedSubjects, String>{
} 