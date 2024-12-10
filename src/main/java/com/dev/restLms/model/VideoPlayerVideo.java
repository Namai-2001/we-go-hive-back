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
@Table(name = "video")
public class VideoPlayerVideo {
  @Id
  @Column(name = "video_id")
  private String videoId;

  @Column(name = "max")
  private String max;

  @Column(name = "video_title")
  private String videoTitle;

  @Column(name = "video_link")
  private String videoLink;

  @Column(name = "video_img")
  private String videoImg;
}