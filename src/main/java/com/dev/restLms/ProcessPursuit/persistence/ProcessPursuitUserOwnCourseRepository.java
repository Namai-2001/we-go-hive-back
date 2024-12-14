package com.dev.restLms.ProcessPursuit.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.ProcessPursuit.model.ProcessPursuitUserOwnCourse;
import java.util.List;


@Repository
public interface ProcessPursuitUserOwnCourseRepository extends JpaRepository<ProcessPursuitUserOwnCourse, String>{
    List<ProcessPursuitUserOwnCourse> findByCourseId(String courseId);
}
