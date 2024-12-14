package com.dev.restLms.pairToJuSe.SubjectVideoService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Subject;
import com.dev.restLms.pairToJuSe.SubjectVideoService.projection.Ch_S_Projection;

public interface Ch_S_Repository extends JpaRepository<Subject, String>{
    Ch_S_Projection findBySubjectId(String subjectId);
}