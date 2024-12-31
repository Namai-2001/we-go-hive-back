package com.dev.restLms.ModifyCourse.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class checkSubjectDto {
    String subjectId;
    String subjectName;
    String subjectDesc;
    String subjectCategory;
    String teacherSessionId;
}
