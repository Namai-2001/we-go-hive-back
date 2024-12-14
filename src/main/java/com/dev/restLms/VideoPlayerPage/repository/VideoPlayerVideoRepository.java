package com.dev.restLms.VideoPlayerPage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.dev.restLms.entity.Video;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoPlayerVideoRepository extends JpaRepository<Video, String> {
  Optional<Video> findByVideoId(String videoId);
  List<Video> findAllByVideoId(String videoId);
}