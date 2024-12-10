package com.dev.restLms.persistent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.model.VideoPlayerVideo;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoPlayerVideoRepository extends JpaRepository<VideoPlayerVideo, String> {
  Optional<VideoPlayerVideo> findByVideoId(String videoId);
  List<VideoPlayerVideo> findAllByVideoId(String videoId);
}