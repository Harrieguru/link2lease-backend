package com.link2lease.dto;

import com.link2lease.enums.MessageType;
import com.link2lease.model.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    // Basic message fields
    private Long id;
    private Long senderId;
    private String senderName;
    private String senderEmail;
    private Long recipientId;
    private String recipientName;
    private String recipientEmail;
    private Long propertyId;
    private String propertyTitle;
    private Long leaseId;
    private String content;
    private String subject;
    private MessageType messageType;
    private Boolean isRead;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
    private Long parentMessageId;

    public MessageDto(Message message){
        this.id = message.getId();
        this.senderId = message.getSender().getId();
        this.senderName = message.getSender().getFullName();
        this.senderEmail = message.getSender().getEmail();
        this.recipientId = message.getRecipient().getId();
        this.recipientName = message.getRecipient().getFullName();
        this.recipientEmail = message.getRecipient().getEmail();
        this.propertyId = message.getProperty() != null ? message.getProperty().getId() : null;
        this.propertyTitle = message.getProperty() != null ? message.getProperty().getTitle() : null;
        this.leaseId = message.getLease() != null ? message.getLease().getId() : null;
        this.content = message.getContent();
        this.subject = message.getSubject();
        this.messageType = message.getMessageType();
        this.isRead = message.getIsRead();
        this.sentAt = message.getSentAt();
        this.readAt = message.getReadAt();
        this.parentMessageId = message.getParentMessage() != null ? message.getParentMessage().getId() : null;
    }
}
