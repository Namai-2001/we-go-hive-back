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
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {
  @Id
  private String assignmentId;
  private String teacherSessionId;
  private String offeredSubjectsId;
  private String assignmentContent;
  private String deadline;
  private String noticeNo;
  private String cutline;
  private String assignmentTitle;

    @PrePersist
    public void generateUUID() {
        if (assignmentId == null) {
          assignmentId = UUID.randomUUID().toString();
        }
    }
}
