package com.link2lease.controller;

import com.link2lease.dto.ConversationDto;
import com.link2lease.dto.MessageCreateDto;
import com.link2lease.dto.MessageDto;
import com.link2lease.dto.MessageStatsDto;
import com.link2lease.service.MessageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final MessageService messageService;

    /**
     * Send a new message
     */
    @PostMapping("/{senderId}")
    public ResponseEntity<MessageDto> sendMessage(
            @PathVariable Long senderId,
            @Valid @RequestBody MessageCreateDto messageCreateDto) {

        log.info("User {} sending message", senderId);
        MessageDto sentMessage = messageService.sendMessage(senderId, messageCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(sentMessage);
    }

    /**
     * Get received messages
     */
    @GetMapping("/{userId}/received")
    public ResponseEntity<List<MessageDto>> getReceivedMessages(@PathVariable Long userId) {
        List<MessageDto> messages = messageService.getReceivedMessages(userId);
        return ResponseEntity.ok(messages);
    }

    /**
     * Get sent messages
     */
    @GetMapping("/{userId}/sent")
    public ResponseEntity<List<MessageDto>> getSentMessages(@PathVariable Long userId) {
        List<MessageDto> messages = messageService.getSentMessages(userId);
        return ResponseEntity.ok(messages);
    }

    /**
     * Get all messages (sent and received)
     */
    @GetMapping("/{userId}/all")
    public ResponseEntity<List<MessageDto>> getAllMessages(@PathVariable Long userId) {
        List<MessageDto> messages = messageService.getAllUserMessages(userId);
        return ResponseEntity.ok(messages);
    }

    /**
     * Get unread messages
     */
    @GetMapping("/{userId}/unread")
    public ResponseEntity<List<MessageDto>> getUnreadMessages(@PathVariable Long userId) {
        List<MessageDto> messages = messageService.getUnreadMessages(userId);
        return ResponseEntity.ok(messages);
    }

    /**
     * Get all conversations
     */
    @GetMapping("/{userId}/conversations")
    public ResponseEntity<List<ConversationDto>> getConversations(@PathVariable Long userId) {
        List<ConversationDto> conversations = messageService.getUserConversations(userId);
        return ResponseEntity.ok(conversations);
    }

    /**
     * Get conversation with specific user
     */
    @GetMapping("/{userId}/conversation/{otherUserId}")
    public ResponseEntity<List<MessageDto>> getConversation(
            @PathVariable Long userId,
            @PathVariable Long otherUserId) {

        List<MessageDto> messages = messageService.getConversation(userId, otherUserId);
        return ResponseEntity.ok(messages);
    }

    /**
     * Get specific message by ID
     */
    @GetMapping("/{userId}/message/{messageId}")
    public ResponseEntity<MessageDto> getMessageById(
            @PathVariable Long userId,
            @PathVariable Long messageId) {

        MessageDto message = messageService.getMessageById(messageId, userId);
        return ResponseEntity.ok(message);
    }

    /**
     * Search messages
     */
    @GetMapping("/{userId}/search")
    public ResponseEntity<List<MessageDto>> searchMessages(
            @PathVariable Long userId,
            @RequestParam String searchTerm) {

        List<MessageDto> messages = messageService.searchMessages(userId, searchTerm);
        return ResponseEntity.ok(messages);
    }

    /**
     * Get message statistics
     */
    @GetMapping("/{userId}/stats")
    public ResponseEntity<MessageStatsDto> getMessageStats(@PathVariable Long userId) {
        MessageStatsDto stats = messageService.getMessageStats(userId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Mark messages as read
     */
    @PutMapping("/{userId}/mark-read")
    public ResponseEntity<Void> markMessagesAsRead(
            @PathVariable Long userId,
            @RequestBody List<Long> messageIds) {

        messageService.markMessagesAsRead(userId, messageIds);
        return ResponseEntity.ok().build();
    }

    /**
     * Mark all messages from a user as read
     */
    @PutMapping("/{userId}/mark-all-read/{senderId}")
    public ResponseEntity<Void> markAllMessagesFromUserAsRead(
            @PathVariable Long userId,
            @PathVariable Long senderId) {

        messageService.markAllMessagesFromUserAsRead(userId, senderId);
        return ResponseEntity.ok().build();
    }

    /**
     * Delete a message
     */
    @DeleteMapping("/{userId}/delete/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable Long userId,
            @PathVariable Long messageId) {

        messageService.deleteMessage(messageId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Get messages about a specific property
     */
    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<MessageDto>> getPropertyMessages(@PathVariable Long propertyId) {
        List<MessageDto> messages = messageService.getPropertyMessages(propertyId);
        return ResponseEntity.ok(messages);
    }

    /**
     * Exception handler for this controller
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        log.error("Error in MessageController: ", e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("Invalid argument in MessageController: ", e);
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(IllegalAccessError.class)
    public ResponseEntity<String> handleIllegalAccessError(IllegalAccessError e) {
        log.error("Access denied in MessageController: ", e);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
    }
}
