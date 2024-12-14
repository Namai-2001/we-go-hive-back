package com.dev.restLms.VideoPlayerPage.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookMarkDTO {
  private Integer bookmarkTime;
  private String bookmarkContent;
}