package com.dev.restLms.hyeon.course.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dev.restLms.entity.UserOwnAssignment;

@Repository
public interface UserOwnAssignmentRepository extends JpaRepository<UserOwnAssignment, String> {
    // 추가적인 쿼리 메소드가 필요한 경우 여기에 작성
	<Optional>UserOwnAssignment findByuserSessionId(String userSessionId);
    List<UserOwnAssignment> findAllByuserSessionId(String userSessionId);
}
