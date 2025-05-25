package id.ac.ui.cs.advprog.papikos.chat.service;

import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRoomService {
    ChatRoom createChatRoom(UUID user1Id, UUID user2Id);

    ChatRoom getChatRoomById(UUID roomId);

    Optional<ChatRoom> findChatRoomByUserPair(UUID user1Id, UUID user2Id);

    List<ChatRoom> getAllChatRoomsForUser(UUID userId);

    ChatRoom updateLastMessageAt(UUID roomId, LocalDateTime timestamp);
}