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
public class Subject {

    @Id
    private String subjectId;
    private String subjectName;
    private String subjectDesc;
    private String subjectCategory;
    private String subjectImageLink;
    private String subjectPromotion;
    private String teacherSessionId;

    @PrePersist
    public void generateUUID() {
        if (subjectId == null) {
            subjectId = UUID.randomUUID().toString();
        }
    }
}