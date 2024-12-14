package com.dev.restLms.ProcessList;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.dev.restLms.entity.Course;

@Repository
public interface ProcessListCourseRepository extends JpaRepository<Course,String> {
    List<ProcessListCourse> findBycourseId(String courseId);
}
