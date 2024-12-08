package com.dev.restLms.persistent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.model.VideoPlayerUserOwnSubjectVideo;
import java.util.List;


@Repository
public interface VideoPlayerUserOwnSubjectVideoRepository extends JpaRepository<VideoPlayerUserOwnSubjectVideo, String> {
  List<VideoPlayerUserOwnSubjectVideo> findBySessionIdAndEpisodeIdAndOfferedSubjectsid(String sessionId, int episodeId, String offeredSubjectsid);
}
