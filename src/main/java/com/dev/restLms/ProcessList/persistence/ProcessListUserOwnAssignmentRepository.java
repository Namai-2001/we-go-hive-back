package com.dev.restLms.ProcessList.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.ProcessList.model.ProcessListUserOwnAssignment;

import java.util.List;


@Repository
public interface ProcessListUserOwnAssignmentRepository extends JpaRepository<ProcessListUserOwnAssignment, Object> {
    List<ProcessListUserOwnAssignment> findByOfferedSubjectsIdAndUserSessionId(String offeredSubjectsId, String userSessionId);
}
