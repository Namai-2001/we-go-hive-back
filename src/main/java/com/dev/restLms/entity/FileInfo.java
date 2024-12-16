package com.dev.restLms.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FileInfo {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID) // 기본 키
  private String fileNo;
  
  private Integer seqNo;
  private String orgFileNm;
  private String filePath;
  private String fileSize;
  private String uploadDt;

    @PrePersist
    public void prePersistSeqNo() {
        if (this.seqNo == null) {
            this.seqNo = 0; // 초기값 설정 (DB에서 AUTO_INCREMENT로 덮어씌워짐)
        }
    }
}
