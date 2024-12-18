package com.dev.restLms.FreeBulletinBoard;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Board;
import java.util.Optional;


public interface FreeBulletinBoardRepository extends JpaRepository<Board, String> {
    Optional<FreeBulletinBoard> findByBoardCategory(String boardCategory);
}
