package com.dev.restLms.AssignmentPage.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.AssignmentPage.projection.AssignmentPageOfferedSubjectsProjection;
import com.dev.restLms.entity.OfferedSubjects;

@Repository
public interface AssignmentPageOfferedSubjectsRepository extends JpaRepository<OfferedSubjects, String> {
  Optional<AssignmentPageOfferedSubjectsProjection> findByOfferedSubjectsId(String offeredSubjectsId);
}
