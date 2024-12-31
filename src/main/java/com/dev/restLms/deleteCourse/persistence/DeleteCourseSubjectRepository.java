package com.dev.restLms.deleteCourse.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Subject;

public interface DeleteCourseSubjectRepository extends JpaRepository <Subject, String> {
    
}
