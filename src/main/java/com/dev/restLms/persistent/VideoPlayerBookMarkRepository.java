package com.dev.restLms.persistent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.model.VideoPlayerBookMark;
import java.util.List;



@Repository
public interface VideoPlayerBookMarkRepository extends JpaRepository<VideoPlayerBookMark, String> {
  VideoPlayerBookMark findByBookmarkTimeAndSessionIdAndEpisodeIdAndOfferedSubjectsId(String bookmarkTime, String sessionId, int episodeId, String offeredSubjectsId);
  List<VideoPlayerBookMark> findBySessionIdAndEpisodeIdAndOfferedSubjectsId(String sessionId, int episodeId, String offeredSubjectsId);
}
