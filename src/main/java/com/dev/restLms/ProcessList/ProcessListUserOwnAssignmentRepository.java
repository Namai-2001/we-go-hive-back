package com.dev.restLms.ProcessList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.entity.UserOwnAssignment;

import java.util.List;


@Repository
public interface ProcessListUserOwnAssignmentRepository extends JpaRepository<UserOwnAssignment, Object> {
    List<ProcessListUserOwnAssignment> findByOfferedSubjectsIdAndUserSessionId(String offeredSubjectsId, String userSessionId);
}
