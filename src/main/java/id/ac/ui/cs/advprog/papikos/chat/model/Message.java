package id.ac.ui.cs.advprog.papikos.chat.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "message_id")
    private UUID messageId;

    @Column(name = "room_id", nullable = false)
    private UUID roomId;

    @Column(name = "sender_user_id", nullable = false)
    private UUID senderUserId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "is_edited", nullable = false)
    @Builder.Default
    private boolean isEdited = false;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Message(UUID roomId, UUID senderUserId, String content) {
        validateInput(roomId, senderUserId, content);
        this.roomId = roomId;
        this.senderUserId = senderUserId;
        this.content = content;
        this.createdAt = LocalDateTime.now(); //TODO: integrasi dengan database
        this.updatedAt = LocalDateTime.now(); //TODO: integrasi dengan database
    }

    private void validateInput(UUID roomId, UUID senderUserId, String content) {
        if (roomId == null) {
            throw new IllegalArgumentException("Invalid room ID");
        }
        if (senderUserId == null) {
            throw new IllegalArgumentException("Invalid sender ID");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }
    }
}