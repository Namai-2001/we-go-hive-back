package com.dev.restLms.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "bookmark")
public class VideoPlayerBookMark {
  // 비디오 플레이어 내에서 CRD 진행 해야함

  // 북마크 시간이 기본키로 되어 있어서 원래는 ERD 상에서는 기본키 + 
  // 외래키 3개가 복합 기본키로 되어 있어야 하는데 지금은 시간이 기본키여서 중복됨.
  @Id // 기본키 + 하위 3개 외래키
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "INCREASE_ID")
  private Integer increaseId;

  @Column(name = "BM_SESSION_ID")
  private String bmSessionId;

  @Column(name = "BM_EPISODE_ID")
  private int bmEpisodeId;

  @Column(name = "BM_OFFERED_SUBJECTS_ID")
  private String bmOfferedSubjectsId;

  @Column(name = "bookmark_time")
  private Integer bookmarkTime;

  @Column(name = "bookmark_content")
  private String bookmarkContent;
}