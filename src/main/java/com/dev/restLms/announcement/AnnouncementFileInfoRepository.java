package com.dev.restLms.announcement;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.FileInfo;
import java.util.Optional;


public interface AnnouncementFileInfoRepository extends JpaRepository <FileInfo, String> {
    Optional<FileInfo> findByFileNo(String fileNo);
}
