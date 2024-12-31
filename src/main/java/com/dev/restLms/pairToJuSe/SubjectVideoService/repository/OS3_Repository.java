package com.dev.restLms.pairToJuSe.SubjectVideoService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.OfferedSubjects;
// import com.dev.restLms.pairToJuSe.SubjectVideoService.projection.S_Projection;
// import com.dev.restLms.pairToJuSe.SubjectVideoService.projection.U_Projection;

public interface OS3_Repository extends JpaRepository<OfferedSubjects, String> {
    // OfferedSubjectsId로 OfferedSubjects 조회
    Optional<OfferedSubjects> findByOfferedSubjectsId(String offeredSubjectsId);
}
