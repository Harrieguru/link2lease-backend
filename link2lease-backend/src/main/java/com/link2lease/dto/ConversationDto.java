package com.link2lease.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationDto {
    private Long otherUserId;
    private String otherUserName;
    private String otherUserEmail;
    private MessageDto latestMessage;
    private Long unreadCount;
    private LocalDateTime lastActivity;
}
