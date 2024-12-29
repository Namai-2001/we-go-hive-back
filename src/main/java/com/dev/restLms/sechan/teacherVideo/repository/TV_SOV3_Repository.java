package com.dev.restLms.sechan.teacherVideo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.SubjectOwnVideo;

public interface TV_SOV3_Repository extends JpaRepository<SubjectOwnVideo, String> {
    List<SubjectOwnVideo> findBySovVideoId(String videoId);
}
