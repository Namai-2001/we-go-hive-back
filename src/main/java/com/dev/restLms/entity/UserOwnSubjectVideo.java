package com.dev.restLms.entity;

import java.util.UUID;

import jakarta.persistence.Column;
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
@AllArgsConstructor
@NoArgsConstructor
public class UserOwnSubjectVideo {
  @Id
  private String increaseId;
  private String uosvSessionId;
  private String uosvEpisodeId;
  private String uosvOfferedSubjectsId;
  private String progress;

  @Column(name = "final")
  private String uosvFinal;

  @PrePersist
  public void generateUUID() {
      if (increaseId == null) {
        increaseId = UUID.randomUUID().toString();
      }
  }
}
