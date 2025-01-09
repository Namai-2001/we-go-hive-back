package com.dev.restLms.hyeon.profile.DTO;

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
public class UserCredentialsDTO {
    private String userId;
    private String userPw;
    private String socialKeyValue;
}
