package com.dev.restLms.juhwi.MessageService.dto;

import java.util.List;

import lombok.Data;

@Data
public class MSG_MessagePost_DTO {
    private List<String> targetSessionIds;
    private String messageTitle;
    private String messageContent;
}
