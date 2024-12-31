package com.dev.restLms.ProcessList;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.FileInfo;
import java.util.Optional;


public interface ProcessListFileinfoReposutory extends JpaRepository <FileInfo, String> {
    Optional<FileInfo> findByFileNo(String fileNo);
}
