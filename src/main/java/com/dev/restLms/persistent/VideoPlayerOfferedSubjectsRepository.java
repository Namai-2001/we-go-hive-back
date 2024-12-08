package com.dev.restLms.persistent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.model.VideoPlayerOfferedSubjects;

@Repository
public interface VideoPlayerOfferedSubjectsRepository extends JpaRepository<VideoPlayerOfferedSubjects, String>{
} 