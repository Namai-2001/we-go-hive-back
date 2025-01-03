package com.dev.restLms.hyeon.officer.DTO;

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
public class SubjectDTO {
    private String subjectId;
    private String subjectName;
    private String subjectDesc;
    private String subjectCategory;
    private String subjectImageLink;
    private String subjectPromotion;
    private String teacherSessionId;
}
