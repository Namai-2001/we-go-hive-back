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
public class OfferedSubjects {
    @Id
    private String offeredSubjectsId;
    private String courseId;
    private String subjectId;
    private String officerSessionId;
    private String teacherSessionId;

    
  @PrePersist
  public void generateUUID() {
      if (offeredSubjectsId == null) {
        offeredSubjectsId = UUID.randomUUID().toString();
      }
  }
}
