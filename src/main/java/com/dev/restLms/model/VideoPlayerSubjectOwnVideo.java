package com.dev.restLms.model;

import jakarta.persistence.Entity;
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
  private int episodeId;

  private String offeredSubjectsid;
  private int videoSortIndex;
  private String videoId;
}
