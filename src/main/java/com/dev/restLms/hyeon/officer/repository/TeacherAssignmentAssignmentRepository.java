package com.dev.restLms.hyeon.officer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.entity.Assignment;

@Repository
public interface TeacherAssignmentAssignmentRepository extends JpaRepository<Assignment, String> {
    List<Assignment> findByOfferedSubjectsId(String offeredSubjectsId);
}