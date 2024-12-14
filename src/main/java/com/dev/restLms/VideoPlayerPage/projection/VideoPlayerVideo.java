package com.dev.restLms.VideoPlayerPage.projection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VideoPlayerVideo {
  private String videoId;
  private String max;
  private String videoTitle;
  private String videoLink;
  private String videoImg;
}