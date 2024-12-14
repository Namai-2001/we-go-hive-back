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
public class ProcessListUserOwnSubjectVideo {
    private String increaseId;

    private String uosvSessionId;

    private String uosvEpisodeId;

    private String uosvOfferedSubjectsId;

    private String progress;
}
