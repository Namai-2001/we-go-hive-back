package com.dev.restLms.VideoPlayerPage.projection;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VideoPlayerUserOwnSubjectVideo {
  private String increaseId;
  private String uosvSessionId;
  private String uosvEpisodeId;
  private String uosvOfferedSubjectsId;
  private String progress;
  private String finalLocation;
}
