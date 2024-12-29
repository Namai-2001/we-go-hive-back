package com.dev.restLms.sechan.courseCompletePage.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Assignment;

public interface CCP_A_Repository extends JpaRepository<Assignment, String> {
    List<Assignment> findByOfferedSubjectsId(String offeredSubjectsId);
}
