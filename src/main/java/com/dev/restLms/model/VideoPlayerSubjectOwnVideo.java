package com.dev.restLms.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "subjectownvideo")
public class VideoPlayerSubjectOwnVideo {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "episode_id")
  private Integer episodeId;

  @Column(name = "SOV_OFFERED_SUBJECTS_ID")
  private String sovOffredSubjectsId;

  @Column(name = "VIDEO_SORT_INDEX")
  private Integer videoSortIndex;

  @Column(name = "SOV_VIDEO_ID")
  private String sovVideoId;
}
