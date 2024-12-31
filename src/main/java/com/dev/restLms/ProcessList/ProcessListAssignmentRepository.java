package com.dev.restLms.ProcessList;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Assignment;
import java.util.List;


public interface ProcessListAssignmentRepository extends JpaRepository<Assignment, String> {
    List<ProcessListAssignment> findByOfferedSubjectsId(String offeredSubjectsId);
}
