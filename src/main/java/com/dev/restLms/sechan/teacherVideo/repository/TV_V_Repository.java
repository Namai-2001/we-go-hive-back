package com.dev.restLms.sechan.teacherVideo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Video;

public interface TV_V_Repository extends JpaRepository<Video, String> {
    List<Video> findByVideoIdIn(List<String> videoIds);
}