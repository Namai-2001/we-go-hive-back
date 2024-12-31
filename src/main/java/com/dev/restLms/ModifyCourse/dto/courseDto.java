package com.dev.restLms.ModifyCourse.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class courseDto {
    String courseTitle;
    String courseCapacity;
    String courseStartDate;
    String courseEndDate;
    String enrollStartDate;
    String enrollEndDate;
}
