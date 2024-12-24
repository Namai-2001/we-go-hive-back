package com.dev.restLms.SurveyStatistics.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.UserOwnPermissionGroup;
import java.util.Optional;


public interface SurveyStatisticsUserOwnPermssionGroupRepository extends JpaRepository<UserOwnPermissionGroup, String> {
    Optional<UserOwnPermissionGroup> findBySessionId(String sessionId);
}
