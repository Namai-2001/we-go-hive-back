package com.dev.restLms.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Video {
  @Id
  private String videoId;
  private String max;
  private String videoTitle;
  private String videoLink;
  private String videoImg;

  @PrePersist
  public void generateUUID() {
      if (videoId == null) {
        videoId = UUID.randomUUID().toString();
      }
  }
}