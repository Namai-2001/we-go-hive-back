package com.dev.restLms.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    private String courseId;

    private String sessionId;
    private String courseTitle;
    private String courseBoundary;
    private String courseCompleted;
    private String courseCapacity;
    private String courseProgressStatus;
    private String courseStartDate;
    private String courseEndDate;
    private String enrollStartDate;
    private String enrollEndDate;
    private String courseImg;

    @PrePersist
    public void generateUUID() {
        if (courseId == null) {
            courseId = UUID.randomUUID().toString();
        }
    }

}
