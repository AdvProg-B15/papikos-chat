package id.ac.ui.cs.advprog.papikos.chat.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

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
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "room_id")
    private UUID roomId;

    @Column(name = "user1_id", nullable = false)
    private UUID user1Id;

    @Column(name = "user2_id", nullable = false)
    private UUID user2Id;

    @Column(name = "last_message_at")
    private LocalDateTime lastMessageAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ChatRoom(UUID user1Id, UUID user2Id) {
        UUID[] users = validateAndSwapUsers(user1Id, user2Id);
        this.user1Id = users[0];
        this.user2Id = users[1];
        this.lastMessageAt = null;
    }

    private UUID[] validateAndSwapUsers(UUID inputUser1, UUID inputUser2) {
        if (inputUser1 == null || inputUser2 == null) {
            throw new IllegalArgumentException("User IDs cannot be null");
        }
        if (inputUser1.equals(inputUser2)) {
            throw new IllegalArgumentException("User IDs must be different");
        }

        if (inputUser1.compareTo(inputUser2) > 0) {
            return new UUID[]{inputUser2, inputUser1};
        }
        return new UUID[]{inputUser1, inputUser2};
    }
}