package id.ac.ui.cs.advprog.papikos.chat.service;

import id.ac.ui.cs.advprog.papikos.chat.model.ChatRoom;
import id.ac.ui.cs.advprog.papikos.chat.repository.ChatRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomsRepository;

    @Autowired
    public ChatRoomService(ChatRoomRepository chatRoomsRepository) {
        this.chatRoomsRepository = chatRoomsRepository;
    }

    @Transactional
    public ChatRoom createChatRoom(UUID user1Id, UUID user2Id) {
        validateUserIds(user1Id, user2Id);
        UUID[] orderedUserIds = orderUserIds(user1Id, user2Id);

        return chatRoomsRepository.findByUser1IdAndUser2Id(orderedUserIds[0], orderedUserIds[1])
                .orElseGet(() -> {
                    ChatRoom newRoom = new ChatRoom(orderedUserIds[0], orderedUserIds[1]);
                    return chatRoomsRepository.save(newRoom);
                });
    }

    @Transactional(readOnly = true)
    public ChatRoom getChatRoomById(UUID roomId) {
        return chatRoomsRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Chat room not found"));
    }

    @Transactional(readOnly = true)
    public Optional<ChatRoom> findChatRoomByUserPair(UUID user1Id, UUID user2Id) {
        validateUserIds(user1Id, user2Id);
        UUID[] orderedUserIds = orderUserIds(user1Id, user2Id);
        return chatRoomsRepository.findByUser1IdAndUser2Id(orderedUserIds[0], orderedUserIds[1]);
    }

    @Transactional(readOnly = true)
    public List<ChatRoom> getAllChatRoomsForUser(UUID userId) {
        validateUserId(userId);
        return chatRoomsRepository.findByUser1IdOrUser2Id(userId, userId);
    }

    @Transactional
    public ChatRoom updateLastMessageAt(UUID roomId, LocalDateTime timestamp) {
        ChatRoom room = getChatRoomById(roomId);
        room.setLastMessageAt(timestamp);
        return chatRoomsRepository.save(room);
    }

    private void validateUserIds(UUID user1Id, UUID user2Id) {
        if (user1Id == null || user2Id == null) {
            throw new IllegalArgumentException("User IDs cannot be null");
        }
        if (user1Id.equals(user2Id)) {
            throw new IllegalArgumentException("User IDs must be different");
        }
        validateUserId(user1Id);
        validateUserId(user2Id);
    }

    private void validateUserId(UUID userId) {
        if (userId == null) {
            throw new IllegalArgumentException("Invalid User ID");
        }
    }

    private UUID[] orderUserIds(UUID id1, UUID id2) {
        return id1.compareTo(id2) < 0 ? new UUID[]{id1, id2} : new UUID[]{id2, id1};
    }
}