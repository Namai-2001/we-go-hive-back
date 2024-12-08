package com.dev.restLms.test;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Parameter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class SessionToNickname {

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    SessionToNicknameEntityRepository sessionToNicknameEntity;

    @GetMapping("/messages1")
    public Map<String, String> getMethodName1(
            @Parameter(required = true, description = "유저 세션 ID") @RequestParam String sessionId) {
        // 특정 사용자의 아이디를 받은 것으로 설정
        // List<Message> result = messageRepository.findBySenderSessionId(sessionId);
        List<Message> result = messageRepository.findAll();
        // 내가 sender인 message 조회



        Map<String, String> resultMap = new HashMap<>();
        int i = 0;
        for (Message res : result) {
            resultMap.put(Integer.toString(i++), sessionToNicknameEntity.findBySessionId(res.getSenderSessionId()).getNickname());
        }

        return resultMap;
    }

    // @GetMapping("/messages2")
    // public List<SessionToNicknameEntity> getMethodName2() {
    //     return sessionToNicknameEntity.findBySessionId("2b3c4d5e-6f7g-8h9i-0j1k-l2m3n4o5p6q7");
    // }

}
