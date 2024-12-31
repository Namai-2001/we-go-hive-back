package com.dev.restLms.ModifyCourse.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.ModifyCourse.projection.ModifyCourseBoardPost;
import com.dev.restLms.entity.BoardPost;
import java.util.List;


public interface ModifyCourseBoardPostRepository extends JpaRepository<BoardPost, String> {
    List<ModifyCourseBoardPost> findByBoardId(String boardId);
}
