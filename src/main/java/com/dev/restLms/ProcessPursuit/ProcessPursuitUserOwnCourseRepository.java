package com.dev.restLms.ProcessPursuit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.entity.UserOwnCourse;

import java.util.List;


@Repository
public interface ProcessPursuitUserOwnCourseRepository extends JpaRepository<UserOwnCourse, String>{
    List<ProcessPursuitUserOwnCourse> findByCourseId(String courseId);
}
