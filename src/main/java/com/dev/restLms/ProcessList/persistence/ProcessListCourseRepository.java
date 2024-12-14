package com.dev.restLms.ProcessList.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.ProcessList.model.ProcessListCourse;

@Repository
public interface ProcessListCourseRepository extends JpaRepository<ProcessListCourse,String> {
    List<ProcessListCourse> findBycourseId(String courseId);
}
