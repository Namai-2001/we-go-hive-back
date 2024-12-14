package com.dev.restLms.pairToJuSe.SubjectVideoService.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Video;
import com.dev.restLms.pairToJuSe.SubjectVideoService.projection.Ch_V_Projection;



public interface Ch_V_Repository extends JpaRepository<Video, String>{
    Ch_V_Projection findByVideoId(String videoId);
}