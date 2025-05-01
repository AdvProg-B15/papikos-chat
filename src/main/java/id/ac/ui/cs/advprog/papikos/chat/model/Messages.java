package id.ac.ui.cs.advprog.papikos.chat.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
public class Messages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long messageId;

    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "sender_user_id")
    private Long senderUserId;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_edited")
    private boolean isEdited;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Messages() {
    }

    public Messages(Long roomId, Long senderUserId, String content) {
        this.roomId = roomId;
        this.senderUserId = senderUserId;
        this.content = content;
    }
}