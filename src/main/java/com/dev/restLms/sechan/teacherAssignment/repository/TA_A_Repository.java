package com.dev.restLms.sechan.teacherAssignment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Assignment;
import com.dev.restLms.sechan.teacherAssignment.projection.TA_A_Projection;

import java.util.List;
import java.util.Optional;


public interface TA_A_Repository extends JpaRepository<Assignment, String> {
    List<Assignment> findByOfferedSubjectsId(String offeredSubjectsId);  
    
    // 과제 상세 Projection 조회
    Optional<TA_A_Projection> findByAssignmentId(String assignmentId);
}
