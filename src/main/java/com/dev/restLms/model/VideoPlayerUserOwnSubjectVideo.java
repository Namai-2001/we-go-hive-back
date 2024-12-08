package com.dev.restLms.model;

import jakarta.persistence.Column;
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
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "userownsubjectvideo")
public class VideoPlayerUserOwnSubjectVideo {
  @Id
  private String sessionId;
  
  private int episodeId;
  private String offeredSubjectsid;
  private int progress;

  @Column(name = "FINAL")
  private String finalLocation;
}
