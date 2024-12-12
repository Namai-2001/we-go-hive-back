package com.dev.restLms.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "userownsubjectvideo")
public class VideoPlayerUserOwnSubjectVideo {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "INCREASE_ID")
  private int increaseId;

  @Column(name = "UOSV_SESSION_ID")
  private String uosvSessionId;
  
  @Column(name = "UOSV_EPISODE_ID")
  private Integer uosvEpisodeId;

  @Column(name = "UOSV_OFFERED_SUBJECTS_ID")
  private String uosvOfferedSubjectsid;

  @Column(name = "progress")
  private Integer progress;

  @Column(name = "final")
  private Integer finalLocation;
}
