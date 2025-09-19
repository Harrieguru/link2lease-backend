package com.link2lease.dto;

import com.link2lease.enums.MessageType;
import com.link2lease.model.Message;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageCreateDto {
    @NotNull(message = "Recipient ID is required")
    private Long recipientId;

    private Long propertyId;
    private Long leaseId;

    @NotBlank(message = "Message Content is required")
    @Size(max = 1000, message = "Message content cannot exceed 1000 characters")
    private String content;

    @Size(max = 200, message = "Subject cannot exceed 200 characters")
    private String subject;

    private MessageType messageType = MessageType.GENERAL;
    private Long parentMessageId;
}
