package com.dev.restLms.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookMarkDTO {
  private Integer bookmarkTime;
  private String bookmarkContent;
}