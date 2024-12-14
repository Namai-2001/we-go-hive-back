package com.dev.restLms.ProcessPursuit;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.entity.Course;

@Repository
public interface ProcessPursuitCourseRepository extends JpaRepository<Course,String> {
    List<ProcessPursuitCourse> findBycourseId(String courseId);
}
