package com.dev.restLms.juhwi.OAuth2.Kakao.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoTokenResponseDTO {
    private String token_type;       // token_type
    private String access_token;     // access_token
    private int expires_in;          // expires_in
    private String refresh_token;    // refresh_token
    private int refresh_token_expires_in; // refresh_token_expires_in
    private String scope;            // scope
}