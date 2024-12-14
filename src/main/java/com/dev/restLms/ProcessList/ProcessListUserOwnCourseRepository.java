package com.dev.restLms.ProcessList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.entity.UserOwnCourse;

import java.util.List;
import java.util.Optional;


@Repository
public interface ProcessListUserOwnCourseRepository extends JpaRepository<UserOwnCourse, Object>{
    List<ProcessListUserOwnCourse> findByCourseId(String courseId);
    Optional<ProcessListUserOwnCourse> findByCourseIdAndSessionId(String courseId, String sessionId);
    List<ProcessListUserOwnCourse> findBySessionId(String sessionId);
}
