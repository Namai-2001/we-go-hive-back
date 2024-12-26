package com.dev.restLms.sechan.subjectRegister.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.UserOwnAssignment;
// import com.dev.restLms.sechan.subjectRegister.projection.SR_U_Projection;

public interface SR_UOA_Repository extends JpaRepository<UserOwnAssignment, String> {
    List<UserOwnAssignment> findByOfferedSubjectsId(String offeredSubjectsId);
}
