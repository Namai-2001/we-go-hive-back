package com.dev.restLms.pairToJuSe.SubjectVideoService.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.UserOwnSubjectVideo;
import com.dev.restLms.pairToJuSe.SubjectVideoService.projection.Ch_UOSV_Projection;


public interface Ch_UOSV_Repository extends JpaRepository<UserOwnSubjectVideo, String>{
    Ch_UOSV_Projection findByUosvEpisodeIdAndUosvOfferedSubjectsId(String uosvEpisodeId, String uosvOfferedSubjectsId);
    Ch_UOSV_Projection findByUosvSessionIdAndUosvOfferedSubjectsIdAndUosvEpisodeId(String uosvSessionId, String uosvOfferedSubjectsId, String uosvEpisodeId);
}