package com.dev.restLms.CreateCourse.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.Board;

public interface CreateCourseBardRepository extends JpaRepository<Board, String> {
    
}
