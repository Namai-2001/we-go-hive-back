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
public class CourseOwnSubject {

    @Id
    private String increaseId;

    private String courseId;
    private String officerSessionId;
    private String subjectId;
    private String subjectApproval; //과목 승인 여부

    @PrePersist
    public void generateUUID() {
        if (increaseId == null) {
            increaseId = UUID.randomUUID().toString();
        }
    }
}
