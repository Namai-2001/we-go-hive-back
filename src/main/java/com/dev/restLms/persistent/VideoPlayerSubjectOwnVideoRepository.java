package com.dev.restLms.persistent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.model.VideoPlayerSubjectOwnVideo;
import java.util.List;


@Repository
public interface VideoPlayerSubjectOwnVideoRepository extends JpaRepository<VideoPlayerSubjectOwnVideo, Integer> {
  List<VideoPlayerSubjectOwnVideo> findByEpisodeIdAndOfferedSubjectsid(int episodeId, String offeredSubjectsid);
}
