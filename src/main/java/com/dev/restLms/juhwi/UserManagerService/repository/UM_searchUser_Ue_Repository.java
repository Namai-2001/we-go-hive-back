package com.dev.restLms.juhwi.UserManagerService.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.dev.restLms.entity.User;
import com.dev.restLms.juhwi.UserManagerService.projecttion.UM_searchUser_Ue_Projection;


public interface UM_searchUser_Ue_Repository extends JpaRepository<User, String> {
    Page<UM_searchUser_Ue_Projection> findBySessionIdContaining(String sessionId, Pageable pageable);
    Page<UM_searchUser_Ue_Projection> findByCurrentConnectionContaining(String recentConnection, Pageable pageable);
    Page<UM_searchUser_Ue_Projection> findByPwChangeDateContaining(String pwChangeDate, Pageable pageable);
    Page<UM_searchUser_Ue_Projection> findByLongTermDisconnectionContaining(String longTermDisconnection, Pageable pageable);
    Page<UM_searchUser_Ue_Projection> findByUserInactivateContaining(String userInactivate, Pageable pageable);
    Page<UM_searchUser_Ue_Projection> findByUnsubscribeContaining(String unsubscribe, Pageable pageable);
    Page<UM_searchUser_Ue_Projection> findByUserEmailContaining(String userEmail, Pageable pageable);
    Page<UM_searchUser_Ue_Projection> findByUserNameContaining(String userName, Pageable pageable);
    Page<UM_searchUser_Ue_Projection> findBySessionIdIn(List<String> sessionId, Pageable pageable);
    // @Override
    // default Page<User> findAll(Pageable pageable) {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'findAll'");
    // }
}