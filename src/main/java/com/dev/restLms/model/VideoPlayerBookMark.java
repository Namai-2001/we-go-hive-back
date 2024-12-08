package com.dev.restLms.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bookmark")
public class VideoPlayerBookMark {
  // 비디오 플레이어 내에서 CRD 진행 해야함

  // 북마크 시간이 기본키로 되어 있어서 원래는 ERD 상에서는 기본키 + 
  // 외래키 3개가 복합 기본키로 되어 있어야 하는데 지금은 시간이 기본키여서 중복됨.
  @Id // 기본키 + 하위 3개 외래키
  private String bookmarkTime;
  private String sessionId;
  private int episodeId;
  private String offeredSubjectsId;
  private String bookmarkContent;
}