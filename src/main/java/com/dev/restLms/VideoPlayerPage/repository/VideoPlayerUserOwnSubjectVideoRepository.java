package com.dev.restLms.VideoPlayerPage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.entity.UserOwnSubjectVideo;

import java.util.List;
import java.util.Optional;


@Repository
public interface VideoPlayerUserOwnSubjectVideoRepository extends JpaRepository<UserOwnSubjectVideo, String> {
  Optional<UserOwnSubjectVideo> findByUosvSessionIdAndUosvEpisodeIdAndUosvOfferedSubjectsId(String uosvSessionId, String uosvEpisodeId, String uosvOfferedSubjectsId);
  List<UserOwnSubjectVideo> findByUosvOfferedSubjectsId(String uosvOfferedSubjectsId);
  
}