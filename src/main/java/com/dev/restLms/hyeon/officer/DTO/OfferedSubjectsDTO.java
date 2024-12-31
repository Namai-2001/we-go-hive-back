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
public class OfferedSubjectsDTO {
    private String offeredSubjectsId;
    private String courseId;
    private String officerSessionId;
    private String subjectId;
    private String teacherSessionId;
}
