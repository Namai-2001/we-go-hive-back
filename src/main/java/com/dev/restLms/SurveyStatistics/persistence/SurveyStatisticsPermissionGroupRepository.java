package com.dev.restLms.SurveyStatistics.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.SurveyStatistics.projection.SurveyStatisticsPermissionGroup;
import com.dev.restLms.entity.PermissionGroup;
import java.util.Optional;


public interface SurveyStatisticsPermissionGroupRepository extends JpaRepository<PermissionGroup, String> {
    Optional<SurveyStatisticsPermissionGroup> findByPermissionGroupUuid(String permissionGroupUuid);
}
