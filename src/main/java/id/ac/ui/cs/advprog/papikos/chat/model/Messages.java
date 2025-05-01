package id.ac.ui.cs.advprog.papikos.chat.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Messages {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;

    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @Column(name = "sender_user_id", nullable = false)
    private Long senderUserId;

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

    public Messages(Long roomId, Long senderUserId, String content) {
        validateInput(roomId, senderUserId, content);
        this.roomId = roomId;
        this.senderUserId = senderUserId;
        this.content = content;
        this.createdAt = LocalDateTime.now(); //TODO: integrasi dengan database
        this.updatedAt = LocalDateTime.now(); //TODO: integrasi dengan database
    }

    private void validateInput(Long roomId, Long senderUserId, String content) {
        if (roomId == null || roomId <= 0) {
            throw new IllegalArgumentException("Invalid room ID");
        }
        if (senderUserId == null || senderUserId <= 0) {
            throw new IllegalArgumentException("Invalid sender ID");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }
    }
}