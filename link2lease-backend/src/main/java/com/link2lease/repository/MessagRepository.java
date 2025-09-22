package com.link2lease.repository;

import com.link2lease.enums.MessageType;
import com.link2lease.model.Message;
import com.link2lease.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessagRepository extends JpaRepository<Message, Long> {

        // Find all messages for a user (sent or received)
        @Query("SELECT m FROM Message m " +
                "LEFT JOIN FETCH m.sender " +
                "LEFT JOIN FETCH m.recipient " +
                "LEFT JOIN FETCH m.property " +
                "LEFT JOIN FETCH m.lease " +
                "WHERE m.sender.id = :userId OR m.recipient.id = :userId ORDER BY m.sentAt DESC")
        List<Message> findAllUserMessages(@Param("userId") Long userId);

        // Find sent messages
        @Query("SELECT m FROM Message m " +
                "LEFT JOIN FETCH m.recipient " +
                "LEFT JOIN FETCH m.property " +
                "LEFT JOIN FETCH m.lease " +
                "WHERE m.sender.id = :senderId ORDER BY m.sentAt DESC")
        List<Message> findBySenderIdOrderBySentAtDesc(@Param("senderId") Long senderId);

        // Find received messages
        @Query("SELECT m FROM Message m " +
                "LEFT JOIN FETCH m.sender " +
                "LEFT JOIN FETCH m.property " +
                "LEFT JOIN FETCH m.lease " +
                "WHERE m.recipient.id = :recipientId ORDER BY m.sentAt DESC")
        List<Message> findByRecipientIdOrderBySentAtDesc(@Param("recipientId") Long recipientId);

        // Find unread messages for a recipient
        @Query("SELECT m FROM Message m " +
                "LEFT JOIN FETCH m.sender " +
                "LEFT JOIN FETCH m.property " +
                "LEFT JOIN FETCH m.lease " +
                "WHERE m.recipient.id = :recipientId AND m.isRead = false ORDER BY m.sentAt DESC")
        List<Message> findByRecipientIdAndIsReadFalseOrderBySentAtDesc(@Param("recipientId") Long recipientId);

        // Count unread messages for a recipient
        Long countByRecipientIdAndIsReadFalse(Long recipientId);

        // Find conversation between two users
        @Query("SELECT m FROM Message m " +
                "LEFT JOIN FETCH m.sender " +
                "LEFT JOIN FETCH m.recipient " +
                "LEFT JOIN FETCH m.property " +
                "LEFT JOIN FETCH m.lease " +
                "WHERE (m.sender.id = :user1Id AND m.recipient.id = :user2Id) OR " +
                "(m.sender.id = :user2Id AND m.recipient.id = :user1Id) " +
                "ORDER BY m.sentAt ASC")
        List<Message> findConversationBetweenUsers(@Param("user1Id") Long user1Id,
                                                   @Param("user2Id") Long user2Id);

        // Find all conversations for a user (grouped by other participant)
        @Query("SELECT DISTINCT " +
                "CASE WHEN m.sender.id = :userId THEN m.recipient ELSE m.sender END " +
                "FROM Message m WHERE m.sender.id = :userId OR m.recipient.id = :userId")
        List<User> findConversationPartners(@Param("userId") Long userId);

        // Find latest message in each conversation for a user
        @Query("SELECT m FROM Message m WHERE m.id IN (" +
                "SELECT MAX(m2.id) FROM Message m2 WHERE " +
                "(m2.sender.id = :userId OR m2.recipient.id = :userId) " +
                "GROUP BY CASE WHEN m2.sender.id = :userId THEN m2.recipient.id ELSE m2.sender.id END" +
                ") ORDER BY m.sentAt DESC")
        List<Message> findLatestMessagesInConversations(@Param("userId") Long userId);

        // Find messages about a specific property
        List<Message> findByPropertyIdOrderBySentAtDesc(Long propertyId);

        // Find messages about a specific lease
        List<Message> findByLeaseIdOrderBySentAtDesc(Long leaseId);

        // Find replies to a message
        List<Message> findByParentMessageIdOrderBySentAtAsc(Long parentMessageId);

        // Mark messages as read
        @Modifying
        @Query("UPDATE Message m SET m.isRead = true, m.readAt = :readAt WHERE m.id IN :messageIds AND m.recipient.id = :recipientId")
        void markMessagesAsRead(@Param("messageIds") List<Long> messageIds,
                                @Param("recipientId") Long recipientId,
                                @Param("readAt") LocalDateTime readAt);

        // Mark all messages from a user as read
        @Modifying
        @Query("UPDATE Message m SET m.isRead = true, m.readAt = :readAt WHERE m.sender.id = :senderId AND m.recipient.id = :recipientId AND m.isRead = false")
        void markAllMessagesFromUserAsRead(@Param("senderId") Long senderId,
                                           @Param("recipientId") Long recipientId,
                                           @Param("readAt") LocalDateTime readAt);

        // Find messages by type
        List<Message> findByMessageTypeAndRecipientIdOrderBySentAtDesc(MessageType messageType, Long recipientId);

        // Search messages by content
        @Query("SELECT m FROM Message m WHERE " +
                "(m.sender.id = :userId OR m.recipient.id = :userId) AND " +
                "(LOWER(m.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
                "LOWER(m.subject) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
                "ORDER BY m.sentAt DESC")
        List<Message> searchUserMessages(@Param("userId") Long userId,
                                         @Param("searchTerm") String searchTerm);

        // Count total messages sent by user
        Long countBySenderId(Long senderId);

        // Count total messages received by user
        Long countByRecipientId(Long recipientId);
}
