package com.dev.restLms.pairToJuSe.SubjectVideoService.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.SubjectOwnVideo;
import com.dev.restLms.pairToJuSe.SubjectVideoService.projection.Ch_SOV_Projection;



public interface Ch_SOV_Repository extends JpaRepository<SubjectOwnVideo, String> {
    Page<Ch_SOV_Projection> findBySovOfferedSubjectsIdContaining(String sovOfferedSubjectsId, Pageable pageable);
}