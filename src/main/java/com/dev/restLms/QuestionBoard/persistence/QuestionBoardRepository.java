package com.dev.restLms.QuestionBoard.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.QuestionBoard.model.QuestionBoard;
import java.util.Optional;


@Repository
public interface QuestionBoardRepository extends JpaRepository<QuestionBoard, String> {
    Optional<QuestionBoard> findByOfferedSubjectsId(String offeredSubjectsId);
}
