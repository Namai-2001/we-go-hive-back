package com.dev.restLms.announcementPost;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.FileInfo;
import java.util.Optional;


public interface AnnouncementPostFileInfoRepository extends JpaRepository<FileInfo, String> {
    Optional<FileInfo> findByFileNo(String fileNo);
}
