package com.dev.restLms.model;

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
// 내가 여기서 뭘 빼야 할까?
// 개설과목 코드가 있으니, 강사의 아이디를 뺴와야 한다.
public class VideoPlayerOfferedSubjects {
  @Id
  private String offeredSubjectsId;

  private String teacherSessionId;
}
