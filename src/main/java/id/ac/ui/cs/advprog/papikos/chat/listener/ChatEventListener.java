package id.ac.ui.cs.advprog.papikos.chat.listener;

import id.ac.ui.cs.advprog.papikos.chat.config.RabbitMQConfig;
import id.ac.ui.cs.advprog.papikos.chat.dto.RentalEvent;
import id.ac.ui.cs.advprog.papikos.chat.service.ChatRoomService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ChatEventListener {

    private final ChatRoomService chatRoomService;

    @Autowired
    public ChatEventListener(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    @RabbitListener(queues = RabbitMQConfig.CHAT_QUEUE_NAME)
    public void handleRentalCreatedEvent(RentalEvent event) {
        String userIdStr     = event.getUserId();
        String kosOwnerIdStr = event.getKosOwnerId();
        System.out.println("ChatService: userId=" + userIdStr + ", kosOwnerId=" + kosOwnerIdStr);

        try {
            UUID userId = UUID.fromString(userIdStr);
            UUID kosOwnerId = UUID.fromString(kosOwnerIdStr);
            chatRoomService.createChatRoom(userId, kosOwnerId);
            System.out.println("Chat room berhasil dibuat untuk userId " + userId + " dan kosOwnerId " + kosOwnerId);
        } catch (IllegalArgumentException ex) {
            System.err.println("Terjadi kesalahan pada format UUID: " + ex.getMessage());
        }
    }
}
