package com.dev.restLms.ProcessList;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessListCourse {
	private String courseId; 
	
	private String sessionId;

	private String courseTitle;
	
	private int courseCapacity;

	private String enrollStartDate;

	private String enrollEndDate;

	private String courseImg;
	
}
