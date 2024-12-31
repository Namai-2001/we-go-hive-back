package com.dev.restLms.announcement;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Board;
import java.util.Optional;


public interface AnnouncementBoardRepository extends JpaRepository<Board, String> {
    Optional<announcementBoard> findByBoardCategory(String boardCategory);
}
