package com.dev.restLms.juhwi.MessageService.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.User;
import com.dev.restLms.juhwi.MessageService.projection.MSG_GetFriendList_Projection;


public interface MSG_GetFriendList_Repository extends JpaRepository<User, String>{
    Page<MSG_GetFriendList_Projection> findByNicknameContaining(String nickname, Pageable pageable);
}