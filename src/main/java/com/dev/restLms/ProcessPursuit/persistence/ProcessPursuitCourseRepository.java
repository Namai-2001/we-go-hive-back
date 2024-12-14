package com.dev.restLms.ProcessPursuit.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.ProcessPursuit.model.ProcessPursuitCourse;

@Repository
public interface ProcessPursuitCourseRepository extends JpaRepository<ProcessPursuitCourse,String> {
    List<ProcessPursuitCourse> findBycourseId(String courseId);
}
