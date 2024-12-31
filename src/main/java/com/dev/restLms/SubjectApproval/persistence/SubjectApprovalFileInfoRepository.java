package com.dev.restLms.SubjectApproval.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.FileInfo;
import java.util.Optional;


public interface SubjectApprovalFileInfoRepository extends JpaRepository<FileInfo, String> {
    Optional<FileInfo> findByFileNo(String fileNo);
}
