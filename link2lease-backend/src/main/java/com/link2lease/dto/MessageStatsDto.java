package com.link2lease.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageStatsDto {
    private Long totalMessages;
    private Long unreadMessages;
    private Long sentMessages;
    private Long receivedMessages;
}
