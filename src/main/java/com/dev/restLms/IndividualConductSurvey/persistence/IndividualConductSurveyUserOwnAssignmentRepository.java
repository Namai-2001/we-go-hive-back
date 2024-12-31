package com.dev.restLms.IndividualConductSurvey.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.IndividualConductSurvey.projection.IndividualConductSurveyUserOwnAssignment;
import com.dev.restLms.entity.UserOwnAssignment;
import java.util.List;


public interface IndividualConductSurveyUserOwnAssignmentRepository extends JpaRepository<UserOwnAssignment, String> {
    List<IndividualConductSurveyUserOwnAssignment> findByOfferedSubjectsId(String offeredSubjectsId);
}
