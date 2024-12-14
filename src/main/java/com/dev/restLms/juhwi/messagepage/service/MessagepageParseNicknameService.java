package com.dev.restLms.juhwi.messagepage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.dev.restLms.juhwi.messagepage.projection.MessagepageParseNicknameProjection;
import com.dev.restLms.juhwi.messagepage.repository.MessagepageParseNicknameRepository;

/* 의도:
 * 1. 호출 관계 로직을 컨트롤러에서 분리하기 위해 제작 (예시: 닉네임으로 찾아서 페이지 네이션하는) -> 단순히 페이지를 가져오는 형태로만 쓰기 위함
 * 
 */
@Service
public class MessagepageParseNicknameService {
    @Autowired
    private MessagepageParseNicknameRepository messagepageParseNicknameRepository;

    public Page<MessagepageParseNicknameProjection> getUsersByNickname(String nickname, int page, int size){
        Pageable pageable = PageRequest.of(page, size);
        return messagepageParseNicknameRepository.findByNicknameContaining(nickname, pageable);
    }
}