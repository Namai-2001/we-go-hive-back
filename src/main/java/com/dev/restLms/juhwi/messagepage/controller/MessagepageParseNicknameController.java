package com.dev.restLms.juhwi.messagepage.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dev.restLms.juhwi.messagepage.projection.MessagepageParseNicknameProjection;
import com.dev.restLms.juhwi.messagepage.service.MessagepageParseNicknameService;

import io.swagger.v3.oas.annotations.Parameter;

/*
 * 1. 단순 엔드포인트만 연결하는 형태로 제작
 * 2. OpenAPI 형태의 문서제작과, 엔드포인트 설정만 하는 형태로 설계하는 것이 권장됨
 */

@RestController
public class MessagepageParseNicknameController {

    @Autowired
    private MessagepageParseNicknameService userService;
    

    @GetMapping("/message-users")
    public Page<MessagepageParseNicknameProjection> getUsers(
        @Parameter(description = "닉네임", required = true)
        @RequestParam String nickname, 
        @Parameter(description = "페이지 수", required = true)
        @RequestParam int page,
        @Parameter(description = "페이징 개수", required = true)
        @RequestParam int size) {
        return userService.getUsersByNickname(nickname, page, size);
    }
}
