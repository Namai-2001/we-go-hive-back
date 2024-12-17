package com.dev.restLms.entity;

import jakarta.persistence.Entity;
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
  private String fileNo;
  
  private Integer seqNo;
  private String orgFileNm;
  private String filePath;
  private String fileSize;
  private String uploadDt;
  private String encFileNm;
  private String uploaderSessionId;
    @PrePersist
    public void prePersistSeqNo() {
        if (this.seqNo == null) {
            this.seqNo = 0; // 초기값 설정 (DB에서 AUTO_INCREMENT로 덮어씌워짐)
        }
    }
}
