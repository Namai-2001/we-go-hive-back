package com.dev.restLms.sechan.SurveyMain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Course;
import com.dev.restLms.sechan.SurveyMain.projection.SM_C_Projection;

public interface SM_C_Repository extends JpaRepository<Course, String>{
    List<SM_C_Projection> findByCourseIdIn(List<String> courseIds);
}
