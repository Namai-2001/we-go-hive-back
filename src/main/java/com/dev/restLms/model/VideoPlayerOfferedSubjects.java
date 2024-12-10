package com.dev.restLms.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "offeredsubjects")
public class VideoPlayerOfferedSubjects {
  @Id
  @Column(name = "offered_subjects_id")
  private String offeredSubjectsId;

  @Column(name = "teacher_session_id")
  private String teacherSessionId;
}
