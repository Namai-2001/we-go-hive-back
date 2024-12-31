package com.dev.restLms.HomePage.dto;

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
public class RandSubjectVidDTO {
  private String subjectName;
  private String vidLink;
  private String vidTitle;
  private String offeredSubjectsId;
}
