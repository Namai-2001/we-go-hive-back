package com.dev.restLms.pairToJuSe.SubjectVideoService.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.dev.restLms.entity.Course;
import com.dev.restLms.pairToJuSe.SubjectVideoService.projection.C_Projection;

public interface C_Repository extends JpaRepository<Course, String>{
    List<C_Projection> findByCourseId(String courseId);
}
