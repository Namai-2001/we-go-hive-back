package com.dev.restLms.ModifyCourse.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.ModifyCourse.projection.ModifyCourseBoard;
import com.dev.restLms.entity.Board;
import java.util.Optional;


public interface ModifyCourseBoardRepository extends JpaRepository<Board, String> {
    Optional<ModifyCourseBoard> findByOfferedSubjectsId(String offeredSubjectsId);
}
