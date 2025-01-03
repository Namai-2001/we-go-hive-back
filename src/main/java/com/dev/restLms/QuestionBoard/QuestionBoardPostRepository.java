package com.dev.restLms.QuestionBoard;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // 올바른 Pageable import
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


// import java.util.List;
import com.dev.restLms.entity.BoardPost;

@Repository
public interface QuestionBoardPostRepository extends JpaRepository<BoardPost, String> {
    // 기존 메서드
    List<QuestionBoardPost> findByBoardId(String boardId, Sort sort);

    Optional<QuestionBoardPost> findByPostId(String postId);

    // Pageable을 사용하는 메서드
    Page<QuestionBoardPost> findByBoardId(String boardId, Pageable pageable);
}
