package com.dev.restLms.CreateCourse.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.CreateCourse.projection.CreateCourseOfferedSubjects;
import com.dev.restLms.entity.OfferedSubjects;
import java.util.List;


public interface CreateCourseOfferedSubjectsRepository extends JpaRepository<OfferedSubjects, String> {
    List<CreateCourseOfferedSubjects> findByTeacherSessionId(String teacherSessionId);
}
