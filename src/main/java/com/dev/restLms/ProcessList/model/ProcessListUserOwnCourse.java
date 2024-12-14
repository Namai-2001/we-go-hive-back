package com.dev.restLms.ProcessList.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "userowncourse")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessListUserOwnCourse {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "INCREASE_ID")
    private int increaseId;
    
    private String sessionId;

	@Column(name = "COURSE_ID")
    private String courseId;

    @Column(name = "OFFICER_SESSION_ID")
    private String officerSessionId;

    @Column(name = "COURSE_APPROVAL", columnDefinition = "CHAR(1) DEFAULT 'F'")
    private String courseApproval;
}
