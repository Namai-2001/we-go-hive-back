package com.dev.restLms.hyeon.course.DTO;

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
public class CourseDTO {
    private String courseId;
    private String sessionId;
    private String courseTitle;
    private String courseBoundary;
    private String courseCompleted;
    private String courseCapacity;
    private String courseStartDate;
    private String courseEndDate;
}
