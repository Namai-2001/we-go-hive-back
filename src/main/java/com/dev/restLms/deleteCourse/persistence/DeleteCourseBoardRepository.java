package com.dev.restLms.deleteCourse.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.deleteCourse.projection.DeleteCourseBoard;
import com.dev.restLms.entity.Board;
import java.util.Optional;


public interface DeleteCourseBoardRepository extends JpaRepository<Board, String> {

    Optional<DeleteCourseBoard> findByOfferedSubjectsId(String offeredSubjectsId);
    
}
