package com.dev.restLms.juhwi.OAuth2.Naver.controller;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Slf4j
@RestController
@RequestMapping("/google")
public class NaverController {
    // 해시 파라미터는 읽히지 않아서 body 로 받아야함
    @GetMapping("/authorize")
    public String getMethodName(@RequestBody String body) {
        return body;
    }
}
