package com.dev.restLms.hyeon.course.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.entity.UserOwnCourse;

@Repository
public interface UserOwnCourseRepository extends JpaRepository<UserOwnCourse, String> {
    // 추가적인 쿼리 메소드가 필요한 경우 여기에 작성
	List<UserOwnCourse> findBysessionId(String sessionId);
}
