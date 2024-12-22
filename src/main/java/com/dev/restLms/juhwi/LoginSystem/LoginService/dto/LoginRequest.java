package com.dev.restLms.juhwi.LoginSystem.LoginService.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 로그인 DTO
public class LoginRequest {

    private String userId;

    private String userPw;
}