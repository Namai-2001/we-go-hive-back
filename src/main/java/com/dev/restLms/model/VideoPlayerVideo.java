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
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "video")
public class VideoPlayerVideo {
  @Id
  private String videoId;

  private String max;
  private String videoTitle;
  private String videoLink;
}
