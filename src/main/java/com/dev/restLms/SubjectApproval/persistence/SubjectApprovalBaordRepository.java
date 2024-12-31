package com.dev.restLms.SubjectApproval.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Board;

public interface SubjectApprovalBaordRepository extends JpaRepository<Board, String> {
    
}
