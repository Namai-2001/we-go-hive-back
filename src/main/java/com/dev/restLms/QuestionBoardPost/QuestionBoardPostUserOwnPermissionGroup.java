package com.dev.restLms.QuestionBoardPost;

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
public class QuestionBoardPostUserOwnPermissionGroup {

    private String permissionGroupUuid2;

    private String sessionId;
    
}
