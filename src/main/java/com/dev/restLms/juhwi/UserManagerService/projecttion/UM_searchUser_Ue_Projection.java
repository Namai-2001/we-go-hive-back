package com.dev.restLms.juhwi.UserManagerService.projecttion;

public interface UM_searchUser_Ue_Projection {
    String getSessionId();
    String getUserEmail();
    String getUserName();
    String getUserInactivate();
    String getLongTermDisconnection();
    String getPwChangeDate();
    String getCurrentConnection();
    String getUnsubscribe();
}