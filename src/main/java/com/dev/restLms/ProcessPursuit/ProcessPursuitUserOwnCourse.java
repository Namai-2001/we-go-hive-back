package com.dev.restLms.ProcessPursuit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessPursuitUserOwnCourse {

    private String sessionId;

    private String courseId;
}
