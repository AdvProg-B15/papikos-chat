package id.ac.ui.cs.advprog.papikos.chat.dto;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomRequest {
    private UUID senderId;
    private UUID recipientId;
}