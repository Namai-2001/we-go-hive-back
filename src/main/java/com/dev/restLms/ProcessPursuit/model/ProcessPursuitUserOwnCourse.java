package com.dev.restLms.ProcessPursuit.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "userowncourse")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessPursuitUserOwnCourse {

	@Id
    private String sessionId;

	@Column(name = "COURSE_ID")
    private String courseId;
}
