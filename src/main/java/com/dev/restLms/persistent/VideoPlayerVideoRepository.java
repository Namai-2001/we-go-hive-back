package com.dev.restLms.persistent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.model.VideoPlayerVideo;
import java.util.List;

@Repository
public interface VideoPlayerVideoRepository extends JpaRepository<VideoPlayerVideo, String> {
  List<VideoPlayerVideo> findByVideoId(String videoId);
}