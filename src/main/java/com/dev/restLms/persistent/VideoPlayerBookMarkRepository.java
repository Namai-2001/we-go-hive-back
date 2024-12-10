package com.dev.restLms.persistent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.model.VideoPlayerBookMark;
import java.util.List;



@Repository
public interface VideoPlayerBookMarkRepository extends JpaRepository<VideoPlayerBookMark, Object> {
  List<VideoPlayerBookMark> findByBmSessionIdAndBmEpisodeIdAndBmOfferedSubjectsId(String bmSessionId, Integer bmEpisodeId, String bmOfferedSubjectsId);
}
