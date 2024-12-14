package com.dev.restLms.QuestionBoard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.entity.Board;

import java.util.Optional;


@Repository
public interface QuestionBoardRepository extends JpaRepository<Board, String> {
    Optional<QuestionBoard> findByOfferedSubjectsId(String offeredSubjectsId);
}
