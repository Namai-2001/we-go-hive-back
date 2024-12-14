package com.dev.restLms.QuestionBoard.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "offeredsubjects")
public class QuestionBoardOfferedSubjects {

    @Id
    @Column(name = "OFFERED_SUBJECTS_ID")
    private String offeredSubjectsId;

    @Column(name = "TEACHER_SESSION_ID")
    private String teacherSessionId;

    @Column(name = "SUBJECT_ID")
    private String subjectId;

    @Column(name = "OFFICER_SESSION_ID")
    private String officerSessionId;
    
}
