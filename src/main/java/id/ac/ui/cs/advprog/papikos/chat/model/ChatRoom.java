package id.ac.ui.cs.advprog.papikos.chat.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "chat_rooms",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_user_pair",
                columnNames = {"user1_id", "user2_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long roomId;

    @Column(name = "user1_id", nullable = false)
    private Long user1Id;

    @Column(name = "user2_id", nullable = false)
    private Long user2Id;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ChatRoom(Long user1Id, Long user2Id) {
        validateInput(user1Id, user2Id);
        autoSwapUsers(user1Id, user2Id);
        this.lastMessageAt = null; //TODO: hubungkan dengan timestamp last message
        this.createdAt = LocalDateTime.now(); //TODO: integrasi dengan database
        this.updatedAt = LocalDateTime.now(); //TODO: integrasi dengan database
    }

    private void validateUserOrder() {
        validateInput(user1Id, user2Id);
        autoSwapUsers(user1Id, user2Id);
    }

    private void validateInput(Long user1Id, Long user2Id) {
        if (user1Id == null || user2Id == null) {
            throw new IllegalArgumentException("User IDs cannot be null");
        }
        if (user1Id < 0 || user2Id < 0) {
            throw new IllegalArgumentException("User IDs cannot be negative");
        }
        if (user1Id.equals(user2Id)) {
            throw new IllegalArgumentException("User IDs must be different");
        }
    }

    private void autoSwapUsers(Long inputUser1, Long inputUser2) {
        if (inputUser1 > inputUser2) {
            this.user1Id = inputUser2;
            this.user2Id = inputUser1;
        } else {
            this.user1Id = inputUser1;
            this.user2Id = inputUser2;
        }
    }
}