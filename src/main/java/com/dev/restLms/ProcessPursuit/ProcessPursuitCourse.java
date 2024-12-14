package com.dev.restLms.ProcessPursuit;

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
public class ProcessPursuitCourse {
	private String courseId; 
	
	private String sessionId;
	private String courseTitle;
	private int courseCapacity;
	private String enrollStartDate;
	private String enrollEndDate;
	
}
