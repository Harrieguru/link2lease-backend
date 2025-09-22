package com.link2lease.service;

import com.link2lease.dto.MessageDto;
import com.link2lease.repository.LeaseRepository;
import com.link2lease.repository.MessagRepository;
import com.link2lease.dto.ConversationDto;
import com.link2lease.repository.PropertyRepository;
import com.link2lease.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.link2lease.dto.MessageCreateDto;
import com.link2lease.dto.MessageStatsDto;
import com.link2lease.model.Message;
import com.link2lease.model.User;
import com.link2lease.model.Property;
import com.link2lease.model.Lease;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MessageService {
    private final MessagRepository messageRepository;
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final LeaseRepository leaseRepository;

//    public MessageService(MessagRepository messageRepository,
//                          UserRepository userRepository,
//                          PropertyRepository propertyRepository,
//                          LeaseRepository leaseRepository){
//        this.leaseRepository = leaseRepository;
//        this.userRepository = userRepository;
//        this.propertyRepository = propertyRepository;
//        this.messageRepository = messageRepository;
//    }

    /**
     * Send a new message
     */
    public MessageDto sendMessage(Long senderId, MessageCreateDto messageCreateDto) {
        log.info("Sending message from user {} to user {}", senderId, messageCreateDto.getRecipientId());

        // Validate sender
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found with id: " + senderId));

        // Validate recipient
        User recipient = userRepository.findById(messageCreateDto.getRecipientId())
                .orElseThrow(() -> new RuntimeException("Recipient not found with id: " + messageCreateDto.getRecipientId()));

        // Validate sender is not sending to themselves
        if (senderId.equals(messageCreateDto.getRecipientId())) {
            throw new IllegalArgumentException("Cannot send message to yourself");
        }

        Message message = new Message();
        message.setSender(sender);
        message.setRecipient(recipient);
        message.setContent(messageCreateDto.getContent());
        message.setSubject(messageCreateDto.getSubject());
        message.setMessageType(messageCreateDto.getMessageType());

        // Set optional references
        if (messageCreateDto.getPropertyId() != null) {
            Property property = propertyRepository.findById(messageCreateDto.getPropertyId())
                    .orElseThrow(() -> new RuntimeException("Property not found with id: " + messageCreateDto.getPropertyId()));
            message.setProperty(property);
        }

        if (messageCreateDto.getLeaseId() != null) {
            Lease lease = leaseRepository.findById(messageCreateDto.getLeaseId())
                    .orElseThrow(() -> new RuntimeException("Lease not found with id: " + messageCreateDto.getLeaseId()));
            message.setLease(lease);
        }

        if (messageCreateDto.getParentMessageId() != null) {
            Message parentMessage = messageRepository.findById(messageCreateDto.getParentMessageId())
                    .orElseThrow(() -> new RuntimeException("Parent message not found with id: " + messageCreateDto.getParentMessageId()));
            // Validate that user has access to the parent message
            if (!parentMessage.getSender().getId().equals(senderId) &&
                    !parentMessage.getRecipient().getId().equals(senderId)) {
                throw new IllegalArgumentException("Cannot reply to a message you don't have access to");
            }
            message.setParentMessage(parentMessage);
        }

        Message savedMessage = messageRepository.save(message);
        log.info("Message sent successfully with id: {}", savedMessage.getId());

        return new MessageDto(savedMessage);
    }

    /**
     * Get received messages for a user
     */
    @Transactional
    public List<MessageDto> getReceivedMessages(Long userId) {
        log.info("Fetching received messages for user: {}", userId);

        validateUser(userId);

        return messageRepository.findByRecipientIdOrderBySentAtDesc(userId)
                .stream()
                .map(MessageDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Get sent messages for a user
     */
    @Transactional
    public List<MessageDto> getSentMessages(Long userId) {
        log.info("Fetching sent messages for user: {}", userId);

        validateUser(userId);

        return messageRepository.findBySenderIdOrderBySentAtDesc(userId)
                .stream()
                .map(MessageDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Get all messages for a user (sent and received)
     */
    @Transactional
    public List<MessageDto> getAllUserMessages(Long userId) {
        log.info("Fetching all messages for user: {}", userId);

        validateUser(userId);

        return messageRepository.findAllUserMessages(userId)
                .stream()
                .map(MessageDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Get unread messages for a user
     */
    @Transactional
    public List<MessageDto> getUnreadMessages(Long userId) {
        log.info("Fetching unread messages for user: {}", userId);

        validateUser(userId);

        return messageRepository.findByRecipientIdAndIsReadFalseOrderBySentAtDesc(userId)
                .stream()
                .map(MessageDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Get conversation between two users
     */
    @Transactional
    public List<MessageDto> getConversation(Long user1Id, Long user2Id) {
        log.info("Fetching conversation between users {} and {}", user1Id, user2Id);

        validateUser(user1Id);
        validateUser(user2Id);

        return messageRepository.findConversationBetweenUsers(user1Id, user2Id)
                .stream()
                .map(MessageDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Get all conversations for a user
     */
    @Transactional
    public List<ConversationDto> getUserConversations(Long userId) {
        log.info("Fetching conversations for user: {}", userId);

        validateUser(userId);

        // Get latest messages in each conversation
        List<Message> latestMessages = messageRepository.findLatestMessagesInConversations(userId);

        return latestMessages.stream().map(message -> {
            // Determine the other user in the conversation
            User otherUser = message.getSender().getId().equals(userId) ?
                    message.getRecipient() : message.getSender();

            // Count unread messages in this conversation
            Long unreadCount = messageRepository.findConversationBetweenUsers(userId, otherUser.getId())
                    .stream()
                    .filter(m -> m.getRecipient().getId().equals(userId) && !m.getIsRead())
                    .count();

            return new ConversationDto(
                    otherUser.getId(),
                    otherUser.getFullName(),
                    otherUser.getEmail(),
                    new MessageDto(message),
                    unreadCount,
                    message.getSentAt()
            );
        }).collect(Collectors.toList());
    }

    /**
     * Mark messages as read
     */
    public void markMessagesAsRead(Long userId, List<Long> messageIds) {
        log.info("Marking messages as read for user: {}", userId);

        validateUser(userId);
        messageRepository.markMessagesAsRead(messageIds, userId, LocalDateTime.now());
    }

    /**
     * Mark all messages from a specific sender as read
     */
    public void markAllMessagesFromUserAsRead(Long recipientId, Long senderId) {
        log.info("Marking all messages from user {} as read for user {}", senderId, recipientId);

        validateUser(recipientId);
        validateUser(senderId);
        messageRepository.markAllMessagesFromUserAsRead(senderId, recipientId, LocalDateTime.now());
    }

    /**
     * Get message by ID (with access control)
     */
    @Transactional
    public MessageDto getMessageById(Long messageId, Long userId) {
        log.info("Fetching message {} for user {}", messageId, userId);

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found with id: " + messageId));

        // Check if user is authorized to view this message
        if (!message.getSender().getId().equals(userId) && !message.getRecipient().getId().equals(userId)) {
            throw new IllegalAccessError("Not authorized to view this message");
        }

        // Mark as read if user is the recipient and message is unread
        if (message.getRecipient().getId().equals(userId) && !message.getIsRead()) {
            message.markAsRead();
            messageRepository.save(message);
        }

        return new MessageDto(message);
    }

    /**
     * Search messages for a user
     */
    @Transactional
    public List<MessageDto> searchMessages(Long userId, String searchTerm) {
        log.info("Searching messages for user {} with term: {}", userId, searchTerm);

        validateUser(userId);

        return messageRepository.searchUserMessages(userId, searchTerm)
                .stream()
                .map(MessageDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Get message statistics for a user
     */
    @Transactional
    public MessageStatsDto getMessageStats(Long userId) { // Fixed: Return MessageStatsDto instead of MessageCreateDto
        log.info("Fetching message statistics for user: {}", userId);

        validateUser(userId);

        Long totalSent = messageRepository.countBySenderId(userId);
        Long totalReceived = messageRepository.countByRecipientId(userId);
        Long unreadCount = messageRepository.countByRecipientIdAndIsReadFalse(userId);
        Long totalMessages = totalSent + totalReceived;

        return new MessageStatsDto(totalMessages, unreadCount, totalSent, totalReceived); // Fixed: Use correct DTO
    }

    /**
     * Delete message (only sender can delete)
     */
    public void deleteMessage(Long messageId, Long userId) {
        log.info("Deleting message {} by user {}", messageId, userId);

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found with id: " + messageId)); // Fixed: Use RuntimeException

        // Only sender can delete their sent messages
        if (!message.getSender().getId().equals(userId)) {
            throw new IllegalAccessError("Only the sender can delete this message");
        }

        messageRepository.delete(message);
        log.info("Message {} deleted successfully", messageId);
    }

    /**
     * Get messages about a specific property
     */
    @Transactional
    public List<MessageDto> getPropertyMessages(Long propertyId) {
        log.info("Fetching messages for property: {}", propertyId);

        // Validate property exists
        propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found with id: " + propertyId)); // Fixed: Use RuntimeException

        return messageRepository.findByPropertyIdOrderBySentAtDesc(propertyId)
                .stream()
                .map(MessageDto::new)
                .collect(Collectors.toList());
    }

    private void validateUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with id: " + userId); // Fixed: Use RuntimeException
        }
    }

}
