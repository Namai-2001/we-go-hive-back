package com.dev.restLms.hyeon.officer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.entity.Board;

@Repository
public interface TeacherAssignmentBoardRepository extends JpaRepository<Board, String> {
    Optional<Board> findByOfferedSubjectsId(String offeredSubjectsId);
    Boolean existsByOfferedSubjectsIdAndBoardCategory(String offeredSubjectsId, String boardCategory);
}