package com.dev.restLms.pairToJuSe.SubjectVideoService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.OfferedSubjects;
import com.dev.restLms.pairToJuSe.SubjectVideoService.projection.Ch_OS_S_Projection;


public interface Ch_OS_S_Repository extends JpaRepository<OfferedSubjects, String>{
    Ch_OS_S_Projection findByOfferedSubjectsId(String offeredSubjectsId);
}
