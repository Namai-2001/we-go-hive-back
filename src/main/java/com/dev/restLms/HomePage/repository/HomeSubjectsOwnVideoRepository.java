package com.dev.restLms.HomePage.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.HomePage.projection.HomeVidIdProjection;
import com.dev.restLms.entity.SubjectOwnVideo;

public interface HomeSubjectsOwnVideoRepository extends JpaRepository<SubjectOwnVideo, String> {
  Optional<HomeVidIdProjection> findBySovOfferedSubjectsIdAndVideoSortIndex(String offeredsubjectId, String sortIdx);
}