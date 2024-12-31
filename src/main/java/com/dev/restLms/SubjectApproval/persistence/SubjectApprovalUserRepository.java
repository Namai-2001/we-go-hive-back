package com.dev.restLms.SubjectApproval.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.SubjectApproval.projection.SubjectApprovalUser;
import com.dev.restLms.entity.User;
import java.util.Optional;



public interface SubjectApprovalUserRepository extends JpaRepository<User, String> {
    Optional<SubjectApprovalUser> findBySessionId(String sessionId);
}
