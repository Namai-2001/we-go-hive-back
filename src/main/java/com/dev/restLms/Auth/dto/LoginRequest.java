package com.dev.restLms.Auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
// 로그인 DTO
public class LoginRequest {

    private String userId;

    private String userPw;
}
