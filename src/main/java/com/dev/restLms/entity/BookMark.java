package com.dev.restLms.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookMark {
  // 비디오 플레이어 내에서 CRD 진행 해야함
  // 북마크 시간이 기본키로 되어 있어서 원래는 ERD 상에서는 기본키 + 
  // 외래키 3개가 복합 기본키로 되어 있어야 하는데 지금은 시간이 기본키여서 중복됨.

  @Id
  private String increaseId;
  private String bmSessionId;
  private String bmEpisodeId;
  private String bmOfferedSubjectsId;
  private String bookmarkContent;
  private String bookmarkTime;

  @PrePersist
  public void generateUUID() {
      if (increaseId == null) {
        increaseId = UUID.randomUUID().toString();
      }
  }
}