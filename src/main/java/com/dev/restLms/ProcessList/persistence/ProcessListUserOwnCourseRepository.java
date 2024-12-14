package com.dev.restLms.ProcessList.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.ProcessList.model.ProcessListUserOwnCourse;

import java.util.List;
import java.util.Optional;


@Repository
public interface ProcessListUserOwnCourseRepository extends JpaRepository<ProcessListUserOwnCourse, Object>{
    List<ProcessListUserOwnCourse> findByCourseId(String courseId);
    Optional<ProcessListUserOwnCourse> findByCourseIdAndSessionId(String courseId, String sessionId);
    List<ProcessListUserOwnCourse> findBySessionId(String sessionId);
}
