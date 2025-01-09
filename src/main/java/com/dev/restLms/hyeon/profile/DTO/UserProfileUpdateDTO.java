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
public class UserProfileUpdateDTO {
    private String userBirth;
    private String userEmail;
    private String userName;
    private String phoneNumber;
    private String nickname;
}
