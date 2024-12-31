package com.dev.restLms.deleteCourse.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.deleteCourse.projection.DeleteCourseBoardPost;
import com.dev.restLms.entity.BoardPost;
import java.util.List;


public interface DeleteCourseBoardPostRepository extends JpaRepository<BoardPost, String> {
    boolean existsByBoardId(String boardId);
    List<DeleteCourseBoardPost> findByBoardId(String boardId);
}
