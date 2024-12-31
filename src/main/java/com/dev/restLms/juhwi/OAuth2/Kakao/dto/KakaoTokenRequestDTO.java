package com.dev.restLms.juhwi.OAuth2.Kakao.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class KakaoTokenRequestDTO {
    
    private String grant_type;
    private String client_id;
    private String redirect_uri; // redirect_uri
    private String code; // code
}
