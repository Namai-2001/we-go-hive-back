package com.dev.restLms.ProcessList.model;

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
@Table(name = "course")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessListCourse {
	@Id
	@Column(name = "COURSE_ID")
	private String courseId; 
	
	private String sessionId; // 관리자 세션 아이디

	@Column(name = "COURSE_TITLE")
	private String courseTitle;
	
	@Column(name = "COURSE_CAPACITY")
	private int courseCapacity;

	@Column(name = "ENROLL_START_DATE")
	private String enrollStartDate;

	@Column(name = "ENROLL_END_DATE")
	private String enrollEndDate;

	@Column(name = "COURSE_IMG")
	private String courseImg;
	
}
